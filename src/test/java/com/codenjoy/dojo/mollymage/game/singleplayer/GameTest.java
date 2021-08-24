package com.codenjoy.dojo.mollymage.game.singleplayer;

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


import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.MollyMage;
import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointField;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class GameTest extends AbstractGameTest {

// _____________________________________________________GAME_TEST_______________________________________________________

    @Test
    public void shouldBoard_whenStartGame() {
        Level level = mock(Level.class);
        when(level.field()).thenReturn(new PointField(10));

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
        assertSame(hero, game.getJoystick());
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
        hero.act();
        hero.up();
        field.tick();
        hero.right();
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

        hero.act();
        hero.up();
        field.tick();

        hero.right();
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

    // приведение не может появится на герое!
    @Test
    public void shouldGhostNotAppearOnHero() {
        shouldMonsterCanMoveOnPotion();

        hero.down();
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼x҉҉☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice,
                0, 0, // на неразрушаемой стене нельзя
                hero.getX(), hero.getY(), // попытка поселиться на герое
                3, 3, // попытка - клетка свободна
                Direction.DOWN.value()); // а это куда он сразу же отправится

        // when пришла пора регенериться чоперу
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼&☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
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

        hero.act();
        field.tick();

        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.right();
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
                hero.getX(), hero.getY(), // на месте героя не должен появиться
                1, 1);                    // а вот тут свободно

        // then
        asrtBrd("☼☼☼☼☼\n" +
                "☼ ☺ ☼\n" +
                "☼ ☼ ☼\n" +
                "☼#  ☼\n" +
                "☼☼☼☼☼\n");
    }

