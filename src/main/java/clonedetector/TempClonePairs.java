package clonedetector;

import clonedetector.classlist.FileData;
import clonedetector.classlist.TokenData;
import common.FileAndString;
import common.PrintProgress;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TempClonePairs {

    public static void outputTempClonePair(NGramFinder nf, String directoryPath, int groupA, int groupB, int group) {
        String filename = directoryPath + File.separator + ".temp_" + groupA + "_" + groupB + "_" + group + ".ccfswtmp";

        StringBuilder buf = new StringBuilder();
        PrintProgress ps = new PrintProgress(2);
        System.out.println("output start");

        if (nf.pairArray == null) {
            FileAndString.writeAll(filename, "\n");
            return;
        }

        for (int[] x : nf.pairArray) {
            int distance = x[2];
            buf.append(distance).append("\t");

            //pairの前
            TokenData backward = nf.tokenList[x[0]];
            int file = backward.file;
            int num = backward.num;

            buf.append(file).append(",").append(num).append("\t");

            //pairの後
            backward = nf.tokenList[x[1]];
            file = backward.file;
            num = backward.num;

            buf.append(file).append(",").append(num).append("\n");
            ps.plusProgress(nf.pairArray.length);
        }
        FileAndString.writeAll(filename, buf.toString());
    }

    public static int[][] loadTempClonePair(int pairCount, String directoryPath, int group, FileData fd) {
        int[][] pairList = new int[pairCount][4];
        int cloneID = 1;
        for (int i = 0; i < group; i++) {
            for (int j = i; j < group; j++) {
                String filename = directoryPath + File.separator + ".temp_" + i + "_" + j + "_" + group + ".ccfswtmp";
                String str = "";
                try {
                    str = FileAndString.readAll(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] a = str.split("\r\n?|\n");
                for (String b : a) {
                    String[] c = b.split("\t");
                    if (c.length != 3) continue;

                    String[] d = c[1].split(",");
                    String[] e = c[2].split(",");
                    int distance = Integer.parseInt(c[0]);
                    int file1 = Integer.parseInt(d[0]);
                    int num1 = Integer.parseInt(d[1]);
                    int file2 = Integer.parseInt(e[0]);
                    int num2 = Integer.parseInt(e[1]);

                    pairList[cloneID - 1][0] = fd.tokenIndexList[file1] + num1;
                    pairList[cloneID - 1][1] = fd.tokenIndexList[file2] + num2;
                    pairList[cloneID - 1][2] = distance;
                    pairList[cloneID - 1][3] = cloneID;
                    //System.out.println(pairList[cloneID - 1][0] + " " + pairList[cloneID - 1][1] + " " + pairList[cloneID - 1][2] + " " + pairList[cloneID - 1][3]);
                    cloneID++;
                }
            }
        }

        return pairList;
    }

    public void outputNgramCount(int[] nGramCountList, String directoryPath) {
        String filename = directoryPath + File.separator + ".ngramcount";
        StringBuilder buf = new StringBuilder();
        Arrays.stream(nGramCountList).forEach(s -> buf.append(s).append("\n"));
        FileAndString.writeAll(filename, buf.toString());
    }

    public int[] loadNgramCount(String directoryPath) {
        String filename = directoryPath + File.separator + ".ngramcount";
        String str = "";
        try {
            str = FileAndString.readAll(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] x = str.split("\r\n?|\n");
        int i = 0;
        int[] tmp = new int[x.length - 1];
        for (String a : x) {
            try {
                tmp[i++] = Integer.parseInt(a);
            } catch (NumberFormatException ignored) {

            }
        }
        return tmp;
    }

    public static void tmpPairList(int[][] pairList, int hash, String directoryPath, int groupA, int groupB, int group) {
        String filename = directoryPath + File.separator + ".rare" + hash + "_" + groupA + "_" + groupB + "_" + group + ".ccfswtmp";
        try (FileOutputStream out = new FileOutputStream(filename, true)) {
            for (int[] x : pairList) {
                out.write(ByteBuffer.allocate(4).putInt(x[0]).array());
                out.write(ByteBuffer.allocate(4).putInt(x[1]).array());
                out.write(ByteBuffer.allocate(4).putInt(x[2]).array());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int[][] loadPairList(int[][] pairList, int hash, String directoryPath,
                                       int groupA, int groupB, int group, int nowPairCount, int sumPairCount) {
        String filename = directoryPath + File.separator + ".rare" + hash + "_" + groupA + "_" + groupB + "_" + group + ".ccfswtmp";

        byte[] tmpa = new byte[4];
        int idx = 0;
        int[][] pair = new int[sumPairCount][3];
        try (BufferedInputStream bi = new BufferedInputStream(new FileInputStream(filename))) {
            while ((bi.read(tmpa, 0, 4)) != -1) {
                pair[idx][0] = ByteBuffer.wrap(tmpa).getInt();
                bi.read(tmpa, 0, 4);
                pair[idx][1] = ByteBuffer.wrap(tmpa).getInt();
                bi.read(tmpa, 0, 4);
                pair[idx][2] = ByteBuffer.wrap(tmpa).getInt();
                idx++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(idx + " " + sumPairCount + " " + nowPairCount);
        for (idx = sumPairCount - nowPairCount; idx < sumPairCount; idx++) {
            pair[idx][0] = pairList[idx - sumPairCount + nowPairCount][0];
            pair[idx][1] = pairList[idx - sumPairCount + nowPairCount][1];
            pair[idx][2] = pairList[idx - sumPairCount + nowPairCount][2];
        }
        return pair;
    }
}