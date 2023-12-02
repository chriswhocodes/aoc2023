package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day2
{
    private static class Sample
    {
        int red;
        int green;
        int blue;

        public Sample(int red, int green, int blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int power()
        {
            return red * green * blue;
        }
    }

    public static void main(String[] args) throws IOException
    {
        new Day2();
    }

    public Day2() throws IOException
    {
        List<String> games = Files.readAllLines(Paths.get("src/main/resources/day2.txt"));

        int gameNumber = 1;

        int possibleSum = 0;

        int powerSum = 0;

        for (String game : games)
        {
            List<Sample> samples = getSamples(game);

            if (possible(samples, 12, 13, 14))
            {
                possibleSum += gameNumber;
            }

            gameNumber++;

            powerSum += getMinimum(samples).power();
        }

        System.out.println("Part 1 (sum of possible game numbers): " + possibleSum);

        System.out.println("Part 2 (sum of powers): " + powerSum);
    }

    private List<Sample> getSamples(String line)
    {
        List<Sample> result = new ArrayList<>();

        int colonIndex = line.indexOf(':');

        if (colonIndex != -1)
        {
            line = line.substring(colonIndex + 1);

            String[] games = line.split(";");

            for (String game : games)
            {
                String[] colours = game.split(",");

                int red = 0;
                int green = 0;
                int blue = 0;

                for (String colour : colours)
                {
                    String[] counts = colour.trim().split(" ");

                    if (counts.length != 2)
                    {
                        throw new RuntimeException("Bad parse:" + colour);
                    }

                    int amount = Integer.parseInt(counts[0]);

                    String colourName = counts[1];

                    switch (colourName)
                    {
                        case "red":
                            red = amount;
                            break;
                        case "green":
                            green = amount;
                            break;
                        case "blue":
                            blue = amount;
                            break;
                    }
                }

                result.add(new Sample(red, green, blue));
            }
        }

        return result;
    }

    private boolean possible(List<Sample> samples, int maxRed, int maxGreen, int maxBlue)
    {
        for (Sample sample : samples)
        {
            if (sample.red > maxRed || sample.green > maxGreen || sample.blue > maxBlue)
            {
                return false;
            }
        }

        return true;
    }

    private Sample getMinimum(List<Sample> samples)
    {
        Sample minimum = new Sample(0, 0, 0);

        for (Sample sample : samples)
        {
            minimum.red = Math.max(minimum.red, sample.red);
            minimum.green = Math.max(minimum.green, sample.green);
            minimum.blue = Math.max(minimum.blue, sample.blue);
        }

        return minimum;
    }
}