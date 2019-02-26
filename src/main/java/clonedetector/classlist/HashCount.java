package clonedetector.classlist;

public class HashCount {
    //ハッシュ値
    public int hash;
    //全NGramの通算
    public int count;
    //ファイルナンバー
    public int file;
    //ファイルでの順番
    public int num;

    public HashCount(int hash, int count, int file, int num) {
        this.hash = hash;
        this.count = count;
        this.file = file;
        this.num = num;
    }

    @Override
    public String toString() {
        return "HashCount [hash=" + hash + ", count=" + count + "]";
    }


}
