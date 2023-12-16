package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day16
{
    public static void main(String[] args) throws Exception
    {
        new Day16();
    }

    private enum Direction
    {
        UP, DOWN, LEFT, RIGHT
    }

    private class Reflector
    {
        Set<Direction> alreadyReflected = new HashSet<>();

        public boolean canReflect(Direction direction)
        {
            return alreadyReflected.add(direction);
        }
    }

    private static class Beam
    {
        private Direction direction;

        private int row;
        private int col;

        private boolean finished = false;

        private static int beamId = 0;
        private int id = beamId++;

        public Beam(Direction direction, int col, int row)
        {
            this.direction = direction;
            this.row = row;
            this.col = col;
        }

        public void move()
        {
            switch (direction)
            {
                case UP -> row--;
                case DOWN -> row++;
                case RIGHT -> col++;
                case LEFT -> col--;
            }
        }

        @Override
        public String toString()
        {
            return "Beam{" +
                    "direction=" + direction +
                    ", row=" + row +
                    ", col=" + col +
                    ", finished=" + finished +
                    ", id=" + id +
                    '}';
        }
    }

    private class Grid
    {
        private int rows;
        private int columns;
        private char[][] locations;

        private Reflector[][] reflected;

        public Grid(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;
            locations = new char[rows][columns];
            reflected = new Reflector[rows][columns];
        }

        public void reset()
        {
            for (int col = 0; col < columns; col++)
            {
                for (int row = 0; row < rows; row++)
                {
                    Reflector reflector = reflected[row][col];

                    if (reflector != null)
                    {
                        reflector.alreadyReflected.clear();
                    }
                }
            }
        }

        public int energise(Beam initial)
        {
            Grid energised = new Grid(rows, columns);

            List<Beam> beams = new ArrayList<>();

            beams.add(initial);

            int activeBeams = beams.size();

            while (activeBeams > 0)
            {
                List<Beam> newBeams = new ArrayList<>();

                for (Beam beam : beams)
                {
                    if (!beam.finished)
                    {
                        char beamChar = getCharAt(beam.col, beam.row);

                        switch (beamChar)
                        {
                            case 0 ->
                            {
                                beam.finished = true;
                                activeBeams--;
                            }
                            case '-' ->
                            {
                                if (beam.direction == Direction.LEFT || beam.direction == Direction.RIGHT)
                                {
                                    // pass through
                                }
                                else
                                {
                                    beam.finished = true;
                                    activeBeams--;

                                    Reflector reflector = grid.reflected[beam.row][beam.col];

                                    if (reflector.canReflect(beam.direction))
                                    {
                                        newBeams.add(new Beam(Direction.LEFT, beam.col, beam.row));
                                        newBeams.add(new Beam(Direction.RIGHT, beam.col, beam.row));
                                    }
                                }
                            }
                            case '|' ->
                            {
                                if (beam.direction == Direction.LEFT || beam.direction == Direction.RIGHT)
                                {
                                    Reflector reflector = grid.reflected[beam.row][beam.col];

                                    beam.finished = true;
                                    activeBeams--;

                                    if (reflector.canReflect(beam.direction))
                                    {
                                        newBeams.add(new Beam(Direction.UP, beam.col, beam.row));
                                        newBeams.add(new Beam(Direction.DOWN, beam.col, beam.row));
                                    }
                                }
                                else
                                {
                                    // pass through
                                }
                            }
                            case '/' ->
                            {
                                Reflector reflector = grid.reflected[beam.row][beam.col];

                                if (reflector.canReflect(beam.direction))
                                {
                                    switch (beam.direction)
                                    {
                                        case UP -> beam.direction = Direction.RIGHT;
                                        case DOWN -> beam.direction = Direction.LEFT;
                                        case LEFT -> beam.direction = Direction.DOWN;
                                        case RIGHT -> beam.direction = Direction.UP;
                                    }
                                }
                                else
                                {
                                    beam.finished = true;
                                    activeBeams--;
                                }
                            }
                            case '\\' ->
                            {
                                Reflector reflector = grid.reflected[beam.row][beam.col];

                                if (reflector.canReflect(beam.direction))
                                {
                                    switch (beam.direction)
                                    {
                                        case UP -> beam.direction = Direction.LEFT;
                                        case DOWN -> beam.direction = Direction.RIGHT;
                                        case LEFT -> beam.direction = Direction.UP;
                                        case RIGHT -> beam.direction = Direction.DOWN;
                                    }
                                }
                                else
                                {
                                    beam.finished = true;
                                    activeBeams--;
                                }
                            }
                        }

                        if (!beam.finished)
                        {
                            energised.setChar(beam.col, beam.row, '#');
                        }

                        beam.move();
                    }
                }

                activeBeams += newBeams.size();
                beams.addAll(newBeams);
            }

            return countEnergised(energised);
        }

        private int countEnergised(Grid energised)
        {
            int sum = 0;

            for (int col = 0; col < columns; col++)
            {
                for (int row = 0; row < rows; row++)
                {
                    char c = energised.getCharAt(col, row);

                    if (c == '#')
                    {
                        sum++;
                    }
                }
            }

            return sum;
        }

        public void setChar(int col, int row, char c)
        {
            locations[row][col] = c;

            switch (c)
            {
                case '/':
                case '\\':
                case '-':
                case '|':
                    reflected[row][col] = new Reflector();
            }
        }

        public char getCharAt(int col, int row)
        {
            char c = 0;

            if (col >= 0 && col < columns && row >= 0 && row < rows)
            {
                c = locations[row][col];
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
                    builder.append(c == 0 ? '.' : c);
                }
                builder.append("\n");
            }

            return builder.toString();
        }
    }

    private Grid grid;

    public Day16() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day16.txt"));

        boolean partOne = false;

        if (partOne)
        {
            grid = parse(lines);

            int energisedCount = grid.energise(new Beam(Direction.RIGHT, 0, 0));

            System.out.println("Part 1 energised: " + energisedCount);
        }
        else
        {
            grid = parse(lines);

            int max = 0;

            for (int row = 0; row < grid.rows; row++)
            {
                grid.reset();
                max = Math.max(max, grid.energise(new Beam(Direction.RIGHT, 0, row)));
                grid.reset();
                max = Math.max(max, grid.energise(new Beam(Direction.LEFT, grid.columns - 1, row)));
            }
            for (int col = 0; col < grid.columns; col++)
            {
                grid.reset();
                max = Math.max(max, grid.energise(new Beam(Direction.DOWN, col, 0)));
                grid.reset();
                max = Math.max(max, grid.energise(new Beam(Direction.UP, col, grid.rows - 1)));
            }

            System.out.println("Part 2 max energised: " + max);
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