package codjam.editor;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

final class IntegracaoIde {

    private static final List<Image> ICONES = carregarIcones();

    private IntegracaoIde() {
    }

    static void instalar() {
        long mascara = AWTEvent.WINDOW_EVENT_MASK | AWTEvent.HIERARCHY_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent evento) {
                if (evento instanceof WindowEvent && evento.getID() == WindowEvent.WINDOW_OPENED) {
                    janelaAberta((Window) evento.getSource());
                } else if (evento instanceof HierarchyEvent) {
                    hierarquiaMudou((HierarchyEvent) evento);
                }
            }
        }, mascara);
    }

    private static void janelaAberta(Window janela) {
        if (!ICONES.isEmpty()) {
            if (janela instanceof Frame) {
                ((Frame) janela).setIconImages(ICONES);
            } else if (janela instanceof Dialog) {
                ((Dialog) janela).setIconImages(ICONES);
            }
        }
        if (janela.getClass().getName().equals("Legal3.LegalMainFrame")) {
            definirFundo(janela);
        }
    }

    private static void definirFundo(Window janela) {
        try {
            janela.getClass().getMethod("setBackgroundColor", java.awt.Color.class).invoke(janela, Tema.FUNDO);
        } catch (ReflectiveOperationException e) {
            janela.setBackground(Tema.FUNDO);
        }
    }

    private static void hierarquiaMudou(HierarchyEvent evento) {
        long mudancas = evento.getChangeFlags();
        if ((mudancas & HierarchyEvent.SHOWING_CHANGED) != 0 && evento.getComponent() instanceof Window
                && evento.getComponent().isShowing()) {
            janelaAberta((Window) evento.getComponent());
        }
        if ((mudancas & HierarchyEvent.PARENT_CHANGED) != 0 && evento.getComponent() instanceof RSyntaxTextArea) {
            adotarDepois((RSyntaxTextArea) evento.getComponent());
        }
    }

    private static void adotarDepois(final RSyntaxTextArea area) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RTextScrollPane rolagem = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class, area);
                if (rolagem != null) {
                    adotar(area, rolagem);
                }
            }
        });
    }

    private static void adotar(final RSyntaxTextArea area, RTextScrollPane rolagem) {
        EditoresDoIde.registrar(area, rolagem);
        area.setPopupMenu(MenuContexto.criar(area, new Runnable() {
            public void run() {
                DialogoPropriedades.mostrar(SwingUtilities.getWindowAncestor(area), EditoresDoIde.preferencias(),
                        new DialogoPropriedades.Aplicador() {
                            public void aplicar(Preferencias novas) {
                                EditoresDoIde.atualizar(novas);
                            }
                        });
            }
        }, true));
    }

    private static List<Image> carregarIcones() {
        List<Image> imagens = new ArrayList<Image>();
        String caminho = System.getProperty("codjam.logo");
        if (caminho == null || !new File(caminho).isFile()) {
            return imagens;
        }
        try {
            Image logo = ImageIO.read(new File(caminho));
            for (int lado : new int[] {16, 32, 64, 128}) {
                imagens.add(logo.getScaledInstance(lado, lado, Image.SCALE_SMOOTH));
            }
        } catch (IOException e) {
            System.err.println("Nao foi possivel carregar a logo: " + e.getMessage());
        }
        return imagens;
    }
}
