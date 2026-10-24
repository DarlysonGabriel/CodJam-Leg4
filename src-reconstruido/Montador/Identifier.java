/*
 * Decompiled with CFR 0.152.
 */
package Montador;

public class Identifier {
    public static final int TYPEVAR = 0;
    public static final int TYPEPROC = 1;
    private String name;
    private int type;
    private int pos;

    public Identifier(String n, int t, int p) {
        this.name = new String(n);
        if (t != 0 && t != 1) {
            t = 0;
        }
        this.type = t;
        this.pos = p;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getName() {
        return this.name;
    }

    public void setType(int t) {
        if (t != 0 && t != 1) {
            t = 0;
        }
        this.type = t;
    }

    public int getType() {
        return this.type;
    }

    public int getPos() {
        return this.pos;
    }

    public void setPos(int p) {
        this.pos = p;
    }
}
