/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import Legal3.Legal;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import org.jdesktop.swingx.painter.Painter;

public class ColetarProgramarSensorPainter
implements Painter {
    public static final int SHADOW_LEFT = 0;
    public static final int SHADOW_RIGHT = 1;
    private int port;
    private BufferedImage shadowImg;
    private Font anke;
    private int shadowPosition;

    public ColetarProgramarSensorPainter(Legal legal, int port, int shadow) {
        this.port = port;
        this.shadowPosition = shadow != 0 && shadow != 1 ? 1 : shadow;
        try {
            this.shadowImg = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/sensorShadow.png"));
        }
        catch (Exception e) {
            System.out.println("Imagem n\u00e3o encontrada");
            System.exit(1);
        }
        try {
            this.anke = legal.getFontAnke().deriveFont(33.0f);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 255, 255));
        g.fillRoundRect(1, 1, width - 3, height - 3, 40, 40);
        g.setColor(new Color(0, 85, 139));
        g.setStroke(new BasicStroke(4.0f));
        g.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);
        if (this.shadowPosition == 1) {
            g.drawImage(this.shadowImg, 58, 9, this.shadowImg.getWidth() + 58, this.shadowImg.getHeight() + 9, 0, 0, this.shadowImg.getWidth(), this.shadowImg.getHeight(), null);
            g.setColor(new Color(0, 0, 0));
            g.setFont(this.anke);
            BasicGraphicsUtils.drawString(g, "S" + this.port, 95, 10, 47);
        } else {
            g.drawImage(this.shadowImg, 10, 9, this.shadowImg.getWidth() + 10, this.shadowImg.getHeight() + 9, 0, 0, this.shadowImg.getWidth(), this.shadowImg.getHeight(), null);
            g.setColor(new Color(0, 0, 0));
            g.setFont(this.anke);
            BasicGraphicsUtils.drawString(g, "S" + this.port, 95, 68, 47);
        }
    }
}
