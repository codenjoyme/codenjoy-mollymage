package com.codenjoy.dojo.mollymage.model.items.ghost;

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

import com.codenjoy.dojo.mollymage.model.*;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;
import com.codenjoy.dojo.services.field.Accessor;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.*;
import static com.codenjoy.dojo.services.StateUtils.filterOne;

public class GhostHunter extends Ghost {

    private Hero prey;
    private DeikstraFindWay way;
    private PerkOnBoard perk;
    private boolean alive = true;

    public GhostHunter(PerkOnBoard perk, Field field, Hero prey) {
        super(perk.copy());
        super.init(field);
        this.prey = prey;
        this.perk = perk;
        this.way = new DeikstraFindWay();
    }

    public DeikstraFindWay.Possible possible(Field field) {
        Accessor<Wall> walls = field.walls();

        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point point) {
                return !walls.contains(point);
            }
        };
    }

    public List<Direction> getDirections(Point from, List<Point> to) {
        DeikstraFindWay.Possible map = possible(field);
        return way.getShortestWay(field.size(), from, to, map);
    }

    @Override
    public void tick() {
        // если нарушитель уже того, выпиливаемся тоже
        if (!prey.isActiveAndAlive()) {
            // привидение умрет от праведного (ничейного) огня! мы увидим его трупик 1 тик
            die();
            return;
        }

        List<Direction> directions = getDirections(this, Arrays.asList(prey));
        if (directions.isEmpty()) {
            // если не видим куда идти - выпиливаемся
            die();
        } else {
            // если видим - идем
            direction = directions.get(0);
            Point from = this.copy();
            this.move(direction.change(from));

            // попутно сносим стенки на пути прожженные (если есть)
            field.boxes().removeAt(from);
        }
    }

    public void die() {
        field.remove(this);
        alive = false;
        // ларчик просто открывался, перки надо не убивать
        // а собирать, иначе они за тобой будут гнаться
        field.perks().add(perk);
        perk.move(this);
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        if (alive) {
            TreasureBox wall = filterOne(alsoAtPoint, TreasureBox.class);
            if (wall != null) {
                return OPENING_TREASURE_BOX;
            }

            Blast blast = filterOne(alsoAtPoint, Blast.class);
            if (blast != null) {
                return GHOST;
            }

            return DEAD_GHOST;
        }

        // если поднизом зелье, видеть ее важнее, чем трупик привидения TODO test me
        Potion potion = filterOne(alsoAtPoint, Potion.class);
        if (potion != null) {
            return potion.state(player, alsoAtPoint);
        }

        return GHOST;
    }
}
