package clonedetector;

import ccfindersw.CCFSWData;
import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import common.FileAndString;
import common.PrintProgress;

import java.util.ArrayList;
import java.util.Collections;

public class CCFFormatter {
    private String language;
    private int[] lineCountList;
    private int[] tokenCountList;
    private ArrayList<String> filePathList;
    private ArrayList<String> fileNameList;
    private String directoryNameAbsolute;
    private ClonePairData cpd;

    private NGramFinder nf;
    private String filename;
    private String toolName;
    private int threshold;

    public CCFFormatter(FileData fileData, NGramFinder nf, OptionReader or, ClonePairData cpd) {
        lineCountList = fileData.lineCountList;
        tokenCountList = fileData.tokenCountList;
        filePathList = fileData.filePathList;
        fileNameList = fileData.fileNameList;
        directoryNameAbsolute = fileData.directoryNameAbsolute;

        this.nf = nf;
        this.language = or.getLanguage();
        this.filename = or.getOutput();
        this.threshold = or.getThreshold();
        this.toolName = CCFSWData.getToolName();
        this.cpd = cpd;
    }

    public void outputCCF() {
        String filename = this.filename + ".txt";
        System.out.println("ccfinder file = " + filename);
        StringBuffer buf = new StringBuffer("");

        // オプション部
        buf.append("#version: ").append(toolName).append("\n");
        buf.append("#format: classwise\n");

        buf.append("#langspec: ");
        buf.append(language.toUpperCase());
        buf.append("\n#option: -b ").append(threshold).append("\n");
        buf.append("#option: -e char\n");
        buf.append("#option: -k 30\n");
        buf.append("#option: -r abdfikmnpstuv\n");
        buf.append("#option: -c wfg\n");
        buf.append("#option: -y \n");

        // ソースファイル部
        buf.append("#begin{file description}\n");
        for (int i = 0; i < filePathList.size(); i++) {
            buf.append("0.").append(i);
            buf.append("\t").append(lineCountList[i]);
            buf.append("\t").append(tokenCountList[i]);
            buf.append("\t").append(fileNameList.get(i));
            buf.append("\n");
        }
        buf.append("#end{file description}\n");
        buf.append("#begin{syntax error}\n#end{syntax error}\n");
        // クローンセット部
        buf.append("#begin{clone}\n");

        PrintProgress ps = new PrintProgress(2);
        int i = 0;
        while (i < cpd.pairListTrue.length) {
            ArrayList<Integer> x = new ArrayList<>();
            int[] i0 = cpd.pairListTrue[i];
            int[] i1;
            int distance = i0[2];
            x.add(i0[0]);
            x.add(i0[1]);
            while (true) {
                if (i + 1 < cpd.pairListTrue.length) {
                    i0 = cpd.pairListTrue[i];
                    i1 = cpd.pairListTrue[i + 1];
                    if (i0[3] == i1[3]) {
                        if (!x.contains(i1[0])) {
                            x.add(i1[0]);
                        }
                        if (!x.contains(i1[1])) {
                            x.add(i1[1]);
                        }
                        i++;
                        ps.plusProgress(cpd.pairListTrue.length);
                        continue;
                    }
                }
                i++;
                ps.plusProgress(cpd.pairListTrue.length);
                Collections.sort(x);
                writeSet(x, distance, buf);
                break;
            }
        }
        buf.append("#end{clone}\n");

        FileAndString.writeAll(filename, buf.toString());
    }

    private void writeSet(ArrayList<Integer> x, int distance, StringBuffer buf) {
        boolean doneLNR = false;
        int LNR = 0;
        buf.append("#begin{set}\n");
        for (Integer s : x) {
            int count = s;// クローン内の一番最後のNgramが全Ngram中何番目か
            int filenum = nf.tokenList[count - distance + 1].file;
            int linenum = nf.tokenList[count - distance + 1].lineStart;
            int columnnum = nf.tokenList[count - distance + 1].columnStart;
            int tokennum = nf.tokenList[count - distance + 1].num;

            buf.append("0.");
            buf.append(filenum);
            buf.append("\t");
            buf.append(linenum);
            buf.append(",");
            buf.append(columnnum);
            buf.append(",");
            buf.append(tokennum);

            linenum = nf.tokenList[count].lineEnd;
            columnnum = nf.tokenList[count].columnEnd + 1;// +2
            tokennum = tokennum + distance - 1;
            if (!doneLNR) {
                LNR = calLNR(count - distance + 1, distance);
                doneLNR = true;
            }
            buf.append("\t");
            buf.append(linenum);
            buf.append(",");
            buf.append(columnnum);
            buf.append(",");
            buf.append(tokennum);
            buf.append("\t");
            buf.append(LNR);
            buf.append("\n");
        }
        buf.append("#end{set}");
        buf.append("\n");
    }

    private int calLNR(int first, int distance) {
        boolean lifeDeath[] = new boolean[distance];
        for (int i = 0; i < distance; i++) {
            lifeDeath[i] = true;
        }
        for (int i = first; i < first + distance; i++) {
            for (int j = i + 1; j < first + distance; j++) {
                if (nf.tokenList[i].hash == nf.tokenList[j].hash) {
                    int k = 1; //maybe 0
                    while (i + k < j && j + k < first + distance) {
                        if (nf.tokenList[i + k].hash != nf.tokenList[j + k].hash) {
                            break;
                        } else if (i + k == j - 1) {
                            for (int l = i; l <= j + k; l++) {
                                lifeDeath[l] = false;
                            }
                            break;
                        }
                        k++;
                    }

                }
            }
        }
        int life = 0, maxLife = 0;
        for (int i = 0; i < distance; i++) {
            if (lifeDeath[i]) {
                life++;
            } else {
                maxLife = Math.max(life, maxLife);
                life = 0;
            }
        }
        if (life != 0)
            maxLife = Math.max(life, maxLife);
        return maxLife;
    }
}
