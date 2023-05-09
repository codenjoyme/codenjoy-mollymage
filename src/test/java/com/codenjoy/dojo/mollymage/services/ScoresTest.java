package com.codenjoy.dojo.mollymage.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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
import com.codenjoy.dojo.services.event.ScoresMap;
import com.codenjoy.dojo.utils.scorestest.AbstractScoresTest;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;

public class ScoresTest  extends AbstractScoresTest {

    @Override
    public GameSettings settings() {
        return new TestGameSettings();
    }

    @Override
    protected Class<? extends ScoresMap> scores() {
        return Scores.class;
    }

    @Override
    protected Class<? extends Enum> eventTypes() {
        return Event.class;
    }

    @Test
    public void shouldCollectScores() {
        assertEvents("100:\n" +
                "KILL_TREASURE_BOX > +10 = 110\n" +
                "KILL_TREASURE_BOX > +10 = 120\n" +
                "KILL_TREASURE_BOX > +10 = 130\n" +
                "KILL_TREASURE_BOX > +10 = 140\n" +
                "HERO_DIED > -50 = 90\n" +
                "KILL_GHOST > +100 = 190\n" +
                "KILL_OTHER_HERO > +200 = 390\n" +
                "KILL_ENEMY_HERO > +500 = 890\n" +
                "DROP_PERK > +0 = 890\n" +
                "CATCH_PERK > +5 = 895\n" +
                "WIN_ROUND > +1000 = 1895");
    }

    @Test
    public void shouldNotBeLessThanZero() {
        assertEvents("100:\n" +
                "HERO_DIED > -50 = 50\n" +
                "HERO_DIED > -50 = 0\n" +
                "HERO_DIED > +0 = 0");
    }

    @Test
    public void shouldCleanScore() {
        assertEvents("100:\n" +
                "KILL_TREASURE_BOX > +10 = 110\n" +
                "(CLEAN) > -110 = 0\n" +
                "KILL_TREASURE_BOX > +10 = 10");
    }

    @Test
    public void shouldCollectScores_whenKillWall() {
        // given
        settings.integer(OPEN_TREASURE_BOX_SCORE, 10);

        // when then
        assertEvents("100:\n" +
                "KILL_TREASURE_BOX > +10 = 110\n" +
                "KILL_TREASURE_BOX > +10 = 120");
    }

    @Test
    public void shouldCollectScores_whenKillYourself() {
        // given
        settings.integer(HERO_DIED_PENALTY, -50);

        // when then
        assertEvents("100:\n" +
                "HERO_DIED > -50 = 50\n" +
                "HERO_DIED > -50 = 0");
    }

    @Test
    public void shouldCollectScores_whenKillGhost() {
        // given
        settings.integer(KILL_GHOST_SCORE, 100);

        // when then
        assertEvents("100:\n" +
                "KILL_GHOST > +100 = 200\n" +
                "KILL_GHOST > +100 = 300");
    }

    @Test
    public void shouldCollectScores_whenKillOtherHero() {
        // given
        settings.integer(KILL_OTHER_HERO_SCORE, 200);

        // when then
        assertEvents("100:\n" +
                "KILL_OTHER_HERO > +200 = 300\n" +
                "KILL_OTHER_HERO > +200 = 500");
    }

    @Test
    public void shouldCollectScores_whenKillEnemyHero() {
        // given
        settings.integer(KILL_ENEMY_HERO_SCORE, 200);

        // when then
        assertEvents("100:\n" +
                "KILL_ENEMY_HERO > +200 = 300\n" +
                "KILL_ENEMY_HERO > +200 = 500");
    }

    @Test
    public void shouldCollectScores_whenDropPerk() {
        // given
        settings.integer(CATCH_PERK_SCORE, 200);

        // when then
        assertEvents("100:\n" +
                "CATCH_PERK > +200 = 300\n" +
                "CATCH_PERK > +200 = 500");
    }

    @Test
    public void shouldCollectScores_whenWinRound() {
        // given
        settings.integer(WIN_ROUND_SCORE, 1000);

        // when then
        assertEvents("100:\n" +
                "WIN_ROUND > +1000 = 1100\n" +
                "WIN_ROUND > +1000 = 2100");
    }
}