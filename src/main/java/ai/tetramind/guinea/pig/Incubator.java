package ai.tetramind.guinea.pig;

import ai.tetramind.GuineaPigApp;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
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

    private AtomicReference<GuineaPig> solution;

    public Incubator(@NotNull Map<double[][], double[]> dataSet) {
        this.workers = Collections.synchronizedSet(new HashSet<>());
        individuals = Collections.synchronizedMap(new HashMap<>());
        status = new AtomicBoolean(true);
        this.dataSet = Collections.synchronizedMap(new HashMap<>(dataSet));
        solution = new AtomicReference<>(null);
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

            while (!isInterrupted() && !incubator.status.get()) {

                var size = incubator.individuals.size();

                if (size < POPULATION_SIZE) {

                    incubator.individuals.put(new GuineaPig(1, 1), null);

                } else if (size > POPULATION_SIZE) {

                    /*
                    var values = incubator.individuals.values();

                    var min = Collections.max(values);
                    */

                }

                var guineaPigs = incubator.individuals.keySet().stream().toList();

                var guineaPig = guineaPigs.get(RANDOM.nextInt(guineaPigs.size()));

                var score = 0.0;

                for (var input : incubator.dataSet.keySet()) {

                    var expected = incubator.dataSet.get(input);

                    var actual = guineaPig.evaluate(input);

                    for (var i = 0; i < expected.length; i++) {
                        score += Math.abs(actual[i] - expected[i]);
                    }
                }

                incubator.individuals.replace(guineaPig, score);

                if(score <= 0.0) {
                    incubator.solution.set(guineaPig);
                    incubator.status.set(false);
                }
            }

            incubator.workers.remove(this);
        }
    }
}
