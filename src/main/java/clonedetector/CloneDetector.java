package clonedetector;

import clonedetector.classlist.ClonePairData;
import clonedetector.classlist.FileData;
import common.FileAndString;
import common.Time;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class CloneDetector {

    public static void main(String[] args) {
        new CloneDetector().start(args);
    }

    public void start(String[] args) {
        //parse commandline
        OptionReader or = new OptionReader();
        new CloneDetectorCLIParser(or).commandline(args);
        if (!or.isANTLRMode()) {
            or.readCommentReservedFiles();
        } else {
            or.ANTLRInitializer();
        }

        //build instance
        FileData fd = new FileData();
        Lexer lx = new Lexer(or, fd);
        NGramFinder nfBoss = new NGramFinder(or);
        ClonePairData cpd = new ClonePairData();

        //Lexer
        lexer(or, fd, lx);

        //clone detection
        cloneDetection(or, nfBoss, fd, cpd);

        //delete ccfswtmp
        deleteCcfswTmp(or);

        //ccfx output
        ccfxOutput(or, fd, nfBoss, cpd);
    }

    private void lexer(OptionReader or, FileData fd, Lexer lx) {
        Time time = new Time();
        if (!or.isNoLexer()) {
            System.out.println("---Lexer start---" + System.lineSeparator() + "Lexer progress");
            //ファイルサーチ
            lx.searchDirectory();
            //全ファイルのレクサー
            lx.doPreProcess();
            //情報の出力
            lx.outputFileList();
            System.out.println("\nLexer " + time.end() + "\nLOC = " + fd.lineCount + " Token = " + fd.tokenCount + "\n---Lexer end---\n");
        } else {
            //noLexer
            System.out.println("noLexer mode");
            lx.loadLexer();
        }
        lx = null;
    }

    public static void cloneDetection(OptionReader or, NGramFinder nfBoss, FileData fd, ClonePairData cpd) {
        System.out.println("\n---CloneDetection start---");
        Time time = new Time();
        int group = or.getGroup();
        int[] pairCount = new int[group * group];
        IntStream.rangeClosed(0, group * group - 1)
                .filter(i -> i / group <= i % group)
                .forEach(i -> {
                    int groupA = i / group;
                    int groupB = i % group;
                    NGramFinder nf2;
                    if (group != 1) {
                        nf2 = new NGramFinder(or);
                    } else {
                        nf2 = nfBoss;
                    }
                    System.out.println("load start");
                    nf2.doGroupLoad(groupA, groupB, group, fd);
                    nf2.sortAndUnique();
                    nf2.searchPair(groupA, groupB, group, fd);
                    TempClonePairs.outputTempClonePair(nf2, or.getDirectory(), groupA, groupB, group);
                    pairCount[i] = nf2.pairArray != null ? nf2.pairArray.length : 0;
                });

        //reload clone from tmp
        int sumPair = IntStream.rangeClosed(0, group * group - 1)
                .filter(i -> i / group <= i % group)
                .map(i -> pairCount[i])
                .sum();
        cpd.pairListTrue = TempClonePairs.loadTempClonePair(sumPair, or.getDirectory(), group, fd);

        //make cloneSet
        if (cpd.pairListTrue.length > 0) {
            Pair2Set ps = new Pair2Set();
            cpd.pairListTrue = ps.makeCloneSet(cpd.pairListTrue, or.isCcfinderx());
            System.out.println("ClonePairData size=" + cpd.pairListTrue.length);
        }
        System.out.println("---CloneDetection end " + time.end() + "---\n");
    }

    private void deleteCcfswTmp(OptionReader or) {
        ArrayList<String> ccfswTmp = FileAndString.searchDirectory(or.getDirectory());
        for (String x : ccfswTmp) {
            if (x.endsWith(".ccfswtmp")) {
                new File(x).delete();
            }
        }
    }

    private void ccfxOutput(OptionReader or, FileData fd, NGramFinder nfBoss, ClonePairData cpd) {
        System.out.println("---Output start---");
        Time time = new Time();

        //ccf
        if (or.isCcfinder()) {
            Arrays.sort(cpd.pairListTrue, Pair2Set::CloneIDFor);
            new CCFFormatter(fd, nfBoss, or, cpd).outputCCF();
        }

        //ccfx
        if (or.isCcfinderx()) {
            Arrays.sort(cpd.pairListTrue, Pair2Set::compareForBack);
            new CCFXFormatter(fd, nfBoss, or, cpd).outputCCFX();
        }

        //ccfsw output
        if (or.isCcfindersw()) {
            CCFSWFormatter ccfsw = new CCFSWFormatter(fd, nfBoss, or, cpd);
            if (or.isCloneSet()) {
                Arrays.sort(cpd.pairListTrue, Pair2Set::CloneIDFor);
                ccfsw.outputCCFSWFormatSet();
            } else {
                Arrays.sort(cpd.pairListTrue, Pair2Set::compareForBack);
                ccfsw.outputCCFSWFormatPair();
            }
        }

        //json output
        if (or.isJson()) {
            Arrays.sort(cpd.pairListTrue, Pair2Set::compareForBack);
            RigelJSONFormatter rigel = new RigelJSONFormatter(fd, nfBoss, or, cpd);
            rigel.outputRigelJSONFormatter();
        }

        System.out.println("---Output end " + time.end() + "---\n");
    }
}
