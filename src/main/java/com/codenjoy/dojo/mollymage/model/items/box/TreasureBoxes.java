package com.codenjoy.dojo.mollymage.model.items.box;

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


import com.codenjoy.dojo.mollymage.model.Field;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.Tickable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.mollymage.model.Field.FOR_HERO;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;

public class TreasureBoxes implements Tickable { // TODO протестить класс

    public static final int MAX = 1000;

    private Dice dice;
    protected Field field;
    private List<TreasureBox> boxes;
    private GameSettings settings;

    public TreasureBoxes(GameSettings settings, Dice dice) {
        this.settings = settings;
        this.dice = dice;
        boxes = new LinkedList<>();
    }

    private int freeSpaces() {
        return (field.size()* field.size() - 1) // TODO -1 это один герой, а если их несколько?
                - field.walls().all().size();
    }

    @Override
    public void tick() {
        regenerate();
    }

    public void regenerate() {
        if (settings.integer(TREASURE_BOX_COUNT) < 0) {
            settings.integer(TREASURE_BOX_COUNT, 0);
        }

        int expected = settings.integer(TREASURE_BOX_COUNT);
        int actual = boxes.size();
        int delta = expected - actual;
        if (delta > freeSpaces()) {  // TODO и это потестить
            settings.integer(TREASURE_BOX_COUNT, expected - (delta - freeSpaces()) - 50); // 50 это место под героев
        }

        if (actual > expected) { // TODO и удаление лишних
            for (int i = 0; i < (actual - expected); i++) {
                boxes.remove(0);
            }
            return;
        }

        int iteration = 0;
        Set<Point> checked = new HashSet<>();
        while (actual < expected && iteration++ < MAX) {  // TODO и это
            Point pt = PointImpl.random(dice, field.size());

            if (checked.contains(pt) || field.isBarrier(pt, !FOR_HERO)) {
                continue;
            }

            boxes.add(new TreasureBox(pt));
            checked.add(pt);
            actual++;
        }

        if (iteration >= MAX) {
            System.out.println("Dead loop at TreasureBoxes.generate!");
        }
    }

    public void init(Field field) {
        this.field = field;
    }

    public List<TreasureBox> all() {
        return boxes;
    }

    public void remove(Point pt) {
        boxes.remove(pt);
    }

    public void add(TreasureBox box) {
        boxes.add(box);
    }

    public void addAll(List<TreasureBox> boxes) {
        this.boxes.addAll(boxes);
    }

    public boolean contains(Point pt) {
        return boxes.contains(pt);
    }
}
