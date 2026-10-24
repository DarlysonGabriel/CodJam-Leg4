/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

import PNCA.LineChangeEvent;
import PNCA.LineChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

public class TextScanner {
    private StringBuilder text;
    private int c;
    String line;
    private int currentLine;
    private int nLines;
    private boolean eol;
    private boolean eof;
    private BufferedReader reader;
    private Vector<LineChangeListener> lineChangeListeners;

    public TextScanner(String s) throws IOException {
        this(new StringBuilder(s));
    }

    public TextScanner(StringBuilder t) throws IOException {
        this.text = t;
        this.lineChangeListeners = new Vector();
        if (t == null) {
            System.err.println("Erro: JTextArea nulo");
            System.exit(1);
        }
        this.nLines = t.toString().split("\n").length;
        this.currentLine = 0;
        this.c = 0;
        this.eol = false;
        this.eof = false;
        this.reader = new BufferedReader(new StringReader(this.text.toString()));
        this.line = this.reader.readLine();
        if (this.line == null) {
            this.eof = true;
        } else if (this.line.length() <= 0) {
            this.eol = true;
        }
    }

    public void resetTextScanner() throws IOException {
        this.currentLine = 0;
        this.c = 0;
        this.eol = false;
        this.eof = false;
        this.reader = new BufferedReader(new StringReader(this.text.toString()));
        this.line = this.reader.readLine();
        if (this.line == null) {
            this.eof = true;
        } else if (this.line.length() <= 0) {
            this.eol = true;
        }
    }

    public TextScanner(TextScanner ts) throws IOException {
        this.text = ts.getStringBuilder();
        this.lineChangeListeners = new Vector();
        this.nLines = this.text.toString().split("\n").length;
        this.reader = new BufferedReader(new StringReader(this.text.toString()));
        this.gotoLine(ts.getCurrentLineNumber());
        this.gotoCol(ts.getCurrentColumn());
        this.c = ts.c;
        this.line = new String(ts.line);
        this.currentLine = ts.currentLine;
        this.nLines = ts.nLines;
        this.eol = ts.eol;
        this.eof = ts.eof;
    }

    public int getCurrentColumn() {
        return this.c;
    }

    public int getCurrentLineNumber() {
        return this.currentLine;
    }

    public char getCurrentChar() {
        if (this.line == null) {
            return '\u0000';
        }
        if (this.c < this.line.length()) {
            return this.line.charAt(this.c);
        }
        return '\u0000';
    }

    public char getPreviousChar() {
        if (this.c - 1 < this.line.length() && this.c - 1 >= 0) {
            return this.line.charAt(this.c - 1);
        }
        return '\u0000';
    }

    public char getNextChar() {
        if (this.c + 1 < this.line.length() && this.c + 1 >= 0) {
            return this.line.charAt(this.c + 1);
        }
        return '\u0000';
    }

    public char readChar(boolean readLine) throws IOException {
        if (!this.eol) {
            if (this.c >= this.line.length()) {
                this.eol = true;
                if (this.currentLine + 1 >= this.nLines) {
                    this.eof = true;
                }
                return '\u0000';
            }
            char rc = this.line.charAt(this.c);
            ++this.c;
            if (this.c >= this.line.length()) {
                this.eol = true;
                if (this.currentLine + 1 >= this.nLines) {
                    this.eof = true;
                }
            }
            return rc;
        }
        if (readLine) {
            this.readNextLine();
            this.c = 0;
            return this.getCurrentChar();
        }
        return '\u0000';
    }

    public void recedeChar() {
        if (this.c > 0) {
            --this.c;
            if (this.c < this.line.length()) {
                this.eol = false;
            }
        }
    }

    public void recedeLine() throws IOException {
        if (this.currentLine > 0) {
            this.gotoLine(this.currentLine - 1);
        }
    }

    public String readNextLine() throws IOException {
        if (this.eof) {
            return "";
        }
        this.line = this.reader.readLine();
        if (this.line == null) {
            this.eof = true;
            this.eol = true;
            return null;
        }
        ++this.currentLine;
        this.generateLineChangeEvent(this.currentLine);
        this.c = 0;
        if (this.line.length() > 0) {
            this.eol = false;
        } else {
            this.eol = true;
            if (this.currentLine + 1 >= this.nLines) {
                this.eof = true;
            }
        }
        return this.line;
    }

    public String readCurrentLine() throws IOException {
        String auxLine = new String(this.line);
        this.line = this.reader.readLine();
        if (this.line == null) {
            this.eof = true;
        } else {
            ++this.currentLine;
            this.generateLineChangeEvent(this.currentLine);
        }
        this.c = 0;
        if (this.line == null) {
            this.eol = true;
        } else if (this.line.length() > 0) {
            this.eol = false;
        } else {
            this.eol = true;
            if (this.currentLine + 1 >= this.nLines) {
                this.eof = true;
            }
        }
        return auxLine;
    }

    public String readToEndOfCurrentLine() throws IOException {
        String auxLine = new String(this.line.substring(this.c + 1));
        this.line = this.reader.readLine();
        if (this.line == null) {
            this.eof = true;
        } else {
            ++this.currentLine;
            this.generateLineChangeEvent(this.currentLine);
        }
        this.c = 0;
        if (this.line == null) {
            this.eol = true;
        } else if (this.line.length() > 0) {
            this.eol = false;
        } else {
            this.eol = true;
            if (this.currentLine + 1 >= this.nLines) {
                this.eof = true;
            }
        }
        return auxLine;
    }

    public String getCurrentLine() throws IOException {
        return this.line;
    }

    public boolean eol() {
        return this.eol;
    }

    public boolean eof() {
        return this.eof;
    }

    public void gotoLine(int l) throws IOException {
        if (l >= this.nLines) {
            l = this.nLines - 1;
        } else if (l < 0) {
            l = 0;
        }
        this.c = 0;
        this.reader = new BufferedReader(new StringReader(this.text.toString()));
        this.currentLine = l;
        int i = 0;
        while (i <= l) {
            this.line = this.reader.readLine();
            ++i;
        }
        if (this.line.length() > 0) {
            this.eol = false;
        } else {
            this.eol = true;
            if (l + 1 == this.nLines) {
                this.eof = true;
            }
        }
        this.generateLineChangeEvent(this.currentLine);
    }

    public void gotoCol(int col) {
        if (col >= this.line.length()) {
            col = this.line.length();
        } else if (col < 0) {
            col = 0;
        }
        this.c = col;
        if (this.line.length() < 1) {
            this.eol = true;
            if (this.currentLine + 1 >= this.nLines) {
                this.eof = true;
            }
        }
    }

    public int getLineCount() {
        return this.nLines;
    }

    public int getCurrentLineLength() {
        return this.line.length();
    }

    public void addLineChangeListener(LineChangeListener lcl) {
        if (this.lineChangeListeners.contains(lcl)) {
            return;
        }
        this.lineChangeListeners.addElement(lcl);
    }

    public Vector<LineChangeListener> getLineChangeListeners() {
        return (Vector)this.lineChangeListeners.clone();
    }

    public void generateLineChangeEvent(int l) {
        LineChangeEvent lce = new LineChangeEvent(this, l);
        Vector<LineChangeListener> vTemp = this.getLineChangeListeners();
        int x = 0;
        while (x < vTemp.size()) {
            LineChangeListener target = null;
            target = vTemp.elementAt(x);
            target.lineChanged(lce);
            ++x;
        }
    }

    public StringBuilder getStringBuilder() {
        return this.text;
    }
}
