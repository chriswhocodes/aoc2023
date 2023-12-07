package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day7
{
	public static void main(String[] args) throws IOException
	{
		new Day7();
	}

	private enum Card
	{
		CARD_A(14),
		CARD_K(13),
		CARD_Q(12),
		CARD_J(11),
		CARD_T(10),
		CARD_9(9),
		CARD_8(8),
		CARD_7(7),
		CARD_6(6),
		CARD_5(5),
		CARD_4(4),
		CARD_3(3),
		CARD_2(2);

		private final int rank;

		Card(int rank)
		{
			this.rank = rank;
		}

		public int getRank(boolean jokersWild)
		{
			if (this == CARD_J)
			{
				return jokersWild ? 1 : rank;
			}
			else
			{
				return rank;
			}
		}

		static Card from(char c)
		{
			switch (c)
			{
			case 'A':
				return CARD_A;
			case 'K':
				return CARD_K;
			case 'Q':
				return CARD_Q;
			case 'J':
				return CARD_J;
			case 'T':
				return CARD_T;
			default:
				for (Card card : values())
				{
					if (card.rank == Integer.parseInt("" + c))
					{
						return card;
					}
				}
			}

			throw new RuntimeException("Could not parse card " + c);
		}

	}

	private enum HandRank
	{
		FIVE_KIND(7),
		FOUR_KIND(6),
		FULL_HOUSE(5),
		THREE_KIND(4),
		TWO_PAIR(3),
		ONE_PAIR(2),
		HIGH_CARD(1);

		private final int rank;

		HandRank(int rank)
		{
			this.rank = rank;
		}

		public int getRank()
		{
			return rank;
		}
	}

	private static class Hand implements Comparable<Hand>
	{
		private static final int CARDS_IN_HAND = 5;
		private HandRank handRank;

		private final List<Card> cards;

		private final int bid;

		private final boolean jokersWild;

		private int jokerCount = 0;

		public Hand(List<Card> cards, int bid, boolean jokersWild)
		{
			this.cards = cards;
			this.bid = bid;
			this.jokersWild = jokersWild;

			if (jokersWild)
			{
				for (Card card : cards)
				{
					if (card == Card.CARD_J)
					{
						jokerCount++;
					}
				}
			}
		}

		public HandRank getHandRank()
		{
			if (handRank == null)
			{
				Map<Card, Integer> counts = new EnumMap<>(Card.class);

				for (Card card : cards)
				{
					counts.put(card, counts.getOrDefault(card, 0) + 1);
				}

				List<Map.Entry<Card, Integer>> entries = new ArrayList<>(counts.entrySet());

				entries.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));

				handRank = HandRank.HIGH_CARD;

				outer:
				for (Map.Entry<Card, Integer> entry : entries)
				{
					switch (entry.getValue())
					{
					case 5:
						handRank = HandRank.FIVE_KIND;
						break outer;
					case 4:
						handRank = HandRank.FOUR_KIND;
						break outer;
					case 3:
						handRank = HandRank.THREE_KIND;
						break;
					case 2:
						if (handRank == HandRank.THREE_KIND)
						{
							handRank = HandRank.FULL_HOUSE;
							break outer;
						}
						else if (handRank == HandRank.ONE_PAIR)
						{
							handRank = HandRank.TWO_PAIR;
							break outer;
						}
						else
						{
							handRank = HandRank.ONE_PAIR;
						}
						break;
					case 1:
						break outer;
					}
				}

				if (jokerCount > 0)
				{
					upgradeHand();
				}
			}

			return handRank;
		}

		private void upgradeHand()
		{
			switch (jokerCount)
			{
			case 5: // JJJJJ
			case 4: // JJJJx
				handRank = HandRank.FIVE_KIND;
				break;

			case 3:
				if (handRank == HandRank.FULL_HOUSE) // JJJxx
				{
					handRank = HandRank.FIVE_KIND;
				}
				else // JJJxy
				{
					handRank = HandRank.FOUR_KIND;
				}
				break;

			case 2:
				if (handRank == HandRank.FULL_HOUSE) // JJxxx
				{
					handRank = HandRank.FIVE_KIND;
				}
				else if (handRank == HandRank.TWO_PAIR) // JJxxy
				{
					handRank = HandRank.FOUR_KIND;
				}
				else if (handRank == HandRank.ONE_PAIR) // JJxyz
				{
					handRank = HandRank.THREE_KIND;
				}
				break;

			case 1:
				if (handRank == HandRank.FOUR_KIND) // Jxxxx
				{
					handRank = HandRank.FIVE_KIND;
				}
				else if (handRank == HandRank.THREE_KIND) // Jxxxy
				{
					handRank = HandRank.FOUR_KIND;
				}
				else if (handRank == HandRank.TWO_PAIR) // Jxxyy
				{
					handRank = HandRank.FULL_HOUSE;
				}
				else if (handRank == HandRank.ONE_PAIR) // Jxxyz
				{
					handRank = HandRank.THREE_KIND;
				}
				else if (handRank == HandRank.HIGH_CARD) // Jwxyz
				{
					handRank = HandRank.ONE_PAIR;
				}
			}
		}

		@Override
		public int compareTo(Hand other)
		{
			if (getHandRank() == other.getHandRank())
			{
				for (int i = 0; i < CARDS_IN_HAND; i++)
				{
					Card ourCard = cards.get(i);
					Card theirCard = other.cards.get(i);

					if (ourCard.getRank(jokersWild) == theirCard.getRank(jokersWild))
					{
						continue;
					}
					else
					{
						return Integer.compare(theirCard.getRank(jokersWild), ourCard.getRank(jokersWild));
					}
				}
			}
			else
			{
				return Integer.compare(other.getHandRank().getRank(), handRank.getRank());
			}

			throw new RuntimeException("Equal hands are undefined");
		}

		@Override
		public String toString()
		{
			return "Hand{" + "handRank=" + getHandRank() + ", cards=" + cards + ", bid=" + bid + '}';
		}
	}

	public Day7() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day7.txt"));

		long sumPart1 = solve(lines, false);

		System.out.println("Part 1 sum: " + sumPart1);

		long sumPart2 = solve(lines, true);

		System.out.println("Part 2 sum: " + sumPart2);
	}

	private long solve(List<String> lines, boolean jokersWild)
	{
		List<Hand> hands = new ArrayList<>();

		for (String line : lines)
		{
			hands.add(parseHand(line, jokersWild));
		}

		Collections.sort(hands);

		int rank = hands.size();

		long sum = 0;

		for (Hand hand : hands)
		{
			sum += hand.bid * rank--;
		}

		return sum;
	}

	private Hand parseHand(String line, boolean jokersWild)
	{
		String[] parts = line.split("\\s+");

		String handCards = parts[0];

		int bid = Integer.parseInt(parts[1]);

		List<Card> cards = new ArrayList<>();

		for (int i = 0; i < handCards.length(); i++)
		{
			cards.add(Card.from(handCards.charAt(i)));
		}

		return new Hand(cards, bid, jokersWild);
	}
}