package clonedetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * クローンペアからクローンセットの情報を計算するクラス
 */
public class Pair2Set {
    private ArrayList<PlaceAndRoad> placeAndRoadList = new ArrayList<>();

    /**
     * そのコード片の位置と，ペアとなるコード片のインスタンスを保持するクラス
     */
    static class PlaceAndRoad {
        /**
         * コード片の出現場所
         */
        int place;
        /**
         * コード片の探索が終わったかどうか
         */
        boolean visited = false;
        /**
         * このコード片とペアとなるコード片のリスト
         */
        ArrayList<PlaceAndRoad> direction = new ArrayList<>();
        /**
         * このコード片を含むクローンペアの位置(clonePairList中のindex)
         */
        ArrayList<Integer> indexOfPair = new ArrayList<>();

        PlaceAndRoad(int place) {
            this.place = place;
        }
    }

    /**
     * ペアを受け取り，クローンセット情報を付加したクローンペアを返す
     * （クローンIDによってクローンセットであるかを判別する）
     *
     * @param pair
     * @return
     */
    int[][] makeCloneSet(int[][] pair, boolean isCCFinderX) {
        System.out.println("Shaping Clone Pairs...");

        // 長さが同じものしかクローンセットにならないためソートをかける
        Arrays.sort(pair, Pair2Set::compareDistanceForBack);

        // 長さが同じものを集めてクローンセットであるかどうかを判別する
        int nowDistance = 0;
        int i = 0;
        int cloneID = 1;
        for (int[] aClonePairListX : pair) {
            if (nowDistance != aClonePairListX[2]) {
                cloneID = registerCloneSet(pair, cloneID);
                nowDistance = aClonePairListX[2];
            }
            makePlaceAndRoadList(aClonePairListX, i);
            i++;
        }
        registerCloneSet(pair, cloneID);

        //クローンペアを2倍にして返す
        int[][] pair2 = new int[pair.length * 2][4];
        for (int j = 0; j < pair.length; j++) {
            pair2[j * 2][0] = pair[j][0];
            pair2[j * 2][1] = pair[j][1];
            pair2[j * 2][2] = pair[j][2];
            pair2[j * 2][3] = pair[j][3];
            pair2[j * 2 + 1][0] = pair[j][1];
            pair2[j * 2 + 1][1] = pair[j][0];
            pair2[j * 2 + 1][2] = pair[j][2];
            pair2[j * 2 + 1][3] = pair[j][3];
        }

        return pair2;
    }

    private void makePlaceAndRoadList(int[] pair, int index) {
        int forward = pair[0];
        int backward = pair[1];
        PlaceAndRoad aPar = parAddList(backward);
        PlaceAndRoad bPar = parAddList(forward);
        aPar.direction.add(bPar);
        bPar.direction.add(aPar);
        aPar.indexOfPair.add(index);
        bPar.indexOfPair.add(index);
    }

    private PlaceAndRoad parAddList(int a) {
        int index;
        PlaceAndRoad tmpPar;
        if ((index = parContain(a)) == -1) {
            tmpPar = new PlaceAndRoad(a);
            placeAndRoadList.add(tmpPar);
        } else {
            tmpPar = placeAndRoadList.get(index);
        }
        return tmpPar;
    }

    private int parContain(int place) {
        for (int i = 0; i < placeAndRoadList.size(); i++) {
            if (placeAndRoadList.get(i).place == place) {
                return i;
            }
        }
        return -1;
    }

    private int registerCloneSet(int[][] pair, int cloneID) {
        if (placeAndRoadList.size() == 0) {
            return cloneID;
        }

        for (PlaceAndRoad par : placeAndRoadList) {
            if (par.visited) {
                continue;
            }
            par.visited = true;
            TreeSet<Integer> cs = new TreeSet<>();
            snake(par, cs);//これ帰ってきたらcs出来てる
            for (Integer x : cs) {
                pair[x][3] = cloneID;
            }
            cloneID++;
        }

        placeAndRoadList.clear();
        return cloneID;
    }

    private void snake(PlaceAndRoad par, TreeSet<Integer> cs) {
        for (PlaceAndRoad parChild : par.direction) {
            if (parChild.visited) {
                continue;
            }
            parChild.visited = true;
            cs.addAll(parChild.indexOfPair);
            snake(parChild, cs);
        }
    }

    private static int compareDistanceForBack(int[] s, int[] t) {
        if (s[2] < t[2])
            return -1;
        else if (s[2] > t[2])
            return 1;
        else {
            if (s[0] < t[0]) {
                return -1;
            } else if (s[0] > t[0]) {
                return 1;
            } else {
                if (s[1] < t[1]) {
                    return -1;
                } else if (s[1] > t[1]) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public static int compareForBack(int[] s, int[] t) {
        if (s[0] < t[0]) {
            return -1;
        } else if (s[0] > t[0]) {
            return 1;
        } else {
            if (s[1] < t[1]) {
                return -1;
            } else if (s[1] > t[1]) {
                return 1;
            }
        }
        return 0;
    }

    public static int CloneIDFor(int[] a, int[] b) {
        if (a[3] < b[3]) {
            return -1;
        } else if (a[3] > b[3]) {
            return 1;
        } else {
            if (a[0] < b[0]) {
                return -1;
            } else if (a[0] > b[0]) {
                return 1;
            }
        }
        return 0;
    }
}
