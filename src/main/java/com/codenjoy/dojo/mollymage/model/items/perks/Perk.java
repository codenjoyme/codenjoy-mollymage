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
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

public abstract class Perk extends PointImpl implements Tickable, State<Element, Player> {

    private final String name;
    private final Element element;
    private int value;

    /**
     * Maximum timer value
     * */
    private int timeout;

    /**
     * Countdown with every tick.
     * When timer becomes = 0, then perk should be disabled
     * */
    private int timer;

    /**
     * After timeout disappears from the board if wasn't picked up.
     * */
    private int pick;

    public Perk(Element element, int value, int timeout) {
        this.element = element;
        this.name = element.name();
        this.value = value;
        this.timeout = timeout;
        this.timer = timeout;
    }

    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public boolean isActive() {
        return timer > 0;
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        return isActive() ? this.element : Element.NONE;
    }

    @Override
    public void tick() {
        timer--;
    }

    /**
     * Perk implementation must resolve the situation when player already has this kind of perk.
     * E.g. strategy can be to reset timer or increase effect power etc.
     * Though, one can implement more complex situations, like combo: time + power etc.
     *
     * @param perk to combine with.
     * @return resulting perk.
     */
    public abstract Perk combine(Perk perk);

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getTimer() {
        return timer;
    }

    @Override
    public String toString() {
        return String.format("{%s('%s')\n  value=%s, timeout=%s, timer=%s, pick=%s}",
                name, element, value, timeout, timer, pick);
    }

    public void tickPick() {
        pick--;
    }

    public void decrease() {
        value--;
        if (value <= 0) {
            timer = 0;
            timeout = 0;
        }
    }
}
