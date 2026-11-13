/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class LegalRoundedPanelPainter
implements Painter {
    private java.awt.Color color;
    private int radius = 40;
    private int stroke = 1;

    public LegalRoundedPanelPainter(java.awt.Color c) {
        c = Legal3.Painters.Color.tema(c);
        this.color = c;
    }

    public LegalRoundedPanelPainter(java.awt.Color c, int radius) {
        this(c, radius, 1);
    }

    public LegalRoundedPanelPainter(java.awt.Color c, int radius, int stroke) {
        c = Legal3.Painters.Color.tema(c);
        this.color = c;
        this.stroke = stroke;
        this.radius = radius;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 255, 255));
        g.fillRoundRect(1, 0, width - 2, height - 1, this.radius, this.radius);
        g.setStroke(new BasicStroke(this.stroke));
        g.setColor(this.color);
        g.drawRoundRect(this.stroke / 2, this.stroke / 2, width - this.stroke, height - this.stroke, this.radius, this.radius);
    }
}
