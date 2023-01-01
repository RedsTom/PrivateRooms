package org.reddev.privateroomsreborn.utils.general

class NumberUtils {

    private static Map<Integer, String> numbers = [
            0: ":zero:",
            1: ":one:",
            2: ":two:",
            3: ":three:",
            4: ":four:",
            5: ":five:",
            6: ":six:",
            7: ":seven:",
            8: ":eight:",
            9: ":nine:",
    ]

    static String getByNumber(int number) {
        StringBuilder sb = new StringBuilder()
        if (number == 0) sb.append(numbers.get(0)).append(" ")
        while (number > 0) {
            sb.append(numbers.get(number % 10)).append(" ")
            number = (number / 10).toInteger()
        }
        sb.deleteCharAt(sb.length() - 1)
        return sb.toString()
    }

    static int getByString(String query) {
        List<Integer> integers = new ArrayList<>()
        numbers.forEach((i, emote) -> {
            if (query.equalsIgnoreCase(emote)) {
                integers.add(i)
            }
        })
        return integers.size() != 1 ? -1 : integers.get(0)
    }

}