package codjam.editor;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class BarraDeRolagem extends BasicScrollBarUI {

    private static final int LARGURA = 12;

    public static ComponentUI createUI(JComponent componente) {
        return new BarraDeRolagem();
    }

    @Override
    protected void configureScrollBarColors() {
        thumbColor = Tema.mistura(Tema.FUNDO, Tema.DESTAQUE, 0.55);
        thumbDarkShadowColor = Tema.BORDA;
        thumbHighlightColor = Tema.DESTAQUE;
        thumbLightShadowColor = Tema.BORDA;
        trackColor = Tema.FUNDO;
        trackHighlightColor = Tema.PAINEL;
    }

    @Override
    public Dimension getPreferredSize(JComponent componente) {
        return new Dimension(LARGURA, LARGURA);
    }

    @Override
    protected JButton createDecreaseButton(int orientacao) {
        return botaoInvisivel();
    }

    @Override
    protected JButton createIncreaseButton(int orientacao) {
        return botaoInvisivel();
    }

    private static JButton botaoInvisivel() {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(0, 0));
        botao.setMinimumSize(new Dimension(0, 0));
        botao.setMaximumSize(new Dimension(0, 0));
        botao.setBackground(Color.BLACK);
        return botao;
    }
}
