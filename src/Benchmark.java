import Tools.ExecutionTimeChecker;
import Tools.ExecutionTimeCheckerInterface;
import Tools.Result;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Benchmark implements ExecutionTimeCheckerInterface {

    @Override
    public void run() {
        Search.main(null);
    }

    public static void main(String[] args) {
        ExecutionTimeChecker checker = new ExecutionTimeChecker(new Benchmark());
        Result result = checker.run(200);
        System.out.println("loops: " + result.loops);
        System.out.println("total time: " + (result.totalTime.divide(BigInteger.valueOf(1000000000))));
        System.out.println("average time: " + result.averageTime.divide(BigDecimal.valueOf(1000000000)));

    }
}
