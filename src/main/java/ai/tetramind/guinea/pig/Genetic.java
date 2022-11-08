package ai.tetramind.guinea.pig;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface Genetic extends Serializable {

    void mutate();

    @NotNull GuineaPig mate(@NotNull GuineaPig guineaPig);
}
