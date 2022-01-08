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
import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import org.apache.commons.lang3.StringUtils;

import static com.codenjoy.dojo.games.mollymage.Element.*;

public class AISolver implements Solver<Board> {

    private Direction direction;
    private Point potion;
    private Dice dice;
    private Board board;

    public AISolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        Point hero = board.getHero();

        boolean nearTreasureBox = board.isNear(hero.getX(), hero.getY(), TREASURE_BOX);
        boolean nearOtherHero = board.isNear(hero.getX(), hero.getY(), OTHER_HERO);
        boolean nearEnemyHero = board.isNear(hero.getX(), hero.getY(), ENEMY_HERO);
        boolean nearGhost = board.isNear(hero.getX(), hero.getY(), GHOST);
        boolean potionNotDropped = !board.isAt(hero.getX(), hero.getY(), HERO_POTION);

        potion = null;
        if ((nearTreasureBox || nearOtherHero || nearEnemyHero || nearGhost) && potionNotDropped) {
            potion = new PointImpl(hero);
        }

        direction = tryToMove(hero);

        String result = mergeCommands(potion, direction);
        return StringUtils.isEmpty(result) ? Direction.STOP.toString() : result;
    }

    private String mergeCommands(Point potion, Direction direction) {
        if (Direction.STOP.equals(direction)) {
            potion = null;
        }
        return "" + ((potion!=null)? Direction.ACT+((direction!=null)?",":""):"") +
                ((direction!=null)?direction:"");
    }

    private Direction tryToMove(Point from) {
        int count = 0;
        Direction result;
        boolean again;
        do {
            result = whereICAnGoFrom(from);
            if (result == null) {
                return null;
            }

            Point to = result.change(from);

            boolean potionAtWay = potion != null && potion.equals(to);
            boolean barrierAtWay = board.isBarrierAt(to);
            boolean blastAtWay = board.getFutureBlasts().contains(to);
            boolean ghostNearWay = board.isNear(to, GHOST);

            if (blastAtWay && board.countNear(from, NONE) == 1 &&
                    !board.isAt(from, HERO_POTION)) {
                return Direction.STOP;
            }

            again = potionAtWay || barrierAtWay || ghostNearWay;

            // TODO продолжить но с тестами
            boolean deadEndAtWay = board.countNear(to, NONE) == 0 && potion != null;
            if (deadEndAtWay) {
                potion = null;
            }
        } while (count++ < 20 && again);

        if (count < 20) {
            return result;
        }
        return Direction.ACT;
    }

    private Direction whereICAnGoFrom(Point pt) {
        Direction result;
        int count = 0;
        do {
            result = Direction.valueOf(dice.next(4));
        } while (count++ < 10 &&
                ((result.inverted() == direction && potion == null) ||
                        !board.isAt(result.changeX(pt.getX()), result.changeY(pt.getY()), Element.NONE)));
        if (count > 10) {
            return null;
        }
        return result;
    }
}
