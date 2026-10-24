import PNCA.CommandSensorEvent;
import PNCA.CommandSensorListener;
import java.io.*;
import java.util.*;

public class Harness {
    static class Sensores implements CommandSensorListener {
        List<String> lista = new ArrayList<String>();
        public void commandSensorCreated(CommandSensorEvent e) {
            lista.add(e.getType() + ":" + e.getPosition());
        }
    }

    public static void main(String[] args) throws Exception {
        System.err.println("COMPILER=" + PNCA.Compiler.class.getProtectionDomain().getCodeSource().getLocation());
        System.err.println("MONTADOR=" + Montador.Montador.class.getProtectionDomain().getCodeSource().getLocation());
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        String nome = null;
        String[] sec = new String[9];
        int atual = 0;
        String linha;
        while ((linha = r.readLine()) != null) {
            if (linha.startsWith("#### ")) {
                if (nome != null) executa(nome, sec);
                nome = linha.substring(5).trim();
                sec = new String[9];
                for (int i = 0; i < 9; i++) sec[i] = "";
                atual = 0;
            } else if (linha.startsWith("@@")) {
                String s = linha.substring(2).trim().toUpperCase();
                atual = s.equals("P") ? 0 : Integer.parseInt(s.substring(1));
            } else if (nome != null) {
                sec[atual] += linha + "\n";
            }
        }
        if (nome != null) executa(nome, sec);
    }

    static String esc(String s) {
        if (s == null) return "null";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\r') b.append("\\r");
            else if (ch < 32 || ch > 126) b.append(String.format("\\u%04x", (int) ch));
            else b.append(ch);
        }
        return b.toString();
    }

    static void executa(String nome, String[] sec) {
        System.out.println("CASO " + nome);
        try {
            PNCA.Compiler c = new PNCA.Compiler(2);
            Sensores sn = new Sensores();
            c.addCommandSensorListener(sn);
            c.iniciaMontador(false);
            Montador.Montador m = c.getMontador();
            compila(c, m, sec, sn);
            System.out.println("  erro=" + c.getErrorCode() + " linha=" + c.getErrorLine()
                + " resto=" + esc(c.getErrorRestToken()) + " porta=" + c.getErroPorta());
            System.out.println("  ce2prom=" + m.getCE2prom() + " limite=" + m.getLimit());
            System.out.println("  sensores=" + sn.lista);
            if (c.getErrorCode() == 0) {
                for (String s : m.getCodigoEnvio()) System.out.println("  | " + esc(s));
            }
        } catch (Throwable t) {
            System.out.println("  EXCECAO " + t);
        }
    }

    static void compila(PNCA.Compiler c, Montador.Montador m, String[] sec, Sensores sn) throws Exception {
        if (sec[0].trim().length() <= 0) { c.setErrorCode(1); return; }
        c.startCompiler(sec[0], true, 0);
        m.iniciaPrograma();
        StringBuilder word = new StringBuilder();
        ArrayList<Integer> stoken = new ArrayList<Integer>();
        c.lexico(word, stoken, c.getTextScanner());
        if (c.getErrorCode() != 0) return;
        while (!stoken.contains(1) && !c.isEndLoop()) c.lexico(word, stoken, c.getTextScanner());
        if (!stoken.contains(1)) { c.setErrorCode(1); return; }

        boolean addEnable = true;
        for (int i = 0; i < 9; i++) {
            String up = sec[i].toUpperCase();
            if (up.indexOf("HABILITE") >= 0 || up.indexOf("DESABILITE") >= 0) addEnable = false;
        }
        if (addEnable) {
            for (int i = 1; i <= 8; i++) {
                String s = sec[i].trim().replaceAll("(?m)^#.*", "").trim();
                if (s.length() > 0) {
                    sn.lista.add("-4:" + i);
                    m.setSensorRequerido(i, -4);
                    m.cSetarEvento(Montador.Montador.getEventos()[i]);
                    m.cLigarEvento(Montador.Montador.getEventos()[i]);
                }
            }
        }
        do {
            c.waitCommand(word, stoken);
        } while (c.getErrorCode() == 0 && !c.isEndLoop() && !stoken.contains(2) && !stoken.contains(3));
        if (c.isEndLoop() && c.getErrorCode() == 10) c.setErrorCode(39);
        if (c.getErrorCode() != 0) return;
        if (!stoken.contains(2) && !stoken.contains(3)) { c.setErrorCode(39); return; }
        m.cFimPrograma();
        if (m.getErrorCode() != 0) { c.setErrorCode(m.getErrorCode(), -1); return; }

        for (int e = 1; e <= 8; e++) {
            if (sec[e].trim().length() > 0) {
                c.startCompiler(sec[e], false, e);
                StringBuilder w2 = new StringBuilder();
                ArrayList<Integer> st2 = new ArrayList<Integer>();
                while (c.getErrorCode() == 0 && !c.isEndLoop()) c.waitCommand(w2, st2);
                if (c.getErrorCode() != 0) return;
            }
            m.cRetornaEvento(e);
            if (m.getErrorCode() != 0) { c.setErrorCode(m.getErrorCode(), -1); return; }
        }
    }
}
