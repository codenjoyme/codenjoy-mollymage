package com.codenjoy.dojo.mollymage.services;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
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
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.services.event.Calculator;
import com.codenjoy.dojo.services.settings.AllSettings;
import com.codenjoy.dojo.services.settings.PropertiesKey;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_TEAMS_PER_ROOM;

public class GameSettings extends SettingsImpl implements AllSettings<GameSettings> {

    public enum Keys implements PropertiesKey {

        OPEN_TREASURE_BOX_SCORE,
        KILL_GHOST_SCORE,
        KILL_OTHER_HERO_SCORE,
        KILL_ENEMY_HERO_SCORE,
        CATCH_PERK_SCORE,
        HERO_DIED_PENALTY,
        WIN_ROUND_SCORE,
        BIG_BADABOOM,
        POTIONS_COUNT,
        POTION_POWER,
        TREASURE_BOX_COUNT,
        GHOSTS_COUNT,
        PERK_WHOLE_TEAM_GET,
        PERK_DROP_RATIO,
        PERK_PICK_TIMEOUT,
        PERK_POTION_BLAST_RADIUS_INC,
        TIMEOUT_POTION_BLAST_RADIUS_INC,
        PERK_POTION_COUNT_INC,
        TIMEOUT_POTION_COUNT_INC,
        TIMEOUT_POTION_IMMUNE,
        TIMEOUT_POISON_THROWER,
        TIMEOUT_POTION_EXPLODER,
        POISON_THROWER_RECHARGE,
        REMOTE_CONTROL_COUNT,
        POTION_EXPLODER_COUNT,
        STEAL_POINTS,
        DEFAULT_PERKS,
        SCORE_COUNTING_TYPE;

        private String key;

        Keys() {
            this.key = key(GameRunner.GAME_NAME);
        }

        @Override
        public String key() {
            return key;
        }
    }

    @Override
    public List<Key> allKeys() {
        return Arrays.asList(Keys.values());
    }

    public GameSettings() {
        initAll();

        integer(OPEN_TREASURE_BOX_SCORE, 1);
        integer(KILL_GHOST_SCORE, 10);
        integer(KILL_OTHER_HERO_SCORE, 20);
        integer(KILL_ENEMY_HERO_SCORE, 100);
        integer(CATCH_PERK_SCORE, 5);
        integer(HERO_DIED_PENALTY, -30);
        integer(WIN_ROUND_SCORE, 30);

        bool(BIG_BADABOOM, false);
        integer(POTIONS_COUNT, 1);
        integer(POTION_POWER, 3);
        integer(TREASURE_BOX_COUNT, 52);
        integer(GHOSTS_COUNT, 5);
        integer(POISON_THROWER_RECHARGE, 3);

        bool(PERK_WHOLE_TEAM_GET, false);
        string(DEFAULT_PERKS, StringUtils.EMPTY);
        bool(STEAL_POINTS, false);
        PerksSettingsWrapper perks =
                perksSettings()
                    .dropRatio(20) // Set value to 0% = perks is disabled.
                    .pickTimeout(30);
        int timeout = 30;
        perks.put(Element.POTION_REMOTE_CONTROL, 3, 1);
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 2, timeout);
        perks.put(Element.POTION_IMMUNE, 0, timeout);
        perks.put(Element.POTION_COUNT_INCREASE, 4, timeout);
        perks.put(Element.POISON_THROWER, 0, timeout);
        perks.put(Element.POTION_EXPLODER, 1, timeout);

        Levels.setup(this);
    }

    public PerksSettingsWrapper perksSettings() {
        return new PerksSettingsWrapper(this);
    }

    public boolean isTeamDeathMatch() {
        return integer(ROUNDS_TEAMS_PER_ROOM) > 1;
    }

    public Calculator<Void> calculator() {
        return new Calculator<>(new Scores(this));
    }
}
