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

import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Dice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static com.codenjoy.dojo.games.mollymage.Element.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class PerksSettingsWrapperTest {

    private final int percentage = 100;

    @Mock Dice dice;

    private static final Element NO_PERK = Element.OPENING_TREASURE_BOX;
    private PerksSettingsWrapper settings;

    @Before
    public void setup() {
        settings = new PerksSettingsWrapper(new TestGameSettings());
    }

    @Test
    public void dropPerk_allTogether_dropRatio20() {
        settings.dropRatio(20);

        settings.put(POTION_BLAST_RADIUS_INCREASE, 0, 0); // range 0..4
        settings.put(POTION_COUNT_INCREASE, 0, 0);        // range 5..9
        settings.put(POTION_REMOTE_CONTROL, 0, 0);        // range 10..14
        settings.put(POTION_IMMUNE, 0, 0);                // range 15..19

        assertPerk(0, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(1, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(2, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(3, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(4, POTION_BLAST_RADIUS_INCREASE);

        assertPerk(5, POTION_COUNT_INCREASE);
        assertPerk(6, POTION_COUNT_INCREASE);
        assertPerk(7, POTION_COUNT_INCREASE);
        assertPerk(8, POTION_COUNT_INCREASE);
        assertPerk(9, POTION_COUNT_INCREASE);

        assertPerk(10, POTION_REMOTE_CONTROL);
        assertPerk(11, POTION_REMOTE_CONTROL);
        assertPerk(12, POTION_REMOTE_CONTROL);
        assertPerk(13, POTION_REMOTE_CONTROL);
        assertPerk(14, POTION_REMOTE_CONTROL);

        assertPerk(15, POTION_IMMUNE);
        assertPerk(16, POTION_IMMUNE);
        assertPerk(17, POTION_IMMUNE);
        assertPerk(18, POTION_IMMUNE);
        assertPerk(19, POTION_IMMUNE);

        assertPerk(20, NO_PERK);
        assertPerk(21, NO_PERK);
        assertPerk(22, NO_PERK);
        assertPerk(23, NO_PERK);
        assertPerk(30, NO_PERK);
        assertPerk(40, NO_PERK);
        assertPerk(50, NO_PERK);
        assertPerk(60, NO_PERK);
        assertPerk(70, NO_PERK);
        assertPerk(80, NO_PERK);
        assertPerk(90, NO_PERK);
        assertPerk(100, NO_PERK);

        assertPerk(101, NO_PERK);
    }

    @Test
    public void dropPerk_twoPerks_dropRatio20() {
        settings.dropRatio(20);

        settings.put(POTION_BLAST_RADIUS_INCREASE, 0, 0); // range 0..9
        settings.put(POTION_REMOTE_CONTROL, 0, 0);        // range 10..19

        assertPerk(0, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(1, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(2, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(3, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(4, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(5, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(6, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(7, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(8, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(9, POTION_BLAST_RADIUS_INCREASE);

        assertPerk(10, POTION_REMOTE_CONTROL);
        assertPerk(11, POTION_REMOTE_CONTROL);
        assertPerk(12, POTION_REMOTE_CONTROL);
        assertPerk(13, POTION_REMOTE_CONTROL);
        assertPerk(14, POTION_REMOTE_CONTROL);
        assertPerk(15, POTION_REMOTE_CONTROL);
        assertPerk(16, POTION_REMOTE_CONTROL);
        assertPerk(17, POTION_REMOTE_CONTROL);
        assertPerk(18, POTION_REMOTE_CONTROL);
        assertPerk(19, POTION_REMOTE_CONTROL);

        assertPerk(20, NO_PERK);
        assertPerk(21, NO_PERK);
        assertPerk(22, NO_PERK);
        assertPerk(23, NO_PERK);
        assertPerk(30, NO_PERK);
        assertPerk(40, NO_PERK);
        assertPerk(50, NO_PERK);
        assertPerk(60, NO_PERK);
        assertPerk(70, NO_PERK);
        assertPerk(80, NO_PERK);
        assertPerk(90, NO_PERK);
        assertPerk(100, NO_PERK);

        assertPerk(101, NO_PERK);
    }

    @Test
    public void dropPerk_onePerks_dropRatio5() {
        settings.dropRatio(5);

        settings.put(POTION_BLAST_RADIUS_INCREASE, 0, 0); // range 0..4

        assertPerk(0, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(1, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(2, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(3, POTION_BLAST_RADIUS_INCREASE);
        assertPerk(4, POTION_BLAST_RADIUS_INCREASE);

        assertPerk(5, NO_PERK);
        assertPerk(6, NO_PERK);
        assertPerk(7, NO_PERK);
        assertPerk(8, NO_PERK);
        assertPerk(9, NO_PERK);

        assertPerk(10, NO_PERK);
        assertPerk(20, NO_PERK);
        assertPerk(21, NO_PERK);
        assertPerk(22, NO_PERK);
        assertPerk(23, NO_PERK);
        assertPerk(30, NO_PERK);
        assertPerk(40, NO_PERK);
        assertPerk(50, NO_PERK);
        assertPerk(60, NO_PERK);
        assertPerk(70, NO_PERK);
        assertPerk(80, NO_PERK);
        assertPerk(90, NO_PERK);
        assertPerk(100, NO_PERK);

        assertPerk(101, NO_PERK);
    }

    @Test
    public void dropPerk_threePerks_dropRatio35() {
        settings.dropRatio(30);

        settings.put(POTION_COUNT_INCREASE, 0, 0);        // range 0..9
        settings.put(POTION_REMOTE_CONTROL, 0, 0);        // range 10..19
        settings.put(POTION_IMMUNE, 0, 0);                // range 20..29

        assertPerk(0, POTION_COUNT_INCREASE);
        assertPerk(1, POTION_COUNT_INCREASE);
        assertPerk(2, POTION_COUNT_INCREASE);
        assertPerk(3, POTION_COUNT_INCREASE);
        assertPerk(4, POTION_COUNT_INCREASE);
        assertPerk(5, POTION_COUNT_INCREASE);
        assertPerk(6, POTION_COUNT_INCREASE);
        assertPerk(7, POTION_COUNT_INCREASE);
        assertPerk(8, POTION_COUNT_INCREASE);
        assertPerk(9, POTION_COUNT_INCREASE);

        assertPerk(10, POTION_REMOTE_CONTROL);
        assertPerk(11, POTION_REMOTE_CONTROL);
        assertPerk(12, POTION_REMOTE_CONTROL);
        assertPerk(13, POTION_REMOTE_CONTROL);
        assertPerk(14, POTION_REMOTE_CONTROL);
        assertPerk(15, POTION_REMOTE_CONTROL);
        assertPerk(16, POTION_REMOTE_CONTROL);
        assertPerk(17, POTION_REMOTE_CONTROL);
        assertPerk(18, POTION_REMOTE_CONTROL);
        assertPerk(19, POTION_REMOTE_CONTROL);

        assertPerk(20, POTION_IMMUNE);
        assertPerk(21, POTION_IMMUNE);
        assertPerk(22, POTION_IMMUNE);
        assertPerk(23, POTION_IMMUNE);
        assertPerk(24, POTION_IMMUNE);
        assertPerk(25, POTION_IMMUNE);
        assertPerk(26, POTION_IMMUNE);
        assertPerk(27, POTION_IMMUNE);
        assertPerk(28, POTION_IMMUNE);
        assertPerk(29, POTION_IMMUNE);

        assertPerk(30, NO_PERK);
        assertPerk(31, NO_PERK);
        assertPerk(32, NO_PERK);
        assertPerk(33, NO_PERK);
        assertPerk(40, NO_PERK);
        assertPerk(50, NO_PERK);
        assertPerk(60, NO_PERK);
        assertPerk(70, NO_PERK);
        assertPerk(80, NO_PERK);
        assertPerk(90, NO_PERK);
        assertPerk(100, NO_PERK);

        assertPerk(101, NO_PERK);
    }

    @Test
    public void dropPerk_noPerks_dropRatio50() {
        settings.dropRatio(50);

        assertPerk(0, NO_PERK);
        assertPerk(1, NO_PERK);
        assertPerk(2, NO_PERK);
        assertPerk(3, NO_PERK);
        assertPerk(4, NO_PERK);
        assertPerk(5, NO_PERK);
        assertPerk(6, NO_PERK);
        assertPerk(7, NO_PERK);
        assertPerk(8, NO_PERK);
        assertPerk(9, NO_PERK);

        assertPerk(10, NO_PERK);
        assertPerk(20, NO_PERK);
        assertPerk(30, NO_PERK);
        assertPerk(31, NO_PERK);
        assertPerk(32, NO_PERK);
        assertPerk(33, NO_PERK);
        assertPerk(40, NO_PERK);
        assertPerk(50, NO_PERK);
        assertPerk(60, NO_PERK);
        assertPerk(70, NO_PERK);
        assertPerk(80, NO_PERK);
        assertPerk(90, NO_PERK);
        assertPerk(100, NO_PERK);

        assertPerk(101, NO_PERK);
    }

    private void assertPerk(int random, Element perk) {
        assertEquals(perk, dice(random));
    }

    private Element dice(int value) {
        Mockito.when(dice.next(percentage)).thenReturn(value);
        return settings.nextPerkDrop(dice);
    }
}
