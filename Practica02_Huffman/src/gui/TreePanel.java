package gui;

import huffman.HuffmanNode;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * TreePanel para dibujar el árbol de Huffman con:
 * - Scroll (via preferredSize, el JScrollPane lo maneja la GUI)
 * - Límites anti-desborde (maxNodes / maxDepth) + overlay de aviso
 * - Layout por in-order para columnas X (evita montajes)
 * - (Opcional) Zoom con Ctrl + rueda
 *
 * Adaptado a tu HuffmanNode:
 *   - campos públicos: data (char), freq (int), left, right
 *   - nodo interno: data == '\0'  (etiqueta "*")
 */
public class TreePanel extends JComponent {

    // --- Datos del árbol ---
    private HuffmanNode root;

    // --- Apariencia / layout ---
    private int nodeDiameter = 28;   // tamaño del nodo (círculo)
    private int hGapBase = 32;       // separación horizontal base (entre columnas)
    private int vGap = 80;           // separación vertical (entre niveles)
    private final int margin = 32;   // margen del lienzo
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    // --- Límites anti-desborde ---
    private int hardCapNodes = 600;  // máximo de nodos a renderizar
    private int hardCapDepth = 14;   // máxima profundidad (niveles)
    private boolean truncated = false; // si la vista fue recortada por límites

    // --- Zoom (opcional) ---
    private double zoom = 1.0;       // Ctrl + rueda

    // --- Layout interno ---
    private final Map<HuffmanNode, Point> positions = new HashMap<>();
    private int maxDepthComputed = 0;
    private int colsComputed = 1;

    public TreePanel(HuffmanNode root) {
        this.root = root;
        setOpaque(true);
        setBackground(Color.WHITE);
        enableZoomControls();
        recomputeLayout();
    }

    /** Permite actualizar raíz y recalcular. */
    public void setRoot(HuffmanNode root) {
        this.root = root;
        recomputeLayout();
        revalidate();
        repaint();
    }

    /** Configura límites (máx. nodos/profundidad). */
    public void setHardCaps(int maxNodes, int maxDepth) {
        this.hardCapNodes = Math.max(1, maxNodes);
        this.hardCapDepth = Math.max(1, maxDepth);
        recomputeLayout();
        revalidate();
        repaint();
    }

    /** Ajusta tamaños/gaps. */
    public void setGaps(int nodeDiameter, int hGapBase, int vGap) {
        this.nodeDiameter = nodeDiameter;
        this.hGapBase = hGapBase;
        this.vGap = vGap;
        recomputeLayout();
        revalidate();
        repaint();
    }

