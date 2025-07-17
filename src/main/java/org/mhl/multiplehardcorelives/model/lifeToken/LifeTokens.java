package org.mhl.multiplehardcorelives.model.lifeToken;

/**
 * An enum for every possible LifeTokens
 */
public enum LifeTokens {
    /**
     * The kind of LifeTokens defined by numbers
     */
    NumericLifeToken("Numeric"),
    ;
    /**
     * The clean name of the LifeToken
     */
    private final String name;

    /**
     * Defines a new LifeTokens enum instance
     * @param name The LifeTokens clean name
     */
    LifeTokens(String name){
        this.name = name;
    }

    /**
     * Sends the LifeTokens clean name
     * @return The LifeTokens clean name
     */
    public String getCleanName() {
        return name;
    }
}
