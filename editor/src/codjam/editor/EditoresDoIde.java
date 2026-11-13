package codjam.editor;

import java.awt.Font;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

final class EditoresDoIde {

    private static final Map<RSyntaxTextArea, RTextScrollPane> EDITORES =
            Collections.synchronizedMap(new WeakHashMap<RSyntaxTextArea, RTextScrollPane>());
    private static Preferencias preferencias = Preferencias.carregar();

    private EditoresDoIde() {
    }

    static Preferencias preferencias() {
        return preferencias;
    }

    static void registrar(RSyntaxTextArea area, RTextScrollPane rolagem) {
        if (EDITORES.containsKey(area)) {
            return;
        }
        EDITORES.put(area, rolagem);
        Tema.aplicar(area, rolagem);
        aplicar(area, rolagem, preferencias);
    }

    static void atualizar(Preferencias novas) {
        preferencias = novas;
        preferencias.salvar();
        synchronized (EDITORES) {
            for (Map.Entry<RSyntaxTextArea, RTextScrollPane> editor : EDITORES.entrySet()) {
                aplicar(editor.getKey(), editor.getValue(), novas);
            }
        }
    }

    static void aplicar(RSyntaxTextArea area, RTextScrollPane rolagem, Preferencias p) {
        Font fonte = new Font(Font.MONOSPACED, Font.PLAIN, p.tamanhoDaFonte);
        area.setFont(fonte);
        rolagem.getGutter().setLineNumberFont(fonte);
        rolagem.setLineNumbersEnabled(p.numerosDeLinha);
        area.setHighlightCurrentLine(p.destacarLinhaAtual);
        area.setLineWrap(p.quebraDeLinha);
        area.setWhitespaceVisible(p.mostrarEspacos);
        area.setTabsEmulated(p.tabsComoEspacos);
        area.setTabSize(p.tamanhoDoTab);
    }
}
