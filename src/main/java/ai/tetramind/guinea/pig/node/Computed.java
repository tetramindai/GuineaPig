package ai.tetramind.guinea.pig.node;

import org.jetbrains.annotations.NotNull;

public interface Computed {
    void compute(double @NotNull [] inputs);
}
