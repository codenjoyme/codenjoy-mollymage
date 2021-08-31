package com.codenjoy.dojo.mollymage.game;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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
import org.junit.Assert;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;

public class MovementTest extends AbstractGameTest {

    @Test
    public void shouldHeroOnBoardOneRightStep_whenCallRightCommand() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldHeroOnBoardTwoRightSteps_whenCallRightCommandTwice() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldHeroOnBoardOneUpStep_whenCallDownCommand() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldHeroWalkUp() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().up();
        tick();

        hero().up();
        tick();

        hero().down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallDown() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldHeroWalkLeft() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        tick();

        hero().right();
        tick();

        hero().left();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallLeft() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().left();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallRight() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n");
        
        // when
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallUp() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().up();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        // then
        assertF("☺    \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");
        
        // when
        hero().up();
        tick();

        // then
        assertF("☺    \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldHeroMovedOncePerTact() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().down();
        hero().up();
        hero().left();
        hero().right();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
        
        // when
        hero().right();
        hero().left();
        hero().down();
        hero().up();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n");
    }

    // герой не может пойти вперед на стенку
    @Test
    public void shouldHeroStop_whenUpWall() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().down();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenLeftWall() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().left();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenRightWall() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().right();
        tick();

        hero().right();
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼  ☺☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().right();
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼  ☺☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenDownWall() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().up();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // герой не может вернуться на место зелья, она его не пускает как стена
    @Test
    public void shouldHeroStop_whenGotoPotion() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().act();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
        
        // when
        hero().right();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "3☺   \n");
        
        // when
        hero().left();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");
    }

    // герой может одноверменно перемещаться по полю и класть зелья
    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_potionFirstly() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().act();
        hero().right();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_moveFirstly() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        hero().act();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");
        
        // when
        hero().right();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " 3☺  \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_potionThanMove() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().act();
        field.tick();

        hero().right();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "3☺   \n");
    }

    @Test
    public void shouldHeroWalkAndDropPotionsTogetherInOneTact_moveThanPotion() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        
        // when
        hero().right();
        field.tick();

        hero().act();
        field.tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");
        
        // when
        hero().right();
        field.tick();

        // then
        assertF("     \n" +
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
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        settings.integer(GHOSTS_COUNT, 1);
        dice(9, 9, // координата
            1, Direction.DOWN.value()); // направление движения
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        dice(1);
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        dice(0, Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        dice(1, Direction.RIGHT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        dice(0, Direction.LEFT.value());
        field.tick();
        field.tick();

        dice(Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        dice(Direction.DOWN.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        field.tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        assertEquals(true, game().isGameOver());
        events.verifyAllEvents("[DIED]");
    }

    // если я двинулся за пределы стены и тут же поставил зелье,
    // то зелье упадет на моем текущем месте
    @Test
    public void shouldMoveOnBoardAndDropPotionTogether() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().left();
        hero().act();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼☻  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение может ходить по зелью
    @Test
    public void shouldMonsterCanMoveOnPotion() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        ghost(3, 3).start();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
        
        // when
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().right();
        hero().act();
        field.tick();

        hero().left();
        field.tick();

        hero().down();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ 2&☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");

        dice(Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // приведение не может появится на герое!
    @Test
    public void shouldGhostNotAppearOnHero() {
        // given
        shouldMonsterCanMoveOnPotion();

        // when
        hero().down();
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼x҉҉☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        events.verifyAllEvents("[KILL_GHOST]");

        // when
        // на неразрушаемой стене нельзя
        // попытка поселиться на герое
        // попытка - клетка свободна
        // а это куда он сразу же отправится
        dice(0, 0, hero().getX(), hero().getY(), 3, 3, Direction.DOWN.value());

        // when пришла пора регенериться чоперу
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼&☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение не может пойти на стенку
    @Test
    public void shouldGhostCantMoveOnWall() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        ghost(3, 3).start();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.RIGHT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение не будет ходить, если ему некуда
    @Test
    public void shouldGhostCantMoveWhenNoSpaceAround() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        ghost(3, 3).start();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.RIGHT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.UP.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.DOWN.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // привидение вновь сможет ходить когда его разбарикадируют
    @Test
    public void shouldGhostCanMoveWhenSpaceAppear() {
        // given
        shouldGhostCantMoveWhenNoSpaceAround();

        // when
        // минус одна коробка
        field.boxes().remove(pt(2, 3));
        removeBoxes(1);

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        dice(Direction.LEFT.value());
        field.tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼&  ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroCantGoToAnotherHero() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");
        
        // when
        hero(0).right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", 0);
    }

    // герои не могут ходить по зелью ни по своему ни по чужому
    @Test
    public void shouldHeroCantGoToPotionFromAnotherHero() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");
        
        // when
        hero(1).act();
        hero(1).right();
        tick();

        hero(1).right();
        hero(0).right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺3 ♥ \n", 0);

        // when
        hero(1).left();
        tick();

        hero(1).left();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1♥  \n", 0);
    }

    @Test
    public void shouldPotionKillAllHero() {
        // given
        shouldHeroCantGoToPotionFromAnotherHero();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉♣  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣҉Ѡ  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [DIED]\n" +
                "listener(1) => [DIED, KILL_OTHER_HERO]\n");
    }

    @Test
    public void shouldNewGamesWhenKillAll() {
        // given
        shouldPotionKillAllHero();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉♣  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣҉Ѡ  \n", 1);

        // when
        dice(0, 0, 1, 0);
        game(0).newGame();
        game(1).newGame();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", 0);

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n", 1);
    }
}
