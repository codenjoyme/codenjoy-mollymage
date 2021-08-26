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


import com.codenjoy.dojo.mollymage.model.MollyMage;
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameTest extends AbstractGameTest {

    @Test
    public void shouldBoard_whenStartGame() {
        Level level = mock(Level.class);
        when(level.size()).thenReturn(10);

        MollyMage board = new MollyMage(level, dice, settings);

        assertEquals(10, board.size());
    }

    @Test
    public void shouldBoard_whenStartGame2() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        assertEquals(level.size(), field.size());
    }

    @Test
    public void shouldHeroOnBoardAtInitPos_whenGameStart() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldSameHero_whenNetFromBoard() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        assertSame(hero(), game().getJoystick());
    }

    @Test
    public void shouldNotAppearBoxesOnDestroyedPlaces() {
        potionsPower(1);
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        // when hero set bomb and goes away
        hero().act();
        hero().up();
        field.tick();
        hero().right();
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "3    \n");

        // when we allow to create more boxes
        // boxes should fill square around hero in coordinates from [0,0] to [2,2]
        // we allow to create 9 boxes and only 7 should be created
        boxesCount(9);
        final int[] square3x3Coordinates = getCoordinatesForPointsInSquare(3);
        dice(dice, square3x3Coordinates);
        field.tick();
        // then
        asrtBrd("     \n" +
                "     \n" +
                "###  \n" +
                "#☺#  \n" +
                "2##  \n");
        assertEquals(7, field.boxes().all().size());

        // when field tick 2 times
        field.tick();
        field.tick();

        //  then two boxes should been destroyed
        asrtBrd("     \n" +
                "     \n" +
                "###  \n" +
                "H☺#  \n" +
                "҉H#  \n");

        // all points on the board allowed for boxes regeneration except
        // [0,1][1,0] - destroyed boxes and [1,1] - hero place
        // when fill board with boxes around hero
        dice(dice, square3x3Coordinates);
        field.tick();

        // then only 6 boxes should been exist
        asrtBrd("     \n" +
                "     \n" +
                "###  \n" +
                " ☺#  \n" +
                "# #  \n");
        assertEquals(6, field.boxes().all().size());


        // when next tick - empty spaces should been filled by boxes
        dice(dice, square3x3Coordinates);
        field.tick();

        // then boxes should been generated on [0,1] and [1,0] to
        asrtBrd("     \n" +
                "     \n" +
                "###  \n" +
                "#☺#  \n" +
                "###  \n");
        assertEquals(8, field.boxes().all().size());
    }

    @Test
    public void shouldGhostNotAppearWhenDestroyWall() {
        potionsPower(3);

        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        dice(dice, 4, 4, Direction.RIGHT.value());
        ghostsCount(1);

        boxAt(3, 0);
        boxesCount(1);

        hero().act();
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("    &\n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉H \n");

        dice(dice,
                Direction.DOWN.value(), // направление движения привидения
                3, 3); // новая коробка
        field.tick();

        asrtBrd("     \n" +
                "   #&\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");
    }

    @Test
    public void shouldWallNotAppearOnHero() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        boxesCount(1);
        dice(dice, 2, 1); // коробка

        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺# ☼\n" +
                "☼☼☼☼☼\n");

        hero().act();
        field.tick();

        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().right();
        field.tick();

        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ ☺ ☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉H ☼\n" +
                "☼☼☼☼☼\n");
        // when
        field.tick();
        dice(dice,
                0, 0,                     // на неразрушаемоей стене нельзя
                hero().getX(), hero().getY(), // на месте героя не должен появиться
                1, 1);                    // а вот тут свободно

        // then
        asrtBrd("☼☼☼☼☼\n" +
                "☼ ☺ ☼\n" +
                "☼ ☼ ☼\n" +
                "☼#  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldGameReturnsRealJoystick() {
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        hero(0).act();
        hero(1).up();
        tick();

        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        assertFalse(hero(0).isAlive());
        assertTrue(hero(1).isAlive());

        Joystick joystick1 = game(0).getJoystick();
        Joystick joystick2 = game(0).getJoystick();

        // when
        dice(dice,
                0, 0,
                1, 0);
        game(0).newGame();
        game(1).newGame();

        // then
        assertNotSame(joystick1, game(0).getJoystick());
        assertNotSame(joystick2, game(0).getJoystick());
    }

    @Test
    public void shouldGetTwoHeroesOnBoard() {
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        assertSame(hero(0), game(0).getJoystick());
        assertSame(hero(1), game(1).getJoystick());

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n", game(1));
    }

    @Test
    public void shouldPrintOtherPotionHero() {
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        hero(0).act();
        hero(0).up();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♠☺   \n", game(1));
    }

    @Test
    public void bug() {
        dice(dice,
                1, 1,
                2, 1);
        givenBr(2);

        ghostAt(1, 0);
        ghostAt(2, 0);

        boxesCount(1);
        boxAt(0, 0);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺♥  \n" +
                "#&&  \n", game(0));

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

        asrtBrd("     \n" +
                "     \n" +
                "☺҉҉♥ \n" +
                "҉҉҉҉ \n" +
                "#xx  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺  ♥ \n" +
                "     \n" +
                "#    \n", game(0));
    }
}
