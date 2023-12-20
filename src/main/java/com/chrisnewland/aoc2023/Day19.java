package com.chrisnewland.aoc2023;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        public long getPermutations(InputRange inputRange)
        {
            long perms = 0;

           // System.out.println("Entered workflow '" + name + "' input: " + inputRange);

            InputRange remainder = inputRange.copy();

            for (Stage stage : stages)
            {
                // System.out.println("Workflow: '" + name + "' stage: " + stage + " input: " + remainder);

                if (remainder == null)
                {
                    break;
                }

                InputRange taken = remainder.copy();

                switch (stage.comparison)
                {
                    case LT ->
                    {
                        switch (stage.parameter)
                        {
                            case 'x' ->
                            {
                                taken.x.max = Math.min(taken.x.max, stage.value - 1);
                                remainder.x.min = Math.max(remainder.x.min, stage.value);
                            }
                            case 'm' ->
                            {
                                taken.m.max = Math.min(taken.m.max, stage.value - 1);
                                remainder.m.min = Math.max(remainder.m.min, stage.value);
                            }
                            case 'a' ->
                            {
                                taken.a.max = Math.min(taken.a.max, stage.value - 1);
                                remainder.a.min = Math.max(remainder.a.min, stage.value);
                            }
                            case 's' ->
                            {
                                taken.s.max = Math.min(taken.s.max, stage.value - 1);
                                remainder.s.min = Math.max(remainder.s.min, stage.value);
                            }
                        }
                    }
                    case GT ->
                    {
                        switch (stage.parameter)
                        {
                            case 'x' ->
                            {
                                taken.x.min = Math.max(taken.x.min, stage.value + 1);
                                remainder.x.max = Math.min(remainder.x.max, stage.value);
                            }
                            case 'm' ->
                            {
                                taken.m.min = Math.max(taken.m.min, stage.value + 1);
                                remainder.m.max = Math.min(remainder.m.max, stage.value);
                            }
                            case 'a' ->
                            {
                                taken.a.min = Math.max(taken.a.min, stage.value + 1);
                                remainder.a.max = Math.min(remainder.a.max, stage.value);
                            }
                            case 's' ->
                            {
                                taken.s.min = Math.max(taken.s.min, stage.value + 1);
                                remainder.s.max = Math.min(remainder.s.max, stage.value);
                            }
                        }
                    }
                    case ANY ->
                    {
                        taken = remainder.copy();
                        remainder = null;
                    }
                }

                // System.out.println("taken    : " + taken);
                // System.out.println("not taken: " + remainder);

                String target = stage.target;

                if ("A".equals(target))
                {
                    long p = taken.getPermutations();

                    // System.out.println("Reached an end stage with " + taken + " perms " + p);

                    perms += p;
                }
                else if (target == null || "R".equals(target))
                {
                    perms += 0;
                }
                else
                {
                    perms += workFlowMap.get(target).getPermutations(taken);
                }
            }

            return perms;
        }

        public boolean accept(Input input)
        {
            boolean accepted = false;

            for (Stage stage : stages)
            {
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

    private static class Range
    {
        private long min;
        private long max;

        public Range(long min, long max)
        {
            this.min = min;
            this.max = max;
        }

        public Range copy()
        {
            return new Range(min, max);
        }

        public long size()
        {
            return max - min + 1;
        }

        @Override
        public String toString()
        {
            return "Range{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }

    private static class InputRange
    {
        private Range x;
        private Range m;
        private Range a;
        private Range s;

        public InputRange copy()
        {
            InputRange copy = new InputRange();

            copy.x = x.copy();
            copy.m = m.copy();
            copy.a = a.copy();
            copy.s = s.copy();

            return copy;
        }

        public long getPermutations()
        {
            return x.size() * m.size() * a.size() * s.size();
        }

        @Override
        public String toString()
        {
            return "InputRange{" +
                    "x=" + x +
                    ", m=" + m +
                    ", a=" + a +
                    ", s=" + s +
                    '}';
        }
    }

    public Day19() throws Exception
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day19.txt"));

        boolean partOne = false;

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
            parse(lines);

            WorkFlow first = workFlowMap.get("in");

            InputRange initial = new InputRange();

            initial.x = new Range(1, 4000);
            initial.m = new Range(1, 4000);
            initial.a = new Range(1, 4000);
            initial.s = new Range(1, 4000);

            long combinations = first.getPermutations(initial);

            System.out.println("Part 2 combinations: " + combinations);
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