package codjam.editor;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public final class CodJamEditor {

    private final JFrame janela = new JFrame("CodJam");
    private final RSyntaxTextArea area = new RSyntaxTextArea();
    private final RTextScrollPane rolagem = new RTextScrollPane(area);
    private Preferencias preferencias = Preferencias.carregar();

    private CodJamEditor() {
        area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LEGAL_3);
        area.setAntiAliasingEnabled(true);
        area.setPopupMenu(MenuContexto.criar(area, new Runnable() {
            public void run() {
                abrirPropriedades();
            }
        }));
        Tema.aplicar(area, rolagem);
        aplicar(preferencias);

        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setIconImages(icones());
        janela.getContentPane().setBackground(Tema.FUNDO);
        janela.getContentPane().add(rolagem);
        janela.setSize(900, 600);
        janela.setLocationRelativeTo(null);
        janela.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent evento) {
                area.requestFocusInWindow();
            }
        });
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Tema.instalar();
                new CodJamEditor().janela.setVisible(true);
            }
        });
    }

    private void abrirPropriedades() {
        DialogoPropriedades.mostrar(janela, preferencias, new DialogoPropriedades.Aplicador() {
            public void aplicar(Preferencias novas) {
                preferencias = novas;
                preferencias.salvar();
                CodJamEditor.this.aplicar(preferencias);
            }
        });
    }

    private void aplicar(Preferencias p) {
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

    private static List<Image> icones() {
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
