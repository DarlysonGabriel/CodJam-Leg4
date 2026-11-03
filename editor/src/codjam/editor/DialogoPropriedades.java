package codjam.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

final class DialogoPropriedades {

    interface Aplicador {
        void aplicar(Preferencias preferencias);
    }

    private DialogoPropriedades() {
    }

    static void mostrar(JFrame janela, Preferencias atuais, final Aplicador aplicador) {
        final JDialog dialogo = new JDialog(janela, "Propriedades", true);
        final JCheckBox numeros = caixa("Mostrar o número da linha", atuais.numerosDeLinha);
        final JCheckBox linhaAtual = caixa("Destacar a linha atual", atuais.destacarLinhaAtual);
        final JCheckBox quebra = caixa("Quebrar linhas longas", atuais.quebraDeLinha);
        final JCheckBox espacos = caixa("Mostrar espaços e tabs", atuais.mostrarEspacos);
        final JCheckBox tabsEspacos = caixa("Usar espaços no lugar de tabs", atuais.tabsComoEspacos);
        final JSpinner fonte = new JSpinner(new SpinnerNumberModel(atuais.tamanhoDaFonte, 8, 48, 1));
        final JSpinner tab = new JSpinner(new SpinnerNumberModel(atuais.tamanhoDoTab, 1, 12, 1));

        JPanel opcoes = new JPanel(new GridLayout(0, 1, 0, 6));
        opcoes.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        opcoes.add(numeros);
        opcoes.add(linhaAtual);
        opcoes.add(quebra);
        opcoes.add(espacos);
        opcoes.add(tabsEspacos);
        opcoes.add(linha("Tamanho da fonte", fonte));
        opcoes.add(linha("Tamanho do tab", tab));

        JButton ok = botao("OK");
        JButton cancelar = botao("Cancelar");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                Preferencias novas = new Preferencias();
                novas.numerosDeLinha = numeros.isSelected();
                novas.destacarLinhaAtual = linhaAtual.isSelected();
                novas.quebraDeLinha = quebra.isSelected();
                novas.mostrarEspacos = espacos.isSelected();
                novas.tabsComoEspacos = tabsEspacos.isSelected();
                novas.tamanhoDaFonte = (Integer) fonte.getValue();
                novas.tamanhoDoTab = (Integer) tab.getValue();
                aplicador.aplicar(novas);
                dialogo.dispose();
            }
        });
        cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                dialogo.dispose();
            }
        });
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.add(ok);
        botoes.add(cancelar);

        ((JComponent) dialogo.getContentPane()).setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        dialogo.getContentPane().add(opcoes, BorderLayout.CENTER);
        dialogo.getContentPane().add(botoes, BorderLayout.SOUTH);
        dialogo.getRootPane().setDefaultButton(ok);
        dialogo.pack();
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }

    private static JButton botao(String texto) {
        JButton botao = new JButton(texto);
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setOpaque(true);
        botao.setBackground(Tema.mistura(Tema.FUNDO, Tema.DESTAQUE, 0.55));
        botao.setForeground(Tema.TEXTO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Tema.DESTAQUE),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        return botao;
    }

    private static JCheckBox caixa(String texto, boolean marcada) {
        JCheckBox caixa = new JCheckBox(texto, marcada);
        caixa.setOpaque(false);
        caixa.setFocusPainted(false);
        return caixa;
    }

    private static JPanel linha(String texto, JSpinner campo) {
        JPanel painel = new JPanel(new BorderLayout(12, 0));
        painel.setOpaque(false);
        painel.add(new JLabel(texto), BorderLayout.CENTER);
        painel.add(campo, BorderLayout.EAST);
        return painel;
    }
}
