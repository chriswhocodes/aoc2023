package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Day11
{
	public static void main(String[] args) throws IOException
	{
		new Day11();
	}

	private class Universe
	{
		private List<String> rowList = new LinkedList<>();

		private int columns = 0;

		private List<Point> points;

		public void addRow(String columns)
		{
			rowList.add(columns);
			this.columns = columns.length();
		}

		public Universe expand(int factor)
		{
			for (int i = 0; i < columns; i++)
			{
				if (emptyColumn(i))
				{
					shiftPointsRight(i,  factor);
				}
			}

			int rowCount = rowList.size();

			for (int i = 0; i < rowCount; i++)
			{
				if (emptyRow(i))
				{
					shiftPointsDown(i,  factor);
				}
			}

			return this;
		}

		private boolean emptyRow(int index)
		{
			return !rowList.get(index).contains("#");
		}

		private boolean emptyColumn(int index)
		{
			for (String row : rowList)
			{
				if (row.charAt(index) != '.')
				{
					return false;
				}
			}

			return true;
		}

		private void shiftPointsRight(int columnGE, int amount)
		{
			for (Point point : points)
			{
				point.shiftRightByAmountIfColumnGE(columnGE, amount);
			}
		}

		private void shiftPointsDown(int rowGE, int amount)
		{
			for (Point point : points)
			{
				point.shiftDownByAmountIfRowGE(rowGE, amount);
			}
		}

		public long getDistanceSum()
		{
			List<Pair> pairs = makePairs(points);

			long sum = 0;

			for (Pair pair : pairs)
			{
				sum += pair.getDistance();
			}

			return sum;
		}

		public void findPoints()
		{
			points = new ArrayList<>();

			for (int row = 0; row < rowList.size(); row++)
			{
				for (int col = 0; col < columns; col++)
				{
					if (rowList.get(row).charAt(col) == '#')
					{
						points.add(new Point(row, col));
					}
				}
			}
		}

		private List<Pair> makePairs(List<Point> points)
		{
			List<Pair> pairs = new ArrayList<>();

			int pointCount = points.size();

			for (int i = 0; i < pointCount - 1; i++)
			{
				for (int j = i + 1; j < pointCount; j++)
				{
					pairs.add(new Pair(points.get(i), points.get(j)));
				}
			}

			return pairs;
		}
	}

	private class Point
	{
		private int originalRow;
		private int originalCol;

		private int shiftRight = 0;
		private int shiftDown = 0;

		public Point(int originalRow, int originalCol)
		{
			this.originalRow = originalRow;
			this.originalCol = originalCol;
		}

		public void shiftRightByAmountIfColumnGE(int ifGreaterEqual, int amount)
		{
			if (originalCol > ifGreaterEqual)
			{
				this.shiftRight += amount;
			}
		}

		public void shiftDownByAmountIfRowGE(int ifGreaterEqual, int amount)
		{
			if (originalRow > ifGreaterEqual)
			{
				this.shiftDown += amount;
			}
		}

		public int getFinalCol()
		{
			return originalCol + shiftRight;
		}

		public int getFinalRow()
		{
			return originalRow + shiftDown;
		}
	}

	private class Pair
	{
		private Point one;
		private Point two;

		public Pair(Point one, Point two)
		{
			this.one = one;
			this.two = two;
		}

		public int getDistance()
		{
			return Math.abs(one.getFinalRow() - two.getFinalRow()) + Math.abs(one.getFinalCol() - two.getFinalCol());
		}
	}

	public Day11() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day11.txt"));

		long sum1 = createUniverse(lines).expand(1).getDistanceSum();
		System.out.println("Part 1 sum: " + sum1);

		long sum2 = createUniverse(lines).expand(999999).getDistanceSum();
		System.out.println("Part 2 sum: " + sum2);
	}

	private Universe createUniverse(List<String> lines)
	{
		Universe universe = new Universe();

		for (String line : lines)
		{
			universe.addRow(line);
		}

		universe.findPoints();

		return universe;
	}
}