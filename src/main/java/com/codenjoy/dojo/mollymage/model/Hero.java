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


import com.codenjoy.dojo.mollymage.model.perks.HeroPerks;
import com.codenjoy.dojo.mollymage.model.perks.Perk;
import com.codenjoy.dojo.mollymage.model.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.round.RoundPlayerHero;

import java.util.List;

import static com.codenjoy.dojo.games.mollymage.Element.*;
import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;
import static com.codenjoy.dojo.services.StateUtils.filter;
import static com.codenjoy.dojo.services.StateUtils.filterOne;

public class Hero extends RoundPlayerHero<Field> implements State<Element, Player> {

    private Level level;
    private boolean potion;
    private Direction direction;
    private int score;

    private HeroPerks perks = new HeroPerks();

    public Hero(Level level) {
        this.level = level;
        score = 0;
        direction = null;
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

        if (direction != null) {
            potion = true;
        } else {
            setPotion(x, y);
        }
    }

    public void apply() {
        if (!isActiveAndAlive()) return;

        if (direction == null) {
            return;
        }

        Point pt = direction.change(this);

        if (!field.isBarrier(pt, FOR_HERO)) {
            move(pt);
            PerkOnBoard perk = field.pickPerk(pt);
            if (perk != null) {
                addPerk(perk.getPerk());
                event(Events.CATCH_PERK);
            }
        }
        direction = null;

        if (potion) {
            setPotion(x, y);
            potion = false;
        }
    }

    private void setPotion(int x, int y) {
        List<Potion> potions = field.potions(this);

        Perk remotePerk = perks.getPerk(POTION_REMOTE_CONTROL);
        if (remotePerk != null) {
            // activate potions that were set on remote control previously
            if (tryActivateRemote(potions)) {
                remotePerk.decrease();
                return;
            }
        }

        Perk countPerk = perks.getPerk(POTION_COUNT_INCREASE);
        if (!сanDrop(countPerk, potions)) {
            return;
        }

        Perk blastPerk = perks.getPerk(POTION_BLAST_RADIUS_INCREASE);
        int boost = (blastPerk == null) ? 0 : blastPerk.getValue();
        Potion potion = new Potion(this, x, y, level.potionsPower() + boost, field);

        if (remotePerk != null) {
            potion.putOnRemoteControl();
        }

        field.drop(potion);
    }

    private boolean tryActivateRemote(List<Potion> potions) {
        boolean activated = false;
        for (Potion potion : potions) {
            if (potion.isOnRemote()) {
                potion.activateRemote();
                activated = true;
            }
        }
        return activated;
    }

    private boolean сanDrop(Perk countPerk, List<Potion> potions) {
        // сколько бомб уже оставили?
        int placed = potions.size();
        // дополнение от перка, если он есть
        int boost = (countPerk == null) ? 0 : countPerk.getValue();

        // сколько я всего могу
        int allowed = level.potionsCount() + boost;

        return placed < allowed;
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

            // под ним бомба
            if (potion != null) {
                return POTION_HERO;
            }

            return HERO;
        }

        // player наблюдает за клеткой в которой не находится сам

        // в клетке только трупики?
        if (heroes.stream().noneMatch(Hero::isActiveAndAlive)) {
            // если в клеточке с героем митчопер, рисуем его
            if (ghost != null) {
                return ghost.state(player, alsoAtPoint);
            }

            // если митчопера нет, следующий по опасности - бобма
            if (potion != null) {
                return potion.state(player, alsoAtPoint);
            }

            // и если опасности нет, тогда уже рисуем останки
            return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_DEAD_HERO : OTHER_DEAD_HERO;
        }

        // в клетке есть другие активные и живые герои

        // под ними бомба
        if (potion != null) {
            return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_POTION_HERO : OTHER_POTION_HERO;
        }

        return anyHeroFromAnotherTeam(player, heroes) ? ENEMY_HERO : OTHER_HERO;
    }

    private boolean anyHeroFromAnotherTeam(Player player, List<Hero> heroes) {
        return heroes.stream()
                .anyMatch(h -> player.getTeamId() != h.getPlayer().getTeamId());
    }

    @Override
    public void tick() {
        // TODO добавить проверку if (!isActiveAndAlive()) return;
        perks.tick();
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

    public int scores() {
        return score;
    }

    public void clearScores() {
        score = 0;
    }

    public void addScore(int added) {
        score = Math.max(0, score + added);
    }
}

