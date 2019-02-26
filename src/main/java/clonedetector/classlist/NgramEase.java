package clonedetector.classlist;

public class NgramEase {
    //ハッシュ値
    public int hash;
    //全NGramの通算
    public int count;
    //ファイルナンバー
    public int file;
    //ファイルでの順番
    public int num;
    //true nest0を含む false nest0を含まない

    public NgramEase(int hash, int count, int file, int num) {
        this.hash = hash;
        this.count = count;
        this.file = file;
        this.num = num;
    }

    @Override
    public String toString() {
        return "NgramEase [hash=" + hash + ", count=" + count + ", file=" + file + ", num=" + num + "]";
    }
}
