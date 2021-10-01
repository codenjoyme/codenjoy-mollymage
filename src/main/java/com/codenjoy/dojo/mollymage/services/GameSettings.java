package com.codenjoy.dojo.mollymage.services;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2020 Codenjoy
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
import com.codenjoy.dojo.mollymage.model.Level;
import com.codenjoy.dojo.services.incativity.InactivitySettings;
import com.codenjoy.dojo.services.round.RoundSettings;
import com.codenjoy.dojo.services.semifinal.SemifinalSettings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_TEAMS_PER_ROOM;

public class GameSettings extends SettingsImpl
        implements SettingsReader<GameSettings>,
                   InactivitySettings<GameSettings>,
                   RoundSettings<GameSettings>,
                   SemifinalSettings<GameSettings> {

    public enum Keys implements Key {

        KILL_WALL_SCORE("[Score] Kill wall score"),
        KILL_GHOST_SCORE("[Score] Kill ghost score"),
        KILL_OTHER_HERO_SCORE("[Score] Kill other hero score"),
        KILL_ENEMY_HERO_SCORE("[Score] Kill enemy hero score"),
        CATCH_PERK_SCORE("[Score] Catch perk score"),
        DIE_PENALTY("[Score] Your hero's death penalty"),
        WIN_ROUND_SCORE("[Score][Rounds] Win round score"),
        BIG_BADABOOM("[Level] Blast activate potion"),
        POTIONS_COUNT("[Level] Potions count"),
        POTION_POWER("[Level] Potion power"),
        TREASURE_BOX_COUNT("[Level] Treasure boxes count"),
        GHOSTS_COUNT("[Level] Ghosts count"),
        PERK_WHOLE_TEAM_GET("[Perks] All teammates get perk"),
        PERK_DROP_RATIO("[Perks] Perks drop ratio in %"),
        PERK_PICK_TIMEOUT("[Perks] Perks pick timeout"),
        PERK_POTION_BLAST_RADIUS_INC("[Perks] Potion blast radius increase"),
        TIMEOUT_POTION_BLAST_RADIUS_INC("[Perks] Potion blast radius increase effect timeout"),
        PERK_POTION_COUNT_INC("[Perks] Potion count increase"),
        TIMEOUT_POTION_COUNT_INC("[Perks] Potion count effect timeout"),
        TIMEOUT_POTION_IMMUNE("[Perks] Potion immune effect timeout"),
        TIMEOUT_POISON_THROWER("[Perks] Poison thrower effect timeout"),
        TIMEOUT_POTION_EXPLODER("[Perks] Potion exploder effect timeout"),
        POISON_THROWER_RECHARGE("[Perks] Poison thrower recharge"),
        REMOTE_CONTROL_COUNT("[Perks] Number of Potion remote controls (how many times player can use it)"),
        POTION_EXPLODER_COUNT("[Perks] Number of Potion Exploder (how many times player can use it)"),
        STEAL_POINTS("[Perks] Steal points from potion owner (works with Potion Exploder perk)"),
        DEFAULT_PERKS("[Perks] Perks available in this game"),
        LEVEL_MAP("[Level] map");

        private String key;

        Keys(String key) {
            this.key = key;
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
        initInactivity();
        initRound();
        initSemifinal();

        integer(KILL_WALL_SCORE, 1);
        integer(KILL_GHOST_SCORE, 10);
        integer(KILL_OTHER_HERO_SCORE, 20);
        integer(KILL_ENEMY_HERO_SCORE, 100);
        integer(CATCH_PERK_SCORE, 5);
        integer(DIE_PENALTY, 30);
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

        multiline(LEVEL_MAP,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                     ☼\n" +
                "☼ ☼☼ ☼☼☼☼ ☼  ☼☼ ☼☼  ☼ ☼\n" +
                "☼ ☼☼      ☼☼ ☼☼ ☼☼ ☼☼ ☼\n" +
                "☼     ☼☼☼  ☼        ☼ ☼\n" +
                "☼ ☼☼☼   ☼    ☼☼☼☼ ☼   ☼\n" +
                "☼  ☼  ☼   ☼☼      ☼☼☼ ☼\n" +
                "☼     ☼☼☼  ☼☼   ☼     ☼\n" +
                "☼ ☼☼☼          ☼☼☼  ☼ ☼\n" +
                "☼  ☼  ☼☼☼☼ ☼☼☼     ☼☼ ☼\n" +
                "☼           ☼   ☼   ☼ ☼\n" +
                "☼ ☼☼☼☼ ☼☼ ☼   ☼☼☼ ☼   ☼\n" +
                "☼      ☼☼ ☼☼☼     ☼☼☼ ☼\n" +
                "☼ ☼☼☼         ☼☼☼     ☼\n" +
                "☼  ☼  ☼☼ ☼☼☼   ☼  ☼☼☼ ☼\n" +
                "☼    ☼☼    ☼ ☼      ☼ ☼\n" +
                "☼ ☼      ☼   ☼☼☼ ☼☼   ☼\n" +
                "☼ ☼  ☼☼  ☼☼      ☼☼ ☼ ☼\n" +
                "☼ ☼☼ ☼☼   ☼ ☼☼☼☼    ☼ ☼\n" +
                "☼       ☼        ☼☼ ☼ ☼\n" +
                "☼ ☼☼☼☼ ☼☼☼ ☼☼☼☼ ☼☼  ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    public Level level() {
        return new Level(string(LEVEL_MAP));
    }

    public PerksSettingsWrapper perksSettings() {
        return new PerksSettingsWrapper(this);
    }

    public boolean isTeamDeathMatch() {
        return integer(ROUNDS_TEAMS_PER_ROOM) > 1;
    }
}
