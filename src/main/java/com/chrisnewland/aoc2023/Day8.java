package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day8
{
	public static void main(String[] args) throws IOException
	{
		new Day8();
	}

	private static class Node
	{
		private final String name;
		private final String leftName;
		private final String rightName;

		private Node left;
		private Node right;

		private final boolean endState;

		public Node(String name, String leftName, String rightName, boolean endState)
		{
			this.name = name;
			this.leftName = leftName;
			this.rightName = rightName;
			this.endState = endState;
		}

		public void resolve(Node left, Node right)
		{
			this.left = left;
			this.right = right;

			if (left == null || right == null)
			{
				throw new RuntimeException("Did not resolve for " + name);
			}
		}

		public Node getNext(char c)
		{
			return switch (c)
			{
				case 'L' -> left;
				case 'R' -> right;
				default -> throw new RuntimeException("unknown direction");
			};
		}
	}

	private final Map<String, Node> nodeMap = new HashMap<>();

	private final String directions;

	public Day8() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day8.txt"));

		directions = lines.remove(0).trim();

		buildNodeMap(lines, nodeName -> "ZZZ".equals(nodeName));
		long steps1 = getStepsPart1("AAA");
		System.out.println("Part 1 steps: " + steps1);

		buildNodeMap(lines, nodeName -> nodeName.endsWith("Z"));
		long steps2 = getStepsPart2("A");
		System.out.println("Part 2 steps: " + steps2);

	}

	private long getStepsPart1(String startNodeName)
	{
		int steps = 0;

		Node node = nodeMap.get(startNodeName);

		int directionLength = directions.length();
		int directionIndex = 0;

		while (!node.endState)
		{
			if (directionIndex == directionLength)
			{
				directionIndex = 0;
			}

			char dir = directions.charAt(directionIndex++);

			node = node.getNext(dir);

			steps++;
		}

		return steps;
	}

	private long getStepsPart2(String startNodeSuffix)
	{
		Set<Node> startingNodes = findNodesEndingWith(startNodeSuffix);

		List<Long> stepList = new ArrayList<>();

		for (Node node : startingNodes)
		{
			long nodeSteps = getStepsPart1(node.name);

			stepList.add(nodeSteps);
		}

		return findLeastCommonMultiple(stepList);
	}

	private long findLeastCommonMultiple(List<Long> steps)
	{
		long lowest = steps.remove(0);

		for (long stepCount : steps)
		{
			lowest = lcm(lowest, stepCount);
		}

		return lowest;
	}

	private long lcm(long first, long second)
	{
		return first * (second / greatestCommonDivisor(first, second));
	}

	private long greatestCommonDivisor(long first, long second)
	{
		while (second > 0)
		{
			long temp = second;

			second = first % second;

			first = temp;
		}

		return first;
	}

	private Set<Node> findNodesEndingWith(String ending)
	{
		Set<Node> result = new HashSet<>();

		for (Map.Entry<String, Node> entry : nodeMap.entrySet())
		{
			if (entry.getKey().endsWith(ending))
			{
				result.add(entry.getValue());
			}
		}

		return result;
	}

	private void buildNodeMap(List<String> lines, Predicate<String> endNodePredicate)
	{
		nodeMap.clear();

		Pattern pattern = Pattern.compile("([A-Z0-9]+) = \\(([A-Z0-9]+), ([A-Z0-9]+)");

		for (String line : lines)
		{
			Matcher matcher = pattern.matcher(line);

			if (matcher.find())
			{
				String name = matcher.group(1);
				String left = matcher.group(2);
				String right = matcher.group(3);

				Node node = new Node(name, left, right, endNodePredicate.test(name));
				nodeMap.put(name, node);
			}
		}

		for (Node node : nodeMap.values())
		{
			Node left = nodeMap.get(node.leftName);
			Node right = nodeMap.get(node.rightName);
			node.resolve(left, right);
		}
	}
}