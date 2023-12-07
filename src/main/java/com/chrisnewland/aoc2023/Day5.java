package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day5
{
	public static void main(String[] args) throws IOException
	{
		new Day5();
	}

	private static class Range
	{
		private long destStart;
		private long sourceStart;
		private long sourceEnd;

		public Range(long destStart, long sourceStart, long length)
		{
			this.destStart = destStart;
			this.sourceStart = sourceStart;
			this.sourceEnd = sourceStart + length;
		}

		public boolean inRange(long source)
		{
			return source >= sourceStart && source < sourceEnd;
		}

		public long getDest(long source)
		{
			return source + (destStart - sourceStart);
		}
	}

	private static class RangeMap
	{
		private List<Range> ranges = new ArrayList<>();

		public void addRange(long destStart, long sourceStart, long length)
		{
			ranges.add(new Range(destStart, sourceStart, length));
		}

		public Long getDestination(Long source)
		{
			for (Range range : ranges)
			{
				if (range.inRange(source))
				{
					return range.getDest(source);
				}
			}

			return source;
		}
	}

	private List<Long> seeds;
	private RangeMap seedToSoil;
	private RangeMap soilToFertilizer;
	private RangeMap fertilizerToWater;
	private RangeMap waterToLight;
	private RangeMap lightToTemperature;
	private RangeMap temperatureToHumidity;
	private RangeMap humidityToLocation;

	public Day5() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day5.txt"));

		parse(lines);

		System.out.println("Part 1, lowest location: " + getLowestLocationSingleSeeds());

		parse(lines);

		System.out.println("Part 2, lowest location: " + getLowestLocationSeedRanges());
	}

	private long getLowestLocationSingleSeeds()
	{
		long lowestLocation = Long.MAX_VALUE;

		for (long seed : seeds)
		{
			long location = humidityToLocation.getDestination(temperatureToHumidity.getDestination(
					lightToTemperature.getDestination(waterToLight.getDestination(
							fertilizerToWater.getDestination(soilToFertilizer.getDestination(seedToSoil.getDestination(seed)))))));

			lowestLocation = Math.min(lowestLocation, location);
		}

		return lowestLocation;
	}

	private long getLowestLocationSeedRanges()
	{
		long lowestLocation = Long.MAX_VALUE;

		int length = seeds.size();

		for (int i = 0; i < length; i += 2)
		{
			long start = seeds.get(i);
			long end = start + seeds.get(i + 1);

			for (long seed = start; seed < end; seed++)
			{
				long location = humidityToLocation.getDestination(temperatureToHumidity.getDestination(
						lightToTemperature.getDestination(waterToLight.getDestination(fertilizerToWater.getDestination(
								soilToFertilizer.getDestination(seedToSoil.getDestination(seed)))))));

				lowestLocation = Math.min(lowestLocation, location);
			}
		}

		return lowestLocation;
	}

	private void parse(List<String> lines)
	{
		seedToSoil = new RangeMap();
		soilToFertilizer = new RangeMap();
		fertilizerToWater = new RangeMap();
		waterToLight = new RangeMap();
		lightToTemperature = new RangeMap();
		temperatureToHumidity = new RangeMap();
		humidityToLocation = new RangeMap();

		RangeMap currentMap = null;

		for (String line : lines)
		{
			if (line.trim().isEmpty())
			{
				continue;
			}
			else if (line.contains(":"))
			{
				if (line.startsWith("seeds"))
				{
					parseSeeds(line);
				}
				else
				{
					currentMap = switch (line)
					{
						case "seed-to-soil map:" -> seedToSoil;
						case "soil-to-fertilizer map:" -> soilToFertilizer;
						case "fertilizer-to-water map:" -> fertilizerToWater;
						case "water-to-light map:" -> waterToLight;
						case "light-to-temperature map:" -> lightToTemperature;
						case "temperature-to-humidity map:" -> temperatureToHumidity;
						case "humidity-to-location map:" -> humidityToLocation;
						default -> throw new RuntimeException("Unexpected label");
					};
				}
			}
			else
			{
				String[] parts = line.trim().split("\\s+");

				currentMap.addRange(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
			}
		}
	}

	private void parseSeeds(String line)
	{
		int colonIndex = line.indexOf(':');

		String[] parts = line.substring(colonIndex + 1).trim().split("\\s+");

		seeds = new ArrayList<>();

		for (String part : parts)
		{
			seeds.add(Long.parseLong(part));
		}
	}
}