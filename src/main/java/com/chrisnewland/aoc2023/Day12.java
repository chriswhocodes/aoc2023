package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day12
{
	public static void main(String[] args) throws IOException
	{
		new Day12();
	}

	private static Map<String, Long> memo = new HashMap<>();

	private String getKey(String segment, List<Integer> groups)
	{
		return segment + "/" + groups.toString();
	}

	private class Puzzle
	{

		public Puzzle()
		{
		}

		//============================
		// Brute force solution
		//============================

		public long solveBruteForce(String row, List<Integer> groups)
		{
			int arrangements = 0;

			long permutations = 1L << countOccurrences(row, '?');

			char[] rowchars = row.toCharArray();

			char[] possible = new char[rowchars.length];

			for (long p = 0; p < permutations; p++)
			{
				int qPos = 0;

				for (int i = 0; i < rowchars.length; i++)
				{
					char c = rowchars[i];

					if (c == '?')
					{
						long pow = 1L << qPos;

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

				if (isValidArrangement(possible, groups))
				{
					arrangements++;
				}
			}

			return arrangements;
		}

		private int countOccurrences(String segment, char c)
		{
			int count = 0;

			for (int i = 0; i < segment.length(); i++)
			{
				if (segment.charAt(i) == c)
				{
					count++;
				}
			}

			return count;
		}

		private boolean isValidArrangement(char[] row, List<Integer> groups)
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

		//============================
		// Recursive solution
		//============================

		public long solveRecursive(String row, List<Integer> groups)
		{
			return countArrangements(row, groups);
		}

		// I was nearly there but the excellent commenting I found here: https://github.com/ash42/adventofcode/blob/main/adventofcode2023/src/nl/michielgraat/adventofcode2023/day12/Day12.java
		// helped me over the line :)

		private long countArrangements(String row, List<Integer> groups)
		{
			String mapKey = getKey(row, groups);

			if (memo.containsKey(mapKey)) // have we solved this (row, groups) arrangement previously?
			{
				return memo.get(mapKey);
			}

			if (row.isEmpty()) // end of row, if groups is empty this is a valid arrangement
			{
				return groups.isEmpty() ? 1 : 0;
			}

			char firstChar = row.charAt(0);

			String remainder = row.substring(1);

			long arrangements = 0;

			if (firstChar == '.') // skip over '.'
			{
				arrangements = countArrangements(remainder, groups);
			}
			else if (firstChar == '?') // count arrangements for both '.' and '#' ('.' would be skipped so no need to prefix)
			{
				arrangements = countArrangements(remainder, groups) + countArrangements("#" + remainder, groups);
			}
			else
			{
				if (groups.isEmpty()) // firstChar is # but no more groups so invalid arrangement
				{
					arrangements = 0;
				}
				else // check if this group of # fits
				{
					int groupLength = groups.get(0);

					if (groupLength > row.length()) // not enough chars until end of string
					{
						arrangements = 0;
					}
					else if (row.substring(0, groupLength).contains(".")) // not enough chars until next '.'
					{
						arrangements = 0;
					}
					else
					{
						List<Integer> nextGroup = groups.subList(1, groups.size());

						if (groupLength == row.length()) // finished this group and no more groups so this is a valid arrangement
						{
							arrangements = nextGroup.isEmpty() ? 1 : 0;
						}
						else
						{
							char nextChar = row.charAt(groupLength);

							if (nextChar == '.') // finished this group but not finished whole string
							{
								arrangements = countArrangements(row.substring(groupLength + 1), nextGroup);
							}
							else if (nextChar == '?') // finished this group so next char must be '.' so continue
							{
								arrangements = countArrangements(row.substring(groupLength + 1), nextGroup);
							}
							else // next char is # but we just finished a group so not valid
							{
								arrangements = 0;
							}
						}
					}
				}
			}

			memo.put(mapKey, arrangements);

			return arrangements;
		}
	}

	public Day12() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day12.txt"));

		boolean bruteForce = false;

		long sum1 = solvePuzzle(lines, false, bruteForce);

		System.out.println("Part 1 sum: " + sum1);

		long sum2 = solvePuzzle(lines, true, bruteForce);

		System.out.println("Part 2 sum: " + sum2);
	}

	private long solvePuzzle(List<String> lines, boolean expand, boolean bruteForce)
	{
		long sum = 0;

		long start = System.currentTimeMillis();

		for (String line : lines)
		{
			Puzzle puzzle = new Puzzle();

			String[] parts = line.trim().split(" ");

			String row = parseRow(parts[0], expand);

			List<Integer> groups = parseGroups(parts[1], expand);

			long arrangements = bruteForce ? puzzle.solveBruteForce(row, groups) : puzzle.solveRecursive(row, groups);

			sum += arrangements;
		}

		long stop = System.currentTimeMillis();

		System.out.println("Solved in " + (stop - start) + "ms");

		return sum;
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