package com.colonolnutty.module.shareddata.utils;

/**
 * User: Jack's Computer
 * Date: 11/19/2017
 * Time: 1:13 PM
 */
public abstract class CNMathUtils {
    public static double factorial(int value) {
        double total = 1;
        for(double i = value; i > 1; i--) {
            total *= i;
        }
        return total;
    }

    /**
     * Calculates the number of permutations possible
     * @param n Number of items
     * @param r Number of items being chosen at a time
     * @return number of permutations
     */
    public static double calculateCombinations(int n, int r) {
        return factorial(n) / (factorial(r) * factorial(n - r));
    }

    public static Double roundTwoDecimalPlaces(Double val) {
        return (double)Math.round(val * 100)/100;
    }
}
