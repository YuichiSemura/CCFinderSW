package clonedetector;

import clonedetector.classlist.FileData;
import clonedetector.classlist.HashCount;
import clonedetector.classlist.Token;
import clonedetector.classlist.TokenData;
import common.PrintProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import static common.TokenName.*;

/**
 * Clone Detection by comparing NGrams
 */
public class NGramFinder {

    OptionReader or;
    /**
     * クローン検出のトークン数のしきい値
     */
    private int THRESHOLD;
    /**　　
     * 検出範囲についてのオプション
     */
    private int detectionRange = 0;

    public int N;

    /**
     * ハッシュでソートする(uniqueを作るために用いる，uniqueを作ったらcountソートに戻す)
     */
    public HashCount[] sethList;

    /**
     * トークンリスト
     */
    public TokenData[] tokenList;

    /**
     * ユニークリスト
     */
    private ArrayList<ArrayList<Integer>> uniqueList;

    /**
     * pairArray はクローンペアを格納するリストである
     * pairList.get(0) frontward clone index
     * pairList.get(1) backward clone index
     * pairList.get(2) distance
     * pairList.get(3) clone id
     */

    public int[][] pairArray;

    public NGramFinder(OptionReader or) {
        this.or = or;
        this.THRESHOLD = or.getThreshold();
        this.detectionRange = or.getDetectionRange();
        this.N = or.getN();

        uniqueList = new ArrayList<>();
    }

    public void doGroupLoad(int groupA, int groupB, int group, FileData fd) {

        int max = fd.filePathList.size();
        int ngramSum = IntStream.rangeClosed(0, max - 1)
                .filter(fileNum -> fileNum % group == groupA || fileNum % group == groupB)
                .map(fileNum -> fd.NGramCountList[fileNum])
                .sum();
        int tokenSum = IntStream.rangeClosed(0, max - 1)
                .filter(fileNum -> fileNum % group == groupA || fileNum % group == groupB)
                .map(fileNum -> fd.tokenCountList[fileNum])
                .sum();

        sethList = new HashCount[ngramSum];
        tokenList = new TokenData[tokenSum];

        PrintProgress ps = new PrintProgress(2);
        for (int i = 0; i < max; i++) {
            ps.plusProgress(max);
            if (i % group != groupA && i % group != groupB) {
                continue;
            }
            String extension = fd.filePathList.get(i).substring(fd.filePathList.get(i).lastIndexOf('.') + 1);
            String language;
            if (!or.isANTLRMode()) {
                language = or.extensionMapTrueEnd.get(extension);
            } else {
                language = or.getLanguage();
            }

            makeEachNgram(fd, i, language);
        }
    }

    public void loadFromCCFXD(FileData fd) {
        System.out.println("load start");

        int max = fd.filePathList.size();
        int sum = IntStream.rangeClosed(0, max - 1)
                .map(fileNum -> fd.NGramCountList[fileNum])
                .sum();
        int tokenSum = IntStream.rangeClosed(0, max - 1)
                .map(fileNum -> fd.tokenCountList[fileNum])
                .sum();

        sethList = new HashCount[sum];
        tokenList = new TokenData[tokenSum];

        PrintProgress ps = new PrintProgress(2);
        for (int fileNum = 0; fileNum < max; fileNum++) {
            ps.plusProgress(max);

            String extension = fd.filePathList.get(fileNum).substring(fd.filePathList.get(fileNum).lastIndexOf('.') + 1);
            String language = or.extensionMapTrueEnd.get(extension);

            makeEachNgram(fd, fileNum, language);
        }
    }

    private void makeEachNgram(FileData fd, int fileNum, String language) {
        CCFXPrepReload rl = new CCFXPrepReload(fd.directoryName, language);
        rl.reload(fd.filePathList.get(fileNum));
        makeFileTokenData(fileNum, fd.tokenIndexList[fileNum], rl.tokenList, or.languageRuleMap.get(language).reserved);
        makeFileNgram(fileNum, fd.NGramIndexList[fileNum], rl.tokenList, or.languageRuleMap.get(language).reserved);
    }

    /**
     *
     */
    private void makeFileTokenData(int fileNumber, int count, ArrayList<Token> lineList, boolean reserved) {
        int i = 0;
        for (Token token : lineList) {
            tokenList[count + i] = new TokenData(token, getNewHash(reserved, token), fileNumber, i);
            i++;
        }
    }

    /**
     * make Ngram
     *
     * @param fileNumber ファイル番号
     * @param lineList   トークンリスト
     */
    private void makeFileNgram(int fileNumber, int count, ArrayList<Token> lineList, boolean reserved) {
        for (int j = N - 1; j < lineList.size(); j++) {
            int tmpHash = 0;
            for (int k = N - 1; k >= 0; k--) {
                Token token = lineList.get(j - k);
                tmpHash += getNewHash(reserved, token);
                tmpHash *= N - 1;
            }
            sethList[count + j - N + 1] = new HashCount(tmpHash, count + j - N + 1, fileNumber, j - N + 1);
        }
    }


