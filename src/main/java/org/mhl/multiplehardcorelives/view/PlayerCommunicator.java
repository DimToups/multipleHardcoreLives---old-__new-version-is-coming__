package org.mhl.multiplehardcorelives.view;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A View made for communicate with players
 */
public class PlayerCommunicator {
    /**
     * Creates an instance of PlayerCommunicator
     */
    public PlayerCommunicator(){

    }

    /**
     * Tells to the specified player that they have definitively died
     * @param player The dead player
     */
    public void informPlayerDefinitiveDeath(Player player){
        player.sendTitle("You are out of lives !", "", 0, 70, 40);
    }

    /**
     * Tells to every player that the session has just started
     */
    public void tellSessionStart() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "The session has just started");
    }

    /**
     * Tells to every player that the session will end in 30 seconds
     */
    public void tellSessionNearlyEnded() {
        Bukkit.broadcastMessage(ChatColor.RED + "The session is ending in 30 seconds");
    }

    /**
     * Tells to every player that the session has ended
     */
    public void tellSessionEnd() {
        Bukkit.broadcastMessage(ChatColor.RED + "The session has now ended");
    }

    /**
     * Tells to every player the session's remaining time
     * @param seconds The remaining seconds
     */
    public void tellTimeLeft(int seconds) {
        Bukkit.broadcastMessage(ChatColor.RED + "The session is ending in " + seconds + " seconds");
    }

    /**
     * Tell to every player their role for the impostor GameMode
     * @param players  The list of online players
     * @param impostor The impostor
     */
    public void tellWhoIsImposter(List<org.mhl.multiplehardcorelives.model.gameLogic.Player> players, org.mhl.multiplehardcorelives.model.gameLogic.Player impostor) {
        Bukkit.getLogger().log(Level.INFO, "Telling players their role");

        Bukkit.broadcastMessage(ChatColor.RED + "The imposter is about to be chosen");
        try{
            TimeUnit.SECONDS.sleep(10);

            for(org.mhl.multiplehardcorelives.model.gameLogic.Player player : players)
                Bukkit.getPlayer(player.getUuid()).sendTitle("You are...", "", 20 / 2, 20 * 2, 20 /2);

            TimeUnit.SECONDS.sleep(5);

            for(org.mhl.multiplehardcorelives.model.gameLogic.Player player : players){
                if(player.getUuid() == impostor.getUuid())
                    Bukkit.getPlayer(player.getUuid()).sendTitle(ChatColor.RED + "Impostor", ChatColor.RED + "Kill at least one player once", 20, 20 * 3, 20 * 2);
                else
                    Bukkit.getPlayer(player.getUuid()).sendTitle("Not the impostor", "Play as usual...", 20, 20 * 3, 20 * 2);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not tell the players who is the impostor\n" + e);
        }
    }
}
