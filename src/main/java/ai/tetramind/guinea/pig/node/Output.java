package ai.tetramind.guinea.pig.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public final class Output extends Node implements Computed {
    private final double[] weights;

    public Output(int dimension) {

        if (dimension <= 0) throw new IllegalStateException();

        weights = new double[dimension];
        Arrays.fill(weights, DEFAULT_VALUE);
    }

    public double read() {
        return value;
    }

    @Override
    public void compute(double @NotNull [] results) {

        if (weights.length != results.length) throw new IllegalStateException();

        var sum = 0.0;
        for (var i = 0; i < results.length; i++) {
            sum += results[i] * weights[i];
        }

        value = (2.0 / (1.0 + Math.pow(Math.E, -2.0 * sum)) - 1.0);
    }

    public void changeWeight(int index, double value) {

        if (index < 0 || index >= weights.length) throw new IllegalStateException();

        weights[index] = value;
    }

    public double[] getWeights() {
        return weights.clone();
    }

    public int getWeightsLength() {
        return weights.length;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Output output)) return false;

        return Arrays.equals(weights, output.weights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(weights));
    }
}
