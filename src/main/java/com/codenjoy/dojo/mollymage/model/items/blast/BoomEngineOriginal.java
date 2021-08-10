package com.codenjoy.dojo.mollymage.model.items.blast;

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


import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.LinkedList;
import java.util.List;

public class BoomEngineOriginal implements BoomEngine {

    private Hero hero;

    public BoomEngineOriginal(Hero hero) {
        this.hero = hero;
    }

    @Override
    public List<Blast> boom(List<? extends Point> barriers, int boardSize, Point source, int radius) {
        List<Blast> blasts = new LinkedList<>();

        add(barriers, boardSize, blasts, source);

        for (Direction direction : Direction.getValues()) {
            Point point = source.copy();
            for (int i = 0; i < radius; i++) {
                point = direction.change(point);
                if (!add(barriers, boardSize, blasts, point)) {
                    break;
                }
            }
        }

        return blasts;
    }

    @Override
    public List<Blast> boom(List<? extends Point> barriers, int size, Poison poison) {
        List<Blast> blasts = new LinkedList<>();

        final int length = poison.getPower();
        final Direction direction = poison.getDirection();
        Point point = hero.copy();
        for (int i = 0; i < length; i++) {
            point = direction.change(point);
            if (!add(barriers, size, blasts, point)) {
                break;
            }
        }
        return blasts;
    }

    private boolean add(List<? extends Point> barriers, int boardSize, List<Blast> blasts, Point pt) {
        if (pt.isOutOf(boardSize)) {
            return false;
        }

        if (barriers.contains(pt)) {
            if (isNotWall(barriers, pt)) {
                blasts.add(new Blast(pt.getX(), pt.getY(), hero));
            }
            return false;
        }

        blasts.add(new Blast(pt.getX(), pt.getY(), hero));
        return true;
    }

    private boolean isNotWall(List<? extends Point> barriers, Point pt) {
        return !barriers.get(barriers.indexOf(pt)).getClass().equals(Wall.class);
    }

}
