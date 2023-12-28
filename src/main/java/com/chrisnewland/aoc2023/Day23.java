package com.chrisnewland.aoc2023;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;

public class Day23
{
    private static class Grid
    {
        private final int rows;
        private final int columns;
        private final char[][] locations;

        public Grid(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;

            //System.out.println("Creating grid: " + columns + ", " + rows);
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

        private void addIfValid(Point current, Set<Point> next, BiPredicate<Point, Point> isValidNextMove, Point... possibleMoves)
        {
            for (Point possible : possibleMoves)
            {
                if (isValidNextMove.test(possible, current))
                {
                    next.add(possible);
                }
            }
        }

        public Set<Point> getNextMoves(Point from, Direction direction, BiPredicate<Point, Point> isValidNextMove)
        {
           // //System.out.println("-----------------");
            Set<Point> nextMoves = new HashSet<>();

            Point north = new Point(from.x, from.y - 1);
            Point south = new Point(from.x, from.y + 1);
            Point east = new Point(from.x + 1, from.y);
            Point west = new Point(from.x - 1, from.y);

            switch (direction)
            {
                case NORTH -> addIfValid(from, nextMoves, isValidNextMove, north, east, west);
                case SOUTH -> addIfValid(from, nextMoves, isValidNextMove, south, east, west);
                case EAST -> addIfValid(from, nextMoves, isValidNextMove, north, south, east);
                case WEST -> addIfValid(from, nextMoves, isValidNextMove, north, south, west);
            }

            return nextMoves;
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

    public static void main(String[] args) throws IOException
    {
        new Day23();
    }

    private Grid grid;

    private enum Direction
    {
        NORTH, SOUTH, EAST, WEST
    }

    private static class Walk
    {
        private Walk parent;

        private static int ID = 0;

        private int walkId = ID++;

        public Walk(Walk parent)
        {
            this.parent = parent;
        }

        public int getLengthToStart()
        {
            return visited.size() + (parent == null ? 0 : parent.getLengthToStart());
        }

        public boolean alreadyVisited(Point point)
        {
            return visited.contains(point) || (parent == null ? false : parent.alreadyVisited(point));
        }

        public String parentWalks()
        {
            return walkId + "," + (parent == null ? "" : parent.parentWalks());
        }

        public Point getEndPoint()
        {
            return visited.get(visited.size() - 1);
        }

        private List<Point> visited = new ArrayList<>();

        public void addPoint(Point point)
        {
       //     //System.out.println("Added " + point);
            visited.add(point);
        }

        @Override
        public String toString()
        {
            if (visited.isEmpty())
            {
                return "empty";
            }
            return "Walk from " + visited.get(0) + " to " + visited.get(visited.size() - 1) + parentWalks();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            Walk walk = (Walk) o;
            return Objects.equals(visited, walk.visited);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(visited);
        }
    }

    private Set<Walk> walkSet = new HashSet<>();

    private Direction getDirection(Point from, Point to)
    {
        if (from.x > to.x)
        {
            return Direction.WEST;
        }
        else if (from.x < to.x)
        {
            return Direction.EAST;
        }
        else if (from.y < to.y)
        {
            return Direction.SOUTH;
        }
        else if (from.y > to.y)
        {
            return Direction.NORTH;
        }
        else
        {
            throw new RuntimeException("Could not determine direction");
        }
    }

    private boolean isValidNextMovePartOne(Point next, Point current)
    {
        char c = grid.getChar(next.x, next.y);

        Direction direction = getDirection(current, next);

        return switch (c)
        {
            case '.' -> true;
            case '>' -> direction == Direction.EAST;
            case '<' -> direction == Direction.WEST;
            case '^' -> direction == Direction.NORTH;
            case 'v' -> direction == Direction.SOUTH;
            default -> false;
        };
    }

    private void findWalks(Walk parentWalk, Point start, Point end, Direction direction, boolean partOne)
    {
        int col = start.x;
        int row = start.y;

        Point current = new Point(col, row);

        Walk currentWalk = new Walk(parentWalk);
        boolean unique = walkSet.add(currentWalk);

        if (!unique)
        {
            //System.out.println("not unique:"+ currentWalk);
            System.exit(-1);
        }

        currentWalk.addPoint(current);

        do
        {
            //System.out.println("current:" + current + " direction:" + direction);

            //System.out.println(walkSet.size() + "  " + currentWalk);

            Set<Point> nextMoves;

            if (partOne)
            {
                nextMoves = grid.getNextMoves(current, direction, this::isValidNextMovePartOne);
            }
            else
            {
                nextMoves = grid.getNextMoves(current, direction, (pointNext, pointCurrent) ->
                {
                    char c = grid.getChar(pointNext.x, pointNext.y);

                    boolean canMove;
                    if (c != '#')
                    {
                        boolean alreadyVisited = currentWalk.alreadyVisited(pointNext);

                        //System.out.println("Already visited " + pointNext + " = " + alreadyVisited);

                        canMove = !alreadyVisited;
                    }
                    else
                    {
                        canMove = false;
                    }

                    return canMove;
                });
            }

            //System.out.println("nextMoves: " + nextMoves.size());

            if (nextMoves.size() == 1)
            {
                //grid.setChar(current.x, current.y, 'O');
                Point next = nextMoves.toArray(new Point[1])[0];
                direction = getDirection(current, next);
                current = next;
                currentWalk.addPoint(current);
            }
            else if (nextMoves.isEmpty())
            {
               //System.out.println("no valid moves");
                return;
            }
            else
            {

                //grid.setChar(current.x, current.y, (""+DP).charAt(0));
                //DP++;
                //System.out.println(grid);

                //System.out.println("DECISION POINT " + current + " from parent " + currentWalk.parentWalks());

                for (Walk walk : walkSet)
                {
                    //System.out.println(walk);
                }


                for (Point nextStart : nextMoves)
                {
                    //System.out.println("nextMove: " + nextStart);
                    findWalks(currentWalk, nextStart, end, getDirection(current, nextStart), partOne);
                }

                return;
            }

        } while (!current.equals(end));

        System.out.println("finished walk: " + currentWalk);
    }

    private static int DP = 0;

    public Day23() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day23.txt"));

        boolean partOne = false;

        grid = parse(lines);

        //System.out.println(grid);

        Point startPoint = new Point(1, 0);
        Point endPoint = new Point(grid.rows - 2, grid.columns - 1);

        findWalks(null, startPoint, endPoint, Direction.SOUTH, partOne);

        //System.out.println(grid);

        int longestWalk = 0;

        for (Walk walk : walkSet)
        {
            System.out.println(walk);

            if (walk.getEndPoint().equals(endPoint))
            {
                int walkLength = walk.getLengthToStart() - 1;

                longestWalk = Math.max(longestWalk, walkLength);

                System.out.println("total length: " + walkLength);
            }
        }

        System.out.println("Day 23 longest walk part : " + longestWalk);
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