package com.company;

public class Main {

    public static void main(String[] args) {
        String configFile = null;
        if(args.length > 0) {
            configFile = args[0];
        }
        ValueBalancer balancer = new ValueBalancer(configFile);
        balancer.run();
        balancer.dispose();
    }
}
