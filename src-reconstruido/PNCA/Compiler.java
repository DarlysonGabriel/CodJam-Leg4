/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

import Montador.DeclarationEvent;
import Montador.DeclarationListener;
import Montador.Montador;
import PNCA.CommandSensorEvent;
import PNCA.CommandSensorListener;
import PNCA.ExpressionType;
import PNCA.IncludeFile;
import PNCA.TextScanner;
import PNCA.Token;
import PNCA.TokenList;
import Utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class Compiler {
    public static final int COMPILERLANGESP = 1;
    public static final int COMPILERLANGBR = 2;
    public static final int COMPILERLANGENG = 3;
    private int language;
    private Vector<String> declaredVars;
    private HashMap<String, String> declaredDefs;
    private HashMap<String, Integer> declaredProcs;
    private Hashtable<Integer, Token> tokenList;
    private Vector<String> secondaryTokenList;
    boolean isMainCode;
    private Vector<Token> measureUnitTokenList;
    private Vector<Token> multiplierTokenList;
    private Vector<Token> outTokenList;
    private Vector<Token> sensorTokenList;
    private Vector<Token> sensorDistanceTokenList;
    private Vector<Token> ledTokenList;
    private Vector<Token> eventTokenList;
    private Vector<Token> servoTokenList;
    private Vector<Token> unitTokenList;
    private Vector<Token> noteTokenList;
    private Vector<Token> soundEffectTokenList;
    private Vector<Token> colorTokenList;
    private Vector<Token> stripTokenList;
    private int erroPorta;
    private CommandSensorEvent erroConflito = null;
    private ArrayList<CommandSensorEvent> sensorEvents;
    private Montador montador;
    private TextScanner textScanner;
    private int errorCode;
    private int errorLine;
    private int currentCommandLine;
    private int editorID;
    private String errorRestToken;
    private Stack<Integer> blockStack = new Stack();
    private int openBlocks;
    private static final String legalLetters = new String("\u00e0\u00e8\u00ec\u00f2\u00f9\u00c0\u00c8\u00cc\u00d2\u00d9\u00e1\u00e9\u00ed\u00f3\u00fa\u00fd\u00c1\u00c9\u00cd\u00d3\u00da\u00dd\u00e2\u00ea\u00ee\u00f4\u00fb\u00c2\u00ca\u00ce\u00d4\u00db\u00e3\u00f1\u00f5\u00c3\u00d1\u00d5\u00e4\u00eb\u00ef\u00f6\u00fc\u00ff\u00c4\u00cb\u00cf\u00d6\u00dc\u00e7\u00c7\u00df\u00d8\u00f8\u00c5\u00e5\u00c6_");
    private static final String legalDigits = new String(">=<+-/*[]");
    private static final String whitechars = new String(" \t\u0000");
    private String floatPointSeparator = ",";
    private static final int TIPODURACAOSEMPRE = 255;
    private Vector<DeclarationListener> declarationListeners = new Vector();
    private Vector<CommandSensorListener> commandSensorListeners = new Vector();
    private boolean endLoop;

    public Compiler(int lang) {
        if (lang != 2 && lang != 1 && lang != 3) {
            System.err.println("Idioma inv\u00e1lido");
            System.exit(1);
        }
        this.language = lang;
        switch (this.language) {
            case 1: 
            case 2: {
                this.floatPointSeparator = new String(",");
                break;
            }
            case 3: {
                this.floatPointSeparator = new String(".");
            }
        }
        switch (this.language) {
            case 2: {
                this.createTokenList(TokenList.TOKENBR);
                break;
            }
            case 3: {
                this.createTokenList(TokenList.TOKENENG);
                break;
            }
            case 1: {
                this.createTokenList(TokenList.TOKENESP);
            }
        }
        this.sensorEvents = new ArrayList();
        this.declaredVars = new Vector(5, 5);
        this.declaredDefs = new HashMap();
        this.declaredProcs = new HashMap();
        this.declaredVars.add("BAT");
        this.declaredVars.add("VEZ");
        this.declaredVars.add("BTENTER");
    }

    public void startCompiler(String s, boolean mainCode, int editorID) {
        this.startCompiler(new StringBuilder(s), mainCode, editorID);
    }

    public void startCompiler(StringBuilder sb, boolean mainCode, int editorID) {
        this.endLoop = false;
        this.editorID = editorID;
        this.isMainCode = mainCode;
        this.currentCommandLine = -1;
        this.openBlocks = 0;
        try {
            this.textScanner = new TextScanner(sb);
        }
        catch (IOException e) {
            System.err.println("Erro criando o leitor de texto.");
            System.exit(1);
        }
    }

    public void iniciaMontador(boolean temSub) {
        this.montador = new Montador(temSub);
        this.addDeclarationListener(this.montador);
    }

    public void waitDefinitionDeclaration(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(4)) {
            this.setErrorCode(14);
        } else if (stoken.contains(17)) {
            this.treatVarDeclaration(word, stoken);
        } else if (stoken.contains(56)) {
            this.treatDefinitionDeclaration(word, stoken);
        } else {
            this.setErrorCode(37);
        }
        if (this.endLoop && this.errorCode == 37) {
            this.errorCode = 0;
        }
    }

    public void waitSubDeclaration(StringBuilder word, ArrayList<Integer> stoken, HashSet<String> usedSubs) throws IOException {
        this.currentCommandLine = this.textScanner.getCurrentLineNumber();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(4)) {
            this.currentCommandLine = this.textScanner.getCurrentLineNumber();
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(102)) {
                this.setErrorCode(35);
                return;
            }
            if (!usedSubs.contains(word.toString().toUpperCase())) {
                this.declaredProcs.put(word.toString(), 1);
                this.montador.naoMonte();
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cRetornaSubRotina();
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.montador.monte();
            } else {
                this.generateDeclarationEvent(word.toString(), 1);
                this.declaredProcs.put(word.toString(), 1);
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cRetornaSubRotina();
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            }
        } else if (stoken.contains(17)) {
            this.setErrorCode(44);
        } else if (stoken.contains(61)) {
            this.textScanner.readNextLine();
        } else {
            this.setErrorCode(77);
        }
        if (this.endLoop && this.errorCode == 77) {
            this.errorCode = 0;
        }
    }

    public void treatVarDeclaration(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.currentCommandLine = this.textScanner.getCurrentLineNumber();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(102)) {
            if (stoken.contains(106)) {
                this.setErrorCode(55);
                return;
            }
            if (stoken.contains(133)) {
                this.setErrorCode(110);
                return;
            }
            if (stoken.contains(101)) {
                this.setErrorCode(40);
                return;
            }
            this.setErrorCode(35);
            return;
        }
        this.generateDeclarationEvent(word.toString(), 0);
        this.declaredVars.add(word.toString());
    }

    public void treatDefinitionDeclaration(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.currentCommandLine = this.textScanner.getCurrentLineNumber();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(102)) {
            if (stoken.contains(106)) {
                this.setErrorCode(55);
                return;
            }
            if (stoken.contains(133)) {
                this.setErrorCode(110);
                return;
            }
            if (stoken.contains(101)) {
                this.setErrorCode(40);
                return;
            }
            this.setErrorCode(35);
            return;
        }
        String definition = word.toString();
        String auxDef = "";
        if (this.textScanner.eol()) {
            this.setErrorCode(145);
            return;
        }
        if ((auxDef = String.valueOf(auxDef) + this.textScanner.readToEndOfCurrentLine()).contains("#")) {
            auxDef = auxDef.substring(0, auxDef.indexOf("#"));
        }
        if (this.errorCode != 0) {
            return;
        }
        this.generateDeclarationEvent(auxDef, 1);
        this.declaredDefs.put(definition.toUpperCase(), auxDef);
    }

    public void treatProc(StringBuilder word, ArrayList<Integer> stoken) {
        this.montador.cGoSub(word.toString());
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void waitCommand(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        this.currentCommandLine = this.textScanner.getCurrentLineNumber();
        switch (this.getValueFromStoken(stoken)) {
            case 61: {
                this.textScanner.readNextLine();
                break;
            }
            case 23: {
                this.instRepita(word, stoken);
                break;
            }
            case 11: {
                this.comportSeguir(word, stoken);
                break;
            }
            case 29: {
                this.comportMotor(word, stoken);
                break;
            }
            case 128: {
                this.comportServo(word, stoken);
                break;
            }
            case 10: {
                this.comportFugir(word, stoken);
                break;
            }
            case 24: {
                this.comEspere(word, stoken);
                break;
            }
            case 13: {
                this.comSom(word, stoken);
                break;
            }
            case 62: {
                this.comNota(word, stoken);
                break;
            }
            case 60: {
                this.comMudo(word, stoken);
                break;
            }
            case 8: {
                this.comRe(word, stoken);
                break;
            }
            case 7: {
                this.comFrente(word, stoken);
                break;
            }
            case 5: {
                this.setErrorCode(90);
                break;
            }
            case 6: {
                this.setErrorCode(90);
                break;
            }
            case 47: {
                this.comGiro(word, stoken);
                break;
            }
            case 18: {
                this.comPare();
                break;
            }
            case 43: {
                this.comColetar();
                break;
            }
            case 44: {
                this.comFimColeta();
                break;
            }
            case 45: {
                this.comAtivar(word, stoken);
                break;
            }
            case 46: {
                this.comIntervalo(word, stoken);
                break;
            }
            case 107: {
                this.setErrorCode(60);
                break;
            }
            case 31: {
                this.comHabilite(word, stoken);
                break;
            }
            case 19: {
                this.comLigue(word, stoken);
                break;
            }
            case 20: {
                this.comDesligue(word, stoken);
                break;
            }
            case 49: {
                this.comAcenda(word, stoken);
                break;
            }
            case 50: {
                this.comApague(word, stoken);
                break;
            }
            case 32: {
                this.comDesabilite(word, stoken);
                break;
            }
            case 25: {
                this.comSe(word, stoken);
                break;
            }
            case 28: {
                this.comEnquanto(word, stoken);
                break;
            }
            case 101: {
                this.treatProc(word, stoken);
                break;
            }
            case 27: {
                this.setErrorCode(11);
                break;
            }
            case 15: {
                this.setErrorCode(50);
                break;
            }
            case 120: {
                this.setErrorCode(43);
                break;
            }
            case 121: {
                if (this.openBlocks > 0) {
                    --this.openBlocks;
                    break;
                }
                this.setErrorCode(13);
                break;
            }
            case 17: {
                this.setErrorCode(44);
                break;
            }
            case 9: {
                this.comCurva(word, stoken);
                break;
            }
            case 2: 
            case 3: {
                if (this.isMainCode) {
                    this.endLoop = true;
                    break;
                }
                this.setErrorCode(58);
                break;
            }
            case 4: {
                this.setErrorCode(14);
                break;
            }
            case 102: {
                this.setErrorCode(15);
                break;
            }
            case 1: {
                this.setErrorCode(61);
                break;
            }
            case 12: {
                this.setErrorCode(62);
                break;
            }
            case 51: {
                this.setErrorCode(90);
                break;
            }
            case 52: {
                this.setErrorCode(90);
                break;
            }
            case 14: {
                this.setErrorCode(63);
                break;
            }
            case 26: {
                this.setErrorCode(65);
                break;
            }
            case 30: {
                this.setErrorCode(66);
                break;
            }
            case 103: {
                this.setErrorCode(15);
                break;
            }
            case 104: {
                this.setErrorCode(68);
                break;
            }
            case 129: {
                this.setErrorCode(49);
                break;
            }
            case 108: {
                this.setErrorCode(69);
                break;
            }
            case 109: {
                this.setErrorCode(70);
                break;
            }
            case 122: {
                this.setErrorCode(72);
                break;
            }
            case 16: {
                this.setErrorCode(64);
                break;
            }
            case 100: {
                this.setErrorCode(67);
                break;
            }
            case 125: {
                this.setErrorCode(38);
                break;
            }
            case 123: {
                this.setErrorCode(73);
                break;
            }
            case 53: {
                this.comEnvie(word, stoken);
                break;
            }
            case 54: {
                this.comReceba(word, stoken);
                break;
            }
            case 55: {
                this.comRecebeu(word, stoken);
                break;
            }
            case 110: 
            case 111: 
            case 112: 
            case 113: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                this.setErrorCode(71);
                break;
            }
            case 106: {
                this.comAtribuicao(word, stoken);
                break;
            }
            default: {
                this.setErrorCode(10);
            }
        }
        if (this.endLoop && this.errorCode == 10) {
            this.errorCode = 0;
        }
    }

    public void instRepita(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(108) && !stoken.contains(16)) {
            this.setErrorCode(34);
            return;
        }
        if (stoken.contains(16)) {
            this.montador.cRepita(255);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            this.commandBlock(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cAteh();
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            return;
        }
        if (stoken.contains(108)) {
            int num = Integer.valueOf(word.toString());
            if (num <= 0 || num > 200) {
                this.setErrorCode(33);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(30)) {
                this.setErrorCode(12);
                return;
            }
            this.montador.cRepita(num);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            this.commandBlock(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cAteh();
        }
    }

    public void comportFugir(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(12)) {
            this.setErrorCode(26);
            return;
        }
        this.comportFugirLuz(word, stoken);
    }

    public void comportFugirLuz(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(3, 3);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(3, 7);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempo(word, stoken, duracao, type);
        int stTempo = this.getValueFromStoken(stoken);
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (this.isSeg(type.toString())) {
                this.montador.cFugirLuzSegundo(duracao[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cFugirLuzMinuto(duracao[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            this.montador.cFugirLuzMinuto(255.0);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            Token cor = this.getToken(this.colorTokenList, type.toString());
            this.montador.cFugirLuzCor(cor.getNumber());
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 1);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cFugirLuzObstaculo();
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cFugirLuzFaixa((int)Math.round(duracao[0]));
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comportMotor(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] motor = new int[1];
        int[] velocidade = new int[1];
        String[] velocidadeVar = new String[1];
        int[] sentido = new int[1];
        this.esperaMotor(word, stoken, motor, sentido, velocidade, velocidadeVar);
        this.generateCommandSensorEvent(128, 13 + motor[0] - 1);
        if (this.errorCode != 0) {
            return;
        }
        if (velocidadeVar[0] == null) {
            if (velocidade[0] == -1) {
                velocidade[0] = 70;
            }
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cVelocidadeMotor(motor[0], sentido[0], velocidade[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else {
            this.montador.cVelocidadeMotor(motor[0], sentido[0], velocidadeVar[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    private void getAllServos(ArrayList<ServoAngulo> servos, StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] servo = new int[1];
        int[] angulo = new int[1];
        String[] var = new String[1];
        Token tServo = this.getToken(this.servoTokenList, word.toString());
        servo[0] = tServo.getNumber();
        this.esperaAngulo(word, stoken, angulo, var);
        if (this.errorCode != 0) {
            return;
        }
        servos.add(new ServoAngulo(servo[0], angulo[0], var[0]));
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        while (auxST.contains(58)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(128)) {
                this.setErrorCode(118);
                return;
            }
            tServo = this.getToken(this.servoTokenList, word.toString());
            servo[0] = tServo.getNumber();
            this.esperaAngulo(word, stoken, angulo, var);
            if (this.errorCode != 0) {
                return;
            }
            servos.add(new ServoAngulo(servo[0], angulo[0], var[0]));
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
        }
    }

    private void comportServo(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        ArrayList<ServoAngulo> servos = new ArrayList<ServoAngulo>();
        this.getAllServos(servos, word, stoken);
        for (ServoAngulo servoAngulo : servos) {
            if (servoAngulo.getAngulo() >= 0) {
                this.montador.cServo(servoAngulo.getServo(), servoAngulo.getAngulo());
            } else {
                this.montador.cServo(servoAngulo.getServo(), servoAngulo.getVar());
            }
            this.generateCommandSensorEvent(100, servoAngulo.getServo() + 8);
            if (this.errorCode == 0) continue;
            return;
        }
        this.montador.cEsperarSegundo(0.7);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comportSeguir(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        if (this.language == 3) {
            String lineColor = word.toString();
            if (stoken.contains(12)) {
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (!auxST.contains(14)) {
                    this.comportSeguirLuz(word, stoken);
                    return;
                }
                this.lexico(word, stoken, this.textScanner);
                this.comportSeguirFaixa(word, stoken, lineColor);
                return;
            }
            if (this.isLineColor(stoken)) {
                this.lexico(word, stoken, this.textScanner);
                this.comportSeguirFaixa(word, stoken, lineColor);
                return;
            }
            this.setErrorCode(23);
            return;
        }
        if (!stoken.contains(12) && !stoken.contains(14)) {
            this.setErrorCode(23);
            return;
        }
        if (stoken.contains(14)) {
            this.comportSeguirFaixa(word, stoken, "");
            return;
        }
        if (stoken.contains(12)) {
            this.comportSeguirLuz(word, stoken);
            return;
        }
    }

    public void comportSeguirLuz(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(3, 3);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(3, 7);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempo(word, stoken, duracao, type);
        int stTempo = this.getValueFromStoken(stoken);
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (this.isSeg(type.toString())) {
                this.montador.cSeguirLuzSegundo(duracao[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cSeguirLuzMinuto(duracao[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            this.montador.cSeguirLuzMinuto(255.0);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            Token cor = this.getToken(this.colorTokenList, type.toString());
            this.montador.cSeguirLuzCor(cor.getNumber());
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 1);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cSeguirLuzObstaculo();
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cSeguirLuzFaixa((int)Math.round(duracao[0]));
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comportSeguirFaixa(StringBuilder word, ArrayList<Integer> stoken, String corFaixa) throws IOException {
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        boolean[] usaVelocidade = new boolean[1];
        String velVar = null;
        ArrayList<Integer> colorToken = new ArrayList<Integer>();
        this.generateCommandSensorEvent(-6, 2);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(-6, 6);
        if (this.errorCode != 0) {
            return;
        }
        if (corFaixa.length() <= 0) {
            this.lexico(word, stoken, this.textScanner);
            corFaixa = word.toString();
            if (this.errorCode != 0) {
                return;
            }
            colorToken = stoken;
        } else {
            colorToken.add(125);
        }
        if (this.isLineColor(colorToken)) {
            double[] duracao = new double[1];
            StringBuilder type = new StringBuilder();
            Token qualFaixa = this.getToken(this.stripTokenList, corFaixa);
            this.esperaTempo(word, stoken, duracao, type);
            int stTempo = this.getValueFromStoken(stoken);
            if (this.errorCode != 0) {
                return;
            }
            int vel = 50;
            auxTS = new TextScanner(this.textScanner);
            this.esperaVelocidade(auxSB, auxST, auxTS, usaVelocidade);
            if (this.errorCode != 0) {
                return;
            }
            if (usaVelocidade[0]) {
                this.esperaVelocidade(auxSB, auxST, this.textScanner, usaVelocidade);
                switch (this.getValueFromStoken(auxST)) {
                    case 37: 
                    case 38: {
                        vel = 30;
                        break;
                    }
                    case 39: {
                        vel = 50;
                        break;
                    }
                    case 40: 
                    case 41: {
                        vel = 70;
                    }
                }
            } else {
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(108)) {
                    this.lexico(auxSB, auxST, this.textScanner);
                    vel = Integer.valueOf(auxSB.toString());
                    if (vel < 0 || vel > 100) {
                        this.setErrorCode(80);
                        return;
                    }
                    this.lexico(word, stoken, this.textScanner);
                    if (!stoken.contains(132)) {
                        this.setErrorCode(126);
                        return;
                    }
                } else if (auxST.contains(106)) {
                    this.lexico(auxSB, auxST, this.textScanner);
                    velVar = auxSB.toString();
                    this.lexico(word, stoken, this.textScanner);
                    if (!stoken.contains(132)) {
                        this.setErrorCode(126);
                        return;
                    }
                }
            }
            if (velVar == null) {
                this.montador.cVelocidadeFaixa(vel);
            } else {
                this.montador.cVelocidadeFaixa(velVar);
            }
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            if (this.tokenListContains(this.unitTokenList, type.toString())) {
                if (this.isSeg(type.toString())) {
                    if (qualFaixa.getNumber() == 788) {
                        this.montador.cSeguirFaixaBrancaSegundo(duracao[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (qualFaixa.getNumber() == 908) {
                        this.montador.cSeguirFaixaPretaSegundo(duracao[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                } else {
                    if (qualFaixa.getNumber() == 788) {
                        this.montador.cSeguirFaixaBrancaMinuto(duracao[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (qualFaixa.getNumber() == 908) {
                        this.montador.cSeguirFaixaPretaMinuto(duracao[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                }
            } else if (Math.round(duracao[0]) == 255L) {
                if (qualFaixa.getNumber() == 788) {
                    this.montador.cSeguirFaixaBrancaMinuto(255.0);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (qualFaixa.getNumber() == 908) {
                    this.montador.cSeguirFaixaPretaMinuto(255.0);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (stTempo == 100) {
                this.generateCommandSensorEvent(2, 4);
                if (this.errorCode != 0) {
                    return;
                }
                Token corFinal = this.getToken(this.colorTokenList, type.toString());
                if (qualFaixa.getNumber() == 788) {
                    this.montador.cSeguirFaixaBrancaCor(corFinal.getNumber());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (qualFaixa.getNumber() == 908) {
                    this.montador.cSeguirFaixaPretaCor(corFinal.getNumber());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (stTempo == 48) {
                this.generateCommandSensorEvent(-4, 1);
                if (this.errorCode != 0) {
                    return;
                }
                if (qualFaixa.getNumber() == 788) {
                    this.montador.cSeguirFaixaBrancaObstaculo();
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (qualFaixa.getNumber() == 908) {
                    this.montador.cSeguirFaixaPretaObstaculo();
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (stTempo == 14) {
                this.setErrorCode(63, -1);
            }
        } else {
            this.setErrorCode(25);
        }
    }

    public void comEnquanto(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int startAddress = 0;
        int enderecoRetorno = -1;
        ExpressionType tipo = this.esperaExpressao(word, stoken);
        if (this.errorCode != 0) {
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(58) || auxST.contains(59)) {
            String y2;
            String y1;
            int value;
            int value2;
            int value1;
            String unit;
            String x2;
            String x1;
            String x3 = "@X3";
            String y3 = "@Y3";
            this.lexico(word, stoken, this.textScanner);
            ExpressionType tipo2 = this.esperaExpressao(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            if (tipo.getOp().equals(">=")) {
                tipo.setOp("M");
            } else if (tipo.getOp().equals("<=")) {
                tipo.setOp("m");
            } else if (tipo.getOp().equals("<>")) {
                tipo.setOp("!");
            }
            if (tipo.isBetween()) {
                if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    unit = tipo.getUnit();
                    if (tipo.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@X2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@X2", "nulo", tipo.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", tipo.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo.isColor()) {
                tipo.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                x1 = tipo.getV1();
                x2 = "@X2";
                x3 = "@X3";
                enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                this.montador.cAdd("@X2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@X3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo.usesDouble()) {
                unit = tipo.getUnit();
                if (tipo.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                x1 = tipo.getV1();
                x2 = "@X2";
                enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                this.montador.cAdd("@X2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo.usesN1()) {
                    x1 = "@X1";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@X1", "nulo", tipo.getN1());
                } else {
                    x1 = tipo.getV1();
                }
                if (tipo.usesN2()) {
                    x2 = "@X2";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@X2", "nulo", tipo.getN2());
                } else {
                    x2 = tipo.getV2();
                }
            }
            if (tipo2.getOp().equals(">=")) {
                tipo2.setOp("M");
            } else if (tipo2.getOp().equals("<=")) {
                tipo2.setOp("m");
            } else if (tipo2.getOp().equals("<>")) {
                tipo2.setOp("!");
            }
            if (tipo2.isBetween()) {
                if (tipo2.isSensor() || !tipo2.isSensor() && tipo2.getUnit().length() > 0) {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    unit = tipo2.getUnit();
                    if (tipo2.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo2.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo2.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@Y2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo2.isColor()) {
                tipo2.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo2.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                y1 = tipo2.getV1();
                y2 = "@Y2";
                y3 = "@Y3";
                enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                this.montador.cAdd("@Y2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@Y3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo2.usesDouble()) {
                unit = tipo2.getUnit();
                if (tipo2.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo2.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo2.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                y1 = tipo2.getV1();
                y2 = "@Y2";
                enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                this.montador.cAdd("@Y2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo2.usesN1()) {
                    y1 = "@Y1";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@Y1", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y1 = tipo2.getV1();
                }
                if (tipo2.usesN2()) {
                    y2 = "@Y2";
                    enderecoRetorno = enderecoRetorno > 0 ? enderecoRetorno : this.montador.getCE2prom() + 1;
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y2 = tipo2.getV2();
                }
            }
            if (auxST.contains(58)) {
                startAddress = this.montador.cIniciaEnquantoE(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoE(enderecoRetorno > 0 ? enderecoRetorno : startAddress, startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            } else {
                startAddress = this.montador.cIniciaEnquantoOU(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoOU(enderecoRetorno > 0 ? enderecoRetorno : startAddress, startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            }
        } else if (tipo.isBetween()) {
            if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                String unit = tipo.getUnit();
                if (tipo.getUnit().toLowerCase().equals("bat")) {
                    this.setErrorCode(54);
                    return;
                }
                int value1 = this.convertUnitToKernel(unit, tipo.getD1());
                int value2 = this.convertUnitToKernel(unit, tipo.getD2());
                if (this.errorCode != 0) {
                    return;
                }
                startAddress = this.montador.cIniciaEnquantoEntre(tipo.getV1(), value1, value2);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoEntre(startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            } else {
                startAddress = this.montador.cIniciaEnquantoEntre(tipo.getV1(), tipo.getN1(), tipo.getN2());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoEntre(startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            }
        } else if (tipo.usesDouble()) {
            int value;
            String unit = tipo.getUnit();
            if (tipo.getV1().toLowerCase().equals("bat")) {
                if (!unit.toLowerCase().equals("v")) {
                    this.setErrorCode(54);
                    return;
                }
                value = this.convertUnitToKernel("bat", tipo.getD2());
            } else {
                value = this.convertUnitToKernel(unit, tipo.getD2());
            }
            if (this.errorCode != 0) {
                return;
            }
            startAddress = this.montador.cIniciaEnquanto(tipo.getOp(), tipo.getV1(), value);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            this.commandBlock(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cTerminaEnquantoV1N2(startAddress);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
        } else if (!tipo.isColor()) {
            if (tipo.usesV1() && tipo.usesV2()) {
                startAddress = this.montador.cIniciaEnquanto(tipo.getOp(), tipo.getV1(), tipo.getV2());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoV1V2(startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            } else if (tipo.usesV1() && tipo.usesN2()) {
                startAddress = this.montador.cIniciaEnquanto(tipo.getOp(), tipo.getV1(), tipo.getN2());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cTerminaEnquantoV1N2(startAddress);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            }
        } else {
            int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
            startAddress = this.montador.cIniciaEnquantoCor(tipo.getOp(), tipo.getV1(), cor);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
            this.commandBlock(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cTerminaEnquantoCor(startAddress, tipo.getOp());
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
                return;
            }
        }
    }

    public void comEnvie(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(108) && !stoken.contains(106)) {
            this.setErrorCode(106);
            return;
        }
        if (stoken.contains(108)) {
            short valor = Short.valueOf(word.toString());
            this.montador.cEnviaInteiro(valor);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stoken.contains(106)) {
            String var = new String(word.toString());
            this.montador.cEnviaVariavel(var);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comReceba(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(106)) {
            this.setErrorCode(107);
            return;
        }
        String var = new String(word.toString());
        this.montador.cRecebe(var);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comRecebeu(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(106)) {
            this.setErrorCode(107);
            return;
        }
        String var = new String(word.toString());
        this.montador.cRecebeu(var);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comAtribuicao(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int porta;
        TextScanner auxTS;
        StringBuilder auxSB = new StringBuilder();
        boolean isSpacelessMinusNumber = false;
        if (word.toString().toUpperCase().equals("VEZ")) {
            this.setErrorCode(149);
            return;
        }
        String v1 = new String(word.toString());
        String v2 = "";
        String v3 = "";
        int token1 = 0;
        int token2 = 0;
        double valor1r = 0.0;
        double valor2r = 0.0;
        int valor1 = 0;
        int valor2 = 0;
        String unit1 = "";
        String unit2 = "";
        String op = "";
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(110)) {
            this.setErrorCode(46);
            return;
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!(stoken.contains(33) || stoken.contains(34) || stoken.contains(129) || stoken.contains(106) || stoken.contains(108) || stoken.contains(63) || stoken.contains(109))) {
            this.setErrorCode(47);
            return;
        }
        token1 = this.getValueFromStoken(stoken);
        if (stoken.contains(63)) {
            short ate;
            short de;
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(64)) {
                this.setErrorCode(147);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(108)) {
                this.setErrorCode(128);
                return;
            }
            try {
                de = Short.valueOf(word.toString());
            }
            catch (Exception e) {
                this.setErrorCode(144);
                return;
            }
            if (de < 0 || de > 100) {
                this.setErrorCode(144);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(65)) {
                this.setErrorCode(147);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(108)) {
                this.setErrorCode(128);
                return;
            }
            try {
                ate = Short.valueOf(word.toString());
            }
            catch (Exception e) {
                this.setErrorCode(144);
                return;
            }
            if (ate < 0 || ate > 100) {
                this.setErrorCode(144);
                return;
            }
            this.montador.cAleatorio(v1, de, ate);
        }
        if (token1 == 108) {
            try {
                Short.valueOf(word.toString());
            }
            catch (Exception e) {
                token1 = 109;
            }
        }
        if (token1 == 33) {
            token1 = 108;
            valor1 = 1;
        } else if (token1 == 34) {
            token1 = 108;
            valor1 = 0;
        } else if (token1 == 108 || token1 == 109) {
            if (token1 == 108) {
                valor1 = Integer.valueOf(word.toString());
                valor1r = valor1;
                try {
                    Short.valueOf(word.toString());
                }
                catch (Exception e) {
                    token1 = 109;
                }
            } else {
                valor1r = Double.valueOf(word.toString());
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(131)) {
                this.lexico(word, stoken, this.textScanner);
                double auxMultiplier = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                token1 = 109;
                valor1r *= auxMultiplier;
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(130)) {
                this.lexico(word, stoken, this.textScanner);
                unit1 = word.toString();
                if (token1 != 109) {
                    token1 = 109;
                    valor1r = valor1;
                }
            }
            if (token1 == 109 && unit1.equals("")) {
                this.setErrorCode(99);
                return;
            }
        } else {
            v2 = new String(word.toString());
        }
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if ((auxST.contains(108) || auxST.contains(109)) && this.isNegativeNumber(auxSB.toString())) {
            isSpacelessMinusNumber = true;
            this.getSpacelessMinusNumber(word, stoken);
            op = "-";
        }
        if (!isSpacelessMinusNumber) {
            if (!this.isArithmeticOp(auxST)) {
                switch (token1) {
                    case 106: 
                    case 129: {
                        this.montador.cAdd(v1, v2, "nulo");
                        if (this.montador.getErrorCode() == 0) break;
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        break;
                    }
                    case 108: {
                        this.montador.cAdd(v1, "nulo", valor1);
                        if (this.montador.getErrorCode() == 0) break;
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        break;
                    }
                    case 109: {
                        if (unit1.toLowerCase().equals("bat")) {
                            this.setErrorCode(47);
                            return;
                        }
                        valor1 = this.convertUnitToKernel(unit1, valor1r);
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cAdd(v1, "nulo", valor1);
                        if (this.montador.getErrorCode() == 0) break;
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            op = new String(word.toString());
            this.lexico(word, stoken, this.textScanner);
        }
        if (!(stoken.contains(33) || stoken.contains(34) || stoken.contains(129) || stoken.contains(106) || stoken.contains(108) || stoken.contains(109))) {
            this.setErrorCode(47);
            return;
        }
        token2 = this.getValueFromStoken(stoken);
        if (token2 == 108) {
            try {
                Short.valueOf(word.toString());
            }
            catch (Exception e) {
                token2 = 109;
            }
        }
        if (token2 == 33) {
            token2 = 108;
            valor2 = 1;
        } else if (token2 == 34) {
            token2 = 108;
            valor2 = 0;
        } else if (token2 == 108 || token2 == 109) {
            if (token2 == 108) {
                valor2 = Integer.valueOf(word.toString());
                valor2r = valor2;
                try {
                    Short.valueOf(word.toString());
                }
                catch (Exception e) {
                    token2 = 109;
                }
            } else {
                valor2r = Double.valueOf(word.toString());
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(131)) {
                this.lexico(word, stoken, this.textScanner);
                double auxMultiplier = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                token2 = 109;
                valor2r *= auxMultiplier;
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(130)) {
                this.lexico(word, stoken, this.textScanner);
                unit2 = word.toString();
                if (token2 != 109) {
                    token2 = 109;
                    valor2r = valor2;
                }
            }
            if (token2 == 109 && unit2.equals("")) {
                this.setErrorCode(99);
                return;
            }
        } else {
            v3 = new String(word.toString());
        }
        if (token1 == 109) {
            if (unit1.toLowerCase().equals("bat")) {
                this.setErrorCode(47);
                return;
            }
            token1 = 108;
            valor1 = this.convertUnitToKernel(unit1, valor1r);
            if (token2 == 129) {
                int IDSensor = this.getToken(this.measureUnitTokenList, unit1).getNumber();
                porta = this.getToken(this.sensorTokenList, v3).getNumber();
                this.generateCommandSensorEvent(IDSensor, porta);
                if (this.errorCode != 0) {
                    return;
                }
            }
            if (this.errorCode != 0) {
                return;
            }
        }
        if (token2 == 109) {
            if (unit2.toLowerCase().equals("bat")) {
                this.setErrorCode(47);
                return;
            }
            token2 = 108;
            valor2 = this.convertUnitToKernel(unit2, valor2r);
            if (token1 == 129) {
                int IDSensor = this.getToken(this.measureUnitTokenList, unit2).getNumber();
                porta = this.getToken(this.sensorTokenList, v2).getNumber();
                this.generateCommandSensorEvent(IDSensor, porta);
                if (this.errorCode != 0) {
                    return;
                }
            }
            if (this.errorCode != 0) {
                return;
            }
        }
        if (token1 == 108 && token2 == 108 && !unit1.equals(unit2)) {
            this.setErrorCode(146);
            return;
        }
        if (op.equals("+")) {
            if (token1 == 108 && token2 == 108) {
                int result;
                try {
                    result = valor1 + valor2;
                    Short.valueOf(String.valueOf(result));
                }
                catch (Exception e) {
                    this.setErrorCode(31);
                    return;
                }
                this.montador.cAdd(v1, "nulo", result);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token1 == 108) {
                this.montador.cAdd(v1, v3, valor1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token2 == 108) {
                this.montador.cAdd(v1, v2, valor2);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cAdd(v1, v2, v3);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (op.equals("-")) {
            if (token1 == 108 && token2 == 108) {
                int result;
                try {
                    result = valor1 - valor2;
                    Short.valueOf(String.valueOf(result));
                }
                catch (Exception e) {
                    this.setErrorCode(31);
                    return;
                }
                this.montador.cAdd(v1, "nulo", result);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token1 == 108) {
                this.montador.cSub(v1, valor1, v3);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token2 == 108) {
                if (valor2 >= 0) {
                    this.montador.cSub(v1, v2, valor2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    this.montador.cAdd(v1, v2, -valor2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
                return;
            }
            this.montador.cSub(v1, v2, v3);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (op.equals("*")) {
            if (token1 == 108 && token2 == 108) {
                int result;
                try {
                    result = valor1 * valor2;
                    Short.valueOf(String.valueOf(result));
                }
                catch (Exception e) {
                    this.setErrorCode(31);
                    return;
                }
                this.montador.cAdd(v1, "nulo", result);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token1 == 108) {
                this.montador.cMult(v1, v3, valor1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token2 == 108) {
                this.montador.cMult(v1, v2, valor2);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cMult(v1, v2, v3);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (op.equals("/")) {
            if (token1 == 108 && token2 == 108) {
                int result;
                try {
                    result = Math.round(valor1 / valor2);
                    Short.valueOf(String.valueOf(result));
                }
                catch (Exception e) {
                    this.setErrorCode(31);
                    return;
                }
                this.montador.cAdd(v1, "nulo", result);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token1 == 108) {
                this.montador.cDiv(v1, valor1, v3);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (token2 == 108) {
                this.montador.cDiv(v1, v2, valor2);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cDiv(v1, v2, v3);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
    }

    public String[] getSpacelessMinusNumber(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        String[] s = new String[2];
        this.lexico(word, stoken, this.textScanner);
        s[0] = new Character(word.toString().charAt(0)).toString();
        s[1] = word.toString().substring(1);
        word.setLength(0);
        word.append(s[1]);
        this.setStokenValue(stoken, 108);
        return s;
    }

    public void comSom(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] som = new int[1];
        this.esperaSom(word, stoken, som);
        if (this.errorCode != 0) {
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        Token tEfeito = this.getToken(this.soundEffectTokenList, word.toString());
        int valorEfeito = tEfeito.getNumber();
        this.montador.cSom(valorEfeito, 0.0);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(108) || auxST.contains(109)) {
            this.setErrorCode(59);
        }
    }

    public void comNota(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] som = new int[1];
        double[] tempo = new double[1];
        this.esperaNota(word, stoken, som);
        if (this.errorCode != 0) {
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        Token tNota = this.getToken(this.noteTokenList, word.toString());
        int valorNota = tNota.getNumber();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
            return;
        }
        if (!auxST.contains(108) && !auxST.contains(109)) {
            this.montador.cSom(valorNota, 255.0);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        this.esperaTempoSom(word, stoken, tempo);
        if (this.errorCode != 0) {
            return;
        }
        this.montador.cSom(valorNota, tempo[0]);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comMudo(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.montador.cSomMudo();
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public boolean isNote(ArrayList<Integer> stoken) {
        return stoken.contains(122);
    }

    public boolean isLineColor(ArrayList<Integer> stoken) {
        return stoken.contains(125);
    }

    public void comRe(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        StringBuilder type = new StringBuilder();
        int[] velAux = new int[1];
        this.esperaTempo(word, stoken, duracao, type);
        if (this.errorCode != 0) {
            return;
        }
        int stTempo = this.getValueFromStoken(stoken);
        String[] velVarAux = new String[1];
        boolean usaVelocidade = this.esperaVelocidadeOuPorcento(word, stoken, this.textScanner, velAux, velVarAux);
        int vel = velAux[0];
        String velVar = velVarAux[0];
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (!usaVelocidade) {
                if (this.isSeg(type.toString())) {
                    this.montador.cReSegundo(duracao[0], 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cReMinuto(duracao[0], 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                if (this.isSeg(type.toString())) {
                    this.montador.cReSegundo(duracao[0], vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cReMinuto(duracao[0], vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (this.isSeg(type.toString())) {
                this.montador.cReSegundo(duracao[0], velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cReMinuto(duracao[0], velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            if (!usaVelocidade) {
                this.montador.cReMinuto(255.0, 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                this.montador.cReMinuto(255.0, vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cReMinuto(255.0, velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cReCor(cor.getNumber(), 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cReCor(cor.getNumber(), vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cReCor(cor.getNumber(), velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 8);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cReObstaculo(70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cReObstaculo(vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cReObstaculo(velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cReFaixa(70, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cReFaixa(vel, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cReFaixa(velVar, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        }
    }

    public void comFrente(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        int[] velAux = new int[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempo(word, stoken, duracao, type);
        if (this.errorCode != 0) {
            return;
        }
        int stTempo = this.getValueFromStoken(stoken);
        String[] velVarAux = new String[1];
        boolean usaVelocidade = this.esperaVelocidadeOuPorcento(word, stoken, this.textScanner, velAux, velVarAux);
        int vel = velAux[0];
        String velVar = velVarAux[0];
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (!usaVelocidade) {
                if (this.isSeg(type.toString())) {
                    this.montador.cFrenteSegundo(duracao[0], 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cFrenteMinuto(duracao[0], 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                if (this.isSeg(type.toString())) {
                    this.montador.cFrenteSegundo(duracao[0], vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cFrenteMinuto(duracao[0], vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (this.isSeg(type.toString())) {
                this.montador.cFrenteSegundo(duracao[0], velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cFrenteMinuto(duracao[0], velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            if (!usaVelocidade) {
                this.montador.cFrenteMinuto(255.0, 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                this.montador.cFrenteMinuto(255.0, vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cFrenteMinuto(255.0, velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cFrenteCor(cor.getNumber(), 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cFrenteCor(cor.getNumber(), vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cFrenteCor(cor.getNumber(), velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 1);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cFrenteObstaculo(70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cFrenteObstaculo(vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cFrenteObstaculo(velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cFrenteFaixa(70, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cFrenteFaixa(vel, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cFrenteFaixa(velVar, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        }
    }

    public void comGiroDireita(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        int[] velAux = new int[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempo(word, stoken, duracao, type);
        int stTempo = this.getValueFromStoken(stoken);
        if (this.errorCode != 0) {
            return;
        }
        String[] velVarAux = new String[1];
        boolean usaVelocidade = this.esperaVelocidadeOuPorcento(word, stoken, this.textScanner, velAux, velVarAux);
        int vel = velAux[0];
        String velVar = velVarAux[0];
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (!usaVelocidade) {
                if (this.isSeg(type.toString())) {
                    this.montador.cGiroDireitaSegundo(duracao[0], 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cGiroDireitaMinuto(duracao[0], 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                if (this.isSeg(type.toString())) {
                    this.montador.cGiroDireitaSegundo(duracao[0], vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cGiroDireitaMinuto(duracao[0], vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (this.isSeg(type.toString())) {
                this.montador.cGiroDireitaSegundo(duracao[0], velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cGiroDireitaMinuto(duracao[0], velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            if (!usaVelocidade) {
                this.montador.cGiroDireitaMinuto(255.0, 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                this.montador.cGiroDireitaMinuto(255.0, vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cGiroDireitaMinuto(255.0, velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroDireitaCor(cor.getNumber(), 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroDireitaCor(cor.getNumber(), vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroDireitaCor(cor.getNumber(), velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 8);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cGiroDireitaObstaculo(70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cGiroDireitaObstaculo(vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cGiroDireitaObstaculo(velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cGiroDireitaFaixa(70, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cGiroDireitaFaixa(vel, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cGiroDireitaFaixa(velVar, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        }
    }

    public void comGiroEsquerda(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        int[] velAux = new int[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempo(word, stoken, duracao, type);
        if (this.errorCode != 0) {
            return;
        }
        int stTempo = this.getValueFromStoken(stoken);
        String[] velVarAux = new String[1];
        boolean usaVelocidade = this.esperaVelocidadeOuPorcento(word, stoken, this.textScanner, velAux, velVarAux);
        int vel = velAux[0];
        String velVar = velVarAux[0];
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (!usaVelocidade) {
                if (this.isSeg(type.toString())) {
                    this.montador.cGiroEsquerdaSegundo(duracao[0], 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cGiroEsquerdaMinuto(duracao[0], 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                if (this.isSeg(type.toString())) {
                    this.montador.cGiroEsquerdaSegundo(duracao[0], vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                this.montador.cGiroEsquerdaMinuto(duracao[0], vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (this.isSeg(type.toString())) {
                this.montador.cGiroEsquerdaSegundo(duracao[0], velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cGiroEsquerdaMinuto(duracao[0], velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            if (!usaVelocidade) {
                this.montador.cGiroEsquerdaMinuto(255.0, 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            if (velVar == null) {
                this.montador.cGiroEsquerdaMinuto(255.0, vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cGiroEsquerdaMinuto(255.0, velVar);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroEsquerdaCor(cor.getNumber(), 70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroEsquerdaCor(cor.getNumber(), vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                this.montador.cGiroEsquerdaCor(cor.getNumber(), velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 8);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cGiroEsquerdaObstaculo(70);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cGiroEsquerdaObstaculo(vel);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cGiroEsquerdaObstaculo(velVar);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                this.montador.cGiroEsquerdaFaixa(70, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (velVar == null) {
                this.montador.cGiroEsquerdaFaixa(vel, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.cGiroEsquerdaFaixa(velVar, (int)Math.round(duracao[0]));
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        }
    }

    public void comPare() throws IOException {
        this.montador.cPare();
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comColetar() throws IOException {
        this.montador.cIniciaColeta();
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comFimColeta() throws IOException {
        this.montador.cFimColeta();
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comAtivar(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (this.errorCode != 0) {
            return;
        }
        if (this.language == 3 && auxST.contains(127)) {
            this.comAcenda(word, stoken);
            return;
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(129)) {
            this.setErrorCode(87);
            return;
        }
        Token sensor = this.getToken(this.sensorTokenList, word.toString());
        int num = sensor.getNumber();
        this.montador.cSensorColeta(num);
    }

    public void comIntervalo(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(108)) {
            this.setErrorCode(88);
            return;
        }
        int num = Integer.valueOf(word.toString());
        if (num <= 0 || num > 255) {
            this.setErrorCode(89);
            return;
        }
        this.montador.cSetTempoColeta(num);
        if (this.montador.getErrorCode() != 0) {
            this.setErrorCode(this.montador.getErrorCode(), -1);
        }
    }

    public void comEspere(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        boolean ESPERE_TEMPO = false;
        boolean ESPERE_EXPRESSAO = true;
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        int tipo = 0;
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (!(auxST.contains(108) || auxST.contains(109) || auxST.contains(16) || auxST.contains(15) || auxST.contains(106) || auxST.contains(129) || auxST.contains(100) || auxST.contains(52))) {
            this.setErrorCode(52);
            return;
        }
        if (auxST.contains(16) || auxST.contains(15) || auxST.contains(109)) {
            tipo = 0;
        } else if (auxST.contains(108)) {
            this.lexico(auxSB, auxST, auxTS);
            tipo = auxST.contains(107) ? 0 : 1;
        } else if (auxST.contains(106) || auxST.contains(129) || auxST.contains(100) || auxST.contains(52)) {
            tipo = 1;
        }
        switch (tipo) {
            case 0: {
                this.comEspereTempo(word, stoken);
                break;
            }
            case 1: {
                this.comEspereExpressao(word, stoken);
                if (this.errorCode != 7) break;
                this.errorCode = 22;
            }
        }
    }

    public void comEspereTempo(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        double[] duracao = new double[1];
        StringBuilder type = new StringBuilder();
        this.esperaTempoObjeto(word, stoken, duracao, type);
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (this.isSeg(type.toString())) {
                this.montador.cEsperarSegundo(duracao[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                return;
            }
            this.montador.cEsperarMinuto(duracao[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (Math.round(duracao[0]) == 255L) {
            this.montador.cEsperarMinuto(255.0);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
            return;
        }
        if (stoken.contains(100)) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            Token cor = this.getToken(this.colorTokenList, type.toString());
            this.montador.cEsperarCor(cor.getNumber());
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (stoken.contains(51)) {
            this.generateCommandSensorEvent(-4, 1);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cEsperarObjeto();
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    private void comEspereExpressao(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        ExpressionType tipo = this.esperaExpressao(word, stoken);
        if (this.errorCode != 0) {
            return;
        }
        if (tipo.getV1().toUpperCase().equals("VEZ") || tipo.getV2().toUpperCase().equals("VEZ")) {
            this.setErrorCode(54);
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(58) || auxST.contains(59)) {
            String y2;
            String y1;
            int value;
            int value2;
            int value1;
            String unit;
            String x2;
            String x1;
            String x3 = "@X3";
            String y3 = "@Y3";
            this.lexico(word, stoken, this.textScanner);
            ExpressionType tipo2 = this.esperaExpressao(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            if (tipo2.getV1().toUpperCase().equals("VEZ") || tipo2.getV2().toUpperCase().equals("VEZ")) {
                this.setErrorCode(54);
                return;
            }
            if (tipo.getOp().equals(">=")) {
                tipo.setOp("M");
            } else if (tipo.getOp().equals("<=")) {
                tipo.setOp("m");
            } else if (tipo.getOp().equals("<>")) {
                tipo.setOp("!");
            }
            if (tipo.isBetween()) {
                if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    unit = tipo.getUnit();
                    if (tipo.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cAdd("@X2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    this.montador.cAdd("@X2", "nulo", tipo.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", tipo.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo.isColor()) {
                tipo.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                x1 = tipo.getV1();
                x2 = "@X2";
                x3 = "@X3";
                this.montador.cAdd("@X2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@X3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo.usesDouble()) {
                unit = tipo.getUnit();
                if (tipo.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                x1 = tipo.getV1();
                x2 = "@X2";
                this.montador.cAdd("@X2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo.usesN1()) {
                    x1 = "@X1";
                    this.montador.cAdd("@X1", "nulo", tipo.getN1());
                } else {
                    x1 = tipo.getV1();
                }
                if (tipo.usesN2()) {
                    x2 = "@X2";
                    this.montador.cAdd("@X2", "nulo", tipo.getN2());
                } else {
                    x2 = tipo.getV2();
                }
            }
            if (tipo2.getOp().equals(">=")) {
                tipo2.setOp("M");
            } else if (tipo2.getOp().equals("<=")) {
                tipo2.setOp("m");
            } else if (tipo2.getOp().equals("<>")) {
                tipo2.setOp("!");
            }
            if (tipo2.isBetween()) {
                if (tipo2.isSensor() || !tipo2.isSensor() && tipo2.getUnit().length() > 0) {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    unit = tipo2.getUnit();
                    if (tipo2.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo2.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo2.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cAdd("@Y2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo2.isColor()) {
                tipo2.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo2.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                y1 = tipo2.getV1();
                y2 = "@Y2";
                y3 = "@Y3";
                this.montador.cAdd("@Y2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@Y3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo2.usesDouble()) {
                unit = tipo2.getUnit();
                if (tipo2.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo2.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo2.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                y1 = tipo2.getV1();
                y2 = "@Y2";
                this.montador.cAdd("@Y2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo2.usesN1()) {
                    y1 = "@Y1";
                    this.montador.cAdd("@Y1", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y1 = tipo2.getV1();
                }
                if (tipo2.usesN2()) {
                    y2 = "@Y2";
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y2 = tipo2.getV2();
                }
            }
            if (auxST.contains(58)) {
                this.montador.espereE(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                this.montador.espereOU(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
        } else if (tipo.isBetween()) {
            if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                String unit = tipo.getUnit();
                if (tipo.getUnit().toLowerCase().equals("bat")) {
                    this.setErrorCode(54);
                    return;
                }
                int value1 = this.convertUnitToKernel(unit, tipo.getD1());
                int value2 = this.convertUnitToKernel(unit, tipo.getD2());
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cesperarCondicaoEntre(tipo.getV1(), value1, value2);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            } else {
                this.montador.cesperarCondicaoEntre(tipo.getV1(), tipo.getN1(), tipo.getN2());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
            }
        } else if (tipo.usesDouble()) {
            int value;
            String unit = tipo.getUnit();
            if (tipo.getV1().toLowerCase().equals("bat")) {
                if (!unit.toLowerCase().equals("v")) {
                    this.setErrorCode(54);
                    return;
                }
                value = this.convertUnitToKernel("bat", tipo.getD2());
            } else {
                value = this.convertUnitToKernel(unit, tipo.getD2());
            }
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cesperarCondicao(tipo.getOp(), tipo.getV1(), value);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else if (!tipo.isColor()) {
            if (tipo.usesV1() && tipo.usesV2()) {
                this.montador.cesperarCondicao(tipo.getOp(), tipo.getV1(), tipo.getV2());
            } else if (tipo.usesV1() && tipo.usesN2()) {
                this.montador.cesperarCondicao(tipo.getOp(), tipo.getV1(), tipo.getN2());
            }
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        } else {
            int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
            this.montador.cesperarCondicaoCor(tipo.getOp(), tipo.getV1(), cor);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comHabilite(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        this.esperaEvento(word, stoken, num);
        if (this.errorCode == 0) {
            TextScanner auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(66)) {
                this.lexico(word, stoken, this.textScanner);
                this.generateCommandSensorEvent(-4, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cResetarEvento(num[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cLigarEvento(num[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (auxST.contains(110)) {
                    this.lexico(word, stoken, this.textScanner);
                    this.lexico(word, stoken, this.textScanner);
                    if (this.errorCode != 0) {
                        return;
                    }
                    if (stoken.contains(34)) {
                        this.generateCommandSensorEvent(-4, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cResetarEvento(num[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        this.montador.cLigarEvento(num[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                    } else if (stoken.contains(33)) {
                        this.generateCommandSensorEvent(-4, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cSetarEvento(num[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        this.montador.cLigarEvento(num[0]);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                    } else {
                        this.setErrorCode(148);
                    }
                    return;
                }
                if (auxST.contains(111) || auxST.contains(112) || auxST.contains(113) || auxST.contains(114)) {
                    this.lexico(word, stoken, this.textScanner);
                    if (this.errorCode != 0) {
                        return;
                    }
                    int op = this.getValueFromStoken(stoken);
                    char cOp = '<';
                    switch (op) {
                        case 111: 
                        case 113: {
                            cOp = '>';
                            break;
                        }
                        case 112: 
                        case 114: {
                            cOp = '<';
                        }
                    }
                    this.lexico(word, stoken, this.textScanner);
                    if (this.errorCode != 0) {
                        return;
                    }
                    if (!stoken.contains(109) && !stoken.contains(108)) {
                        this.setErrorCode(128);
                        return;
                    }
                    double value = Double.valueOf(word.toString());
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(131)) {
                        this.lexico(word, stoken, this.textScanner);
                        double auxMultiplier = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                        value *= auxMultiplier;
                    }
                    String unit = "";
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(130)) {
                        this.lexico(word, stoken, this.textScanner);
                        if (this.errorCode != 0) {
                            return;
                        }
                        unit = word.toString();
                        int IDSensor = this.getToken(this.measureUnitTokenList, unit).getNumber();
                        cOp = this.invertOperators(cOp, IDSensor);
                        this.generateCommandSensorEvent(IDSensor, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                        if (this.errorCode != 0) {
                            return;
                        }
                    } else {
                        this.generateCommandSensorEvent(20, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                        if (this.errorCode != 0) {
                            return;
                        }
                    }
                    int valor = 0;
                    if (unit.length() > 0) {
                        valor = this.convertUnitToKernel(unit, value);
                        if (this.errorCode != 0) {
                            return;
                        }
                    } else {
                        valor = (int)Math.round(value);
                    }
                    if (op == 111 && valor >= 1023) {
                        this.setErrorCode(129);
                        return;
                    }
                    if (op == 112 && valor <= 0) {
                        this.setErrorCode(129);
                        return;
                    }
                    if (op == 113 && valor <= 0) {
                        this.setErrorCode(130);
                        return;
                    }
                    if (op == 114 && valor >= 1023) {
                        this.setErrorCode(130);
                        return;
                    }
                    if (valor < 0 || valor > 1023) {
                        this.setErrorCode(131);
                        return;
                    }
                    if (op == 113) {
                        --valor;
                    } else if (op == 114) {
                        ++valor;
                    }
                    this.montador.cSetarEventoAnalogico(cOp, valor, num[0]);
                    this.montador.cLigarEvento(num[0]);
                } else {
                    this.generateCommandSensorEvent(-4, Integer.valueOf(new Integer(num[0]).toString().substring(0, 1)));
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cSetarEvento(num[0]);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cLigarEvento(num[0]);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            }
        }
    }

    public void comAcenda(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        this.esperaLed(word, stoken, num, false);
        if (this.errorCode == 0) {
            this.montador.cLigarLed(num[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comApague(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        this.esperaLed(word, stoken, num, true);
        if (this.errorCode == 0) {
            this.montador.cDesligarLed(num[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comLigue(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (this.errorCode != 0) {
            return;
        }
        if (auxST.contains(21)) {
            this.lexico(word, stoken, this.textScanner);
            this.esperaSaidaSensorDistancia(word, stoken, num);
            if (this.errorCode == 0) {
                int porta = (num[0] - 3) / 10;
                this.generateCommandSensorEvent(8, porta);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cLigarSaida(num[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
            return;
        }
        this.esperaSaida(word, stoken, num);
        if (this.errorCode == 0) {
            int porta = (num[0] - 2) / 10;
            this.generateCommandSensorEvent(-1, porta);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cLigarSaida(num[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comDesligue(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (this.errorCode != 0) {
            return;
        }
        if (auxST.contains(21)) {
            this.lexico(word, stoken, this.textScanner);
            this.esperaSaidaSensorDistancia(word, stoken, num);
            if (this.errorCode == 0) {
                int porta = (num[0] - 3) / 10;
                this.generateCommandSensorEvent(8, porta);
                if (this.errorCode != 0) {
                    return;
                }
                this.montador.cDesligarSaida(num[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            }
            return;
        }
        this.esperaSaida(word, stoken, num, false);
        if (this.errorCode == 0) {
            int porta = (num[0] - 2) / 10;
            this.generateCommandSensorEvent(-1, porta);
            if (this.errorCode != 0) {
                return;
            }
            this.montador.cDesligarSaida(num[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comDesabilite(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int[] num = new int[1];
        this.esperaEvento(word, stoken, num, true);
        if (this.errorCode == 0) {
            this.montador.cDesligarEvento(num[0]);
            if (this.montador.getErrorCode() != 0) {
                this.setErrorCode(this.montador.getErrorCode(), -1);
            }
        }
    }

    public void comSe(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int ifStartAddress = 0;
        int elseStartAddress = 0;
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        ExpressionType tipo = this.esperaExpressao(word, stoken);
        if (this.errorCode != 0) {
            return;
        }
        auxSB = new StringBuilder();
        auxST = new ArrayList();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(58) || auxST.contains(59)) {
            String y2;
            String y1;
            int value;
            int value2;
            int value1;
            String unit;
            String x2;
            String x1;
            String x3 = "@X3";
            String y3 = "@Y3";
            this.lexico(word, stoken, this.textScanner);
            ExpressionType tipo2 = this.esperaExpressao(word, stoken);
            if (this.errorCode != 0) {
                return;
            }
            if (tipo.getOp().equals(">=")) {
                tipo.setOp("M");
            } else if (tipo.getOp().equals("<=")) {
                tipo.setOp("m");
            } else if (tipo.getOp().equals("<>")) {
                tipo.setOp("!");
            }
            if (tipo.isBetween()) {
                if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    unit = tipo.getUnit();
                    if (tipo.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cAdd("@X2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo.setOp("E");
                    x1 = tipo.getV1();
                    x2 = "@X2";
                    x3 = "@X3";
                    this.montador.cAdd("@X2", "nulo", tipo.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@X3", "nulo", tipo.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo.isColor()) {
                tipo.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                x1 = tipo.getV1();
                x2 = "@X2";
                x3 = "@X3";
                this.montador.cAdd("@X2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@X3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo.usesDouble()) {
                unit = tipo.getUnit();
                if (tipo.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                x1 = tipo.getV1();
                x2 = "@X2";
                this.montador.cAdd("@X2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo.usesN1()) {
                    x1 = "@X1";
                    this.montador.cAdd("@X1", "nulo", tipo.getN1());
                } else {
                    x1 = tipo.getV1();
                }
                if (tipo.usesN2()) {
                    x2 = "@X2";
                    this.montador.cAdd("@X2", "nulo", tipo.getN2());
                } else {
                    x2 = tipo.getV2();
                }
            }
            if (tipo2.getOp().equals(">=")) {
                tipo2.setOp("M");
            } else if (tipo2.getOp().equals("<=")) {
                tipo2.setOp("m");
            } else if (tipo2.getOp().equals("<>")) {
                tipo2.setOp("!");
            }
            if (tipo2.isBetween()) {
                if (tipo2.isSensor() || !tipo2.isSensor() && tipo2.getUnit().length() > 0) {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    unit = tipo2.getUnit();
                    if (tipo2.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value1 = this.convertUnitToKernel(unit, tipo2.getD1());
                    value2 = this.convertUnitToKernel(unit, tipo2.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cAdd("@Y2", "nulo", value1);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    tipo2.setOp("E");
                    y1 = tipo2.getV1();
                    y2 = "@Y2";
                    y3 = "@Y3";
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    this.montador.cAdd("@Y3", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                }
            } else if (tipo2.isColor()) {
                tipo2.setOp("E");
                int cor = this.getToken(this.colorTokenList, tipo2.getV2().toString()).getNumber();
                int[] iInicio = new int[1];
                int[] iFim = new int[1];
                this.getColorInterval(cor, iInicio, iFim);
                y1 = tipo2.getV1();
                y2 = "@Y2";
                y3 = "@Y3";
                this.montador.cAdd("@Y2", "nulo", iInicio[0] + 1);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
                this.montador.cAdd("@Y3", "nulo", iFim[0]);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else if (tipo2.usesDouble()) {
                unit = tipo2.getUnit();
                if (tipo2.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo2.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo2.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                y1 = tipo2.getV1();
                y2 = "@Y2";
                this.montador.cAdd("@Y2", "nulo", value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                }
            } else {
                if (tipo2.usesN1()) {
                    y1 = "@Y1";
                    this.montador.cAdd("@Y1", "nulo", tipo2.getN1());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y1 = tipo2.getV1();
                }
                if (tipo2.usesN2()) {
                    y2 = "@Y2";
                    this.montador.cAdd("@Y2", "nulo", tipo2.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                } else {
                    y2 = tipo2.getV2();
                }
            }
            if (auxST.contains(58)) {
                this.lexico(word, stoken, this.textScanner);
                if (this.errorCode != 0) {
                    return;
                }
                if (!stoken.contains(26)) {
                    this.setErrorCode(57);
                    return;
                }
                ifStartAddress = this.montador.cIniciaSeEntaoE(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(27)) {
                    this.lexico(word, stoken, this.textScanner);
                    elseStartAddress = this.montador.cIniciaSenaoE(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cTerminaSenaoE(elseStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                } else {
                    this.montador.cTerminaSeEntaoE(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                }
            } else {
                this.lexico(word, stoken, this.textScanner);
                if (this.errorCode != 0) {
                    return;
                }
                if (!stoken.contains(26)) {
                    this.setErrorCode(57);
                    return;
                }
                ifStartAddress = this.montador.cIniciaSeEntaoOU(x1, x2, x3, tipo.getOp(), y1, y2, y3, tipo2.getOp());
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(27)) {
                    this.lexico(word, stoken, this.textScanner);
                    elseStartAddress = this.montador.cIniciaSenaoOU(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cTerminaSenaoOU(elseStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                } else {
                    this.montador.cTerminaSeEntaoOU(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                }
            }
        } else {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(26)) {
                this.setErrorCode(57);
                return;
            }
            if (tipo.isBetween()) {
                if (tipo.isSensor() || !tipo.isSensor() && tipo.getUnit().length() > 0) {
                    String unit = tipo.getUnit();
                    if (tipo.getUnit().toLowerCase().equals("bat")) {
                        this.setErrorCode(54);
                        return;
                    }
                    int value1 = this.convertUnitToKernel(unit, tipo.getD1());
                    int value2 = this.convertUnitToKernel(unit, tipo.getD2());
                    if (this.errorCode != 0) {
                        return;
                    }
                    ifStartAddress = this.montador.cIniciaSeEntaoEntre(tipo.getV1(), value1, value2);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(27)) {
                        this.lexico(word, stoken, this.textScanner);
                        elseStartAddress = this.montador.cIniciaSenaoEntre(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                        this.commandBlock(word, stoken);
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cTerminaSenaoEntre(elseStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    } else {
                        this.montador.cTerminaSeEntaoEntre(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    }
                } else {
                    ifStartAddress = this.montador.cIniciaSeEntaoEntre(tipo.getV1(), tipo.getN1(), tipo.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(27)) {
                        this.lexico(word, stoken, this.textScanner);
                        elseStartAddress = this.montador.cIniciaSenaoEntre(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                        this.commandBlock(word, stoken);
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cTerminaSenaoEntre(elseStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    } else {
                        this.montador.cTerminaSeEntaoEntre(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    }
                }
            } else if (tipo.usesDouble()) {
                int value;
                String unit = tipo.getUnit();
                if (tipo.getV1().toLowerCase().equals("bat")) {
                    if (!unit.toLowerCase().equals("v")) {
                        this.setErrorCode(54);
                        return;
                    }
                    value = this.convertUnitToKernel("bat", tipo.getD2());
                } else {
                    value = this.convertUnitToKernel(unit, tipo.getD2());
                }
                if (this.errorCode != 0) {
                    return;
                }
                ifStartAddress = this.montador.cIniciaSeEntao(tipo.getOp(), tipo.getV1(), value);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(27)) {
                    this.lexico(word, stoken, this.textScanner);
                    elseStartAddress = this.montador.cIniciaSenaoV1N2(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cTerminaSenao(elseStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                } else {
                    this.montador.cTerminaSeEntaoV1N2(ifStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                }
            } else if (!tipo.isColor()) {
                if (tipo.usesV1() && tipo.usesV2()) {
                    ifStartAddress = this.montador.cIniciaSeEntao(tipo.getOp(), tipo.getV1(), tipo.getV2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(27)) {
                        this.lexico(word, stoken, this.textScanner);
                        elseStartAddress = this.montador.cIniciaSenaoV1V2(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                        this.commandBlock(word, stoken);
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cTerminaSenao(elseStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    } else {
                        this.montador.cTerminaSeEntaoV1V2(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    }
                } else if (tipo.usesV1() && tipo.usesN2()) {
                    ifStartAddress = this.montador.cIniciaSeEntao(tipo.getOp(), tipo.getV1(), tipo.getN2());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    auxTS = new TextScanner(this.textScanner);
                    this.lexico(auxSB, auxST, auxTS);
                    if (auxST.contains(27)) {
                        this.lexico(word, stoken, this.textScanner);
                        elseStartAddress = this.montador.cIniciaSenaoV1N2(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                        this.commandBlock(word, stoken);
                        if (this.errorCode != 0) {
                            return;
                        }
                        this.montador.cTerminaSenao(elseStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    } else {
                        this.montador.cTerminaSeEntaoV1N2(ifStartAddress);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                            return;
                        }
                    }
                }
            } else {
                int cor = this.getToken(this.colorTokenList, tipo.getV2().toString()).getNumber();
                ifStartAddress = this.montador.cIniciaSeEntaoCor(tipo.getOp(), tipo.getV1(), cor);
                if (this.montador.getErrorCode() != 0) {
                    this.setErrorCode(this.montador.getErrorCode(), -1);
                    return;
                }
                this.commandBlock(word, stoken);
                if (this.errorCode != 0) {
                    return;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(27)) {
                    this.lexico(word, stoken, this.textScanner);
                    elseStartAddress = this.montador.cIniciaSenaoCor(ifStartAddress, tipo.getOp());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                    this.commandBlock(word, stoken);
                    if (this.errorCode != 0) {
                        return;
                    }
                    this.montador.cTerminaSenaoCor(elseStartAddress);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                } else {
                    this.montador.cTerminaSeEntaoCor(ifStartAddress, tipo.getOp());
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                        return;
                    }
                }
            }
        }
    }

    public void comCurva(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        double[] duracao = new double[1];
        int[] direcao = new int[1];
        int[] velAux = new int[1];
        StringBuilder type = new StringBuilder();
        this.esperaDirecao(word, stoken, direcao);
        if (this.errorCode != 0) {
            return;
        }
        this.esperaTempo(word, stoken, duracao, type);
        if (this.errorCode != 0) {
            return;
        }
        int stTempo = this.getValueFromStoken(stoken);
        String[] velVarAux = new String[1];
        boolean usaVelocidade = this.esperaVelocidadeOuPorcento(word, stoken, this.textScanner, velAux, velVarAux);
        int vel = velAux[0];
        String velVar = velVarAux[0];
        if (this.errorCode != 0) {
            return;
        }
        if (this.tokenListContains(this.unitTokenList, type.toString())) {
            if (!usaVelocidade) {
                if (this.isSeg(type.toString())) {
                    if (direcao[0] == 5) {
                        this.montador.cCurvaDireitaSegundo(duracao[0], 70);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (direcao[0] == 6) {
                        this.montador.cCurvaEsquerdaSegundo(duracao[0], 70);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                } else {
                    if (direcao[0] == 5) {
                        this.montador.cCurvaDireitaMinuto(duracao[0], 70);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (direcao[0] == 6) {
                        this.montador.cCurvaEsquerdaMinuto(duracao[0], 70);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                }
            } else if (velVar == null) {
                if (this.isSeg(type.toString())) {
                    if (direcao[0] == 5) {
                        this.montador.cCurvaDireitaSegundo(duracao[0], vel);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (direcao[0] == 6) {
                        this.montador.cCurvaEsquerdaSegundo(duracao[0], vel);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                } else {
                    if (direcao[0] == 5) {
                        this.montador.cCurvaDireitaMinuto(duracao[0], vel);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                    if (direcao[0] == 6) {
                        this.montador.cCurvaEsquerdaMinuto(duracao[0], vel);
                        if (this.montador.getErrorCode() != 0) {
                            this.setErrorCode(this.montador.getErrorCode(), -1);
                        }
                        return;
                    }
                }
            } else if (this.isSeg(type.toString())) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaSegundo(duracao[0], velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaSegundo(duracao[0], velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaMinuto(duracao[0], velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaMinuto(duracao[0], velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            }
        } else if (Math.round(duracao[0]) == 255L) {
            if (!usaVelocidade) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaMinuto(255.0, 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaMinuto(255.0, 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (velVar == null) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaMinuto(255.0, vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaMinuto(255.0, vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaMinuto(255.0, velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaMinuto(255.0, velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            }
        } else if (stTempo == 100) {
            this.generateCommandSensorEvent(2, 4);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaCor(cor.getNumber(), 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaCor(cor.getNumber(), 70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (velVar == null) {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaCor(cor.getNumber(), vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaCor(cor.getNumber(), vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else {
                Token cor = this.getToken(this.colorTokenList, type.toString());
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaCor(cor.getNumber(), velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaCor(cor.getNumber(), velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            }
        } else if (stTempo == 48) {
            this.generateCommandSensorEvent(-4, 1);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaObstaculo(70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaObstaculo(70);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (velVar == null) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaObstaculo(vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaObstaculo(vel);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaObstaculo(velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaObstaculo(velVar);
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            }
        } else if (stTempo == 125) {
            this.generateCommandSensorEvent(-6, 2);
            if (this.errorCode != 0) {
                return;
            }
            this.generateCommandSensorEvent(-6, 6);
            if (this.errorCode != 0) {
                return;
            }
            if (!usaVelocidade) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaFaixa(70, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaFaixa(70, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else if (velVar == null) {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaFaixa(vel, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaFaixa(vel, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            } else {
                if (direcao[0] == 5) {
                    this.montador.cCurvaDireitaFaixa(velVar, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
                if (direcao[0] == 6) {
                    this.montador.cCurvaEsquerdaFaixa(velVar, (int)Math.round(duracao[0]));
                    if (this.montador.getErrorCode() != 0) {
                        this.setErrorCode(this.montador.getErrorCode(), -1);
                    }
                    return;
                }
            }
        }
    }

    public void comGiro(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.generateCommandSensorEvent(128, 13);
        if (this.errorCode != 0) {
            return;
        }
        this.generateCommandSensorEvent(128, 14);
        if (this.errorCode != 0) {
            return;
        }
        int[] direcao = new int[1];
        this.esperaDirecao(word, stoken, direcao);
        if (this.errorCode != 0) {
            return;
        }
        if (direcao[0] == 5) {
            this.comGiroDireita(word, stoken);
        } else if (direcao[0] == 6) {
            this.comGiroEsquerda(word, stoken);
        }
    }

    private void esperaSom(StringBuilder word, ArrayList<Integer> stoken, int[] som) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(123)) {
            this.setErrorCode(125);
        }
    }

    private void esperaNota(StringBuilder word, ArrayList<Integer> stoken, int[] som) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!this.isNote(stoken)) {
            this.setErrorCode(27);
        }
    }

    private void esperaSaida(StringBuilder word, ArrayList<Integer> stoken, int[] num) throws IOException {
        this.esperaSaida(word, stoken, num, false);
    }

    private void esperaSaida(StringBuilder word, ArrayList<Integer> stoken, int[] num, boolean isOff) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(104) && !stoken.contains(129)) {
            if (!isOff) {
                this.setErrorCode(29);
            } else {
                this.setErrorCode(75);
            }
            return;
        }
        Token out = null;
        out = stoken.contains(104) ? this.getToken(this.outTokenList, word.toString()) : this.getToken(this.eventTokenList, word.toString());
        num[0] = out.getNumber();
    }

    private void esperaSaidaSensorDistancia(StringBuilder word, ArrayList<Integer> stoken, int[] num) throws IOException {
        this.esperaSaidaSensorDistancia(word, stoken, num, false);
    }

    private void esperaSaidaSensorDistancia(StringBuilder word, ArrayList<Integer> stoken, int[] num, boolean isOff) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(129)) {
            this.setErrorCode(87);
            return;
        }
        Token out = this.getToken(this.sensorDistanceTokenList, word.toString());
        num[0] = out.getNumber();
    }

    private void esperaLed(StringBuilder word, ArrayList<Integer> stoken, int[] num, boolean turnOff) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(127)) {
            if (!turnOff) {
                this.setErrorCode(82);
            } else {
                this.setErrorCode(83);
            }
            return;
        }
        Token led = this.getToken(this.ledTokenList, word.toString());
        num[0] = led.getNumber();
    }

    private void esperaEvento(StringBuilder word, ArrayList<Integer> stoken, int[] num) throws IOException {
        this.esperaEvento(word, stoken, num, false);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void esperaEvento(StringBuilder word, ArrayList<Integer> stoken, int[] num, boolean isOff) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(129)) {
            if (this.tokenListContains(this.eventTokenList, word.toString())) {
                Token out = this.getToken(this.eventTokenList, word.toString());
                num[0] = out.getNumber();
                return;
            }
            if (!isOff) {
                this.setErrorCode(16);
                return;
            }
            this.setErrorCode(74);
            return;
        }
        if (!isOff) {
            this.setErrorCode(16);
            return;
        }
        this.setErrorCode(74);
    }

    private void esperaMotor(StringBuilder word, ArrayList<Integer> stoken, int[] motor, int[] sentido, int[] velocidade, String[] velocidadeVar) throws IOException {
        velocidade[0] = -1;
        velocidadeVar[0] = null;
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(35) && !stoken.contains(36)) {
            this.setErrorCode(78);
            return;
        }
        motor[0] = stoken.contains(35) ? 1 : 2;
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(18)) {
            this.lexico(word, stoken, this.textScanner);
            velocidade[0] = 0;
            sentido[0] = 1;
            return;
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(7) && !stoken.contains(8)) {
            this.setErrorCode(96);
            return;
        }
        sentido[0] = stoken.contains(7) ? 1 : -1;
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(108)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            int vel = Integer.valueOf(word.toString());
            if (vel < 0 || vel > 100) {
                this.setErrorCode(80);
                return;
            }
            velocidade[0] = vel;
            this.lexico(word, stoken, this.textScanner);
            if (!stoken.contains(132)) {
                this.setErrorCode(126);
                return;
            }
        } else if (auxST.contains(106)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            velocidadeVar[0] = word.toString();
            this.lexico(word, stoken, this.textScanner);
            if (!stoken.contains(132)) {
                this.setErrorCode(126);
                return;
            }
        }
    }

    private void esperaAngulo(StringBuilder word, ArrayList<Integer> stoken, int[] angulo, String[] var) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(108) && !stoken.contains(106)) {
            this.setErrorCode(41);
            return;
        }
        if (stoken.contains(108)) {
            angulo[0] = Integer.valueOf(word.toString());
            var[0] = null;
            if (angulo[0] < 0 || angulo[0] > 180) {
                this.setErrorCode(42);
                return;
            }
        }
        if (stoken.contains(106)) {
            angulo[0] = -1;
            var[0] = word.toString();
            StringBuilder auxSB = new StringBuilder();
            ArrayList<Integer> auxST = new ArrayList<Integer>();
            TextScanner auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(68) || auxST.contains(67)) {
                this.lexico(word, stoken, this.textScanner);
                if (this.errorCode != 0) {
                    return;
                }
            }
            return;
        }
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(68) || auxST.contains(67)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (angulo[0] >= 0) {
                if (angulo[0] < 2) {
                    if (!stoken.contains(67)) {
                        this.setErrorCode(155);
                        return;
                    }
                } else if (!stoken.contains(68)) {
                    this.setErrorCode(154);
                    return;
                }
            }
        }
    }

    private void esperaDirecao(StringBuilder word, ArrayList<Integer> stoken, int[] direcao) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(5) && !stoken.contains(6)) {
            this.setErrorCode(18);
            return;
        }
        direcao[0] = this.getValueFromStoken(stoken);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private ExpressionType esperaExpressao(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        int IDSensor;
        double auxMultiplier;
        TextScanner auxTS;
        String v1 = "";
        String v2 = "";
        int token1 = 0;
        int token2 = 0;
        int valor1 = 0;
        int valor2 = 0;
        double valor1r = 0.0;
        double valor2r = 0.0;
        String unit1 = "";
        String unit2 = "";
        boolean opSwitched = false;
        String op = "";
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return null;
        }
        if (!(stoken.contains(33) || stoken.contains(34) || stoken.contains(109) || stoken.contains(108) || stoken.contains(106) || stoken.contains(129) || stoken.contains(100) || stoken.contains(52))) {
            this.setErrorCode(51);
            return null;
        }
        token1 = this.getValueFromStoken(stoken);
        if (token1 == 52) {
            this.lexico(word, stoken, this.textScanner);
            if (!stoken.contains(100)) {
                this.setErrorCode(24);
                return null;
            }
            token1 = this.getValueFromStoken(stoken);
        }
        if (token1 == 108) {
            try {
                Short.valueOf(word.toString());
            }
            catch (Exception e) {
                token1 = 109;
            }
        }
        if (token1 == 108) {
            valor1 = Integer.valueOf(word.toString());
            try {
                Short.valueOf(word.toString());
            }
            catch (Exception e) {
                token1 = 109;
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(131)) {
                this.lexico(word, stoken, this.textScanner);
                double auxMultiplier2 = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                token1 = 109;
                valor1r = valor1;
                valor1r *= auxMultiplier2;
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(130)) {
                this.lexico(word, stoken, this.textScanner);
                unit1 = word.toString();
                if (token1 != 109) {
                    token1 = 109;
                    valor1r = valor1;
                }
                int IDSensor2 = this.getToken(this.measureUnitTokenList, unit1).getNumber();
                op = this.invertOperators(op, IDSensor2);
                opSwitched = true;
            }
            if (token1 == 109 && unit1.equals("")) {
                this.setErrorCode(99);
                return null;
            }
        } else if (token1 == 109) {
            valor1r = Double.valueOf(word.toString());
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (auxST.contains(131)) {
                this.lexico(word, stoken, this.textScanner);
                double auxMultiplier3 = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                valor1r *= auxMultiplier3;
            }
            auxTS = new TextScanner(this.textScanner);
            this.lexico(auxSB, auxST, auxTS);
            if (!auxST.contains(130)) {
                this.setErrorCode(99);
                return null;
            }
            this.lexico(word, stoken, this.textScanner);
            unit1 = word.toString();
            int IDSensor3 = this.getToken(this.measureUnitTokenList, unit1).getNumber();
            op = this.invertOperators(op, IDSensor3);
            opSwitched = true;
        } else {
            v1 = new String(word.toString());
        }
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return null;
        }
        if (!this.isRelationalOp(stoken) && !stoken.contains(57)) {
            this.setErrorCode(7);
            return null;
        }
        if (this.isRelationalOp(stoken)) {
            op = new String(word.toString());
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return null;
            }
            if (!(stoken.contains(33) || stoken.contains(34) || stoken.contains(109) || stoken.contains(108) || stoken.contains(106) || stoken.contains(129) || stoken.contains(100) || stoken.contains(52))) {
                this.setErrorCode(53);
                return null;
            }
            token2 = this.getValueFromStoken(stoken);
            if (token2 == 52) {
                this.lexico(word, stoken, this.textScanner);
                if (!stoken.contains(100)) {
                    this.setErrorCode(24);
                    return null;
                }
                token2 = this.getValueFromStoken(stoken);
            }
            if (token2 == 108) {
                try {
                    Short.valueOf(word.toString());
                }
                catch (Exception e) {
                    token2 = 109;
                }
            }
            if (token2 == 108) {
                valor2 = Integer.valueOf(word.toString());
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(131)) {
                    this.lexico(word, stoken, this.textScanner);
                    double auxMultiplier4 = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                    token2 = 109;
                    valor2r = valor2;
                    valor2r *= auxMultiplier4;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(130)) {
                    this.lexico(word, stoken, this.textScanner);
                    unit2 = word.toString();
                    if (token2 != 109) {
                        token2 = 109;
                        valor2r = valor2;
                    }
                    if (!opSwitched) {
                        int IDSensor4 = this.getToken(this.measureUnitTokenList, unit2).getNumber();
                        op = this.invertOperators(op, IDSensor4);
                    }
                }
                if (token2 == 109 && unit2.equals("")) {
                    this.setErrorCode(99);
                    return null;
                }
            } else if (token2 == 109) {
                valor2r = Double.valueOf(word.toString());
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (auxST.contains(131)) {
                    this.lexico(word, stoken, this.textScanner);
                    double auxMultiplier5 = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
                    valor2r *= auxMultiplier5;
                }
                auxTS = new TextScanner(this.textScanner);
                this.lexico(auxSB, auxST, auxTS);
                if (!auxST.contains(130)) {
                    this.setErrorCode(99);
                    return null;
                }
                this.lexico(word, stoken, this.textScanner);
                unit2 = word.toString();
                if (!opSwitched) {
                    int IDSensor5 = this.getToken(this.measureUnitTokenList, unit2).getNumber();
                    op = this.invertOperators(op, IDSensor5);
                }
            } else {
                v2 = new String(word.toString());
            }
            if (token1 == 109 && token2 == 106) {
                op = this.invertRelationalOperator(op);
                return new ExpressionType(true, false, false, true, v2, "", 0.0, valor1r, op, unit1);
            }
            if (token2 == 109 && token1 == 106) {
                return new ExpressionType(true, false, false, true, v1, "", 0.0, valor2r, op, unit2);
            }
            if (token1 == 109) {
                if (token2 != 129) {
                    this.setErrorCode(54);
                    return null;
                }
                int IDSensor6 = this.getToken(this.measureUnitTokenList, unit1).getNumber();
                int porta = this.getToken(this.sensorTokenList, v2).getNumber();
                this.generateCommandSensorEvent(IDSensor6, porta);
                if (this.errorCode != 0) {
                    return null;
                }
                op = this.invertRelationalOperator(op);
                return new ExpressionType(true, false, false, true, v2, "", 0.0, valor1r, op, unit1);
            }
            if (token2 == 109) {
                if (token1 != 129) {
                    this.setErrorCode(54);
                    return null;
                }
                int IDSensor7 = this.getToken(this.measureUnitTokenList, unit2).getNumber();
                int porta = this.getToken(this.sensorTokenList, v1).getNumber();
                this.generateCommandSensorEvent(IDSensor7, porta);
                if (this.errorCode != 0) {
                    return null;
                }
                return new ExpressionType(true, false, false, true, v1, "", 0.0, valor2r, op, unit2);
            }
            if (token1 == 108 && token2 == 108) {
                boolean bTrue = false;
                if (op.equals("=")) {
                    bTrue = valor1 == valor2;
                } else if (op.equals(">")) {
                    bTrue = valor1 > valor2;
                } else if (op.equals("<")) {
                    bTrue = valor1 < valor2;
                } else if (op.equals(">=")) {
                    bTrue = valor1 >= valor2;
                } else if (op.equals("<=")) {
                    bTrue = valor1 <= valor2;
                } else if (op.equals("<>")) {
                    bTrue = valor1 != valor2;
                }
                if (bTrue) {
                    return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "=", false);
                }
                return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "<>", false);
            }
            if (token1 == 100 && token2 == 100) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                boolean bTrue = false;
                int cor1 = this.getToken(this.colorTokenList, v1.toString()).getNumber();
                int cor2 = this.getToken(this.colorTokenList, v2.toString()).getNumber();
                if (op.equals("=")) {
                    bTrue = cor1 == cor2;
                } else if (op.equals("<>")) {
                    bTrue = cor1 != cor2;
                }
                if (bTrue) {
                    return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "=", false);
                }
                return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "<>", false);
            }
            if (token1 == 100) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token2 != 106 && token2 != 129) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token2 == 129) {
                    int porta = this.getToken(this.sensorTokenList, v2).getNumber();
                    this.generateCommandSensorEvent(2, porta);
                    if (this.errorCode != 0) {
                        return null;
                    }
                }
                return new ExpressionType(true, true, false, false, v2, v1, 0, 0, op, true);
            }
            if (token2 == 100) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token1 != 106 && token1 != 129) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token1 == 129) {
                    int porta = this.getToken(this.sensorTokenList, v1).getNumber();
                    this.generateCommandSensorEvent(2, porta);
                    if (this.errorCode != 0) {
                        return null;
                    }
                }
                return new ExpressionType(true, true, false, false, v1, v2, 0, 0, op, true);
            }
            if (!(token1 != 106 && token1 != 129 || token2 != 106 && token2 != 129)) {
                return new ExpressionType(true, true, false, false, v1, v2, 0, 0, op, false);
            }
            if (!(token1 != 106 && token1 != 129 || token2 != 33 && token2 != 34)) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token1 == 129) {
                    int porta = this.getToken(this.sensorTokenList, v1).getNumber();
                    this.generateCommandSensorEvent(-4, porta);
                    if (this.errorCode != 0) {
                        return null;
                    }
                }
                if (token2 == 33) {
                    return new ExpressionType(true, false, false, true, v1, "", 0, 1, op, false);
                }
                if (token2 == 34) {
                    return new ExpressionType(true, false, false, true, v1, "", 0, 0, op, false);
                }
            }
            if (!(token1 != 33 && token1 != 34 || token2 != 106 && token2 != 129)) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                if (token2 == 129) {
                    int porta = this.getToken(this.sensorTokenList, v2).getNumber();
                    this.generateCommandSensorEvent(-4, porta);
                    if (this.errorCode != 0) {
                        return null;
                    }
                }
                if (token1 == 33) {
                    return new ExpressionType(true, false, false, true, v2, "", 0, 1, op, false);
                }
                if (token1 == 34) {
                    return new ExpressionType(true, false, false, true, v2, "", 0, 0, op, false);
                }
            }
            if (!(token1 != 33 && token1 != 34 || token2 != 33 && token2 != 34)) {
                if (!op.equals("=") && !op.equals("<>")) {
                    this.setErrorCode(54);
                    return null;
                }
                boolean bTrue = false;
                bTrue = token1 == token2 ? op.equals("=") : op.equals("<>");
                if (bTrue) {
                    return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "=", false);
                }
                return new ExpressionType(true, true, false, false, "nulo", "nulo", 0, 0, "<>", false);
            }
            if (token1 == 108) {
                if (token2 != 106 && token2 != 129) {
                    this.setErrorCode(54);
                    return null;
                }
                op = this.invertRelationalOperator(op);
                if (token2 == 129) {
                    if (valor1 < 0 || valor1 > 1023) {
                        this.setErrorCode(138);
                        return null;
                    }
                    int porta = this.getToken(this.sensorTokenList, v2).getNumber();
                    this.generateCommandSensorEvent(20, porta);
                    if (this.errorCode != 0) {
                        return null;
                    }
                }
                return new ExpressionType(true, false, false, true, v2, "", 0, valor1, op, false);
            }
            if (token2 != 108) {
                this.setErrorCode(54);
                return null;
            }
            if (token1 != 106 && token1 != 129) {
                this.setErrorCode(54);
                return null;
            }
            if (token1 == 129) {
                if (valor2 < 0 || valor2 > 1023) {
                    this.setErrorCode(138);
                    return null;
                }
                int porta = this.getToken(this.sensorTokenList, v1).getNumber();
                this.generateCommandSensorEvent(20, porta);
                if (this.errorCode != 0) {
                    return null;
                }
            }
            return new ExpressionType(true, false, false, true, v1, "", 0, valor2, op, false);
        }
        if (token1 != 106 && token1 != 129) {
            this.setErrorCode(117);
            return null;
        }
        double iInicio = 0.0;
        double iFim = 0.0;
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(108) && !stoken.contains(109)) {
            this.setErrorCode(114);
            return null;
        }
        int tInicio = this.getValueFromStoken(stoken);
        iInicio = Double.valueOf(word.toString());
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(131)) {
            this.lexico(word, stoken, this.textScanner);
            auxMultiplier = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
            iInicio *= auxMultiplier;
        }
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(130)) {
            this.lexico(word, stoken, this.textScanner);
            unit1 = word.toString();
        }
        if (tInicio == 109 && unit1.equals("")) {
            this.setErrorCode(99);
            return null;
        }
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(58)) {
            this.setErrorCode(116);
            return null;
        }
        this.lexico(word, stoken, this.textScanner);
        if (!stoken.contains(108) && !stoken.contains(109)) {
            this.setErrorCode(115);
            return null;
        }
        int tFim = this.getValueFromStoken(stoken);
        iFim = Double.valueOf(word.toString());
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(131)) {
            this.lexico(word, stoken, this.textScanner);
            auxMultiplier = this.getToken(this.multiplierTokenList, word.toString()).getNumber();
            iFim *= auxMultiplier;
        }
        auxTS = new TextScanner(this.textScanner);
        this.lexico(auxSB, auxST, auxTS);
        if (auxST.contains(130)) {
            this.lexico(word, stoken, this.textScanner);
            unit2 = word.toString();
        }
        if (!unit1.equals(unit2)) {
            this.setErrorCode(146);
            return null;
        }
        if (tFim == 109 && unit2.equals("")) {
            this.setErrorCode(99);
            return null;
        }
        if (token1 == 129 && !unit1.equals("")) {
            int IDSensor8 = this.getToken(this.measureUnitTokenList, unit1).getNumber();
            int porta = this.getToken(this.sensorTokenList, v1).getNumber();
            this.generateCommandSensorEvent(IDSensor8, porta);
            if (this.errorCode != 0) {
                return null;
            }
        }
        if (unit1 != "" && ((IDSensor = this.getToken(this.measureUnitTokenList, unit1).getNumber()) == 6 || IDSensor == 7)) {
            double aux = iInicio;
            iInicio = iFim;
            iFim = aux;
        }
        if (iFim <= iInicio) {
            this.setErrorCode(153);
            return null;
        }
        if (token1 == 106 && unit1.equals("")) {
            return new ExpressionType(v1, (int)Math.round(iInicio), (int)Math.round(iFim), false);
        }
        if (token1 == 106) {
            return new ExpressionType(v1, iInicio, iFim, false, unit1);
        }
        return new ExpressionType(v1, iInicio, iFim, true, unit1);
    }

    private void esperaTempoObjeto(StringBuilder word, ArrayList<Integer> stoken, double[] duracao, StringBuilder tipo) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(108) || stoken.contains(109)) {
            int auxTipoNum = this.getValueFromStoken(stoken);
            Double value = this.oneDecimal(Double.valueOf(word.toString()));
            if (word.toString().startsWith("0.0")) {
                this.setErrorCode(21);
                return;
            }
            duracao[0] = value;
            if (value > 120.0) {
                this.setErrorCode(20);
                return;
            }
            if (value <= 0.0) {
                this.setErrorCode(21);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(107)) {
                this.setErrorCode(22);
                return;
            }
            if (word.toString().equalsIgnoreCase("min") && auxTipoNum == 109) {
                this.setErrorCode(45);
                return;
            }
            tipo.append((CharSequence)word);
            return;
        }
        if (stoken.contains(16)) {
            duracao[0] = 255.0;
            return;
        }
        if (stoken.contains(15)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (stoken.contains(52)) {
                this.lexico(word, stoken, this.textScanner);
                if (!stoken.contains(100)) {
                    this.setErrorCode(24);
                    return;
                }
                tipo.append((CharSequence)word);
                return;
            }
            if (stoken.contains(100) || stoken.contains(51)) {
                tipo.append((CharSequence)word);
                return;
            }
        }
        this.setErrorCode(19);
    }

    private void esperaTempo(StringBuilder word, ArrayList<Integer> stoken, double[] duracao, StringBuilder tipo) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(108) || stoken.contains(109)) {
            int auxTipoNum = this.getValueFromStoken(stoken);
            Double value = this.oneDecimal(Double.valueOf(word.toString()));
            if (word.toString().startsWith("0.0")) {
                this.setErrorCode(21);
                return;
            }
            duracao[0] = value;
            if (value > 120.0) {
                this.setErrorCode(20);
                return;
            }
            if (value <= 0.0) {
                this.setErrorCode(21);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(107)) {
                this.setErrorCode(22);
                return;
            }
            if (word.toString().equalsIgnoreCase("min") && auxTipoNum == 109) {
                this.setErrorCode(45);
                return;
            }
            tipo.append((CharSequence)word);
            return;
        }
        if (stoken.contains(16)) {
            duracao[0] = 255.0;
            return;
        }
        if (stoken.contains(15)) {
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (stoken.contains(100) || stoken.contains(48) || stoken.contains(14) && this.language == 2 || stoken.contains(14) && this.language == 1 || stoken.contains(125) && this.language == 3 || stoken.contains(12) && this.language == 3) {
                if (stoken.contains(125) || stoken.contains(12)) {
                    this.setStokenValue(stoken, 125);
                    Token qualFaixa = this.getToken(this.stripTokenList, word.toString());
                    duracao[0] = qualFaixa.getNumber();
                    this.lexico(word, stoken, this.textScanner);
                    if (this.errorCode != 0) {
                        return;
                    }
                    if (!stoken.contains(14)) {
                        this.setErrorCode(152);
                        return;
                    }
                    tipo.append((CharSequence)word);
                    this.setStokenValue(stoken, 125);
                    return;
                }
                tipo.append((CharSequence)word);
                if (stoken.contains(14)) {
                    this.lexico(word, stoken, this.textScanner);
                    if (this.errorCode != 0) {
                        return;
                    }
                    if (this.isLineColor(stoken)) {
                        Token qualFaixa = this.getToken(this.stripTokenList, word.toString());
                        duracao[0] = qualFaixa.getNumber();
                    } else {
                        this.setErrorCode(25);
                    }
                }
                return;
            }
            if (stoken.contains(52)) {
                this.lexico(word, stoken, this.textScanner);
                if (this.errorCode != 0) {
                    return;
                }
                if (stoken.contains(100)) {
                    tipo.append((CharSequence)word);
                } else {
                    this.setErrorCode(24);
                }
                return;
            }
        }
        this.setErrorCode(19);
    }

    private void esperaVelocidade(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts, boolean[] usaVelocidade) throws IOException {
        this.lexico(word, stoken, ts);
        usaVelocidade[0] = false;
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(37) || stoken.contains(38) || stoken.contains(39) || stoken.contains(40) || stoken.contains(41)) {
            usaVelocidade[0] = true;
        }
    }

    private boolean esperaVelocidadeOuPorcento(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts, int[] vel, String[] velVar) throws IOException {
        StringBuilder auxSB = new StringBuilder();
        ArrayList<Integer> auxST = new ArrayList<Integer>();
        TextScanner auxTS = new TextScanner(this.textScanner);
        velVar[0] = null;
        this.lexico(auxSB, auxST, auxTS);
        boolean usaVelocidade = false;
        if (this.errorCode != 0) {
            return usaVelocidade;
        }
        if (auxST.contains(37) || auxST.contains(38) || auxST.contains(39) || auxST.contains(40) || auxST.contains(41) || auxST.contains(106)) {
            usaVelocidade = true;
            if (auxST.contains(106)) {
                velVar[0] = auxSB.toString();
                vel[0] = -1;
                this.lexico(auxSB, auxST, auxTS);
                if (!auxST.contains(132)) {
                    usaVelocidade = false;
                    return usaVelocidade;
                }
                this.lexico(word, stoken, this.textScanner);
                this.lexico(word, stoken, this.textScanner);
                return usaVelocidade;
            }
            this.lexico(word, stoken, this.textScanner);
            switch (this.getValueFromStoken(stoken)) {
                case 37: 
                case 38: {
                    vel[0] = 20;
                    break;
                }
                case 39: {
                    vel[0] = 70;
                    break;
                }
                case 40: 
                case 41: {
                    vel[0] = 100;
                }
            }
        } else if (auxST.contains(108)) {
            usaVelocidade = true;
            this.lexico(word, stoken, this.textScanner);
            vel[0] = Integer.valueOf(word.toString());
            if (vel[0] < 0 || vel[0] > 100) {
                this.setErrorCode(80);
                return usaVelocidade;
            }
            this.lexico(word, stoken, this.textScanner);
            if (!stoken.contains(132)) {
                this.setErrorCode(126);
                return usaVelocidade;
            }
        }
        return usaVelocidade;
    }

    private void esperaTempoSom(StringBuilder word, ArrayList<Integer> stoken, double[] tempo) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (stoken.contains(108) || stoken.contains(109)) {
            double value = this.oneDecimal(Double.valueOf(word.toString()));
            if (word.toString().startsWith("0.0")) {
                this.setErrorCode(21);
                return;
            }
            tempo[0] = value;
            if (value > 25.0) {
                this.setErrorCode(76);
                return;
            }
            if (value <= 0.0) {
                this.setErrorCode(21);
                return;
            }
            this.lexico(word, stoken, this.textScanner);
            if (this.errorCode != 0) {
                return;
            }
            if (!stoken.contains(107)) {
                this.setErrorCode(22);
                return;
            }
            if (word.toString().equalsIgnoreCase("min")) {
                this.setErrorCode(32);
                return;
            }
            return;
        }
        this.setErrorCode(19);
    }

    public void lexico(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        this.lexico(word, stoken, ts, false);
    }

    public void lexico(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts, boolean getAnyChars) throws IOException {
        word.setLength(0);
        this.setStokenValue(stoken, -1);
        if (ts.eof()) {
            this.endLoop = true;
        }
        if (this.endLoop) {
            this.setStokenValue(stoken, -1);
            return;
        }
        this.setErrorCode(0);
        boolean validChar = false;
        boolean openComment = false;
        char c = ts.getCurrentChar();
        do {
            if (c == '{') {
                openComment = true;
                while (c != '}' && !ts.eof()) {
                    ts.readChar(true);
                    c = ts.getCurrentChar();
                }
                if (c == '}') {
                    openComment = false;
                    ts.readChar(true);
                    c = ts.getCurrentChar();
                }
            }
            if (c == '#') {
                ts.readCurrentLine();
                c = !ts.eol() ? ts.getCurrentChar() : (char)'\u0000';
            }
            if (whitechars.indexOf(c) < 0) continue;
            ts.readChar(true);
            c = ts.getCurrentChar();
        } while ((whitechars.indexOf(c) >= 0 || c == '{' || c == '#') && !ts.eof());
        if (ts.eof()) {
            this.endLoop = true;
            if (openComment) {
                this.setErrorCode(2);
            } else {
                this.setStokenValue(stoken, -1);
            }
            if (ts != this.textScanner) {
                this.errorCode = 0;
                this.endLoop = false;
            }
            return;
        }
        if (getAnyChars) {
            this.identifyAllChars(word, stoken, ts);
            return;
        }
        boolean bl = validChar = this.isLegalDigit(c) || c == '\"';
        if (!validChar) {
            c = ts.readChar(true);
            this.setErrorCode(3);
            return;
        }
        char c2 = ts.getNextChar();
        if (this.isLegalLetter(c)) {
            this.identifyWord(word, stoken, ts);
        } else if (this.isNumber(c) || c == '-' && this.isNumber(c2)) {
            this.identifyNumber(word, stoken, ts);
        } else if (c == '>' || c == '<' || c == '=' || c == '%') {
            this.identifyOperator(word, stoken, ts);
        } else if (c == '+' || c == '-' || c == '*' || c == '/') {
            this.identifyArithmeticOperator(word, stoken, ts);
        } else if (c == ']' || c == '[') {
            this.identifyBeginEnd(word, stoken, ts);
        } else if (c == '\"') {
            this.identifyString(word, stoken, ts);
        } else {
            this.setErrorCode(3);
        }
        if (ts.eof()) {
            this.endLoop = true;
        }
        if (ts != this.textScanner) {
            if (this.errorCode != 9) {
                this.errorCode = 0;
            }
            this.endLoop = false;
        }
    }

    private void identifyString(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        ts.readChar(false);
        char c = ts.getCurrentChar();
        while (c != '\"' && !ts.eol()) {
            word.append(c);
            ts.readChar(false);
            c = ts.getCurrentChar();
        }
        if (c != '\"') {
            this.setErrorCode(6);
            return;
        }
        ts.readChar(false);
        this.setStokenValue(stoken, 105);
    }

    /*
     * Unable to fully structure code
     */
    private void identifyNumber(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        this.setStokenValue(stoken, 108);
        char c = ts.getCurrentChar();
        word.append(c);
        ts.readChar(false);
        c = ts.getCurrentChar();
        while (this.isNumber(c)) {
            word.append(c);
            if (ts.eol()) break;
            ts.readChar(false);
            c = ts.getCurrentChar();
        }
        word.replace(0, word.length(), word.toString().toUpperCase());
        String auxWord = new String(word);
        try {
            Short.valueOf(auxWord);
        }
        catch (NumberFormatException e) {
            try {
                Double.valueOf(auxWord);
                this.setStokenValue(stoken, 109);
                word.replace(0, word.length(), String.valueOf(auxWord) + ".0");
                double d = Double.valueOf(word.toString());
                word.replace(0, word.length(), String.valueOf(d));
            }
            catch (NumberFormatException e2) {
                this.setErrorCode(4);
                return;
            }
        }
        if (this.floatPointSeparator.indexOf(c) < 0) {
            return;
        }
        word.setLength(0);
        ts.readChar(false);
        c = ts.getCurrentChar();
        if (!this.isNumber(c)) {
            this.setErrorCode(5);
            return;
        }
        while (this.isNumber(c)) {
            word.append(c);
            if (ts.eol()) break;
            ts.readChar(false);
            c = ts.getCurrentChar();
        }
        this.setStokenValue(stoken, 109);
        word.replace(0, word.length(), String.valueOf(auxWord) + "." + word);
        double d = Double.valueOf(word.toString());
        word.replace(0, word.length(), String.valueOf(d));
    }

    private void identifyOperator(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        String auxWord;
        char c = ts.getCurrentChar();
        word.append(c);
        ts.readChar(false);
        char c2 = ts.getCurrentChar();
        if (c == '>' && c2 == '=' || c == '<' && c2 == '=' || c == '<' && c2 == '>') {
            word.append(c2);
            ts.readChar(false);
        }
        if ((auxWord = new String(word)).equals("=")) {
            this.setStokenValue(stoken, 110);
            return;
        }
        if (auxWord.equals(">")) {
            this.setStokenValue(stoken, 111);
            return;
        }
        if (auxWord.equals("<")) {
            this.setStokenValue(stoken, 112);
            return;
        }
        if (auxWord.equals(">=")) {
            this.setStokenValue(stoken, 113);
            return;
        }
        if (auxWord.equals("<=")) {
            this.setStokenValue(stoken, 114);
            return;
        }
        if (auxWord.equals("<>")) {
            this.setStokenValue(stoken, 115);
            return;
        }
        if (auxWord.equals("%")) {
            this.setStokenValue(stoken, 132);
            return;
        }
    }

    private void identifyArithmeticOperator(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        char c = ts.getCurrentChar();
        word.append(c);
        ts.readChar(false);
        switch (c) {
            case '+': {
                this.setStokenValue(stoken, 116);
                break;
            }
            case '-': {
                this.setStokenValue(stoken, 117);
                break;
            }
            case '*': {
                this.setStokenValue(stoken, 118);
                break;
            }
            case '/': {
                this.setStokenValue(stoken, 119);
            }
        }
    }

    private void identifyBeginEnd(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        char c = ts.getCurrentChar();
        word.append(c);
        ts.readChar(false);
        if (c == '[') {
            this.setStokenValue(stoken, 120);
        } else if (c == ']') {
            this.setStokenValue(stoken, 121);
        }
    }

    private void identifyWord(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        char c = ts.getCurrentChar();
        while (this.isLegalLetter(c) || this.isNumber(c)) {
            word.append(c);
            ts.readChar(false);
            c = ts.getCurrentChar();
        }
        word.replace(0, word.length(), word.toString().toUpperCase());
        String auxWord = new String(word);
        if (auxWord.equalsIgnoreCase("Turn") && this.language == 3) {
            StringBuilder auxCurrentWord = new StringBuilder();
            TextScanner auxTS = new TextScanner(ts);
            auxCurrentWord.setLength(0);
            this.skipSpaces(auxTS);
            c = auxTS.getCurrentChar();
            while (this.isLegalLetter(c) || this.isNumber(c)) {
                auxCurrentWord.append(c);
                auxTS.readChar(false);
                c = auxTS.getCurrentChar();
            }
            if (auxCurrentWord.toString().equalsIgnoreCase("on") || auxCurrentWord.toString().equalsIgnoreCase("off")) {
                auxCurrentWord.setLength(0);
                this.skipSpaces(ts);
                c = ts.getCurrentChar();
                while (this.isLegalLetter(c) || this.isNumber(c)) {
                    auxCurrentWord.append(c);
                    ts.readChar(false);
                    c = ts.getCurrentChar();
                }
                word.append(" " + auxCurrentWord.toString().toUpperCase());
                if (word.toString().equalsIgnoreCase("turn on")) {
                    this.addStokenValue(stoken, 19);
                    return;
                }
                if (word.toString().equalsIgnoreCase("turn off")) {
                    this.addStokenValue(stoken, 20);
                    return;
                }
            }
        }
        int i = 1;
        while (i <= 68) {
            if (this.tokenList.get(i).getMainToken().equalsIgnoreCase(auxWord)) {
                if (stoken.contains(2) && i == 3) break;
                this.addStokenValue(stoken, i);
                String auxRest = new String(this.tokenList.get(i).getEndingTokens());
                if (auxRest.length() > 0) {
                    StringTokenizer auxTokens = new StringTokenizer(auxRest);
                    StringBuilder auxCurrentWord = new StringBuilder();
                    int correctTokens = 0;
                    int totalTokens = auxTokens.countTokens();
                    boolean endWhile = false;
                    while (!endWhile) {
                        if (!auxTokens.hasMoreTokens()) {
                            endWhile = true;
                            continue;
                        }
                        String auxCurrentToken = new String(auxTokens.nextToken());
                        auxCurrentWord.setLength(0);
                        this.skipSpaces(ts);
                        c = ts.getCurrentChar();
                        while (this.isLegalLetter(c) || this.isNumber(c)) {
                            auxCurrentWord.append(c);
                            ts.readChar(false);
                            c = ts.getCurrentChar();
                        }
                        if (!auxCurrentToken.equalsIgnoreCase(auxCurrentWord.toString())) {
                            endWhile = true;
                            continue;
                        }
                        word.append(" " + auxCurrentToken.toUpperCase());
                        ++correctTokens;
                    }
                    if (correctTokens != totalTokens) {
                        this.currentCommandLine = this.textScanner.getCurrentLineNumber();
                        this.setErrorCode(9);
                        this.setErrorRestToken(new String(this.restToken(word.toString(), this.getValueFromStoken(stoken))));
                    }
                }
            }
            ++i;
        }
        if (this.tokenListContains(this.outTokenList, auxWord)) {
            this.addStokenValue(stoken, 104);
            return;
        }
        if (this.tokenListContains(this.sensorTokenList, auxWord)) {
            this.addStokenValue(stoken, 129);
            return;
        }
        if (this.tokenListContains(this.servoTokenList, auxWord)) {
            this.addStokenValue(stoken, 128);
            return;
        }
        if (this.tokenListContains(this.ledTokenList, auxWord)) {
            this.addStokenValue(stoken, 127);
            return;
        }
        if (this.tokenListContains(this.unitTokenList, auxWord)) {
            this.addStokenValue(stoken, 107);
            return;
        }
        if (this.tokenListContains(this.measureUnitTokenList, auxWord)) {
            this.addStokenValue(stoken, 130);
            return;
        }
        if (this.tokenListContains(this.multiplierTokenList, auxWord)) {
            this.addStokenValue(stoken, 131);
            return;
        }
        if (this.tokenListContains(this.noteTokenList, auxWord)) {
            this.addStokenValue(stoken, 122);
            return;
        }
        if (this.tokenListContains(this.soundEffectTokenList, auxWord)) {
            this.addStokenValue(stoken, 123);
            return;
        }
        if (this.tokenListContains(this.colorTokenList, auxWord)) {
            this.addStokenValue(stoken, 100);
            return;
        }
        if (this.tokenListContains(this.stripTokenList, auxWord)) {
            this.addStokenValue(stoken, 125);
            return;
        }
        if (this.declaredVars.contains(auxWord)) {
            this.addStokenValue(stoken, 106);
            return;
        }
        if (this.declaredDefs.containsKey(auxWord)) {
            this.addStokenValue(stoken, 133);
            return;
        }
        if (this.declaredProcs.containsKey(auxWord)) {
            if (this.declaredProcs.get(auxWord) == 1) {
                this.addStokenValue(stoken, 101);
            } else {
                this.addStokenValue(stoken, 102);
            }
            return;
        }
        if (this.secondaryTokenList.contains(auxWord)) {
            this.addStokenValue(stoken, 103);
            return;
        }
        if (auxWord.equalsIgnoreCase("nulo")) {
            this.addStokenValue(stoken, 126);
            return;
        }
        this.addStokenValue(stoken, 102);
    }

    private void identifyAllChars(StringBuilder word, ArrayList<Integer> stoken, TextScanner ts) throws IOException {
        char c = ts.getCurrentChar();
        while (this.isNotWhiteChar(c)) {
            word.append(c);
            ts.readChar(false);
            c = ts.getCurrentChar();
        }
        word.replace(0, word.length(), word.toString().toUpperCase());
    }

    public Token getToken(Vector<Token> v, String s) {
        int i = 0;
        while (i < v.size()) {
            if (v.elementAt(i).getMainToken().equalsIgnoreCase(s)) {
                return v.elementAt(i);
            }
            ++i;
        }
        return null;
    }

    public Token getMainToken(int id) {
        int i = 1;
        while (i <= 68) {
            if (this.tokenList.get(i).getId() == id) {
                return this.tokenList.get(i);
            }
            ++i;
        }
        return null;
    }

    public boolean tokenListContains(Vector<Token> v, String s) {
        int i = 0;
        while (i < v.size()) {
            if (v.elementAt(i).getMainToken().equalsIgnoreCase(s)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public String restToken(String word, int token) {
        Token t = this.getMainToken(token);
        if (t == null) {
            return "";
        }
        String s = new String(String.valueOf(t.getMainToken()) + " " + t.getEndingTokens());
        return StringUtils.capitalizeOnlyFirstLetters(s.substring(word.length() + 1));
    }

    private void createTokenList(String[] tokens) {
        try {
            int auxNumber;
            Token auxNewToken;
            String auxToken;
            StringTokenizer lineTokens;
            this.tokenList = new Hashtable(20);
            this.secondaryTokenList = new Vector(5, 5);
            int index = 0;
            int i = 1;
            while (i <= 68) {
                String line = tokens[index];
                lineTokens = new StringTokenizer(line, " ");
                auxToken = lineTokens.nextToken().toUpperCase();
                StringBuffer auxEndTokens = new StringBuffer("");
                while (lineTokens.hasMoreTokens()) {
                    String auxSecToken = new String(lineTokens.nextToken().toUpperCase());
                    this.secondaryTokenList.add(auxSecToken);
                    auxEndTokens.append(String.valueOf(auxEndTokens.length() > 0 ? " " : "") + auxSecToken);
                }
                auxNewToken = new Token(auxToken, auxEndTokens.toString(), i);
                this.tokenList.put(i, auxNewToken);
                ++i;
                ++index;
            }
            this.measureUnitTokenList = new Vector(5, 1);
            i = 0;
            while (i < 6) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.measureUnitTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.multiplierTokenList = new Vector(2, 1);
            i = 0;
            while (i < 2) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.multiplierTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.servoTokenList = new Vector(4, 1);
            i = 0;
            while (i < 4) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.servoTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.ledTokenList = new Vector(6, 1);
            i = 0;
            while (i < 6) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.ledTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.outTokenList = new Vector(8, 1);
            i = 0;
            while (i < 8) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.outTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.sensorTokenList = new Vector(8, 1);
            i = 0;
            while (i < 8) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.sensorTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.eventTokenList = new Vector(8, 1);
            i = 0;
            while (i < 8) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.eventTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.sensorDistanceTokenList = new Vector(8, 1);
            i = 0;
            while (i < 8) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.sensorDistanceTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.unitTokenList = new Vector(2, 1);
            auxToken = tokens[index];
            auxNewToken = new Token(auxToken, "");
            this.unitTokenList.add(auxNewToken);
            auxToken = tokens[++index];
            auxNewToken = new Token(auxToken, "");
            this.unitTokenList.add(auxNewToken);
            ++index;
            this.noteTokenList = new Vector(48, 5);
            i = 0;
            while (i < 48) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.noteTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.soundEffectTokenList = new Vector(3, 2);
            i = 0;
            while (i < 3) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.soundEffectTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.colorTokenList = new Vector(7, 5);
            i = 0;
            while (i < 7) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.colorTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
            this.stripTokenList = new Vector(2, 1);
            i = 0;
            while (i < 2) {
                lineTokens = new StringTokenizer(tokens[index], " ");
                auxToken = lineTokens.nextToken();
                auxNumber = new Integer(lineTokens.nextToken());
                auxNewToken = new Token(auxToken, "", auxNumber, -1);
                this.stripTokenList.add(auxNewToken);
                ++i;
                ++index;
            }
        }
        catch (Exception e) {
            System.err.println("Erro lendo arquivo de tokens");
            System.exit(1);
        }
    }

    private void commandBlock(StringBuilder word, ArrayList<Integer> stoken) throws IOException {
        this.lexico(word, stoken, this.textScanner);
        if (this.errorCode != 0) {
            return;
        }
        if (!stoken.contains(120)) {
            this.setErrorCode(13);
            return;
        }
        this.blockStack.push(this.textScanner.getCurrentLineNumber());
        ++this.openBlocks;
        while (!this.endLoop && !stoken.contains(121) && this.errorCode == 0) {
            this.waitCommand(word, stoken);
        }
        int auxError = this.blockStack.pop();
        if (this.errorCode != 0) {
            if (this.errorCode != 10) {
                return;
            }
            this.setErrorCode(36, auxError);
            return;
        }
        if (!stoken.contains(121)) {
            this.setErrorCode(36, auxError);
            return;
        }
        word.setLength(0);
        this.setStokenValue(stoken, 124);
    }

    private void skipSpaces(TextScanner ts) {
        char c = ts.getCurrentChar();
        while (whitechars.indexOf(c) >= 0 && !ts.eof()) {
            try {
                ts.readChar(true);
            }
            catch (Exception e) {
                break;
            }
            c = ts.getCurrentChar();
        }
    }

    private boolean isNotWhiteChar(char c) {
        return whitechars.indexOf(c) < 0;
    }

    private boolean isLegalDigit(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '%' || legalLetters.indexOf(c) >= 0 || legalDigits.indexOf(c) >= 0;
    }

    private boolean isLegalLetter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || legalLetters.indexOf(c) >= 0;
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean isEndLoop() {
        return this.endLoop;
    }

    public void addDeclarationListener(DeclarationListener dl) {
        if (this.declarationListeners.contains(dl)) {
            return;
        }
        this.declarationListeners.addElement(dl);
    }

    public void addCommandSensorListener(CommandSensorListener csl) {
        if (this.commandSensorListeners.contains(csl)) {
            return;
        }
        this.commandSensorListeners.addElement(csl);
    }

    public void removeDeclarationListener(DeclarationListener dl) {
        if (!this.declarationListeners.contains(dl)) {
            return;
        }
        this.declarationListeners.removeElement(dl);
    }

    public Vector<DeclarationListener> getDeclarationListeners() {
        return (Vector)this.declarationListeners.clone();
    }

    public Vector<CommandSensorListener> getCommandSensorListeners() {
        return (Vector)this.commandSensorListeners.clone();
    }

    public void generateDeclarationEvent(String name, int type) {
        DeclarationEvent de = new DeclarationEvent(this, name, type);
        Vector<DeclarationListener> vTemp = this.getDeclarationListeners();
        int x = 0;
        while (x < vTemp.size()) {
            DeclarationListener target = null;
            target = vTemp.elementAt(x);
            target.identifierDeclared(de);
            ++x;
        }
    }

    public double oneDecimal(double d) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(1, 4);
        return bd.doubleValue();
    }

    public boolean isArithmeticOp(ArrayList<Integer> stoken) {
        return stoken.contains(116) || stoken.contains(117) || stoken.contains(118) || stoken.contains(119);
    }

    public boolean isRelationalOp(ArrayList<Integer> stoken) {
        return stoken.contains(111) || stoken.contains(113) || stoken.contains(112) || stoken.contains(114) || stoken.contains(110) || stoken.contains(115);
    }

    public Montador getMontador() {
        return this.montador;
    }

    public TextScanner getTextScanner() {
        return this.textScanner;
    }

    public String invertRelationalOperator(String op) {
        if (op.indexOf(62) >= 0) {
            op = new String(op.replace('>', '<'));
        } else if (op.indexOf(60) >= 0) {
            op = new String(op.replace('<', '>'));
        }
        return op;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        if (this.currentCommandLine == -1) {
            this.setErrorLine(this.textScanner.getCurrentLineNumber());
        } else {
            this.setErrorLine(this.currentCommandLine);
        }
    }

    public void setErrorCode(int errorCode, int line) {
        this.errorCode = errorCode;
        this.setErrorLine(line);
    }

    public void setErrorLine(int errorLine) {
        this.errorLine = errorLine;
    }

    public int getErrorLine() {
        return this.errorLine;
    }

    private void setErrorRestToken(String errorRestToken) {
        this.errorRestToken = errorRestToken;
    }

    public String getErrorRestToken() {
        return this.errorRestToken;
    }

    public boolean isSeg(String s) {
        if (this.language == 1) {
            return s.equalsIgnoreCase("seg");
        }
        return s.equalsIgnoreCase("s");
    }

    public boolean isNegativeNumber(String s) {
        float i;
        try {
            i = new Float(s).floatValue();
        }
        catch (Exception e) {
            return false;
        }
        return i < 0.0f;
    }

    public void generateCommandSensorEvent(int type, int position) {
        boolean success = this.montador.setSensorRequerido(position, type);
        if (!success) {
            this.setErrorCode(105);
            this.erroPorta = position;
            int i = 0;
            while (i < this.sensorEvents.size()) {
                CommandSensorEvent aux = this.sensorEvents.get(i);
                if (aux.getPosition() == position) {
                    this.erroConflito = aux;
                    return;
                }
                ++i;
            }
            return;
        }
        CommandSensorEvent cse = new CommandSensorEvent(this, type, position, this.currentCommandLine, this.editorID);
        this.sensorEvents.add(cse);
        Vector<CommandSensorListener> vTemp = this.getCommandSensorListeners();
        int x = 0;
        while (x < vTemp.size()) {
            CommandSensorListener target = null;
            target = vTemp.elementAt(x);
            target.commandSensorCreated(cse);
            ++x;
        }
    }

    public void addCommandSensorEvent(CommandSensorEvent cse) {
        this.sensorEvents.add(cse);
    }

    private int convertUnitToKernel(String unit, double value) {
        if (unit.length() == 0) {
            return (int)Math.round(value);
        }
        int result = -1;
        if (unit.toLowerCase().equals("ohm")) {
            if (value < 1000.0 || value > 1.0E7) {
                this.setErrorCode(100);
                return -1;
            }
            result = (int)Math.round(100000.0 / (value + 100000.0) * 1023.0);
        } else if (unit.toLowerCase().equals("lx")) {
            if (value < 0.0 || value > 313.6) {
                this.setErrorCode(103);
                return -1;
            }
            result = (int)Math.round(value * 21259.84251968504 * 7.6725E-4 / 5.0);
        } else if (unit.toLowerCase().equals("v")) {
            if (value < -5.6 || value > 6.1) {
                this.setErrorCode(104);
                return -1;
            }
            result = (int)Math.round((6.47959 - value) * 294.0 / 5.0);
        } else if (unit.toLowerCase().equals("cm")) {
            if (value < 2.0 || value > 200.0) {
                this.setErrorCode(132);
                return -1;
            }
            result = (int)Math.round(value * 5.0);
        } else if (unit.toLowerCase().equals("bat")) {
            if (value < 5.5 || value > 14.0) {
                this.setErrorCode(151);
                return -1;
            }
            result = (int)Math.round(62.5 * value);
        } else if (unit.toLowerCase().equals("c")) {
            if (value < -31.2 || value > 126.4) {
                this.setErrorCode(101);
                return -1;
            }
            result = (int)Math.round(1013.7837837837837 / (Math.pow(Math.E, 3970.0 * (1.0 / (value + 273.15) - 0.0033540164346805303)) + 0.990990990990991));
        } else if (unit.toLowerCase().equals("db")) {
            if (value < 40.0 || value > 90.0) {
                this.setErrorCode(102);
                return -1;
            }
            result = (int)Math.round((value - 40.0) / 0.0491);
        }
        return result;
    }

    public int getErroPorta() {
        return this.erroPorta;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public ArrayList<IncludeFile> scanAllIncludes(StringBuilder word, ArrayList<Integer> stoken) {
        ArrayList<IncludeFile> includeList = new ArrayList<IncludeFile>();
        try {
            this.setStokenValue(stoken, -1);
            block2: while (true) {
                if (this.textScanner.eof()) {
                    this.textScanner.resetTextScanner();
                    this.endLoop = false;
                    this.currentCommandLine = -1;
                    return includeList;
                }
                while (true) {
                    if (stoken.contains(61) || this.textScanner.eof()) {
                        if (!stoken.contains(61)) continue block2;
                        this.lexico(word, stoken, this.textScanner);
                        if (!stoken.contains(105)) break;
                        includeList.add(new IncludeFile(word.toString(), this.textScanner.getCurrentLineNumber()));
                        continue block2;
                    }
                    this.lexico(word, stoken, this.textScanner);
                }
                this.setErrorCode(119);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return includeList;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public HashSet<String> scanAndGenerateAllSubs(StringBuilder word, ArrayList<Integer> stoken) {
        HashSet<String> subList = new HashSet<String>();
        try {
            this.setStokenValue(stoken, -1);
            block2: while (true) {
                if (this.textScanner.eof()) {
                    this.textScanner.resetTextScanner();
                    this.endLoop = false;
                    this.currentCommandLine = -1;
                    return subList;
                }
                while (true) {
                    if (stoken.contains(4) || this.textScanner.eof()) {
                        if (!stoken.contains(4)) continue block2;
                        this.lexico(word, stoken, this.textScanner);
                        subList.add(word.toString().toUpperCase());
                        if (this.declaredProcs.containsKey(word.toString())) continue block2;
                        this.declaredProcs.put(word.toString(), 0);
                        continue block2;
                    }
                    this.lexico(word, stoken, this.textScanner);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return subList;
    }

    public int getSensorTokenListNumber(StringBuilder word) {
        if (this.tokenListContains(this.sensorTokenList, word.toString())) {
            Token out = this.getToken(this.sensorTokenList, word.toString());
            return out.getNumber();
        }
        return -1;
    }

    public HashMap<String, String> getDeclaredDefs() {
        return this.declaredDefs;
    }

    private void getColorInterval(int color, int[] iInicio, int[] iFim) {
        iInicio[0] = this.montador.getCorIntervaloInicio(color);
        iFim[0] = color;
    }

    public int getEditorID() {
        return this.editorID;
    }

    public CommandSensorEvent getErroConflito() {
        return this.erroConflito;
    }

    private char invertOperators(char op, int idSensor) {
        if (idSensor != 6 && idSensor != 7) {
            return op;
        }
        if (op != '<' && op != '>') {
            return op;
        }
        if (op == '<') {
            return '>';
        }
        return '<';
    }

    private String invertOperators(String op, int idSensor) {
        if (idSensor != 6 && idSensor != 7) {
            return op;
        }
        if (!(op.equals("<") || op.equals(">") || op.equals("<=") || op.equals(">="))) {
            return op;
        }
        if (op.equals("<")) {
            return ">";
        }
        if (op.equals(">")) {
            return "<";
        }
        if (op.equals("<=")) {
            return ">=";
        }
        return "<=";
    }

    private int getValueFromStoken(ArrayList<Integer> stoken) {
        return stoken.get(0);
    }

    private void setStokenValue(ArrayList<Integer> stoken, int value) {
        stoken.clear();
        stoken.add(value);
    }

    private void addStokenValue(ArrayList<Integer> stoken, int value) {
        if (value != -1 && stoken.contains(-1)) {
            stoken.remove(stoken.indexOf(-1));
        }
        stoken.add(value);
    }

    class ServoAngulo {
        private int servo;
        private int angulo;
        private String var;

        public ServoAngulo(int s, int a, String v) {
            this.servo = s;
            this.angulo = a;
            this.var = v;
        }

        public int getServo() {
            return this.servo;
        }

        public int getAngulo() {
            return this.angulo;
        }

        public String getVar() {
            return this.var;
        }

        public void setVar(String var) {
            this.var = var;
        }
    }
}
