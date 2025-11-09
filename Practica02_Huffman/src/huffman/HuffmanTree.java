package huffman;

import java.util.*;

public class HuffmanTree {
    private HuffmanNode root;

    // Construye el árbol de Huffman a partir de las frecuencias
    public HuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }

        root = pq.poll();
    }

    public HuffmanNode getRoot() {
        return root;
    }

    // Calcula el mapa de frecuencias desde un texto
    public static Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }
}
