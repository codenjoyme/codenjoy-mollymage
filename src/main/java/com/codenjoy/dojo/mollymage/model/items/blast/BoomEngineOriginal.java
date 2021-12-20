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


import com.codenjoy.dojo.mollymage.model.Field;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.LinkedList;
import java.util.List;

public class BoomEngineOriginal implements BoomEngine {

    private Field field;
    private Hero owner;

    public BoomEngineOriginal(Field field, Hero owner) {
        this.owner = owner;
        this.field = field;
    }

    @Override
    public List<Blast> boom(Point source, int radius) {
        List<Blast> blasts = new LinkedList<>();

        add(blasts, source);

        for (Direction direction : Direction.getValues()) {
            addBlast(blasts, radius, direction, source);
        }

        return blasts;
    }

    @Override
    public List<Blast> boom(Poison poison) {
        List<Blast> blasts = new LinkedList<>();

        addBlast(blasts, poison.power(), poison.direction(), owner);

        return blasts;
    }

    private boolean barriersAt(Point pt) {
        return field.ghosts().contains(pt)
                || field.hunters().contains(pt)
                || field.boxes().contains(pt)
                || field.heroes().stream()
                        .filter(Hero::isActiveAndAlive)
                        .anyMatch(hero -> hero.itsMe(pt));
    }

    private boolean wallAt(Point pt) {
        return field.walls().contains(pt);
    }

    private void addBlast(List<Blast> blasts, int length, Direction direction, Point point) {
        Point pt = point.copy();
        for (int i = 0; i < length; i++) {
            pt = direction.change(pt);
            if (!add(blasts, pt)) {
                break;
            }
        }
    }

    private boolean add(List<Blast> blasts, Point pt) {
        if (pt.isOutOf(field.size())) {
            return false;
        }

        if (wallAt(pt)) {
            return false;
        }

        if (barriersAt(pt)) {
            blasts.add(new Blast(pt.getX(), pt.getY(), owner));
            return false;
        }

        blasts.add(new Blast(pt.getX(), pt.getY(), owner));
        return true;
    }

    private boolean isNotWall(List<? extends Point> barriers, Point pt) {
        return !barriers.get(barriers.indexOf(pt)).getClass().equals(Wall.class);
    }

}
