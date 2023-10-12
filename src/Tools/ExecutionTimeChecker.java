package Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class ExecutionTimeChecker {
    private BigInteger startTime;
    private ExecutionTimeCheckerInterface runner;

    public ExecutionTimeChecker(ExecutionTimeCheckerInterface callback){
        this.setCallback(callback);
    }

    private void start(){
        this.startTime = BigInteger.valueOf(System.nanoTime());
    }

    private  BigInteger finish(){
//        return System.nanoTime() - this.startTime;
        return BigInteger.valueOf(System.nanoTime()).subtract(this.startTime);
//        return this.startTime.subtract(BigInteger.valueOf(System.nanoTime()));
    }

    private void setCallback(ExecutionTimeCheckerInterface callback){
        this.runner = callback;
    }

    private BigInteger execute(){
        this.start();
        this.runner.run();;
        return this.finish();
    }

    public Result run(int loops){
        BigInteger totalTime = runLoops(loops);
        BigDecimal averageTime = new BigDecimal(totalTime).divide(BigDecimal.valueOf(loops), 10, RoundingMode.HALF_UP);
        long sizeInMemory = getSize();

        return new Result(totalTime, averageTime, sizeInMemory, loops);
    }

    private BigInteger runLoops(int loops){
        BigInteger time = BigInteger.valueOf(0);
        for (int i = 0; i<loops; i++){
            time = time.add(this.execute());
        }
        return time;
    }

    private long getSize(){
        try{
            return this.getObjectSize(this.runner);
        }catch (IOException e){
            return -1;
        }
    }

    private long getObjectSize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.runner);
        return baos.toByteArray().length;
    }

}

