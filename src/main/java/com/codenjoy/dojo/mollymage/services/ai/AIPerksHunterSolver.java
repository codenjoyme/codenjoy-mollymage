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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;
import com.codenjoy.dojo.services.dice.RandomDice;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.GHOST;

public class AIPerksHunterSolver implements Solver<Board> {

    private DeikstraFindWay way;

    public AIPerksHunterSolver(Dice dice) {
        this.way = new DeikstraFindWay();
    }

    public static DeikstraFindWay.Possible possible(Board board) {
        List<Point> futureBlasts = board.getFutureBlasts();

        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point from, Direction where) {
                Point to = where.change(from);
                if (futureBlasts.contains(to)) return false;
                if (board.isAt(to, GHOST)) return false;
                return true;
            }

            @Override
            public boolean possible(Point point) {
                return !board.isBarrierAt(point);
            }
        };
    }

    public List<Direction> getDirections(Board board) {
        int size = board.size();
        Point from = board.getHero();
        List<Point> to = board.get(
                Element.POTION_BLAST_RADIUS_INCREASE,
                Element.POTION_COUNT_INCREASE,
                Element.POTION_IMMUNE,
                Element.POTION_REMOTE_CONTROL,
                Element.POISON_THROWER,
                Element.POTION_EXPLODER);
        if (to.isEmpty()) {
            return Arrays.asList();
        }
        DeikstraFindWay.Possible map = possible(board);
        return way.getShortestWay(size, from, to, map);
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) return Direction.STOP.toString();
        List<Direction> result = getDirections(board);
        if (result.isEmpty()) return Direction.ACT.toString();
        return result.get(0).toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(args,
                // paste here board page url from browser after registration
                // or put it as command line parameter
                "http://127.0.0.1:8080/codenjoy-contest/board/player/anyidyouwant?code=12345678901234567890",
                new AISolver(new RandomDice()),
                new Board());
    }
}
