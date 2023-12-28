package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day22
{
	public static void main(String[] args) throws IOException
	{
		new Day22();
	}

	private static class Cube
	{
		private final int x;
		private final int y;
		private int z;

		public Cube(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Cube copy()
		{
			return new Cube(x, y, z);
		}

		@Override
		public String toString()
		{
			return "Cube{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Cube cube = (Cube) o;
			return x == cube.x && y == cube.y && z == cube.z;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(x, y, z);
		}
	}

	private static class Brick
	{
		private List<Cube> cubes = new ArrayList<>();

		private boolean isVertical = false;

		private static int ID = 0;

		private int brickId = ID++;

		private Brick()
		{
		}

		public Brick(int x1, int y1, int z1, int x2, int y2, int z2)
		{
			if (x1 != x2)
			{
				for (int x = x1; x <= x2; x++)
				{
					cubes.add(new Cube(x, y1, z1));
				}
			}
			else if (y1 != y2)
			{
				for (int y = y1; y <= y2; y++)
				{
					cubes.add(new Cube(x1, y, z1));
				}
			}
			else if (z1 != z2)
			{
				for (int z = z1; z <= z2; z++)
				{
					cubes.add(new Cube(x1, y1, z));
				}

				isVertical = true;
			}
			else
			{
				cubes.add(new Cube(x1, y1, z1));

				isVertical = true;
			}

			if (isVertical)
			{
				cubes.sort(new Comparator<Cube>()
				{
					@Override
					public int compare(Cube o1, Cube o2)
					{
						return Integer.compare(o1.z, o2.z);
					}
				});
			}
		}

		public Brick copy()
		{
			Brick copy = new Brick();

			copy.brickId = this.brickId;
			copy.isVertical = this.isVertical;

			for (Cube cube : cubes)
			{
				copy.cubes.add(cube.copy());
			}

			return copy;
		}

		public void lower()
		{
			for (Cube cube : cubes)
			{
				cube.z--;
			}

			//System.out.println("lowered " + this);
		}

		@Override
		public String toString()
		{
			return "Brick{" + "cubes=" + cubes + ", isVertical=" + isVertical + ", brickId=" + brickId + '}';
		}
	}

	private List<Brick> getBricksCopy(List<Brick> bricks)
	{
		List<Brick> copy = new ArrayList<>(bricks.size());

		for (Brick brick : bricks)
		{
			copy.add(brick.copy());
		}

		return copy;
	}

	public Day22() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day22.txt"));

		boolean partOne = false;

		if (partOne)
		{
			List<Brick> bricks = parse(lines);

			fall(bricks);

			int canDisintegrate = safeToDisintegrate(bricks);

			System.out.println("Day 22 Part 1 safe to disintegrate: " + canDisintegrate);
		}
		else
		{
			List<Brick> bricks = parse(lines);

			fall(bricks);

			int sumOfFallingBricks = sumOfDisintegrations(bricks);

			System.out.println("Day 22 Part 2 sum of fallers: " + sumOfFallingBricks);
		}
	}

	private boolean spaceOccupied(List<Brick> bricks, int x, int y, int z)
	{
		Cube cube = new Cube(x, y, z);

		for (Brick brick : bricks)
		{
			if (brick.cubes.contains(cube))
			{
				return true;
			}
		}

		return false;
	}

	private int safeToDisintegrate(List<Brick> bricks)
	{
		int count = 0;

		int brickCount = bricks.size();

		for (int i = 0; i < brickCount; i++)
		{
			List<Brick> copy = getBricksCopy(bricks);

			copy.remove(i);

			if (fall(copy) == 0)
			{
				count++;
			}
		}

		return count;
	}

	private int sumOfDisintegrations(List<Brick> bricks)
	{
		int count = 0;

		int brickCount = bricks.size();

		for (int i = 0; i < brickCount; i++)
		{
			List<Brick> copy = getBricksCopy(bricks);

			copy.remove(i);

			count += fall(copy);
		}

		return count;
	}

	private int fall(List<Brick> bricks)
	{
		boolean changed;

		Set<Integer> fallenIds = new HashSet<>();

		do
		{
			changed = false;

			for (Brick brick : bricks)
			{
				if (brick.isVertical)
				{
					Cube bottomCube = brick.cubes.get(0);

					int lowest = bottomCube.z;

					if (lowest > 1)
					{
						if (!spaceOccupied(bricks, bottomCube.x, bottomCube.y, lowest - 1))
						{
							brick.lower();
							changed = true;
							fallenIds.add(brick.brickId);
						}
					}
				}
				else
				{
					int lowest = brick.cubes.get(0).z;

					if (lowest > 1)
					{
						boolean canFall = true;

						for (Cube cube : brick.cubes)
						{
							if (spaceOccupied(bricks, cube.x, cube.y, lowest - 1))
							{
								canFall = false;
								break;
							}
						}

						if (canFall)
						{
							brick.lower();
							changed = true;

							fallenIds.add(brick.brickId);
						}
					}
				}
			}

		} while (changed);

		return fallenIds.size();
	}

	private List<Brick> parse(List<String> lines)
	{
		List<Brick> result = new ArrayList<>();

		for (String line : lines)
		{
			String[] parts = line.replace("~", ",").split(",");

			result.add(new Brick(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
					Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));

		}

		return result;
	}
}