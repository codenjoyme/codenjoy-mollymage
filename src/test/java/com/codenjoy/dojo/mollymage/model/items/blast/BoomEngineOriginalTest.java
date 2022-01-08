package com.codenjoy.dojo.mollymage.model.items.blast;

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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.services.printer.state.State;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.games.mollymage.Element.HERO_POTION;
import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BoomEngineOriginalTest {

    private BoomEngine engine;
    private Poison poison;
    private PrinterFactory<Element, Player> printer;
    private Field field;
    private TestGameSettings settings;
    private Dice dice;

    @Before
    public void setup() {
        settings = settings();
        dice = mock(Dice.class);
        printer = new PrinterFactoryImpl<>();
    }

    private void givenFl(String map) {
        int levelNumber = LevelProgress.levelsStartsFrom1;
        settings.setLevelMaps(levelNumber, map);
        Level level = settings.level(levelNumber, dice, Level::new);

        field = new MollyMage(dice, level, settings);
        engine = new BoomEngineOriginal(field, null);
    }

    private TestGameSettings settings() {
        return new TestGameSettings();
    }

    @Test
    public void testOneBarrier() {
        // given
        givenFl("           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "  ☼☼       \n" +
                "  ☼☼       \n" +
                "           \n" +
                "   ☻       \n");

        Point source = pt(3, 0);
        int radius = 7;
        int countBlasts = radius + 1 + 1 + 3;

        // when then
        assertBoom(source, radius, countBlasts,
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "           \n" +
                "  ☼☼       \n" +
                "  ☼☼       \n" +
                "   ҉       \n" +
                "҉҉҉☻҉҉҉҉҉҉҉\n");
    }

    @Test
    public void testOneBarrierAtCenter() {
        // given
        givenFl("            \n" +
                "            \n" +
                "            \n" +
                "            \n" +
                "            \n" +
                "            \n" +
                "          ☼☼\n" +
                "       ☻  ☼☼\n" +
                "            \n" +
                "            \n" +
                "      ☼☼    \n" +
                "      ☼☼    \n");

        Point source = pt(7, 4);
        int radius = 7;
        int countBlasts = 2 * radius + 2 + 2 + 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "       ҉    \n" +
                "       ҉    \n" +
                "       ҉    \n" +
                "       ҉    \n" +
                "       ҉    \n" +
                "       ҉    \n" +
                "       ҉  ☼☼\n" +
                "҉҉҉҉҉҉҉☻҉҉☼☼\n" +
                "       ҉    \n" +
                "       ҉    \n" +
                "      ☼☼    \n" +
                "      ☼☼    \n");
    }

    @Test
    public void testOneBarrier2() {
        // given
        givenFl("          \n" +
                "          \n" +
                "          \n" +
                "          \n" +
                "          \n" +
                "☼☼   ☻    \n" +
                "☼☼        \n" +
                "          \n" +
                "          \n" +
                "          \n");

        Point source = pt(5, 4);
        int radius = 4;
        int countBlasts = 3 * radius + 1 + 3;

        // when then
        assertBoom(source, radius, countBlasts,
                "          \n" +
                "     ҉    \n" +
                "     ҉    \n" +
                "     ҉    \n" +
                "     ҉    \n" +
                "☼☼҉҉҉☻҉҉҉҉\n" +
                "☼☼   ҉    \n" +
                "     ҉    \n" +
                "     ҉    \n" +
                "     ҉    \n");
    }

    @Test
    public void testBigBoomAtClassicWalls() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "           \n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼    ☻    ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "           \n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(5, 5);
        int radius = 3;
        int countBlasts = 4 * radius + 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "     ҉     \n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "☼ ҉҉҉☻҉҉҉ ☼\n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "     ҉     \n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls2() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼   ☻   ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(4, 5);
        int radius = 3;
        int countBlasts = 2 * radius + 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼҉҉҉☻҉҉҉☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls3() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼☻☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(5, 5);
        int radius = 3;
        int countBlasts = 2 * radius + 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼    ҉    ☼\n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "☼    ҉    ☼\n" +
                "☼ ☼ ☼☻☼ ☼ ☼\n" +
                "☼    ҉    ☼\n" +
                "☼ ☼ ☼҉☼ ☼ ☼\n" +
                "☼    ҉    ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls4() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                 ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☻                ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(1, 1);
        int radius = 15;
        int countBlasts = 2 * radius + 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                 ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉                ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☻҉҉҉҉҉҉҉҉҉҉҉҉҉҉҉ ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls5() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼          ☻        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(11, 11);
        int radius = 15;
        int countBlasts = 2 * (field.size() - 2) - 1;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉҉҉҉҉☻҉҉҉҉҉҉҉҉☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls6() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼           ☻       ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(12, 11);
        int radius = 15;
        int countBlasts = field.size() - 2;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉҉҉҉҉҉☻҉҉҉҉҉҉҉☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testBigBoomAtClassicWalls7() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼☻☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                   ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");

        Point source = pt(11, 12);
        int radius = 15;
        int countBlasts = field.size() - 2;

        // when then
        assertBoom(source, radius, countBlasts,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼☻☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼          ҉        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void testPoisonBoom_1() {
        // given
        givenFl("☼      ☼\n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "☼      ☻\n" +
                "        \n" +
                "        \n" +
                "☼      ☼\n");

        Point source = pt(7, 3);
        int range = 4;
        int countBlasts = 4;

        givenPoison(source, LEFT, range);

        // when then
        assertPoisonBoom(source, countBlasts, poison,
                "☼      ☼\n" +
                "        \n" +
                "        \n" +
                "        \n" +
                "☼  ҉҉҉҉☻\n" +
                "        \n" +
                "        \n" +
                "☼      ☼\n");
    }

    @Test
    public void testPoisonBoom_2() {
        // given
        givenFl("☼     ☼\n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                " ☼  ☻  \n" +
                "☼     ☼\n");

        Point source = pt(4, 1);
        int range = 4;
        int countBlasts = 4;

        givenPoison(source, UP, range);

        // when then
        assertPoisonBoom(source, countBlasts, poison,
                "☼     ☼\n" +
                "    ҉  \n" +
                "    ҉  \n" +
                "    ҉  \n" +
                "    ҉  \n" +
                " ☼  ☻  \n" +
                "☼     ☼\n");
    }

    @Test
    public void testPoisonBoom_3() {
        // given
        givenFl("☼           ☼\n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                " ☼      ☻    \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "☼           ☼\n");

        Point source = pt(8, 6);
        int range = 4;
        int countBlasts = 4;

        givenPoison(source, RIGHT, range);

        // when then
        assertPoisonBoom(source, countBlasts, poison,
                "☼           ☼\n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                " ☼      ☻҉҉҉҉\n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "             \n" +
                "☼           ☼\n");
    }

    @Test
    public void testPoisonBoom_4() {
        // given
        givenFl("☼     ☼\n" +
                " ☼ ☻   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "☼     ☼\n");

        Point source = pt(3, 5);
        int range = 4;
        int countBlasts = 4;

        givenPoison(source, DOWN, range);

        // when then
        assertPoisonBoom(source, countBlasts, poison,
                "☼     ☼\n" +
                " ☼ ☻   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "☼     ☼\n");
    }

    @Test
    public void testPoisonBoomAtWalls_WallShouldStopBlast() {
        // given
        givenFl("☼       ☼\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                " ☼      ☻\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "☼       ☼\n");

        Point source = pt(8, 4);
        int range = 8;
        int countBlasts = 6;

        givenPoison(source, LEFT, range);

        // when then
        assertPoisonBoom(source, countBlasts, poison,
                "☼       ☼\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                " ☼҉҉҉҉҉҉☻\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "☼       ☼\n");
    }

    private void givenPoison(Point pt, Direction direction, int range) {
        Hero hero = new Hero();
        engine = new BoomEngineOriginal(field, hero);

        hero.move(pt);
        poison = new Poison(hero, direction, range);
    }

    private void assertBoom(Point source, int radius, int countBlasts, String expected) {
        List<Blast> blasts = engine.boom(source, radius);
        assertEquals(countBlasts, blasts.size());

        String actual = print(blasts, source);
        assertEquals(expected, actual);
    }

    private void assertPoisonBoom(Point source, int countBlasts, Poison poison, String expected) {
        List<Blast> blasts = engine.boom(poison);
        assertEquals(countBlasts, blasts.size());

        String actual = print(blasts, source);
        assertEquals(expected, actual);
    }

    public String print(List<Blast> blast, Point source) {
        Printer<?> printer = this.printer.getPrinter(new BoardReader<Player>() {
            @Override
            public int size() {
                return field.size();
            }

            class B extends PointImpl implements State<Element, Player> {

                public B(Point point) {
                    super(point);
                }

                @Override
                public Element state(Player player, Object... alsoAtPoint) {
                    return HERO_POTION;
                }
            }

            @Override
            public void addAll(Player player, Consumer<Iterable<? extends Point>> processor) {
                processor.accept(new LinkedList<>() {{
                    addAll(field.walls().all());
                    add(new B(source));
                    addAll(blast);
                }});
            }

        }, null);

        return printer.print().toString();
    }

}
