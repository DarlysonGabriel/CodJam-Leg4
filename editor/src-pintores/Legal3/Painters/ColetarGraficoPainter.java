/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.painter.Painter;

public class ColetarGraficoPainter
implements Painter {
    private BufferedImage img1;
    private BufferedImage img2;
    private BufferedImage img3;
    private BufferedImage img4;
    private BufferedImage img5;
    private BufferedImage img6;
    private BufferedImage img7;
    private BufferedImage img8;
    private BufferedImage img9;

    public ColetarGraficoPainter() {
        try {
            this.img1 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/top-left.png"));
            this.img2 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/top-middle.png"));
            this.img3 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/top-right.png"));
            this.img4 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/horizontal-separator.png"));
            this.img5 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/left-border.png"));
            this.img6 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/horizontal-separator.png"));
            this.img7 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/bottom-left.png"));
            this.img8 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/bottom-middle.png"));
            this.img9 = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Grafico/bottom-right.png"));
        }
        catch (Exception e) {
            System.out.println("Imagem n\u00e3o encontrada");
            System.exit(1);
        }
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0, 101, 46));
        g.fill(new Rectangle2D.Double(0.0, 50.0, width, height - 107));
        g.drawImage(this.img1, 0, 0, this.img1.getWidth(), this.img1.getHeight(), 0, 0, this.img1.getWidth(), this.img1.getHeight(), null);
        g.drawImage(this.img2, 32, 0, width - 27, this.img2.getHeight(), 0, 0, this.img2.getWidth(), this.img2.getHeight(), null);
        g.drawImage(this.img3, width - 27, 0, width, this.img3.getHeight(), 0, 0, this.img3.getWidth(), this.img3.getHeight(), null);
        g.drawImage(this.img4, 0, 50, width, this.img4.getHeight() + 50, 0, 0, this.img4.getWidth(), this.img4.getHeight(), null);
        g.drawImage(this.img5, 0, 50, this.img5.getWidth(), height - 57, 0, 0, this.img5.getWidth(), this.img5.getHeight(), null);
        g.drawImage(this.img6, 0, height - 55 - this.img6.getHeight(), width, height - 55, 0, 0, this.img6.getWidth(), this.img6.getHeight(), null);
        g.drawImage(this.img7, 0, height - this.img7.getHeight(), this.img7.getWidth(), height, 0, 0, this.img7.getWidth(), this.img7.getHeight(), null);
        g.drawImage(this.img8, 20, height - this.img8.getHeight(), width - 22, height, 0, 0, this.img8.getWidth(), this.img8.getHeight(), null);
        g.drawImage(this.img9, width - this.img9.getWidth(), height - this.img9.getHeight(), width, height, 0, 0, this.img9.getWidth(), this.img9.getHeight(), null);
    }
}
