package com.codenjoy.dojo.bomberman.model.perks;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.bomberman.model.Elements;
import org.junit.Test;

import static org.junit.Assert.*;

public class HeroPerksTest {

    @Test
    public void should_reset_timer_when_add_Bomb_Blast_Increase_Twice() {
        HeroPerks hp = new HeroPerks();
        BombBlastRadiusIncrease bip = new BombBlastRadiusIncrease(2, 5);
        Elements e = Elements.BOMB_BLAST_RADIUS_INCREASE;

        hp.add(bip);
        hp.tick();
        hp.tick();

        Perk oldBip = hp.getPerk(e);
        assertEquals("Perk timer is expected to be countdown", 3, oldBip.getTimer());

        hp.add(bip);
        Perk newBip = hp.getPerk(e);
        assertEquals("Perk timer is expected to be reset", 5, newBip.getTimer());

    }

    @Test
    public void should_remove_Perk_on_expiration() {
        HeroPerks hp = new HeroPerks();
        BombBlastRadiusIncrease bip = new BombBlastRadiusIncrease(2, 2);
        hp.add(bip);

        hp.tick();
        int perksCount = hp.getPerksList().size();
        assertEquals("Perks list must not be empty", 1, perksCount);

        hp.tick();
        perksCount = hp.getPerksList().size();
        assertEquals("Perks list must be empty", 0, perksCount);
    }

}
