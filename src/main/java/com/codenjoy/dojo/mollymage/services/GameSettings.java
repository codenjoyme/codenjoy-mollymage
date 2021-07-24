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


import com.codenjoy.dojo.mollymage.model.*;
import com.codenjoy.dojo.mollymage.model.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.round.RoundSettings;
import com.codenjoy.dojo.services.semifinal.SemifinalSettings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;

public class GameSettings extends SettingsImpl
        implements SettingsReader<GameSettings>,
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
        BIG_BADABOOM("[Level] Blast activate bomb"),
        POTIONS_COUNT("[Level] Potions count"),
        POTION_POWER("[Level] Potion power"),
        BOARD_SIZE("[Level] Board size"),
        TREASURE_BOX_COUNT("[Level] Treasure boxes count"),
        GHOSTS_COUNT("[Level] Ghosts count"),
        PERK_DROP_RATIO("[Perks] Perks drop ratio in %"),
        PERK_PICK_TIMEOUT("[Perks] Perks pick timeout"),
        PERK_POTION_BLAST_RADIUS_INC("[Perks] Potion blast radius increase"),
        TIMEOUT_POTION_BLAST_RADIUS_INC("[Perks] Potion blast radius increase effect timeout"),
        PERK_POTION_COUNT_INC("[Perks] Potion count increase"),
        TIMEOUT_POTION_COUNT_INC("[Perks] Potion count effect timeout"),
        TIMEOUT_POTION_IMMUNE("[Perks] Potion immune effect timeout"),
        REMOTE_CONTROL_COUNT("[Perks] Number of Potion remote controls (how many times player can use it)"),
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
        int boardSize = 23;
        integer(BOARD_SIZE, boardSize);
        integer(TREASURE_BOX_COUNT, (boardSize * boardSize) / 10);
        integer(GHOSTS_COUNT, 5);

        string(DEFAULT_PERKS, StringUtils.EMPTY);
        PerksSettingsWrapper perks =
                perksSettings()
                    .dropRatio(20) // Set value to 0% = perks is disabled.
                    .pickTimeout(30);
        int timeout = 30;
        perks.put(Element.POTION_REMOTE_CONTROL, 3, 1);
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 2, timeout);
        perks.put(Element.POTION_IMMUNE, 0, timeout);
        perks.put(Element.POTION_COUNT_INCREASE, 4, timeout);

        multiline(LEVEL_MAP,
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼                     ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");
    }

    public Level getLevel() {
        String map = string(LEVEL_MAP);
        return new Level() {
            @Override
            public int potionsCount() {
                return integer(POTIONS_COUNT);
            }
            
            @Override
            public int potionsPower() {
                return integer(POTION_POWER);
            }

            @Override
            public int perksDropRate() {
                return integer(PERK_DROP_RATIO);
            }
        };
    }

    public Walls getWalls(Dice dice) {
        OriginalWalls originalWalls = new OriginalWalls(integerValue(BOARD_SIZE));
        Ghosts ghosts = new Ghosts(originalWalls, integerValue(GHOSTS_COUNT), dice);

        return new EatSpaceWalls(ghosts, integerValue(TREASURE_BOX_COUNT), dice);
    }

    public Hero getHero(Level level) {
        return new Hero(level);
    }

    public PerksSettingsWrapper perksSettings() {
        return new PerksSettingsWrapper(this);
    }

}
