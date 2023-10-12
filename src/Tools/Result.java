package Tools;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Result {
    public BigInteger totalTime;
    public BigDecimal averageTime;
    public long sizeInMemory;
    public int loops;

    Result(BigInteger totalTime, BigDecimal averageTime, long sizeInMemory, int loops){
        this.totalTime = totalTime;
        this.averageTime = averageTime;
        this.sizeInMemory = sizeInMemory;
        this.loops = loops;
    }
}
