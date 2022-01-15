package com.codenjoy.dojo.mollymage;

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
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.services.GameRunner;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static com.codenjoy.dojo.utils.TestUtils.assertPerformance;
import static org.junit.Assert.assertEquals;

public class PerformanceTest {

    @Test
    public void test() {
        // about 10.9 sec
        int boardSize = 50;
        int walls = 500;
        int ghosts = 100;
        int players = 20;
        int ticks = 100;

        int expectedCreation = 1200;
        int expectedTick = 3000;
        int expectedPrint = 8000;

        PrinterFactory<Element, Player> factory = new PrinterFactoryImpl<>();

        String level = toString(boardSize, factory);
        Dice dice = new DiceGenerator().getDice(20000);
        GameRunner runner = new GameRunner(){

            @Override
            public Dice getDice() {
                return dice;
            }

            @Override
            public GameSettings getSettings() {
                return super.getSettings()
                        .setLevelMaps(LevelProgress.levelsStartsFrom1, level)
                        .integer(POTION_POWER, 10)
                        .integer(TREASURE_BOX_COUNT, walls)
                        .integer(GHOSTS_COUNT, ghosts);
            }
        };

        boolean printBoard = false;
        String board = assertPerformance(runner,
                players, ticks,
                expectedCreation, expectedPrint, expectedTick,
                printBoard);

        assertEquals(
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼&☼ ☼ ☼&☼#☼&☼#☼#☼ ☼ ☼&☼#☼#☼#☼#☼ ☼ ☼#☼#☼ ☼#☼#☼ ☼ ☼☼\n" +
                "☼     &  &i # # ##  #& #  #&##  &    #          #☼\n" +
                "☼ ☼ ☼#☼#☼ ☼#☼&☼&☼#☼#☼#☼#☼ ☼#☼#☼#☼#☼ ☼ ☼ ☼ ☼#☼#☼#☼☼\n" +
                "☼#     ♥ 3 r      # ##♥ ##   # ##   #  &####  ###☼\n" +
                "☼ ☼ ☼ ☼ ☼#☼#☼ ☼ ☼ ☼#☼&☼#☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼A☼ ☼ ☼&☼☼\n" +
                "☼      ## ##### ##   ####    #       T &  ♥r# ## ☼\n" +
                "☼♠☼ ☼ ☼ ☼#☼#☼ ☼#☼#☼ ☼ ☼ ☼ ☼#☼#☼#☼#☼r☼ ☼ ☼ ☼#☼ ☼#☼☼\n" +
                "☼# c             ####        ###  ## &#    +    #☼\n" +
                "☼#☼#☼ ☼#☼ ☼#☼#☼ ☼#☼#☼#☼ ☼ ☼#☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼#☼ ☼☼\n" +
                "☼#####    # ## & & #& &#  &#     #   # #   # # & ☼\n" +
                "☼#☼ ☼ ☼#☼ ☼#☼#☼ ☼ ☼#☼#☼ ☼&☼#☼ ☼ ☼ ☼#☼ ☼#☼ ☼#☼ ☼ ☼☼\n" +
                "☼c    &#### #    &# ##    #  #  &      #♠#&## # #☼\n" +
                "☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼&☼ ☼#☼ ☼ ☼ ☼☼\n" +
                "☼♥            #      &   #  #   #       #   &    ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼&☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼&☼ ☼ ☼&☼ ☼☼\n" +
                "☼          ♥ +   &   #  # # #        #       && &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼&☼&☼#☼☼\n" +
                "☼    #            r +&  #         ♥  ## #&   ## &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼♥☼ ☼&☼#☼ ☼&☼#☼ ☼ ☼ ☼#☼#☼&☼ ☼#☼&☼☼\n" +
                "☼           &      & &##  #  ##     ###&#  ###  #☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼#☼ ☼&☼ ☼ ☼#☼#☼ ☼ ☼#☼T☼ ☼☼\n" +
                "☼ #  ♥          #      & #   #c # #   &   ##   ##☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼&☼ ☼#☼T☼ ☼#☼#☼ ☼#☼#☼ ☼ ☼☼\n" +
                "☼      1  &            &## #& ### ####  #####    ☼\n" +
                "☼c☼ ☼#☼#☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼#☼#☼&☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼☼\n" +
                "☼& &           # #   &  # ## #############&  #  #☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼ ☼#☼ ☼ ☼&☼ ☼ ☼ ☼ ☼#☼ ☼#☼#☼ ☼ ☼#☼#☼ ☼#☼☼\n" +
                "☼  #### # #1# # #&#  &###    ###  ## ####&# #   #☼\n" +
                "☼ ☼#☼#☼ ☼ ☼ ☼#☼#☼ ☼#☼ ☼ ☼&☼#☼#☼ ☼#☼#☼#☼#☼ ☼ ☼#☼ ☼☼\n" +
                "☼  ####  ♥  # &  ##1##       #  # 3#&#  # # #    ☼\n" +
                "☼ ☼#☼ ☼ ☼#☼ ☼#☼&☼ ☼ ☼#☼☺☼ ☼#☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼&☼☼\n" +
                "☼ ##### ♥  # # &T♥       &1&       &#### &3 ♥ #&#☼\n" +
                "☼#☼#☼ ☼ ☼ ☼#☼#☼ ☼#☼ ☼ ☼#☼ ☼ ☼A☼#☼#☼ ☼#☼#☼#☼ ☼ ☼ ☼☼\n" +
                "☼###  &   # #####         c    #### #   & #1    #☼\n" +
                "☼ ☼#☼ ☼ ☼ ☼ ☼#☼ ☼#☼#☼ ☼ ☼A☼ ☼#☼2☼#☼#☼#☼ ☼&☼♥☼#☼ ☼☼\n" +
                "☼##   ##    ##  ### # ## #     ♥ #  # ##### ##&  ☼\n" +
                "☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼#☼#☼ ☼☼\n" +
                "☼ ###     A T     #     #       c # ##   ### #   ☼\n" +
                "☼#☼ ☼#☼#☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼#☼ ☼ ☼☼\n" +
                "☼ # #        #       #      #   # # #H҉҉##&   &  ☼\n" +
                "☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼ ☼ ☼☼\n" +
                "☼    &   &         &   c&   i &  &   4#&&#      &☼\n" +
                "☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼&☼#☼ ☼ ☼ ☼ ☼ ☼ ☼♥☼ ☼#☼ ☼#☼ ☼☼\n" +
                "☼     #      &     ##   #              ####   ###☼\n" +
                "☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼#☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼#☼ ☼☼\n" +
                "☼    #           # #  ## #   #     T      # #####☼\n" +
                "☼ ☼#☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼&☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼#☼☼\n" +
                "☼  ##♠        &   #########   # r& &    # #&    #☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n",
                board);

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
}