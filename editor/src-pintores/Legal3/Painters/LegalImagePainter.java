/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.painter.Painter;

public class LegalImagePainter
implements Painter {
    private BufferedImage img;

    public LegalImagePainter(BufferedImage img) {
        this.img = img;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(this.img, 0, 0, this.img.getWidth(), this.img.getHeight(), 0, 0, this.img.getWidth(), this.img.getHeight(), null);
    }
}
