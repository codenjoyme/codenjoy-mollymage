package com.codenjoy.dojo.mollymage.model;

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

import com.codenjoy.dojo.mollymage.model.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import org.junit.Before;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Arrays;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static com.codenjoy.dojo.services.settings.SimpleParameter.v;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class AbstractGameTest {

    public int SIZE = 5;
    protected Game game;
    protected Hero hero;
    protected Walls walls = new WallsImpl();
    protected GameSettings settings;
    protected EventListener listener;
    protected Dice ghostDice;
    protected Dice dice;
    protected Player player;
    protected MollyMage field;
    private PrinterFactory printer;
    protected PerksSettingsWrapper perks;
    protected EventsListenersAssert events;

    @Before
    public void setUp() {
        dice = mock(Dice.class);
        ghostDice = mock(Dice.class);
        settings = spy(new TestGameSettings());
        printer = new PrinterFactoryImpl();
        events = new EventsListenersAssert(() -> Arrays.asList(listener), Events.class);
        perks = settings.perksSettings();
        potionsPower(1);

        givenWalls();

        withWalls(walls);

        givenBoard(SIZE, 0, 0);
    }

    protected void givenBoard(int size, int x, int y) {
        dice(dice, x, y);
        settings.integer(BOARD_SIZE, size);
        field = new MollyMage(dice, settings);
        listener = mock(EventListener.class);
        player = new Player(listener, settings);
        game = new Single(player, printer);
        game.on(field);
        game.newGame();
        hero = (Hero)game.getJoystick();
    }

    protected void gotoMaxUp() {
        for (int y = 0; y <= SIZE + 1; y++) {
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
        for (int y = 0; y < SIZE / 2; y++) {
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


    protected void givenBoardWithWalls() {
        givenBoardWithWalls(SIZE);
    }

    protected void givenBoardWithWalls(int size) {
        withWalls(new OriginalWalls(v(size)));
        givenBoard(size, 1, 1); // hero в левом нижнем углу с учетом стен
    }

    protected void givenBoardWithDestroyWalls() {
        givenBoardWithDestroyWalls(SIZE);
    }

    protected void givenBoardWithDestroyWalls(int size) {
        withWalls(new Ghosts(new DestroyWalls(new OriginalWalls(v(size))), v(0), dice));
        givenBoard(size, 1, 1); // hero в левом нижнем углу с учетом стен
    }

    protected void withWalls(Walls walls) {
        when(settings.getWalls(dice)).thenReturn(walls);
    }

    protected void givenBoardWithOriginalWalls() {
        givenBoardWithOriginalWalls(SIZE);
    }

    protected void givenBoardWithOriginalWalls(int size) {
        withWalls(new OriginalWalls(v(size)));
        givenBoard(size, 1, 1); // hero в левом нижнем углу с учетом стен
    }

    protected void potionsPower(int power) {
        settings.integer(POTION_POWER, power);
    }

    protected void assertPotionPower(int power, String expected) {
        givenBoardWithOriginalWalls(9);
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

    protected void givenBoardWithGhost(int size) {
        dice(ghostDice, size - 2, size - 2);

        SIZE = size;
        Ghosts walls = new Ghosts(new OriginalWalls(v(size)), v(1), ghostDice);
        withWalls(walls);

        givenBoard(size, 1, 1); // hero в левом нижнем углу с учетом стен

        walls.init(field);
        walls.regenerate();

        this.walls = walls;

        dice(ghostDice, 1, Direction.UP.value());  // Чертик будет упираться в стенку и стоять на месте
    }

    protected TreasureBox destroyWallAt(int x, int y) {
        TreasureBox wall = new TreasureBox(pt(x, y));
        walls.add(wall);
        return wall;
    }

    private void givenWalls(Wall... input) {
        Arrays.asList(input).forEach(walls::add);
    }

    protected Ghost ghostAt(int x, int y) {
        Ghost chopper = new Ghost(pt(x, y), field, ghostDice);
        chopper.stop();
        walls.add(chopper);
        return chopper;
    }
}
