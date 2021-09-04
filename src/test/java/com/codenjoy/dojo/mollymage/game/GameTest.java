package com.codenjoy.dojo.mollymage.game;

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


import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GameTest extends AbstractGameTest {

    @Test
    public void shouldBoard_whenStartGame() {
        // given
        givenFl("     \n" +
                "     \n" +
                "  ☺  \n" +
                " ☺   \n" +
                "☺    \n");

        // then
        assertEquals(5, field.size());
    }

    @Test
    public void heroesCanBeRemovedFromTheGame() {
        // given
        givenFl("     \n" +
                "     \n" +
                "  ☺  \n" +
                " ☺   \n" +
                "☺    \n");

        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                " ♥   \n" +
                "♥    \n", 0);

        // when
        game(1).close();

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n" +
                "listener(2) => []\n");

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "     \n" +
                "♥    \n", 0);
    }

    @Test
    public void heroesCanBeRestartedInTheGame() {
        // given
        givenFl("     \n" +
                "     \n" +
                "  ☺  \n" +
                " ☺   \n" +
                "☺    \n");

        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                " ♥   \n" +
                "♥    \n", 0);

        // when
        dice(4, 0);
        game(1).newGame();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "     \n" +
                "♥   ♥\n", 0);
    }

    @Test
    public void shouldBoard_whenStartGame2() {
        // given when
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // then
        assertEquals(5, field.size());
    }

    @Test
    public void shouldHeroOnBoardAtInitPos_whenGameStart() {
        // given when
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldSameHero_whenNetFromBoard() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when then
        assertSame(hero(), game().getJoystick());
    }

    private int[] inSquare(Point pt, int size) {
        List<Integer> result = new ArrayList<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                result.add(x + pt.getX());
                result.add(y + pt.getY());
            }
        }
        return result.stream()
                .mapToInt(i -> i).toArray();
    }

    @Test
    public void shouldNotAppearBoxesOnDestroyedPlaces() {
        // given
        settings.integer(POTION_POWER, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        // hero set bomb and goes away
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "3    \n");

        // when
        // we allow to create more boxes
        // boxes should fill square around hero in coordinates from [0,0] to [2,2]
        // we allow to create 9 boxes and only 7 should be created
        settings.integer(TREASURE_BOX_COUNT, 9);
        dice(inSquare(pt(0, 0), 3));
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "###  \n" +
                "#☺#  \n" +
                "2##  \n");

        assertEquals(7, field.boxes().size());

        // when
        // field tick 2 times
        tick();
        tick();

        // then
        // two boxes should been destroyed
        assertF("     \n" +
                "     \n" +
                "###  \n" +
                "H☺#  \n" +
                "҉H#  \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        // all points on the board allowed for boxes regeneration except
        // [0,1][1,0] - destroyed boxes and [1,1] - hero place
        // when fill board with boxes around hero
        dice(inSquare(pt(0, 0), 3));
        tick();

        // then
        // only 6 boxes should been exist
        assertF("     \n" +
                "     \n" +
                "###  \n" +
                " ☺#  \n" +
                "# #  \n");

        assertEquals(6, field.boxes().size());

        // when
        // next tick - empty spaces should been filled by boxes
        dice(inSquare(pt(0, 0), 3));
        tick();

        // then
        // boxes should been generated on [0,1] and [1,0] to
        assertF("     \n" +
                "     \n" +
                "###  \n" +
                "#☺#  \n" +
                "###  \n");

        assertEquals(8, field.boxes().size());
    }

    @Test
    public void shouldGhostNotAppearWhenDestroyWall() {
        // given
        settings.integer(POTION_POWER, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺  # \n");

        settings.integer(GHOSTS_COUNT, 1);
        dice(4, 4, // координаты привидения
            Direction.RIGHT.value()); // направление движения

        // when
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("    &\n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉H \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        // when
        dice(Direction.DOWN.value(), // направление движения привидения
            3, 3); // новая коробка
        tick();

        // then
        assertF("     \n" +
                "   #&\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");
    }

    @Test
    public void shouldWallNotAppearOnHero() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺# ☼\n" +
                "☼☼☼☼☼\n");

        // when
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺# ☼\n" +
                "☼☼☼☼☼\n");

        // when
        hero().act();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        hero().right();
        tick();

        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ ☺ ☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉H ☼\n" +
                "☼☼☼☼☼\n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        // when
        tick();

        dice(0, 0,  // на неразрушаемоей стене нельзя
            hero().getX(), hero().getY(),  // на месте героя не должен появиться
            1, 1); // а вот тут свободно

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ ☺ ☼\n" +
                "☼ ☼ ☼\n" +
                "☼#  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldGameReturnsRealJoystick() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).act();
        hero(1).up();
        tick();

        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        // then
        events.verifyAllEvents(
                "listener(0) => [DIED]\n" +
                "listener(1) => []\n");

        assertEquals(false, hero(0).isAlive());
        assertEquals(true, hero(1).isAlive());

        Joystick joystick1 = game(0).getJoystick();
        Joystick joystick2 = game(0).getJoystick();

        // when
        dice(0, 0,
            1, 0);
        game(0).newGame();
        game(1).newGame();

        // then
        assertNotSame(joystick1, game(0).getJoystick());
        assertNotSame(joystick2, game(0).getJoystick());
    }

    @Test
    public void shouldGetTwoHeroesOnBoard() {
        // given when
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // then
        assertSame(hero(0), game(0).getJoystick());
        assertSame(hero(1), game(1).getJoystick());

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n", 1);
    }

    @Test
    public void shouldPrintOtherPotionHero() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).act();
        hero(0).up();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♠☺   \n", 1);
    }

    @Test
    public void bug() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺☺  \n" +
                "#&&  \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺♥  \n" +
                "#&&  \n", 0);

        // when
        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).left();
        hero(1).right();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉҉♥ \n" +
                "҉҉҉҉ \n" +
                "#xx  \n", 0);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        // when
        removeGhosts(2); // больше не надо привидений
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺  ♥ \n" +
                "     \n" +
                "#    \n", 0);
    }
}