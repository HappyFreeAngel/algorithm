package com.linyingjie.math;

import java.math.BigInteger;

public class MersennePrime {

    // Returns Factorial of N
    static BigInteger factorial(int N) {
        // Initialize result
        BigInteger f = new BigInteger("1"); // Or BigInteger.ONE

        // Multiply f with 2, 3, ...N
        for (int i = 2; i <= N; i++)
            f = f.multiply(BigInteger.valueOf(i));

        return f;
    }

    static boolean isPrime(BigInteger number){

        return number.isProbablePrime(100);

    }

    static BigInteger createMasonPrime(int k){

        BigInteger masonPrime = null;
        BigInteger TWO = new BigInteger("2");
        masonPrime= TWO.pow(4*k).multiply(TWO.pow(3)).subtract(BigInteger.ONE);
        return masonPrime;
    }


    public static boolean isPrime1(long n) {

        if((n > 2 && n % 2 == 0) || n == 1) {
            return false;
        }

        for (int i = 3; i <= (int)Math.sqrt(n); i += 2) {

            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isPrime(long n){
        // Corner case
        if (n <= 1)
            return false;

        int  middle=(int)(Math.sqrt(n)+1);
        // Check from 2 to n-1
        for (int i = 2; i < middle; i++)
            if (n % i == 0) {
                System.out.println(n + "可以被" + i + "整除,所以不是素数.");
                return false;
            }

        return true;
    }

    // Driver method
    public static void main(String args[]) throws Exception {
        //int N = 20;
        //System.out.println(factorial(N));

        for(int k=0;k<10000;k++) {

            long fromTime = System.currentTimeMillis();
            BigInteger masonPrime = createMasonPrime(k);
            long toTime = System.currentTimeMillis();
            System.out.println("生成梅森素数 耗时:" + (toTime - fromTime) + "毫秒.");

            System.out.println("bitCount=" + masonPrime.bitCount() + "  bitLength=" + masonPrime.bitLength());


            String masonPrimeString = masonPrime.toString();

            long masonPrimeLong = Long.parseLong(masonPrimeString);
            System.out.println(masonPrimeString+" 是否是素数:"+isPrime(masonPrimeLong));

            System.out.println("长度是=" + masonPrimeString.length());
            System.out.println(masonPrime);
            //System.out.println(masonPrime.isProbablePrime(5));
        }
    }
}
