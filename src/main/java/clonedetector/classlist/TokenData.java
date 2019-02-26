package clonedetector.classlist;

public class TokenData {
    //ハッシュ値
    public int hash;
    //ファイルナンバー
    public int file;
    //ファイルでの順番
    public int num;
    //何行目はじめ
    public int lineStart;
    //何行目終わり
    public int lineEnd;
    //カラムはじめ
    public int columnStart;
    //カラム終わり
    public int columnEnd;

    public TokenData(int hash, int file, int num, int lineStart, int lineEnd, int columnStart, int columnEnd) {
        this.hash = hash;
        this.file = file;
        this.num = num;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    public TokenData(Token token, int file, int num) {
        this.hash = token.hash;
        this.lineStart = token.lineStart;
        this.lineEnd = token.lineEnd;
        this.columnStart = token.columnStart;
        this.columnEnd = token.columnEnd;
        this.file = file;
        this.num = num;
    }

    @Override
    public String toString() {
        return "TokenData [hash=" + hash + ", file=" + file + ", num=" + num + ", lineStart="
                + lineStart + ", lineEnd=" + lineEnd + ", columnStart=" + columnStart + ", columnEnd=" + columnEnd + "]";
    }
}
