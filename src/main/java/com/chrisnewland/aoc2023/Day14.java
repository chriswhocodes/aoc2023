package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day14
{
    public static void main(String[] args) throws Exception
    {
        new Day14();
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
            locations = new char[rows][columns];
        }

        public void tiltNorth()
        {
            for (int col = 0; col < columns; col++)
            {
                boolean changed;

                do
                {
                    changed = false;

                    for (int row = 0; row < rows - 1; row++)
                    {
                        char north = locations[row][col];
                        char south = locations[row + 1][col];

                        if (north == '.' && south == 'O')
                        {
                            locations[row][col] = 'O';
                            locations[row + 1][col] = '.';
                            changed = true;
                        }
                    }
                } while (changed);
            }
        }

        public void tiltEast()
        {
            for (int row = 0; row < rows; row++)
            {
                boolean changed;

                do
                {
                    changed = false;

                    for (int col = 0; col < columns - 1; col++)
                    {
                        char east = locations[row][col + 1];
                        char west = locations[row][col];

                        if (east == '.' && west == 'O')
                        {
                            locations[row][col + 1] = 'O';
                            locations[row][col] = '.';
                            changed = true;
                        }
                    }
                } while (changed);
            }
        }

        public void tiltWest()
        {
            for (int row = 0; row < rows; row++)
            {
                boolean changed;

                do
                {
                    changed = false;

                    for (int col = columns - 2; col >= 0; col--)
                    {
                        char east = locations[row][col + 1];
                        char west = locations[row][col];

                        if (east == 'O' && west == '.')
                        {
                            locations[row][col + 1] = '.';
                            locations[row][col] = 'O';
                            changed = true;
                        }
                    }
                } while (changed);
            }
        }

        public void tiltSouth()
        {
            for (int col = 0; col < columns; col++)
            {
                boolean changed;

                do
                {
                    changed = false;

                    for (int row = rows - 2; row >= 0; row--)
                    {
                        char north = locations[row][col];
                        char south = locations[row + 1][col];

                        if (north == 'O' && south == '.')
                        {
                            locations[row][col] = '.';
                            locations[row + 1][col] = 'O';
                            changed = true;
                        }
                    }
                } while (changed);
            }
        }


        public void cycle()
        {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
        }

        public long calculateLoad()
        {
            long sum = 0;

            for (int col = 0; col < columns; col++)
            {
                for (int row = 0; row < rows; row++)
                {
                    char c = locations[row][col];

                    if (c == 'O')
                    {
                        sum += rows - row;
                    }
                }
            }

            return sum;
        }

        public void setChar(int col, int row, char c)
        {
            locations[row][col] = c;
        }

        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    char c = locations[row][col];
                    builder.append(c == 0 ? '.' : c);
                }
                builder.append("\n");
            }

            return builder.toString();
        }
    }

    private Grid grid;

    public Day14() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day14.txt.test"));

        boolean partOne = false;

        if (partOne)
        {
            grid = parse(lines);

            System.out.println(grid);

            grid.tiltNorth();

            System.out.println(grid);

            System.out.println("Part 1 load: " + grid.calculateLoad());
        }
        else
        {
            grid = parse(lines);

            System.out.println(grid);

            long counter = 0;

            long start = System.currentTimeMillis();

            for (long i = 0; i < 1_000_000_000L; i++)
            {
                if (counter++ == 1_000_000)
                {
                    System.out.println(i);
                    counter = 0;

                    long stop = System.currentTimeMillis();

                    System.out.println(stop - start);
                    start = stop;
                }
                grid.cycle();

            }

            System.out.println(grid);

            System.out.println("Part 2 load: " + grid.calculateLoad());
        }
    }

    private Grid parse(List<String> lines) throws Exception
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