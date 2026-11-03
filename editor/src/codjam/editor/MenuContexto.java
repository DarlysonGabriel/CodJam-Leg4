package codjam.editor;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

final class MenuContexto {

    private MenuContexto() {
    }

    static JPopupMenu criar(final RSyntaxTextArea area, Runnable abrirPropriedades) {
        final JMenuItem desfazer = item("Desfazer", new Runnable() {
            public void run() {
                area.undoLastAction();
            }
        });
        final JMenuItem copiar = item("Copiar", new Runnable() {
            public void run() {
                area.copy();
            }
        });
        final JMenuItem colar = item("Colar", new Runnable() {
            public void run() {
                area.paste();
            }
        });
        JMenuItem propriedades = item("Propriedades", abrirPropriedades);

        JPopupMenu menu = new JPopupMenu();
        menu.add(desfazer);
        menu.add(copiar);
        menu.add(colar);
        menu.addSeparator();
        menu.add(propriedades);
        menu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent evento) {
                desfazer.setEnabled(area.canUndo());
                copiar.setEnabled(area.getSelectedText() != null);
                colar.setEnabled(areaDeTransferenciaTemTexto());
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent evento) {
            }

            public void popupMenuCanceled(PopupMenuEvent evento) {
            }
        });
        return menu;
    }

    private static JMenuItem item(String texto, final Runnable acao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evento) {
                acao.run();
            }
        });
        return item;
    }

    private static boolean areaDeTransferenciaTemTexto() {
        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
        } catch (IllegalStateException e) {
            return true;
        }
    }
}
