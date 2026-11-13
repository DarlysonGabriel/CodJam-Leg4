/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.painter.Painter;

public class LegalTopRightMenusPainter
implements Painter {
    private BufferedImage img1;

    public LegalTopRightMenusPainter() {
        try {
            this.img1 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Programar/top_right_icons_background.png"));
        }
        catch (Exception e) {
            System.out.println("Imagem n\u00e3o encontrada");
            System.exit(1);
        }
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            g.drawImage(this.img1, 0, 0, width, height, 0, 0, this.img1.getWidth(), this.img1.getHeight(), null);
        }
        catch (Exception e) {
            System.out.println("Imagem n\u00e3o encontrada");
            System.exit(1);
        }
    }
}
