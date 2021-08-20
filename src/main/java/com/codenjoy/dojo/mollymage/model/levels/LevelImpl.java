package com.codenjoy.dojo.mollymage.model.levels;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.utils.LevelUtils;

import java.util.List;
import java.util.NoSuchElementException;

import static com.codenjoy.dojo.games.mollymage.Element.*;

public class LevelImpl implements Level {

    private final String map;
    private final int size;
    private final LengthToXY xy;

    public LevelImpl(String map) {
        this.map = LevelUtils.clear(map);
        this.size = (int) Math.sqrt(map.length());
        this.xy = new LengthToXY(size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Point getHeroPosition() {
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == HERO.ch()) {
                return xy.getXY(index);
            }
        }
        throw new NoSuchElementException("no hero");
    }

    @Override
    public List<Wall> getWalls() {
        return LevelUtils.getObjects(xy, map, (point, element) -> new Wall(point), WALL);
    }

    @Override
    public List<TreasureBox> getBoxes() {
        return LevelUtils.getObjects(xy, map, (point, element) -> new TreasureBox(point), TREASURE_BOX);
    }
}
