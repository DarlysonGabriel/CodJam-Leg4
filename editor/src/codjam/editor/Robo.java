package codjam.editor;

import java.awt.Window;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;

final class Robo {

    private Robo() {
    }

    static void controlar() {
        trocarDeModo("openControlDevice");
    }

    static void coletarDados() {
        trocarDeModo("openColetarMenu");
    }

    private static void trocarDeModo(String metodoDeAbertura) {
        Object legal = encontrarLegal();
        if (legal == null) {
            System.err.println("O IDE ainda nao esta pronto para trocar de modo.");
            return;
        }
        try {
            Boolean fechou = (Boolean) legal.getClass().getMethod("closeCompiler").invoke(legal);
            if (fechou != null && !fechou) {
                return;
            }
            legal.getClass().getMethod(metodoDeAbertura).invoke(legal);
        } catch (ReflectiveOperationException e) {
            System.err.println("Nao foi possivel abrir o modo do robo: " + e);
        }
    }

    private static Object encontrarLegal() {
        for (Window janela : Window.getWindows()) {
            if (!janela.getClass().getName().equals("Legal3.LegalMainFrame")) {
                continue;
            }
            for (WindowListener ouvinte : janela.getWindowListeners()) {
                Object legal = legalDoOuvinte(ouvinte);
                if (legal != null) {
                    return legal;
                }
            }
        }
        return null;
    }

    private static Object legalDoOuvinte(WindowListener ouvinte) {
        try {
            Field externo = ouvinte.getClass().getDeclaredField("this$0");
            externo.setAccessible(true);
            Object valor = externo.get(ouvinte);
            return valor != null && valor.getClass().getName().equals("Legal3.Legal") ? valor : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
