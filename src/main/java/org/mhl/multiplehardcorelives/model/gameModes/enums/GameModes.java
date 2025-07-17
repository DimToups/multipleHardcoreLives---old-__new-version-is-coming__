package org.mhl.multiplehardcorelives.model.gameModes.enums;

import org.mhl.multiplehardcorelives.controller.MhlController;
import org.mhl.multiplehardcorelives.model.gameModes.classic.Classic;
import org.mhl.multiplehardcorelives.model.gameModes.MhlGameMode;
import org.mhl.multiplehardcorelives.model.gameModes.impostor.Impostor;
import org.mhl.multiplehardcorelives.model.lifeToken.LifeTokens;

/**
 * An enum for every MhlGameModes and define them
 */
public enum GameModes {
    /**
     * The classic MhlGameMode
     */
    Classic("Classic", "Description", LifeTokens.NumericLifeToken),
    /**
     * The impostor MhlGameMode
     * A player is designated to kill someone during a session. If they fail, they will end the session on their last life
     */
    Impostor("Impostor", "Description", LifeTokens.NumericLifeToken),
    ;

    private final String name;
    private final String description;
    private final LifeTokens lifeTokens;

    GameModes(String name, String description, LifeTokens numericLifeToken){
        this.name = name;
        this.description = description;
        this.lifeTokens = numericLifeToken;
    }

    /**
     * Sends the name of the GameMode
     * @return The name of the GameMode
     */
    public String getName() {
        return name;
    }

    /**
     * Sends the description of the GameMode
     * @return The description of the GameMode
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sends a new MhlGameMode depending on the specified gameModes parameter
     * @param controller The plugin's controller
     * @param gameModes  The specified GameModes
     * @return           A new MhlGameMode instance
     */
    @org.jetbrains.annotations.Nullable
    public static MhlGameMode toMhlGameMode(MhlController controller, GameModes gameModes){
        return switch (gameModes){
            case Classic -> new Classic();
            case Impostor -> new Impostor(controller);
            default -> null;
        };
    }

    /**
     * Sends the associated LifeToken enum instance
     * @return The associated LifeToken enum instance
     */
    public LifeTokens getLifeCurrency() {
        return this.lifeTokens;
    }
}
