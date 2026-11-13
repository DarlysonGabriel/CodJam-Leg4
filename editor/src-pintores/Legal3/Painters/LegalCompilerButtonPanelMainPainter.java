/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import com.jhlabs.image.GaussianFilter;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.painter.Painter;

public class LegalCompilerButtonPanelMainPainter
implements Painter {
    private java.awt.Color cInner;
    private java.awt.Color cOuter;
    private BufferedImage background = null;

    public LegalCompilerButtonPanelMainPainter(java.awt.Color cInner, java.awt.Color cOuter) {
        cInner = Legal3.Painters.Color.tema(cInner);
        cOuter = Legal3.Painters.Color.tema(cOuter);
        this.cInner = cInner;
        this.cOuter = cOuter;
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        if (this.background != null) {
            g.drawImage((Image)this.background, 0, 0, null);
        } else {
            BufferedImage img = new BufferedImage(width, height, 2);
            Graphics2D gImg = img.createGraphics();
            gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RadialGradientPaint paint = new RadialGradientPaint(new Point2D.Float(width / 2, height / 2), height / 2, new float[]{0.5f, 1.0f}, new java.awt.Color[]{this.cInner, this.cOuter});
            gImg.setPaint(paint);
            gImg.fillRoundRect(1, 1, width - 8, height - 8, 20, 20);
            gImg.setColor(new Color(255, 255, 255));
            gImg.setStroke(new BasicStroke(2.0f));
            gImg.drawRoundRect(1, 1, width - 8, height - 8, 20, 20);
            this.background = LegalCompilerButtonPanelMainPainter.applyShadow(img, 2, Color.BLACK, 0.4f);
            g.drawImage((Image)this.background, 0, 0, null);
        }
    }

    public static BufferedImage createCompatibleImage(BufferedImage image) {
        return LegalCompilerButtonPanelMainPainter.createCompatibleImage(image, image.getWidth(), image.getHeight());
    }

    public static BufferedImage createCompatibleImage(int width, int height) {
        return LegalCompilerButtonPanelMainPainter.createCompatibleImage(width, height, 3);
    }

    public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        BufferedImage image = LegalCompilerButtonPanelMainPainter.getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
        image.coerceData(true);
        return image;
    }

    public static BufferedImage createCompatibleImage(BufferedImage image, int width, int height) {
        return LegalCompilerButtonPanelMainPainter.getGraphicsConfiguration().createCompatibleImage(width, height, image.getTransparency());
    }

    public static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static BufferedImage generateMask(BufferedImage imgSource, java.awt.Color color, float alpha) {
        int imgWidth = imgSource.getWidth();
        int imgHeight = imgSource.getHeight();
        BufferedImage imgBlur = LegalCompilerButtonPanelMainPainter.createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgBlur.createGraphics();
        LegalCompilerButtonPanelMainPainter.applyQualityRenderingHints(g2);
        g2.drawImage((Image)imgSource, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(5, alpha));
        g2.setColor(color);
        g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
        g2.dispose();
        return imgBlur;
    }

    public static void applyQualityRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static BufferedImage generateBlur(BufferedImage imgSource, int size, java.awt.Color color, float alpha) {
        GaussianFilter filter = new GaussianFilter(size);
        int imgWidth = imgSource.getWidth();
        int imgHeight = imgSource.getHeight();
        BufferedImage imgBlur = LegalCompilerButtonPanelMainPainter.createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgBlur.createGraphics();
        LegalCompilerButtonPanelMainPainter.applyQualityRenderingHints(g2);
        g2.drawImage((Image)imgSource, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(5, alpha));
        g2.setColor(color);
        g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
        g2.dispose();
        imgBlur = filter.filter(imgBlur, null);
        return imgBlur;
    }

    public static BufferedImage applyShadow(BufferedImage imgSource, int size, java.awt.Color color, float alpha) {
        BufferedImage result = LegalCompilerButtonPanelMainPainter.createCompatibleImage(imgSource, imgSource.getWidth() + size * 2, imgSource.getHeight() + size * 2);
        Graphics2D g2d = result.createGraphics();
        g2d.drawImage((Image)LegalCompilerButtonPanelMainPainter.generateShadow(imgSource, size, color, alpha), size, size, null);
        g2d.drawImage((Image)imgSource, 0, 0, null);
        g2d.dispose();
        return result;
    }

    public static BufferedImage generateShadow(BufferedImage imgSource, int size, java.awt.Color color, float alpha) {
        int imgWidth = imgSource.getWidth() + size * 2;
        int imgHeight = imgSource.getHeight() + size * 2;
        BufferedImage imgMask = LegalCompilerButtonPanelMainPainter.createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgMask.createGraphics();
        LegalCompilerButtonPanelMainPainter.applyQualityRenderingHints(g2);
        int x = Math.round((float)(imgWidth - imgSource.getWidth()) / 2.0f);
        int y = Math.round((float)(imgHeight - imgSource.getHeight()) / 2.0f);
        g2.drawImage((Image)imgSource, x, y, null);
        g2.dispose();
        BufferedImage imgGlow = LegalCompilerButtonPanelMainPainter.generateBlur(imgMask, size * 2, color, alpha);
        return imgGlow;
    }

    public static Image applyMask(BufferedImage sourceImage, BufferedImage maskImage) {
        return LegalCompilerButtonPanelMainPainter.applyMask(sourceImage, maskImage, 6);
    }

    public static BufferedImage applyMask(BufferedImage sourceImage, BufferedImage maskImage, int method) {
        BufferedImage maskedImage = null;
        if (sourceImage != null) {
            int width = maskImage.getWidth(null);
            int height = maskImage.getHeight(null);
            maskedImage = new BufferedImage(width, height, 2);
            Graphics2D mg = maskedImage.createGraphics();
            int x = (width - sourceImage.getWidth(null)) / 2;
            int y = (height - sourceImage.getHeight(null)) / 2;
            mg.drawImage((Image)sourceImage, x, y, null);
            mg.setComposite(AlphaComposite.getInstance(method));
            mg.drawImage((Image)maskImage, 0, 0, null);
            mg.dispose();
        }
        return maskedImage;
    }
}
