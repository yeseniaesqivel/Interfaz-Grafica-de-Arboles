package huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {
    public char data;
    public int freq;
    public HuffmanNode left, right;

    public HuffmanNode(char data, int freq) {
        this.data = data;
        this.freq = freq;
    }

    public HuffmanNode(char data, int freq, HuffmanNode left, HuffmanNode right) {
        this.data = data;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return this.freq - other.freq;
    }

    public boolean isLeaf() {
        return (left == null && right == null);
    }
}
