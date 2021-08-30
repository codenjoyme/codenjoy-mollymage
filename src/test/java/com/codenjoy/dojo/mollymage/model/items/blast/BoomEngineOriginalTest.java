package com.codenjoy.dojo.mollymage.model.items.blast;

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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.model.Field;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.MollyMage;
import com.codenjoy.dojo.mollymage.model.Player;
import com.codenjoy.dojo.mollymage.model.levels.Level;
import com.codenjoy.dojo.mollymage.model.levels.LevelImpl;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BoomEngineOriginalTest {

    private BoomEngine engine;
    private Poison poison;
    private Level level;
    private PrinterFactory printerFactory;
    private Field field;

    private void givenBr(String board) {
        level = new LevelImpl(board);
        field = new MollyMage(level, mock(Dice.class), new TestGameSettings());
        engine = new BoomEngineOriginal(field, null);
        printerFactory = new PrinterFactoryImpl();
    }

    @Test
    public void testOneBarrier() {
        givenBr("           \n" +
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
        givenBr("            \n" +
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
        int countBlasts = 2*radius + 2 + 2 + 1;

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
        givenBr("          \n" +
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
        int countBlasts = 3*radius + 1 + 3;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = 4*radius + 1;

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
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = 2*radius + 1;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = 2*radius + 1;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = 2*radius + 1;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = 2 * (level.size() - 2) - 1;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = level.size() - 2;

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
        givenBr("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        int countBlasts = level.size() - 2;

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
        givenBr("☼      ☼\n" +
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

        prepareDateForPoisonTests(source, LEFT, range);

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
        givenBr("☼     ☼\n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                " ☼  ☻  \n" +
                "☼     ☼\n");

        Point source = pt(4, 1);
        int range = 4;
        int countBlasts = 4;

        prepareDateForPoisonTests(source, UP, range);

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
        givenBr("☼           ☼\n" +
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

        prepareDateForPoisonTests(source, RIGHT, range);

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
        givenBr("☼     ☼\n" +
                " ☼ ☻   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "   ҉   \n" +
                "☼     ☼\n");

        Point source = pt(3, 5);
        int range = 4;
        int countBlasts = 4;

        prepareDateForPoisonTests(source, DOWN, range);

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
        givenBr("☼       ☼\n" +
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

        prepareDateForPoisonTests(source, LEFT, range);

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

    private void prepareDateForPoisonTests(Point source, Direction direction, int range) {
        Hero hero = new Hero();
        engine = new BoomEngineOriginal(field, hero);
        hero.setX(source.getX());
        hero.setY(source.getY());
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
        Printer<String> printer = printerFactory.getPrinter(new BoardReader<Player>() {
            @Override
            public int size() {
                return level.size();
            }

            class B extends PointImpl implements State<Element, Object> {

                public B(Point point) {
                    super(point);
                }

                @Override
                public Element state(Object player, Object... alsoAtPoint) {
                    return Element.POTION_HERO;
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
        return printer.print();
    }

}
