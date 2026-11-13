/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class ColetarChartPainter
implements Painter {
    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRoundRect(1, 1, width - 3, height - 3, 15, 15);
        g.setColor(new Color(0, 85, 139));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(1, 1, width - 3, height - 3, 15, 15);
    }
}
