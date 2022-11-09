package ai.tetramind.guinea.pig;

import org.jetbrains.annotations.NotNull;

public record Struggle(double[] @NotNull [] inputs, double @NotNull [] expected) {
}
