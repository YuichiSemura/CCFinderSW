package clonedetector;

import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import clonedetector.classlist.LangRuleConstructor;
import clonedetector.classlist.TokenData;
import common.FileAndString;
import common.PrintProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * CCFSWFormatter
 * When option -ccfsw, output to hogehoge_ccfsw.txt
 */
public class CCFSWFormatter {

    private FileData fd;
    private NGramFinder nf;
    private String filename;
    private String toolName;
    private String directoryPath;
    private String language;
    private int threshold;
    private String charset;
    private HashMap<String, LangRuleConstructor> languageRuleMap;
    private ClonePairData cpd;

    public CCFSWFormatter(FileData fd, NGramFinder nf, OptionReader or, ClonePairData cpd) {
        this.fd = fd;
        this.nf = nf;
        this.filename = or.getOutput();
        this.threshold = or.getThreshold();
        this.toolName = or.getToolname();
        this.language = or.getLanguage();
        this.directoryPath = or.getDirectory();
        this.languageRuleMap = or.languageRuleMap;
        this.charset = or.getCharset();
        this.cpd = cpd;
    }

    /**
     * -ccfsw pair
     * call by CloneDetector.java
     */
    public void outputCCFSWFormatPair() {
        String filename = this.filename + "_ccfsw.txt";
        System.out.println("ccfsw file = " + filename);
        StringBuilder buf = new StringBuilder();
        optionRuleFile(buf);

        // クローンペア部
        buf.append("#clone_pairs\n");

        PrintProgress ps = new PrintProgress(2);
        for (int[] x : cpd.pairListTrue) {
            int distance = x[2];
            buf.append("cloneID:").append(x[3]).append("\n\t");

            //pairの前
            TokenData forward = nf.tokenList[x[0] - distance + 1];
            int fileNum = forward.file;
            int lineNum = forward.lineStart + 1;
            int columnNum = forward.columnStart + 1;

            buf.append(fileNum);
            buf.append(":").append(lineNum);
            buf.append(",").append(columnNum);
            buf.append(" - ");

            TokenData backward = nf.tokenList[x[0]];
            lineNum = backward.lineEnd + 1;
            columnNum = backward.columnEnd + 2;// +2

            buf.append(lineNum);
            buf.append(",");
            buf.append(columnNum);
            buf.append("\n\t");

            //pairの後
            forward = nf.tokenList[x[1] - distance + 1];
            fileNum = forward.file;
            lineNum = forward.lineStart + 1;
            columnNum = forward.columnStart + 1;

            buf.append(fileNum);
            buf.append(":").append(lineNum);
            buf.append(",").append(columnNum);
            buf.append(" - ");

            backward = nf.tokenList[x[1]];
            lineNum = backward.lineEnd + 1;
            columnNum = backward.columnEnd + 2;// +2

            buf.append(lineNum);
            buf.append(",");
            buf.append(columnNum);
            buf.append("\n");
            ps.plusProgress(cpd.pairListTrue.length);
        }
        FileAndString.writeAll(filename, buf.toString());
    }

    /**
     * -ccfsw set
     * call by CloneDetector.java
     */
    public void outputCCFSWFormatSet() {
        String filename = this.filename + "_ccfsw.txt";
        System.out.println("ccfsw file = " + filename);
        StringBuilder buf = new StringBuilder();
        optionRuleFile(buf);

        // クローンセット部
        buf.append("#clone_sets\n");

        PrintProgress ps = new PrintProgress(2);
        int i = 0;
        while (i < cpd.pairListTrue.length) {
            ArrayList<Integer> setList = new ArrayList<>();
            int[] i0 = cpd.pairListTrue[i];
            int[] i1;
            int distance = i0[2];
            setList.add(i0[0]);
            setList.add(i0[1]);
            while (true) {
                int cloneID = i0[3];
                if (i + 1 < cpd.pairListTrue.length) {
                    i0 = cpd.pairListTrue[i];
                    i1 = cpd.pairListTrue[i + 1];
                    if (i0[3] == i1[3]) {
                        if (!setList.contains(i1[0])) {
                            setList.add(i1[0]);
                        }
                        if (!setList.contains(i1[1])) {
                            setList.add(i1[1]);
                        }
                        i++;
                        ps.plusProgress(cpd.pairListTrue.length);
                        continue;
                    }
                }
                i++;
                ps.plusProgress(cpd.pairListTrue.length);
                Collections.sort(setList);
                buf.append("cloneID:").append(cloneID).append("\n");
                appendSet(buf, distance, setList);
                break;
            }
        }

        FileAndString.writeAll(filename, buf.toString());
    }

    private void appendSet(StringBuilder buf, int distance, ArrayList<Integer> setList) {
        for (Integer y : setList) {
            //pairの前
            TokenData forward = nf.tokenList[y - distance + 1];
            int fileNum = forward.file;
            int lineNum = forward.lineStart;
            int columnNum = forward.columnStart;

            buf.append("\t").append(fileNum);
            buf.append(":").append(lineNum);
            buf.append(",").append(columnNum);
            buf.append(" - ");

            TokenData backward = nf.tokenList[y];
            lineNum = backward.lineEnd;
            columnNum = backward.columnEnd + 1;// +2

            buf.append(lineNum);
            buf.append(",");
            buf.append(columnNum);
            buf.append("\n");
        }
    }

    /**
     * ペアとセットの共通出力部
     */
    private void optionRuleFile(StringBuilder buf) {
        // オプション部
        buf.append("#version\t").append(toolName).append("\n");
        buf.append("#option\n-d\t").append(directoryPath).append("\n");
        buf.append("-l\t").append(language).append("\n");
        buf.append("-o\t").append(this.filename).append("\n");
        buf.append("-t\t").append(threshold).append("\n");
        //buf.append("-w\t").append(getRelation(detectionRange)).append("\n");
        buf.append("-charset\t").append(charset).append("\n");

        //言語・予約語・コメントファイル
        buf.append("#rule_constructor\n");
        for (String x : languageRuleMap.keySet()) {
            buf.append(x).append("{\n");
            buf.append("\t").append("comment_file\t").append(languageRuleMap.get(x).commentFilePath).append("\n");
            buf.append("\t").append("reserved_file\t").append(languageRuleMap.get(x).reservedFilePath).append("\n");
            buf.append("}\n");
        }

        // ソースファイル部
        buf.append("#source_files\n");
        for (int i = 0; i < fd.filePathList.size(); i++) {
            buf.append(i);
            buf.append("\t").append(fd.lineCountList[i]);
            buf.append("\t").append(fd.tokenCountList[i]);
            buf.append("\t").append(fd.fileNameList.get(i));
            buf.append("\n");
        }
    }
}
