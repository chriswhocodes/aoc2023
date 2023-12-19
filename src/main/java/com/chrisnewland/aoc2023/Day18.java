package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day17
{
    public static void main(String[] args) throws Exception
    {
        new Day17();
    }

    private enum Direction
    {
        FORWARD, LEFT, RIGHT
    }

    private enum Facing
    {
        NORTH, SOUTH, EAST, WEST
    }

    private class Grid
    {
        private int rows;
        private int columns;
        private int[][] costs;

        public Grid(int rows, int columns)
        {
            this.rows = rows;
            this.columns = columns;
            costs = new int[rows][columns];
        }

        public void setChar(int col, int row, int cost)
        {
            costs[row][col] = cost;
        }

        private int[][] memo;

        public int findLeastCostPath()
        {
            Facing facing = Facing.EAST;

            memo = new int[this.rows][this.columns];

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    memo[row][col] = -1;
                }
            }

            int destRow = rows - 1;
            int destCol = columns - 1;

//            return minCostDP();
            return minCostRecursive(facing, destRow, destCol, 0);
        }

        private int min3(int x, int y, int z)
        {
            return Math.min(x, Math.min(y, z));
        }

        public int minCostDP()
        {
            int col;
            int row;
            int totalCost[][] = new int[rows][columns];

            totalCost[0][0] = costs[0][0];

// Initializing the first column of the totalCost array
            for (row = 1; row < rows; row++)
            {
                totalCost[row][0] = totalCost[row - 1][0] + costs[row][0];
            }

// Initializing the first row of the totalCost
            for (col = 1; col < columns; col++)
            {
                totalCost[0][col] = totalCost[0][col - 1] + costs[0][col];
            }

// Constructing the rest of the totalCost array
            for (row = 1; row < rows; row++)
            {
                for (col = 1; col < columns; col++)
                {
                    totalCost[row][col] = min3(totalCost[row - 1][col - 1], totalCost[row - 1][col], totalCost[row][col - 1]) + costs[row][col];
                }
            }

            return totalCost[rows - 1][columns - 1];
        }

        public int minCostRecursive(Facing facing, int row, int col, int forwardCount)
        {
            System.out.printf("Facing: %s r:%d c:%d forward:%d\n", facing, row, col, forwardCount);

            if (col < 0 || row < 0 || col >= columns || row >= rows)
            {
                System.out.println("oob");
                return Integer.MAX_VALUE;
            }
            else if (row == 0 && col == 0)
            {
                return 0;
            }

            if (memo[row][col] != -1)
            {
                System.out.printf("Found cached cost in %d,%d", row, col);
                return memo[row][col];
            }

            int costForward = Integer.MAX_VALUE;
            int costLeft = Integer.MAX_VALUE;
            int costRight = Integer.MAX_VALUE;

            switch (facing)
            {
                case NORTH ->
                {
                    if (forwardCount < 2)
                    {
                        costForward = minCostRecursive(Facing.NORTH, row - 1, col, ++forwardCount);
                    }
                    else
                    {
                        System.out.println("too many forward");
                    }
                    costLeft = minCostRecursive(Facing.WEST, row, col - 1, 1);
                    costRight = minCostRecursive(Facing.EAST, row, col + 1, 1);
                }
                case SOUTH ->
                {
                    if (forwardCount < 2)
                    {
                        costForward = minCostRecursive(Facing.SOUTH, row + 1, col, ++forwardCount);
                    }
                    else
                    {
                        System.out.println("too many forward");
                    }
                    costLeft = minCostRecursive(Facing.EAST, row, col + 1, 1);
                    costRight = minCostRecursive(Facing.WEST, row, col - 1, 1);
                }
                case EAST ->
                {
                    if (forwardCount < 2)
                    {
                        costForward = minCostRecursive(Facing.EAST, row, col + 1, ++forwardCount);
                    }
                    else
                    {
                        System.out.println("too many forward");
                    }
                    costLeft = minCostRecursive(Facing.NORTH, row - 1, col, 1);
                    costRight = minCostRecursive(Facing.SOUTH, row + 1, col, 1);
                }
                case WEST ->
                {
                    if (forwardCount < 2)
                    {
                        costForward = minCostRecursive(Facing.WEST, row, col - 1, ++forwardCount);
                    }
                    else
                    {
                        System.out.println("too many forward");
                    }
                    costLeft = minCostRecursive(Facing.SOUTH, row + 1, col, 1);
                    costRight = minCostRecursive(Facing.NORTH, row - 1, col, 1);
                }
            }

            System.out.printf("forward: %d, left: %d, right: %d", costForward, costLeft, costRight);

            memo[row][col] = costs[row][col] + min3(costLeft, costForward, costRight);

            return memo[row][col];
        }

        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (int row = 0; row < rows; row++)
            {
                for (int col = 0; col < columns; col++)
                {
                    builder.append(costs[row][col]);
                }
                builder.append("\n");
            }

            builder.append(rows).append(",").append(columns);
            return builder.toString();
        }
    }

    private Grid grid;

    public Day17() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day17.txt.test"));

        boolean partOne = true;

        if (partOne)
        {
            grid = parse(lines);

            System.out.println(grid);

            int lowestCost = grid.findLeastCostPath();

            System.out.println("Part 1 least cost: " + lowestCost);
        }
        else
        {

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

                grid.setChar(col, row, Integer.parseInt("" + c));
            }
            row++;
        }

        return grid;
    }
}