/*
 * Decompiled with CFR 0.152.
 */
package Montador;

import Montador.DeclarationEvent;
import Montador.DeclarationListener;
import Montador.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Montador
implements DeclarationListener {
    private ArrayList<String> codigoObj = new ArrayList();
    private ArrayList<String> codigoEnvio = new ArrayList();
    private Vector<Identifier> variaveis = new Vector(5, 5);
    private Vector<Identifier> procedimentos = new Vector(5, 5);
    private boolean temSubRotina;
    private int cComandos;
    private int iInstrucoes;
    private int cE2prom;
    private int cRam;
    private ArrayList<String> codigoObjFixar = null;
    private ArrayList<String> codigoEnvioFixar = null;
    private int cComandosFixar = -1;
    private int iInstrucoesFixar = -1;
    private int cE2promFixar = -1;
    private int cRamFixar = -1;
    private int sensoresColetor = 0;
    private int tempoColeta = 0;
    private static final int SEMPRE = 255;
    public static final int DIRFRENTE = 1;
    public static final int DIRRE = -1;
    private static final double PAUSASEGUNDALEITURA = 0.3;
    private static final String IPARE = "CP";
    private static final String IVELMOTORES = "CM";
    private static final String IVELMOTORESINDIRETO = "Cm";
    private static final String IVELM1FRENTE = "CF";
    private static final String IVELM2FRENTE = "Cf";
    private static final String IVELM1RE = "CR";
    private static final String IVELM2RE = "Cr";
    private static final String IVELM1FRENTEINDIRETO = "CD";
    private static final String IVELM2FRENTEINDIRETO = "Cd";
    private static final String IVELM1REINDIRETO = "CQ";
    private static final String IVELM2REINDIRETO = "Cq";
    private static final String ISETAEVENTOANALOGICO = "Cx";
    private static final String ISEGUIRLUZ = "CG";
    private static final String ISEGUIRLUZMINUTO = "Cg";
    private static final String IFUGIRLUZ = "CU";
    private static final String IFUGIRLUZMINUTO = "Cu";
    private static final String ISEGUIRLINHABRANCA = "CN";
    private static final String ISEGUIRLINHABRANCAMINUTO = "Cn";
    private static final String ISEGUIRLINHAPRETA = "CL";
    private static final String ISEGUIRLINHAPRETAMINUTO = "Cl";
    private static final String IESPERAR = "CW";
    private static final String IESPERARMINUTO = "Cw";
    private static final String IREPITA = "C[";
    private static final String IATEH = "C]";
    private static final String IMUDO = "CB";
    private static final String ISOM = "C2";
    private static final String IRETEVENT = "C!";
    private static final String IFIM = "C.";
    private static final String ISOMADEC = "CY";
    private static final String IALEATORIO = "C+";
    private static final String IJMP = "CJ";
    private static final String IJE = "C=";
    private static final String IJG = "C>";
    private static final String IJL = "C<";
    private static final String ICMPV = "CX";
    private static final String ICPMVW = "C\u00e1";
    private static final String ICMP_AND = "C@";
    private static final String ICMP_OR = "CO";
    private static final String ICMPS = "CZ";
    private static final String IINSW = "CA";
    private static final String IADDVW = "C\u00e3";
    private static final String IADDM = "C?";
    private static final String ISUBVW = "C\u00e4";
    private static final String ISUBM = "CK";
    private static final String IDIVVW = "C\u00eb";
    private static final String IDIVM = "C\u00ea";
    private static final String IMULTVW = "C\u00f6";
    private static final String IMULTM = "Cp";
    private static final String IRET = "C}";
    private static final String IGOSUB = "C{";
    private static final String INOP = "C\u00f9";
    private static final String ILIGAR = "C#";
    private static final String IDESLIGAR = "C*";
    private static final String ILIGARSAIDA = "C\u00cb";
    private static final String IDESLIGARSAIDA = "C\u00ca";
    private static final String ISERVO = "C|";
    private static final String ISERVOINDIRETO = "C/";
    private static final String IPWM = "C|";
    private static final String IINTRESET = "C0";
    private static final String IINTSET = "C1";
    private static final String IINICIACOLETA = "CI";
    private static final String IFIMCOLETA = "CS";
    private static final String IENVIAINT = "C3";
    private static final String IENVIAVAR = "C4";
    private static final String IRECEBE = "C5";
    private static final String IRECEBEU = "C8";
    public static final int DIRECAOMOTORESFF = 0;
    public static final int DIRECAOMOTORESFR = 1;
    public static final int DIRECAOMOTORESRF = 2;
    public static final int DIRECAOMOTORESRR = 3;
    public static final int DIRECAOMOTORESESQUERDA = 4;
    public static final int DIRECAOMOTORESDIREITA = 5;
    public static final int MOTORVELOCIDADEPADRAO = 70;
    public static final int RAPIDO = 100;
    public static final int NORMAL = 70;
    public static final int LENTO = 20;
    public static final int RAPIDOFAIXA = 70;
    public static final int NORMALFAIXA = 50;
    public static final int LENTOFAIXA = 30;
    public static final int CURVALENTOMENOR = 10;
    public static final int CURVALENTOMAIOR = 30;
    public static final int CURVANORMALMENOR = 20;
    public static final int CURVANORMALMAIOR = 50;
    public static final int CURVARAPIDOMENOR = 50;
    public static final int CURVARAPIDOMAIOR = 100;
    public static final int M1 = 1;
    public static final int M2 = 2;
    public static final int L1 = 1;
    public static final int L2 = 2;
    public static final int L3 = 3;
    public static final int L4 = 4;
    public static final int L5 = 5;
    public static final int L6 = 6;
    public static final int EVENTO1 = 12;
    public static final int EVENTO2 = 22;
    public static final int EVENTO3 = 32;
    public static final int EVENTO4 = 42;
    public static final int EVENTO5 = 52;
    public static final int EVENTO6 = 62;
    public static final int EVENTO7 = 72;
    public static final int EVENTO8 = 82;
    public static final int S1 = 12;
    public static final int S2 = 22;
    public static final int S3 = 32;
    public static final int S4 = 42;
    public static final int S5 = 52;
    public static final int S6 = 62;
    public static final int S7 = 72;
    public static final int S8 = 82;
    private static final int SENSORFAIXA1 = 2;
    private static final int SENSORFAIXA2 = 6;
    private static final int SENSORCOR = 4;
    private static final int SENSORDIGITALFRENTE = 1;
    private static final int SENSORDIGITALRE = 8;
    public static final int AZUL = 160;
    public static final int VERMELHO = 328;
    public static final int AMARELO = 460;
    public static final int VERDE = 608;
    public static final int BRANCO = 788;
    public static final int PRETO = 908;
    public static final int INDEFINIDA = 1023;
    private int errorCode;
    private HashMap<Integer, Integer> corInicio;
    private static final double PAUSASEGUNDALEITURACOR = 1.0;
    private static final double PAUSASEGUNDALEITURAFAIXA = 1.0;
    private int limite = 4000;
    private static int[] eventos = new int[9];

    static {
        Montador.eventos[1] = 12;
        Montador.eventos[2] = 22;
        Montador.eventos[3] = 32;
        Montador.eventos[4] = 42;
        Montador.eventos[5] = 52;
        Montador.eventos[6] = 62;
        Montador.eventos[7] = 72;
        Montador.eventos[8] = 82;
    }

    public Montador(boolean temSub) {
        this.codigoObj.add("n\u00e3o usar esta linha");
        this.codigoEnvio.add("Mp10\r");
        this.corInicio = new HashMap();
        this.corInicio.put(160, 0);
        this.corInicio.put(328, 160);
        this.corInicio.put(460, 328);
        this.corInicio.put(608, 460);
        this.corInicio.put(788, 608);
        this.corInicio.put(908, 788);
        this.corInicio.put(1023, 908);
        int i = 0;
        while (i < 14) {
            this.codigoObj.add("D7F");
            ++i;
        }
        this.temSubRotina = temSub;
        this.cComandos = 0;
        this.iInstrucoes = 0;
        this.cRam = 32;
        this.cE2prom = 14;
        this.variaveis.add(new Identifier("S1", 0, 1));
        this.variaveis.add(new Identifier("S2", 0, 2));
        this.variaveis.add(new Identifier("S3", 0, 3));
        this.variaveis.add(new Identifier("S4", 0, 4));
        this.variaveis.add(new Identifier("S5", 0, 5));
        this.variaveis.add(new Identifier("S6", 0, 6));
        this.variaveis.add(new Identifier("S7", 0, 7));
        this.variaveis.add(new Identifier("S8", 0, 8));
        this.variaveis.add(new Identifier("BAT", 0, 23));
        this.variaveis.add(new Identifier("BTENTER", 0, 24));
        this.variaveis.add(new Identifier("NULO", 0, 25));
        this.variaveis.add(new Identifier("@X1", 0, 26));
        this.variaveis.add(new Identifier("@X2", 0, 27));
        this.variaveis.add(new Identifier("@X3", 0, 28));
        this.variaveis.add(new Identifier("@Y1", 0, 29));
        this.variaveis.add(new Identifier("@Y2", 0, 30));
        this.variaveis.add(new Identifier("@Y3", 0, 31));
        this.variaveis.add(new Identifier("VEZ", 0, 32));
        if (this.temSubRotina) {
            this.setCodigo(IJMP);
            this.setCodigo(INOP);
            this.setCodigo(INOP);
        }
    }

    public void mostraCodObjeto() {
        System.out.println("Total de Comandos: " + this.cComandos);
        System.out.println("Total de instru\u00e7\u00f5es: " + this.iInstrucoes);
        System.out.println("Tamanho do Programa: " + this.cE2prom + " bytes");
        System.out.println("");
        String dAnterior = "";
        int i = 1;
        while (i <= this.cE2prom) {
            System.out.print(String.valueOf(this.dado8bits(i)) + " - " + this.codigoObj.get(i));
            if (this.codigoObj.get(i).startsWith("D")) {
                System.out.print("  (D " + Integer.parseInt(this.codigoObj.get(i).trim().substring(1), 16) + ")");
                if (dAnterior.equals("")) {
                    dAnterior = this.codigoObj.get(i);
                } else {
                    String s16 = String.valueOf(dAnterior.trim().substring(1)) + this.codigoObj.get(i).trim().substring(1);
                    if (Integer.parseInt(s16, 16) <= Short.MAX_VALUE) {
                        System.out.print("  (D " + Integer.parseInt(s16, 16) + ")");
                    } else {
                        System.out.print("  (D " + (short)Integer.parseInt(Integer.toBinaryString(Integer.parseInt(s16, 16)), 2) + ")");
                    }
                    dAnterior = this.codigoObj.get(i);
                }
            } else {
                dAnterior = "";
            }
            System.out.println();
            ++i;
        }
    }

    public String mostraCodObjetoStr() {
        String s = "";
        s = String.valueOf(s) + "Total de Comandos: " + this.cComandos + "\n";
        s = String.valueOf(s) + "Total de instru\u00e7\u00f5es: " + this.iInstrucoes + "\n";
        s = String.valueOf(s) + "Tamanho do Programa: " + this.cE2prom + " bytes" + "\n";
        s = String.valueOf(s) + "\n";
        String dAnterior = "";
        int i = 1;
        while (i <= this.cE2prom) {
            s = String.valueOf(s) + this.dado8bits(i) + " - " + this.codigoObj.get(i);
            if (this.codigoObj.get(i).startsWith("D")) {
                s = String.valueOf(s) + "  (D " + Integer.parseInt(this.codigoObj.get(i).trim().substring(1), 16) + ")";
                if (dAnterior.equals("")) {
                    dAnterior = this.codigoObj.get(i);
                } else {
                    String s16 = String.valueOf(dAnterior.trim().substring(1)) + this.codigoObj.get(i).trim().substring(1);
                    s = Integer.parseInt(s16, 16) <= Short.MAX_VALUE ? String.valueOf(s) + "  (D " + Integer.parseInt(s16, 16) + ")" : String.valueOf(s) + "  (D " + (short)Integer.parseInt(Integer.toBinaryString(Integer.parseInt(s16, 16)), 2) + ")";
                    dAnterior = this.codigoObj.get(i);
                }
            } else {
                dAnterior = "";
            }
            s = String.valueOf(s) + "\n";
            ++i;
        }
        return s;
    }

    public void mostraEnvio() {
        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println(" Total de instru\u00e7\u00f5es: " + this.iInstrucoes);
        System.out.println(" Tamanho do Programa: " + this.codigoEnvio.size() + " bytes");
        System.out.println();
        int i = 0;
        while (i < this.codigoEnvio.size()) {
            System.out.println(String.valueOf(this.dado8bits(i)) + " - " + this.codigoEnvio.get(i));
            ++i;
        }
    }

    public String dado8bits(int x) {
        String aux = Integer.toHexString(x).toUpperCase();
        if (aux.length() < 2) {
            return "D0" + aux;
        }
        return "D" + aux;
    }

    public String dadoCaracter(char caracter) {
        char x = caracter;
        String aux = Integer.toHexString(x).toUpperCase();
        if (aux.length() < 2) {
            return "D0" + aux;
        }
        return "D" + aux;
    }

    public String dado16bitsH(int x) {
        String aux = "";
        if (x >= 0) {
            aux = Integer.toHexString(x).toUpperCase();
        } else {
            aux = Integer.toHexString(x).toUpperCase();
            aux = aux.substring(4);
        }
        if (aux.length() < 3) {
            return "D00";
        }
        if (aux.length() < 4) {
            return "D0" + aux.substring(0, 1);
        }
        return "D" + aux.substring(0, 2);
    }

    public String dado16bitsL(int x) {
        String aux = "";
        if (x >= 0) {
            aux = Integer.toHexString(x).toUpperCase();
        } else {
            aux = Integer.toHexString(x).toUpperCase();
            aux = aux.substring(4);
        }
        if (aux.equals("79")) {
            System.out.println("asf");
        }
        switch (aux.length()) {
            case 1: {
                return "D0" + aux;
            }
            case 2: {
                return "D" + aux;
            }
            case 3: {
                return "D" + aux.substring(1, 3);
            }
            case 4: {
                return "D" + aux.substring(2, 4);
            }
        }
        return "";
    }

    int ajustaTempo(double tempo) {
        return (int)Math.round(tempo);
    }

    public List<String> getCodigoObj() {
        return this.codigoObj;
    }

    public ArrayList<String> getCodigoEnvio() {
        return this.codigoEnvio;
    }

    public ArrayList<String> getCodigoEnvioSemIDs() {
        ArrayList<String> auxCodigoEnvio = new ArrayList<String>(this.codigoEnvio);
        int i = 1;
        while (i < 15) {
            auxCodigoEnvio.set(i, "D7F\r");
            ++i;
        }
        return auxCodigoEnvio;
    }

    void setCodigo(String codigo) {
        ++this.cE2prom;
        if (codigo.substring(0, 1).equals("C")) {
            ++this.iInstrucoes;
        }
        this.codigoObj.add(codigo);
    }

    public void iniciaPrograma() {
        if (this.temSubRotina) {
            this.codigoObj.set(16, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(17, this.dado16bitsL(this.cE2prom + 1));
        }
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getCComandos() {
        return this.cComandos;
    }

    public int getIInstrucoes() {
        return this.iInstrucoes;
    }

    public int getCE2prom() {
        return this.cE2prom;
    }

    private void esperaCor(int cor, int enderecoSalto, int porta) {
        switch (cor) {
            case 1023: {
                this.iIns(porta, 909, 1023);
                break;
            }
            case 908: {
                this.iIns(porta, 789, 908);
                break;
            }
            case 788: {
                this.iIns(porta, 609, 788);
                break;
            }
            case 608: {
                this.iIns(porta, 461, 608);
                break;
            }
            case 460: {
                this.iIns(porta, 329, 460);
                break;
            }
            case 328: {
                this.iIns(porta, 161, 328);
                break;
            }
            case 160: {
                this.iIns(porta, 1, 160);
            }
        }
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoSalto);
    }

    private void esperaObstaculo(int enderecoSalto, int porta) {
        this.setCodigo(ICMPV);
        this.setCodigo(this.dado8bits(porta));
        this.setCodigo(this.dado8bits(1));
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoSalto);
    }

    void iJump(int endereco) {
        this.setCodigo(IJMP);
        this.setCodigo(this.dado16bitsH(endereco));
        this.setCodigo(this.dado16bitsL(endereco));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iJe(int endereco) {
        this.setCodigo(IJE);
        this.setCodigo(this.dado16bitsH(endereco));
        this.setCodigo(this.dado16bitsL(endereco));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iJg(int endereco) {
        this.setCodigo(IJG);
        this.setCodigo(this.dado16bitsH(endereco));
        this.setCodigo(this.dado16bitsL(endereco));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iJl(int endereco) {
        this.setCodigo(IJL);
        this.setCodigo(this.dado16bitsH(endereco));
        this.setCodigo(this.dado16bitsL(endereco));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iCmpv(int registrador, int valor8bits) {
        this.setCodigo(ICMPV);
        this.setCodigo(this.dado8bits(registrador));
        this.setCodigo(this.dado8bits(valor8bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iCmpvw(int registrador, int valor16bits) {
        this.setCodigo(ICPMVW);
        this.setCodigo(this.dado8bits(registrador));
        this.setCodigo(this.dado16bitsH(valor16bits));
        this.setCodigo(this.dado16bitsL(valor16bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iCmps(int registrador1, int registrador2) {
        this.setCodigo(ICMPS);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iIns(int registrador, int valor1, int valor2) {
        this.setCodigo(IINSW);
        this.setCodigo(this.dado8bits(registrador));
        this.setCodigo(this.dado16bitsH(valor1));
        this.setCodigo(this.dado16bitsL(valor1));
        this.setCodigo(this.dado16bitsH(valor2));
        this.setCodigo(this.dado16bitsL(valor2));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iAddvw(int registrador1, int registrador2, int valor16bits) {
        this.setCodigo(IADDVW);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado16bitsH(valor16bits));
        this.setCodigo(this.dado16bitsL(valor16bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iAddm(int registrador1, int registrador2, int registrador3) {
        this.setCodigo(IADDM);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado8bits(registrador3));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iSubvw(int registrador1, int registrador2, int valor16bits) {
        this.setCodigo(ISUBVW);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado16bitsH(valor16bits));
        this.setCodigo(this.dado16bitsL(valor16bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iSubm(int registrador1, int registrador2, int registrador3) {
        this.setCodigo(ISUBM);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado8bits(registrador3));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iDivvw(int registrador1, int registrador2, int valor16bits) {
        this.setCodigo(IDIVVW);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado16bitsH(valor16bits));
        this.setCodigo(this.dado16bitsL(valor16bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iDivm(int registrador1, int registrador2, int registrador3) {
        this.setCodigo(IDIVM);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado8bits(registrador3));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iMultvw(int registrador1, int registrador2, int valor16bits) {
        this.setCodigo(IMULTVW);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado16bitsH(valor16bits));
        this.setCodigo(this.dado16bitsL(valor16bits));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    void iMultm(int registrador1, int registrador2, int registrador3) {
        this.setCodigo(IMULTM);
        this.setCodigo(this.dado8bits(registrador1));
        this.setCodigo(this.dado8bits(registrador2));
        this.setCodigo(this.dado8bits(registrador3));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cRepita(int vezes) {
        ++this.cComandos;
        this.setCodigo(IREPITA);
        this.setCodigo(this.dado8bits(vezes));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cAteh() {
        this.setCodigo(IATEH);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSom(int tipo, double duracao) {
        ++this.cComandos;
        this.setCodigo(ISOM);
        this.setCodigo(this.dado8bits(tipo));
        if (duracao == 255.0) {
            this.setCodigo(this.dado8bits((int)Math.round(duracao)));
        } else {
            this.setCodigo(this.dado8bits((int)Math.round(duracao * 10.0)));
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSomMudo() {
        ++this.cComandos;
        this.setCodigo(IMUDO);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cLigarLed(int led) {
        ++this.cComandos;
        this.setCodigo(ILIGAR);
        this.setCodigo(this.dado8bits(led));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDesligarLed(int led) {
        ++this.cComandos;
        this.setCodigo(IDESLIGAR);
        this.setCodigo(this.dado8bits(led));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cLigarEvento(int evento) {
        ++this.cComandos;
        this.setCodigo(ILIGAR);
        this.setCodigo(this.dado8bits(evento));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDesligarEvento(int evento) {
        ++this.cComandos;
        this.setCodigo(IDESLIGAR);
        this.setCodigo(this.dado8bits(evento));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSetarEventoAnalogico(char op, int valor, int evento) {
        ++this.cComandos;
        this.setCodigo(ISETAEVENTOANALOGICO);
        this.setCodigo(this.dado8bits(evento));
        if (op == '>') {
            valor += 2048;
        }
        this.setCodigo(this.dado16bitsH(valor));
        this.setCodigo(this.dado16bitsL(valor));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSetarEvento(int evento) {
        ++this.cComandos;
        this.setCodigo(IINTSET);
        this.setCodigo(this.dado8bits(evento));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cResetarEvento(int evento) {
        ++this.cComandos;
        this.setCodigo(IINTRESET);
        this.setCodigo(this.dado8bits(evento));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cLigarSaida(int saida) {
        ++this.cComandos;
        this.setCodigo(ILIGARSAIDA);
        this.setCodigo(this.dado8bits(saida));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDesligarSaida(int saida) {
        ++this.cComandos;
        this.setCodigo(IDESLIGARSAIDA);
        this.setCodigo(this.dado8bits(saida));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDesligarMotores() {
        this.cPare();
    }

    public void cServo(int servo, int angulo) {
        ++this.cComandos;
        this.setCodigo("C|");
        this.setCodigo(this.dado8bits(servo));
        this.setCodigo(this.dado8bits(angulo));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cServo(int servo, String var1) {
        ++this.cComandos;
        this.setCodigo(ISERVOINDIRETO);
        this.setCodigo(this.dado8bits(servo));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, var1).getPos()));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cPWM(int pwm, int valor) {
        ++this.cComandos;
        this.setCodigo("C|");
        this.setCodigo(this.dado8bits(pwm));
        this.setCodigo(this.dado8bits(valor));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFimPrograma() {
        this.setCodigo(IFIM);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    private void montaCodigoEnvio() {
        int contaEvento = 1;
        int i = 1;
        while (i <= this.cE2prom) {
            this.codigoEnvio.add(String.valueOf(this.codigoObj.get(i)) + '\r');
            if (this.codigoObj.get(i).equals(IRETEVENT) || this.codigoObj.get(i).equals(IFIM)) {
                switch (contaEvento) {
                    case 1: {
                        this.codigoEnvio.add("M1\r");
                        break;
                    }
                    case 2: {
                        this.codigoEnvio.add("M2\r");
                        break;
                    }
                    case 3: {
                        this.codigoEnvio.add("M3\r");
                        break;
                    }
                    case 4: {
                        this.codigoEnvio.add("M4\r");
                        break;
                    }
                    case 5: {
                        this.codigoEnvio.add("M5\r");
                        break;
                    }
                    case 6: {
                        this.codigoEnvio.add("M6\r");
                        break;
                    }
                    case 7: {
                        this.codigoEnvio.add("M7\r");
                        break;
                    }
                    case 8: {
                        this.codigoEnvio.add("M8\r");
                        break;
                    }
                    case 9: {
                        this.codigoEnvio.add("Mf\r");
                    }
                }
                ++contaEvento;
            }
            ++i;
        }
    }

    public void cSetTempoColeta(int tempo) {
        ++this.cComandos;
        this.tempoColeta = tempo * 2;
    }

    public void cSensorColeta(int sensor) {
        ++this.cComandos;
        switch (sensor) {
            case 1: {
                this.sensoresColetor |= 1;
                break;
            }
            case 2: {
                this.sensoresColetor |= 2;
                break;
            }
            case 3: {
                this.sensoresColetor |= 4;
                break;
            }
            case 4: {
                this.sensoresColetor |= 8;
                break;
            }
            case 5: {
                this.sensoresColetor |= 0x10;
                break;
            }
            case 6: {
                this.sensoresColetor |= 0x20;
                break;
            }
            case 7: {
                this.sensoresColetor |= 0x40;
                break;
            }
            case 8: {
                this.sensoresColetor |= 0x80;
            }
        }
    }

    public void cFimColeta() {
        ++this.cComandos;
        this.setCodigo(IFIMCOLETA);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cIniciaColeta() {
        ++this.cComandos;
        this.setCodigo(IINICIACOLETA);
        this.setCodigo(this.dado8bits(this.getSensoresColetor()));
        this.setCodigo(this.dado16bitsH(this.tempoColeta));
        this.setCodigo(this.dado16bitsL(this.tempoColeta));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cRetornaEvento(int evento) {
        this.setCodigo(IRETEVENT);
        if (evento == 8) {
            this.montaCodigoEnvio();
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGoSub(String nome) {
        ++this.cComandos;
        Identifier proc = this.getIdentifier(this.procedimentos, nome);
        if (proc == null) {
            this.errorCode = 40;
        } else {
            this.setCodigo(IGOSUB);
            this.setCodigo(this.dado16bitsH(proc.getPos()));
            this.setCodigo(this.dado16bitsL(proc.getPos()));
            this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
        }
    }

    public void cRetornaSubRotina() {
        this.setCodigo(IRET);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEsperarSegundo(double tempo) {
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
        } else {
            this.cEsperar(tempo);
        }
    }

    private void cEsperar(double tempo) {
        ++this.cComandos;
        this.setCodigo(IESPERAR);
        int enderecoRetorno = this.cE2prom;
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEsperarMinuto(double tempo) {
        ++this.cComandos;
        this.setCodigo(IESPERARMINUTO);
        int enderecoRetorno = this.cE2prom;
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEsperarCor(int cor) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEsperarObjeto() {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cesperarCondicao(String op, String var1, int var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        this.iCmpvw(v1.getPos(), var2);
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals("<>")) {
            this.iJe(enderecoRetorno);
        }
        if (op.contentEquals(">=")) {
            this.iJl(enderecoRetorno);
        }
        if (op.contentEquals("<=")) {
            this.iJg(enderecoRetorno);
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cesperarCondicaoEntre(String var1, int i1, int i2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        this.iIns(v1.getPos(), i1, i2);
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoRetorno);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cesperarCondicao(String op, String var1, String var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        int enderecoRetorno = this.cE2prom + 1;
        this.iCmps(v1.getPos(), v2.getPos());
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoRetorno);
        }
        if (op.contentEquals("<>")) {
            this.iJe(enderecoRetorno);
        }
        if (op.contentEquals(">=")) {
            this.iJl(enderecoRetorno);
        }
        if (op.contentEquals("<=")) {
            this.iJg(enderecoRetorno);
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cesperarCondicaoCor(String op, String var1, int cor) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        if (op.contentEquals("=")) {
            this.esperaCor(cor, enderecoRetorno, v1.getPos());
        }
        if (op.contentEquals("<>")) {
            this.esperaCor(cor, enderecoRetorno + 15, v1.getPos());
            this.iJump(enderecoRetorno);
        }
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirLuzSegundo(double tempo) {
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cSeguirLuz(tempoInteiro);
        } else {
            this.cSeguirLuz(tempo);
        }
    }

    private void cSeguirLuz(double tempo) {
        int enderecoRetorno = this.cE2prom + 1;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLUZ);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirLuzMinuto(double tempo) {
        int enderecoRetorno = this.cE2prom + 1;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLUZMINUTO);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirLuzCor(int cor) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cSeguirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirLuzFaixa(int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cSeguirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirLuzObstaculo() {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cSeguirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFugirLuzSegundo(double tempo) {
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cFugirLuz(tempoInteiro);
        } else {
            this.cFugirLuz(tempo);
        }
    }

    private void cFugirLuz(double tempo) {
        int enderecoRetorno = this.cE2prom + 1;
        ++this.cComandos;
        this.setCodigo(IFUGIRLUZ);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFugirLuzMinuto(double tempo) {
        int enderecoRetorno = this.cE2prom + 1;
        ++this.cComandos;
        this.setCodigo(IFUGIRLUZMINUTO);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFugirLuzCor(int cor) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cFugirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFugirLuzFaixa(int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cFugirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFugirLuzObstaculo() {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cFugirLuzSegundo(0.5);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaBrancaSegundo(double tempo) {
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cSeguirFaixaBranca(tempoInteiro);
        } else {
            this.cSeguirFaixaBranca(tempo);
        }
    }

    private void cSeguirFaixaBranca(double tempo) {
        int enderecoRetorno = this.cE2prom + 1;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLINHABRANCA);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaBrancaMinuto(double tempo) {
        int enderecoRetorno = this.cE2prom - 2;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLINHABRANCAMINUTO);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaBrancaCor(int cor) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom - 2;
        this.cSeguirFaixaBrancaSegundo(0.5);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaBrancaObstaculo() {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom - 2;
        this.cSeguirFaixaBrancaSegundo(0.5);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaPretaSegundo(double tempo) {
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cSeguirFaixaPreta(tempoInteiro);
        } else {
            this.cSeguirFaixaPreta(tempo);
        }
    }

    private void cSeguirFaixaPreta(double tempo) {
        int enderecoRetorno = this.cE2prom - 2;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLINHAPRETA);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaPretaMinuto(double tempo) {
        int enderecoRetorno = this.cE2prom - 2;
        ++this.cComandos;
        this.setCodigo(ISEGUIRLINHAPRETAMINUTO);
        if (tempo != 255.0) {
            this.setCodigo(this.dado8bits(this.ajustaTempo(tempo)));
        } else {
            this.setCodigo(this.dado8bits(100));
            this.iJump(enderecoRetorno);
        }
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaPretaCor(int cor) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom - 2;
        this.cSeguirFaixaPretaSegundo(0.5);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSeguirFaixaPretaObstaculo() {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom - 2;
        this.cSeguirFaixaPretaSegundo(0.5);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cPare() {
        ++this.cComandos;
        this.setCodigo(IPARE);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cVelocidadeFaixa(int velocidade) {
        this.cVelocidadeMotoresFaixa(velocidade, velocidade);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cVelocidadeFaixa(String velocidade) {
        this.cVelocidadeMotoresFaixa(velocidade, velocidade, 0);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    private void cVelocidadeMotoresFaixa(int velocidadeM1, int velocidadeM2) {
        velocidadeM1 = this.converterVelocidadeMotores(velocidadeM1);
        velocidadeM2 = this.converterVelocidadeMotores(velocidadeM2);
        this.setCodigo(IVELMOTORES);
        this.setCodigo(this.dado8bits(velocidadeM1));
        this.setCodigo(this.dado8bits(velocidadeM2));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    private void cVelocidadeMotoresFaixa(String velocidadeM1, String velocidadeM2, int direcao) {
        this.setCodigo(IVELMOTORESINDIRETO);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, velocidadeM1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, velocidadeM2).getPos()));
        this.setCodigo(this.dado8bits(direcao));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEnviaInteiro(int valor) {
        ++this.cComandos;
        this.setCodigo(IENVIAINT);
        this.setCodigo(this.dado16bitsH(valor));
        this.setCodigo(this.dado16bitsL(valor));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cEnviaVariavel(String var) {
        ++this.cComandos;
        Identifier v = this.getIdentifier(this.variaveis, var);
        this.setCodigo(IENVIAVAR);
        this.setCodigo(this.dado8bits(v.getPos()));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cRecebe(String var) {
        ++this.cComandos;
        Identifier v = this.getIdentifier(this.variaveis, var);
        this.setCodigo(IRECEBE);
        this.setCodigo(this.dado8bits(v.getPos()));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cRecebeu(String var) {
        ++this.cComandos;
        Identifier v = this.getIdentifier(this.variaveis, var);
        this.setCodigo(IRECEBEU);
        this.setCodigo(this.dado8bits(v.getPos()));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 0);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cComandoVelocidadeMotores(int velocidade1, int velocidade2) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade1, velocidade2);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 0);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 0);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 0);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cFrenteObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 0);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(-velocidade, -velocidade);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 3);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(-velocidade, -velocidade);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 3);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, -velocidade);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 3);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, -velocidade);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 3);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, -velocidade);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cReObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 3);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, -velocidade);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 1);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, -velocidade);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 1);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, -velocidade);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 1);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, -velocidade);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 1);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, -velocidade);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroDireitaObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 1);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(-velocidade, velocidade);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 2);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(-velocidade, velocidade);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 2);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, velocidade);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 2);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, velocidade);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 2);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(-velocidade, velocidade);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cGiroEsquerdaObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 2);
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 8);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMaior, vMenor);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 5);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMaior, vMenor);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 5);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMaior, vMenor);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 5);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMaior, vMenor);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 5);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMaior, vMenor);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaDireitaObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 5);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaSegundo(double tempo, int velocidade) {
        ++this.cComandos;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMenor, vMaior);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
            --this.cComandos;
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaSegundo(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 4);
        int tempoInteiro = (int)Math.floor(tempo);
        int resto = (int)Math.round((tempo - (double)tempoInteiro) * 10.0);
        if (resto != 0) {
            this.cSomaDec(resto);
            this.cEsperar(tempoInteiro);
            this.cPare();
            --this.cComandos;
        } else {
            this.cEsperar(tempo);
            this.cPare();
        }
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaMinuto(double tempo, int velocidade) {
        ++this.cComandos;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMenor, vMaior);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaMinuto(double tempo, String velocidade) {
        ++this.cComandos;
        this.cVelocidadeMotores(velocidade, velocidade, 4);
        this.cEsperarMinuto(tempo);
        --this.cComandos;
        this.cPare();
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaCor(int cor, int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMenor, vMaior);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaCor(int cor, String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 4);
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaCor(cor, enderecoRetorno, 4);
        this.cPare();
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaFaixa(int velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMenor, vMaior);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaFaixa(String velocidade, int faixa) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 4);
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(1.0);
        --this.cComandos;
        this.esperaFaixas(faixa, enderecoRetorno, 2, 6);
        this.cPare();
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaObstaculo(int velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int vMenor = this.getVelocidadeMenor(velocidade);
        int vMaior = this.getVelocidadeMaior(velocidade);
        this.cVelocidadeMotores(vMenor, vMaior);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        --this.cComandos;
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cCurvaEsquerdaObstaculo(String velocidade) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.cVelocidadeMotores(velocidade, velocidade, 4);
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cDesligarMotores();
        --this.cComandos;
        this.cEsperarSegundo(0.3);
        --this.cComandos;
        this.esperaObstaculo(enderecoRetorno, 1);
        this.cPare();
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public int getVelocidadeMaior(int v) {
        if (v <= 20) {
            return 30;
        }
        if (v <= 70) {
            return 50;
        }
        return 100;
    }

    public int getVelocidadeMenor(int v) {
        if (v <= 20) {
            return 10;
        }
        if (v <= 70) {
            return 20;
        }
        return 50;
    }

    private void cVelocidadeMotores(int velocidadeM1, int velocidadeM2) {
        velocidadeM1 = this.converterVelocidadeMotores(velocidadeM1);
        velocidadeM2 = this.converterVelocidadeMotores(velocidadeM2);
        this.setCodigo(IVELMOTORES);
        this.setCodigo(this.dado8bits(velocidadeM1));
        this.setCodigo(this.dado8bits(velocidadeM2));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    private void cVelocidadeMotores(String velocidadeM1, String velocidadeM2, int direcao) {
        this.setCodigo(IVELMOTORESINDIRETO);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, velocidadeM1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, velocidadeM2).getPos()));
        this.setCodigo(this.dado8bits(direcao));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cVelocidadeMotor(int motor, int direcao, int velocidade) {
        String comando = "";
        if (motor == 1) {
            if (direcao == 1) {
                comando = IVELM1FRENTE;
            } else if (direcao == -1) {
                comando = IVELM1RE;
            }
        } else if (direcao == 1) {
            comando = IVELM2FRENTE;
        } else if (direcao == -1) {
            comando = IVELM2RE;
        }
        this.setCodigo(comando);
        this.setCodigo(this.dado8bits(velocidade));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cVelocidadeMotor(int motor, int direcao, String velocidade) {
        String comando = "";
        Identifier v1 = this.getIdentifier(this.variaveis, velocidade);
        if (motor == 1) {
            if (direcao == 1) {
                comando = IVELM1FRENTEINDIRETO;
            } else if (direcao == -1) {
                comando = IVELM1REINDIRETO;
            }
        } else if (direcao == 1) {
            comando = IVELM2FRENTEINDIRETO;
        } else if (direcao == -1) {
            comando = IVELM2REINDIRETO;
        }
        this.setCodigo(comando);
        this.setCodigo(this.dado8bits(v1.getPos()));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    private int converterVelocidadeMotores(int v) {
        if (v < -100) {
            v = -100;
        }
        if (v > 100) {
            v = 100;
        }
        if (v >= 0) {
            return v;
        }
        return -v + 128;
    }

    public void cAdd(String var1, String var2, int valor16bits) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        this.iAddvw(v1.getPos(), v2.getPos(), valor16bits);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cAdd(String var1, String var2, String var3) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier v3 = this.getIdentifier(this.variaveis, var3);
        this.iAddm(v1.getPos(), v2.getPos(), v3.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSub(String var1, String var2, int valor16bits) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        this.iSubvw(v1.getPos(), v2.getPos(), valor16bits);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSub(String var1, int valor16bits, String var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier nulo = this.getIdentifier(this.variaveis, "nulo");
        this.iAddvw(nulo.getPos(), nulo.getPos(), valor16bits);
        this.iSubm(v1.getPos(), nulo.getPos(), v2.getPos());
        this.iSubm(nulo.getPos(), nulo.getPos(), nulo.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cSub(String var1, String var2, String var3) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier v3 = this.getIdentifier(this.variaveis, var3);
        this.iSubm(v1.getPos(), v2.getPos(), v3.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDiv(String var1, String var2, int valor16bits) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        this.iDivvw(v1.getPos(), v2.getPos(), valor16bits);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDiv(String var1, int valor16bits, String var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier nulo = this.getIdentifier(this.variaveis, "nulo");
        this.iAddvw(nulo.getPos(), nulo.getPos(), valor16bits);
        this.iDivm(v1.getPos(), nulo.getPos(), v2.getPos());
        this.iSubm(nulo.getPos(), nulo.getPos(), nulo.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cDiv(String var1, String var2, String var3) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier v3 = this.getIdentifier(this.variaveis, var3);
        this.iDivm(v1.getPos(), v2.getPos(), v3.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cMult(String var1, String var2, int valor16bits) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        this.iMultvw(v1.getPos(), v2.getPos(), valor16bits);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cMult(String var1, String var2, String var3) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        Identifier v3 = this.getIdentifier(this.variaveis, var3);
        this.iMultm(v1.getPos(), v2.getPos(), v3.getPos());
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public int cIniciaEnquanto(String op, String var1, int var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iCmpvw(v1.getPos(), var2);
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<>")) {
            this.iJump(this.cE2prom + 4);
            this.iJe(enderecoFim);
        }
        if (op.contentEquals(">=")) {
            this.iJump(this.cE2prom + 4);
            this.iJl(enderecoFim);
        }
        if (op.contentEquals("<=")) {
            this.iJump(this.cE2prom + 4);
            this.iJg(enderecoFim);
        }
        return enderecoRetorno;
    }

    public int cIniciaEnquantoEntre(String var1, int i1, int i2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iIns(v1.getPos(), i1, i2);
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoFim);
        return enderecoRetorno;
    }

    public int cIniciaEnquantoE(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.setCodigo(ICMP_AND);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoFim);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
        return enderecoRetorno;
    }

    public void cTerminaEnquantoE(int enderecoRetorno, int enderecoInicio) {
        this.iJump(enderecoRetorno);
        this.codigoObj.set(enderecoInicio + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoInicio + 14, this.dado16bitsL(this.cE2prom + 1));
    }

    public int cIniciaEnquantoOU(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.setCodigo(ICMP_OR);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoFim);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
        return enderecoRetorno;
    }

    public void cTerminaEnquantoOU(int enderecoRetorno, int enderecoInicio) {
        this.iJump(enderecoRetorno);
        this.codigoObj.set(enderecoInicio + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoInicio + 14, this.dado16bitsL(this.cE2prom + 1));
    }

    public int cIniciaEnquanto(String op, String var1, String var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iCmps(v1.getPos(), v2.getPos());
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<>")) {
            this.iJump(this.cE2prom + 4);
            this.iJe(enderecoFim);
        }
        if (op.contentEquals(">=")) {
            this.iJump(this.cE2prom + 4);
            this.iJl(enderecoFim);
        }
        if (op.contentEquals("<=")) {
            this.iJump(this.cE2prom + 4);
            this.iJg(enderecoFim);
        }
        return enderecoRetorno;
    }

    public int cIniciaEnquantoCor(String op, String var1, int cor) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        if (op.contentEquals("=")) {
            this.esperaCor(cor, enderecoFim, v1.getPos());
        }
        if (op.contentEquals("<>")) {
            this.esperaCor(cor, enderecoRetorno + 15, v1.getPos());
            this.iJump(enderecoFim);
        }
        return enderecoRetorno;
    }

    public void cTerminaEnquantoEntre(int enderecoRetorno) {
        this.iJump(enderecoRetorno);
        this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaEnquantoV1V2(int enderecoRetorno) {
        this.iJump(enderecoRetorno);
        this.codigoObj.set(enderecoRetorno + 7, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaEnquantoV1N2(int enderecoRetorno) {
        this.iJump(enderecoRetorno);
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 9, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaEnquantoCor(int enderecoRetorno, String op) {
        this.iJump(enderecoRetorno);
        if (op.contentEquals("=")) {
            this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
        }
        if (op.contentEquals("<>")) {
            this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
        }
    }

    public void cSomaDec(int decimos) {
        this.setCodigo(ISOMADEC);
        this.setCodigo(this.dado8bits(decimos));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public int cIniciaSeEntaoEntre(String var1, int i1, int i2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iIns(v1.getPos(), i1, i2);
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoFim);
        return enderecoRetorno;
    }

    public int cIniciaSeEntaoE(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.setCodigo(ICMP_AND);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoFim);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
        return enderecoRetorno;
    }

    public int cIniciaSenaoE(int enderecoRetorno) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
        return enderecoRetornoElse;
    }

    public void cTerminaSenaoE(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 2, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 3, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSeEntaoE(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
    }

    public int cIniciaSeEntaoOU(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.setCodigo(ICMP_OR);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoFim);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
        return enderecoRetorno;
    }

    public int cIniciaSenaoOU(int enderecoRetorno) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
        return enderecoRetornoElse;
    }

    public void cTerminaSenaoOU(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 2, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 3, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSeEntaoOU(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
    }

    public int cIniciaSeEntao(String op, String var1, int var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iCmpvw(v1.getPos(), var2);
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<>")) {
            this.iJump(this.cE2prom + 4);
            this.iJe(enderecoFim);
        }
        if (op.contentEquals(">=")) {
            this.iJump(this.cE2prom + 4);
            this.iJl(enderecoFim);
        }
        if (op.contentEquals("<=")) {
            this.iJump(this.cE2prom + 4);
            this.iJg(enderecoFim);
        }
        return enderecoRetorno;
    }

    public int cIniciaSeEntao(String op, String var1, String var2) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        Identifier v2 = this.getIdentifier(this.variaveis, var2);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        this.iCmps(v1.getPos(), v2.getPos());
        if (op.contentEquals("=")) {
            this.iJe(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals(">")) {
            this.iJg(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<")) {
            this.iJl(this.cE2prom + 7);
            this.iJump(enderecoFim);
        }
        if (op.contentEquals("<>")) {
            this.iJump(this.cE2prom + 4);
            this.iJe(enderecoFim);
        }
        if (op.contentEquals(">=")) {
            this.iJump(this.cE2prom + 4);
            this.iJl(enderecoFim);
        }
        if (op.contentEquals("<=")) {
            this.iJump(this.cE2prom + 4);
            this.iJg(enderecoFim);
        }
        return enderecoRetorno;
    }

    public int cIniciaSeEntaoCor(String op, String var1, int cor) {
        ++this.cComandos;
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        int enderecoRetorno = this.cE2prom + 1;
        int enderecoFim = 0;
        if (op.contentEquals("=")) {
            this.esperaCor(cor, enderecoFim, v1.getPos());
        }
        if (op.contentEquals("<>")) {
            this.esperaCor(cor, enderecoRetorno + 15, v1.getPos());
            this.iJump(enderecoFim);
        }
        return enderecoRetorno;
    }

    public void cTerminaSeEntaoV1V2(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 7, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSeEntaoEntre(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSeEntaoV1N2(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 9, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSeEntaoCor(int enderecoRetorno, String op) {
        if (op.contentEquals("=")) {
            this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
        }
        if (op.contentEquals("<>")) {
            this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
        }
    }

    public int cIniciaSenaoEntre(int enderecoRetorno) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
        return enderecoRetornoElse;
    }

    public int cIniciaSenaoV1V2(int enderecoRetorno) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        this.codigoObj.set(enderecoRetorno + 7, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsL(this.cE2prom + 1));
        return enderecoRetornoElse;
    }

    public int cIniciaSenaoV1N2(int enderecoRetorno) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        this.codigoObj.set(enderecoRetorno + 8, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 9, this.dado16bitsL(this.cE2prom + 1));
        return enderecoRetornoElse;
    }

    public int cIniciaSenaoCor(int enderecoRetorno, String op) {
        int enderecoRetornoElse = this.cE2prom;
        int enderecoFim = 0;
        this.iJump(enderecoFim);
        if (op.contentEquals("=")) {
            this.codigoObj.set(enderecoRetorno + 10, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 11, this.dado16bitsL(this.cE2prom + 1));
        }
        if (op.contentEquals("<>")) {
            this.codigoObj.set(enderecoRetorno + 13, this.dado16bitsH(this.cE2prom + 1));
            this.codigoObj.set(enderecoRetorno + 14, this.dado16bitsL(this.cE2prom + 1));
        }
        return enderecoRetornoElse;
    }

    public void cTerminaSenao(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 2, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 3, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSenaoEntre(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 2, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 3, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cTerminaSenaoCor(int enderecoRetorno) {
        this.codigoObj.set(enderecoRetorno + 2, this.dado16bitsH(this.cE2prom + 1));
        this.codigoObj.set(enderecoRetorno + 3, this.dado16bitsL(this.cE2prom + 1));
    }

    public void cAleatorio(String var1, int de, int ate) {
        Identifier v1 = this.getIdentifier(this.variaveis, var1);
        ++this.cComandos;
        this.setCodigo(IALEATORIO);
        this.setCodigo(this.dado8bits(v1.getPos()));
        this.setCodigo(this.dado8bits(de));
        this.setCodigo(this.dado8bits(ate));
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void cVariavel(String nome) {
        ++this.cRam;
        Identifier ident = new Identifier(nome, 0, this.cRam);
        this.variaveis.add(ident);
    }

    public Identifier getIdentifier(Vector<Identifier> v, String s) {
        int i = 0;
        while (i < v.size()) {
            if (v.elementAt(i).getName().equalsIgnoreCase(s)) {
                return v.elementAt(i);
            }
            ++i;
        }
        return null;
    }

    @Override
    public void identifierDeclared(DeclarationEvent de) {
        if (de.getType() == 1) {
            int pos = this.cE2prom + 1;
            Identifier ident = new Identifier(de.getName(), de.getType(), pos);
            this.procedimentos.add(ident);
        } else if (de.getType() == 0) {
            ++this.cRam;
            int pos = this.cRam;
            Identifier ident = new Identifier(de.getName(), de.getType(), pos);
            this.variaveis.add(ident);
        }
    }

    public int getLimit() {
        return this.limite;
    }

    public boolean setSensorRequerido(int posicao, int ID) {
        if (this.codigoObj.get(posicao).equals("D7F")) {
            this.codigoObj.set(posicao, this.dado8bits(ID + 127));
            return true;
        }
        if (this.codigoObj.get(posicao).equals(this.dado8bits(ID + 127))) {
            return true;
        }
        if (ID == -4 && Montador.getSensorID(this.codigoObj.get(posicao)) < 0) {
            return true;
        }
        if (ID != -4 && ID < 0 && Montador.getSensorID(this.codigoObj.get(posicao)) == -4) {
            this.codigoObj.set(posicao, this.dado8bits(ID + 127));
            return true;
        }
        return false;
    }

    public static int getSensorID(String s) {
        return Integer.parseInt(s.substring(1), 16) - 127;
    }

    public static String getSensorRequerido(int ID) {
        return Montador.sDado8bits(ID + 127);
    }

    public int getSensoresColetor() {
        return this.sensoresColetor;
    }

    public static String sDado8bits(int x) {
        String aux = Integer.toHexString(x).toUpperCase();
        if (aux.length() < 2) {
            return "D0" + aux;
        }
        return "D" + aux;
    }

    private void esperaFaixas(int faixa, int enderecoSalto, int porta1, int porta2) {
        switch (faixa) {
            case 908: {
                this.setCodigo(ICMPV);
                this.setCodigo(this.dado8bits(porta1));
                this.setCodigo(this.dado8bits(0));
                break;
            }
            case 788: {
                this.setCodigo(ICMPV);
                this.setCodigo(this.dado8bits(porta1));
                this.setCodigo(this.dado8bits(1));
            }
        }
        this.iJe(this.cE2prom + 13);
        switch (faixa) {
            case 908: {
                this.setCodigo(ICMPV);
                this.setCodigo(this.dado8bits(porta2));
                this.setCodigo(this.dado8bits(0));
                break;
            }
            case 788: {
                this.setCodigo(ICMPV);
                this.setCodigo(this.dado8bits(porta2));
                this.setCodigo(this.dado8bits(1));
            }
        }
        this.iJe(this.cE2prom + 7);
        this.iJump(enderecoSalto);
    }

    public void naoMonte() {
        this.codigoObjFixar = (ArrayList)this.codigoObj.clone();
        this.codigoEnvioFixar = (ArrayList)this.codigoEnvio.clone();
        this.cComandosFixar = this.cComandos;
        this.iInstrucoesFixar = this.iInstrucoes;
        this.cE2promFixar = this.cE2prom;
        this.cRamFixar = this.cRam;
    }

    public void monte() {
        this.codigoObj = (ArrayList)this.codigoObjFixar.clone();
        this.codigoEnvio = (ArrayList)this.codigoEnvioFixar.clone();
        this.cComandos = this.cComandosFixar;
        this.iInstrucoes = this.iInstrucoesFixar;
        this.cE2prom = this.cE2promFixar;
        this.cRam = this.cRamFixar;
        this.codigoObjFixar = null;
        this.codigoEnvioFixar = null;
        this.cComandosFixar = -1;
        this.iInstrucoesFixar = -1;
        this.cE2promFixar = -1;
        this.cRamFixar = -1;
    }

    public void espereE(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.setCodigo(ICMP_AND);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoRetorno);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public void espereOU(String x1, String x2, String x3, String op1, String y1, String y2, String y3, String op2) {
        ++this.cComandos;
        int enderecoRetorno = this.cE2prom + 1;
        this.setCodigo(ICMP_OR);
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, x3).getPos()));
        this.setCodigo(this.dado8bits(op1.charAt(0)));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y1).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y2).getPos()));
        this.setCodigo(this.dado8bits(this.getIdentifier(this.variaveis, y3).getPos()));
        this.setCodigo(this.dado8bits(op2.charAt(0)));
        this.iJg(enderecoRetorno + 15);
        this.iJump(enderecoRetorno);
        this.errorCode = this.cE2prom >= this.limite ? 56 : 0;
    }

    public int getCorIntervaloInicio(int cor) {
        return this.corInicio.get(cor);
    }

    private String printCodObjIndices() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < this.codigoObj.size()) {
            String aux = Integer.toHexString(i).toUpperCase();
            if (aux.length() < 2) {
                aux = "0" + aux;
            }
            result.append("[");
            result.append(aux);
            result.append(",");
            result.append(this.codigoObj.get(i));
            result.append("]");
            result.append(" ");
            ++i;
        }
        return result.toString();
    }

    public static int[] getEventos() {
        return eventos;
    }
}
