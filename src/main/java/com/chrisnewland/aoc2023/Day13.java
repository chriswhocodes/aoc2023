package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day13
{
	public static void main(String[] args) throws Exception
	{
		new Day13();
	}

	private static class Pattern
	{
		private final List<char[]> rows = new ArrayList<>();

		private class Symmetry
		{
			private int score;
			private boolean rotated;
			private int mirrorLine;

			public Symmetry(int score, boolean rotated, int mirrorLine)
			{
				this.score = score;
				this.rotated = rotated;
				this.mirrorLine = mirrorLine;
			}

			@Override
			public String toString()
			{
				return "Symmetry{" + "score=" + score + ", rotated=" + rotated + ", mirrorLine=" + mirrorLine + '}';
			}
		}

		public void add(String row)
		{
			rows.add(row.toCharArray());
		}

		public Symmetry getSymmetry(boolean smudged)
		{
			System.out.println("============================");
			System.out.println("new puzzle");

			System.out.println(toString(rows));

			Symmetry symmetry = getSymmetry(rows);

			if (smudged)
			{
				System.out.println("orig score: " + symmetry);

				Symmetry smudgedSymmetry = getSmudgedSymmetry(rows, false, symmetry);

				if (smudgedSymmetry.score == 0)
				{
					System.out.println("Rotating");
					List<char[]> rotated = rotate(rows);

					smudgedSymmetry = getSmudgedSymmetry(rotated, true, symmetry);
					smudgedSymmetry.rotated = true;
				}
				else
				{
					smudgedSymmetry.score *= 100;
				}

				symmetry = smudgedSymmetry;

				if (symmetry.score == 0)
				{
					System.out.println("Bad");
					System.exit(-1);
				}
			}

			System.out.println("Score: " + symmetry);

			return symmetry;
		}

		private Symmetry getSmudgedSymmetry(List<char[]> input, boolean rotated, Symmetry originalSymmetry)
		{
			int rows = input.size();
			int cols = input.get(0).length;

			System.out.println("pattern is " + rows + ", " + cols);

			for (int row = 0; row < rows; row++)
			{
				for (int col = 0; col < cols; col++)
				{
					char currentChar = input.get(row)[col];

					System.out.println("smudging " + row + ", " + col);

					char nextChar = (currentChar == '.') ? '#' : '.';

					input.get(row)[col] = nextChar;

					Symmetry newSymmetry = getHorizontalSymmetry(input);

					input.get(row)[col] = currentChar; // restore char

					System.out.println("smudged score: " + newSymmetry);

					if (newSymmetry.score != 0 && !(rotated == originalSymmetry.rotated && newSymmetry.mirrorLine == originalSymmetry.mirrorLine))
					{
						System.out.println("Smudge at " + row + ", " + col);
						return newSymmetry;
					}
				}
			}

			return new Symmetry(0, false, 0);
		}

		private Symmetry getSymmetry(List<char[]> input)
		{
			Symmetry symmetry = getHorizontalSymmetry(input);

			if (symmetry.score == 0)
			{
				List<char[]> rotated = rotate(input);

				symmetry = getHorizontalSymmetry(rotated);
				symmetry.rotated = true;
			}
			else
			{
				symmetry.score *= 100;
			}

			return symmetry;
		}

		private Symmetry getHorizontalSymmetry(List<char[]> rows)
		{
			int score = 0;

			String last = null;

			int rowCount = rows.size();

			int mirrorLine = 0;

			for (int i = 0; i < rowCount; i++)
			{
				String row = new String(rows.get(i));

				if (last != null && last.equals(row))
				{
					mirrorLine = i;

					int above = i - 1;
					int below = i;

					boolean validMirror = true;

					while (above >= 0 && below < rowCount)
					{
						above--;
						below++;

						if (above >= 0 && below < rowCount)
						{
							String mirrorAbove = new String(rows.get(above));
							String mirrorBelow = new String(rows.get(below));

							if (!mirrorAbove.equals(mirrorBelow))
							{
								validMirror = false;
								break;
							}
						}
					}

					if (validMirror)
					{
						score = mirrorLine;
						break;
					}
				}

				last = row;
			}

			return new Symmetry(score, false, mirrorLine);
		}

		public String toString(List<char[]> input)
		{
			StringBuilder builder = new StringBuilder();

			for (char[] row : input)
			{
				builder.append(new String(row)).append("\n");
			}

			return builder.toString();
		}

		private List<char[]> rotate(List<char[]> input)
		{
			List<char[]> newRows = new ArrayList<>();

			int cols = input.get(0).length;

			for (int i = 0; i < cols; i++)
			{
				newRows.add(new char[input.size()]);
			}

			int newCol = 0;

			for (char[] row : input)
			{
				for (int i = 0; i < cols; i++)
				{
					char c = row[i];

					char[] newRow = newRows.get(i);

					newRow[newCol] = c;
				}

				newCol++;
			}

			return newRows;
		}
	}

	public Day13() throws Exception
	{
		// 23111 too low
		// 23035 too low

		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day13.txt"));

		List<Pattern> patterns = parse(lines);

		boolean partOne = false;

		if (partOne)
		{
			int sum1 = 0;
			int pos = 0;

			for (Pattern pattern : patterns)
			{
				System.out.println("puzzle: " + pos++);
				sum1 += pattern.getSymmetry(false).score;
			}

			System.out.println("Part 1 sum: " + sum1);
		}
		else
		{

			int sum2 = 0;
			int pos2 = 0;

			for (Pattern pattern : patterns)
			{
				System.out.println("puzzle: " + pos2++);
				sum2 += pattern.getSymmetry(true).score;
			}

			System.out.println("Part 2 sum: " + sum2);
		}
	}

	private List<Pattern> parse(List<String> lines)
	{
		List<Pattern> patterns = new ArrayList<>();

		Pattern pattern = new Pattern();
		patterns.add(pattern);

		for (String line : lines)
		{
			if (line.isEmpty())
			{
				pattern = new Pattern();
				patterns.add(pattern);
			}
			else
			{
				pattern.add(line);
			}
		}

		return patterns;
	}
}
