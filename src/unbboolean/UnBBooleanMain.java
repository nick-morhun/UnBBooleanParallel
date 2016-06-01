package unbboolean;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import unbboolean.gui.UnBBooleanFrame;

/**
 * UnBBoolean's main class. Starts the software.
 *
 * @author Danilo Balby Silva Castanheira(danbalby@yahoo.com)
 */
public class UnBBooleanMain {

    static boolean showSplash = false;

    /**
     * Main method. Starts the software
     */
    public static void main(String[] args) {

        JFrame fr = new JFrame() {

            public void paint(Graphics g) {
                super.paint(g);
                BufferedImage img;
                try {
                    URL url = UnBBooleanMain.class.getResource("splash.png");
                    img = ImageIO.read(url);
                    g.drawImage(img, 0, 0, null);
                    showSplash = true;
                } catch (Exception e) {
                    System.out.println("Can not find \"splash.png\".");
                }
                char[] loadStr = Text.LOADING.toCharArray();
                g.drawChars(loadStr, 0, loadStr.length, 20, 320);
                char[] verStr = Text.VERSION.toCharArray();
                g.drawChars(verStr, 0, verStr.length, 490, 320);
            }
        };
        fr.setSize(550, 350);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        fr.setLocation((screenSize.width - 550) / 2,
                (screenSize.height - 350) / 2);
        fr.setUndecorated(true);
        fr.repaint();
        fr.setVisible(true);

        //System.out.println("Available processors " + Runtime.getRuntime().availableProcessors());
        UnBBooleanFrame frame = new UnBBooleanFrame();
        frame.setVisible(true);
        fr.setVisible(false);
    }
}