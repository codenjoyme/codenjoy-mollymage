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
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.blast.BoomEngineOriginal;
import com.codenjoy.dojo.mollymage.model.items.blast.Poison;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBoxes;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.ghost.GhostHunter;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghosts;
import com.codenjoy.dojo.mollymage.model.items.perks.*;
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.BoardUtils;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.round.RoundField;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.BIG_BADABOOM;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.PERK_WHOLE_TEAM_GET;
import static java.util.stream.Collectors.toList;

public class MollyMage extends RoundField<Player> implements Field {

    private PointField field;
    private List<Player> players;
    private Dice dice;
    private GameSettings settings;

    private TreasureBoxes boxes;
    private Ghosts ghosts;
    private List<Point> destroyedObjects = new LinkedList<>();
    private List<Point> previousTickDestroyedObjects = new LinkedList<>();
    private List<Potion> destroyedPotions = new LinkedList<>();

    public MollyMage(Level level, Dice dice, GameSettings settings) {
        super(Events.START_ROUND, Events.WIN_ROUND, Events.DIED, settings);

        field = level.field();
        players = new LinkedList<>();
        this.dice = dice;
        this.settings = settings;

        ghosts = new Ghosts(settings, dice);
        ghosts.init(this);

        boxes = new TreasureBoxes(settings, dice);
        boxes.init(this);
    }

