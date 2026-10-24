/*
 * Decompiled with CFR 0.152.
 */
package PNCA;

public class ExpressionType {
    private boolean usesV1;
    private boolean usesV2;
    private boolean usesN1;
    private boolean usesN2;
    private boolean isColor;
    private boolean isSensor;
    private String v1;
    private String v2;
    private int n1;
    private int n2;
    private double d1;
    private double d2;
    private String op;
    private String unit;
    private boolean usesInteger = true;
    private boolean usesDouble = false;
    private boolean typeIsBetween;

    public ExpressionType(boolean usesV1, boolean usesV2, boolean usesN1, boolean usesN2, String v1, String v2, double d1, double d2, String op, String unit) {
        this.typeIsBetween = false;
        this.usesV1 = usesV1;
        this.usesV2 = usesV2;
        this.usesN1 = usesN1;
        this.usesN2 = usesN2;
        this.unit = unit;
        this.v1 = v1;
        this.v2 = v2;
        this.d1 = d1;
        this.d2 = d2;
        this.op = op;
        this.isColor = false;
        this.usesInteger = false;
        this.usesDouble = true;
    }

    public ExpressionType(boolean usesV1, boolean usesV2, boolean usesN1, boolean usesN2, String v1, String v2, int n1, int n2, String op, boolean isColor) {
        this.usesV1 = usesV1;
        this.usesV2 = usesV2;
        this.usesN1 = usesN1;
        this.usesN2 = usesN2;
        this.v1 = v1;
        this.v2 = v2;
        this.n1 = n1;
        this.n2 = n2;
        this.op = op;
        this.isColor = isColor;
    }

    public ExpressionType(String v1, double d1, double d2, boolean isSensor, String unit) {
        this.v1 = v1;
        this.isSensor = isSensor;
        this.typeIsBetween = true;
        this.d1 = d1;
        this.d2 = d2;
        this.usesV1 = true;
        this.usesV2 = false;
        this.unit = unit;
        this.v2 = "";
        this.op = "E";
        this.isColor = false;
    }

    public ExpressionType(String v1, int n1, int n2, boolean isSensor) {
        this.isSensor = isSensor;
        this.typeIsBetween = true;
        this.v1 = v1;
        this.n1 = n1;
        this.n2 = n2;
        this.usesV1 = true;
        this.usesV2 = false;
        this.usesN1 = true;
        this.usesN2 = true;
        this.unit = "";
        this.v2 = "";
        this.d1 = 0.0;
        this.d2 = 0.0;
        this.op = "E";
        this.isColor = false;
    }

    public boolean usesV1() {
        return this.usesV1;
    }

    public void setUsesV1(boolean usesV1) {
        this.usesV1 = usesV1;
    }

    public boolean usesV2() {
        return this.usesV2;
    }

    public void setUsesV2(boolean usesV2) {
        this.usesV2 = usesV2;
    }

    public boolean usesN1() {
        return this.usesN1;
    }

    public void setUsesN1(boolean usesN1) {
        this.usesN1 = usesN1;
    }

    public boolean usesN2() {
        return this.usesN2;
    }

    public void setUsesN2(boolean usesN2) {
        this.usesN2 = usesN2;
    }

    public String getV1() {
        return this.v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {
        return this.v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }

    public int getN1() {
        return this.n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return this.n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public double getD1() {
        return this.d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return this.d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    public String getOp() {
        return this.op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setColor(boolean isColor) {
        this.isColor = isColor;
    }

    public boolean isColor() {
        return this.isColor;
    }

    public boolean usesInteger() {
        return this.usesInteger;
    }

    public boolean usesDouble() {
        return this.usesDouble;
    }

    public String getUnit() {
        return this.unit;
    }

    public boolean isBetween() {
        return this.typeIsBetween;
    }

    public boolean isSensor() {
        return this.isSensor;
    }

    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
    }
}
