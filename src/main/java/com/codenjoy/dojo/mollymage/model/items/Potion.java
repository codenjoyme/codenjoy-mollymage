package com.codenjoy.dojo.mollymage.model.items;

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
import com.codenjoy.dojo.mollymage.model.Field;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.STEAL_POINTS;

public class Potion extends PointImpl implements Tickable, State<Element, Player> {

    protected int timer = 5;
    protected int power;
    private final Hero owner;
    private Set<Hero> interceptors;
    private final Field field;
    private boolean onRemote = false;

    public Potion(Hero owner, Point pt, int power, Field field) {
        super(pt);
        this.power = power;
        this.owner = owner;
        this.field = field;
        interceptors = new LinkedHashSet<>();
    }

    public void tick() {
        if (!onRemote) {
            timer--;
        }

        if (timer == 0) {
            boom();
        }
    }

    public List<Hero> getOwners() {
        List<Hero> results = new ArrayList<>();
        if (interceptors == null || interceptors.size() == 0) {
            results.add(owner);
        } else {
            results.addAll(interceptors);
        }
        return results;
    }

    public void intercept(Hero hero) {
        addBlastOwner(hero);
        putOnRemoteControl();
    }

    private void addBlastOwner(Hero hero) {
        interceptors.add(hero);
    }

    public void boom() {
        if (isOwnerGetScoresFromHisPotion()) {
            addBlastOwner(owner);
        }
        field.remove(this);
    }

    private boolean isOwnerGetScoresFromHisPotion() {
        return !field.settings().bool(STEAL_POINTS);
    }

    public int getPower() {
        return power;
    }

    public boolean isExploded() {
        return timer == 0;
    }

    public boolean itsMine(Hero hero) {
        return getOwner() == hero;
    }

    public Hero getOwner() {
        return owner;
    }

    public void putOnRemoteControl() {
        this.onRemote = true;
    }

    public void activateRemote(Hero hero) {
        intercept(hero);
        this.timer = 0;
    }

    public boolean isOnRemote() {
        return onRemote;
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        switch (timer) {
            case 1:
                return Element.POTION_TIMER_1;
            case 2:
                return Element.POTION_TIMER_2;
            case 3:
                return Element.POTION_TIMER_3;
            case 4:
                return Element.POTION_TIMER_4;
            case 5:
                return Element.POTION_TIMER_5;
            default:
                return Element.BLAST;
        }
    }
}
