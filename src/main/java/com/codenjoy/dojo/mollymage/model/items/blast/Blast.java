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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.BOOM;

public class Blast extends PointImpl implements State<Element, Player> {

    private Hero hero;

    public Blast(int x, int y, Hero hero) {
        super(x, y);
        this.hero = hero;
    }

    public Hero owner() {
        return hero;
    }

    @Override
    public Element state(Player player, List<State> alsoAtPoint) {
        return BOOM;
    }
}
