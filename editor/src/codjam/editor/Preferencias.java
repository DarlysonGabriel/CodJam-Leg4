package codjam.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

final class Preferencias {

    private static final Path ARQUIVO = Paths.get(System.getProperty("user.home"), ".codjam", "editor.properties");

    boolean numerosDeLinha = true;
    boolean destacarLinhaAtual = true;
    boolean quebraDeLinha = false;
    boolean mostrarEspacos = false;
    boolean tabsComoEspacos = false;
    int tamanhoDaFonte = 14;
    int tamanhoDoTab = 3;

    static Preferencias carregar() {
        Preferencias preferencias = new Preferencias();
        if (!Files.isRegularFile(ARQUIVO)) {
            return preferencias;
        }
        Properties propriedades = new Properties();
        try (InputStream entrada = Files.newInputStream(ARQUIVO)) {
            propriedades.load(entrada);
        } catch (IOException e) {
            return preferencias;
        }
        preferencias.numerosDeLinha = booleano(propriedades, "numerosDeLinha", preferencias.numerosDeLinha);
        preferencias.destacarLinhaAtual = booleano(propriedades, "destacarLinhaAtual", preferencias.destacarLinhaAtual);
        preferencias.quebraDeLinha = booleano(propriedades, "quebraDeLinha", preferencias.quebraDeLinha);
        preferencias.mostrarEspacos = booleano(propriedades, "mostrarEspacos", preferencias.mostrarEspacos);
        preferencias.tabsComoEspacos = booleano(propriedades, "tabsComoEspacos", preferencias.tabsComoEspacos);
        preferencias.tamanhoDaFonte = inteiro(propriedades, "tamanhoDaFonte", preferencias.tamanhoDaFonte, 8, 48);
        preferencias.tamanhoDoTab = inteiro(propriedades, "tamanhoDoTab", preferencias.tamanhoDoTab, 1, 12);
        return preferencias;
    }

    void salvar() {
        Properties propriedades = new Properties();
        propriedades.setProperty("numerosDeLinha", String.valueOf(numerosDeLinha));
        propriedades.setProperty("destacarLinhaAtual", String.valueOf(destacarLinhaAtual));
        propriedades.setProperty("quebraDeLinha", String.valueOf(quebraDeLinha));
        propriedades.setProperty("mostrarEspacos", String.valueOf(mostrarEspacos));
        propriedades.setProperty("tabsComoEspacos", String.valueOf(tabsComoEspacos));
        propriedades.setProperty("tamanhoDaFonte", String.valueOf(tamanhoDaFonte));
        propriedades.setProperty("tamanhoDoTab", String.valueOf(tamanhoDoTab));
        try {
            Files.createDirectories(ARQUIVO.getParent());
            try (OutputStream saida = Files.newOutputStream(ARQUIVO)) {
                propriedades.store(saida, "Preferencias do editor CodJam");
            }
        } catch (IOException e) {
            System.err.println("Nao foi possivel salvar as preferencias: " + e.getMessage());
        }
    }

    Preferencias copia() {
        Preferencias copia = new Preferencias();
        copia.numerosDeLinha = numerosDeLinha;
        copia.destacarLinhaAtual = destacarLinhaAtual;
        copia.quebraDeLinha = quebraDeLinha;
        copia.mostrarEspacos = mostrarEspacos;
        copia.tabsComoEspacos = tabsComoEspacos;
        copia.tamanhoDaFonte = tamanhoDaFonte;
        copia.tamanhoDoTab = tamanhoDoTab;
        return copia;
    }

    private static boolean booleano(Properties propriedades, String chave, boolean padrao) {
        String valor = propriedades.getProperty(chave);
        return valor == null ? padrao : Boolean.parseBoolean(valor);
    }

    private static int inteiro(Properties propriedades, String chave, int padrao, int minimo, int maximo) {
        try {
            int valor = Integer.parseInt(propriedades.getProperty(chave, String.valueOf(padrao)).trim());
            return Math.max(minimo, Math.min(maximo, valor));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }
}