// ______________________________________________________MOVEMENT_______________________________________________________

    @Test
    public void shouldHeroOnBoardOneRightStep_whenCallRightCommand() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldHeroOnBoardTwoRightSteps_whenCallRightCommandTwice() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        field.tick();

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldHeroOnBoardOneUpStep_whenCallDownCommand() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldHeroWalkUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.down();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallDown() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.down();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldHeroWalkLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        field.tick();

        hero.right();
        field.tick();

        hero.left();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.left();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoMaxRight();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoMaxUp();

        asrtBrd("☺    \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldHeroMovedOncePerTact() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.down();
        hero.up();
        hero.left();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        hero.right();
        hero.left();
        hero.down();
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n");
    }

    // герой не может пойти вперед на стенку
    @Test
    public void shouldHeroStop_whenUpWall() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero.down();
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenLeftWall() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero.left();
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenRightWall() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        gotoMaxRight();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼  ☺☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenDownWall() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        gotoMaxUp();

        asrtBrd("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // герой не может вернуться на место зелья, она его не пускает как стена
    @Test
    public void shouldHeroStop_whenGotoPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "3☺   \n");

        hero.left();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");
    }

    // герой может одноверменно перемещаться по полю и класть зелья
    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_potionFirstly() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_moveFirstly() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " 3☺  \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_potionThanMove() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        field.tick();

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "3☺   \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_moveThanPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        field.tick();

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " 3☺  \n");
    }

    // появляются привидения, их несоклько за игру
    // каждый такт привидения куда-то рендомно муваются
    // если герой и привидение попали в одну клетку - герой умирает
    @Test
    public void shouldRandomMoveMonster() {
        givenBr("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        ghostsCount(1);
        ghostAt(9, 9);
        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 1, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 1);
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 0, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼       & ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼&        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 1, Direction.RIGHT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼ &       ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 0, Direction.LEFT.value());
        field.tick();
        field.tick();

        dice(dice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼&        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼&☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼Ѡ        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        Assert.assertTrue(game.isGameOver());
        verify(listener).event(Events.DIED);
    }

    // если я двинулся за пределы стены и тут же поставил зелье,
    // то зелье упадет на моем текущем месте
    @Test
    public void shouldMoveOnBoardAndDropPotionTogether() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.left();
        hero.act();
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼☻  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение может ходить по зелью
    @Test
    public void shouldMonsterCanMoveOnPotion() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        ghostsCount(1);
        ghostAt(3, 3);
        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.right();
        hero.act();
        field.tick();

        hero.left();
        field.tick();

        hero.down();
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ 2&☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение не может пойти на стенку
    @Test
    public void shouldGhostCantMoveOnWall() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        ghostAt(3, 3);
        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.RIGHT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение не будет ходить, если ему некуда
    @Test
    public void shouldGhostCantMoveWhenNoSpaceAround() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        ghostsCount(1);
        ghostAt(3, 3);

        boxesCount(2);
        boxAt(2, 3);
        boxAt(3, 2);

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.RIGHT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.UP.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение вновь сможет ходить когда его разбарикадируют
    @Test
    public void shouldGhostCanMoveWhenSpaceAppear() {
        shouldGhostCantMoveWhenNoSpaceAround();

        // минус одна коробка
        field.boxes().remove(pt(2, 3));
        boxesCount(boxesCount() - 1);

        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(dice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼&  ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

// _______________________________________________________POTION________________________________________________________

    @Test
    public void shouldPotionDropped_whenHeroDropPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
    }

    @Test
    public void shouldPotionDropped_whenHeroDropPotionAtAnotherPlace() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.up();
        field.tick();

        hero.right();
        field.tick();

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");
    }

    @Test
    public void shouldPotionsDropped_whenHeroDropThreePotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(3);

        hero.up();
        field.tick();

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "2☻   \n" +
                "     \n");
    }

    // проверить, что герой не может класть зелья больше,
    // чем у него в settings прописано
    @Test
    public void shouldOnlyTwoPotions_whenLevelApproveIt() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero.up();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☻    \n" +
                "     \n");

        hero.up();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☻    \n" +
                "3    \n" +
                "     \n");

        hero.up();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "☺    \n" +
                "3    \n" +
                "2    \n" +
                "     \n");
    }

    // герой не может класть два зелья на одно место
    @Test
    public void shouldOnlyOnePotionPerPlace() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);

        hero.act();
        field.tick();

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        assertEquals(1, field.potions().size());

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        field.tick();   // зелья больше нет, иначе тут был бы взрыв второй

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBoom_whenDroppedPotionHas5Ticks() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    // проверить, что я могу поставить еще одно зелье, когда другое рвануло
    @Test
    public void shouldCanDropNewPotion_whenOtherBoom() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        shouldBoom_whenDroppedPotionHas5Ticks();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☻  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_inOtherCorner() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoMaxUp();
        gotoMaxRight();

        hero.act();
        field.tick();

        hero.left();
        field.tick();

        hero.left();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("  ☺҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldWallProtectsHero() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero.act();
        goOut();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼ ☼ ☼\n" +
                "☼1  ☼\n" +
                "☼☼☼☼☼\n");

        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉҉ ☼\n" +
                "☼☼☼☼☼\n");

        assertHeroAlive();
    }

    @Test
    public void shouldWallProtectsHero2() {
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
        assertPotionPower(5,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉ ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertHeroAlive();
    }

    // разрыв зелья длинной указанной в settings
    @Test
    public void shouldChangePotionPower_to2() {
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
        assertPotionPower(2,
                "☼☼☼☼☼☼☼☼☼\n" +
                        "☼       ☼\n" +
                        "☼ ☼ ☼ ☼ ☼\n" +
                        "☼       ☼\n" +
                        "☼ ☼ ☼ ☼ ☼\n" +
                        "☼҉ ☺    ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉҉҉    ☼\n" +
                        "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangePotionPower_to3() {
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
        assertPotionPower(3,
                "☼☼☼☼☼☼☼☼☼\n" +
                        "☼       ☼\n" +
                        "☼ ☼ ☼ ☼ ☼\n" +
                        "☼       ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉ ☺    ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉҉҉҉   ☼\n" +
                        "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangePotionPower_to6() {
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
        assertPotionPower(6,
                "☼☼☼☼☼☼☼☼☼\n" +
                        "☼҉      ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉      ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉ ☺    ☼\n" +
                        "☼҉☼ ☼ ☼ ☼\n" +
                        "☼҉҉҉҉҉҉҉☼\n" +
                        "☼☼☼☼☼☼☼☼☼\n");
    }

    // я немогу модифицировать список зелья на доске, меняя getPotions
    // но список зелья, что у меня на руках обязательно синхронизирован
    // с теми, что на поле
    @Test
    public void shouldNoChangeOriginalPotionsWhenUseBoardApiButTimersSynchronized() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);
        hero.act();
        hero.right();
        field.tick();
        hero.act();
        hero.right();
        field.tick();

        List<Potion> potions1 = field.potions().all();
        List<Potion> potions2 = field.potions().all();
        List<Potion> potions3 = field.potions().all();
        assertEquals(potions1.toString(), potions2.toString());
        assertEquals(potions2.toString(), potions3.toString());
        assertEquals(potions3.toString(), potions1.toString());

        Potion potion11 = potions1.get(0);
        Potion potion12 = potions2.get(0);
        Potion potion13 = potions3.get(0);
        assertEquals(potion11.toString(), potion12.toString());
        assertEquals(potion12.toString(), potion13.toString());
        assertEquals(potion13.toString(), potion11.toString());

        Potion potion21 = potions1.get(1);
        Potion potion22 = potions2.get(1);
        Potion potion23 = potions3.get(1);
        assertEquals(potion21.toString(), potion22.toString());
        assertEquals(potion22.toString(), potion23.toString());
        assertEquals(potion23.toString(), potion21.toString());

        field.tick();
        field.tick();

        assertFalse(potion11.isExploded());
        assertFalse(potion12.isExploded());
        assertFalse(potion13.isExploded());

        field.tick();

        assertTrue(potion11.isExploded());
        assertTrue(potion12.isExploded());
        assertTrue(potion13.isExploded());

        assertFalse(potion21.isExploded());
        assertFalse(potion22.isExploded());
        assertFalse(potion23.isExploded());

        field.tick();

        assertTrue(potion21.isExploded());
        assertTrue(potion22.isExploded());
        assertTrue(potion23.isExploded());
    }

    @Test
    public void shouldReturnShouldNotSynchronizedPotionsList_whenUseBoardApi() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        hero.right();
        field.tick();

        List<Potion> potions1 = field.potions().all();
        assertEquals(1, potions1.size());

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        List<Potion> potions2 = field.potions().all();
        assertEquals(0, potions2.size());
        assertEquals(0, potions1.size());
        assertEquals(potions1.toString(), potions2.toString());
    }

    @Test
    public void shouldChangeBlast_whenUseBoardApi() {  // TODO а нода вообще такое? стреляет по перформансу перекладывать объекты и усложняет код
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        List<Blast> blasts1 = field.blasts().all();
        List<Blast> blasts2 = field.blasts().all();
        List<Blast> blasts3 = field.blasts().all();
        assertEquals(blasts1.toString(), blasts2.toString());
        assertEquals(blasts2.toString(), blasts3.toString());
        assertEquals(blasts3.toString(), blasts1.toString());

        Point blast11 = blasts1.get(0);
        Point blast12 = blasts2.get(0);
        Point blast13 = blasts3.get(0);
        assertEquals(blast11.toString(), blast12.toString());
        assertEquals(blast12.toString(), blast13.toString());
        assertEquals(blast13.toString(), blast11.toString());

        Point blast21 = blasts1.get(1);
        Point blast22 = blasts2.get(1);
        Point blast23 = blasts3.get(1);
        assertEquals(blast21.toString(), blast22.toString());
        assertEquals(blast22.toString(), blast23.toString());
        assertEquals(blast23.toString(), blast21.toString());
    }

    // взрывная волна не проходит через непробиваемую стенку
    @Test
    public void shouldBlastWaveDoesNotPassThroughWall() {
        settings.integer(POTION_POWER, 3);
        givenBr("☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼☺    ☼\n" +
                "☼☼☼☼☼☼☼\n");

        asrtBrd("☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼☺    ☼\n" +
                "☼☼☼☼☼☼☼\n");

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        hero.up();
        field.tick();

        hero.act();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼҉☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼Ѡ☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldStopBlastWhenHeroOrDestroyWalls() {
        potionsPower(5);

        givenBr("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺      \n");

        int count = 1;
        boxesCount(count);
        boxAt(3, 0);

        when(dice.next(anyInt())).thenReturn(101); // don't drop perk by accident

        hero.act();
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "Ѡ      \n" +
                "҉      \n" +
                "҉҉҉H   \n");
    }

    @Test
    public void shouldStopBlastWhenGhost() {
        potionsPower(5);

        givenBr("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺      \n");

        ghostsCount(1);
        ghostAt(4, 0).stop();

        hero.act();
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.right();
        field.tick();
        field.tick();

        asrtBrd("       \n" +
                "҉      \n" +
                "҉      \n" +
                "҉☺     \n" +
                "҉      \n" +
                "҉      \n" +
                "҉҉҉҉x  \n");
    }

