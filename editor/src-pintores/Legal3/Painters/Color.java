package Legal3.Painters;

import codjam.editor.MapaDeCores;

public class Color extends java.awt.Color {

    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color white = WHITE;
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
    public static final Color lightGray = LIGHT_GRAY;
    public static final Color GRAY = new Color(128, 128, 128);
    public static final Color gray = GRAY;
    public static final Color DARK_GRAY = new Color(64, 64, 64);
    public static final Color darkGray = DARK_GRAY;
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color black = BLACK;

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        super(MapaDeCores.mapear(empacotar(a, r, g, b)), true);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1f);
    }

    public Color(float r, float g, float b, float a) {
        this(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), Math.round(a * 255));
    }

    public Color(int rgb) {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, 255);
    }

    public Color(int argb, boolean temAlfa) {
        this((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, temAlfa ? argb >>> 24 : 255);
    }

    public static java.awt.Color tema(java.awt.Color cor) {
        return cor == null || cor instanceof Color ? cor : new Color(cor.getRGB(), true);
    }

    private static int empacotar(int a, int r, int g, int b) {
        return (limitar(a) << 24) | (limitar(r) << 16) | (limitar(g) << 8) | limitar(b);
    }

    private static int limitar(int valor) {
        return Math.max(0, Math.min(255, valor));
    }
}
