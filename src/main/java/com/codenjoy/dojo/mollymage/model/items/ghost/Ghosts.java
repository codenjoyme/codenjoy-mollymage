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


import com.codenjoy.dojo.mollymage.model.Field;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.Tickable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;

public class Ghosts implements Tickable {

    public static final int MAX = 1000;

    private Dice dice;
    private Field field;
    private GameSettings settings;

    public Ghosts(GameSettings settings, Dice dice) {
        this.settings = settings;
        this.dice = dice;
    }

    public void init(Field field) {
        this.field = field;
    }

    @Override
    public void tick() {
        regenerate();
        field.ghosts().tick();
    }

    public void regenerate() {     // TODO потестить
        if (settings.integer(GHOSTS_COUNT) < 0) {
            settings.integer(GHOSTS_COUNT, 0);
        }

        int actual = field.ghosts().size();
        int expected = settings.integer(GHOSTS_COUNT);

        int iteration = 0;
        Set<Point> checked = new HashSet<>();
        while (actual < expected && iteration++ < MAX) {
            Point pt = PointImpl.random(dice, field.size());

            if (checked.contains(pt) || field.isBarrier(pt, !FOR_HERO)) {
                checked.add(pt);
                continue;
            }

            field.ghosts().add(new Ghost(pt, field, dice));
            actual++;
        }

        if (iteration >= MAX) {
            System.out.println("Dead loop at Ghosts.regenerate!");
        }
    }
}