package ai.tetramind.guinea.pig.node;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Output extends Node {

    private final double[] weights;

    public Output(int dimension) {

        if (dimension <= 0) throw new IllegalStateException();

        weights = new double[dimension];
        Arrays.fill(weights, DEFAULT_VALUE);
    }

    public double read() {
        return value;
    }

    protected void write(double @NotNull [] results) {

        if (weights.length != results.length) throw new IllegalStateException();

        var sum = 0.0;
        for (var i = 0; i < results.length; i++) {
            sum += results[i] * weights[i];
        }

        value = sum;
    }
}
