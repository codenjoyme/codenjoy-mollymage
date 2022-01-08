package com.codenjoy.dojo.mollymage.model;

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
import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Poison;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.perks.HeroPerks;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.joystick.Act;
import com.codenjoy.dojo.services.joystick.RoundsDirectionActJoystick;
import com.codenjoy.dojo.services.printer.state.HeroState;
import com.codenjoy.dojo.services.printer.state.State;
import com.codenjoy.dojo.services.round.RoundPlayerHero;

import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.*;
import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;
import static com.codenjoy.dojo.mollymage.services.Event.*;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.printer.state.StateUtils.filterOne;

public class Hero extends RoundPlayerHero<Field>
        implements RoundsDirectionActJoystick, State<Element, Player>,
                   HeroState<Element, Hero, Player> {

    public static final int ACT_THROW_POISON = 1;
    public static final int ACT_EXPLODE_ALL_POTIONS = 2;

    private boolean potion;
    private Direction direction;
    private int score;
    private boolean throwPoison;
    private boolean explodeAllPotions;
    private int recharge;

    private HeroPerks perks = new HeroPerks();

    public Hero() {
        score = 0;
        direction = null;
        recharge = 0;
    }

    public Hero(Point pt) {
        this();
        move(pt);
    }

    @Override
    public void init(Field field) {
        super.init(field);

        field.heroes().add(this);
    }

    @Override
    public void change(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void act(Act act) {
        if (act.is(ACT_THROW_POISON)) {
            if (direction != null) {
                throwPoison = true;
            }
            return;
        }

        if (act.is(ACT_EXPLODE_ALL_POTIONS)) {
            explodeAllPotions = true;
            return;
        }

        if (direction != null) {
            potion = true;
        } else {
            setPotion(this);
        }
    }

    void dropPotion() {
        act();
    }

    void throwPoison() {
        act(ACT_THROW_POISON);
    }

    void throwPoison(Direction direction) {
        switch (direction) {
            case LEFT: left(); break;
            case RIGHT: right(); break;
            case UP: up(); break;
            case DOWN: down(); break;
        }
        throwPoison();
    }

    void explodeAllPotions() {
        act(ACT_EXPLODE_ALL_POTIONS);
    }

    @Override
    public void die() {
        super.die(HERO_DIED);
    }

    public void apply() {
        if (!isActiveAndAlive()) return;

        if (explodeAllPotions) {
            explodeAllPotionsOnField();
            explodeAllPotions = false;
        }

        if (direction == null) {
            return;
        }

        if (throwPoison) {
            throwPoisonAt(direction);

            throwPoison = false;
            direction = null;
            return;
        }

        Point pt = direction.change(this);

        if (!field.isBarrier(pt, FOR_HERO)) {
            move(pt);
            field.pickPerk(pt).forEach(perk -> {
                field.pickPerkBy((Player) this.getPlayer(), perk.getPerk());
                event(CATCH_PERK);
            });
        }
        direction = null;

        if (potion) {
            setPotion(this);
            potion = false;
        }
    }

    private void explodeAllPotionsOnField() {
        Perk perk = perks.getPerk(POTION_EXPLODER);

        if (perk == null || perk.getValue() <= 0) {
            return;
        }
        field.explodeAllPotions(this);
        perk.decrease();
    }

    private void throwPoisonAt(Direction direction) {
        Perk perk = perks.getPerk(POISON_THROWER);

        if (perk == null || recharge != 0) {
            return;
        }
        field.poisons().add(new Poison(this, direction, getBlastPower()));

        recharge += settings().integer(POISON_THROWER_RECHARGE);
    }

    private void setPotion(Point pt) {
        List<Potion> potions = field.heroPotions(this);

        Perk remotePerk = perks.getPerk(POTION_REMOTE_CONTROL);
        if (remotePerk != null) {
            // activate potions that were set on remote control previously
            if (tryActivateRemote(potions, this)) {
                remotePerk.decrease();
                return;
            }
        }

        Perk countPerk = perks.getPerk(POTION_COUNT_INCREASE);
        if (!сanDrop(countPerk, potions)) {
            return;
        }

        final int blastPower = getBlastPower();
        Potion potion = new Potion(this, pt, blastPower, field);

        if (remotePerk != null) {
            potion.putOnRemoteControl();
        }

        field.drop(potion);
    }

    private int getBlastPower() {
        Perk blastPerk = perks.getPerk(POTION_BLAST_RADIUS_INCREASE);
        int boost = (blastPerk == null) ? 0 : blastPerk.getValue();
        return settings().integer(POTION_POWER) + boost;
    }

    private boolean tryActivateRemote(List<Potion> potions, Hero hero) {
        boolean activated = false;
        for (Potion potion : potions) {
            if (potion.isOnRemote()) {
                potion.activateRemote(hero);
                activated = true;
            }
        }
        return activated;
    }

    private boolean сanDrop(Perk countPerk, List<Potion> potions) {
        // сколько зелья уже оставили?
        int placed = potions.size();
        // дополнение от перка, если он есть
        int boost = (countPerk == null) ? 0 : countPerk.getValue();

        // сколько я всего могу
        int allowed = settings().integer(POTIONS_COUNT) + boost;

        return placed < allowed;
    }

    private void rechargeTick() {
        recharge = Math.max(--recharge, 0);
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        return HeroState.super.state(player, alsoAtPoint);
    }

    @Override
    public Element beforeState(Object... alsoAtPoint) {
        if (!isActiveAndAlive()) {
            return HERO_DEAD;
        }

        Potion potion = filterOne(alsoAtPoint, Potion.class);
        if (potion != null) {
            return HERO_POTION;
        }

        return HERO;
    }

    @Override
    public Element middleState(Element state, List<Hero> heroes, Object[] alsoAtPoint) {
        // если в этой клетке есть хоть один живой, его надо отобразить как угрозу
        if (heroes.stream().anyMatch(Hero::isActiveAndAlive)) {
            return state == HERO_DEAD
                    ? null
                    : state;
        }

        // если в клеточке привидение, рисуем его как угрозу
        if (filterOne(alsoAtPoint, Ghost.class) != null) {
            return null;
        }

        // если привидения нет, следующий по опасности - зелье
        if (filterOne(alsoAtPoint, Potion.class) != null) {
            return null;
        }

        // и если опасности нет, тогда уже рисуем останки
        return HERO_DEAD;
    }

    @Override
    public void tickHero() {
        perks.tick();
        rechargeTick();
    }

    public List<Perk> getPerks() {
        return perks.getPerksList();
    }

    public void addPerk(Perk perk) {
        perks.add(perk);
    }

    public Perk getPerk(Element element) {
        return perks.getPerk(element);
    }

    @Override
    public int scores() {
        return score;
    }

    public void clearScores() {
        score = 0;
    }

    public void addScore(int added) {
        score = Math.max(0, score + added);
    }

    public int getTeamId() {
        return getPlayer().getTeamId();
    }

    public void fireKillHero(Hero prey) {
        if (isMyTeam(prey)) {
            event(KILL_OTHER_HERO);
        } else {
            event(KILL_ENEMY_HERO);
        }
    }
}
