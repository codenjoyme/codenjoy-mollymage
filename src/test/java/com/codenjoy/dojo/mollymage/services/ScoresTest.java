package com.codenjoy.dojo.mollymage.services;

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


import com.codenjoy.dojo.services.PlayerScores;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static org.junit.Assert.assertEquals;

public class ScoresTest {

    private PlayerScores scores;
    private GameSettings settings;

    public void killWall() {
        scores.event(Events.KILL_TREASURE_BOX);
    }

    public void killYourself() {
        scores.event(Events.DIED);
    }

    public void killGhost() {
        scores.event(Events.KILL_GHOST);
    }

    public void killOtherHero() {
        scores.event(Events.KILL_OTHER_HERO);
    }

    public void killEnemyHero() {
        scores.event(Events.KILL_ENEMY_HERO);
    }

    public void dropPerk() {
        scores.event(Events.CATCH_PERK);
    }

    public void winRound() {
        scores.event(Events.WIN_ROUND);
    }

    @Before
    public void setup() {
        settings = new GameSettings();
        scores = new Scores(0, settings);
    }

    @Test
    public void shouldCollectScores() {
        scores = new Scores(140, settings);

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

        assertEquals(140
                + 4*settings.integer(KILL_WALL_SCORE)
                - settings.integer(DIE_PENALTY)
                + settings.integer(CATCH_PERK_SCORE)
                + settings.integer(KILL_OTHER_HERO_SCORE)
                + settings.integer(KILL_ENEMY_HERO_SCORE)
                + settings.integer(KILL_GHOST_SCORE)
                + settings.integer(WIN_ROUND_SCORE), scores.getScore());
    }

    @Test
    public void shouldStillZeroAfterDead() {
        killYourself();

        assertEquals(0, scores.getScore());
    }

    @Test
    public void shouldClearScore() {
        killWall();

        scores.clear();

        assertEquals(0, scores.getScore());
    }
}