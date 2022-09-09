package com.codenjoy.dojo.mollymage.services;

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

import com.codenjoy.dojo.client.Utils;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkSettings;
import com.codenjoy.dojo.mollymage.model.items.perks.PerksSettingsWrapper;
import com.codenjoy.dojo.utils.JsonUtils;
import com.codenjoy.dojo.utils.TestUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.codenjoy.dojo.games.mollymage.ElementUtils.perks;
import static org.junit.Assert.assertEquals;

public class GameSettingsTest {

    @Test
    public void shouldDefaultPerkSettings() {
        GameSettings settings = new GameSettings();
        PerksSettingsWrapper perksSettings = settings.perksSettings();

        assertEquals("{POTION_BLAST_RADIUS_INCREASE=PerkSettings{value=2, timeout=30}, \n" +
                        "POTION_COUNT_INCREASE=PerkSettings{value=4, timeout=30}, \n" +
                        "POTION_IMMUNE=PerkSettings{value=0, timeout=30}, \n" +
                        "POTION_REMOTE_CONTROL=PerkSettings{value=3, timeout=1}, \n" +
                        "POISON_THROWER=PerkSettings{value=0, timeout=30}, \n" +
                        "POTION_EXPLODER=PerkSettings{value=1, timeout=30}}",
                Utils.split(allPerkSettings(perksSettings), "}, \n"));
    }

    public LinkedHashMap<String, PerkSettings> allPerkSettings(PerksSettingsWrapper perksSettings) {
        return new LinkedHashMap<>() {{
            Arrays.stream(perks)
                    .forEach(it -> put(it.name(), perksSettings.get(it)));
        }};
    }

