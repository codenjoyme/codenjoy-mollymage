package com.codenjoy.dojo.mollymage.model.items;

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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.field.Multimap;

import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.WALL;

public class Wall extends PointImpl implements State<Element, Player>, Tickable {

    public Wall(int x, int y) {
        super(x, y);
    }

    public Wall(Point pt) {
        super(pt);
    }

    @Override
    public Element state(Player player, Multimap<Class<? extends Point>, Point> alsoAtPoint) {
        return WALL;
    }

    @Override
    public void tick() {
        // do nothing
    }
}
