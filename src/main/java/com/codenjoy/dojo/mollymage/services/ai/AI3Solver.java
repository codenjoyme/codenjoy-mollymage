package com.codenjoy.dojo.mollymage.services.ai;

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


import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;

public class AI3Solver implements Solver<Board> {
    private Dice dice;

    public AI3Solver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        Direction direction = null;
        int toX = 0;
        int toY = 0;
        do {
            direction = Direction.random(dice);

            Point hero = board.getHero();
            toX = direction.changeX(hero.getX());
            toY = direction.changeY(hero.getY());

        } while (board.isAt(toX, toY, Element.WALL) || board.isAt(toX, toY, Element.TREASURE_BOX));

        String command = direction.toString();
        if (!command.equalsIgnoreCase("act") && !command.equalsIgnoreCase("stop")) {
            if (dice.next(5) == 0) {
                command = "act, " + command;
            }
        }
        return command;
    }
}
