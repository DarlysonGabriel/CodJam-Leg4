/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import Legal3.Legal;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import org.jdesktop.swingx.painter.Painter;

public class LegalScreenPainter
implements Painter {
    private String title = "";
    private Font anke = null;
    private int menuWidth;
    private int menuHeight;

    public LegalScreenPainter(Legal legal) {
        this(legal, "", 76, 34);
    }

    public LegalScreenPainter(Legal legal, int menuWidth, int menuHeight) {
        this(legal, "", 76, 34);
    }

    public LegalScreenPainter(Legal legal, String title) {
        this(legal, title, 76, 34);
    }

    public LegalScreenPainter(Legal legal, String title, int menuWidth, int menuHeight) {
        this.title = title;
        this.menuWidth = menuWidth;
        this.menuHeight = menuHeight;
        try {
            this.anke = legal.getFontAnke().deriveFont(37.0f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint top = new GradientPaint(0.0f, 0.0f, new Color(80, 168, 221), 0.0f, (float)height / 2.0f - 40.0f, new Color(189, 230, 250));
        GradientPaint bottom = new GradientPaint(0.0f, (float)height / 2.0f, new Color(189, 230, 250), 0.0f, height, new Color(80, 168, 221));
        g.setPaint(top);
        g.fillRoundRect(1, 1, width - 3, height / 2 + 40, 40, 40);
        g.setPaint(bottom);
        g.fillRoundRect(1, height / 2, width - 3, height / 2, 40, 40);
        g.setColor(new Color(0, 85, 139));
        g.fillRoundRect(1, 1, width - 2, 40, 40, 40);
        g.fillRect(0, 21, width, 30);
        g.setColor(new Color(0, 85, 139));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);
        g.setColor(new Color(255, 255, 255));
        g.setStroke(new BasicStroke(1.0f));
        g.fillRoundRect(width - 7 - this.menuWidth, 8, this.menuWidth, this.menuHeight, 34, 34);
        g.setFont(this.anke);
        BasicGraphicsUtils.drawString(g, this.title, 95, 15, 41);
    }
}
