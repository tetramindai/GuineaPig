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
    private static final Random RANDOM = new SecureRandom();
    private static final int POPULATION_SIZE = 10000;
    private final AtomicBoolean status;
    private final Set<@NotNull Worker> workers;
    private final Map<@NotNull GuineaPig, Double> individuals;
    private final Map<double[][], double[]> dataSet;

    private final AtomicReference<GuineaPig> solution;

    private final GuineaPig origin;

    private final AtomicReference<Double> fitness;

    public Incubator(@NotNull Map<double[][], double[]> dataSet, @NotNull GuineaPig origin) {

        if (dataSet.isEmpty()) throw new IllegalStateException();

        this.origin = origin;
        this.dataSet = new HashMap<>(dataSet);
        this.workers = new HashSet<>();
        individuals = new HashMap<>();
        individuals.put(origin, null);
        status = new AtomicBoolean(true);
        solution = new AtomicReference<>(null);
        fitness = new AtomicReference<>(Double.MAX_VALUE);
    }

    public void createWorker() {

        var worker = new Worker(this);

        workers.add(worker);

        worker.start();
    }

    private static class Worker extends Thread {
        private final Incubator incubator;

        public Worker(@NotNull Incubator incubator) {
            this.incubator = incubator;
        }

        @Override
        public void run() {

            while (!isInterrupted() && incubator.status.get()) {

                var size = 0;

                synchronized (incubator.individuals) {
                    size = incubator.individuals.size();
                }

                if (size < 2) {
                    createIndividual();
                } else if (size > POPULATION_SIZE) {
                    killIndividual();
                } else {
                    if (RANDOM.nextDouble() < 0.3) {
                        mateIndividual();
                    } else {
                        mutateIndividual();
                    }
                }
                proceedIndividual();
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

                for (var input : incubator.dataSet.keySet()) {

                    var expected = incubator.dataSet.get(input);

                    var actual = guineaPig.evaluate(input);

                    for (var i = 0; i < expected.length; i++) {
                        score += Math.abs(actual[i] - expected[i]);
                    }
                }

                if (score < incubator.fitness.get()) {

                    incubator.fitness.set(score);

                    var timestamp = new Timestamp(System.currentTimeMillis());

                    System.out.println(timestamp + " fitness : " + score);
                }

                synchronized (incubator.individuals) {

                    incubator.individuals.replace(guineaPig, score);

                    if (score <= 0.0) {

                        incubator.solution.set(guineaPig);
                        incubator.status.set(false);

                        try (var outputStream = new ObjectOutputStream(new FileOutputStream("solution"))) {
                            outputStream.writeObject(guineaPig);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Done!");
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
