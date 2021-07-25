package com.codenjoy.dojo.mollymage.model.items.ghost;

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


import com.codenjoy.dojo.mollymage.model.Objects;
import com.codenjoy.dojo.mollymage.model.ObjectsDecorator;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.settings.Parameter;

import java.util.HashSet;
import java.util.Set;

import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;

public class Ghosts extends ObjectsDecorator implements Objects {

    public static final int MAX = 1000;

    private Parameter<Integer> count;
    private Dice dice;

    public Ghosts(Objects walls, Parameter<Integer> count, Dice dice) {
        super(walls);
        this.dice = dice;
        this.count = count;
    }

    public void regenerate() {     // TODO потестить
        if (count.getValue() < 0) {
            count.update(0);
        }

        int count = walls.listSubtypes(Ghost.class).size();

        int iteration = 0;
        Set<Point> checked = new HashSet<>();
        while (count < this.count.getValue() && iteration++ < MAX) {
            Point pt = PointImpl.random(dice, field.size());

            if (checked.contains(pt) || field.isBarrier(pt, !FOR_HERO)) {
                checked.add(pt);
                continue;
            }

            walls.add(new Ghost(pt, field, dice));
            count++;
        }

        if (iteration >= MAX) {
            System.out.println("Dead loop at Ghosts.regenerate!"); // TODO тут часто вылетает :(
        }
    }

    @Override
    public void tact() {
        regenerate();
    }
}
