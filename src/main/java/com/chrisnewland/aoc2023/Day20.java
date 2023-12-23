package com.chrisnewland.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Day20
{
    public static void main(String[] args) throws IOException
    {
        new Day20();
    }

    private enum Pulse
    {
        HIGH, LOW;
    }

    private class Message
    {
        private Module source;
        private Module destination;
        private Pulse pulse;

        public Message(Module source, Module destination, Pulse pulse)
        {
            this.source = source;
            this.destination = destination;
            this.pulse = pulse;
        }

        @Override
        public String toString()
        {
            return source + " -" + pulse + " -> " + destination;
        }
    }

    private static class MessageBus
    {
        private static Queue<Message> queue = new LinkedBlockingQueue<>();

        public static void addMessage(Message message)
        {
            System.out.println("********* " + message);
            queue.add(message);
        }

        private static long sentHigh = 0;
        private static long sentLow = 0;

        public static void process()
        {
            while (!queue.isEmpty())
            {
                Message message = queue.remove();

                System.out.println("proc  " + message);

                message.destination.handle(message);

                if (message.pulse == Pulse.LOW)
                {
                    sentLow++;
                    System.out.println("sentLow:" + sentLow);
                }
                else
                {
                    sentHigh++;
                    System.out.println("sentHigh:" + sentHigh);
                }
            }

            System.out.println("done");
        }

        public static long getPulseProduct()
        {
            return sentHigh * sentLow;
        }
    }

    private abstract class Module
    {
        protected String name;

        public Module(String name)
        {
            this.name = name;
            moduleMap.put(name, this);

            System.out.println("Created '" + name + "'");
        }

        private List<Module> outputs = new ArrayList<>();

        public void attachOutput(Module module)
        {
            System.out.println("Attaching " + module + " to " + this);
            outputs.add(module);
            module.attachInput(this);
        }

        private List<Module> inputs = new ArrayList<>();

        public void attachInput(Module module)
        {
            inputs.add(module);
        }

        protected void emit(Pulse pulse)
        {
            for (Module dest : outputs)
            {
                MessageBus.addMessage(new Message(this, dest, pulse));
            }
        }

        public abstract void handle(Message message);

        @Override
        public String toString()
        {
            return name;
        }
    }

    private class FlipFlop extends Module
    {
        public FlipFlop(String name)
        {
            super(name);
        }

        boolean stateOn = false;

        @Override
        public void handle(Message message)
        {
            switch (message.pulse)
            {
                case LOW ->
                {
                    if (stateOn)
                    {
                        emit(Pulse.LOW);
                    }
                    else
                    {
                        emit(Pulse.HIGH);
                    }

                    stateOn = !stateOn;
                }
            }
        }
    }

    private class Conjunction extends Module
    {
        public Conjunction(String name)
        {
            super(name);
        }

        private Map<Module, Pulse> lastState = new HashMap<>();

        @Override
        public void attachInput(Module module)
        {
            System.out.println(name + " STATE LOW on input" + module);
            lastState.put(module, Pulse.LOW);
            super.attachInput(module);
        }

        @Override
        public void handle(Message message)
        {
            System.out.println(name + " input from " + message.source + " was " + message.pulse);
            lastState.put(message.source, message.pulse);

            int countLow = 0;
            int countHigh = 0;

            for (Pulse pulse : lastState.values())
            {
                System.out.println(name + " lastState: " + pulse);

                if (pulse == Pulse.LOW)
                {
                    countLow++;
                }
                else
                {
                    countHigh++;
                }
            }

            if (countHigh == lastState.size())
            {
                emit(Pulse.LOW);
            }
            else
            {
                emit(Pulse.HIGH);
            }
        }
    }

    private class Plain extends Module
    {
        public Plain(String name)
        {
            super(name);
        }

        @Override
        public void handle(Message message)
        {
            emit(message.pulse);
        }
    }

    private Map<String, Module> moduleMap = new HashMap<>();

    public Day20() throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/day20.txt"));

        parse(lines);

        Module broadcaster = moduleMap.get("broadcaster");

        Module button = new Plain("button");
        button.attachOutput(broadcaster);

        for (int i = 0; i < 1000; i++)
        {
            button.handle(new Message(button, broadcaster, Pulse.LOW));
            MessageBus.process();
        }
        
        System.out.println("Part 1 pulses: " + MessageBus.getPulseProduct());
    }

    private void parse(List<String> lines)
    {
        for (String line : lines)
        {
            String[] parts = line.split("->");

            String name = parts[0].trim();

            if (name.startsWith("%"))
            {
                new FlipFlop(name.substring(1));
            }
            else if (name.startsWith("&"))
            {
                new Conjunction(name.substring(1));
            }
            else
            {
                new Plain(name);
            }

            System.out.println(name);
        }

        for (String line : lines)
        {
            System.out.println("line: " + line);
            String[] parts = line.split("->");

            String name = parts[0].trim();

            if (name.startsWith("%"))
            {
                name = name.substring(1);
            }
            else if (name.startsWith("&"))
            {
                name = name.substring(1);
            }

            Module source = moduleMap.get(name);

            String outputs = parts[1];

            System.out.println("outputs: " + outputs);

            String[] modules = outputs.split(",");

            for (String output : modules)
            {
                System.out.println("parsed: " + output);
                Module dest = moduleMap.get(output.trim());

                if (dest == null)
                {
                    dest = new Plain(output.trim());
                }

                source.attachOutput(dest);
            }
        }
    }
}