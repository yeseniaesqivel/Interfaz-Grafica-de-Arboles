import gui.HuffmanGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new HuffmanGUI().setVisible(true);
        });
    }
}
