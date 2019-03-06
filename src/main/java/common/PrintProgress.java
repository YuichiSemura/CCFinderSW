package common;

import java.util.stream.IntStream;

public class PrintProgress {
    private int percent = 0;
    private long progressX = 0;
    private int scale = 0;

    public PrintProgress(int scale) {
        if (scale >= 2) {
            this.scale = 2;
        } else {
            this.scale = 1;
        }
    }

    public PrintProgress() {
        this.scale = 1;
    }

    /**
     * percent = progress / max * 100
     *
     * @param max
     */
    public synchronized void plusProgress(int max) {
        if (percent > 100) {
            return;
        }
        progressX++;
        while (progressX >= percent * max / 100) {
            percent++;
            if (percent >= 100) {
                break;
            }
            if (percent % scale == 0) {
                System.out.print("|");
            }
            if (percent % (scale * 10) == 0) {
                System.out.print(percent + "%");
            }
            if (scale == 1 && percent == 50) {
                System.out.println();
            } else if (percent == 100) {
                System.out.println();
            }
        }

        if (progressX == max) {
            System.out.println("100%");
        }
    }

    public static void main(String[] args) {
        new PrintProgress().testRun();
    }

    public void testRun() {
        System.out.println("1");
        test(1);
        System.out.println("2");
        test(2);
        System.out.println("73");
        test(73);
        System.out.println("100");
        test(100);
        System.out.println("1111");
        test(1111);
        System.out.println("112400");
        test(112400);
    }

    public void test(int max) {
        PrintProgress ps = new PrintProgress(2);
        IntStream.range(0, max).forEach(i -> ps.plusProgress(max));
    }
}
