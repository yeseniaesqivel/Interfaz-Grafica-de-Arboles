package gui;

import huffman.HuffmanNode;
import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {
    private final HuffmanNode root;

    public TreePanel(HuffmanNode root) {
        this.root = root;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            drawNode(g, root, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void drawNode(Graphics g, HuffmanNode node, int x, int y, int offset) {
        g.setColor(Color.BLACK);
        g.drawOval(x - 15, y - 15, 30, 30);
        String label = node.data == '\0' ? "*" : String.valueOf(node.data);
        g.drawString(label + "(" + node.freq + ")", x - 15, y - 20);

        if (node.left != null) {
            g.drawLine(x, y + 15, x - offset, y + 80 - 15);
            drawNode(g, node.left, x - offset, y + 80, offset / 2);
        }
        if (node.right != null) {
            g.drawLine(x, y + 15, x + offset, y + 80 - 15);
            drawNode(g, node.right, x + offset, y + 80, offset / 2);
        }
    }
}
