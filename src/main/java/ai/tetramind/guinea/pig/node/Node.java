package ai.tetramind.guinea.pig.node;

import java.io.Serializable;
import java.util.Objects;

public abstract class Node implements Serializable {
    public static final double DEFAULT_VALUE = 0.0;
    protected double value;

    public Node() {
        value = DEFAULT_VALUE;
    }

    public void reset() {
        value = DEFAULT_VALUE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Node node)) return false;

        return node.value == value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
