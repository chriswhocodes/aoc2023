package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day3
{
    public static void main(String[] args) throws IOException
    {
        new Day3();
    }

    private char[][] schematic;
    private int rows;
    private int columns;

    public Day3() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day3.txt"));

        createSchematic(lines);
        
        int sum = getSchematicSum();

        System.out.println("Part 1 sum: " + sum);

        int gearSum = getGearSum();

        System.out.println("Part 2 sum: " + gearSum);

    }

    private void createSchematic(List<String> lines)
    {
        this.columns = lines.get(0).trim().length();

        this.rows = lines.size();

        schematic = new char[rows][columns];

        int row = 0;

        for (String line : lines)
        {
            for (int i = 0; i < columns; i++)
            {
                char c = line.charAt(i);

                schematic[row][i] = c;
            }

            row++;
        }
    }

    private int getSchematicSum()
    {
        int sum = 0;

        for (int row = 0; row < rows; row++)
        {
            boolean inNumber = false;
            StringBuilder numberBuilder = new StringBuilder();

            int digitStartColumn = 0;

            for (int col = 0; col < columns; col++)
            {
                char c = schematic[row][col];

                if (Character.isDigit(c))
                {
                    if (!inNumber)
                    {
                        digitStartColumn = col;
                        inNumber = true;
                    }
                    numberBuilder.append(c);
                }

                if (!Character.isDigit(c) || col == columns - 1)
                {
                    if (inNumber)
                    {
                        inNumber = false;
                        int digitEndColumn = col - 1;

                        int number = Integer.parseInt(numberBuilder.toString());

                        numberBuilder.setLength(0);

                        if (adjacentToSymbol(row, digitStartColumn, digitEndColumn))
                        {
                            sum += number;
                        }
                    }
                }
            }
        }

        return sum;
    }

    private boolean adjacentToSymbol(int numberRow, int startCol, int endCol)
    {
        int rowAbove = numberRow == 0 ? numberRow : numberRow - 1;
        int rowBelow = numberRow == rows - 1 ? numberRow : numberRow + 1;

        int colLeft = startCol == 0 ? startCol : startCol - 1;
        int colRight = endCol == columns - 1 ? endCol : endCol + 1;

        for (int row = rowAbove; row <= rowBelow; row++)
        {
            for (int col = colLeft; col <= colRight; col++)
            {
                char c = schematic[row][col];

                if (!Character.isDigit(c) && c != '.')
                {
                    return true;
                }
            }
        }

        return false;
    }

    private int getGearSum()
    {
        int sum = 0;

        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < columns; col++)
            {
                char c = schematic[row][col];

                if (c == '*')
                {
                    sum += findGear(row, col);
                }
            }
        }

        return sum;
    }

    // ABC
    // D*E
    // FGH
    private int findGear(int starRow, int starCol)
    {
        boolean a = isDigitAtPosition(starRow - 1, starCol - 1);
        boolean b = isDigitAtPosition(starRow - 1, starCol);
        boolean c = isDigitAtPosition(starRow - 1, starCol + 1);

        boolean d = isDigitAtPosition(starRow, starCol - 1);
        boolean e = isDigitAtPosition(starRow, starCol + 1);

        boolean f = isDigitAtPosition(starRow + 1, starCol - 1);
        boolean g = isDigitAtPosition(starRow + 1, starCol);
        boolean h = isDigitAtPosition(starRow + 1, starCol + 1);

        int numbersFound = 0;
        int products = 1;

        if (b)
        {
            products *= getNumberThatIntersects(starRow - 1, starCol);
            numbersFound++;
        }
        else
        {
            if (a)
            {
                products *= getNumberThatIntersects(starRow - 1, starCol - 1);
                numbersFound++;
            }

            if (c)
            {
                products *= getNumberThatIntersects(starRow - 1, starCol + 1);
                numbersFound++;
            }
        }

        if (d)
        {
            products *= getNumberThatIntersects(starRow, starCol - 1);
            numbersFound++;
        }

        if (e)
        {
            products *= getNumberThatIntersects(starRow, starCol + 1);
            numbersFound++;
        }

        if (g)
        {
            products *= getNumberThatIntersects(starRow + 1, starCol);
            numbersFound++;
        }
        else
        {
            if (f)
            {
                products *= getNumberThatIntersects(starRow + 1, starCol - 1);
                numbersFound++;
            }

            if (h)
            {
                products *= getNumberThatIntersects(starRow + 1, starCol + 1);
                numbersFound++;
            }
        }

        return (numbersFound < 2) ? 0 : products;
    }

    private boolean isDigitAtPosition(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < columns)
        {
            return Character.isDigit(schematic[row][col]);
        }

        return false;
    }

    private int getNumberThatIntersects(int row, int col)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(schematic[row][col]);

        boolean movingLeft = true;
        boolean movingRight = true;

        int startLeft = col - 1;

        while (movingLeft)
        {
            if (isDigitAtPosition(row, startLeft))
            {
                builder.insert(0, schematic[row][startLeft]);
                startLeft--;
            }
            else
            {
                movingLeft = false;
            }
        }

        int startRight = col + 1;

        while (movingRight)
        {
            if (isDigitAtPosition(row, startRight))
            {
                builder.append(schematic[row][startRight]);
                startRight++;
            }
            else
            {
                movingRight = false;
            }
        }

        return Integer.parseInt(builder.toString());
    }
}