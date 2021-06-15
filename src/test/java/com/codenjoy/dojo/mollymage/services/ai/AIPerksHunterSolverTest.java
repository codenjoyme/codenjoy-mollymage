package com.codenjoy.dojo.mollymage.services.ai;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AIPerksHunterSolverTest {

    @Test
    public void test() {
        asrtWay("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☺xx      # # ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼##x          ☼\n" +
                "☼ ☼x☼#☼ ☼ ☼ ☼ ☼\n" +
                "☼  x#    #    ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼  xxxxxxx    ☼\n" +
                "☼#☼ ☼ ☼#☼x☼ ☼#☼\n" +
                "☼  #  #  xxx  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ ##      #x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ #   #    xx+☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        // добавили митчопера &
        asrtWay("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☺xx      # # ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼##x          ☼\n" +
                "☼ ☼x☼#☼ ☼ ☼ ☼ ☼\n" +
                "☼  x#    #    ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼  xxxxxxx    ☼\n" +
                "☼#☼ ☼ ☼#☼x☼ ☼#☼\n" +
                "☼  #  #  xxx  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ ##      #x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ #   #  & xx+☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        // добавили бомбера ♥
        asrtWay("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☺xx      # # ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼##xxxxxxxxx  ☼\n" +
                "☼ ☼ ☼#☼ ☼ ☼x☼ ☼\n" +
                "☼   #    # x  ☼\n" +
                "☼ ☼ ☼ ☼#☼ ☼x☼ ☼\n" +
                "☼    ♥     x  ☼\n" +
                "☼#☼ ☼ ☼#☼ ☼x☼#☼\n" +
                "☼  #  #    x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ ##      #x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ #   #  & xx+☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        // добавили бомбера с бомбой ♠
        asrtWay("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☺xx      # # ☼\n" +
                "☼ ☼x☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼##xxxxxxxxxxx☼\n" +
                // TODO почему тут машрут не простраивается, если поднять ♠ на клеточку выше
                "☼ ☼ ☼#☼ ☼ ☼ ☼x☼\n" +
                "☼   #    # ♠ x☼\n" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼x☼\n" +
                "☼    ♥     xxx☼\n" +
                "☼#☼ ☼ ☼#☼ ☼x☼#☼\n" +
                "☼  #  #    x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ ##      #x  ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼x☼#☼\n" +
                "☼ #   #  & xx+☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        asrtWay("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☺        # # ☼\n" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼##           ☼\n" +
                "☼ ☼ ☼#☼ ☼ ☼♠☼ ☼\n" + // TODO вот так
                "☼   #    #    ☼\n" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼\n" +
                "☼    ♥        ☼\n" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼\n" +
                "☼  #  #       ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼\n" +
                "☼ ##      #   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼\n" +
                "☼ #   #  &   +☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    private void asrtWay(String expected) {
        Board board = new Board() {
            @Override
            public Element valueOf(char ch) {
                return Element.valueOf(ch);
            }
        };

        assertEquals(expected,
                TestUtils.printWay(expected,
                        Element.HERO, Element.POTION_BLAST_RADIUS_INCREASE,
                        Element.NONE, Element.DEAD_GHOST,
                        board,
                        b -> AIPerksHunterSolver.possible((Board) b)));
    }

}
