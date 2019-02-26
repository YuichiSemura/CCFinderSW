package common;

public class Time {
    private long start;

    public Time() {
        start();
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public String end() {
        long end = System.currentTimeMillis();
        String x = (end - start) + "ms";
        start = end;
        return x;
    }
}
