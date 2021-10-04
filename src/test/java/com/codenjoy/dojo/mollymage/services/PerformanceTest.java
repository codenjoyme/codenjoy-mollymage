package com.codenjoy.dojo.mollymage.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.profile.Profiler;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_ENABLED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PerformanceTest {

    private Profiler profiler;

    @Test
    public void test() {

        // about 9 sec
        int boardSize = 100;
        int walls = 1500;
        int ghosts = 200;
        int players = 100;
        int ticks = 100;
        boolean printBoard = false;

        profiler = new Profiler(){{
            PRINT_SOUT = true;
        }};
        profiler.start();

        PrinterFactory<Element, Player> factory = new PrinterFactoryImpl<>();

        String level = toString(boardSize, factory);
        GameRunner runner = new GameRunner(){
            @Override
            public GameSettings getSettings() {
                return super.getSettings()
                        .bool(ROUNDS_ENABLED, false)
                        .string(LEVEL_MAP, level)
                        .integer(POTION_POWER, 10)
                        .integer(TREASURE_BOX_COUNT, walls)
                        .integer(GHOSTS_COUNT, ghosts);
            }
        };

        List<Game> games = TestUtils.getGames(players, runner,
                factory, () -> mock(EventListener.class));

        profiler.done("creation");

        for (int i = 0; i < ticks; i++) {
            for (Game game : games) {
                Joystick joystick = game.getJoystick();
                int next = new RandomDice().next(5);
                if (next % 2 == 0) {
                    joystick.act();
                }
                switch (next) {
                    case 0: joystick.left(); break;
                    case 1: joystick.right(); break;
                    case 2: joystick.up(); break;
                    case 3: joystick.down(); break;
                }
            }
            // because of MULTIPLE there is only one tick for all
            games.get(0).getField().tick();
            for (Game game : games) {
                if (game.isGameOver()) {
                    game.newGame();
                }
            }
            profiler.done("tick");

            Object board = null;
            for (Game game : games) {
                board = game.getBoardAsString();
            }
            if (printBoard) {
                System.out.println(board);
            }
            profiler.done("print");
        }

        profiler.print();

        int reserve = 3;
        // сколько пользователей - столько раз выполнялось
        assertLess("print", 10000 * reserve);
        assertLess("tick", 6000 * reserve);
        // выполнялось единожды
        assertLess("creation", 2000 * reserve);

    }

    private String toString(int boardSize, PrinterFactory<Element, Player> factory) {
        return (String) factory.getPrinter(new BoardReader<Player>() {
            @Override
            public int size() {
                return boardSize;
            }

            @Override
            public void addAll(Player player, Consumer<Iterable<? extends Point>> processor) {
                processor.accept(generate(boardSize));
            }
        }, null).print();
    }

    private List<Wall> generate(int size) {
        List<Wall> result = new LinkedList<>();
        for (int x = 0; x < size; x++) {
            result.add(new Wall(pt(x, 0)));
            result.add(new Wall(pt(x, size - 1)));
        }

        final int D = 1;
        for (int y = D; y < size - D; y++) {
            result.add(new Wall(pt(0, y)));
            result.add(new Wall(pt(size - 1, y)));
        }

        for (int x = 2; x <= size - 2; x++) {
            for (int y = 2; y <= size - 2; y++) {
                if (y % 2 != 0 || x % 2 != 0) {
                    continue;
                }
                result.add(new Wall(pt(x, y)));
            }
        }
        return result;
    }

    private void assertLess(String phase, double expected) {
        double actual = profiler.info(phase).getTime();
        assertEquals(actual + " > " + expected,
                true, actual < expected);
    }
}
