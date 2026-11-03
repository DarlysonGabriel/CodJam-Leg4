package codjam.editor;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;

final class Tema {

    static final Color FUNDO = new Color(0x060609);
    static final Color TEXTO = new Color(0xEAE6EE);
    static final Color DESTAQUE = new Color(0x9049E3);

    static final Color PAINEL = mistura(FUNDO, DESTAQUE, 0.08);
    static final Color BORDA = mistura(FUNDO, DESTAQUE, 0.35);
    static final Color LINHA_ATUAL = mistura(FUNDO, DESTAQUE, 0.18);
    static final Color SELECAO = mistura(FUNDO, DESTAQUE, 0.55);
    static final Color TEXTO_SUAVE = mistura(FUNDO, TEXTO, 0.45);
    static final Color DESTAQUE_CLARO = mistura(DESTAQUE, TEXTO, 0.45);

    private Tema() {
    }

    static Color mistura(Color de, Color para, double parte) {
        int r = (int) Math.round(de.getRed() + (para.getRed() - de.getRed()) * parte);
        int g = (int) Math.round(de.getGreen() + (para.getGreen() - de.getGreen()) * parte);
        int b = (int) Math.round(de.getBlue() + (para.getBlue() - de.getBlue()) * parte);
        return new Color(r, g, b);
    }

    static void instalar() {
        ColorUIResource fundo = new ColorUIResource(FUNDO);
        ColorUIResource painel = new ColorUIResource(PAINEL);
        ColorUIResource texto = new ColorUIResource(TEXTO);
        ColorUIResource destaque = new ColorUIResource(DESTAQUE);
        ColorUIResource borda = new ColorUIResource(BORDA);

        for (String chave : new String[] {"Panel", "OptionPane", "CheckBox", "Label", "Spinner", "Button", "PopupMenu",
                "MenuItem", "Separator", "ScrollBar", "TextField", "FormattedTextField"}) {
            UIManager.put(chave + ".background", painel);
            UIManager.put(chave + ".foreground", texto);
        }
        UIManager.put("Panel.background", fundo);
        UIManager.put("OptionPane.background", fundo);
        UIManager.put("CheckBox.background", fundo);
        UIManager.put("CheckBox.focus", destaque);
        UIManager.put("Label.background", fundo);
        UIManager.put("Button.select", new ColorUIResource(SELECAO));
        UIManager.put("Button.focus", destaque);
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(SELECAO));
        UIManager.put("MenuItem.selectionForeground", texto);
        UIManager.put("MenuItem.disabledForeground", new ColorUIResource(TEXTO_SUAVE));
        UIManager.put("PopupMenu.border", javax.swing.BorderFactory.createLineBorder(BORDA));
        UIManager.put("Separator.foreground", borda);
        UIManager.put("Separator.background", painel);
        UIManager.put("ScrollBarUI", BarraDeRolagem.class.getName());
        UIManager.put("ScrollBar.thumb", destaque);
        UIManager.put("ScrollBar.thumbShadow", borda);
        UIManager.put("ScrollBar.thumbHighlight", new ColorUIResource(DESTAQUE_CLARO));
        UIManager.put("ScrollBar.thumbDarkShadow", borda);
        UIManager.put("ScrollBar.track", fundo);
        UIManager.put("ScrollBar.trackHighlight", painel);
        UIManager.put("Spinner.background", fundo);
        UIManager.put("TextField.caretForeground", texto);
        UIManager.put("TextField.selectionBackground", new ColorUIResource(SELECAO));
        UIManager.put("TextField.selectionForeground", texto);
    }

    static void aplicar(RSyntaxTextArea area, RTextScrollPane rolagem) {
        area.setBackground(FUNDO);
        area.setForeground(TEXTO);
        area.setCaretColor(TEXTO);
        area.setSelectionColor(SELECAO);
        area.setSelectedTextColor(TEXTO);
        area.setUseSelectedTextColor(true);
        area.setCurrentLineHighlightColor(LINHA_ATUAL);
        area.setFadeCurrentLineHighlight(false);
        area.setMarginLineColor(BORDA);
        area.setMatchedBracketBGColor(SELECAO);
        area.setMatchedBracketBorderColor(DESTAQUE);
        area.setSyntaxScheme(esquemaDeCores(area.getSyntaxScheme()));

        rolagem.setBorder(null);
        rolagem.getViewport().setBackground(FUNDO);
        rolagem.getGutter().setBackground(PAINEL);
        rolagem.getGutter().setBorderColor(BORDA);
        rolagem.getGutter().setLineNumberColor(TEXTO_SUAVE);
    }

    private static SyntaxScheme esquemaDeCores(SyntaxScheme esquema) {
        for (int tipo = 0; tipo < esquema.getStyleCount(); tipo++) {
            Style estilo = esquema.getStyle(tipo);
            if (estilo != null) {
                estilo.foreground = TEXTO;
                estilo.background = null;
            }
        }
        colorir(esquema, DESTAQUE_CLARO, Token.RESERVED_WORD, Token.RESERVED_WORD_2, Token.FUNCTION, Token.DATA_TYPE);
        colorir(esquema, DESTAQUE, Token.LITERAL_NUMBER_DECIMAL_INT, Token.LITERAL_NUMBER_FLOAT,
                Token.LITERAL_NUMBER_HEXADECIMAL, Token.LITERAL_BOOLEAN);
        colorir(esquema, TEXTO_SUAVE, Token.COMMENT_EOL, Token.COMMENT_MULTILINE, Token.COMMENT_DOCUMENTATION);
        colorir(esquema, DESTAQUE_CLARO, Token.OPERATOR, Token.SEPARATOR);
        return esquema;
    }

    private static void colorir(SyntaxScheme esquema, Color cor, int... tipos) {
        for (int tipo : tipos) {
            Style estilo = esquema.getStyle(tipo);
            if (estilo != null) {
                estilo.foreground = cor;
            }
        }
    }
}
