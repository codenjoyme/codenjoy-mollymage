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

import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.services.Events;
import com.codenjoy.dojo.services.Direction;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTIONS_COUNT;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class EventsTest extends AbstractGameTest {

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

        verifyNoMoreInteractions(listener());
    }

    @Test
    public void shouldFireEventWhenKillWall() {
        dice(dice, 1, 0);
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

        hero().act();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n");

        verify(listener()).event(Events.KILL_TREASURE_BOX);
    }

    @Test
    public void shouldFireEventWhenKillGhost() {
        dice(dice, 1, 0);
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        ghostsCount(1);
        ghostAt(0, 0);

        hero().act();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n");

        verify(listener()).event(Events.KILL_GHOST);
    }

    @Test
    public void shouldCalculateGhostsAndWallKills() {
        dice(dice, 1, 0);
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

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        asrtBrd(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        hero().right();
        field.tick();

        asrtBrd("  ☺  \n" +
                "#3   \n" +
                "&2   \n" +
                "#1   \n" +
                "x҉҉  \n");

        events.verifyAllEvents("[KILL_GHOST]");

        dice(dice, 3, 4, Direction.ACT.value()); // новое привидение, стоит не двигается
        field.tick();
        field.ghosts().stream().forEach(Ghost::stop); // и не будет больше двигаться

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
        field.ghosts().stream().forEach(Ghost::stop); // и не будет больше двигаться

        asrtBrd(" ҉☺&#\n" +
                "H҉҉& \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        dice(dice, 4, 3); // новая коробка
        hero().left();
        field.tick();

        hero().down();
        hero().act();
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
        game().newGame();

        asrtBrd("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");

        heroes.set(0, (Hero) game().getJoystick());
        hero().move(pt(1, 0));

        asrtBrd("   &#\n" +
                "   &#\n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        hero().act();
        hero().right();
        field.tick();

        hero().right();
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

        dice(dice, 1, 0);
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

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().act();
        hero().up();
        field.tick();

        asrtBrd(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        events.verifyAllEvents("[]");

        hero().right();
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
        hero().act();
        hero().up();
        field.tick();

        hero().right();
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
        assertEquals(1, field.ghosts().size());
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

    @Test
    public void shouldFireEventWhenKillWallOnlyForOneHero() {

        dice(dice,
                1, 0,
                1, 1);
        givenBr(2);

        boxesCount(1);
        boxAt(0, 0);

        hero(0).act();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        tick();
        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                        "listener(1) => []\n");

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd(" ♥  #\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n", game(0));
    }

    @Test
    public void shouldFireEventWhenKillGhostMultiplayer() {
        dice(dice,
                1, 0,
                1, 1);
        givenBr(2);

        ghostAt(0, 0);

        hero(0).act();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        tick();
        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                        "listener(1) => []\n");

        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseDied() {
        dice(dice,
                0, 0,
                2, 0);
        givenBr(2);

        boxesCount(1);
        boxAt(1, 0);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd("    #\n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        dice(dice,
                0, 0,
                2, 0);

        givenBr(2);

        boxesCount(1);
        boxAt(1, 0);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#♥  \n", game(0));

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "3#3  \n", game(0));

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                        "listener(1) => [KILL_TREASURE_BOX]\n");

        dice(dice, 4, 4); // новая коробка
        tick();

        asrtBrd("    #\n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseDied() {
        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBr(2);

        boxesCount(2);
        boxAt(2, 0);
        boxAt(1, 0);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

        dice(dice, // новые коробки
                4, 4,
                4, 3);
        tick();

        asrtBrd("    #\n" +
                "    #\n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied() {
        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBr(4);

        boxesCount(1);
        boxAt(2, 2);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "҉ѠH♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_TREASURE_BOX]\n");

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd("    #\n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied_caseNotEqualPosition() {
        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBr(4);

        boxesCount(3);
        boxAt(1, 1);
        boxAt(2, 2);
        boxAt(0, 2);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "HѠH♣҉\n" +  // первую стенку подбил монополист, центральную все
                " H♣҉ \n" +  // эту стенку подбили только лвое
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_TREASURE_BOX]\n");

        dice(dice, // новые коробки
                4, 4,
                4, 3,
                4, 2);
        tick();

        asrtBrd("    #\n" +
                "  ♣ #\n" +
                " Ѡ ♣#\n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {

        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBr(2);

        boxesCount(2);
        boxAt(2, 0);
        boxAt(1, 0);

        hero(0).act();
        hero(0).up();
        hero(1).act();
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

        asrtBrd("     \n" +
                "☺  ♥ \n" +
                "҉  ҉ \n" +
                "҉  ҉ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                        "listener(1) => [KILL_TREASURE_BOX]\n");


        dice(dice, // новые коробки
                4, 4,
                4, 3);
        tick();

        asrtBrd("    #\n" +
                "☺  ♥#\n" +
                "     \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseDied() {
        dice(dice,
                0, 0,
                2, 0);
        givenBr(2);

        ghostAt(1, 0);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                        "listener(1) => [DIED, KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseAlive() {
        dice(dice,
                0, 0,
                2, 0);
        givenBr(2);

        ghostAt(1, 0);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                        "listener(1) => [KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourGhosts_caseDied() {
        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBr(4);

        ghostAt(2, 2);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "҉Ѡx♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                        "listener(1) => [DIED, KILL_GHOST]\n" +
                        "listener(2) => [DIED, KILL_GHOST]\n" +
                        "listener(3) => [DIED, KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBr(4);

        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                        "&244&\n" +
                        " 1#3 \n" +
                        "&223&\n" +
                        "☺♥♥♥ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                        "&24♠&\n" +
                        " 1#3 \n" +
                        "&♠2♠&\n" +
                        "☺    \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                        "listener(1) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "x҉҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                "☺҉҉҉ \n", game(0));

        boxesCount(0); // больше не надо коробок
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("     \n" +
                "   ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "☺    \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_withEnemies() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBr(4);

        ghostsCount(4);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        boxesCount(1);
        boxAt(2, 2);

        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);
        player(3).inTeam(1);

        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                        "&244&\n" +
                        " 1#3 \n" +
                        "&223&\n" +
                        "☺♥♡♡ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                        "&24♤&\n" +
                        " 1#3 \n" +
                        "&♠2♤&\n" +
                        "☺    \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                        "listener(1) => [DIED, KILL_ENEMY_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "x҉҉♧x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♧x\n" +
                "☺҉҉҉ \n", game(0));

        boxesCount(0); // больше не надо коробок
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("     \n" +
                "   ♧ \n" +
                "     \n" +
                " ♣ ♧ \n" +
                "☺    \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_caseKillAll() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBr(4);

        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                        "&244&\n" +
                        " 1#3 \n" +
                        "&223&\n" +
                        "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                        "&☻4♠&\n" +
                        " 1#3 \n" +
                        "&♠2♠&\n" +
                        "     \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                        "listener(1) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "xѠ҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                " ҉҉҉ \n", game(0));

        dice(dice, // новые коробки
                4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenNotBigBadaboom_caseKillAll() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, false)
                .perksSettings().dropRatio(0);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBr(4);

        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                        "&244&\n" +
                        " 1#3 \n" +
                        "&223&\n" +
                        "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                        "&☻4♠&\n" +
                        " 1#3 \n" +
                        "&♠2♠&\n" +
                        "     \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED]\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("     \n" +
                "&Ѡ3♠&\n" +
                "҉҉H2 \n" +
                "&11♠&\n" +
                "     \n", game(0));

        dice(dice, // новые коробки
                4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                        "listener(1) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                        "listener(2) => [DIED]\n" +
                        "listener(3) => []\n");

        asrtBrd(" ҉  #\n" +
                "xѠ2♠&\n" +
                " ҉҉1 \n" +
                "x♣҉1&\n" +
                " ҉҉  \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                        "listener(3) => [DIED]\n");

        asrtBrd("    #\n" +
                " Ѡ11&\n" +
                "  ҉҉҉\n" +
                " ♣҉♣x\n" +
                "   ҉ \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => [KILL_GHOST]\n");

        asrtBrd("  ҉҉#\n" +
                " Ѡ҉♣x\n" +
                "  ҉҉ \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }
}
