package com.codenjoy.dojo.mollymage.model.mock;

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
import com.codenjoy.dojo.mollymage.model.ObjectsImpl;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Point;

import java.util.List;

public class TreasureBoxesStub extends ObjectsDecorator implements Objects {

    public TreasureBoxesStub(GameSettings settings, List<? extends Point> points) {
        super(new ObjectsImpl(settings));
        points.forEach(pt -> this.walls.add(new TreasureBox(pt)));
    }

    @Override
    protected void tact() {
        // do nothing
    }

}
