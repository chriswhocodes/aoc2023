package com.chrisnewland.aoc2023;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day21
{
    private class Grid
    {
        private int rows;
        private int columns;
        private char[][] locations;

        public Grid(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;

            System.out.println("Creating grid: " + columns + ", " + rows);
            locations = new char[rows][columns];
        }

        public void setChar(int col, int row, char c)
        {
            locations[row][col] = c;
        }

        public char getChar(int col, int row)
        {
            char c = 0;

            if (col >= 0 && col < columns && row >= 0 && row < rows)
            {
                return locations[row][col];
            }

            return c;
        }


        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    char c = locations[row][col];
                    builder.append(c != 0 ? c : '.');
                }
                builder.append("\n");
            }

            return builder.toString();
        }

        public Point getStartPoint()
        {
            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    char c = locations[row][col];

                    if (c == 'S')
                    {
                        return new Point(col, row);
                    }
                }
            }

            throw new RuntimeException("Start not found");
        }

        private void tryAdd(Set<Point> points, int col, int row)
        {
            char next = getChar(col, row);

            if (next == '.')
            {
                points.add(new Point(col, row));
            }
        }

        public Set<Point> getNextSteps(Set<Point> inputs)
        {
            Set<Point> next = new HashSet<>();

            for (Point input : inputs)
            {
                tryAdd(next, input.x, input.y - 1);
                tryAdd(next, input.x, input.y + 1);
                tryAdd(next, input.x + 1, input.y);
                tryAdd(next, input.x - 1, input.y);
            }

            return next;
        }
    }

    public static void main(String[] args) throws IOException
    {
        new Day21();
    }

    private Grid grid;

    public Day21() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day21.txt"));

        boolean partOne = true;

        if (partOne)
        {
            grid = parse(lines);

            System.out.println(grid);
        }

        Point startPoint = grid.getStartPoint();

        Set<Point> starting = Set.of(startPoint);

        int count = 0;

        for (int i = 0; i < 64; i++)
        {
            Set<Point> next = grid.getNextSteps(starting);
            System.out.println(next);

            starting = next;

            count = 1 + next.size();
            System.out.println("Count:" + count);
        }
    }

    private Grid parse(List<String> lines)
    {
        int rows = lines.size();
        int cols = lines.get(0).length();

        Grid grid = new Grid(rows, cols);

        int row = 0;

        for (String line : lines)
        {
            for (int col = 0; col < cols; col++)
            {
                char c = line.charAt(col);

                grid.setChar(col, row, c);
            }
            row++;
        }

        return grid;
    }
}
