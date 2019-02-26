package clonedetector;

import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import common.PrintProgress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CCFXFormatter {
    private String language;
    private int[] tokenCountList;
    private ArrayList<String> fileNameList;
    private String directoryNameAbsolute;
    private ClonePairData cpd;

    private NGramFinder nf;
    private String filename;
    private int threshold;

    public CCFXFormatter(FileData fileData, NGramFinder nf, OptionReader or, ClonePairData cpd) {
        tokenCountList = fileData.tokenCountList;
        fileNameList = fileData.fileNameList;
        directoryNameAbsolute = fileData.directoryNameAbsolute;

        this.nf = nf;
        this.language = or.getLanguage();
        this.filename = or.getOutput();
        this.threshold = or.getThreshold();
        this.cpd = cpd;
    }

    public void outputCCFX() {
        String filename = this.filename + ".ccfxd";
        System.out.println("ccfxd file = " + filename);

        try (FileOutputStream out = new FileOutputStream(filename)) {
            out.write("ccfxraw0\n".getBytes());
            byte[] tmp2 = {0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x70, 0x61, 0x3A, 0x64};
            out.write(tmp2);

            out.write(("b\t" + threshold + "\n").getBytes());
            out.write("s\t2\n".getBytes());
            out.write("k\t+\n".getBytes());
            out.write("t\t12\n".getBytes());
            out.write("w\tf+g+w+\n".getBytes());
            out.write("j\t+\n".getBytes());
            out.write("k\t60m\n".getBytes());
            out.write(("preprocessed_file_postfix\t." + language + ".2_0_0_2.default.ccfxprep\n").getBytes());
            out.write("pp\t+\n".getBytes());
            out.write(("n\t" + directoryNameAbsolute + "\n").getBytes());

            out.write(0x0A);
            out.write(language.getBytes());
            out.write(0x0A);

            for (int i = 0; i < fileNameList.size(); i++) {
                out.write(fileNameList.get(i).getBytes());
                out.write(0x0A);

                byte[] tmp_number = ByteBuffer.allocate(4).putInt(i + 1).array();
                for (int j = 0; j < 4; j++)
                    out.write(tmp_number[3 - j]);

                byte[] tmp_token = ByteBuffer.allocate(4).putInt(tokenCountList[i]).array();
                for (int j = 0; j < 4; j++)
                    out.write(tmp_token[3 - j]);
            }
            out.write(0x0A);

            for (int i = 0; i < 8; i++)
                out.write(0x00);
            out.write(0x0A);

            for (int i = 0; i < 4; i++)
                out.write(0x00);

            PrintProgress ps = new PrintProgress(2);
            //clone pair 一つは 32byte
            for (int[] k : cpd.pairListTrue) {
                byte[] outClone = new byte[32];
                int distance = k[2];
                int count = k[0];// クローン内の一番最後のNgramが全Ngram中何番目か

                int filenum = nf.tokenList[count - distance + 1].file + 1;
                int tokennum = nf.tokenList[count - distance + 1].num;

                byte[] tmp_file = ByteBuffer.allocate(4).putInt(filenum).array();
                for (int j = 0; j < 4; j++)
                    outClone[j] = tmp_file[3 - j];
                byte[] tmp_token = ByteBuffer.allocate(4).putInt(tokennum).array();
                for (int j = 0; j < 4; j++)
                    outClone[j + 4] = tmp_token[3 - j];

                tokennum = tokennum + distance;
                tmp_token = ByteBuffer.allocate(4).putInt(tokennum).array();
                for (int j = 0; j < 4; j++)
                    outClone[j + 8] = tmp_token[3 - j];

                count = k[1];// クローン内の一番最後のNgramが全Ngram中何番目か
                filenum = nf.tokenList[count - distance + 1].file + 1;
                tokennum = nf.tokenList[count - distance + 1].num;
                tmp_file = ByteBuffer.allocate(4).putInt(filenum).array();
                tmp_token = ByteBuffer.allocate(4).putInt(tokennum).array();
                for (int j = 0; j < 4; j++)
                    outClone[j + 12] = tmp_file[3 - j];
                for (int j = 0; j < 4; j++)
                    outClone[j + 16] = tmp_token[3 - j];
                tokennum = tokennum + distance;
                tmp_token = ByteBuffer.allocate(4).putInt(tokennum).array();
                for (int j = 0; j < 4; j++)
                    outClone[j + 20] = tmp_token[3 - j];
                byte[] tmp_id = ByteBuffer.allocate(4).putInt(k[3]).array();
                for (int j = 0; j < 4; j++)
                    outClone[j + 24] = tmp_id[3 - j];
                for (int j = 0; j < 4; j++)
                    outClone[j + 28] = 0x00;

                out.write(outClone);
                ps.plusProgress(cpd.pairListTrue.length);
            }

            for (int i = 0; i < 32; i++)
                out.write(0x00);
            out.write(0x0A);
            for (int i = 0; i < 8; i++)
                out.write(0x00);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
