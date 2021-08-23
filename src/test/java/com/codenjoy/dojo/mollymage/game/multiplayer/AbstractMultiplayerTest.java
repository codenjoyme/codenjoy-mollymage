package com.codenjoy.dojo.mollymage.game.multiplayer;

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
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.mollymage.model.levels.LevelImpl;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public abstract class AbstractMultiplayerTest {

    protected List<Hero> heroes = new LinkedList<>();
    protected List<Game> games = new LinkedList<>();
    private List<EventListener> listeners = new LinkedList<>();
    private List<Player> players = new LinkedList<>();
    protected GameSettings settings = settings();
    protected MollyMage field;
    protected Dice dice = mock(Dice.class);
    private PrinterFactory printerFactory = new PrinterFactoryImpl();
    protected PerksSettingsWrapper perks;
    protected EventsListenersAssert events = new EventsListenersAssert(() -> listeners, Events.class);

    public void setup() {
        perks = settings.perksSettings();

        Level level = new LevelImpl(
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        boxesCount(0);
        ghostsCount(0);

        field = new MollyMage(level, dice, settings);
    }

    protected GameSettings settings() {
        return spy(new TestGameSettings())
                .integer(POTION_POWER, 1);
    }

    public void givenBoard(int count) {
        for (int i = 0; i < count; i++) {
            listeners.add(mock(EventListener.class));
            players.add(new Player(listener(i), settings));
            games.add(new Single(player(i), printerFactory));
        }

        games.forEach(g -> {
            g.on(field);
            g.newGame();
        });
        resetHeroes();
    }

    protected void boxAt(int x, int y) {
        field.boxes().add(new TreasureBox(x, y));
    }

    protected void perkAt(int x, int y, Perk perk) {
        field.perks().add(new PerkOnBoard(new PointImpl(x, y), perk));
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

    protected Ghost ghostAt(int x, int y) {
        Ghost ghost = new Ghost(pt(x, y), field, dice);
        ghost.stop();
        field.ghosts().add(ghost);
        return ghost;
    }

    protected void asrtBrd(String board, Game game) {
        assertEquals(board, game.getBoardAsString());
    }

    protected Hero hero(int index) {
        return heroes.get(index);
    }

    protected Game game(int index) {
        return games.get(index);
    }

    protected Player player(int index) {
        return players.get(index);
    }

    protected EventListener listener(int index) {
        return listeners.get(index);
    }

    protected void tick() {
        field.tick();
    }

    protected void newGameForAllDied() {
        players.forEach(player -> {
            if (!player.isAlive()) {
                field.newGame(player(players.indexOf(player)));
            }
        });
        resetHeroes();
    }

    protected void dice(Dice dice, int... values) {
        reset(dice);
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int value : values) {
            when = when.thenReturn(value);
        }
    }

    protected void resetHeroes() {
        heroes.clear();
        players.forEach(player -> heroes.add(player.getHero()));
    }

    protected void assertBoards(String expected, Integer... indexes) {
        events.assertAll(expected, games.size(), indexes, index -> {
            Object actual = game(index).getBoardAsString();
            return String.format("game(%s)\n%s\n", index, actual);
        });
    }

    protected void resetListeners() {
        listeners.forEach(Mockito::reset);
    }
}