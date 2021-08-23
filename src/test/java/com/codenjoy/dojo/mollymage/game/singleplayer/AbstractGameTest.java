package com.codenjoy.dojo.mollymage.game.singleplayer;

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
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.model.levels.LevelImpl;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import org.junit.Before;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public abstract class AbstractGameTest {

    protected Game game;
    protected Hero hero;
    protected GameSettings settings;
    protected EventListener listener;
    protected Dice dice;
    protected Player player;
    protected MollyMage field;
    private PrinterFactory printer;
    protected PerksSettingsWrapper perks;
    protected EventsListenersAssert events;
    protected Level level;

    @Before
    public void setUp() {
        dice = mock(Dice.class);
        settings = spy(new TestGameSettings());
        printer = new PrinterFactoryImpl();
        events = new EventsListenersAssert(() -> Arrays.asList(listener), Events.class);
        perks = settings.perksSettings();
        potionsPower(1);

        boxesCount(0);
        ghostsCount(0);
    }

    protected void givenBr(String map) {
        level = new LevelImpl(map);
        listener = mock(EventListener.class);
        player = new Player(listener, settings);
        game = new Single(player, printer);

        Point heroPosition = level.getHeroPosition();
        dice(dice, heroPosition.getX(), heroPosition.getY());

        field = new MollyMage(level, dice, settings);
        List<TreasureBox> boxes = level.getBoxes();
        boxesCount(boxes.size());
        field.boxes().addAll(boxes);

        game.on(field);
        game.newGame();
        hero = (Hero) game.getJoystick();
    }

    protected void gotoMaxUp() {
        for (int y = 0; y <= level.size() + 1; y++) {
            hero.up();
            field.tick();
        }
    }

    protected void newGameForDied() {
        if (!player.isAlive()) {
            field.newGame(player);
        }
        hero = player.getHero();
        hero.setAlive(true);
    }

    protected void canDropPotions(int count) {
        settings.integer(POTIONS_COUNT, count);
    }

    protected void assertHeroDie() {
        assertEquals("Expected game over", true, game.isGameOver());
    }

    protected void assertHeroAlive() {
        assertFalse(game.isGameOver());
    }

    protected void gotoBoardCenter() {
        for (int y = 0; y < level.size() / 2; y++) {
            hero.up();
            field.tick();
            hero.right();
            field.tick();
        }
    }

    protected void asrtBrd(String expected) {
        assertEquals(expected, printer.getPrinter(
                field.reader(), player).print());
    }

    protected void potionsPower(int power) {
        settings.integer(POTION_POWER, power);
    }

    protected void assertPotionPower(int power, String expected) {
        potionsPower(power);

        hero.act();
        goOut();
        field.tick();

        asrtBrd(expected);
    }

    protected void goOut() {
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        hero.up();
        field.tick();
        hero.up();
        field.tick();
    }

    protected void dice(Dice dice, int... values) {
        reset(dice);
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int value : values) {
            when = when.thenReturn(value);
        }
    }

    protected void boxAt(int x, int y) {
        field.boxes().add(new TreasureBox(x, y));
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
        field.ghosts().add(ghost);
        return ghost;
    }

    protected void perkAt(int x, int y, Perk perk) {
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
}
