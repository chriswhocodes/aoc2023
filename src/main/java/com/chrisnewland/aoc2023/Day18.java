package com.chrisnewland.aoc2023;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day18
{
    public static void main(String[] args) throws Exception
    {
        new Day18();
    }

    private static class DigStep
    {
        private char direction;
        private int length;
        private String colour;

        public DigStep(String line, boolean partOne)
        {
            String[] parts = line.replace("(", "").replace(")", "").trim().split("\\s+");

            if (partOne)
            {
                direction = parts[0].charAt(0);
                length = Integer.parseInt(parts[1]);
                colour = parts[2];
            }
            else
            {
                length = Integer.parseInt(parts[2].substring(1, 6), 16);

                char dirChar = parts[2].charAt(6);

                direction = switch (dirChar)
                {
                    case '0' -> 'R';
                    case '1' -> 'D';
                    case '2' -> 'L';
                    case '3' -> 'U';
                    default -> throw new RuntimeException("unknown direction");
                };
            }
        }

        @Override
        public String toString()
        {
            return "DigStep{" +
                    "direction=" + direction +
                    ", length=" + length +
                    ", colour='" + colour + '\'' +
                    '}';
        }
    }

    private class Dimensions
    {
        private int minRow;
        private int maxRow;
        private int minCol;
        private int maxCol;

        public void update(int row, int col)
        {
            minRow = Math.min(row, minRow);
            maxRow = Math.max(row, maxRow);
            minCol = Math.min(col, minCol);
            maxCol = Math.max(col, maxCol);
        }

        @Override
        public String toString()
        {
            return "Dimensions{" +
                    "minRow=" + minRow +
                    ", maxRow=" + maxRow +
                    ", minCol=" + minCol +
                    ", maxCol=" + maxCol +
                    '}';
        }
    }

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

        public void flood(int col, int row)
        {
            Queue<Point> queue = new LinkedList<>();

            queue.add(new Point(col, row));

            while (!queue.isEmpty())
            {
                Point point = queue.remove();

                if (!canFill(point.x, point.y)) // filled since added to queue
                {
                    continue;
                }

                setChar(point.x, point.y, '#');

                if (canFill(point.x, point.y - 1))
                {
                    queue.add(new Point(point.x, point.y - 1));
                }

                if (canFill(point.x, point.y + 1))
                {
                    queue.add(new Point(point.x, point.y + 1));
                }

                if (canFill(point.x + 1, point.y))
                {
                    queue.add(new Point(point.x + 1, point.y));
                }

                if (canFill(point.x - 1, point.y))
                {
                    queue.add(new Point(point.x - 1, point.y));
                }
            }
        }

        private boolean canFill(int col, int row)
        {
            boolean canFill = false;

            if (col >= 0 && col < columns && row >= 0 && row < rows)
            {
                canFill = (locations[row][col] == 0);
            }

            return canFill;
        }

        public int countFilled()
        {
            int filled = 0;
            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    char c = locations[row][col];
                    if (c == '#')
                    {
                        filled++;
                    }
                }
            }

            return filled;
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
    }

    private class DigPlan
    {
        private Grid grid;

        public DigPlan(List<DigStep> steps)
        {
            Dimensions dimensions = new Dimensions();

            int row = 0;
            int col = 0;

            for (DigStep step : steps)
            {
                for (int i = 0; i < step.length; i++)
                {
                    switch (step.direction)
                    {
                        case 'U' -> row--;
                        case 'D' -> row++;
                        case 'L' -> col--;
                        case 'R' -> col++;
                    }

                    dimensions.update(row, col);
                }
            }

            int rows = 1 + dimensions.maxRow - dimensions.minRow;
            int cols = 1 + dimensions.maxCol - dimensions.minCol;

            System.out.println(dimensions);

            System.out.printf("%d, %d\n", rows, cols);

            grid = new Grid(rows, cols);

            row = Math.abs(dimensions.minRow);
            col = Math.abs(dimensions.minCol);

            for (DigStep step : steps)
            {
                for (int i = 0; i < step.length; i++)
                {
                    switch (step.direction)
                    {
                        case 'U' -> row--;
                        case 'D' -> row++;
                        case 'L' -> col--;
                        case 'R' -> col++;
                    }

                    grid.setChar(col, row, '#');
                }
            }
        }
    }

    public Day18() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day18.txt"));

        boolean partOne = true;

        if (partOne)
        {
            List<DigStep> steps = parse(lines, partOne);

            for (DigStep step : steps)
            {
                System.out.println(step);
            }

            DigPlan plan = new DigPlan(steps);

            System.out.println(plan.grid);

            plan.grid.flood(235, 3);

            System.out.println(plan.grid);

            int fill = plan.grid.countFilled();

            System.out.println(fill);
        }
        else
        {

        }
    }

    private List<DigStep> parse(List<String> lines, boolean partOne)
    {
        List<DigStep> steps = new ArrayList<>();

        for (String line : lines)
        {
            steps.add(new DigStep(line, partOne));
        }

        return steps;
    }
}