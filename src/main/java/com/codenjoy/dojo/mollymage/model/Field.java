package com.codenjoy.dojo.mollymage.model;

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


import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.blast.Poison;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBoxes;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghosts;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.round.RoundGameField;

import java.util.List;
import java.util.Optional;

public interface Field extends RoundGameField<Player> {  // TODO применить тут ISP (все ли методы должны быть паблик?)

    boolean FOR_HERO = true;

    boolean isFree(Point pt);

    int size();

    List<Hero> heroes(boolean activeAliveOnly);

    List<Potion> potions();

    List<Potion> potions(Hero hero);

    List<Wall> walls();

    boolean isBarrier(Point pt, boolean isForHero);

    void remove(Player player);

    List<Blast> blasts();

    void drop(Potion potion);

    void remove(Potion potion);

    void remove(Point pt);

    List<PerkOnBoard> perks();

    PerkOnBoard pickPerk(Point pt);

    void addPerk(Player player, Perk perk);

    void addPerk(int teamId, Perk perk);

    Dice dice();

    Optional<Point> freeRandom(Player player);

    TreasureBoxes boxes();

    Ghosts ghosts();

    void addPoison(Poison poison);
}
