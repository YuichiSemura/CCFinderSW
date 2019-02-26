package clonedetector;

import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import common.FileAndString;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCFXDReader {
    ClonePairData cpd;

    public CCFXDReader(ClonePairData cpd) {
        this.cpd = cpd;
    }

    public void readCCFXD(String filename, int[] NGramIndexList, int N) {
        int idx = 0;
        byte[] ccfxd = {};
        filename = filename + ".ccfxd";
        try {
            ccfxd = Files.readAllBytes(new File(filename).toPath());
        } catch (IOException e) {
            System.out.println("Exception : " + filename);
        }

        int i = 13;
        for (; i < ccfxd.length; i++) {
            if (ccfxd[i - 13] == 0x0A && ccfxd[i - 12] == 0x00 && ccfxd[i - 11] == 0x00
                    && ccfxd[i - 10] == 0x00 && ccfxd[i - 9] == 0x00 && ccfxd[i - 8] == 0x00
                    && ccfxd[i - 7] == 0x00 && ccfxd[i - 6] == 0x00 && ccfxd[i - 5] == 0x00
                    && ccfxd[i - 4] == 0x0A && ccfxd[i - 3] == 0x00 && ccfxd[i - 2] == 0x00
                    && ccfxd[i - 1] == 0x00 && ccfxd[i] == 0x00) {
                break;
            }
        }
        byte[] tmp = new byte[4];
        int memo = i = i + 32;
        int count = 0;
        for (; i < ccfxd.length - 32; i += 32) {
            count++;
        }
        int[][] pair = new int[count][4];
        count = 0;
        for (i = memo; i < ccfxd.length - 32; i += 32) {

            int j = i - 31;
            int file = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt() - 1;

            j = j + 4;
            int token = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt();
            j = j + 4;
            int token2 = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt();
            j = j + 4;
            int file2 = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt() - 1;
            j = j + 4;
            int token3 = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt();
            j = j + 4;
            int token4 = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt();
            j = j + 4;
            int cloneID = ByteBuffer.wrap(swapArray(ccfxd, j, tmp)).getInt();

            int forward = NGramIndexList[file] + token2 - N;
            int backward = NGramIndexList[file2] + token4 - N;
            int distance = token2 - token;
            pair[count][0] = forward;
            pair[count][1] = backward;
            pair[count][2] = distance;
            pair[count][3] = cloneID;
            count++;
        }
        cpd.pairListTrue = pair;
    }

    public byte[] swapArray(byte[] ccfxd, int j, byte[] tmp) {
        tmp[3] = ccfxd[j];
        tmp[2] = ccfxd[j + 1];
        tmp[1] = ccfxd[j + 2];
        tmp[0] = ccfxd[j + 3];
        return tmp;
    }

    public void readReadable(String filename, FileData fd, int N) {
        filename = filename + "_readable.txt";
        String str = "";
        try {
            str = FileAndString.readAll(filename);
        } catch (IOException e) {
            System.out.println("Exception : " + filename);
        }


        Pattern p1 = Pattern.compile("source_files \\{[\\s\\S]*?(\r\n?|\n)}");
        Matcher m = p1.matcher(str);
        if (!m.find()) {
            System.out.println("file error: " + filename);
            return;
        }
        String sourceFile = m.group();
        String[] sourceFileLine = sourceFile.split("\r\n?|\n");

        int max = sourceFileLine.length - 2;
        fd.NGramIndexList = new int[max];
        fd.NGramCountList = new int[max];
        fd.tokenCountList = new int[max];
        fd.lineCountList = new int[max];

        int i = 0;
        for (String x : sourceFileLine) {
            String[] tab = x.split("\t");
            if (tab.length < 3) {
                continue;
            }
            fd.filePathList.add(tab[1]);
            fd.fileNameList.add(tab[1]);
            fd.NGramCountList[i] = Math.max(Integer.parseInt(tab[2]) - N + 1, 0);
            i++;
        }

        fd.NGramIndexList[0] = 0;
        for (i = 1; i < max; i++) {
            fd.NGramIndexList[i] = fd.NGramCountList[i - 1] + fd.NGramIndexList[i - 1];
        }

        Pattern p2 = Pattern.compile("clone_pairs \\{[\\s\\S]*?}");
        Matcher m2 = p2.matcher(str);
        if (!m2.find()) {
            System.out.println("file error2: " + filename);
            return;
        }

        String sourceFile2 = m2.group();
        String[] sourceFileLine2 = sourceFile2.split("\r\n?|\n");

        int[][] pair = new int[sourceFileLine2.length - 2][4];//fileA tokenA fileB tokenB length ID
        int count = 0;
        for (String x : sourceFileLine2) {
            String[] tab = x.split("\t");
            if (tab.length < 3) {
                continue;
            }
            int file1 = Integer.parseInt(tab[1].substring(0, tab[1].indexOf('.'))) - 1;
            int token1 = Integer.parseInt(tab[1].substring(tab[1].indexOf('.') + 1, tab[1].indexOf('-')));
            int token2 = Integer.parseInt(tab[1].substring(tab[1].indexOf('-') + 1));
            int file2 = Integer.parseInt(tab[2].substring(0, tab[2].indexOf('.'))) - 1;
            int token3 = Integer.parseInt(tab[2].substring(tab[2].indexOf('.') + 1, tab[2].indexOf('-')));
            int token4 = Integer.parseInt(tab[2].substring(tab[2].indexOf('-') + 1));
            int cloneID = Integer.parseInt(tab[0]);

            int forward = fd.NGramIndexList[file1] + token2 - N;
            int backward = fd.NGramIndexList[file2] + token4 - N;
            int distance = token2 - token1;
            pair[count][0] = forward;
            pair[count][1] = backward;
            pair[count][2] = distance;
            pair[count][3] = cloneID;
            count++;
        }
        cpd.pairListTrue = pair;
    }
}
