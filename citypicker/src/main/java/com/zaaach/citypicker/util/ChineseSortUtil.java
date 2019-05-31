package com.zaaach.citypicker.util;

import com.zaaach.citypicker.model.City;

import java.util.List;

/**
 * @Description: 对汉字进行排序
 * @Author: Liangchaojie
 * @Create On 2018/2/26 12:51
 */

/**
 * ---------------------
 * 作者：LosingCarryJie
 * 来源：CSDN
 * 原文：https://blog.csdn.net/LosingCarryJie/article/details/79376001
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */

public class ChineseSortUtil {
    public static void sortList(List<City> list) {
        transferList(list);
    }

    /**
     * 进行冒泡排序
     *
     * @param list
     */
    private static void transferList(List<City> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                exchangeNameOrder(j, list);
            }
        }
    }

    /**
     * 交换两个名字的顺序,根据首字母判断
     *
     * @param j
     * @param list
     */
    private static void exchangeNameOrder(int j, List<City> list) {
        String namePinYin1 = list.get(j).getPinyin();
        String namePinYin2 = list.get(j + 1).getPinyin();

        int size = namePinYin1.length() >= namePinYin2.length() ? namePinYin2.length() : namePinYin1.length();
        for (int i = 0; i < size; i++) {
            char jc = namePinYin1.charAt(i);
            char jcNext = namePinYin2.charAt(i);
            if (jc < jcNext) {//A在B之前就不用比较了
                break;
            }
            if (jc > jcNext) {//A在B之后就直接交换,让A在前面B在后面
                City City = list.get(j);
                list.set(j, list.get(j + 1));
                list.set(j + 1, City);
                break;
            }
            //如果AB一样就继续比较后面的字母
        }
    }
}
