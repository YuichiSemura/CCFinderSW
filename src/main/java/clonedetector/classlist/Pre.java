package clonedetector.classlist;

/**
 * for CCFXprep
 * use in PreProcess.java
 */
public class Pre{
    /**
     * 開始行
     */
    public int lineStart;
    /**
     * 開始列
     */
    public int clmStart;
    /**
     * 終了行
     */
    public int lineEnd;
    /**
     * 終了列
     */
    public int clmEnd;
    /**
     * ファイルでの開始文字数
     */
    public int sumStart;
    /**
     * ファイルでの終了文字数
     */
    public int sumEnd;
    /**
     * Token type defined in TokenName
     */
    public int type;
    /**
     * Actual string
     */
    public String token;

    public Pre(String str, int lineS, int clmS,
               int lineE, int clmE, int type, int sumStart, int sumEnd) {
        this.lineStart = lineS;
        this.clmStart = clmS;
        this.sumStart = sumStart;
        this.lineEnd = lineE;
        this.clmEnd = clmE;
        this.sumEnd = sumEnd;
        this.type = type;
        this.token = str;
    }


    @Override
    public String toString() {
        return "Pre [line=" + lineStart + ", clm=" + clmStart + ", sum=" + sumStart +
                ", line=" + lineEnd + ", clm=" + clmEnd + ", sum=" + sumEnd
                + ", type=" + type + ", hash=" + token + "]";
    }

}
