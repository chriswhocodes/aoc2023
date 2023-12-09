package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day9
{
    public static void main(String[] args) throws IOException
    {
        new Day9();
    }

    private static class Sequence
    {
        private List<Long> values = new ArrayList<>();

        private boolean allZeros = true;

        public void add(long value)
        {
            values.add(value);

            if (value != 0)
            {
                allZeros = false;
            }
        }

        public Long getFirst()
        {
            return values.get(0);
        }

        public Long getLast()
        {
            return values.get(values.size() - 1);
        }

        public Sequence getDeltaSequence()
        {
            Sequence deltas = new Sequence();

            Long previous = null;

            for (Long value : values)
            {
                if (previous != null)
                {
                    deltas.add(value - previous);
                }

                previous = value;
            }

            return deltas;
        }

        public long getExtrapolatedValueRight()
        {
            return getExtrapolatedValueRight(this);
        }

        private long getExtrapolatedValueRight(Sequence sequence)
        {
            if (sequence.allZeros)
            {
                return sequence.getLast();
            }
            else
            {
                long childLast = getExtrapolatedValueRight(sequence.getDeltaSequence());

                return childLast + sequence.getLast();
            }
        }

        public long getExtrapolatedValueLeft()
        {
            return getExtrapolatedValueLeft(this);
        }

        private long getExtrapolatedValueLeft(Sequence sequence)
        {
            if (sequence.allZeros)
            {
                return sequence.getLast();
            }
            else
            {
                long childFirst = getExtrapolatedValueLeft(sequence.getDeltaSequence());

                return sequence.getFirst() - childFirst;
            }
        }

        @Override
        public String toString()
        {
            return values.toString();
        }
    }

    private List<Sequence> sequences;

    public Day9() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day9.txt"));

        parse(lines);

        int length = sequences.size();

        long sequenceSumRight = 0;
        long sequenceSumLeft = 0;

        for (int i = 0; i < length; i++)
        {
            Sequence sequence = sequences.get(i);

            sequenceSumRight += sequence.getExtrapolatedValueRight();

            sequenceSumLeft += sequence.getExtrapolatedValueLeft();
        }

        System.out.println("Part1 sum: " + sequenceSumRight);
        System.out.println("Part2 sum: " + sequenceSumLeft);
    }

    private void parse(List<String> lines)
    {
        sequences = new ArrayList<>();

        for (String line : lines)
        {
            String[] parts = line.trim().split("\\s+");

            Sequence sequence = new Sequence();

            sequences.add(sequence);

            for (String part : parts)
            {
                sequence.add(Long.parseLong(part));
            }
        }
    }
}