    @Test
    public void testUpdate() {
        // given
        GameSettings settings = new GameSettings();

        assertEquals("{\n" +
                "  'BIG_BADABOOM':false,\n" +
                "  'CATCH_PERK_SCORE':5,\n" +
                "  'DEFAULT_PERKS':'r+icTA',\n" +
                "  'GAME_MODE':'[MULTIPLE] One level chosen at random. Multi player (all together or in the rooms).',\n" +
                "  'GHOSTS_COUNT':5,\n" +
                "  'HERO_DIED_PENALTY':-30,\n" +
                "  'INACTIVITY_ENABLED':false,\n" +
                "  'INACTIVITY_TIMEOUT':300,\n" +
                "  'KILL_ENEMY_HERO_SCORE':100,\n" +
                "  'KILL_GHOST_SCORE':10,\n" +
                "  'KILL_OTHER_HERO_SCORE':20,\n" +
                "  'LEVELS_MAP_1_1':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_2':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼ ☼☼ ☼☼☼☼ ☼  ☼☼ ☼☼  ☼ ☼\\n☼ ☼☼      ☼☼ ☼☼ ☼☼ ☼☼ ☼\\n☼     ☼☼☼  ☼        ☼ ☼\\n☼ ☼☼☼   ☼    ☼☼☼☼ ☼   ☼\\n☼  ☼  ☼   ☼☼      ☼☼☼ ☼\\n☼     ☼☼☼  ☼☼   ☼     ☼\\n☼ ☼☼☼          ☼☼☼  ☼ ☼\\n☼  ☼  ☼☼☼☼ ☼☼☼     ☼☼ ☼\\n☼           ☼   ☼   ☼ ☼\\n☼ ☼☼☼☼ ☼☼ ☼   ☼☼☼ ☼   ☼\\n☼      ☼☼ ☼☼☼     ☼☼☼ ☼\\n☼ ☼☼☼         ☼☼☼     ☼\\n☼  ☼  ☼☼ ☼☼☼   ☼  ☼☼☼ ☼\\n☼    ☼☼    ☼ ☼      ☼ ☼\\n☼ ☼      ☼   ☼☼☼ ☼☼   ☼\\n☼ ☼  ☼☼  ☼☼      ☼☼ ☼ ☼\\n☼ ☼☼ ☼☼   ☼ ☼☼☼☼    ☼ ☼\\n☼       ☼        ☼☼ ☼ ☼\\n☼ ☼☼☼☼ ☼☼☼ ☼☼☼☼ ☼☼  ☼ ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_3':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼     ☼          ☼    ☼\\n☼     ☼          ☼    ☼\\n☼     ☼   ☼    ☼☼☼    ☼\\n☼     ☼   ☼      ☼    ☼\\n☼     ☼☼☼☼☼☼     ☼☼   ☼\\n☼       ☼             ☼\\n☼☼☼     ☼   ☼         ☼\\n☼           ☼    ☼    ☼\\n☼           ☼    ☼    ☼\\n☼☼☼☼☼☼☼☼    ☼☼☼☼☼☼    ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼     ☼   ☼\\n☼                 ☼   ☼\\n☼         ☼    ☼☼☼☼☼☼☼☼\\n☼   ☼     ☼       ☼   ☼\\n☼   ☼     ☼       ☼   ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼        ☼\\n☼     ☼               ☼\\n☼     ☼       ☼       ☼\\n☼             ☼       ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'OPEN_TREASURE_BOX_SCORE':1,\n" +
                "  'PERK_DROP_RATIO':20,\n" +
                "  'PERK_PICK_TIMEOUT':30,\n" +
                "  'PERK_POTION_BLAST_RADIUS_INC':2,\n" +
                "  'PERK_POTION_COUNT_INC':4,\n" +
                "  'PERK_WHOLE_TEAM_GET':false,\n" +
                "  'POISON_THROWER_RECHARGE':3,\n" +
                "  'POTIONS_COUNT':1,\n" +
                "  'POTION_EXPLODER_COUNT':1,\n" +
                "  'POTION_POWER':3,\n" +
                "  'REMOTE_CONTROL_COUNT':3,\n" +
                "  'ROOM_SIZE':5,\n" +
                "  'ROUNDS_ENABLED':false,\n" +
                "  'ROUNDS_MIN_TICKS_FOR_WIN':1,\n" +
                "  'ROUNDS_PER_MATCH':1,\n" +
                "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                "  'ROUNDS_TIME':200,\n" +
                "  'ROUNDS_TIME_BEFORE_START':5,\n" +
                "  'ROUNDS_TIME_FOR_WINNER':1,\n" +
                "  'SCORE_COUNTING_TYPE':'Accumulate points consistently',\n" +
                "  'SEMIFINAL_CLEAR_SCORE':false,\n" +
                "  'SEMIFINAL_ENABLED':false,\n" +
                "  'SEMIFINAL_LIMIT':50,\n" +
                "  'SEMIFINAL_PERCENTAGE':true,\n" +
                "  'SEMIFINAL_RESET_BOARD':true,\n" +
                "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                "  'SEMIFINAL_TIMEOUT':900,\n" +
                "  'STEAL_POINTS':false,\n" +
                "  'TIMEOUT_POISON_THROWER':30,\n" +
                "  'TIMEOUT_POTION_BLAST_RADIUS_INC':30,\n" +
                "  'TIMEOUT_POTION_COUNT_INC':30,\n" +
                "  'TIMEOUT_POTION_EXPLODER':30,\n" +
                "  'TIMEOUT_POTION_IMMUNE':30,\n" +
                "  'TREASURE_BOX_COUNT':52,\n" +
                "  'WIN_ROUND_SCORE':30\n" +
                "}", JsonUtils.prettyPrint(settings.asJson()));

        // when
        settings.update(new JSONObject("{\n" +
                "  'HERO_DIED_PENALTY':12,\n" +
                "  'PERK_POTION_BLAST_RADIUS_INC':4,\n" +
                "  'PERK_DROP_RATIO':23,\n" +
                "  'ROUNDS_ENABLED':true,\n" +
                "  'ROUNDS_TIME_BEFORE_START':10,\n" +
                "  'SEMIFINAL_LIMIT':150,\n" +
                "  'SEMIFINAL_PERCENTAGE':false,\n" +
                "  'TIMEOUT_POTION_COUNT_INC':12,\n" +
                "}"));

        // then
        assertEquals("{\n" +
                "  'BIG_BADABOOM':false,\n" +
                "  'CATCH_PERK_SCORE':5,\n" +
                "  'DEFAULT_PERKS':'r+icTA',\n" +
                "  'GAME_MODE':'[MULTIPLE] One level chosen at random. Multi player (all together or in the rooms).',\n" +
                "  'GHOSTS_COUNT':5,\n" +
                "  'HERO_DIED_PENALTY':12,\n" +
                "  'INACTIVITY_ENABLED':false,\n" +
                "  'INACTIVITY_TIMEOUT':300,\n" +
                "  'KILL_ENEMY_HERO_SCORE':100,\n" +
                "  'KILL_GHOST_SCORE':10,\n" +
                "  'KILL_OTHER_HERO_SCORE':20,\n" +
                "  'LEVELS_MAP_1_1':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_2':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼ ☼☼ ☼☼☼☼ ☼  ☼☼ ☼☼  ☼ ☼\\n☼ ☼☼      ☼☼ ☼☼ ☼☼ ☼☼ ☼\\n☼     ☼☼☼  ☼        ☼ ☼\\n☼ ☼☼☼   ☼    ☼☼☼☼ ☼   ☼\\n☼  ☼  ☼   ☼☼      ☼☼☼ ☼\\n☼     ☼☼☼  ☼☼   ☼     ☼\\n☼ ☼☼☼          ☼☼☼  ☼ ☼\\n☼  ☼  ☼☼☼☼ ☼☼☼     ☼☼ ☼\\n☼           ☼   ☼   ☼ ☼\\n☼ ☼☼☼☼ ☼☼ ☼   ☼☼☼ ☼   ☼\\n☼      ☼☼ ☼☼☼     ☼☼☼ ☼\\n☼ ☼☼☼         ☼☼☼     ☼\\n☼  ☼  ☼☼ ☼☼☼   ☼  ☼☼☼ ☼\\n☼    ☼☼    ☼ ☼      ☼ ☼\\n☼ ☼      ☼   ☼☼☼ ☼☼   ☼\\n☼ ☼  ☼☼  ☼☼      ☼☼ ☼ ☼\\n☼ ☼☼ ☼☼   ☼ ☼☼☼☼    ☼ ☼\\n☼       ☼        ☼☼ ☼ ☼\\n☼ ☼☼☼☼ ☼☼☼ ☼☼☼☼ ☼☼  ☼ ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_3':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼     ☼          ☼    ☼\\n☼     ☼          ☼    ☼\\n☼     ☼   ☼    ☼☼☼    ☼\\n☼     ☼   ☼      ☼    ☼\\n☼     ☼☼☼☼☼☼     ☼☼   ☼\\n☼       ☼             ☼\\n☼☼☼     ☼   ☼         ☼\\n☼           ☼    ☼    ☼\\n☼           ☼    ☼    ☼\\n☼☼☼☼☼☼☼☼    ☼☼☼☼☼☼    ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼     ☼   ☼\\n☼                 ☼   ☼\\n☼         ☼    ☼☼☼☼☼☼☼☼\\n☼   ☼     ☼       ☼   ☼\\n☼   ☼     ☼       ☼   ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼        ☼\\n☼     ☼               ☼\\n☼     ☼       ☼       ☼\\n☼             ☼       ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'OPEN_TREASURE_BOX_SCORE':1,\n" +
                "  'PERK_DROP_RATIO':23,\n" +
                "  'PERK_PICK_TIMEOUT':30,\n" +
                "  'PERK_POTION_BLAST_RADIUS_INC':4,\n" +
                "  'PERK_POTION_COUNT_INC':4,\n" +
                "  'PERK_WHOLE_TEAM_GET':false,\n" +
                "  'POISON_THROWER_RECHARGE':3,\n" +
                "  'POTIONS_COUNT':1,\n" +
                "  'POTION_EXPLODER_COUNT':1,\n" +
                "  'POTION_POWER':3,\n" +
                "  'REMOTE_CONTROL_COUNT':3,\n" +
                "  'ROOM_SIZE':5,\n" +
                "  'ROUNDS_ENABLED':true,\n" +
                "  'ROUNDS_MIN_TICKS_FOR_WIN':1,\n" +
                "  'ROUNDS_PER_MATCH':1,\n" +
                "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                "  'ROUNDS_TIME':200,\n" +
                "  'ROUNDS_TIME_BEFORE_START':10,\n" +
                "  'ROUNDS_TIME_FOR_WINNER':1,\n" +
                "  'SCORE_COUNTING_TYPE':'Accumulate points consistently',\n" +
                "  'SEMIFINAL_CLEAR_SCORE':false,\n" +
                "  'SEMIFINAL_ENABLED':false,\n" +
                "  'SEMIFINAL_LIMIT':150,\n" +
                "  'SEMIFINAL_PERCENTAGE':false,\n" +
                "  'SEMIFINAL_RESET_BOARD':true,\n" +
                "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                "  'SEMIFINAL_TIMEOUT':900,\n" +
                "  'STEAL_POINTS':false,\n" +
                "  'TIMEOUT_POISON_THROWER':30,\n" +
                "  'TIMEOUT_POTION_BLAST_RADIUS_INC':30,\n" +
                "  'TIMEOUT_POTION_COUNT_INC':12,\n" +
                "  'TIMEOUT_POTION_EXPLODER':30,\n" +
                "  'TIMEOUT_POTION_IMMUNE':30,\n" +
                "  'TREASURE_BOX_COUNT':52,\n" +
                "  'WIN_ROUND_SCORE':30\n" +
                "}", JsonUtils.prettyPrint(settings.asJson()));

        // when
        settings.update(new JSONObject("{}"));

        // then
        assertEquals("{\n" +
                "  'BIG_BADABOOM':false,\n" +
                "  'CATCH_PERK_SCORE':5,\n" +
                "  'DEFAULT_PERKS':'r+icTA',\n" +
                "  'GAME_MODE':'[MULTIPLE] One level chosen at random. Multi player (all together or in the rooms).',\n" +
                "  'GHOSTS_COUNT':5,\n" +
                "  'HERO_DIED_PENALTY':12,\n" +
                "  'INACTIVITY_ENABLED':false,\n" +
                "  'INACTIVITY_TIMEOUT':300,\n" +
                "  'KILL_ENEMY_HERO_SCORE':100,\n" +
                "  'KILL_GHOST_SCORE':10,\n" +
                "  'KILL_OTHER_HERO_SCORE':20,\n" +
                "  'LEVELS_MAP_1_1':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_2':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼                     ☼\\n☼ ☼☼ ☼☼☼☼ ☼  ☼☼ ☼☼  ☼ ☼\\n☼ ☼☼      ☼☼ ☼☼ ☼☼ ☼☼ ☼\\n☼     ☼☼☼  ☼        ☼ ☼\\n☼ ☼☼☼   ☼    ☼☼☼☼ ☼   ☼\\n☼  ☼  ☼   ☼☼      ☼☼☼ ☼\\n☼     ☼☼☼  ☼☼   ☼     ☼\\n☼ ☼☼☼          ☼☼☼  ☼ ☼\\n☼  ☼  ☼☼☼☼ ☼☼☼     ☼☼ ☼\\n☼           ☼   ☼   ☼ ☼\\n☼ ☼☼☼☼ ☼☼ ☼   ☼☼☼ ☼   ☼\\n☼      ☼☼ ☼☼☼     ☼☼☼ ☼\\n☼ ☼☼☼         ☼☼☼     ☼\\n☼  ☼  ☼☼ ☼☼☼   ☼  ☼☼☼ ☼\\n☼    ☼☼    ☼ ☼      ☼ ☼\\n☼ ☼      ☼   ☼☼☼ ☼☼   ☼\\n☼ ☼  ☼☼  ☼☼      ☼☼ ☼ ☼\\n☼ ☼☼ ☼☼   ☼ ☼☼☼☼    ☼ ☼\\n☼       ☼        ☼☼ ☼ ☼\\n☼ ☼☼☼☼ ☼☼☼ ☼☼☼☼ ☼☼  ☼ ☼\\n☼                     ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'LEVELS_MAP_1_3':'☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n☼     ☼          ☼    ☼\\n☼     ☼          ☼    ☼\\n☼     ☼   ☼    ☼☼☼    ☼\\n☼     ☼   ☼      ☼    ☼\\n☼     ☼☼☼☼☼☼     ☼☼   ☼\\n☼       ☼             ☼\\n☼☼☼     ☼   ☼         ☼\\n☼           ☼    ☼    ☼\\n☼           ☼    ☼    ☼\\n☼☼☼☼☼☼☼☼    ☼☼☼☼☼☼    ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼         ☼\\n☼     ☼     ☼     ☼   ☼\\n☼                 ☼   ☼\\n☼         ☼    ☼☼☼☼☼☼☼☼\\n☼   ☼     ☼       ☼   ☼\\n☼   ☼     ☼       ☼   ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼        ☼\\n☼     ☼               ☼\\n☼     ☼       ☼       ☼\\n☼             ☼       ☼\\n☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\\n',\n" +
                "  'OPEN_TREASURE_BOX_SCORE':1,\n" +
                "  'PERK_DROP_RATIO':23,\n" +
                "  'PERK_PICK_TIMEOUT':30,\n" +
                "  'PERK_POTION_BLAST_RADIUS_INC':4,\n" +
                "  'PERK_POTION_COUNT_INC':4,\n" +
                "  'PERK_WHOLE_TEAM_GET':false,\n" +
                "  'POISON_THROWER_RECHARGE':3,\n" +
                "  'POTIONS_COUNT':1,\n" +
                "  'POTION_EXPLODER_COUNT':1,\n" +
                "  'POTION_POWER':3,\n" +
                "  'REMOTE_CONTROL_COUNT':3,\n" +
                "  'ROOM_SIZE':5,\n" +
                "  'ROUNDS_ENABLED':true,\n" +
                "  'ROUNDS_MIN_TICKS_FOR_WIN':1,\n" +
                "  'ROUNDS_PER_MATCH':1,\n" +
                "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                "  'ROUNDS_TIME':200,\n" +
                "  'ROUNDS_TIME_BEFORE_START':10,\n" +
                "  'ROUNDS_TIME_FOR_WINNER':1,\n" +
                "  'SCORE_COUNTING_TYPE':'Accumulate points consistently',\n" +
                "  'SEMIFINAL_CLEAR_SCORE':false,\n" +
                "  'SEMIFINAL_ENABLED':false,\n" +
                "  'SEMIFINAL_LIMIT':150,\n" +
                "  'SEMIFINAL_PERCENTAGE':false,\n" +
                "  'SEMIFINAL_RESET_BOARD':true,\n" +
                "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                "  'SEMIFINAL_TIMEOUT':900,\n" +
                "  'STEAL_POINTS':false,\n" +
                "  'TIMEOUT_POISON_THROWER':30,\n" +
                "  'TIMEOUT_POTION_BLAST_RADIUS_INC':30,\n" +
                "  'TIMEOUT_POTION_COUNT_INC':12,\n" +
                "  'TIMEOUT_POTION_EXPLODER':30,\n" +
                "  'TIMEOUT_POTION_IMMUNE':30,\n" +
                "  'TREASURE_BOX_COUNT':52,\n" +
                "  'WIN_ROUND_SCORE':30\n" +
                "}", JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldGetAllKeys() {
        assertEquals("OPEN_TREASURE_BOX_SCORE         =[Score] Open treasure box score\n" +
                    "KILL_GHOST_SCORE                =[Score] Kill ghost score\n" +
                    "KILL_OTHER_HERO_SCORE           =[Score] Kill other hero score\n" +
                    "KILL_ENEMY_HERO_SCORE           =[Score] Kill enemy hero score\n" +
                    "CATCH_PERK_SCORE                =[Score] Catch perk score\n" +
                    "HERO_DIED_PENALTY               =[Score] Your hero's death penalty\n" +
                    "WIN_ROUND_SCORE                 =[Score][Rounds] Win round score\n" +
                    "BIG_BADABOOM                    =[Map] Blast activate potion\n" +
                    "POTIONS_COUNT                   =[Map] Potions count\n" +
                    "POTION_POWER                    =[Map] Potion power\n" +
                    "TREASURE_BOX_COUNT              =[Map] Treasure boxes count\n" +
                    "GHOSTS_COUNT                    =[Map] Ghosts count\n" +
                    "PERK_WHOLE_TEAM_GET             =[Perks] All teammates get perk\n" +
                    "PERK_DROP_RATIO                 =[Perks] Perks drop ratio in %\n" +
                    "PERK_PICK_TIMEOUT               =[Perks] Perks pick timeout\n" +
                    "PERK_POTION_BLAST_RADIUS_INC    =[Perks] Potion blast radius increase\n" +
                    "TIMEOUT_POTION_BLAST_RADIUS_INC =[Perks] Potion blast radius increase effect timeout\n" +
                    "PERK_POTION_COUNT_INC           =[Perks] Potion count increase\n" +
                    "TIMEOUT_POTION_COUNT_INC        =[Perks] Potion count effect timeout\n" +
                    "TIMEOUT_POTION_IMMUNE           =[Perks] Potion immune effect timeout\n" +
                    "TIMEOUT_POISON_THROWER          =[Perks] Poison thrower effect timeout\n" +
                    "TIMEOUT_POTION_EXPLODER         =[Perks] Potion exploder effect timeout\n" +
                    "POISON_THROWER_RECHARGE         =[Perks] Poison thrower recharge\n" +
                    "REMOTE_CONTROL_COUNT            =[Perks] Number of Potion remote controls (how many times player can use it)\n" +
                    "POTION_EXPLODER_COUNT           =[Perks] Number of Potion Exploder (how many times player can use it)\n" +
                    "STEAL_POINTS                    =[Perks] Steal points from potion owner (works with Potion Exploder perk)\n" +
                    "DEFAULT_PERKS                   =[Perks] Perks available in this game\n" +
                    "SCORE_COUNTING_TYPE             =[Score] Counting score mode",
                TestUtils.toString(new GameSettings().allKeys()));
    }
}