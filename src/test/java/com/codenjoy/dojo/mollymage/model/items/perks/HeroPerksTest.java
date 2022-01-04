package com.codenjoy.dojo.mollymage.model.items.perks;

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

import com.codenjoy.dojo.games.mollymage.Element;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class HeroPerksTest {

    @Test
    public void shouldIncreaseTimer_whenAddPotionBlastIncreaseTwice() {
        // given
        HeroPerks perk = new HeroPerks();
        PotionBlastRadiusIncrease bip = new PotionBlastRadiusIncrease(2, 5);

        // when
        perk.add(bip);
        perk.tick();
        perk.tick();

        // then
        assertEquals("Perk timer is expected to be countdown",
                3, perk.getPerk(Element.POTION_BLAST_RADIUS_INCREASE).getTimer());

        // when
        perk.add(bip);

        // then
        assertEquals("Perk timer is expected to be increased",
                6, perk.getPerk(Element.POTION_BLAST_RADIUS_INCREASE).getTimer());
    }

    @Test
    public void shouldRemovePerk_whenExpiration() {
        // given
        HeroPerks perk = new HeroPerks();
        PotionBlastRadiusIncrease bip = new PotionBlastRadiusIncrease(2, 2);

        // when
        perk.add(bip);
        perk.tick();

        // then
        assertEquals("Perks list must not be empty",
                1, perk.getPerksList().size());

        // when
        perk.tick();

        // then
        assertEquals("Perks list must be empty",
                0, perk.getPerksList().size());
    }

    @Test
    public void shouldNotModifyPerks_afterClone() {
        // given
        HeroPerks perk = new HeroPerks();
        PotionBlastRadiusIncrease bip = new PotionBlastRadiusIncrease(2, 2);
        perk.add(bip);

        // when
        List<Perk> list = perk.getPerksList();

        // then
        assertEquals(1, list.size());

        // when
        list.clear();

        // then
        assertEquals("Must not be mutated", 1, perk.getPerksList().size());
    }

}