    private void enableZoomControls() {
        addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                double delta = (e.getWheelRotation() < 0) ? 0.1 : -0.1;
                zoom = Math.max(0.3, Math.min(2.5, zoom + delta));
                revalidate();
                repaint();
            }
        });
    }

    // ================= CÁLCULO DE LAYOUT =================

    private static class Slot {
        HuffmanNode node;
        int depth;
    }

    private void recomputeLayout() {
        positions.clear();
        truncated = false;
        maxDepthComputed = 0;
        colsComputed = 1;

        if (root == null) {
            setPreferredSize(new Dimension(400, 200));
            return;
        }

        // 1) BFS para medir profundidad y cortar por límites
        int visited = 0;
        Deque<Slot> q = new ArrayDeque<>();
        Slot s0 = new Slot(); s0.node = root; s0.depth = 0; q.add(s0);

        while (!q.isEmpty()) {
            Slot s = q.poll();
            if (s.node == null) continue;
            visited++;
            maxDepthComputed = Math.max(maxDepthComputed, s.depth);
            if (visited > hardCapNodes) { truncated = true; break; }

            if (s.depth < hardCapDepth) {
                HuffmanNode L = s.node.left, R = s.node.right;
                if (L != null) { Slot sl = new Slot(); sl.node = L; sl.depth = s.depth + 1; q.add(sl); }
                if (R != null) { Slot sr = new Slot(); sr.node = R; sr.depth = s.depth + 1; q.add(sr); }
            } else {
                truncated = true;
            }
        }

        // 2) In-order limitado para columnas X
        List<HuffmanNode> inorder = new ArrayList<>();
        inOrderLimited(root, 0, inorder);

        colsComputed = Math.max(1, inorder.size());
        Map<HuffmanNode, Integer> idx = new HashMap<>();
        for (int i = 0; i < inorder.size(); i++) idx.put(inorder.get(i), i);

        // 3) Asignar posiciones
        assignPositions(root, 0, idx);

        // 4) preferredSize para que JScrollPane pueda scrollear
        int xStep = nodeDiameter + hGapBase;
        int width  = colsComputed * xStep + margin * 2;
        int height = (maxDepthComputed + 1) * vGap + margin * 2;

        Dimension base = new Dimension(Math.max(width, 300), Math.max(height, 200));
        Dimension scaled = new Dimension((int)(base.width * zoom), (int)(base.height * zoom));
        setPreferredSize(scaled);
    }

    private void inOrderLimited(HuffmanNode n, int depth, List<HuffmanNode> out) {
        if (n == null) return;
        if (depth > hardCapDepth) { truncated = true; return; }
        inOrderLimited(n.left, depth + 1, out);
        if (out.size() < hardCapNodes) out.add(n); else truncated = true;
        inOrderLimited(n.right, depth + 1, out);
    }

    private void assignPositions(HuffmanNode n, int depth, Map<HuffmanNode, Integer> idx) {
        if (n == null || depth > hardCapDepth) return;
        Integer col = idx.get(n);
        if (col != null) {
            int x = margin + col * (nodeDiameter + hGapBase) + nodeDiameter / 2;
            int y = margin + depth * vGap + nodeDiameter / 2;
            positions.put(n, new Point(x, y));
        }
        assignPositions(n.left,  depth + 1, idx);
        assignPositions(n.right, depth + 1, idx);
    }

    // ================= DIBUJO =================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.scale(zoom, zoom);

        // fondo
        g2.setColor(getBackground());
        g2.fillRect(0, 0, (int)(getWidth()/zoom), (int)(getHeight()/zoom));

        // aristas
        g2.setColor(Color.DARK_GRAY);
        drawEdges(g2, root);

        // nodos
        g2.setFont(font);
        drawNodes(g2, root);

        // overlay si fue truncado
        if (truncated) {
            paintOverlay(g2, String.format("Vista limitada (%,d nodos máx., profundidad ≤ %d)", hardCapNodes, hardCapDepth));
        }
        g2.dispose();
    }

    private void drawEdges(Graphics2D g2, HuffmanNode n) {
        if (n == null) return;
        Point p = positions.get(n);
        if (p != null) {
            if (n.left != null && positions.containsKey(n.left)) {
                Point c = positions.get(n.left);
                g2.drawLine(p.x, p.y, c.x, c.y);
            }
            if (n.right != null && positions.containsKey(n.right)) {
                Point c = positions.get(n.right);
                g2.drawLine(p.x, p.y, c.x, c.y);
            }
        }
        drawEdges(g2, n.left);
        drawEdges(g2, n.right);
    }

    private void drawNodes(Graphics2D g2, HuffmanNode n) {
        if (n == null) return;
        Point p = positions.get(n);
        if (p != null) {
            int r = nodeDiameter / 2;
            int x = p.x - r, y = p.y - r;

            // nodo
            g2.setColor(new Color(240, 248, 255));
            g2.fillOval(x, y, nodeDiameter, nodeDiameter);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, nodeDiameter, nodeDiameter);

            // etiqueta centrada (hoja: char:freq, interno: freq con *)
            String text = (n.data == '\0')
                    ? ("*(" + n.freq + ")")
                    : (n.data + ":" + n.freq);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();
            g2.drawString(text, p.x - tw / 2, p.y + th / 2 - 2);
        }
        drawNodes(g2, n.left);
        drawNodes(g2, n.right);
    }

    private void paintOverlay(Graphics2D g2, String msg) {
        Font f = g2.getFont().deriveFont(Font.BOLD, 12f);
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(msg) + 20;
        int h = fm.getHeight() + 10;
        int x = margin, y = margin / 2;
        g2.setColor(new Color(255, 255, 200, 200));
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(x, y, w, h, 12, 12);
        g2.drawString(msg, x + 10, y + h - 10);
    }
}
