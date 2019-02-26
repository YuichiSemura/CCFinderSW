package clonedetector.classlist;

public class Token {
    /**
     * 文字のハッシュ
     */
    public int hash;
    /**
     * Token type, common.TokenName.java
     */
    public int type;
    /**
     * 開始行
     */
    public int lineStart;
    /**
     * 終了行
     */
    public int lineEnd;
    /**
     * 開始列
     */
    public int columnStart;
    /**
     * 終了列
     */
    public int columnEnd;

    public Token(String str, int lineS, int clmS, int lineE, int clmE, int type) {
        this.hash = str.hashCode();
        this.lineStart = lineS;
        this.lineEnd = lineE;
        this.columnStart = clmS;
        this.columnEnd = clmE;
        this.type = (byte) type;
    }

    public Token(Pre pre) {
        this.hash = pre.token.hashCode();
        this.lineStart = pre.lineStart;
        this.lineEnd = pre.lineEnd;
        this.columnStart = pre.clmStart;
        this.columnEnd = pre.clmEnd;
        this.type = pre.type;
    }

    @Override
    public String toString() {
        return "Token [hash=" + hash + ", type=" + type + ", lineStart="
                + lineStart + ", columnStart=" + columnStart + ", lineEnd=" + lineEnd + ", columnEnd=" + columnEnd + "]";
    }

}

