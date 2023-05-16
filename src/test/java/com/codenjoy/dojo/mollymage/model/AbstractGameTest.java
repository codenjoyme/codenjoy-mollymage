package com.codenjoy.dojo.mollymage.model;

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
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.perks.Perk;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.mollymage.services.Event;
import com.codenjoy.dojo.mollymage.services.GameRunner;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.utils.gametest.NewAbstractBaseGameTest;
import org.junit.After;
import org.junit.Before;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.joining;

public abstract class AbstractGameTest 
        extends NewAbstractBaseGameTest<Player, MollyMage, GameSettings, Level, Hero> {

    protected PerksSettingsWrapper perks;

    @Before
    public void setup() {
        super.setup();
        perks = settings().perksSettings();
    }

    @After
    public void after() {
        super.after();
    }

    @Override
    protected GameType gameType() {
        return new GameRunner();
    }

    protected void afterCreateField() {
        settings().integer(TREASURE_BOX_COUNT, field().boxes().size())
                .integer(GHOSTS_COUNT, field().ghosts().size());

        stopGhosts(); // по умолчанию все привидения стоят на месте
    }

    @Override
    protected GameSettings setupSettings(GameSettings settings) {
        return TestGameSettings.update(settings);
    }

    @Override
    protected Function<String, Level> createLevel() {
        return Level::new;
    }

    @Override
    protected Class<?> eventClass() {
        return Event.class;
    }

    // other methods

    public void newBox(int x, int y) {
        field().boxes().add(new TreasureBox(pt(x, y)));
    }

    public void newPerk(int x, int y, Perk perk) {
        field().perks().add(new PerkOnBoard(new PointImpl(x, y), perk));
    }

    public void removeBoxes(int count) {
        settings().integer(TREASURE_BOX_COUNT, settings().integer(TREASURE_BOX_COUNT) - count);
    }

    public void removeGhosts(int count) {
        settings().integer(GHOSTS_COUNT, settings().integer(GHOSTS_COUNT) - count);
    }

    public Ghost ghost(int x, int y) {
        return field().ghosts().getAt(pt(x, y)).get(0);
    }

    public void stopGhosts() {
        field().ghosts().forEach(Ghost::stop);
    }

    public void assertHeroPerks(String expected) {
        assertEquals(expected,
                hero().getPerks().stream()
                        .sorted(Comparator.comparing(PointImpl::copy))
                        .map(Objects::toString)
                        .collect(joining("\n")));
    }

    public void assertFieldPerks(String expected) {
        assertEquals(expected,
                field().perks().stream()
                        .sorted(Comparator.comparing(PerkOnBoard::copy))
                        .map(Objects::toString)
                        .collect(joining("\n")));
    }
}