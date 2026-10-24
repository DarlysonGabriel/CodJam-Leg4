/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

import java.util.EventObject;

public class LineChangeEvent
extends EventObject {
    private int line;

    public LineChangeEvent(Object source, int l) {
        super(source);
        this.line = l;
    }

    public int getLine() {
        return this.line;
    }
}
