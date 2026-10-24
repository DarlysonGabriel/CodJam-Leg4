/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

import java.util.EventObject;

public class CommandSensorEvent
extends EventObject {
    public static final int SANALOGICOGENERICO = 20;
    public static final int SDISTANCIA = 8;
    public static final int SVOLTIMETRO = 7;
    public static final int SRESISTENCIA = 6;
    public static final int STEMP = 5;
    public static final int SRUIDO = 4;
    public static final int SLUZ = 3;
    public static final int SCOR = 2;
    public static final int SQUALQUERANALOGICO = 1;
    public static final int SDIGITALGENERICO = -20;
    public static final int SINFRA = -7;
    public static final int SFAIXA = -6;
    public static final int SCONTATO = -5;
    public static final int SQUALQUERDIGITAL = -4;
    public static final int SLED = -2;
    public static final int SDISPOSITIVOEXTERNO = -1;
    public static final int AMOTOR = 128;
    public static final int ASERVOA = 100;
    public static final int ASERVOA_FIM = 126;
    public static final int PORTA_MOTOR_E = 13;
    public static final int PORTA_MOTOR_D = 14;
    private int type;
    private int position;
    private int line;
    private int editor;

    public CommandSensorEvent(Object source, int t, int p, int l, int e) {
        super(source);
        this.position = p;
        this.type = t;
        this.line = l;
        this.editor = e;
    }

    public int getType() {
        return this.type;
    }

    public int getPosition() {
        return this.position;
    }

    public int getLine() {
        return this.line;
    }

    public int getEditor() {
        return this.editor;
    }
}
