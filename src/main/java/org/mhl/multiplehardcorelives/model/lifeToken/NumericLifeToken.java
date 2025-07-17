package org.mhl.multiplehardcorelives.model.lifeToken;

/**
 * A subclass of LifeToken made to represent a numeric number of lives
 */
public class NumericLifeToken extends LifeToken {
    /**
     * The remaining lives of the instance
     */
    private int remainingLives;

    /**
     * Creates an instance of NumericLifeToken with 5 lives
     */
    public NumericLifeToken(){
         remainingLives = 5;
    }

    /**
     * Creates an instance of NumericLifeToken with a specified number of lives
     * @param i The wanted number of lives
     */
    public NumericLifeToken(int i) {
        remainingLives = i;
    }

    /**
     * Sends the remaining lives of the instance
     * @return The remaining lives of the instance
     */
    public int getRemainingLives() {
        return remainingLives;
    }

    /**
     * Sets the remaining lives to the specified integer
     * @param remainingLives The wanted remaining lives
     */
    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    @Override
    public boolean isSuperior(LifeToken tokens) {
        if(tokens.getClass() == NumericLifeToken.class)
            return remainingLives == ((NumericLifeToken) tokens).getRemainingLives();
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isSuperiorOrEqual(LifeToken tokens) {
        if(tokens.getClass() == NumericLifeToken.class)
            return remainingLives >= ((NumericLifeToken) tokens).getRemainingLives();
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isInferiorOrEqual(LifeToken tokens) {
        if(tokens.getClass() == NumericLifeToken.class)
            return remainingLives <= ((NumericLifeToken) tokens).getRemainingLives();
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isNull() {
        return this.remainingLives <= 0;
    }

    @Override
    public LifeToken minus(LifeToken tokens) {
        if(tokens.getClass() == NumericLifeToken.class)
            return new NumericLifeToken(this.remainingLives - ((NumericLifeToken) tokens).remainingLives);
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return String.valueOf(this.remainingLives);
    }
}
