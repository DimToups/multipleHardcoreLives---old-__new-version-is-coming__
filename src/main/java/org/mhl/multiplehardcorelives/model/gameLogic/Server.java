package org.mhl.multiplehardcorelives.model.gameLogic;

import org.bukkit.Bukkit;
import org.mhl.multiplehardcorelives.model.lifeToken.LifeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A model that stores data about the server and its players.
 */
public class Server {
    /**
     * The server's address.
     */
    private final String address;
    /**
     * The default number of lives.
     */
    private LifeToken defaultNbLivesTokens;
    /**
     * The server's list of loaded players.
     */
    private final List<Player> players = new ArrayList<>();

    /**
     * The server's world border length
     */
    private int worldBorderLength = 500;

    /**
     * Initialises a new Server by using its address.
     * @param address The server's address.
     */
    public Server(String address){
        this.address = address;
    }

    /**
     * Initialises a new Server by using its address and the wanted default number of lives.
     * @param address The server's address.
     * @param livesTokens The wanted default number of lives.
     */
    public Server(String address, LifeToken livesTokens){
        this.address = address;
        this.defaultNbLivesTokens = livesTokens;
    }

    /**
     * Initialises a new Server by using its address, the wanted default number of lives, and its world border length
     * @param address The server's address
     * @param livesTokens The wanted default number of lives
     * @param worldBorderLength The wanted world border length
     */
    public Server(String address, LifeToken livesTokens, int worldBorderLength){
        this.address = address;
        this.defaultNbLivesTokens = livesTokens;
        this.worldBorderLength = worldBorderLength;
    }

    /**
     * Sends the server's address.
     * @return The server's address.
     */
    public String getAddress(){
        return this.address;
    }

    /**
     * Sends the server's loaded players.
     * @return The loaded players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sends the default number of lives.
     * @return The default number of lives.
     */
    public LifeToken getDefaultNbLivesTokens() {
        return defaultNbLivesTokens;
    }

    /**
     * Sets the default number of lives.
     * @param defaultNbLivesTokens The default number of lives.
     */
    public void setDefaultNbLivesTokens(LifeToken defaultNbLivesTokens) {
        if(defaultNbLivesTokens.isNull()){
            Bukkit.getLogger().log(Level.WARNING, "The server cannot have a zero or negative default number of lives like " + defaultNbLivesTokens);
            return;
        }
        Bukkit.getLogger().log(Level.INFO, "Setting the default number of lives to " + defaultNbLivesTokens);
        this.defaultNbLivesTokens = defaultNbLivesTokens;
    }

    /**
     * Adds a new player to the list of loaded players.
     * @param newPlayer The new loaded player.
     */
    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
        Bukkit.getLogger().log(Level.INFO, "Added the player with the UUID " + newPlayer.getUuid() + " to the controller's server instance");
    }

    /**
     * Tells if the server has a player with the specified UUID
     * @param uuid The wanted player UUID
     * @return True if the server has this player, false otherwise
     */
    public boolean hasPlayerWithUUID(UUID uuid) {
        for(Player player : players)
            if (player.getUuid() == uuid)
                return true;
        Bukkit.getLogger().log(Level.WARNING, "No player has the UUID " + uuid + " in the server.");
        return false;
    }

    /**
     * Sets the value of the worldBorderLength field
     * @param length The wanted length
     */
    public void setWorldBorderLength(int length) {
        this.worldBorderLength = length;
    }

    /**
     * Sends the server's world border length
     * @return The server's world border length
     */
    public int getWorldBorderLength() {
        return worldBorderLength;
    }

    /**
     * Sends the list of onlinePlayers
     * @return The list of onlinePlayers
     */
    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(this.players.stream().filter(Player::isOnline).toList());
    }
}
