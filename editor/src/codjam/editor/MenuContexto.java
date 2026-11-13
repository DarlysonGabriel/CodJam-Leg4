package codjam.editor;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

final class MenuContexto {

    private MenuContexto() {
    }

    static JPopupMenu criar(RSyntaxTextArea area, Runnable abrirPropriedades) {
        return criar(area, abrirPropriedades, false);
    }

    static JPopupMenu criar(final RSyntaxTextArea area, Runnable abrirPropriedades, boolean comRobo) {
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

        JPopupMenu menu = new JPopupMenu();
        menu.add(desfazer);
        menu.add(copiar);
        menu.add(colar);
        if (comRobo) {
            menu.addSeparator();
            menu.add(menuDoRobo());
        }
        menu.addSeparator();
        menu.add(item("Propriedades", abrirPropriedades));
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

    private static JMenu menuDoRobo() {
        JMenu robo = new JMenu("Robô");
        robo.add(item("Controlar", new Runnable() {
            public void run() {
                Robo.controlar();
            }
        }));
        robo.add(item("Coletar dados", new Runnable() {
            public void run() {
                Robo.coletarDados();
            }
        }));
        return robo;
    }

    private static JMenuItem item(String texto, final Runnable acao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
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
