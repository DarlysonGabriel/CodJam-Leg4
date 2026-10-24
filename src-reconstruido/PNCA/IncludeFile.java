/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

public class IncludeFile {
    private String filename;
    private int line;

    public IncludeFile(String f, int l) {
        this.filename = f;
        this.line = l;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
