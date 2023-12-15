package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day15
{
    public static void main(String[] args) throws Exception
    {
        new Day15();
    }

    //        Determine the ASCII code for the current character of the string.
//        Increase the current value by the ASCII code you just determined.
//        Set the current value to itself multiplied by 17.
//        Set the current value to the remainder of dividing itself by 256.
    private long hash(String input)
    {
        long result = 0;

        int len = input.length();

        for (int i = 0; i < len; i++)
        {
            char c = input.charAt(i);

            result += (int) c;
            result *= 17;
            result = result % 256;
        }

        System.out.println("Hash of " + input +" = " + result);

        return result;
    }

    public Day15() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day15.txt"));

        String line = lines.get(0);

        String[] parts = line.split(",");

        long sum = 0;

        for (String part : parts)
        {
            sum += hash(part);
        }

        System.out.println("Part 1 hash: " + sum);
    }
}
