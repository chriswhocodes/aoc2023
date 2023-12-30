package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day24
{
	public static void main(String[] args) throws IOException
	{
		new Day24();
	}

	private static class Hailstone
	{
		private long x;
		private long y;
		private long z;

		private long vx;
		private long vy;
		private long vz;

		public Hailstone(long x, long y, long z, long vx, long vy, long vz)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.vx = vx;
			this.vy = vy;
			this.vz = vz;
		}

		@Override
		public String toString()
		{
			return "Hailstone{" + "x=" + x + ", y=" + y + ", z=" + z + ", vx=" + vx + ", vy=" + vy + ", vz=" + vz + '}';
		}
	}

	public Day24() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day24.txt"));
		//List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day24.txt.test"));

		boolean partOne = true;

		if (partOne)
		{
			List<Hailstone> hailstones = parse(lines);

			long collisions = findCollisions(hailstones, 200000000000000L, 400000000000000L);
			//long collisions = findCollisions(hailstones, 7, 27);

			System.out.println("Day 24 Part 1 collisions: " + collisions);
		}
		else
		{

		}
	}

	private long findCollisions(List<Hailstone> hailstones, long rangeMin, long rangeMax)
	{
		long collisions = 0;

		long count = hailstones.size();

		for (int i = 0; i < count - 1; i++)
		{
			for (int j = i + 1; j < count; j++)
			{
				Hailstone a = hailstones.get(i);
				Hailstone b = hailstones.get(j);

				if (collides(a, b, rangeMin, rangeMax))
				{
					collisions++;
				}
			}
		}

		return collisions;
	}

	private boolean collides(Hailstone a, Hailstone b, long rangeMin, long rangeMax)
	{
		double a1 = a.vy;
		double b1 = -a.vx;
		double c1 = a1 * a.x + b1 * a.y;

		double a2 = b.vy;
		double b2 = -b.vx;
		double c2 = a2 * b.x + b2 * b.y;

		double delta = a1 * b2 - a2 * b1;

		if (delta != 0)
		{

			double ix = (b2 * c1 - b1 * c2) / delta;
			double iy = (a1 * c2 - a2 * c1) / delta;

			boolean inside = (ix >= rangeMin && ix <= rangeMax && iy >= rangeMin && iy <= rangeMax);

			if (inside)
			{
				boolean futureAX = Math.signum(ix - a.x) == Math.signum(a.vx);
				boolean futureAY = Math.signum(iy - a.y) == Math.signum(a.vy);
				boolean futureBX = Math.signum(ix - b.x) == Math.signum(b.vx);
				boolean futureBY = Math.signum(iy - b.y) == Math.signum(b.vy);

				boolean future = futureAX && futureAY && futureBX && futureBY;

				return future;
			}
		}

		return false;
	}

	private List<Hailstone> parse(List<String> lines)
	{
		List<Hailstone> result = new ArrayList<>();

		for (String line : lines)
		{
			String[] parts = line.replace("@", ",").replace(" ", "").split(",");

			result.add(new Hailstone(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]),
					Long.parseLong(parts[3]), Long.parseLong(parts[4]), Long.parseLong(parts[5])));

		}

		return result;
	}
}