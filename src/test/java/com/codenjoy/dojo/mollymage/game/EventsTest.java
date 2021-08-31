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
        givenFl("     \n" +
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

        hero().act();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        field.tick();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n");

        verify(listener()).event(Events.KILL_TREASURE_BOX);
    }

    @Test
    public void shouldFireEventWhenKillGhost() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "&☺   \n");

        hero().act();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        field.tick();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n");

        verify(listener()).event(Events.KILL_GHOST);
    }

    @Test
    public void shouldCalculateGhostsAndWallKills() {
        givenFl("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        canDropPotions(4);
        potionsPower(1);

        assertF("     \n" +
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

        assertF(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        hero().right();
        field.tick();

        assertF("  ☺  \n" +
                "#3   \n" +
                "&2   \n" +
                "#1   \n" +
                "x҉҉  \n");

        events.verifyAllEvents("[KILL_GHOST]");

        // новое привидение, стоит не двигается
        dice(3, 4, Direction.ACT.value());
        field.tick();
        field.ghosts().stream().forEach(Ghost::stop); // и не будет больше двигаться

        assertF("  ☺& \n" +
                "#2   \n" +
                "&1   \n" +
                "H҉҉  \n" +
                " ҉   \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        // новая коробка
        dice(4, 4);
        field.tick();

        assertF("  ☺&#\n" +
                "#1   \n" +
                "x҉҉  \n" +
                " ҉   \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST]");

        // новое привидение, стоит не двигается
        dice(3, 3, Direction.ACT.value());
        field.tick();
        field.ghosts().stream().forEach(Ghost::stop); // и не будет больше двигаться

        assertF(" ҉☺&#\n" +
                "H҉҉& \n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_TREASURE_BOX]");

        // новая коробка
        dice(4, 3);
        hero().left();
        field.tick();

        hero().down();
        hero().act();
        field.tick();

        assertF("   &#\n" +
                " ☻ &#\n" +
                "     \n" +
                "     \n" +
                "     \n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        assertF(" ҉ &#\n" +
                "҉Ѡ҉&#\n" +
                " ҉   \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        dice(1, 1);
        field.tick();
        game().newGame();

        assertF("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ☺   \n" +
                "     \n");

        heroes.set(0, (Hero) game().getJoystick());
        hero().move(pt(1, 0));

        assertF("   &#\n" +
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

        assertF("   &#\n" +
                "   &#\n" +
                "     \n" +
                " ҉   \n" +
                "҉҉҉☺ \n");
    }

    @Test
    public void shouldCalculateGhostsAndWallKills_caseBigBadaboom() {
        settings.bool(BIG_BADABOOM, true);

        givenFl("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        canDropPotions(4);
        potionsPower(1);

        assertF("     \n" +
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

        assertF(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");

        events.verifyAllEvents("[]");

        hero().right();
        field.tick();

        assertF(" ҉☺  \n" +
                "H҉҉  \n" +
                "x҉҉  \n" +
                "H҉҉  \n" +
                "x҉҉  \n");

        // новые координаты коробок
        dice(2, 2, 3, 3);
        field.tick();

        assertF("  ☺  \n" +
                "   # \n" +
                "  #  \n" +
                "     \n" +
                "     \n");

        events.verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_GHOST, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldGhostNotAppearOnThePlaceWhereItDie_AfterKill() {
        potionsPower(3);

        givenFl("   \n" +
                "   \n" +
                "☺ &\n");

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
        assertF("҉  \n" +
                "҉☺ \n" +
                "҉҉x\n");

        // when fill free places boxes
        boxesCount(6);
        dice(preparedCoordinatesForBoxesAndGhosts());
        field.tick();

        // then boxes fill whole field except 2 free points([2,2] and [0,2]).
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
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "#☺   \n");

        hero(1).act();
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

        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ♥\n", game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        // новые коробки
        dice(4, 4);
        tick();

        assertF(" ☺  #\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ♥\n", game(0));
    }

    @Test
    public void shouldFireEventWhenKillGhostMultiplayer() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "&☺   \n");

        hero(1).act();
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

        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ♥\n", game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [KILL_GHOST]\n");

        tick();

        assertF(" ☺   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ♥\n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseDied() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#☺  \n");

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

        // новые коробки
        dice(4, 4);
        tick();

        assertF("    #\n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#☺  \n");

        assertF("     \n" +
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

        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "3#3  \n", game(0));

        tick();
        tick();
        tick();

        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        // новая коробка
        dice(4, 4);
        tick();

        assertF("    #\n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseDied() {
        settings.integer(POTION_POWER, 2);
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺##☺ \n");

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

        // новые коробки
        dice(4, 4, 4, 3);
        tick();

        assertF("    #\n" +
                "    #\n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied() {
        givenFl("     \n" +
                "  ☺  \n" +
                " ☺#☺ \n" +
                "  ☺  \n" +
                "     \n");

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "҉♣H♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_TREASURE_BOX]\n");

        // новые коробки
        dice(4, 4);
        tick();

        assertF("    #\n" +
                "  Ѡ  \n" +
                " ♣ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied_caseNotEqualPosition() {
        givenFl("     \n" +
                "  ☺  \n" +
                "#☺#☺ \n" +
                " #☺  \n" +
                "     \n");

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "H♣H♣҉\n" +  // первую стенку подбил монополист, центральную все
                " H♣҉ \n" +  // эту стенку подбили только двое
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n");

        // новые коробки
        dice(4, 4, 4, 3, 4, 2);
        tick();

        assertF("    #\n" +
                "  Ѡ #\n" +
                " ♣ ♣#\n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {
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
                "☺##♥ \n", game(0));

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

        assertF("     \n" +
                "☺  ♥ \n" +
                "҉  ҉ \n" +
                "҉  ҉ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");


        // новые коробки
        dice(4, 4, 4, 3);
        tick();

        assertF("    #\n" +
                "☺  ♥#\n" +
                "     \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseDied() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&☺  \n");

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_GHOST]\n");

        ghostsCount(0); // больще не надо привидений
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseAlive() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&☺  \n");

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

        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
        
        ghostsCount(0); // чтобы новый не появлялся
        tick();

        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourGhosts_caseDied() {
        givenFl("     \n" +
                "  ☺  \n" +
                " ☺&☺ \n" +
                "  ☺  \n" +
                "     \n");

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        assertF("  ҉  \n" +
                " ҉Ѡ҉ \n" +
                "҉♣x♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_GHOST]\n" +
                "listener(2) => [DIED, KILL_GHOST]\n" +
                "listener(3) => [DIED, KILL_GHOST]\n");

        ghostsCount(0); // чтобы новый не появлялся
        tick();

        assertF("     \n" +
                "  Ѡ  \n" +
                " ♣ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom() {
        canDropPotions(2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

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

        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        assertF("     \n" +
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

        assertF(" ҉҉҉ \n" +
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

        assertF("     \n" +
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

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

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

        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♡♡ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        assertF("     \n" +
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

        assertF(" ҉҉҉ \n" +
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

        assertF("     \n" +
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

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

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

        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        assertF("     \n" +
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

        assertF(" ҉҉҉ \n" +
                "xѠ҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                " ҉҉҉ \n", game(0));

        // новые коробки
        dice(4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        assertF("    #\n" +
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

        givenFl("     \n" +
                "&   &\n" +
                "  #  \n" +
                "&   &\n" +
                "☺☺☺☺ \n");

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

        assertF("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        assertF("     \n" +
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

        assertF("     \n" +
                "&Ѡ3♠&\n" +
                "҉҉H2 \n" +
                "&11♠&\n" +
                "     \n", game(0));

        // новые коробки
        dice(4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(2) => [DIED]\n" +
                "listener(3) => []\n");

        assertF(" ҉  #\n" +
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

        assertF("    #\n" +
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

        assertF("  ҉҉#\n" +
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

        assertF("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }
}
