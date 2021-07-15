package com.codenjoy.dojo.mollymage.services.ai;

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


import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.*;
import org.apache.commons.lang3.StringUtils;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class AISolver implements Solver<Board> {

    private Direction direction;
    private Point bomb;
    private Dice dice;
    private Board board;

    public AISolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        Point hero = board.getHero();

        boolean nearTreasureBox = board.isNear(hero.getX(), hero.getY(), Element.TREASURE_BOX);
        boolean nearOtherHero = board.isNear(hero.getX(), hero.getY(), Element.OTHER_HERO);
        boolean nearEnemyHero = board.isNear(hero.getX(), hero.getY(), Element.ENEMY_HERO);
        boolean nearGhost = board.isNear(hero.getX(), hero.getY(), Element.GHOST);
        boolean potionNotDropped = !board.isAt(hero.getX(), hero.getY(), Element.POTION_HERO);

        bomb = null;
        if ((nearTreasureBox || nearOtherHero || nearEnemyHero || nearGhost) && potionNotDropped) {
            bomb = new PointImpl(hero);
        }

        direction = tryToMove(hero);

        String result = mergeCommands(bomb, direction);
        return StringUtils.isEmpty(result) ? Direction.STOP.toString() : result;
    }

    private String mergeCommands(Point bomb, Direction direction) {
        if (Direction.STOP.equals(direction)) {
            bomb = null;
        }
        return "" + ((bomb!=null)? Direction.ACT+((direction!=null)?",":""):"") +
                ((direction!=null)?direction:"");
    }

    private Direction tryToMove(Point pt) {
        int count = 0;
        int newX = pt.getX();
        int newY = pt.getY();
        Direction result = null;
        boolean again = false;
        do {
            result = whereICAnGoFrom(pt);
            if (result == null) {
                return null;
            }

            newX = result.changeX(pt.getX());
            newY = result.changeY(pt.getY());

            boolean bombAtWay = bomb != null && bomb.equals(pt(newX, newY));
            boolean barrierAtWay = board.isBarrierAt(newX, newY);
            boolean blastAtWay = board.getFutureBlasts().contains(pt(newX, newY));
            boolean ghostNearWay = board.isNear(newX, newY, Element.GHOST);

            if (blastAtWay && board.countNear(pt.getX(), pt.getY(), Element.NONE) == 1 &&
                    !board.isAt(pt.getX(), pt.getY(), Element.POTION_HERO)) {
                return Direction.STOP;
            }

            again = bombAtWay || barrierAtWay || ghostNearWay;

            // TODO продолжить но с тестами
            boolean deadEndAtWay = board.countNear(newX, newY, Element.NONE) == 0 && bomb != null;
            if (deadEndAtWay) {
                bomb = null;
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
                ((result.inverted() == direction && bomb == null) ||
                        !board.isAt(result.changeX(pt.getX()), result.changeY(pt.getY()), Element.NONE)));
        if (count > 10) {
            return null;
        }
        return result;
    }
}
