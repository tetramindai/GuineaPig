package ai.tetramind.guinea.pig;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class Incubator {
    private final static int DEFAULT_MAX_CYCLE = Integer.MAX_VALUE;
    private final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final Random RANDOM = new SecureRandom();
    private static final int POPULATION_SIZE = 100000;

    private static final int STRUGGLE_DURATION = 100;

    private final AtomicBoolean status;
    private final Set<@NotNull Worker> workers;
    private final Map<@NotNull GuineaPig, Double> individuals;
    private final AtomicReference<GuineaPig> solution;
    private final GuineaPig origin;
    private final AtomicReference<Double> fitness;
    private final Predator predator;

    public Incubator(@NotNull Predator predator, @NotNull GuineaPig origin) {

        this.predator = predator;
        this.origin = origin;
        this.workers = new HashSet<>();
        individuals = new HashMap<>();
        individuals.put(origin, null);
        status = new AtomicBoolean(true);
        solution = new AtomicReference<>(null);
        fitness = new AtomicReference<>(Double.MAX_VALUE);

        for (var w = 0; w < AVAILABLE_PROCESSORS; w++) {
            createWorker();
        }
    }

    private void createWorker() {

        var worker = new Worker(this, DEFAULT_MAX_CYCLE);

        workers.add(worker);
    }

    public void start() {

        synchronized (workers) {
            for (var worker : workers) {
                worker.start();
            }
        }
    }

    public GuineaPig waitSolution() {

        try {
            Worker worker = null;

            do {
                if (worker != null) {
                    worker.join();
                }

                synchronized (workers) {
                    if (!workers.isEmpty()) {
                        worker = workers.stream().findFirst().get();
                    }
                }

            } while (worker != null);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return solution.get();
    }

    public void stop() {
        status.set(false);
    }

    private static class Worker extends Thread {
        private final int maxCycle;
        private final int cycle;

        private final Incubator incubator;

        public Worker(@NotNull Incubator incubator, int maxCycle) {
            this.incubator = incubator;
            this.maxCycle = maxCycle;
            cycle = 0;
        }

        @Override
        public void run() {

            while (!isInterrupted() && incubator.status.get() && cycle <= maxCycle) {

                var size = 0;

                synchronized (incubator.individuals) {
                    size = incubator.individuals.size();
                }

                if (size < 2) {
                    createIndividual();
                } else if (size > POPULATION_SIZE) {
                    killIndividual();
                } else {
                    mateIndividual();
                }

                proceedIndividual();

                if (RANDOM.nextBoolean()) {
                    mutateIndividual();
                }
            }

            incubator.workers.remove(this);
        }

        private void mateIndividual() {

            var father = randomIndividual();
            var mother = randomIndividual();

            var child = father.mate(mother);

            synchronized (incubator.individuals) {
                incubator.individuals.put(child, null);
            }
        }

        private void mutateIndividual() {

            var guineaPig = randomIndividual();

            synchronized (guineaPig) {
                guineaPig.mutate();
            }
        }

        private GuineaPig randomIndividual() {

            synchronized (incubator.individuals) {

                var guineaPigs = incubator.individuals.keySet().stream().toList();

                return guineaPigs.get(RANDOM.nextInt(guineaPigs.size()));
            }
        }

        private void proceedIndividual() throws RuntimeException {

            var guineaPig = randomIndividual();

            synchronized (guineaPig) {

                var score = 0.0;

                for (var s = 0; s < STRUGGLE_DURATION; s++) {

                    var struggle = incubator.predator.generate();

                    if (struggle != null) {

                        var inputs = struggle.inputs();
                        var expected = struggle.expected();

                        var actual = guineaPig.evaluate(inputs);

                        for (var i = 0; i < expected.length; i++) {
                            score += Math.abs(actual[i] - expected[i]);
                        }

                    } else {
                        s--;
                    }
                }

                synchronized (incubator.individuals) {

                    if (score < incubator.fitness.get()) {

                        incubator.fitness.set(score);
                        incubator.solution.set(guineaPig);

                        var timestamp = new Timestamp(System.currentTimeMillis());

                        System.out.println(timestamp + " fitness : " + score);
                    }

                    incubator.individuals.replace(guineaPig, score);

                    if (score <= 0.0) {

                        incubator.status.set(false);

                        try (var outputStream = new ObjectOutputStream(new FileOutputStream("solution"))) {
                            outputStream.writeObject(guineaPig);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Done !");
                    }
                }
            }
        }

        private void killIndividual() {

            var first = randomIndividual();
            var second = randomIndividual();

            if (first != second) {

                synchronized (incubator.individuals) {

                    var firstScore = incubator.individuals.get(first);
                    var secondScore = incubator.individuals.get(second);

                    if (firstScore != null && secondScore != null) {
                        if (firstScore < secondScore) {
                            incubator.individuals.remove(second);
                        } else if (secondScore < firstScore) {
                            incubator.individuals.remove(first);
                        }
                    }
                }
            }
        }

        private void createIndividual() {

            var clone = incubator.origin.clone();

            synchronized (incubator.individuals) {
                incubator.individuals.put(clone, null);
            }
        }
    }
}
