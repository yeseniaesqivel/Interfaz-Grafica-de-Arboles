package gui;

import huffman.HuffmanTree;
import huffman.HuffmanNode;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HuffmanGUI extends JFrame {

    private final JTextField inputField = new JTextField(20);
    private final JButton btnConsola = new JButton("Entrada por Consola");
    private final JButton btnArchivo = new JButton("Desde Archivo");
    private final JPanel mainPanel = new JPanel(); // se queda, pero ya no dibujamos aquí el árbol

    public HuffmanGUI() {
        super("Visualización Árbol de Huffman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Texto o palabra:"));
        topPanel.add(inputField);
        topPanel.add(btnConsola);
        topPanel.add(btnArchivo);
        add(topPanel, BorderLayout.NORTH);

        // Dejo un panel central “vacío” (no usamos para el árbol, solo para mantener layout)
        add(mainPanel, BorderLayout.CENTER);

        btnConsola.addActionListener(e -> generarDesdeConsola());
        btnArchivo.addActionListener(e -> generarDesdeArchivo());
    }

    // === Botón: Entrada por Consola ============================================================
    private void generarDesdeConsola() {
        String text = inputField.getText();
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un texto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        text = text.trim();

        // Aviso de texto largo (aplicas límites en la vista)
        if (text.length() > 2000) {
            JOptionPane.showMessageDialog(this,
                    "Texto largo detectado. Se aplicarán límites de visualización.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }

        mostrarArbol(text);
    }

    // === Botón: Desde Archivo ==================================================================
    private void generarDesdeArchivo() {
        try {
            // Si quieres usar /data/compressed.txt cambia a: Path.of("data", "compressed.txt")
            String text = Files.readString(Path.of("compressed.txt"));
            text = text.replaceAll("[\\r\\n\\uFEFF]", "").trim(); // limpia saltos y BOM

            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El archivo está vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (text.length() > 2000) {
                JOptionPane.showMessageDialog(this,
                        "Archivo con texto largo. Se aplicarán límites de visualización.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

            mostrarArbol(text);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer el archivo compressed.txt.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === Construcción del árbol y apertura de VENTANA NUEVA ====================================
    private void mostrarArbol(String text) {
        // Construye árbol de Huffman con la lógica ya implementada (Integrante 1)
        Map<Character, Integer> freqMap = HuffmanTree.buildFrequencyMap(text);
        HuffmanTree tree = new HuffmanTree(freqMap);
        HuffmanNode root = tree.getRoot();

        // Abre cada resultado en su propia ventana con scroll
        showTreeInNewWindow(root, "Árbol de Huffman");
    }

    /**
     * Abre una nueva ventana con el TreePanel envuelto en JScrollPane.
     * Aplica límites anti-desborde y gaps (si tu TreePanel tiene esos setters).
     */
    private void showTreeInNewWindow(HuffmanNode root, String title) {
        JFrame f = new JFrame(title == null ? "Árbol de Huffman" : title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TreePanel panel = new TreePanel(root);

        // Si ya implementaste estos métodos en TreePanel, déjalos activos:
        try {
            panel.setHardCaps(600, 14); // máx. nodos y profundidad
            panel.setGaps(28, 32, 80);  // diámetro nodo, gap horizontal base, gap vertical
        } catch (NoSuchMethodError | Exception ignore) {
            // Si aún no existen, no falla la ejecución; puedes implementarlos luego.
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);

        f.setContentPane(sp);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
