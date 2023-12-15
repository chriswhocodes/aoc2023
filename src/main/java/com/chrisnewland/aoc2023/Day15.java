package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Day15
{
	public static void main(String[] args) throws Exception
	{
		new Day15();
	}

	private int hash(String input)
	{
		int result = 0;

		int len = input.length();

		for (int i = 0; i < len; i++)
		{
			char c = input.charAt(i);

			result += (int) c;
			result *= 17;
			result = result % 256;
		}

		return result;
	}

	private static class Lens
	{
		private String label;
		private int focalLength;

		public Lens(String label, int focalLength)
		{
			this.label = label;
			this.focalLength = focalLength;
		}

		@Override
		public String toString()
		{
			return "Lens{" + "label='" + label + '\'' + ", focalLength=" + focalLength + '}';
		}
	}

	private static class Box
	{
		private int number;

		private List<Lens> lenses = new LinkedList<>();

		public Box(int number)
		{
			this.number = number;
		}

		public void addLens(Lens lens)
		{
			Iterator<Lens> iter = lenses.iterator();

			boolean replaced = false;

			while (iter.hasNext())
			{
				Lens existing = iter.next();

				if (existing.label.equals(lens.label))
				{
					existing.focalLength = lens.focalLength;
					replaced = true;
				}
			}

			if (!replaced)
			{
				lenses.add(lens);
			}
		}

		public void removeLens(String label)
		{
			Iterator<Lens> iter = lenses.iterator();

			while (iter.hasNext())
			{
				Lens existing = iter.next();

				if (existing.label.equals(label))
				{
					iter.remove();
				}
			}
		}

		public int getPower()
		{
			int power = 0;

			for (int i = 0; i < lenses.size(); i++)
			{
				power += (1 + number) * (i + 1) * lenses.get(i).focalLength;
			}

			return power;
		}

		@Override
		public String toString()
		{
			return "Box{" + "number=" + number + ", lenses=" + lenses + '}';
		}
	}

	public Day15() throws Exception
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day15.txt"));

		String line = lines.get(0);

		String[] parts = line.split(",");

		System.out.println("Part 1 hash: " + doPart1(parts));

		System.out.println("Part 2 power: " + doPart2(parts));
	}

	private long doPart1(String[] parts)
	{
		long sum = 0;

		for (String part : parts)
		{
			sum += hash(part);
		}

		return sum;
	}

	private long doPart2(String[] parts)
	{
		Box[] boxes = new Box[256];

		for (int i = 0; i < boxes.length; i++)
		{
			boxes[i] = new Box(i);
		}

		for (String part : parts)
		{
			if (part.contains("-"))
			{
				String[] boxParts = part.split("-");
				String label = boxParts[0];
				int boxNumber = hash(label);
				Box box = boxes[boxNumber];
				box.removeLens(label);
			}
			else if (part.contains("="))
			{
				String[] boxParts = part.split("=");
				String label = boxParts[0];
				int focalLength = Integer.parseInt(boxParts[1]);
				int boxNumber = hash(label);
				Box box = boxes[boxNumber];
				box.addLens(new Lens(label, focalLength));
			}
		}

		long power = 0;

		for (int i = 0; i < boxes.length; i++)
		{
			power += boxes[i].getPower();
		}

		return power;
	}
}