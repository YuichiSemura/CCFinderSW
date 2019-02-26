package clonedetector.classlist;

import java.util.ArrayList;

public class FileData {
    /**
     * 一番親となるディレクトリのパス
     */
    public String directoryName;// ファイルパス
    /**
     * 一番親となるディレクトリの絶対パス
     */
    public String directoryNameAbsolute;
    /**
     * 全ファイルの行数を合算したもの
     */
    public int lineCount = 0;
    public int tokenCount = 0;


    /**
     * 各ファイルの行数が順に格納されている
     */
    public int[] lineCountList;
    /**
     * 各ファイルのトークン数が順に格納されている
     */
    public int[] tokenCountList;
    /**
     * 各ファイルのトークン数が順に格納されている
     */
    public int[] tokenIndexList;

    /**
     * 各ファイルのパスが格納されている
     */
    public ArrayList<String> filePathList;
    /**
     * 各ファイルの絶対パスが格納されている
     */
    public ArrayList<String> fileNameList;


    /**
     * 各ファイルのNgramのindexが格納されている
     */
    public int[] NGramIndexList;
    /**
     * 各ファイルのNgramの数が格納されている
     */
    public int[] NGramCountList;
}
