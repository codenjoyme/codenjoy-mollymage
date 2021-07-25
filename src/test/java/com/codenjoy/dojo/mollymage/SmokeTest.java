package com.codenjoy.dojo.mollymage;

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


import com.codenjoy.dojo.games.mollymage.Board;
import com.codenjoy.dojo.mollymage.services.ai.AIPerksHunterSolver;
import com.codenjoy.dojo.mollymage.services.ai.AISolver;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.services.GameRunner;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.client.local.LocalGameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.utils.Smoke;
import org.junit.Test;

import java.util.Arrays;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static org.junit.Assert.assertEquals;

public class SmokeTest {

    @Test
    public void test() {
        Dice dice = LocalGameRunner.getDice("435874345435874365843564398", 100, 200);

        // about 3 sec
        // coverage 881 lines from 1071
        int ticks = 1000;

        Smoke.play(ticks, "SmokeTest.data",
                new GameRunner() {
                    @Override
                    public Dice getDice() {
                        return dice;
                    }

                    @Override
                    public GameSettings getSettings() {
                        GameSettings settings = new TestGameSettings()
                                        .integer(TREASURE_BOX_COUNT, 14)
                                .integer(GHOSTS_COUNT, 3)
                                .string(LEVEL_MAP,
                                        "☼☼☼☼☼☼☼☼☼☼☼\n" +
                                        "☼         ☼\n" +
                                        "☼ ☼ ☼ ☼ ☼ ☼\n" +
                                        "☼         ☼\n" +
                                        "☼ ☼ ☼ ☼ ☼ ☼\n" +
                                        "☼         ☼\n" +
                                        "☼ ☼ ☼ ☼ ☼ ☼\n" +
                                        "☼         ☼\n" +
                                        "☼ ☼ ☼ ☼ ☼ ☼\n" +
                                        "☼         ☼\n" +
                                        "☼☼☼☼☼☼☼☼☼☼☼\n");

                        settings.perksSettings()
                                .dropRatio(70)
                                .pickTimeout(16)
                                .put(Element.POTION_BLAST_RADIUS_INCREASE, 5, 10)
                                .put(Element.POTION_COUNT_INCREASE, 5, 3)
                                .put(Element.POTION_REMOTE_CONTROL, 5, 10)
                                .put(Element.POTION_IMMUNE, 5, 10);

                        return settings;
                    }
                },
                Arrays.asList(new AISolver(dice), new AIPerksHunterSolver(dice)),
                Arrays.asList(new Board(), new Board()));
    }
}