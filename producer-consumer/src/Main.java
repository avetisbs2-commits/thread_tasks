import ui.ProducerConsumerFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProducerConsumerFrame frame = new ProducerConsumerFrame();
                frame.setVisible(true);
            }
        });
    }
}
