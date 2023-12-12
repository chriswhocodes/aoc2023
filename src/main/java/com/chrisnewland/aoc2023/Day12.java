package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day12
{
	public static void main(String[] args) throws IOException
	{
		new Day12();
	}

	private class Puzzle
	{
		private String row;
		private List<Integer> groups;

		public Puzzle(String row, List<Integer> groups)
		{
			this.row = row;
			this.groups = groups;

			simplify();
		}

		private void simplify()
		{
			boolean changed;

			do
			{
				System.out.println("Simplifying " + this);

				changed = false;

				if (row.startsWith("."))
				{
					row = row.substring(1);
					changed = true;
				}

				if (row.endsWith("."))
				{
					row = row.substring(0, row.length() - 1);
					changed = true;
				}

				int length = row.length();

				row = row.replace("..", ".");

				if (row.length() != length)
				{
					changed = true;
				}

				String[] parts = row.split("\\.");

				int partCount = parts.length;

				if (partCount > 0)
				{
					String lastPart = parts[partCount - 1];

					int lastGroupIndex = groups.size() - 1;

					int lastGroupLength = groups.get(lastGroupIndex);

					if (lastPart.indexOf('?') == -1 && lastPart.length() == lastGroupLength)
					{
						row = row.substring(0, row.length() - lastPart.length() - 1);
						groups.remove(lastGroupIndex);
						changed = true;
					}
					else if (lastPart.length() < lastGroupLength)
					{
						row = row.substring(0, row.length() - lastPart.length() - 1);
						changed = true;
					}

					if (!changed)
					{
						String firstPart = parts[0];

						int firstGroupIndex = 0;

						int firstGroupLength = groups.get(firstGroupIndex);

						if (firstPart.indexOf('?') == -1 && firstPart.length() == firstGroupLength)
						{
							row = row.substring(firstPart.length());
							groups.remove(firstGroupIndex);
							changed = true;
						}
						else if (firstPart.length() < firstGroupLength)
						{
							row = row.substring(firstPart.length());
							changed = true;
						}
					}
				}

			} while (changed);
		}

		public int solve()
		{
			int arrangements = 0;

			int questionMarks = 0;

			char[] rowchars = row.toCharArray();

			for (char c : rowchars)
			{
				if (c == '?')
				{
					questionMarks++;
				}
			}

			int permutations = 1 << questionMarks;

			System.out.println("Permutations: " + permutations);

			char[] possible = new char[rowchars.length];

			for (int p = 0; p < permutations; p++)
			{
				int qPos = 0;

				for (int i = 0; i < rowchars.length; i++)
				{
					char c = rowchars[i];

					if (c == '?')
					{
						int pow = 1 << qPos;

						if ((p & pow) == pow)
						{
							c = '#';
						}
						else
						{
							c = '.';
						}

						qPos++;
					}

					possible[i] = c;
				}

				if (valid(possible))
				{
					arrangements++;
				}
			}

			return arrangements;
		}

		private boolean valid(char[] row)
		{
			int groupPos = 0;
			int groupLen = groups.get(groupPos);

			boolean inGroup = false;
			int currentGroupLen = 0;

			for (char c : row)
			{
				if (c == '#')
				{
					inGroup = true;
					currentGroupLen++;

					if (currentGroupLen > groupLen)
					{
						return false;
					}
				}
				else
				{
					if (inGroup)
					{
						inGroup = false;

						if (currentGroupLen != groupLen)
						{
							return false;
						}

						if (groupPos < groups.size() - 1)
						{
							groupPos++;
							groupLen = groups.get(groupPos);
							currentGroupLen = 0;
						}
					}
				}
			}

			return groupPos == groups.size() - 1 && currentGroupLen == groupLen;
		}

		@Override
		public String toString()
		{
			return "Puzzle{" + new String(row) + ", groups=" + groups + '}';
		}
	}

	public Day12() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day12.txt"));

		long sum1 = solvePuzzles(lines, false);

		System.out.println("Part 1 sum: " + sum1);

		long sum2 = solvePuzzles(lines, true);

		System.out.println("Part 2 sum: " + sum2);
	}

	private long solvePuzzles(List<String> lines, boolean expand)
	{
		long sum = 0;

		int pos = 0;

		for (String line : lines)
		{
			System.out.println("LINE " + pos++);
			Puzzle puzzle = parse(line, expand);

			System.out.println("Parsed: " + puzzle);

			long start = System.currentTimeMillis();
			int arrangements = puzzle.solve();
			long stop = System.currentTimeMillis();

			System.out.println("Puzzle has " + arrangements + " in " + (stop - start) + "ms");

			sum += arrangements;
		}

		return sum;
	}

	private Puzzle parse(String line, boolean expand)
	{
		String[] parts = line.trim().split(" ");

		return new Puzzle(parseRow(parts[0], expand), parseGroups(parts[1], expand));
	}

	private String parseRow(String row, boolean expand)
	{
		if (expand)
		{
			row = row + "?" + row + "?" + row + "?" + row + "?" + row;
		}

		return row;
	}

	private List<Integer> parseGroups(String groups, boolean expand)
	{
		if (expand)
		{
			groups = groups + "," + groups + "," + groups + "," + groups + "," + groups;
		}

		List<Integer> result = new ArrayList<>();

		String[] parts = groups.split(",");

		for (String part : parts)
		{
			result.add(Integer.parseInt(part));
		}

		return result;
	}
}