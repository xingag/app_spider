package com.xingag.qmxsp;

import java.util.Random;

/***
 * 产生随机数
 */

public class NumUtils
{

    /***
     * 产生随机数
     * @param min
     * @param max
     * @return
     */
    public static int geneRandom(int min, int max)
    {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /***
     * 根据偏移量产生随机数
     * @param num
     * @param offset
     * @return
     */
    public static int geneRandomWithOffset(int num, int offset)
    {
        num += geneRandom(offset * (-1), offset);
        return num;
    }

}
