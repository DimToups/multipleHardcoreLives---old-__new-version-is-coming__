package org.mhl.multiplehardcorelives.model.gameLogic;

import org.jetbrains.annotations.NotNull;
import org.mhl.multiplehardcorelives.model.lifeToken.LifeToken;

import java.util.UUID;

/**
 * A model storing the data of a player for the server it's currently in.
 */
public class Player {
    /**
     * The player's UUID from its minecraft account.
     */
    private final UUID uuid;
    /**
     * The player's name.
     */
    private final String name;
    /**
     * The player's number of lives
     */
    private LifeToken livesTokens;
    /**
     * Indicates the state of the player on the server
     */
    private boolean isOnline = false;

    /**
     * Initialises a new Player by defining its UUID, name and its number of lives.
     * @param uuid    The player's UUID from its minecraft account.
     * @param name    The player's name.
     * @param livesTokens The player's number of lives
     */
    public Player(@NotNull UUID uuid, String name, LifeToken livesTokens){
        this.uuid = uuid;
        this.name = name;
        this.livesTokens = livesTokens;
    }

    /**
     * Sends the player's UUID.
     * @return The player's UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sends the player's name.
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sends the player's number of lives.
     * @return The player's number of lives.
     */
    public LifeToken getLivesTokens() {
        return livesTokens;
    }

    /**
     * Tells by a boolean if the player is connected or not.
     * @return The state of the player on the server.
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Tells if the player is connected or not by returning a string.
     * @return "Online" if the player is online, "Offline" otherwise.
     */
    public String isOnlineToString(){
        return isOnline() ? "Online" : "Offline";
    }

    /**
     * Sets the number of lives of the player.
     * @param lives The number of lives of the player.
     */
    public void setLivesTokens(LifeToken lives) {
        this.livesTokens = lives;
    }

    /**
     * Sets the player to online.
     */
    public void setToOnline() {
        this.isOnline = true;
    }

    /**
     * Sets the player to offline.
     */
    public void setToOffline() {
        this.isOnline = false;
    }
}
