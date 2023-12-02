package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day1
{
    public static void main(String[] args) throws Exception
    {
        new Day1();
    }

    public Day1() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day1.txt"));

        Map<String, Integer> replaceMap = new HashMap<>();

        replaceMap.put("1", 1);
        replaceMap.put("2", 2);
        replaceMap.put("3", 3);
        replaceMap.put("4", 4);
        replaceMap.put("5", 5);
        replaceMap.put("6", 6);
        replaceMap.put("7", 7);
        replaceMap.put("8", 8);
        replaceMap.put("9", 9);

        int digitSum = getSum(lines, replaceMap);

        System.out.println("Part 1 (sum of numbers from first and last digits) " + digitSum);

        replaceMap.put("one", 1);
        replaceMap.put("two", 2);
        replaceMap.put("three", 3);
        replaceMap.put("four", 4);
        replaceMap.put("five", 5);
        replaceMap.put("six", 6);
        replaceMap.put("seven", 7);
        replaceMap.put("eight", 8);
        replaceMap.put("nine", 9);

        int wordOrDigitSum = getSum(lines, replaceMap);

        System.out.println("Part 2 (sum of numbers from first and last digits or words) " + wordOrDigitSum);
    }

    private int getSum(List<String> lines, Map<String, Integer> numberMap)
    {
        int sum = 0;

        for (String line : lines)
        {
            sum += getValue(line.trim(), numberMap);
        }

        return sum;
    }

    private int getValue(String line, Map<String, Integer> numberMap)
    {
        int firstNumber = -1;
        int lastNumber = -1;

        int firstIndex = -1;
        int lastIndex = -1;

        for (String key : numberMap.keySet())
        {
            int index = line.indexOf(key);

            if (index != -1)
            {
                if (firstIndex == -1 || index < firstIndex)
                {
                    firstIndex = index;
                    firstNumber = numberMap.get(key);
                }
            }

            index = line.lastIndexOf(key);

            if (index != -1)
            {
                if (lastIndex == -1 || index > lastIndex)
                {
                    lastIndex = index;
                    lastNumber = numberMap.get(key);
                }
            }
        }

        return (firstNumber * 10) + lastNumber;
    }
}