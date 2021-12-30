package com.codenjoy.dojo.mollymage.model;

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
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;

public class EventsTest extends AbstractGameTest {

    @Test
    public void shouldNoEventsWhenHeroNotMove() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        tick();
        tick();
        tick();
        tick();

        // then
        verifyAllEvents("");
    }

    @Test
    public void shouldFireEventWhenKillWall() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "#☺   \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "#☺   \n");

        // when
        hero().dropPotion();
        hero().right();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n");

        verifyAllEvents("[KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldFireEventWhenKillGhost() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "&☺   \n");

        // when
        hero().dropPotion();
        hero().right();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n");

        verifyAllEvents("[KILL_GHOST]");
    }

    @Test
    public void shouldCalculateGhostsAndWallKills() {
        // given
        settings.integer(POTIONS_COUNT, 4)
                .integer(POTION_POWER, 1);

        givenFl("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        assertF("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        // then
        assertF(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        // when
        hero().right();
        tick();

        // then
        assertF("  ☺  \n" +
                "#3   \n" +
                "&2   \n" +
                "#1   \n" +
                "x҉҉  \n");

        verifyAllEvents("[KILL_GHOST]");

        // when
        // новое привидение, стоит не двигается
        dice(3, 4, Direction.ACT.value());
        tick();
        stopGhosts();

        // then
        assertF("  ☺& \n" +
                "#2   \n" +
                "&1   \n" +
                "H҉҉  \n" +
                " ҉   \n");

        verifyAllEvents("[KILL_TREASURE_BOX]");

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        assertF("  ☺&#\n" +
                "#1   \n" +
                "x҉҉  \n" +
                " ҉   \n" +
                "     \n");

        verifyAllEvents("[KILL_GHOST]");

        // when
        // новое привидение, стоит не двигается
        dice(3, 3, Direction.ACT.value());
        tick();
        stopGhosts();

        // then
        assertF(" ҉☺&#\n" +
                "H҉҉& \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        verifyAllEvents("[KILL_TREASURE_BOX]");

        // when
        // новые коробки
        dice(4, 3);
        hero().left();
        tick();

        hero().down();
        hero().dropPotion();
        tick();

        // then
        assertF("   &#\n" +
                " ☻ &#\n" +
                "     \n" +
                "     \n" +
                "     \n");

        // when
        tick();
        tick();
        tick();
        tick();

        // then
        assertF(" ҉ &#\n" +
                "҉Ѡ҉&#\n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        dice(1, 1);
        tick();
        game().newGame();

        // then
        assertF("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");

        // when
        hero().move(pt(1, 0));

        // then
        assertF("   &#\n" +
                "   &#\n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        // when
        hero().dropPotion();
        hero().right();
        tick();

        hero().right();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ҉   \n" +
                "҉҉҉☺ \n");
    }

    @Test
    public void shouldCalculateGhostsAndWallKills_caseBigBadaboom() {
        // given
        settings.bool(BIG_BADABOOM, true)
                .integer(POTIONS_COUNT, 4)
                .integer(POTION_POWER, 1);

        givenFl("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        assertF("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        // then
        assertF(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        verifyAllEvents("");

        // when
        hero().right();
        tick();

        // then
        assertF(" ҉☺  \n" +
                "H҉҉  \n" +
                "x҉҉  \n" +
                "H҉҉  \n" +
                "x҉҉  \n");

        // when
        // новые коробки
        dice(2, 2,
            3, 3);
        tick();

        // then
        assertF("  ☺  \n" +
                "   # \n" +
                "  #  \n" +
                "     \n" +
                "     \n");

        verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_GHOST, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldGhostNotAppearOnThePlaceWhereItDie_AfterKill() {
        // given
        settings.integer(POTION_POWER, 3);

        givenFl("   \n" +
                "   \n" +
                "☺ &\n");

        // when
        // portion explode
        hero().dropPotion();
        hero().up();
        tick();

        hero().right();
        tick();

        tick();
        tick();
        tick();

        // then
        // ghost die
        assertF("҉  \n" +
                "҉☺ \n" +
                "҉҉x\n");

        verifyAllEvents("[KILL_GHOST]");

        // when
        // fill free places boxes
        settings.integer(TREASURE_BOX_COUNT, 6);
        dice(preparedCoordinatesForBoxesAndGhosts());
        tick();

        // then
        // boxes fill whole field except 2 free points([2,2] and [0,2]).
        // ghost tried to generate on both free places, but appeared only on [2,2]
        // [0,2] denied as previous place of death
        assertF("##&\n" +
                "#☺#\n" +
                "## \n");

        assertEquals(1, field.ghosts().size());
    }

    private int[] preparedCoordinatesForBoxesAndGhosts() {
        int[] result = new int[]
                {
                        0, 0, 0, 1,        // boxes, first line
                        1, 0, 1, 1, 1, 2,  // boxes second line
                        2, 0, 2, 1,        // boxes third line
                        0, 2,              // first point for ghost
                        2, 2,              // second point for ghost
                };
        return result;
    }

    @Test
    public void shouldFireEventWhenKillWallOnlyForOneHero() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "#☺   \n");

        // when
        hero(1).dropPotion();
        hero(1).right();
        hero(0).up();
        tick();

        hero(1).right();
        hero(0).up();
        tick();

        hero(1).right();
        hero(0).up();
        tick();

        tick();
        tick();

        // then
        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ♥\n", 0);

        verifyAllEvents(
                "listener(1) => [KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        assertF(" ☺  #\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ♥\n", 0);
    }

    @Test
    public void shouldFireEventWhenKillGhostMultiplayer() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "&☺   \n");

        // when
        hero(1).dropPotion();
        hero(1).right();
        hero(0).up();
        tick();

        hero(1).right();
        hero(0).up();
        tick();

        hero(1).right();
        hero(0).up();
        tick();

        tick();
        tick();

        // then
        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ♥\n", 0);

        verifyAllEvents(
                "listener(1) => [KILL_GHOST]\n");

        // when
        tick();

        // then
        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ♥\n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseHERO_DIED() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#☺  \n");

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉H҉҉ \n", 0);

        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [HERO_DIED, KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        assertF("    #\n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#☺  \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#♥  \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "3#3  \n", 0);

        // when
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉H҉҉ \n", 0);

        verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        assertF("    #\n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseHERO_DIED() {
        // given
        settings.integer(POTION_POWER, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺##☺ \n");

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "҉HH҉҉\n", 0);

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [HERO_DIED, KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4,
            4, 3);
        tick();

        // then
        assertF("    #\n" +
                "    #\n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseHERO_DIED() {
        // given
        givenFl("     \n" +
                "  ☺  \n" +
                " ☺#☺ \n" +
                "  ☺  \n" +
                "     \n");

        // when
        hero(0).dropPotion();
        hero(1).dropPotion();
        hero(2).dropPotion();
        hero(3).dropPotion();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "҉♣H♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", 0);

        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(3) => [HERO_DIED, KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        assertF("    #\n" +
                "  Ѡ  \n" +
                " ♣ ♣ \n" +
                "  ♣  \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseHERO_DIED_caseNotEqualPosition() {
        // given
        givenFl("     \n" +
                "  ☺  \n" +
                "#☺#☺ \n" +
                " #☺  \n" +
                "     \n");

        // when
        hero(0).dropPotion();
        hero(1).dropPotion();
        hero(2).dropPotion();
        hero(3).dropPotion();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "H♣H♣҉\n" +  // первую стенку подбил монополист, центральную все
                " H♣҉ \n" +  // эту стенку подбили только двое
                "  ҉  \n", 0);

        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [HERO_DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED, KILL_TREASURE_BOX]\n" +
                "listener(3) => [HERO_DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n");

        // when
        // новые коробки
        dice(4, 4,
            4, 3,
            4, 2);
        tick();

        // then
        assertF("    #\n" +
                "  Ѡ #\n" +
                " ♣ ♣#\n" +
                "  ♣  \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {
        // given
        settings.integer(POTION_POWER, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺##☺ \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺##♥ \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "☺  ♥ \n" +
                "҉  ҉ \n" +
                "҉  ҉ \n" +
                "҉HH҉҉\n", 0);

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");


        // when
        // новые коробки
        dice(4, 4,
            4, 3);
        tick();

        // then
        assertF("    #\n" +
                "☺  ♥#\n" +
                "     \n" +
                "     \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseHERO_DIED() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&☺  \n");

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉x҉҉ \n", 0);

        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_GHOST]\n" +
                "listener(1) => [HERO_DIED, KILL_GHOST]\n");

        // when
        removeGhosts(1); // больше не надо привидений
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseAlive() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&☺  \n");

        // when
        hero(0).dropPotion();
        hero(0).up();
        hero(1).dropPotion();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉x҉҉ \n", 0);

        verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        // when
        removeGhosts(1); // больше не надо привидений
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourGhosts_caseHERO_DIED() {
        // given
        givenFl("     \n" +
                "  ☺  \n" +
                " ☺&☺ \n" +
                "  ☺  \n" +
                "     \n");

        // when
        hero(0).dropPotion();
        hero(1).dropPotion();
        hero(2).dropPotion();
        hero(3).dropPotion();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "҉♣x♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", 0);

        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_GHOST]\n" +
                "listener(1) => [HERO_DIED, KILL_GHOST]\n" +
                "listener(2) => [HERO_DIED, KILL_GHOST]\n" +
                "listener(3) => [HERO_DIED, KILL_GHOST]\n");

        // when
        removeGhosts(1); // больше не надо привидений
        tick();

        // then
        assertF("     \n" +
                "  Ѡ  \n" +
                " ♣ ♣ \n" +
                "  ♣  \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom() {
        // given
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

        // when
        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).dropPotion();
        tick();

        hero(0).move(1, 3);
        hero(0).dropPotion();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).dropPotion();
        hero(1).move(1, 1);
        hero(1).dropPotion();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).dropPotion();
        hero(2).move(3, 1);
        hero(2).dropPotion();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).dropPotion();
        hero(3).move(3, 3);
        hero(3).dropPotion();
        hero(3).move(3, 0);

        tick();

        // then
        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                0);

        // when
        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        // then
        assertF("     \n" +
                "&24♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "☺    \n",
                0);

        verifyAllEvents("");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [HERO_DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        assertF(" ҉҉҉ \n" +
                "x҉҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                "☺҉҉҉ \n", 0);

        // when
        removeBoxes(1); // больше не надо коробок
        tick();

        // then
        verifyAllEvents("");

        assertF("     \n" +
                "   ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "☺    \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_withEnemies() {
        // given
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

        // when
        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);
        player(3).inTeam(1);

        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).dropPotion();
        tick();

        hero(0).move(1, 3);
        hero(0).dropPotion();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).dropPotion();
        hero(1).move(1, 1);
        hero(1).dropPotion();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).dropPotion();
        hero(2).move(3, 1);
        hero(2).dropPotion();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).dropPotion();
        hero(3).move(3, 3);
        hero(3).dropPotion();
        hero(3).move(3, 0);

        tick();

        // then
        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥öö \n", 0);

        // when
        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        // then
        assertF("     \n" +
                "&24Ö&\n" +
                " 1#3 \n" +
                "&♠2Ö&\n" +
                "☺    \n", 0);

        verifyAllEvents("");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [HERO_DIED, KILL_ENEMY_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [HERO_DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        assertF(" ҉҉҉ \n" +
                "x҉҉øx\n" +
                "҉҉H҉҉\n" +
                "x♣҉øx\n" +
                "☺҉҉҉ \n", 0);

        // when
        removeBoxes(1); // больше не надо коробок
        tick();

        // then
        verifyAllEvents("");

        assertF("     \n" +
                "   ø \n" +
                "     \n" +
                " ♣ ø \n" +
                "☺    \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_caseKillAll() {
        // given
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

        // when
        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).dropPotion();
        tick();

        hero(0).move(1, 3);
        hero(0).dropPotion();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).dropPotion();
        hero(1).move(1, 1);
        hero(1).dropPotion();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).dropPotion();
        hero(2).move(3, 1);
        hero(2).dropPotion();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).dropPotion();
        hero(3).move(3, 3);
        hero(3).dropPotion();
        hero(3).move(3, 0);

        tick();

        // then
        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                0);

        // when
        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        // then
        assertF("     \n" +
                "&☻4♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "     \n",
                0);

        verifyAllEvents("");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [HERO_DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n");

        assertF(" ҉҉҉ \n" +
                "xѠ҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                " ҉҉҉ \n", 0);

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        verifyAllEvents("");

        assertF("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", 0);
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenNotBigBadaboom_caseKillAll() {
        // given
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, false)
                .perksSettings().dropRatio(0);

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

        // when
        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).dropPotion();
        tick();

        hero(0).move(1, 3);
        hero(0).dropPotion();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).dropPotion();
        hero(1).move(1, 1);
        hero(1).dropPotion();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).dropPotion();
        hero(2).move(3, 1);
        hero(2).dropPotion();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).dropPotion();
        hero(3).move(3, 3);
        hero(3).dropPotion();
        hero(3).move(3, 0);

        tick();

        // then
        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n", 0);

        // when
        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        // then
        assertF("     \n" +
                "&☻4♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "     \n", 0);

        verifyAllEvents("");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX]\n" +
                "listener(1) => [HERO_DIED]\n");

        assertF("     \n" +
                "&Ѡ3♠&\n" +
                "҉҉H2 \n" +
                "&11♠&\n" +
                "     \n", 0);

        // when
        // новые коробки
        dice(4, 4);
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(2) => [HERO_DIED]\n");

        assertF(" ҉  #\n" +
                "xѠ2♠&\n" +
                " ҉҉1 \n" +
                "x♣҉1&\n" +
                " ҉҉  \n", 0);

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(2) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(3) => [HERO_DIED]\n");

        assertF("    #\n" +
                " Ѡ11&\n" +
                "  ҉҉҉\n" +
                " ♣҉♣x\n" +
                "   ҉ \n", 0);

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(3) => [KILL_GHOST]\n");

        assertF("  ҉҉#\n" +
                " Ѡ҉♣x\n" +
                "  ҉҉ \n" +
                " ♣ ♣ \n" +
                "     \n", 0);

        // when
        tick();

        // then
        verifyAllEvents("");

        assertF("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", 0);
    }
}