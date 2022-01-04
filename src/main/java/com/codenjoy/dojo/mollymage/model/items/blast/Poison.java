package com.codenjoy.dojo.mollymage.model.items.blast;

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

import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.PointImpl;

public class Poison extends PointImpl {
    final private Hero owner;
    final private Direction direction;
    final private int power;

    public Poison(Hero owner, Direction direction, int power) {
        super(owner.getX(), owner.getY());
        this.direction = direction;
        this.owner = owner;
        this.power = power;
    }

    public Hero owner() {
        return owner;
    }

    public int power() {
        return power;
    }

    public Direction direction() {
        return direction;
    }
}
