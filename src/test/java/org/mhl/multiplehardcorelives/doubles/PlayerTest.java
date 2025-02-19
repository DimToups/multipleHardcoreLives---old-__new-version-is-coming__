package org.mhl.multiplehardcorelives.doubles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.lifeToken.NumericLifeToken;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private static Player player;

    @Nested
    class ConstructorTest{
        @ParameterizedTest
        @ValueSource(longs = {0, 1 , -1})
        public void doesPlayerHaveTheCorrectUUID (long uuid) {
            player = new Player(new UUID(uuid, uuid), "test", new NumericLifeToken());
            assertEquals(new UUID(uuid, uuid), player.getUuid());
        }

        @Test
        public void canPlayerHaveNullUUID () {
            try {
                player = new Player(null, "test", new NumericLifeToken());
            } catch (Exception e) {
                assertTrue(true);
                return;
            }
            fail("The player's UUID should not be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "test"})
        public void doesPlayerHaveTheCorrectName (String name) {
            player = new Player(new UUID(0,0), name, new NumericLifeToken());
            assertEquals(name, player.getName());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, -1})
        public void doesPlayerHaveTheCorrectAmountOfNumericLifeToken (int amount) {
            player = new Player(new UUID(0,0), "test", new NumericLifeToken(amount));
            assertInstanceOf(NumericLifeToken.class, player.getLivesTokens());
            assertEquals(amount, ((NumericLifeToken)player.getLivesTokens()).getRemainingLives());
        }
    }

    @Nested
    class SetterTests {
        @BeforeEach
        public void setup () {
            player = new Player(new UUID(0,0), "test", new NumericLifeToken());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, -1})
        public void doesChangingAmountOfNumericLifeTokenWorks (int amount) {
            player.setLivesTokens(new NumericLifeToken(amount));
        }

        @Test
        public void doSetToOfflineWorks () {
            player.setToOffline();
            assertFalse(player.isOnline());
        }

        @Test
        public void doSetToOnlineWorks () {
            player.setToOnline();
            assertTrue(player.isOnline());
        }

        @Test
        public void isOfflineDefaultValue () {
            assertFalse(player.isOnline());
        }
    }

    @Test
    public void isOnlineStringCorrect () {
        player = new Player(new UUID(0,0), "test", new NumericLifeToken());
        player.setToOnline();
        assertEquals("Online", player.isOnlineToString());
    }

    @Test
    public void isOfflineStringCorrect () {
        player = new Player(new UUID(0,0), "test", new NumericLifeToken());
        player.setToOffline();
        assertEquals("Offline", player.isOnlineToString());
    }
}
