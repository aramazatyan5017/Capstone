package org.example.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aram.azatyan | 2/21/2024 12:15 PM
 */
public class Utils {

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
