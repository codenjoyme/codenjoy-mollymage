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

public class PotionBlastRadiusIncrease extends Perk {

    public PotionBlastRadiusIncrease(int value, int timeout) {
        super(Element.POTION_BLAST_RADIUS_INCREASE, value, timeout);
    }

    /**
     * In case player grabs another such a perk while current one is active still,
     * timer will be increased and power value too.
     */
    @Override
    public Perk combine(Perk perk) {
        return new PotionBlastRadiusIncrease(
                this.getValue() + perk.getValue(),
                this.getTimer() + perk.getTimer());
    }
}