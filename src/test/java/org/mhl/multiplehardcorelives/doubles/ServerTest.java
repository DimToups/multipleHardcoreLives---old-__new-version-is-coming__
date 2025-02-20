package org.mhl.multiplehardcorelives.doubles;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mhl.multiplehardcorelives.model.gameLogic.Player;
import org.mhl.multiplehardcorelives.model.gameLogic.Server;
import org.mhl.multiplehardcorelives.model.lifeToken.NumericLifeToken;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    private Server server;

    @Nested
    class ConstructorTests {
        @Nested
        class AddressConstructor {
            @ParameterizedTest
            @ValueSource(strings = {"", " ", "test"})
            public void isTheAddressInitialised (String name) {
                server = new Server(name);
                assertEquals(name, server.getAddress());
            }

            @Test
            public void isThePlayerListInitialised () {
                server = new Server("test");
                assertNotNull(server.getPlayers());
            }

            @Test
            public void isThereNoPlayers () {
                server = new Server("test");
                assertEquals(0, server.getPlayers().size());
            }

            @Test
            public void isTheDefaultNbLivesTokensNull () {
                server = new Server("test");
                assertNull(server.getDefaultNbLivesTokens());
            }
        }

        @Nested
        class AddressAndLifeTokensConstructor {
            @ParameterizedTest
            @ValueSource(strings = {"", " ", "test"})
            public void isTheAddressInitialised (String name) {
                server = new Server(name, new NumericLifeToken());
                assertEquals(name, server.getAddress());
            }

            @Test
            public void isThePlayerListInitialised () {
                server = new Server("test", new NumericLifeToken());
                assertNotNull(server.getPlayers());
            }

            @Test
            public void isThereNoPlayers () {
                server = new Server("test", new NumericLifeToken());
                assertEquals(0, server.getPlayers().size());
            }

            @ParameterizedTest
            @ValueSource(ints = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
            public void isTheDefaultNbLivesTokensInitialised (int amount) {
                server = new Server("test", new NumericLifeToken(amount));
                assertEquals(amount, ((NumericLifeToken)server.getDefaultNbLivesTokens()).getRemainingLives());
            }
        }

        @Nested
        class AddressAndXConstructor {
            @ParameterizedTest
            @ValueSource(strings = {"", " ", "test"})
            public void isTheAddressInitialised (String name) {
                server = new Server(name, new NumericLifeToken(), 0);
                assertEquals(name, server.getAddress());
            }

            @Test
            public void isThePlayerListInitialised () {
                server = new Server("test", new NumericLifeToken(), 0);
                assertNotNull(server.getPlayers());
            }

            @Test
            public void isThereNoPlayers () {
                server = new Server("test", new NumericLifeToken(), 0);
                assertEquals(0, server.getPlayers().size());
            }

            @ParameterizedTest
            @ValueSource(ints = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
            public void isTheDefaultNbLivesTokensInitialised (int amount) {
                server = new Server("test", new NumericLifeToken(amount), 0);
                assertEquals(amount, ((NumericLifeToken)server.getDefaultNbLivesTokens()).getRemainingLives());
            }

            @ParameterizedTest
            @ValueSource(ints = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
            public void test (int length) {
                server = new Server("test", new NumericLifeToken(), length);
                assertEquals(length, server.getWorldBorderLength());
            }
        }
    }

    @Nested
    class PlayersTest {
        private static org.bukkit.Server bukkitServer;
        private Player player;

        @BeforeAll
        public static void setup() {
            bukkitServer = MockBukkit.mock();
        }

        @Test
        public void isNewPlayerAdded () {
            server = new Server("test");
            player = new Player(new UUID(0, 0), "name", new NumericLifeToken());
            server.addPlayer(player);

            assertTrue(server.getPlayers().contains(player));
        }
    }
}
