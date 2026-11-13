/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class LegalCompilerPainter
implements Painter {
    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRoundRect(1, 0, width - 2, height - 1, 40, 40);
        g.setStroke(new BasicStroke(2.0f));
        g.setPaint(new GradientPaint(0.0f, 0.0f, new Color(11, 88, 143), 0.0f, height, new Color(32, 143, 206)));
        g.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);
    }
}
