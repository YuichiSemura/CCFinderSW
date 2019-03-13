package clonedetector;

import clonedetector.classlist.TokenData;

import java.util.HashSet;

public class MetricsCalculator {
    public static int calLNR(int first, int distance, TokenData[] td) {
        boolean lifeDeath[] = new boolean[distance];
        for (int i = 0; i < distance; i++) {
            lifeDeath[i] = true;
        }
        for (int a = first; a < first + distance; a++) {
            for (int b = a + 1; b < first + distance; b++) {
                int c = 0;
                while (true) {
                    if (td[a + c].hash != td[b + c].hash) {
                        break;
                    }
                    if (a + c == b - 1 || a + c >= b || b + c >= first + distance) {
                        for (int d = 0; d < c * 2; d++) {
                            lifeDeath[a - first + d] = false;
                        }
                        break;
                    }
                    c++;
                }
            }
        }
        int life = 0;
        for (boolean b : lifeDeath) {
            if (b) life++;
        }
        return life;
    }

    public static float calRNR(int first, int distance, TokenData[] td) {
        return (float) calLNR(first, distance, td) / (float) distance;
    }

    public static int calTKS(int first, int distance, TokenData[] td) {
        HashSet<Integer> tkSet = new HashSet<>();
        for (int a = first; a < first + distance; a++) {
            tkSet.add(td[a].hash);
        }
        return tkSet.size();
    }
}