    @Override
    public void remove(Player player) {
        super.remove(player);
        removeAloneHeroes();
    }

    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
        removeAloneHeroes();
    }

    // TODO DF3D попробовать избавиться от этого метода
    private void removeAloneHeroes() {
        heroes().removeNotSame(players.stream().
                map(GamePlayer::getHero)
                .collect(toList()));
    }

    @Override
    protected List<Player> players() {
        return players;
    }

    public List<PerkOnBoard> pickPerk(Point pt) {
        List<PerkOnBoard> result = perks().getAt(pt);
        if (result.isEmpty()) {
            return result;
        }

        // TODO написать тест на случай подбирания двух перков подряд если они лежат в окдной клетке
        PerkOnBoard next = result.iterator().next();
        perks().remove(next);
        return Arrays.asList(next);
    }

    @Override
    public void addPerk(Player player, Perk perk) {
        if (isWholeTeamShouldGetPerk()) {
            addPerk(player.getTeamId(), perk);
        } else {
            player.getHero().addPerk(perk);
        }
    }

    @Override
    public void addPerk(int teamId, Perk perk) {
        for (Player player : players) {
            if (player.getTeamId() == teamId) {
                player.getHero().addPerk(perk);
            }
        }
    }

    private boolean isWholeTeamShouldGetPerk() {
        return settings.isTeamDeathMatch()
                && settings.bool(PERK_WHOLE_TEAM_GET);
    }

    @Override
    public Dice dice() {
        return dice;
    }

    @Override
    public Optional<Point> freeRandom(Player player) {
        return BoardUtils.freeRandom(size(), dice, pt -> isFree(pt));
    }

    @Override
    public boolean isFree(Point pt) {
        return !isBarrier(pt, !FOR_HERO);
    }

    @Override
    public int size() {
        return field.size();
    }

    @Override
    public void cleanStuff() {
        removeBlasts();
    }

    @Override
    protected void setNewObjects() {
        // do nothing
    }

    @Override
    public void tickField() {
        applyAllHeroes();       // герои ходят
        ghostEatHeroes();       // омномном
        boxes.tick();           // сундуки появляются
        ghosts.tick();          // привидения водят свой хоровод
        hunters().tick();       // охотники охотятся
        ghostEatHeroes();       // омномном
        disablePotionRemote();  // если остались remote зелья без хозяев, взрываем
        makeBlastsFromPoisonThrower();  //  heroes throws poison
        tactAllPotions();       // все что касается зелья и взрывов
        tactAllPerks();         // тикаем перки на поле
        tactAllHeroes();        // в том числе и перки героев
    }

    private void disablePotionRemote() {
        for (Potion potion : potions()) {
            Hero owner = potion.getOwner();
            if (!owner.isActiveAndAlive()) {
                if (potion.isOnRemote()) {
                    potion.activateRemote(owner);
                    owner.getPerk(Element.POTION_REMOTE_CONTROL).decrease();
                }
            }
        }
    }

    private void tactAllPerks() {
        // тикаем счетчик перка на поле и если просрочка, удаляем
        perks().forEach(PerkOnBoard::tick);
        List<PerkOnBoard> alive = perks().stream()
                .filter(perk -> perk.getPerk().getPick() > 0)
                .collect(toList());
        perks().clear();
        perks().addAll(alive);
    }

    private void tactAllHeroes() {
        for (Player p : players) {
            p.getHero().tick();
        }
    }

    private void applyAllHeroes() {
        for (Player player : players) {
            player.getHero().apply();
        }
    }

    private void removeBlasts() {
        blasts().clear();

        for (Point pt : destroyedObjects) {
            if (pt instanceof TreasureBox) {
                boxes().remove(pt);
                dropPerk(pt, dice);
            } else if (pt instanceof GhostHunter) {
                hunters().remove(pt);
            } else if (pt instanceof Ghost) {
                ghosts().remove(pt);
            }
        }

        cleanDestroyedObjects();
    }

    private void cleanDestroyedObjects() {
        previousTickDestroyedObjects.clear();
        previousTickDestroyedObjects.addAll(destroyedObjects);
        destroyedObjects.clear();
        toxins().clear();
    }

    private void ghostEatHeroes() {
        ghosts().forEach(ghost -> eatBy(ghost));
        hunters().forEach(hunter -> eatBy(hunter));
    }

    private void eatBy(Ghost ghost) {
        for (Player player : players) {
            Hero hero = player.getHero();
            if (hero.isAlive() && ghost.itsMe(hero)) {
                player.getHero().die();
            }
        }
    }

    private void tactAllPotions() {
        potions().tick();

        do {
            makeBlastsFromDestroyedPotions();

            if (settings.bool(BIG_BADABOOM)) {
                // если зелье зацепила взрывная волна и его тоже подрываем
                for (Potion potion : potions()) {
                    if (blasts().contains(potion)) {
                        potion.boom();
                    }
                }
            }

            // и повторяем все, пока были взорванные зелья
        } while (!destroyedPotions.isEmpty());

        // потому уже считаем скоры за разрушения
        blastKillAllNear();

        // убираем взрывную волну над обнаженными перками, тут взрыв сделал свое дело
        blasts().removeIn(perks().all());

    }

    private void makeBlastsFromDestroyedPotions() {
        // все взрываем, чтобы было пекло
        for (Potion potion : destroyedPotions) {
            potions().remove(potion);

            List<Blast> blast = makeBlast(potion);
            blasts().addAll(blast);
        }
        destroyedPotions.clear();
    }

    private void makeBlastsFromPoisonThrower() {
        for (Poison poison : toxins()) {
            List<Blast> blast = makeBlast(poison);
            blasts().addAll(blast);
        }

        toxins().clear();
    }

    @Override
    public List<Potion> potions(Hero hero) {
        return potions().stream()
                .filter(potion -> potion.itsMine(hero))
                .collect(toList());
    }

    @Override
    public void drop(Potion potion) {
        if (!existAtPlace(potion.getX(), potion.getY())) {
            potions().add(potion);
        }
    }

    @Override
    public void remove(Potion potion) {
        destroyedPotions.add(potion);
    }

    @Override
    public void remove(Point pt) {
        destroyedObjects.add(pt);
    }

    private List<Blast> makeBlast(Poison poison) {
        List barriers = getBarriersForBlast();

        return new BoomEngineOriginal(poison.getOwner())
                .boom(barriers, size(), poison);
    }

    private List<Point> getBarriersForBlast() {
        List<Point> result = new LinkedList<>();
        result.addAll(walls().all());
        result.addAll(ghosts().all());
        result.addAll(hunters().all());
        result.addAll(boxes().all());
        result.addAll(heroes().stream()
                .filter(Hero::isActiveAndAlive)
                .collect(toList()));
        return result;
    }

    private List<Blast> makeBlast(Potion potion) {
        List<Point> barriers = getBarriersForBlast();

        // TODO move potion inside BoomEngine
        List<Blast> result = new ArrayList<>();
        for (Hero owner : potion.getOwners()) {
            result.addAll(new BoomEngineOriginal(owner)
                    .boom(barriers, size(), potion, potion.getPower()));
        }
        return result;
    }

    private void blastKillAllNear() {
        blastKillHeroes();
        blastKillPerks();
        blastKillBoxesAndGhosts();
    }

    private void blastKillBoxesAndGhosts() {
        // собираем все разрушаемые стенки которые уже есть в радиусе
        // надо определить кто кого чем кикнул (ызрывные волны могут пересекаться)
        List<Point> all = new LinkedList<>();
        all.addAll(boxes().all());
        all.addAll(ghosts().all());
        all.addAll(hunters().all());

        Multimap<Hero, Point> deathMatch = LinkedHashMultimap.create();
        for (Blast blast : blasts()) {
            Hero hunter = blast.owner();
            int index = all.indexOf(blast);
            if (index != -1) {
                Point object = all.get(index);
                deathMatch.put(hunter, object);
            }
        }

        // у нас есть два списка, прибитые стенки
        // и те, благодаря кому они разрушены
        Set<Point> preys = new HashSet<>(deathMatch.values());
        Set<Hero> hunters = new HashSet<>(deathMatch.keys());

        // вначале прибиваем объекты
        preys.forEach(object -> {
            if (object instanceof GhostHunter) {
                ((GhostHunter) object).die();
            } else {
                remove(object);
            }
        });

        // а потом все виновники получают свои ачивки
        hunters.forEach(hunter -> {
            if (!hunter.hasPlayer()) {
                return;
            }

            deathMatch.get(hunter).forEach(object -> {
                if (object instanceof Ghost) {
                    hunter.event(Events.KILL_GHOST);
                } else if (object instanceof TreasureBox) {
                    hunter.event(Events.KILL_TREASURE_BOX);
                }
            });
        });
    }

    private void blastKillPerks() {
        // собираем все перки которые уже есть в радиусе
        // надо определить кто кого чем кикнул (ызрывные волны могут пересекаться)
        Multimap<Hero, PerkOnBoard> deathMatch = HashMultimap.create();
        for (Blast blast : blasts()) {
            Hero hunter = blast.owner();
            perks().getAt(blast)
                    .forEach(perk -> deathMatch.put(hunter, perk));
        }

        // у нас есть два списка, прибитые перки
        // и те, благодаря кому
        Set<PerkOnBoard> preys = new HashSet<>(deathMatch.values());
        Set<Hero> hunters = new HashSet<>(deathMatch.keys());

        // вначале прибиваем перки
        preys.forEach(perk -> pickPerk(perk));

        // а потом все виновники получают свои результаты )
        hunters.forEach(hunter -> {
            if (!hunter.hasPlayer()) {
                return;
            }

            deathMatch.get(hunter).forEach(perk -> {
                hunter.event(Events.DROP_PERK);

                // TODO может это делать на этапе, когда balsts развиднеется в removeBlasts
                blasts().remove(perk);
                hunters().add(new GhostHunter(perk, this, hunter));
            });
        });
    }

    private void blastKillHeroes() {
        // беремся за героев, если у них только нет иммунитета
        // надо определить кто кого чем кикнул (ызрывные волны могут пересекаться)
        Multimap<Hero, Hero> deathMatch = HashMultimap.create();
        for (Blast blast : blasts()) {
            Hero hunter = blast.owner();
            for (Player player : aliveActive()) {
                Hero prey = player.getHero();
                if (prey.itsMe(blast)) {
                    Perk immune = prey.getPerk(Element.POTION_IMMUNE);
                    if (immune == null) {
                        deathMatch.put(hunter, prey);
                    }
                }
            }
        }

        // у нас есть два списка, те кого прибили
        // и те, благодаря кому
        Set<Hero> preys = new HashSet<>(deathMatch.values());
        Set<Hero> hunters = new HashSet<>(deathMatch.keys());

        // вначале прибиваем жертв
        preys.forEach(hero -> {
            if (!hero.hasPlayer()) {
                return;
            }

            hero.die();
        });

        // а потом все, кто выжил получают за это очки за всех тех, кого зацепили взрывной волной
        // не стоит беспокоиться что они погибли сами - за это есть регулируемые штрафные очки
        for (Hero hunter : hunters) {
            if (!hunter.hasPlayer()) {
                continue;
            }
            for (Hero prey : deathMatch.get(hunter)) {
                if (hunter != prey) {
                    if (hunter.getPlayer().getTeamId() != prey.getPlayer().getTeamId()) {
                        hunter.event(Events.KILL_ENEMY_HERO);
                    } else {
                        hunter.event(Events.KILL_OTHER_HERO);
                    }
                }
            }
        }
    }

    private boolean dropPerk(Point pt, Dice dice) {
        Element element = settings.perksSettings().nextPerkDrop(dice);
        PerkSettings perk = settings.perksSettings().get(element);

        switch (element) {
            case POTION_BLAST_RADIUS_INCREASE:
                setup(pt, new PotionBlastRadiusIncrease(perk.value(), perk.timeout()));
                return true;

            case POTION_COUNT_INCREASE:
                setup(pt, new PotionCountIncrease(perk.value(), perk.timeout()));
                return true;

            case POTION_IMMUNE:
                setup(pt, new PotionImmune(perk.timeout()));
                return true;

            case POTION_REMOTE_CONTROL:
                setup(pt, new PotionRemoteControl(perk.value(), perk.timeout()));
                return true;

            case POISON_THROWER:
                setup(pt, new PoisonThrower(perk.timeout()));
                return true;

            case POTION_EXPLODER:
                setup(pt, new PotionExploder(perk.value(), perk.timeout()));
                return true;

            default:
                return false;
        }
    }

    private void setup(Point pt, Perk perk) {
        perk.setPick(settings.perksSettings().pickTimeout());
        perks().add(new PerkOnBoard(pt, perk));
    }

    private boolean existAtPlace(int x, int y) {
        for (Potion potion : potions()) {
            if (potion.getX() == x && potion.getY() == y) {
                return true;
            }
        }
        return false;
    }

    // препятствие это все, чем может быть занята клеточка
    // но если мы для героя смотрим - он может пойти к чоперу и на перк
    @Override
    public boolean isBarrier(Point pt, boolean isForHero) {
        List<Player> players = isForHero ? aliveActive() : players();

        // мы дергаем этот метод когда еще герой ищет себе место, потому тут надо скипнуть все недоинициализированные плеера
        players = players.stream()
                .filter(p -> p.getHero() != null)
                .collect(toList());

        for (Player player : players) {
            if (player.getHero().itsMe(pt)) {
                return true;
            }
        }

        if (potions().contains(pt)) {
            return true;
        }

        // TODO test me привидение или стена не могут появиться на перке
        if (!isForHero) {
            if (perks().contains(pt)) {
                return true;
            }
        }

        // TODO: test me
        if (walls().contains(pt)) {
            return true;
        }

        // TODO: test me
        if (boxes().contains(pt)) {
            return true;
        }

        // TODO test me стенка или другой чопер не могут появиться на чопере
        // TODO но герой может пойти к нему на встречу
        if (!isForHero) {
            if (ghosts().contains(pt)) {
                return true;
            }
            if (hunters().contains(pt)) {
                return true;
            }
        }

        //  ban on the creation of new elements on the places just destroyed objects
        if (!isForHero) {
            if (previousTickDestroyedObjects.contains(pt)) {
                return true;
            }
        }

        return pt.isOutOf(size());
    }

    public List<Hero> heroes(boolean activeAliveOnly) {
        return players.stream()
                .map(Player::getHero)
                .filter(hero -> !activeAliveOnly || hero.isActiveAndAlive())
                .collect(toList());
    }

    public BoardReader reader() {
        return field.reader(
                Hero.class,
                TreasureBox.class,
                Ghost.class,
                GhostHunter.class,
                Wall.class,
                Potion.class,
                Blast.class,
                PerkOnBoard.class);
    }

    @Override
    public GameSettings settings() {
        return settings;
    }

    @Override
    public void addPoison(Poison poison) {
        toxins().add(poison);
    }

    @Override
    public void explodeAllPotions(Hero hero) {
        for (Potion potion : potions()) {
            potion.intercept(hero);
            potion.activateRemote(hero);
        }
    }

    @Override
    public PointField.Accessor<Blast> blasts() {
        return field.of(Blast.class);
    }

    @Override
    public PointField.Accessor<Wall> walls() {
        return field.of(Wall.class);
    }

    @Override
    public PointField.Accessor<Poison> toxins() {
        return field.of(Poison.class);
    }

    @Override
    public PointField.Accessor<Potion> potions() {
        return field.of(Potion.class);
    }

    @Override
    public PointField.Accessor<Ghost> ghosts() {
        return field.of(Ghost.class);
    }

    @Override
    public PointField.Accessor<GhostHunter> hunters() {
        return field.of(GhostHunter.class);
    }

    @Override
    public PointField.Accessor<PerkOnBoard> perks() {
        return field.of(PerkOnBoard.class);
    }

    @Override
    public PointField.Accessor<TreasureBox> boxes() {
        return field.of(TreasureBox.class);
    }

    @Override
    public PointField.Accessor<Hero> heroes() {
        return field.of(Hero.class);
    }

}
