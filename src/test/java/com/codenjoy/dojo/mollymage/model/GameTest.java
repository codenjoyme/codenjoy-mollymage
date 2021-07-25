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


import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBoxes;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghosts;
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.services.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.BIG_BADABOOM;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static com.codenjoy.dojo.services.settings.SimpleParameter.v;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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
        assertEquals(SIZE, field.size());
    }

    @Test
    public void shouldHeroOnBoardAtInitPos_whenGameStart() {
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldHeroOnBoardOneRightStep_whenCallRightCommand() {
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
        gotoMaxRight();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n");
    }

    @Test
    public void shouldHeroStop_whenGoToWallUp() {
        gotoMaxUp();

        asrtBrd("☺    \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldHeroMovedOncePerTact() {
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

    @Test
    public void shouldPotionDropped_whenHeroDropPotion() {
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

    // если герой стоит на зелье то он умирает после его взрыва
    @Test
    public void shouldKillHero_whenPotionExploded() {
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
    public void shouldSameHero_whenNetFromBoard() {
        assertSame(hero, game.getJoystick());
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed() {
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
    public void shouldBlastAfter_whenPotionExposed_HeroDie() {
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

    // появляются стенки, которые конфигурятся извне
    @Test
    public void shouldHeroNotAtWall() {
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        givenBoardWithWalls();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

    }

    // герой не может пойти вперед на стенку
    @Test
    public void shouldHeroStop_whenUpWall() {
        givenBoardWithWalls();

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
        givenBoardWithWalls();

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
        givenBoardWithWalls();

        gotoMaxRight();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼  ☺☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldHeroStop_whenDownWall() {
        givenBoardWithWalls();

        gotoMaxUp();

        asrtBrd("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    private void gotoMaxRight() {
        for (int x = 0; x <= SIZE + 1; x++) {
            hero.right();
            field.tick();
        }
    }

    // герой не может вернуться на место зелья, она его не пускает как стена
    @Test
    public void shouldHeroStop_whenGotoPotion() {
        hero.act();
        field.tick();

        hero.right();
        field.tick();

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

    @Test
    public void shouldWallProtectsHero() {
        givenBoardWithOriginalWalls();

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
        canDropPotions(2);
        hero.act();
        hero.right();
        field.tick();
        hero.act();
        hero.right();
        field.tick();

        List<Potion> potions1 = field.potions();
        List<Potion> potions2 = field.potions();
        List<Potion> potions3 = field.potions();
        assertSame(potions1, potions2);
        assertSame(potions2, potions3);
        assertSame(potions3, potions1);

        Potion potion11 = potions1.get(0);
        Potion potion12 = potions2.get(0);
        Potion potion13 = potions3.get(0);
        assertSame(potion11, potion12);
        assertSame(potion12, potion13);
        assertSame(potion13, potion11);

        Potion potion21 = potions1.get(1);
        Potion potion22 = potions2.get(1);
        Potion potion23 = potions3.get(1);
        assertSame(potion21, potion22);
        assertSame(potion22, potion23);
        assertSame(potion23, potion21);

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
        hero.act();
        hero.right();
        field.tick();

        List<Potion> potions1 = field.potions();
        assertEquals(1, potions1.size());

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        List<Potion> potions2 = field.potions();
        assertEquals(0, potions2.size());
        assertEquals(0, potions1.size());
        assertSame(potions1, potions2);
    }

    @Test
    public void shouldChangeBlast_whenUseBoardApi() {  // TODO а нода вообще такое? стреляет по перформансу перекладывать объекты и усложняет код
        hero.act();
        hero.right();
        field.tick();
        hero.right();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        List<Blast> blasts1 = field.blasts();
        List<Blast> blasts2 = field.blasts();
        List<Blast> blasts3 = field.blasts();
        assertSame(blasts1, blasts2);
        assertSame(blasts2, blasts3);
        assertSame(blasts3, blasts1);

        Point blast11 = blasts1.get(0);
        Point blast12 = blasts2.get(0);
        Point blast13 = blasts3.get(0);
        assertSame(blast11, blast12);
        assertSame(blast12, blast13);
        assertSame(blast13, blast11);

        Point blast21 = blasts1.get(1);
        Point blast22 = blasts2.get(1);
        Point blast23 = blasts3.get(1);
        assertSame(blast21, blast22);
        assertSame(blast22, blast23);
        assertSame(blast23, blast21);
    }

    @Test
    public void shouldChangeWall_whenUseBoardApi() {
        givenBoardWithBoxes();

        Objects walls1 = field.objects();
        Objects walls2 = field.objects();
        Objects walls3 = field.objects();
        assertSame(walls1, walls2);
        assertSame(walls2, walls3);
        assertSame(walls3, walls1);

        Iterator<Wall> iterator1 = walls1.iterator();
        Iterator<Wall> iterator2 = walls2.iterator();
        Iterator<Wall> iterator3 = walls3.iterator();

        Point wall11 = iterator1.next();
        Point wall12 = iterator2.next();
        Point wall13 = iterator3.next();
        assertSame(wall11, wall12);
        assertSame(wall12, wall13);
        assertSame(wall13, wall11);

        Point wall21 = iterator1.next();
        Point wall22 = iterator2.next();
        Point wall23 = iterator3.next();
        assertSame(wall21, wall22);
        assertSame(wall22, wall23);
        assertSame(wall23, wall21);
    }

    // в настройках уровня так же есть и разрущающиеся стены
    @Test
    public void shouldRandomSetDestroyWalls_whenStart() {
        givenBoardWithBoxes();

        asrtBrd("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");
    }

    // они взрываются от ударной волны
    @Test
    public void shouldDestroyWallsDestroyed_whenPotionExploded() {
        givenBoardWithBoxes();

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

    // появляются привидения, их несоклько за игру
    // каждый такт привидения куда-то рендомно муваются
    // если герой и привидение попали в одну клетку - герой умирает
    @Test
    public void shouldRandomMoveMonster() {
        givenBoardWithGhost(11);

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

        dice(ghostDice, 1, Direction.DOWN.value());
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

        dice(ghostDice, 1);
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

        dice(ghostDice, 0, Direction.LEFT.value());
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

        dice(ghostDice, 1, Direction.RIGHT.value());
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

        dice(ghostDice, 0, Direction.LEFT.value());
        field.tick();
        field.tick();

        dice(ghostDice, Direction.LEFT.value());
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

        dice(ghostDice, Direction.DOWN.value());
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


    // привидение умирает, если попадает под взывающееся зелье
    @Test
    public void shouldDieMonster_whenPotionExploded() {
        SIZE = 11;
        givenBoardWithGhost(SIZE);

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

        dice(ghostDice, 1, Direction.DOWN.value());
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        dice(ghostDice, 1, Direction.LEFT.value());
        field.tick();
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

        dice(ghostDice, SIZE - 2, SIZE - 2, Direction.DOWN.value());
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
    public void shouldNoEventsWhenHeroNotMove() {
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void shouldFireEventWhenKillWall() {
        boxAt(0, 0);

        givenBoard(SIZE, 1, 0);

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
        ghostAt(0, 0);

        givenBoard(SIZE, 1, 0);

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
        ghostAt(0, 0);
        boxAt(0, 1);
        ghostAt(0, 2);
        boxAt(0, 3);

        givenBoard(SIZE, 1, 0);

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

        field.tick();

        asrtBrd("  ☺  \n" +
                "#2   \n" +
                "&1   \n" +
                "H҉҉  \n" +
                " ҉   \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        field.tick();

        asrtBrd("  ☺  \n" +
                "#1   \n" +
                "x҉҉  \n" +
                " ҉   \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST]");

        field.tick();

        asrtBrd(" ҉☺  \n" +
                "H҉҉  \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        hero.left();
        field.tick();

        hero.down();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                " ☻   \n" +
                "     \n" +
                "     \n" +
                "     \n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd(" ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();
        game.newGame();

        hero = (Hero) game.getJoystick();
        hero.move(pt(1, 0));

        asrtBrd("     \n" +
                "     \n" +
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

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "҉҉҉☺ \n");
    }

    @Test
    public void shouldCalculateGhostsAndWallKills_caseBigBadaboom() {
        settings.bool(BIG_BADABOOM, true);

        ghostAt(0, 0);
        boxAt(0, 1);
        ghostAt(0, 2);
        boxAt(0, 3);

        givenBoard(SIZE, 1, 0);

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

        field.tick();

        asrtBrd("  ☺  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_GHOST, KILL_TREASURE_BOX]");
    }

    // если я двинулся за пределы стены и тут же поставил зелье,
    // то зелье упадет на моем текущем месте
    @Test
    public void shouldMoveOnBoardAndDropPotionTogether() {
        givenBoardWithOriginalWalls();
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
        givenBoardWithGhost(SIZE);

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

        dice(ghostDice, Direction.LEFT.value());
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
        givenBoardWithGhost(SIZE);

        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.RIGHT.value());
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
        givenBoardWithGhost(SIZE);
        objects.add(new TreasureBox(2, 3));
        objects.add(new TreasureBox(3, 2));

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.RIGHT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.UP.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ #&☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.DOWN.value());
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

        objects.destroy(new TreasureBox(2, 3));

        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(ghostDice, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼&  ☼\n" +
                "☼ ☼#☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    // взрывная волна не проходит через непробиваемую стенку
    @Test
    public void shouldBlastWaveDoesNotPassThroughWall() {
        settings.integer(POTION_POWER, 3);
        givenBoardWithWalls(7);

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

        boxAt(3, 0);

        givenBoard(7, 0, 0); // hero position
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

        ghostAt(4, 0);

        givenBoard(7, 0, 0);

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

    @Test
    public void shouldGhostAppearAfterKill() {
        potionsPower(3);

        dice(ghostDice, 3, 0, Direction.DOWN.value());
        Ghosts walls = new Ghosts(new ObjectsImpl(), v(1), ghostDice);
        walls.init(field);
        withObjects(walls);

        givenBoard(SIZE, 0, 0);

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

        dice(ghostDice, 2, 2, Direction.DOWN.value());
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

    @Test
    public void shouldGhostNotAppearWhenDestroyWall() {
        potionsPower(3);

        dice(ghostDice, 4, 4, Direction.RIGHT.value());
        boxAt(3, 0);
        Ghosts walls = new Ghosts(this.objects, v(1), ghostDice);
        walls.init(field);
        withObjects(walls);

        givenBoard(SIZE, 0, 0);

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

        dice(ghostDice, Direction.DOWN.value());
        field.tick();

        asrtBrd("     \n" +
                "    &\n" +
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

        dice(ghostDice,
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
        int size = 5;
        Dice wallDice = mock(Dice.class);
        dice(wallDice, 2, 1);

        generateWalls(size);
        TreasureBoxes walls = new TreasureBoxes(new ObjectsImpl(), v(1), wallDice);
        withObjects(walls);

        givenBoard(size, 1, 1);  // hero в левом нижнем углу

        walls.init(field);
        walls.regenerate();

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


}
