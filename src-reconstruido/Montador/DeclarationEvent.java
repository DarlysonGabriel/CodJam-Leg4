/*
 * Decompiled with CFR 0.152.
 */
package Montador;

import java.util.EventObject;

public class DeclarationEvent
extends EventObject {
    public static final int TYPEVAR = 0;
    public static final int TYPEPROC = 1;
    public static final int TYPEDEF = 1;
    private static final long serialVersionUID = -7179894318057918706L;
    private String name;
    private int type;

    public DeclarationEvent(Object source, String newDeclaration, int decType) {
        super(source);
        this.name = newDeclaration;
        this.type = decType;
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }
}
