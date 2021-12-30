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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Poison;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.perks.HeroPerks;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.round.RoundPlayerHero;

import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.*;
import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;
import static com.codenjoy.dojo.mollymage.services.Event.*;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.StateUtils.filter;
import static com.codenjoy.dojo.services.StateUtils.filterOne;

public class Hero extends RoundPlayerHero<Field> implements State<Element, Player> {

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
    public void right() {
        if (!isActiveAndAlive()) return;

        direction = Direction.RIGHT;
    }

    @Override
    public void down() {
        if (!isActiveAndAlive()) return;

        direction = Direction.DOWN;
    }

    @Override
    public void up() {
        if (!isActiveAndAlive()) return;

        direction = Direction.UP;
    }

    @Override
    public void left() {
        if (!isActiveAndAlive()) return;

        direction = Direction.LEFT;
    }

    @Override
    public void act(int... p) {
        if (!isActiveAndAlive()) return;

        Act is = new Act(p);
        if (is.act(ACT_THROW_POISON)) {
            if (direction != null) {
                throwPoison = true;
            }
            return;
        }

        if (is.act(ACT_EXPLODE_ALL_POTIONS)) {
            explodeAllPotions = true;
            return;
        }

        if (direction != null) {
            potion = true;
        } else {
            setPotion(this);
        }
    }

    public void dropPotion() {
        act();
    }

    public void throwPoison() {
        act(ACT_THROW_POISON);
    }

    public void throwPoison(Direction direction) {
        switch (direction) {
            case LEFT: left(); break;
            case RIGHT: right(); break;
            case UP: up(); break;
            case DOWN: down(); break;
        }
        throwPoison();
    }

    public void explodeAllPotions() {
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
        field.addPoison(new Poison(this, direction, getBlastPower()));

        recharge += settings().integer(POISON_THROWER_RECHARGE);
    }

    private void setPotion(Point pt) {
        List<Potion> potions = field.potions(this);

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
        Potion potion = filterOne(alsoAtPoint, Potion.class);
        List<Hero> heroes = filter(alsoAtPoint, Hero.class);
        Ghost ghost = filterOne(alsoAtPoint, Ghost.class);

        // player наблюдатель содержится в той же клетке которую прорисовываем
        if (heroes.contains(player.getHero())) {
            // герой наблюдателя неактивен или его вынесли
            if (!player.getHero().isActiveAndAlive()) {
                return DEAD_HERO;
            }

            // герой наблюдателя жив и активен

            // под ним зелье
            if (potion != null) {
                return POTION_HERO;
            }

            return HERO;
        }

        // player наблюдает за клеткой в которой не находится сам

        // в клетке только трупики?
        if (heroes.stream().noneMatch(Hero::isActiveAndAlive)) {
            // если в клеточке с героем привидение, рисуем его
            if (ghost != null) {
                return ghost.state(player, alsoAtPoint);
            }

            // если привидения нет, следующий по опасности - зелье
            if (potion != null) {
                return potion.state(player, alsoAtPoint);
            }

            // и если опасности нет, тогда уже рисуем останки
            return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_DEAD_HERO : OTHER_DEAD_HERO;
        }

        // в клетке есть другие активные и живые герои

        // под ними зелье
        if (potion != null) {
            return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_POTION_HERO : OTHER_POTION_HERO;
        }

        return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_HERO : OTHER_HERO;
    }

    // TODO do we use only settings.isTeamDeathMatch() here?
    private boolean anyHeroFromAnotherTeam(Player player, List<Hero> heroes) {
        return heroes.stream()
                .anyMatch(hero -> player.getTeamId() != hero.getPlayer().getTeamId());
    }

    @Override
    public void tick() {
        // TODO добавить проверку if (!isActiveAndAlive()) return;
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
        if (getTeamId() == prey.getTeamId()) {
            event(KILL_OTHER_HERO);
        } else {
            event(KILL_ENEMY_HERO);
        }
    }
}

