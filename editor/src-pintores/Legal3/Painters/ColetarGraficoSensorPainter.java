/*
 * Decompiled with CFR 0.152.
 */
package Legal3.Painters;

import Legal3.Legal;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import org.jdesktop.swingx.painter.Painter;

public class ColetarGraficoSensorPainter
implements Painter {
    public static final int SELECTED_LIGHT = 3;
    public static final int SELECTED_SOUND = 4;
    public static final int SELECTED_COLOR = 2;
    public static final int SELECTED_TEMPERATURE = 5;
    public static final int SELECTED_CONTACT = -5;
    public static final int SELECTED_PROXIMITY = -7;
    public static final int SELECTED_LINE = -6;
    public static final int SELECTED_RESISTANCE = 6;
    public static final int SELECTED_DISTANCE = 8;
    public static final int SELECTED_VOLTMETER = 7;
    public static final int SELECTED_GENERICO_ANALOGICO = 20;
    public static final int SELECTED_GENERICO_DIGITAL = -20;
    private BufferedImage imgLight;
    private BufferedImage imgSound;
    private BufferedImage imgColor;
    private BufferedImage imgTemperature;
    private BufferedImage imgContact;
    private BufferedImage imgProximity;
    private BufferedImage imgLine;
    private BufferedImage imgResistance;
    private BufferedImage imgDistance;
    private BufferedImage imgVoltmeter;
    private BufferedImage imgGenericoAnalogico;
    private BufferedImage imgGenericoDigital;
    private int sensorSelected = 0;
    private int port;
    private Font anke;
    private boolean selected = false;
    private boolean over = false;
    private boolean pressed = false;

    public ColetarGraficoSensorPainter(Legal legal, int port) {
        this.port = port;
        try {
            this.anke = legal.getFontAnke().deriveFont(28.0f);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.imgLight = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/luz.png"));
            this.imgSound = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/ruido.png"));
            this.imgColor = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/palette.png"));
            this.imgTemperature = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/thermo.png"));
            this.imgContact = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/contato.png"));
            this.imgProximity = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/prox.png"));
            this.imgLine = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/faixa.png"));
            this.imgResistance = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/resistencia.png"));
            this.imgDistance = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/distancia.png"));
            this.imgVoltmeter = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/voltimetro.png"));
            this.imgGenericoAnalogico = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/generico_analogico.png"));
            this.imgGenericoDigital = ImageIO.read(this.getClass().getResourceAsStream("painter-images/Coletar/generico_digital.png"));
        }
        catch (Exception e) {
            System.out.println("Imagem n\u00e3o encontrada");
            System.exit(1);
        }
    }

    public void paint(Graphics2D g, Object component, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (this.isOver()) {
            if (this.isPressed()) {
                g.setColor(new Color(31, 188, 255));
            } else {
                g.setColor(new Color(141, 222, 255));
            }
        } else {
            if (this.isPressed()) {
                g.setColor(new Color(31, 188, 255));
            }
            if (this.isSelected()) {
                g.setColor(new Color(255, 255, 255));
            } else {
                g.setColor(new Color(190, 231, 255));
            }
        }
        g.fillRoundRect(1, 1, width - 3, height - 3, 40, 40);
        g.setColor(new Color(0, 85, 139));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(1, 1, width - 3, height - 3, 40, 40);
        g.setColor(new Color(0, 0, 0));
        g.setFont(this.anke);
        BasicGraphicsUtils.drawString(g, "S" + this.port + ":", 95, 10, 38);
        BufferedImage img = this.getSelectecImage();
        if (img != null) {
            g.drawImage(img, 50, 4, img.getWidth() + 50, img.getHeight() + 4, 0, 0, img.getWidth(), img.getHeight(), null);
        }
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSensorSelected() {
        return this.sensorSelected;
    }

    public void setSensorSelected(int sensorSelected) {
        this.sensorSelected = sensorSelected;
    }

    private BufferedImage getSelectecImage() {
        switch (this.sensorSelected) {
            case 0: {
                return null;
            }
            case 2: {
                return this.imgColor;
            }
            case 3: {
                return this.imgLight;
            }
            case 4: {
                return this.imgSound;
            }
            case 5: {
                return this.imgTemperature;
            }
            case 6: {
                return this.imgResistance;
            }
            case 7: {
                return this.imgVoltmeter;
            }
            case 8: {
                return this.imgDistance;
            }
            case -5: {
                return this.imgContact;
            }
            case -6: {
                return this.imgLine;
            }
            case -7: {
                return this.imgProximity;
            }
            case 20: {
                return this.imgGenericoAnalogico;
            }
            case -20: {
                return this.imgGenericoDigital;
            }
        }
        return null;
    }

    public boolean isOver() {
        return this.over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
