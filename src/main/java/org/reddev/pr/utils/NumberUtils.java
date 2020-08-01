package org.reddev.pr.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberUtils {

    private static Map<Integer, String> numbers = new HashMap<>();

    static {
        numbers.put(0, ":zero:");
        numbers.put(1, ":one:");
        numbers.put(2, ":two:");
        numbers.put(3, ":three:");
        numbers.put(4, ":four:");
        numbers.put(5, ":five:");
        numbers.put(6, ":six:");
        numbers.put(7, ":seven:");
        numbers.put(8, ":eight:");
        numbers.put(9, ":nine:");
    }

    public static String getByNumber(int number) {
        StringBuilder sb = new StringBuilder();
        if (number == 0) sb.append(numbers.get(0)).append(" ");
        while (number > 0) {
            sb.append(numbers.get(number % 10)).append(" ");
            number = number / 10;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static int getByString(String query) {
        List<Integer> integers = new ArrayList<>();
        numbers.forEach((i, emote) -> {
            if (query.equalsIgnoreCase(emote)) {
                integers.add(i);
            }
        });
        return integers.size() != 1 ? -1 : integers.get(0);
    }

}
