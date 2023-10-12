
public class ThreadTest extends Thread {
    public static long runTime;
    public void run() {
        long startTime = System.currentTimeMillis();
        Search.main(null);
        long endTime = System.currentTimeMillis();
        runTime = endTime - startTime;
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }

    public static void main(String[] args) {
        /*
        ThreadTest[] t = new ThreadTest[10];
        long total = 0;
        for (int i = 0; i < 10; i++)
        {
            t[i] = new ThreadTest();
            t[i].start();
            total += runTime;
        }
        System.out.println("Average time: " + total/10);
         */
        long total = 0;
        int iterNum = 100;
        int minTime = 100000, maxTime = 0;
        for (int i = 0; i < iterNum; i++) {
            long startTime = System.currentTimeMillis();
            Search.main(null);
            long endTime = System.currentTimeMillis();
            total += endTime - startTime;
            minTime = Math.min(minTime, (int)(endTime - startTime));
            maxTime = Math.max(maxTime, (int)(endTime - startTime));
        }
        System.out.println("Min time: " + minTime);
        System.out.println("Average time: " + total/iterNum);
        System.out.println("Max time: " + maxTime);
    }

}
