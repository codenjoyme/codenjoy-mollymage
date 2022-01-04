package com.codenjoy.dojo.mollymage.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.client.local.DiceGenerator;

import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.utils.TestUtils;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_ENABLED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GameRunnerTest {

    private final PrinterFactory<Element, Player> printer = new PrinterFactoryImpl<>();

    @Test
    public void shouldWork() {
        int size = 23;
        int boxes = 5;
        int ghosts = 15;

        Dice dice = new DiceGenerator().getDice();
        for (int i = 0; i < 100; i++) {

            EventListener listener = mock(EventListener.class);
            GameRunner gameType = new GameRunner() {
                @Override
                public Dice getDice() {
                    return dice;
                }

                @Override
                public GameSettings getSettings() {
                    return super.getSettings()
                            .bool(ROUNDS_ENABLED, false)
                            .integer(TREASURE_BOX_COUNT, boxes)
                            .integer(GHOSTS_COUNT, ghosts);
                }
            };

            GameSettings settings = gameType.getSettings();

            Game game = TestUtils.buildGame(gameType, listener, printer);
            game.getField().tick();

            PlayerScores scores = gameType.getPlayerScores(10, settings);
            assertEquals(10, scores.getScore());
            scores.event(Event.KILL_GHOST);
            assertEquals(20, scores.getScore());

            assertEquals(size, gameType.getBoardSize(settings).getValue().intValue());

            Joystick joystick = game.getJoystick();

            String actual = (String) game.getBoardAsString();

            // there are 3 different maps
            List<Integer> wallsList = new LinkedList<>(Arrays.asList(88, 169, 240));
            int walls;
            while (true) {
                walls = wallsList.remove(0);
                try {
                    assertCharCount(actual, "☼", walls);
                    break;
                } catch (AssertionError e) {
                    if (wallsList.isEmpty()) {
                        throw e;
                    }
                }
            }

            assertCharCount(actual, "#", boxes);
            int gameOver = 0;
            try {
                assertCharCount(actual, "☺", 1);
                assertCharCount(actual, "&", ghosts);
                assertEquals(false, game.isGameOver());
            } catch (AssertionError e) {
                assertCharCount(actual, "Ѡ", 1);
                gameOver = 1;
                assertCharCount(actual, "&", ghosts - gameOver);
                assertEquals(true, game.isGameOver());
            }
            assertCharCount(actual, " ", size * size - walls - boxes - ghosts - 1 + gameOver);

            joystick.act();
            for (int index = 0; index < 100; index++) {
                game.getField().tick();
            }

            assertEquals(true, game.isGameOver());
        }
    }

    private void assertCharCount(String actual, String ch, int count) {
        assertEquals(count, actual.length() - actual.replace(ch, "").length());
    }

}
