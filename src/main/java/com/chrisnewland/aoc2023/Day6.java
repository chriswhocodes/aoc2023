package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day6
{
	public static void main(String[] args) throws IOException
	{
		new Day6();
	}

	private static class Race
	{
		private final long time;
		private final long distance;

		public Race(long time, long distance)
		{
			this.time = time;
			this.distance = distance;
		}

		private long getDistanceForPressTime(long pressed)
		{
			return (time - pressed) * pressed;
		}

		public int getWaysToWin()
		{
			int ways = 0;

			for (int i = 1; i < time; i++)
			{
				if (getDistanceForPressTime(i) > distance)
				{
					ways++;
				}
			}

			return ways;
		}
	}

	public Day6() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day6.txt"));

		System.out.println("Part 1: " + solve(lines, false));

		System.out.println("Part 2: " + solve(lines, true));
	}

	private int solve(List<String> lines, boolean stripSpaces)
	{
		List<Race> races = parse(lines, stripSpaces);

		int product = 1;

		for (Race race : races)
		{
			product *= race.getWaysToWin();
		}

		return product;
	}

	private List<Race> parse(List<String> lines, boolean stripSpaces)
	{
		List<Race> races = new ArrayList<>();

		String timeLine = lines.get(0);

		String distanceLine = lines.get(1);

		if (stripSpaces)
		{
			timeLine = timeLine.replaceAll("\\s+", "");
			distanceLine = distanceLine.replaceAll("\\s+", "");
		}

		List<Long> times = getNumbersFromLine(timeLine);

		List<Long> distances = getNumbersFromLine(distanceLine);

		int count = times.size();

		for (int i = 0; i < count; i++)
		{
			races.add(new Race(times.get(i), distances.get(i)));
		}

		return races;
	}

	private List<Long> getNumbersFromLine(String line)
	{
		int colonPos = line.indexOf(':');

		String[] parts = line.substring(colonPos + 1).trim().split("\\s+");

		List<Long> result = new ArrayList<>();

		for (String part : parts)
		{
			result.add(Long.parseLong(part));
		}

		return result;
	}
}