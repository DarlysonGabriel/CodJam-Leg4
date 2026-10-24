/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

public class Token {
    private String mainToken;
    private String endingTokens;
    private boolean hasEndingTokens;
    private int idToken;
    private int number;
    private boolean numberSet;

    public Token() {
        this("", "", 0, false, -1);
    }

    public Token(String mt, String et) {
        this(mt, et, 0, false, -1);
    }

    public Token(String mt, String et, int id) {
        this(mt, et, 0, false, id);
    }

    public Token(String mt, String et, int n, int id) {
        this(mt, et, n, true, id);
    }

    public Token(String mt, String et, int n, boolean ns, int id) {
        this.mainToken = mt;
        if (et == null) {
            this.endingTokens = new String("");
            this.hasEndingTokens = false;
        } else if (et.length() < 1) {
            this.endingTokens = new String("");
            this.hasEndingTokens = false;
        } else {
            this.endingTokens = et;
            this.hasEndingTokens = true;
        }
        this.number = n;
        this.numberSet = ns;
        this.idToken = id;
    }

    public void setMainToken(String mainToken) {
        this.mainToken = mainToken;
    }

    public String getMainToken() {
        return this.mainToken;
    }

    public void setEndingTokens(String endingTokens) {
        this.endingTokens = endingTokens;
    }

    public String getEndingTokens() {
        return this.endingTokens;
    }

    public boolean hasEndingTokens() {
        return this.hasEndingTokens;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int n) {
        this.number = n;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(String.format("%s", this.mainToken));
        if (this.hasEndingTokens) {
            result.append(String.format(" %s", this.endingTokens));
        }
        if (this.numberSet) {
            result.append(String.format(" - %d", this.number));
        }
        return result.toString();
    }

    public int getId() {
        return this.idToken;
    }
}
