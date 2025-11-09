package gui;

import huffman.HuffmanTree;
import huffman.HuffmanNode;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HuffmanGUI extends JFrame {

    private final JTextField inputField = new JTextField(20);
    private final JButton btnConsola = new JButton("Entrada por Consola");
    private final JButton btnArchivo = new JButton("Desde Archivo");
    private final JPanel mainPanel = new JPanel();

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
        add(mainPanel, BorderLayout.CENTER);

        btnConsola.addActionListener(e -> generarDesdeConsola());
        btnArchivo.addActionListener(e -> generarDesdeArchivo());
    }

    private void generarDesdeConsola() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un texto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarArbol(text);
    }

    private void generarDesdeArchivo() {
        try {
            String text = Files.readString(Path.of("compressed.txt"));
            text = text.replaceAll("[\\r\\n\\uFEFF]", "").trim(); // elimina saltos, retornos y BOM
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El archivo está vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mostrarArbol(text);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo compressed.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void mostrarArbol(String text) {
        mainPanel.removeAll();
        Map<Character, Integer> freqMap = HuffmanTree.buildFrequencyMap(text);
        HuffmanTree tree = new HuffmanTree(freqMap);
        HuffmanNode root = tree.getRoot();
        TreePanel panel = new TreePanel(root);
        mainPanel.add(panel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}

