package org.mhl.multiplehardcorelives.model.lifeToken;

/**
 * An abstract class for making custom life currencies
 */
public abstract class LifeToken {
    /**
     * Creates an instance of LifeToken
     */
    public LifeToken(){

    }

    /**
     * Compares the LifeToken to the one in parameter to know which one is superior
     * @param tokens The comparison
     * @return true if the parameter is inferior, false otherwise
     */
    public abstract boolean isSuperior(LifeToken tokens);

    /**
     * Compares the LifeToken to the one in parameter to know which one is superior
     * @param tokens The comparison
     * @return true if the parameter is inferior or equal, false otherwise
     */
    public abstract boolean isSuperiorOrEqual(LifeToken tokens);

    /**
     * Compares the LifeToken to the one in parameter to know which one is inferior
     * @param tokens The comparison
     * @return true if the parameter is superior, false otherwise
     */
    public abstract boolean isInferiorOrEqual(LifeToken tokens);

    /**
     * Verify if the LifeToken is null or not
     * @return true of null, false otherwise
     */
    public abstract boolean isNull();

    /**
     * Subtract the parameter LifeTokens to the caller
     * @param lifeToken The amount of LifeTokens to subtract
     * @return The new amount of LifeTokens
     */
    public abstract LifeToken minus(LifeToken lifeToken);

    /**
     * Transforms the LifeToken into a human-readable String
     * @return The stringified LifeTokens
     */
    @Override
    public abstract String toString();
}