// ____________________________________________________KILL_/_DEATH_____________________________________________________

    // если герой стоит на зелье то он умирает после его взрыва
    @Test
    public void shouldKillHero_whenPotionExploded() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        hero.right();
        field.tick();

        field.tick();
        field.tick();

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    // после смерти ходить больше нельзя
    @Test
    public void shouldException_whenTryToMoveIfDead_goLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero.left();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    private void killPotioner() {
        hero.up();
        field.tick();

        hero.right();
        hero.act();
        field.tick();

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goDown() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero.down();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_dropPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    // если герой стоит под действием ударной волны, он умирает
    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.act();
        field.tick();

        hero.right();
        field.tick();

        field.tick();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.right();
        field.tick();

        hero.act();
        field.tick();

        hero.left();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1   \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉҉  \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.up();
        hero.act();
        field.tick();

        hero.down();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "1    \n" +
                "☺    \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉   \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromDown() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero.down();
        field.tick();

        hero.act();
        field.tick();

        hero.up();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "1    \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "҉҉   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "     \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldNoKillHero_whenPotionExploded_blastWaveAffect_fromDownRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero.down();
        field.tick();

        hero.right();
        field.tick();

        hero.act();
        field.tick();

        hero.up();
        field.tick();

        hero.left();
        field.tick();

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " 1   \n");

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_HeroDie() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoBoardCenter();

        hero.act();
        field.tick();

        hero.down();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "  ҉  \n" +
                " ҉҉҉ \n" +
                "  Ѡ  \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "  Ѡ  \n" +
                "     \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    // они взрываются от ударной волны
    @Test
    public void shouldDestroyWallsDestroyed_whenPotionExploded() {
        givenBr("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");

        hero.act();
        goOut();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "# # #\n" +
                "#1  #\n" +
                "#####\n");

        field.tick();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "#҉# #\n" +
                "H҉҉ #\n" +
                "#H###\n");
    }

    // привидение умирает, если попадает под взывающееся зелье
    @Test
    public void shouldDieMonster_whenPotionExploded() {
        givenBr("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        ghostsCount(1);
        ghostAt(9, 9);
        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 1, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        dice(dice, 1, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      & ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼1 &      ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼҉x       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, level.size() - 2, level.size() - 2, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldGhostAppearAfterKill() {
        potionsPower(3);

        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        dice(dice, 3, 0, Direction.DOWN.value());
        ghostsCount(1);

        hero.act();
        hero.up();
        field.tick();

        hero.right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉x \n");

        dice(dice, 2, 2, Direction.DOWN.value());
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

// _______________________________________________________EVENTS________________________________________________________

    @Test
    public void shouldNoEventsWhenHeroNotMove() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void shouldFireEventWhenKillWall() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        boxesCount(1);
        boxAt(0, 0);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "#☺   \n");

        hero.act();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n");

        verify(listener).event(Events.KILL_TREASURE_BOX);
    }

    @Test
    public void shouldFireEventWhenKillGhost() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        ghostsCount(1);
        ghostAt(0, 0);

        hero.act();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n");

        verify(listener).event(Events.KILL_GHOST);
    }

    @Test
    public void shouldCalculateGhostsAndWallKills() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        boxesCount(2);
        boxAt(0, 1);
        boxAt(0, 3);

        ghostsCount(2);
        ghostAt(0, 0);
        ghostAt(0, 2);

        canDropPotions(4);
        potionsPower(1);

        asrtBrd("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        asrtBrd(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        hero.right();
        field.tick();

        asrtBrd("  ☺  \n" +
                "#3   \n" +
                "&2   \n" +
                "#1   \n" +
                "x҉҉  \n");

        events.verifyAllEvents("[KILL_GHOST]");

        dice(dice, 3, 4, Direction.ACT.value()); // новое привидение, стоит не двигается
        field.tick();
        field.ghosts().all().forEach(Ghost::stop); // и не будет больше двигаться

        asrtBrd("  ☺& \n" +
                "#2   \n" +
                "&1   \n" +
                "H҉҉  \n" +
                " ҉   \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        dice(dice, 4, 4); // новая коробка
        field.tick();

        asrtBrd("  ☺&#\n" +
                "#1   \n" +
                "x҉҉  \n" +
                " ҉   \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST]");

        dice(dice, 3, 3, Direction.ACT.value()); // новое привидение, стоит не двигается
        field.tick();
        field.ghosts().all().forEach(Ghost::stop); // и не будет больше двигаться

        asrtBrd(" ҉☺&#\n" +
                "H҉҉& \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        dice(dice, 4, 3); // новая коробка
        hero.left();
        field.tick();

        hero.down();
        hero.act();
        field.tick();

        asrtBrd("   &#\n" +
                " ☻ &#\n" +
                "     \n" +
                "     \n" +
                "     \n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd(" ҉ &#\n" +
                "҉Ѡ҉&#\n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        dice(dice, 1, 1);
        field.tick();
        game.newGame();

        asrtBrd("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");

        hero = (Hero) game.getJoystick();
        hero.move(pt(1, 0));

        asrtBrd("   &#\n" +
                "   &#\n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        hero.act();
        hero.right();
        field.tick();

        hero.right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ҉   \n" +
                "҉҉҉☺ \n");
    }

    @Test
    public void shouldCalculateGhostsAndWallKills_caseBigBadaboom() {
        settings.bool(BIG_BADABOOM, true);

        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        boxesCount(2);
        boxAt(0, 1);
        boxAt(0, 3);

        ghostsCount(2);
        ghostAt(0, 0);
        ghostAt(0, 2);

        canDropPotions(4);
        potionsPower(1);

        asrtBrd("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.act();
        hero.up();
        field.tick();

        asrtBrd(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        events.verifyAllEvents("[]");

        hero.right();
        field.tick();

        asrtBrd(" ҉☺  \n" +
                "H҉҉  \n" +
                "x҉҉  \n" +
                "H҉҉  \n" +
                "x҉҉  \n");

        dice(dice,
                2, 2, // новые координаты коробок
                3, 3);
        field.tick();

        asrtBrd("  ☺  \n" +
                "   # \n" +
                "  #  \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_GHOST, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldGhostNotAppearOnThePlaceWhereItDie_AfterKill() {
        potionsPower(3);

        givenBr("   \n" +
                "   \n" +
                "☺  \n");

        dice(dice, 2, 0, Direction.DOWN.value());
        ghostsCount(1);

        // when portion explode
        hero.act();
        hero.up();
        field.tick();

        hero.right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        // then ghost die
        asrtBrd("҉  \n" +
                "҉☺ \n" +
                "҉҉x\n");

        // when fill free places boxes
        boxesCount(6);
        dice(dice, preparedCoordinatesForBoxesAndGhosts());
        field.tick();

        // then boxes fill whole field except 2 free points([2,2] and [0,2]).
        // ghost tried to generate on both free places, but appeared only on [2,2]
        // [0,2] denied as previous place of death
        asrtBrd("##&\n" +
                "#☺#\n" +
                "## \n");
        assertEquals(1, field.ghosts().all().size());
    }

    private int[] preparedCoordinatesForBoxesAndGhosts() {
        int[] result = new int[]
                {
                        0, 0, 0, 1,         // boxes, first line
                        1, 0, 1, 1, 1, 2,  // boxes second line
                        2, 0, 2, 1,        // boxes third line
                        0, 2,              // first point for ghost
                        2, 2,              // second point for ghost
                };
        return result;
    }

// _____________________________________________________________________________________________________________________
}
