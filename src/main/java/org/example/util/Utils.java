package org.example.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aram.azatyan | 2/21/2024 12:15 PM
 */
public class Utils {

    // TODO: 3/19/2024 old method
    public static <T> List<T> fillAndGetList(T obj, int count) {
        if (count < 0) throw new IllegalArgumentException("negative count");
        if (count == 0) return new ArrayList<T>();

        List<T> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) list.add(obj);
        return list;
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    public static List<boolean[]> getTrueAndFalseCombinations(int length) {
        if (length <= 0) throw new IllegalArgumentException("invalid length");
        List<boolean[]> list = new ArrayList<>();
        for (int i = (1 << length) - 1; i >= 0; i--) {
            boolean[] arr = new boolean[length];
            for (int j = 0; j < length; j++) {
                arr[j] = (i & (1 << (length - j - 1))) != 0;
            }
            list.add(arr);
        }
        return list;
    }
}
