package codjam.editor;

import java.awt.Color;

public final class MapaDeCores {

    private static final double GAMA = 3.0;
    private static final int[] TABELA = criarTabela();

    private MapaDeCores() {
    }

    public static int mapear(int argb) {
        int alfa = argb >>> 24;
        int nivel = (299 * ((argb >> 16) & 0xFF) + 587 * ((argb >> 8) & 0xFF) + 114 * (argb & 0xFF)) / 1000;
        return (alfa << 24) | TABELA[nivel];
    }

    private static int[] criarTabela() {
        int[] tabela = new int[256];
        for (int nivel = 0; nivel < tabela.length; nivel++) {
            tabela[nivel] = gradiente(Math.pow(nivel / 255.0, GAMA)).getRGB() & 0xFFFFFF;
        }
        return tabela;
    }

    private static Color gradiente(double posicao) {
        Color meio = Tema.mistura(Tema.FUNDO, Tema.DESTAQUE, 0.45);
        if (posicao < 0.35) {
            return Tema.mistura(Tema.FUNDO, meio, posicao / 0.35);
        }
        if (posicao < 0.7) {
            return Tema.mistura(meio, Tema.DESTAQUE, (posicao - 0.35) / 0.35);
        }
        return Tema.mistura(Tema.DESTAQUE, Tema.TEXTO, (posicao - 0.7) / 0.3);
    }
}
