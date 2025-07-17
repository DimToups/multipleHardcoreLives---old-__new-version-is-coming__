package org.mhl.multiplehardcorelives.model.gameModes.impostor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.gameModes.MhlGameMode;
import org.mhl.multiplehardcorelives.model.gameModes.enums.GameModes;
import org.mhl.multiplehardcorelives.model.lifeToken.NumericLifeToken;
import org.mhl.multiplehardcorelives.model.session.enums.SessionEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * The Impostor MhlGameMode class
 * A player is designated to kill someone during a session. If they fail, they will end the session on their last life
 */
public class Impostor extends MhlGameMode {
    /**
     * The designated player for being the impostor
     */
    private Player impostor;

    /**
     * Makes an instance of the Impostor class
     * @param controller The plugin's controller
     */
    public Impostor(MhlController controller) {
        super(controller, new NumericLifeToken(), new NumericLifeToken(5), new NumericLifeToken(1), GameModes.Impostor);
    }

    /**
     * Designates an imposter for the session after 5 minutes
     */
    public void onSessionStart() {
        Thread t = new Thread(new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    TimeUnit.SECONDS.sleep(60 * 5);

                    // Designation of the imposter
                    List<Player> players = new ArrayList<>();
                    for(Player player : controller.getServer().getOnlinePlayers())
                        if(player.getLivesTokens().isSuperior(new NumericLifeToken(1)))
                            players.add(player);
                    if(players.isEmpty())
                        players = controller.getServer().getOnlinePlayers();
                    Random rand = new Random();
                    impostor = players.get(rand.nextInt(players.size()));
                    controller.tellWhoIsImposter(impostor);
                    Bukkit.getLogger().log(Level.INFO, "The impostor for the session is " + impostor.getName());
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Could not find any player. Aborting the session\n" + e);
                    controller.endSession();
                }
            }
        });
        t.start();
    }

    public void onPlayerDeath(PlayerDeathEvent pde) {
        if(impostor != null && Objects.requireNonNull(pde.getDeathMessage()).contains(impostor.getName()) && !pde.getEntity().getUniqueId().equals(impostor.getUuid())){
            controller.getCurrentSession().getEvents().getLast().setClaimer(controller.findPlayerSafelyByUUID(pde.getEntity().getUniqueId()));
            Bukkit.getLogger().log(Level.INFO, "The imposter has killed someone");
        }
    }

    public void onSessionEnd() {
        if (hasTheImposterKilled()) {
            controller.setNbLivesOfPlayer(impostor, new NumericLifeToken(1));
            Bukkit.getLogger().log(Level.INFO, "The impostor (" + impostor.getName() + ") has killed nobody during the session");

            Bukkit.getPlayer(impostor.getUuid()).sendMessage(ChatColor.RED + "You have killed nobody during the session");
            Bukkit.getPlayer(impostor.getUuid()).sendMessage(ChatColor.RED + "You now have one remaining life");
        }
    }

    private boolean hasTheImposterKilled() {
        return controller.getCurrentSession().getEventsOfType(SessionEvents.Player_death).stream().noneMatch(e -> e.getClaimer() == impostor);
    }

    /**
     * Recommends options to the player when typing the command mhlGameMode to configure it
     *
     * @param commandSender The command sender
     * @param command       The command
     * @param s             The full sent command
     * @param strings       The parameters sent from the command
     * @return The list of possible options at a precise index
     */
    @Override
    public List<String> getCommandTabCompleter(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> args = new ArrayList<>();
        if(strings.length == 2)
            args.add("showImpostor");
        if(strings.length ==3)
            if (strings[2].equals("showImpostor"))
                args.add(impostor.getName());
        return args;
    }

    /**
     * Sends the Player instance of the imposter
     * @return The Player instance of the imposter
     */
    public Player getImpostor() {
        return impostor;
    }
}
