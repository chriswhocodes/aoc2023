package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day4
{
	public static void main(String[] args) throws IOException
	{
		new Day4();
	}

	private static class Card
	{
		private Set<Integer> winningNumbers = new HashSet<>();

		private Set<Integer> playingNumbers = new HashSet<>();

		public Card(String line)
		{
			int colonIndex = line.indexOf(':');

			String[] sides = line.substring(colonIndex + 1).trim().split("\\|");

			String winningSide = sides[0].trim();
			String playingSide = sides[1].trim();

			String[] winning = winningSide.split("\\s+");
			String[] playing = playingSide.split("\\s+");

			for (String winner : winning)
			{
				winningNumbers.add(Integer.parseInt(winner.trim()));
			}

			for (String player : playing)
			{
				playingNumbers.add(Integer.parseInt(player.trim()));
			}
		}

		public int getWinningMatchCount()
		{
			int matches = 0;

			for (int number : playingNumbers)
			{
				if (winningNumbers.contains(number))
				{
					matches++;
				}
			}

			return matches;
		}

		public int getPoints()
		{
			int matches = getWinningMatchCount();

			return matches == 0 ? 0 : (int) Math.pow(2, matches - 1);
		}
	}

	private Map<Integer, Integer> cardCountMap = new LinkedHashMap<>();

	private void count(int cardNumber)
	{
		Integer existingCount = getCount(cardNumber);

		existingCount++;

		cardCountMap.put(cardNumber, existingCount);
	}

	private int getCount(int cardNumber)
	{
		Integer existingCount = cardCountMap.get(cardNumber);

		if (existingCount == null)
		{
			existingCount = 0;
		}

		return existingCount;
	}

	public Day4() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day4.txt"));

		int cardSum = 0;

		for (String line : lines)
		{
			Card card = new Card(line);

			cardSum += card.getPoints();
		}

		System.out.println("Part 1 points: " + cardSum);

		int cardNumber = 1;

		for (String line : lines)
		{
			Card card = new Card(line);

			count(cardNumber);

			int countForCard = getCount(cardNumber);

			for (int i = 0; i < countForCard; i++)
			{
				int matches = card.getWinningMatchCount();

				for (int c = cardNumber + 1; c < cardNumber + matches + 1; c++)
				{
					count(c);
				}
			}

			cardNumber++;

		}

		int scratchCardCount = 0;

		for (Integer count : cardCountMap.values())
		{
			scratchCardCount += count;
		}

		System.out.println("Part 2 scratchcards: " + scratchCardCount);
	}
}
