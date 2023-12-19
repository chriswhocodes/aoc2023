package com.chrisnewland.aoc2023;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Day19
{
	public static void main(String[] args) throws Exception
	{
		new Day19();
	}

	private enum Comparison
	{
		LT,
		GT,
		ANY;
	}

	private static class Stage
	{
		private Comparison comparison;

		private char parameter;
		private int value;
		private String target;

		public Stage(Comparison comparison, char parameter, int value, String target)
		{
			this.comparison = comparison;
			this.parameter = parameter;
			this.value = value;
			this.target = target;
		}

		@Override
		public String toString()
		{
			return "Stage{" + "comparison=" + comparison + ", parameter=" + parameter + ", value=" + value + ", target='" + target
					+ '\'' + '}';
		}

		public String evaluate(Input input)
		{
			boolean accept;

			if (comparison == Comparison.ANY)
			{
				accept = true;
			}
			else
			{
				int inputValue = switch (parameter)
				{
					case 'x' -> input.x;
					case 'm' -> input.m;
					case 'a' -> input.a;
					case 's' -> input.s;
					default -> throw new RuntimeException("unknown input parameter " + parameter);
				};

				accept = switch (comparison)
				{
					case LT -> inputValue < value;
					case GT -> inputValue > value;
					case ANY -> true;
				};
			}

			return accept ? target : null;
		}
	}

	private static class Input
	{
		private int x;
		private int m;
		private int a;
		private int s;

		public Input(int x, int m, int a, int s)
		{
			this.x = x;
			this.m = m;
			this.a = a;
			this.s = s;
		}

		@Override
		public String toString()
		{
			return "Input{" + "x=" + x + ", m=" + m + ", a=" + a + ", s=" + s + '}';
		}

		public int getPartSum()
		{
			return x + m + a + s;
		}
	}

	private static Map<String, WorkFlow> workFlowMap = new HashMap<>();

	private static class WorkFlow
	{
		String name;
		private List<Stage> stages = new ArrayList<>();

		public WorkFlow(String name)
		{
			this.name = name;
			workFlowMap.put(name, this);
		}

		public boolean accept(Input input)
		{
			boolean accepted = false;

			for (Stage stage : stages)
			{
				System.out.printf("%s evaluating %s on %s\n", name, stage, input);

				String next = stage.evaluate(input);

				if (next == null)
				{
					continue;
				}
				else if ("A".equals(next))
				{
					return true;
				}
				else if ("R".equals(next))
				{
					return false;
				}
				else
				{
					WorkFlow nextWorkFlow = workFlowMap.get(next);

					return nextWorkFlow.accept(input);
				}
			}

			return accepted;
		}

		public void addStage(Stage stage)
		{
			stages.add(stage);
		}
	}

	public Day19() throws Exception
	{
		List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day19.txt"));

		boolean partOne = true;

		if (partOne)
		{
			int sum = 0;

			List<Input> inputs = parse(lines);

			WorkFlow first = workFlowMap.get("in");

			for (Input input : inputs)
			{
				if (first.accept(input))
				{
					sum += input.getPartSum();
				}
			}

			System.out.println("Part one sum: " + sum);

		}
		else
		{

		}
	}

	private List<Input> parse(List<String> lines)
	{
		List<Input> inputs = new ArrayList<>();

		boolean inWorkflows = true;

		for (String line : lines)
		{
			if (line.trim().isEmpty())
			{
				inWorkflows = false;
				continue;
			}

			if (inWorkflows)
			{
				parseWorkFlow(line);
			}
			else
			{
				inputs.add(parseInput(line));
			}
		}

		return inputs;
	}

	private void parseWorkFlow(String line)
	{
		int openBrace = line.indexOf('{');

		String name = line.substring(0, openBrace);

		String rest = line.substring(openBrace + 1, line.length() - 1);

		String[] parts = rest.split(",");

		WorkFlow workFlow = new WorkFlow(name);

		for (String part : parts)
		{
			Comparison comparison = Comparison.ANY;
			char parameter = 0;
			int value = 0;
			String target;

			int colonIndex = part.indexOf(':');

			if (colonIndex != -1)
			{
				parameter = part.charAt(0);

				comparison = switch (part.charAt(1))
				{
					case '<' -> Comparison.LT;
					case '>' -> Comparison.GT;
					default -> throw new RuntimeException("Unknown comparison");
				};

				value = Integer.parseInt(part.substring(2, colonIndex));

				target = part.substring(colonIndex + 1);
			}
			else
			{
				target = part;
			}

			workFlow.addStage(new Stage(comparison, parameter, value, target));
		}
	}

	private Input parseInput(String line)
	{
		String[] parts = line.substring(1, line.length() - 1).split(",");

		int x = 0;
		int m = 0;
		int a = 0;
		int s = 0;

		for (String part : parts)
		{
			String[] valueParts = part.split("=");

			switch (valueParts[0])
			{
			case "x":
				x = Integer.parseInt(valueParts[1]);
				break;
			case "m":
				m = Integer.parseInt(valueParts[1]);
				break;
			case "a":
				a = Integer.parseInt(valueParts[1]);
				break;
			case "s":
				s = Integer.parseInt(valueParts[1]);
				break;
			}
		}

		return new Input(x, m, a, s);
	}
}