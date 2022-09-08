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
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.event.ScoresImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static org.junit.Assert.assertEquals;

public class ScoresTest {

    private PlayerScores scores;
    private GameSettings settings;

    public void killWall() {
        scores.event(Event.KILL_TREASURE_BOX);
    }

    public void killYourself() {
        scores.event(Event.HERO_DIED);
    }

    public void killGhost() {
        scores.event(Event.KILL_GHOST);
    }

    public void killOtherHero() {
        scores.event(Event.KILL_OTHER_HERO);
    }

    public void killEnemyHero() {
        scores.event(Event.KILL_ENEMY_HERO);
    }

    public void dropPerk() {
        scores.event(Event.CATCH_PERK);
    }

    public void winRound() {
        scores.event(Event.WIN_ROUND);
    }

    @Before
    public void setup() {
        settings = new TestGameSettings();
        givenScores(0);
    }

    @Test
    public void shouldCollectScores() {
        // given
        givenScores(140);

        // when
        killWall();
        killWall();
        killWall();
        killWall();

        killYourself();

        killGhost();

        killOtherHero();
        killEnemyHero();

        dropPerk();

        winRound();

        // then
        assertEquals(140
                    + 4 * settings.integer(OPEN_TREASURE_BOX_SCORE)
                    + settings.integer(HERO_DIED_PENALTY)
                    + settings.integer(CATCH_PERK_SCORE)
                    + settings.integer(KILL_OTHER_HERO_SCORE)
                    + settings.integer(KILL_ENEMY_HERO_SCORE)
                    + settings.integer(KILL_GHOST_SCORE)
                    + settings.integer(WIN_ROUND_SCORE),
                scores.getScore());
    }

    private void givenScores(int score) {
        scores = new ScoresImpl<>(score, new Calculator<>(new Scores(settings)));
    }

    @Test
    public void shouldStillZeroAfterDead() {
        // given
        givenScores(0);

        // when
        killYourself();

        // then
        assertEquals(0, scores.getScore());
    }

    @Test
    public void shouldClearScore() {
        // given
        givenScores(0);

        killWall();

        // when
        scores.clear();

        // then
        assertEquals(0, scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenKillWall() {
        // given
        givenScores(140);

        // then
        killWall();
        killWall();

        // then
        assertEquals(140
                    + 2*settings.integer(OPEN_TREASURE_BOX_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenKillYourself() {
        // given
        givenScores(140);

        // when
        killYourself();
        killYourself();

        // then
        assertEquals(140
                    + 2 * settings.integer(HERO_DIED_PENALTY),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenKillGhost() {
        // given
        givenScores(140);

        // when
        killGhost();
        killGhost();

        // then
        assertEquals(140
                    + 2 * settings.integer(KILL_GHOST_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenKillOtherHero() {
        // given
        givenScores(140);

        // when
        killOtherHero();
        killOtherHero();

        // then
        assertEquals(140
                    + 2 * settings.integer(KILL_OTHER_HERO_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenKillEnemyHero() {
        // given
        givenScores(140);

        // when
        killEnemyHero();
        killEnemyHero();

        // then
        assertEquals(140
                        + 2 * settings.integer(KILL_ENEMY_HERO_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenDropPerk() {
        // given
        givenScores(140);

        // when
        dropPerk();
        dropPerk();

        // then
        assertEquals(140
                    + 2 * settings.integer(CATCH_PERK_SCORE),
                scores.getScore());
    }

    @Test
    public void shouldCollectScores_whenWinRound() {
        // given
        givenScores(140);

        // when
        winRound();
        winRound();

        // then
        assertEquals(140
                    + 2 * settings.integer(WIN_ROUND_SCORE),
                scores.getScore());
    }
}