/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class LegalControlarSensorPainter
implements Painter {
    private java.awt.Color borderColor;
    private java.awt.Color backgroundColor = Color.WHITE;

    public LegalControlarSensorPainter(java.awt.Color b) {
        b = Legal3.Painters.Color.tema(b);
        this.borderColor = b;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(247, 247, 248));
        g.fillRect(0, 0, width, height);
        g.setColor(this.backgroundColor);
        g.fillRoundRect(2, 2, width - 4, height - 4, 25, 25);
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(this.borderColor);
        g.drawRoundRect(1, 1, width - 3, height - 3, 25, 25);
    }

    public java.awt.Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(java.awt.Color backgroundColor) {
        backgroundColor = Legal3.Painters.Color.tema(backgroundColor);
        this.backgroundColor = backgroundColor;
    }
}
