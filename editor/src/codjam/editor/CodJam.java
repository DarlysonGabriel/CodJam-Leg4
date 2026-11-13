package codjam.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;

public final class CodJam {

    private CodJam() {
    }

    public static void main(String[] argumentos) throws Exception {
        System.setProperty("swing.aatext", "true");
        Tema.instalar();
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evento) {
                if ("lookAndFeel".equals(evento.getPropertyName())) {
                    Tema.instalar();
                }
            }
        });
        IntegracaoIde.instalar();
        Legal3.Legal.main(argumentos);
    }
}
