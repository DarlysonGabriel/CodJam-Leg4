/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.jdesktop.swingx.painter.Painter;

public class LegalCompilerMessagePanelPainter
implements Painter {
    public static final int ERROR = 0;
    public static final int EDITING = 1;
    public static final int SUCCESS = 2;
    public static final int WARNING = 3;
    private int state = 2;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(210, 210, 210));
        g.fillRoundRect(1, 0, width - 2, height - 1, 40, 40);
        g.setStroke(new BasicStroke(1.0f));
        switch (this.state) {
            case 1: {
                g.setColor(Color.yellow);
                break;
            }
            case 3: {
                g.setColor(Color.yellow);
                break;
            }
            case 2: {
                g.setColor(new Color(0, 100, 0));
                break;
            }
            case 0: {
                g.setColor(Color.red);
            }
        }
        g.drawRoundRect(0, 0, width - 2, height - 2, 40, 40);
    }
}
