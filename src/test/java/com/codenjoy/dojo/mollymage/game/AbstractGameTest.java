package com.codenjoy.dojo.mollymage.game;

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

import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.MollyMage;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.mollymage.model.levels.LevelImpl;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public abstract class AbstractGameTest {

    public static final int DEFAULT_COUNT = 3;

    public static final int CATCH_PERK_SCORE_FOR_TEST = 10;
    public static final int PERK_TIMEOUT_FOR_TEST = 10;

    protected List<EventListener> listeners;
    protected List<Player> players;
    protected List<Game> games;
    protected List<Hero> heroes;

    protected GameSettings settings;
    protected Dice dice = mock(Dice.class);
    protected MollyMage field;
    private PrinterFactory printer;
    protected PerksSettingsWrapper perks;
    protected EventsListenersAssert events;
    protected LevelImpl level;

    @Before
    public void setup() {
        listeners = new LinkedList<>();
        players = new LinkedList<>();
        games = new LinkedList<>();
        heroes = new ArrayList<>();

        dice = mock(Dice.class);
        settings = settings();
        perks = settings.perksSettings();
        printer = new PrinterFactoryImpl();
        events = new EventsListenersAssert(() -> listeners, Events.class);
    }

    public void dice(int... ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    protected void givenFl(String map) {
        settings.string(LEVEL_MAP, map);
        level = (LevelImpl) settings.level();

        field = new MollyMage(level, dice, settings);
        level.heroes().forEach(hero -> givenPlayer(hero));

        boxesCount(field.boxes().size());
        ghostsCount(field.ghosts().size());
        stopGhosts(); // по умолчанию все привидения стоят на месте
    }

    public Player givenPlayer(Point pt) {
        EventListener listener = mock(EventListener.class);
        listeners.add(listener);
        Player player = new Player(listener, settings);
        players.add(player);
        Game game = new Single(player, printer);
        games.add(game);

        dice(pt.getX(), pt.getY());
        game.on(field);
        game.newGame();
        return player;
    }

    protected GameSettings settings() {
        return spy(new TestGameSettings())
                .integer(POTION_POWER, 1)
                .integer(TREASURE_BOX_COUNT, 0)
                .integer(GHOSTS_COUNT, 0);
    }

    protected void tick() {
        field.tick();
    }

    public void assertF(String expected, int index) {
        assertEquals(expected, game(index).getBoardAsString());
    }

    public Game game(int index) {
        return games.get(index);
    }

    public Player player(int index) {
        return players.get(index);
    }

    public Hero hero(int index) {
        return (Hero) game(index).getPlayer().getHero();
    }

    // getters, if only one player

    public void assertF(String expected) {
        assertF(expected, 0);
    }

    public Game game() {
        return game(0);
    }

    public Player player() {
        return player(0);
    }

    public Hero hero() {
        return hero(0);
    }


    protected EventListener listener() {
        return listeners.get(0);
    }

    protected EventListener listener(int index) {
        return listeners.get(index);
    }

    // other stuff

    protected void gotoMaxUp() {
        for (int y = 0; y <= level.size() + 1; y++) {
            hero().up();
            field.tick();
        }
    }

    protected void gotoMaxRight() {
        for (int x = 0; x <= level.size() + 1; x++) {
            hero().right();
            field.tick();
        }
    }

    protected GameSettings potionsCount(int count) {
        return settings.integer(POTIONS_COUNT, count);
    }

    protected void assertHeroDie() {
        assertEquals("Expected game over", true, game().isGameOver());
    }

    protected void assertHeroAlive() {
        assertFalse(game().isGameOver());
    }

    protected void gotoBoardCenter() {
        for (int y = 0; y < level.size() / 2; y++) {
            hero().up();
            field.tick();
            hero().right();
            field.tick();
        }
    }

    protected void assertBoards(String expected, Integer... indexes) {
        events.assertAll(expected, games.size(), indexes, index -> {
            Object actual = game(index).getBoardAsString();
            return String.format("game(%s)\n%s\n", index, actual);
        });
    }

    protected void newGameForAllDied() {
        players.forEach(player -> {
            if (!player.isAlive()) {
                field.newGame(player(players.indexOf(player)));
            }
        });
    }

    protected GameSettings potionsPower(int power) {
        return settings.integer(POTION_POWER, power);
    }

    protected void assertPotionPower(int power, String expected) {
        potionsPower(power);

        hero().act();
        goOut();
        field.tick();

        assertF(expected);
    }

    protected void goOut() {
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        hero().up();
        field.tick();
        hero().up();
        field.tick();
    }

    protected void newBox(int x, int y) {
        field.boxes().add(new TreasureBox(x, y));
    }

    protected void newGhost(int x, int y) {
        Ghost ghost = new Ghost(pt(x, y));
        ghost.init(field);
        ghostsCount(ghostsCount() + 1);
        field.ghosts().add(ghost);
    }

    protected int boxesCount() {
        return settings.integer(TREASURE_BOX_COUNT);
    }

    protected void boxesCount(int count) {
        settings.integer(TREASURE_BOX_COUNT, count);
    }

    protected int ghostsCount() {
        return settings.integer(GHOSTS_COUNT);
    }

    protected void ghostsCount(int count) {
        settings.integer(GHOSTS_COUNT, count);
    }

    protected Ghost ghost(int x, int y) {
        return field.ghosts().getAt(pt(x, y)).get(0);
    }

    protected void newPerk(int x, int y, Perk perk) {
        field.perks().add(new PerkOnBoard(new PointImpl(x, y), perk));
    }

    protected int[] getCoordinatesForPointsInSquare(int size) {
        List<Integer> result = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                result.add(y);
                result.add(x);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    protected void resetListeners() {
        listeners.forEach(Mockito::reset);
    }

    protected void stopGhosts() {
        field.ghosts().forEach(Ghost::stop);
    }
}
