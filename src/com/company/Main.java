package com.company;

public class Main {

    public static void main(String[] args) {
        StopWatchTimer timer = new StopWatchTimer();
        timer.start();
        String configFile = null;
        if(args.length > 0) {
            configFile = args[0];
        }
        ValueBalancer balancer = new ValueBalancer(configFile);
        balancer.run();
        timer.stop();
        long time = timer.timeInMinutes();
        String unitOfMeasurement = "minutes";
        if(time == 0) {
            time = timer.timeInSeconds();
            unitOfMeasurement = "seconds";
        }
        if(time == 0) {
            time = timer.timeInMilliseconds();
            unitOfMeasurement = "milliseconds";
        }
        System.out.println("[INFO] Finished running in " + time + " " + unitOfMeasurement);
    }
}
