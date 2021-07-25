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


import com.codenjoy.dojo.client.local.LocalGameRunner;
import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBoxes;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghosts;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.model.AbstractGameTest.generate;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.settings.SimpleParameter.v;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectsTest {

    private final static int SIZE = 9;
    private Field field;
    private Objects objects;
    private PrinterFactory factory = new PrinterFactoryImpl();
    private Dice dice = LocalGameRunner.getDice("kgyhfksdfksf", SIZE, 1000);
    private GameSettings settings = new TestGameSettings();

    @Before
    public void setup() {
        field = mock(Field.class);
        when(field.size()).thenReturn(SIZE);
        when(field.objects()).thenAnswer(invocation -> objects);
        when(field.isBarrier(any(Point.class), anyBoolean()))
                .thenAnswer(inv -> objects.itsMe(inv.getArgument(0, Point.class)));
    }

    @Test
    public void testOriginalWalls() {
        assertEquals(
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n",
                print(objects(SIZE)));
    }

    private String print(final Objects walls) {
        Printer<String> printer = factory.getPrinter(new BoardReader<Player>() {
            @Override
            public int size() {
                return SIZE;
            }

            @Override
            public Iterable<? extends Point> elements(Player player) {
                return walls;
            }
        }, null);
        return printer.print();
    }

    @Test
    public void testWalls() {
        assertEquals(
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n",
                print(new ObjectsImpl(settings)));
    }

    @Test
    public void checkPrintDestroyWalls() {
        String actual = getBoardWithDestroyWalls(20);

        assertEquals(
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼# # #  ☼\n" +
                "☼ ☼#☼ ☼#☼\n" +
                "☼## ## #☼\n" +
                "☼#☼ ☼ ☼#☼\n" +
                "☼  ##   ☼\n" +
                "☼ ☼#☼#☼#☼\n" +
                "☼#  ##  ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n", actual);
    }

    @Test
    public void checkPrintGhosts() {
        String actual = givenBoardWithGhosts(10);

        assertEquals(
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼   &  &☼\n" +
                "☼&☼ ☼&☼ ☼\n" +
                "☼ &  &  ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼  &   &☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼  & &  ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n", actual);
    }

    private String givenBoardWithGhosts(int count) {
        settings.integer(GHOSTS_COUNT, count);
        objects = new Ghosts(objects(SIZE), dice);
        objects.init(field);
        objects.tick();
        return print(objects);
    }

    private String getBoardWithDestroyWalls(int count) {
        settings.integer(TREASURE_BOX_COUNT, count);
        objects = new TreasureBoxes(objects(SIZE), dice);
        objects.init(field);
        objects.tick();
        return print(objects);
    }

    private ObjectsImpl objects(int size) {
        return new ObjectsImpl(settings) {{
            for (Point pt : generate(size)) {
                add(pt);
            }
        }};
    }

}
