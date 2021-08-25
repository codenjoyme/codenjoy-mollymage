package com.codenjoy.dojo.mollymage.model.items.box;

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
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.ghost.GhostHunter;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

import static com.codenjoy.dojo.games.mollymage.Element.OPENING_TREASURE_BOX;
import static com.codenjoy.dojo.games.mollymage.Element.TREASURE_BOX;
import static com.codenjoy.dojo.services.StateUtils.filterOne;

public class TreasureBox extends PointImpl implements State<Element, Player> {

    public TreasureBox(int x, int y) {
        super(x, y);
    }

    public TreasureBox(Point pt) {
        super(pt);
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        Blast blast = filterOne(alsoAtPoint, Blast.class);
        if (blast != null) {
            return OPENING_TREASURE_BOX;
        }

        GhostHunter ghost = filterOne(alsoAtPoint, GhostHunter.class);
        if (ghost != null) {
            return ghost.state(player, alsoAtPoint);
        }

        return TREASURE_BOX;
    }
}
