/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import Legal3.Legal;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class LegalDialogBackgroundPainter
implements Painter {
    private java.awt.Color borderColor;

    public LegalDialogBackgroundPainter(java.awt.Color c) {
        c = Legal3.Painters.Color.tema(c);
        this.borderColor = c;
    }

    public void setPainterColor(java.awt.Color c) {
        c = Legal3.Painters.Color.tema(c);
        this.borderColor = c;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(240, 240, 240));
        if (Legal.getSo() == 0) {
            g.fillRoundRect(1, 1, width - 3, height - 3, 40, 40);
        } else {
            g.fillRect(1, 1, width - 3, height - 3);
        }
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(this.borderColor);
        if (Legal.getSo() == 0) {
            g.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);
        } else {
            g.drawRect(1, 1, width - 3, height - 3);
        }
    }
}
