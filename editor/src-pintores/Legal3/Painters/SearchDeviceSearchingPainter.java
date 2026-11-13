/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

public class SearchDeviceSearchingPainter
implements Painter {
    private JXPanel parent;
    private BufferedImage img;
    private double angle = 0.0;
    final Ticker ticker = new Ticker();

    public SearchDeviceSearchingPainter(JXPanel p) {
        this.parent = p;
        String image = "painter-images/SearchDialog/module.png";
        try {
            this.img = ImageIO.read(this.getClass().getResourceAsStream(image));
        }
        catch (Exception e) {
            System.out.println("Imagem " + image + " n\u00e3o encontrada");
            System.exit(1);
        }
        this.ticker.start();
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cx = ((width - this.img.getWidth()) / 2 + (width - this.img.getWidth()) / 2 + this.img.getWidth()) / 2;
        int cy = ((height - this.img.getHeight()) / 2 + (height - this.img.getHeight()) / 2 + this.img.getHeight()) / 2;
        int radius1 = this.img.getWidth() / 2;
        int radius2 = radius1 + 7;
        int radius3 = radius2 + 5;
        int x1 = (int)((double)cx + (double)radius2 * Math.cos(this.angle));
        int y1 = (int)((double)cy + (double)radius2 * Math.sin(this.angle));
        int x2 = (int)((double)cx + (double)radius2 * Math.cos(this.angle + Math.PI));
        int y2 = (int)((double)cy + (double)radius2 * Math.sin(this.angle + Math.PI));
        g.setColor(new Color(231, 231, 232));
        g.fillOval(cx - radius3, cy - radius3, 2 * radius3, 2 * radius3);
        g.drawImage(this.img, (width - this.img.getWidth()) / 2, (height - this.img.getHeight()) / 2, (width - this.img.getWidth()) / 2 + this.img.getWidth(), (height - this.img.getHeight()) / 2 + this.img.getHeight(), 0, 0, this.img.getWidth(), this.img.getHeight(), null);
        g.setPaint(new GradientPaint(new Point(x1, y1), new Color(5, 46, 84), new Point(x2, y2), new Color(69, 205, 255)));
        g.setStroke(new BasicStroke(6.0f));
        g.drawOval(cx - radius2, cy - radius2, 2 * radius2, 2 * radius2);
    }

    public void done() {
        this.ticker.done();
    }

    private class Ticker
    extends Thread {
        public boolean update = true;
        private long startTime = System.currentTimeMillis();
        private long currentTime;

        public void done() {
            this.update = false;
        }

        @Override
        public void run() {
            try {
                while (this.update) {
                    if (!SearchDeviceSearchingPainter.this.parent.isVisible()) continue;
                    this.currentTime = System.currentTimeMillis() - this.startTime;
                    SearchDeviceSearchingPainter.this.angle = (float)this.currentTime / 300.0f;
                    SearchDeviceSearchingPainter.this.parent.repaint();
                }
            }
            catch (Exception e) {
                this.update = false;
            }
        }
    }
}