    /**
     * main of clone detection
     * compare Hash of NGram
     * search UniqueList to ClonePairData
     */
    public void searchPair(int groupA, int groupB, int group, FileData fd) {
        if (uniqueList.size() == 0) {
            System.out.println("no ngram");
            return;
        }

        System.out.println("sethSize=" + sethList.length + " uniqueSize=" + uniqueList.size() + "\ncompare start");

        PrintProgress ps = new PrintProgress(2);
        LinkedHashMap<Long, Integer> map = new LinkedHashMap<>();
        for (ArrayList<Integer> cloneClass : uniqueList) {
            if (cloneClass.size() != 1) {
                for (int j = 0; j < cloneClass.size(); j++) {
                    for (int k = j + 1; k < cloneClass.size(); k++) {
                        int jStartIndex = cloneClass.get(j);
                        int kStartIndex = cloneClass.get(k);
                        int jFile = sethList[jStartIndex].file;
                        int kFile = sethList[kStartIndex].file;
                        if ((detectionRange == 1 && jFile != kFile) || (detectionRange == 2 && jFile == kFile)) {
                            continue;
                        }
                        if (!((jFile % group == groupA && kFile % group == groupB)
                                || (jFile % group == groupB && kFile % group == groupA))) {
                            continue;
                        }
                        int x = 0;
                        do {
                            x += N;
                        } while (jStartIndex + x < sethList.length && kStartIndex + x < sethList.length &&
                                jFile == sethList[jStartIndex + x].file && kFile == sethList[kStartIndex + x].file &&
                                sethList[jStartIndex + x].hash == sethList[kStartIndex + x].hash);
                        x = x - N;
                        do {
                            x++;
                        } while (jStartIndex + x < sethList.length && kStartIndex + x < sethList.length &&
                                jFile == sethList[jStartIndex + x].file && kFile == sethList[kStartIndex + x].file &&
                                sethList[jStartIndex + x].hash == sethList[kStartIndex + x].hash);
                        x--;
                        if (kStartIndex + x + 1 == sethList.length || sethList[kStartIndex + x + 1].file != kFile) {
                            if (tokenList[fd.tokenIndexList[kFile] + sethList[kStartIndex + x].num + N - 1].hash == "eof".hashCode()) {
                                x--;
                            }
                        }
                        if (x + N < THRESHOLD) {
                            continue;
                        }

                        int distance = x + N;
                        int lastJ = fd.tokenIndexList[jFile] + sethList[jStartIndex + x].num + N - 1;
                        int lastK = fd.tokenIndexList[kFile] + sethList[kStartIndex + x].num + N - 1;

                        long tmpLong = lastJ * ((long) Integer.MAX_VALUE + 1) + lastK;
                        if (!map.containsKey(tmpLong) || map.get(tmpLong) < distance) {
                            map.put(tmpLong, distance);
                        }
                    }
                }
            }
            ps.plusProgress(uniqueList.size());
        }
        pairArray = mapToArray(map);
    }

    private int[][] mapToArray(LinkedHashMap<Long, Integer> map) {
        int[][] pair = new int[map.keySet().size()][3];
        int idx = 0;
        for (long x : map.keySet()) {
            pair[idx][0] = (int) (x >>> 31);
            pair[idx][1] = (int) (x & 0x7FFFFFFF);
            pair[idx][2] = map.get(x);
            idx++;
        }
        Arrays.sort(pair, NGramFinder::compareForBackDistance);
        return pair;
    }

    /**
     * This method  makes sorted "sethList" into uniqueList.
     * <p>
     * sethList: appearance list
     */
    public void sortAndUnique() {
        Arrays.sort(sethList, Comparator.comparingInt(s -> s.hash));

        int nowHash = 0;
        int max = 0;
        for (int i = 0; i < sethList.length; i++) {
            if (nowHash == sethList[i].hash) {
                uniqueList.get(uniqueList.size() - 1).add(sethList[i].count);
            } else {
                while (i + 1 < sethList.length && sethList[i].hash != sethList[i + 1].hash) {
                    i++;
                }
                if (uniqueList.size() > 0)
                    max = Math.max(max, uniqueList.get(uniqueList.size() - 1).size());
                nowHash = sethList[i].hash;
                ArrayList<Integer> upl = new ArrayList<>();
                upl.add(sethList[i].count);
                uniqueList.add(upl);
            }
        }

        Arrays.sort(sethList, Comparator.comparingInt(s -> s.count));
    }

    private static int compareForBackDistance(int[] s, int[] t) {
        if (s[0] < t[0]) {
            return -1;
        } else if (s[0] > t[0]) {
            return 1;
        } else {
            if (s[1] < t[1]) {
                return -1;
            } else if (s[1] > t[1]) {
                return 1;
            } else {
                if (s[2] < t[2]) {
                    return -1;
                } else if (s[2] > t[2]) {
                    return 1;
                }
            }
        }
        return 0;
    }
}