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

import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.Level;
import com.codenjoy.dojo.mollymage.model.MollyMage;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import org.junit.After;
import org.junit.Before;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public abstract class AbstractGameTest {

    private List<EventListener> listeners;
    private List<Player> players;
    private List<Game> games;

    private Dice dice;
    private PrinterFactory<Element, Player> printer;
    protected MollyMage field;
    protected GameSettings settings;
    protected PerksSettingsWrapper perks;
    protected EventsListenersAssert events;

    @Before
    public void setup() {
        listeners = new LinkedList<>();
        players = new LinkedList<>();
        games = new LinkedList<>();

        dice = mock(Dice.class);
        settings = settings();
        perks = settings.perksSettings();
        printer = new PrinterFactoryImpl<>();
        events = new EventsListenersAssert(() -> listeners, Events.class);
    }

    @After
    public void tearDown() {
        events.verifyAllEvents("");
    }

    public void dice(int... ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    public void givenFl(String... maps) {
        int levelNumber = LevelProgress.levelsStartsFrom1;
        settings.setLevelMaps(levelNumber, maps);
        Level level = settings.level(levelNumber, dice);

        field = new MollyMage(dice, level, settings);
        level.heroes().forEach(this::givenPlayer);

        settings.integer(TREASURE_BOX_COUNT, field.boxes().size())
                .integer(GHOSTS_COUNT, field.ghosts().size());

        stopGhosts(); // по умолчанию все привидения стоят на месте
    }

    private void givenPlayer(Hero hero) {
        EventListener listener = mock(EventListener.class);
        listeners.add(listener);

        Player player = new Player(listener, settings);
        players.add(player);

        Game game = new Single(player, printer);
        games.add(game);

        dice(hero.getX(), hero.getY());
        game.on(field);
        game.newGame();
    }

    protected GameSettings settings() {
        return spy(new TestGameSettings())
                .integer(POTION_POWER, 1)
                .integer(TREASURE_BOX_COUNT, 0)
                .integer(GHOSTS_COUNT, 0);
    }

    public void tick() {
        field.tick();
    }

    // getters & asserts

    /**
     * Проверяет одну борду с заданным индексом
     * @param expected ожидаемое значение
     * @param index индекс
     */
    public void assertF(String expected, int index) {
        assertEquals(expected, game(index).getBoardAsString());
    }

    /**
     * Проверяет все борды сразу
     * @param expected ожидаемое значение
     */
    public void assertA(String expected) {
        assertEquals(expected,
                EventsListenersAssert.collectAll(games, index -> {
                    Object actual = game(index).getBoardAsString();
                    return String.format("game(%s)\n%s\n", index, actual);
                }));
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

    public void assertHeroDie() {
        assertEquals(true, game().isGameOver());
    }

    public void assertHeroAlive() {
        assertEquals(false, game().isGameOver());
    }

    // other stuff

    public void newBox(int x, int y) {
        field.boxes().add(new TreasureBox(pt(x, y)));
    }

    public void newPerk(int x, int y, Perk perk) {
        field.perks().add(new PerkOnBoard(new PointImpl(x, y), perk));
    }

    public void removeBoxes(int count) {
        settings.integer(TREASURE_BOX_COUNT, settings.integer(TREASURE_BOX_COUNT) - count);
    }

    public void removeGhosts(int count) {
        settings.integer(GHOSTS_COUNT, settings.integer(GHOSTS_COUNT) - count);
    }

    public Ghost ghost(int x, int y) {
        return field.ghosts().getAt(pt(x, y)).get(0);
    }

    public void stopGhosts() {
        field.ghosts().forEach(Ghost::stop);
    }

    public void assertHeroPerks(String expected) {
        assertEquals(expected,
                hero().getPerks().stream()
                        .sorted(Comparator.comparing(PointImpl::copy))
                        .map(Objects::toString)
                        .collect(joining("\n")));
    }

    public void assertFieldPerks(String expected) {
        assertEquals(expected,
                field.perks().stream()
                        .sorted(Comparator.comparing(PerkOnBoard::copy))
                        .map(Objects::toString)
                        .collect(joining("\n")));
    }

}
