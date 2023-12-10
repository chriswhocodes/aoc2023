package com.chrisnewland.aoc2023;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day10
{
    public static void main(String[] args) throws IOException
    {
        new Day10();
    }

    enum Direction
    {
        NORTH, SOUTH, EAST, WEST;

        public Direction inverse()
        {
            return switch (this)
            {
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
                case EAST -> WEST;
                case WEST -> EAST;
            };
        }
    }

    enum Pipe
    {
        START('S', Set.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
        HORIZONTAL('-', Set.of(Direction.EAST, Direction.WEST)),
        VERTICAL('|', Set.of(Direction.NORTH, Direction.SOUTH)),
        TOPLEFT('F', Set.of(Direction.SOUTH, Direction.EAST)),
        TOPRIGHT('7', Set.of(Direction.SOUTH, Direction.WEST)),
        BOTTOMLEFT('L', Set.of(Direction.NORTH, Direction.EAST)),
        BOTTOMRIGHT('J', Set.of(Direction.NORTH, Direction.WEST)),

        private char symbol;
        private Set<Direction> allowedDirections;

        Pipe(char symbol, Set<Direction> allowed)
        {
            this.symbol = symbol;
            this.allowedDirections = allowed;
        }

        public boolean canTravel(Direction direction, Pipe nextPipe)
        {
            return nextPipe != null && allowedDirections.contains(direction) && nextPipe.allowedDirections.contains(direction.inverse());
        }

        static Pipe of(char symbol)
        {
            for (Pipe pipe : values())
            {
                if (pipe.symbol == symbol)
                {
                    return pipe;
                }
            }

            return null;
        }
    }

    private class Grid
    {
        private int rows;
        private int columns;

        private char[][] locations;

        private Grid pipeGrid;

        private int startRow;
        private int startCol;
        private int currentRow;
        private int currentCol;

        public Grid(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;
            locations = new char[rows][columns];
        }

        public void setChar(int col, int row, char c)
        {
            locations[row][col] = c;

            if (c == 'S')
            {
                startCol = col;
                startRow = row;
            }
        }

        public Pipe getPipeAt(int col, int row)
        {
            if (col >= 0 && col < columns && row >= 0 && row < rows)
            {
                char c = locations[row][col];

                if (c != '.')
                {
                    return Pipe.of(c);
                }
            }

            return null;
        }

        public Direction getNextDirection(Pipe pipe, Direction last)
        {
            if (last != Direction.SOUTH && pipe.canTravel(Direction.NORTH, getPipeAt(currentCol, currentRow - 1)))
            {
                currentRow--;
                return Direction.NORTH;
            }
            else if (last != Direction.WEST && pipe.canTravel(Direction.EAST, getPipeAt(currentCol + 1, currentRow)))
            {
                currentCol++;
                return Direction.EAST;
            }
            else if (last != Direction.NORTH && pipe.canTravel(Direction.SOUTH, getPipeAt(currentCol, currentRow + 1)))
            {
                currentRow++;
                return Direction.SOUTH;
            }
            else if (last != Direction.EAST && pipe.canTravel(Direction.WEST, getPipeAt(currentCol - 1, currentRow)))
            {
                currentCol--;
                return Direction.WEST;
            }

            throw new RuntimeException("Couldn't find next direction");
        }

        public Loop getLoop()
        {
            Loop loop = new Loop();

            Direction direction = null;

            currentRow = startRow;
            currentCol = startCol;

            Pipe pipe = getPipeAt(currentCol, currentRow);

            pipeGrid = new Grid(rows, columns);

            do
            {
                loop.add(pipe, currentCol, currentRow);

                pipeGrid.setChar(currentCol, currentRow, pipe.symbol);

                direction = getNextDirection(pipe, direction);

                pipe = getPipeAt(currentCol, currentRow);

            } while (!(currentRow == startRow && currentCol == startCol));

            return loop;
        }

        public int calculatePointsInside(Polygon polygon)
        {
            int inside = 0;

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    if (pipeGrid.getPipeAt(col, row) == null)
                    {
                        if (polygon.contains(new Point(col, row)))
                        {
                            inside++;
                        }
                    }
                }
            }
            return inside;
        }

        @Override
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

    private class Loop
    {
        private List<Pipe> pipes = new ArrayList<>();

        private List<Integer> xPoints = new ArrayList<>();

        private List<Integer> yPoints = new ArrayList<>();

        public void add(Pipe pipe, int x, int y)
        {
            pipes.add(pipe);
            xPoints.add(x);
            yPoints.add(y);
        }

        public Polygon getPolygon()
        {
            int size = xPoints.size();

            int[] x = new int[size];
            int[] y = new int[size];

            for (int i = 0; i < size; i++)
            {
                x[i] = xPoints.get(i);
                y[i] = yPoints.get(i);
            }

            return new Polygon(x, y, size);
        }

        public int furthestLength()
        {
            return pipes.size() / 2;
        }

        @Override
        public String toString()
        {
            return pipes.toString();
        }
    }

    private Grid grid;

    public Day10() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day10.txt"));

        parseGrid(lines);

        Loop loop = grid.getLoop();

        int furthest = loop.furthestLength();

        System.out.println("Part 1 furthest path:" + furthest);

        int insideCount = grid.calculatePointsInside(loop.getPolygon());

        System.out.println("Part 2 inside count:" + insideCount);
    }

    private void parseGrid(List<String> lines)
    {
        int rows = lines.size();

        int cols = lines.get(0).length();

        grid = new Grid(rows, cols);

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
    }
}