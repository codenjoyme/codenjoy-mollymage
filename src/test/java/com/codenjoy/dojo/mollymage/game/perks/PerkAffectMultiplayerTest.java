package com.codenjoy.dojo.mollymage.game.perks;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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
import com.codenjoy.dojo.mollymage.game.AbstractGameTest;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.items.perks.*;
import com.codenjoy.dojo.services.PointImpl;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.STEAL_POINTS;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_TEAMS_PER_ROOM;
import static org.junit.Assert.assertEquals;

public class PerkAffectMultiplayerTest extends AbstractGameTest {

    @Test
    public void shouldNotTeammateGetPerk_AfterFirstPlayerPickUp_withEnemy() {
        // given
        settings.integer(POTIONS_COUNT, 1)
                .integer(CATCH_PERK_SCORE, CATCH_PERK_SCORE_FOR_TEST)
                .bool(PERK_WHOLE_TEAM_GET,false)
                .integer(ROUNDS_TEAMS_PER_ROOM, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺☺  \n");

        // set up 3 players, 2 in one team, and 1 perk on field
        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);

        field.perks().add(new PerkOnBoard(new PointImpl(0, 1), new PotionImmune(settings.integer(TIMEOUT_POTION_IMMUNE))));

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "i    \n" +
                "☺♥♡  \n", 0);

        //heroes should not have any perks
        assertEquals(0, hero(0).getPerks().size());

        assertEquals(0, hero(1).getPerks().size());

        assertEquals(0, hero(2).getPerks().size());

        //when first hero get perk
        hero(0).up();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " ♥♡  \n", 0);

        //teammate should not get perk
        assertEquals(1, hero(0).getPerks().size());
        assertEquals(0, hero(1).getPerks().size());
        assertEquals(0, hero(2).getPerks().size());

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n");

        assertEquals(CATCH_PERK_SCORE_FOR_TEST, hero(0).scores());
        assertEquals(0, hero(1).scores());
        assertEquals(0, hero(2).scores());
    }

    @Test
    public void shouldTeammateGetPerk_AfterFirstPlayerPickUp_withEnemy() {
        // given
        settings.integer(POTIONS_COUNT, 1)
                .integer(CATCH_PERK_SCORE, CATCH_PERK_SCORE_FOR_TEST)
                .bool(PERK_WHOLE_TEAM_GET, true)
                .integer(ROUNDS_TEAMS_PER_ROOM, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺☺  \n");

        // set up 3 players, 2 in one team, and 1 perk on field
        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);

        field.perks().add(new PerkOnBoard(new PointImpl(0, 1), new PotionImmune(settings.integer(TIMEOUT_POTION_IMMUNE))));

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "i    \n" +
                "☺♥♡  \n", 0);


        // heroes should not have any perks
        assertEquals(0, player(0).getHero().getPerks().size());
        assertEquals(0, player(1).getHero().getPerks().size());
        assertEquals(0, player(2).getHero().getPerks().size());

        //when first hero get perk
        hero(0).up();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " ♥♡  \n", 0);

        //teammate should get perk to
        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n");

        assertEquals(1,player(0).getHero().getPerks().size());
        assertEquals(1,player(1).getHero().getPerks().size());
        assertEquals(0, player(2).getHero().getPerks().size());

        //scores for perk earned only one hero, who picked up perk
        assertEquals(CATCH_PERK_SCORE_FOR_TEST,player(0).getHero().scores());
        assertEquals(0, player(1).getHero().scores());
        assertEquals(0, player(2).getHero().scores());
    }

    /**  hero1 should get score for killing hero2 when then different blasts crossed
     *   PT - Poison Thrower
     */
    @Test
    public void shouldKillEnemyByPTAndScorePoints_whenCrossBlast() {
        // given
        int killScore = 10;
        settings.integer(POTION_POWER, 2)
                .integer(CATCH_PERK_SCORE, 0)
                .integer(KILL_OTHER_HERO_SCORE, killScore);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺ ☺  \n");

        newPerk(0, 1, new PoisonThrower(10));

        Hero hero1 = hero(0);
        Hero hero2 = hero(1);

        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "☺ ♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "♥ ☺  \n", 1);

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        tick();

        // then
        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when heroes are going on the position
        hero1.up();
        hero2.up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "  3  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "     \n" +
                "  3  \n", 1);

        // when potion boom, hero1 should shoot by poison thrower
        tick();
        tick();
        hero1.right();
        hero1.act(1);
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉♣  \n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", 0);

        assertF("     \n" +
                "     \n" +
                "♥҉Ѡ  \n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [DIED]\n");

        assertEquals(killScore, hero1.scores());
        assertEquals(0, hero2.scores());
    }

    /**  both heroes should get score for killing ghost when then different blasts crossed
     *   PT - Poison Thrower
     */
    @Test
    public void shouldKillGhostByPTAndScorePoints_whenCrossBlast() {
        // given
        int killScore = 10;
        settings.integer(POTION_POWER, 2)
                .integer(CATCH_PERK_SCORE, 0)
                .integer(KILL_GHOST_SCORE, killScore);

        givenFl("     \n" +
                "     \n" +
                "  &  \n" +
                "     \n" +
                "☺ ☺  \n");

        newPerk(0, 1, new PoisonThrower(10));

        Hero hero1 = hero(0);
        Hero hero2 = hero(1);

        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        assertF("     \n" +
                "     \n" +
                "  &  \n" +
                "T    \n" +
                "☺ ♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "  &  \n" +
                "T    \n" +
                "♥ ☺  \n", 1);

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        tick();

        hero1.up();
        hero2.right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ &  \n" +
                "   ♥ \n" +
                "  3  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥ &  \n" +
                "   ☺ \n" +
                "  3  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potion boom, hero1 should shoot by poison thrower
        tick();
        tick();
        hero1.right();
        hero1.act(1);
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉x  \n" +
                "  ҉♥ \n" +
                "҉҉҉҉҉\n", 0);

        assertF("     \n" +
                "     \n" +
                "♥҉x  \n" +
                "  ҉☺ \n" +
                "҉҉҉҉҉\n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        assertEquals(killScore, hero1.scores());
        assertEquals(killScore, hero2.scores());
    }

    /**  both heroes should get score for killing box when then different blasts crossed
     *   PT - Poison Thrower
     */
    @Test
    public void shouldKillBoxByPTAndScorePoints_whenCrossBlast() {
        // given
        int killScore = 10;
        settings.integer(POTION_POWER, 2)
                .integer(CATCH_PERK_SCORE, 0)
                .integer(KILL_WALL_SCORE, killScore);

        givenFl("     \n" +
                "     \n" +
                "  #  \n" +
                "     \n" +
                "☺ ☺  \n");

        newPerk(0, 1, new PoisonThrower(10));

        Hero hero1 = hero(0);
        Hero hero2 = hero(1);
        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        assertF("     \n" +
                "     \n" +
                "  #  \n" +
                "T    \n" +
                "☺ ♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "  #  \n" +
                "T    \n" +
                "♥ ☺  \n", 1);

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        tick();

        hero1.up();
        hero2.right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ #  \n" +
                "   ♥ \n" +
                "  3  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥ #  \n" +
                "   ☺ \n" +
                "  3  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potion boom - hero1 should shoot by poison thrower
        tick();
        tick();
        hero1.right();
        hero1.act(1);
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉H  \n" +
                "  ҉♥ \n" +
                "҉҉҉҉҉\n", 0);

        assertF("     \n" +
                "     \n" +
                "♥҉H  \n" +
                "  ҉☺ \n" +
                "҉҉҉҉҉\n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        assertEquals(killScore, hero1.scores());
        assertEquals(killScore, hero2.scores());
    }

    /**  both heroes should kill perk when then different blasts crossed
     *   and get personal hunter perks. GhostHunters should double.
     *   PT - Poison Thrower
     */
    @Test
    public void shouldKillOnePerkAndGetTwoHuntedGhost_CrossBlastPortionAndPoisonThrow() {
        // given
        int killScore = 10;
        settings.integer(POTION_POWER, 2)
                .integer(CATCH_PERK_SCORE, 0)
                .integer(KILL_GHOST_SCORE, killScore);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺ ☺  \n");

        newPerk(0, 1, new PoisonThrower(10));
        Hero hero1 = hero(0);
        Hero hero2 = hero(1);

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "☺ ♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "♥ ☺  \n", 1);

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "  4  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "  4  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");


        // when move heroes on position and set perk for destroy
        hero1.up();
        hero2.right();
        tick();
        hero2.right();
        tick();
        hero2.up();
        tick();
        newPerk(2, 2, new PotionCountIncrease(1, 10));

        // then
        assertF("     \n" +
                "     \n" +
                "☺ c ♥\n" +
                "     \n" +
                "  1  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥ c ☺\n" +
                "     \n" +
                "  1  \n", 1);


        // when both heroes kill one perk
        hero1.right();
        hero1.act(1);
        tick();

        // then two GhostHunters should born on the one Point
        assertF("     \n" +
                "     \n" +
                "☺҉x ♥\n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", 0);

        assertF("     \n" +
                "     \n" +
                "♥҉x ☺\n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", 1);

        events.verifyAllEvents(
                "listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");

        assertEquals(2, field.hunters().size());

        // when field tick
        tick();

        // then both hunters are visible and haunting heroes
        assertF("     \n" +
                "     \n" +
                "☺x x♥\n" +
                "     \n" +
                "     \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥x x☺\n" +
                "     \n" +
                "     \n", 1);
    }

    @Test
    public void shouldPerkCantSpawnFromGhost() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&   \n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(50);

        hero(0).act();
        tick();

        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();

        tick();

        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉x   \n", 0);

        events.verifyAllEvents(
                "[KILL_GHOST]");

        removeGhosts(1); // больше не надо привидений

        tick();

        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n", 0);
    }

    @Test
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test1() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when hero0 catch perk and both heroes act and move
        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "44   \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        //when hero0 with PE perk explode own potion and hero1's simple potion
        hero(0).act(2);
        hero(0).up();

        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", 1);

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n");
    }

    @Test
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test2() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when both heroes set Remote_Control potions. Hero0 get PE perk
        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        hero(0).addPerk(new PotionRemoteControl(1, 10));
        hero(1).addPerk(new PotionRemoteControl(1, 10));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "55   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "55   \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when hero0 uses PE perk and explode both potions
        hero(0).act(2);
        hero(0).up();

        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", 1);

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n");
    }

    // Remote Control and Perk Exploder should works together.
    @Test
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test3() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when hero0 sets usually potion. Hero1 sets RC potion.
        hero(1).addPerk(new PotionRemoteControl(1, 10));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "45&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "45&  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");


        // when hero0 explode all, hero1 explode own remote control perk
        hero(0).act(2);
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then both heroes kill ghost
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    // Both Heroes have Perk Exploder.
    @Test
    public void shouldExplodeBothPotionsOnBoardAndKillGhost_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));
        newPerk(1, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when heroes plant potions and catch perk
        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "44&  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => [CATCH_PERK]\n");

        // when hero0 and hero1 explode all, both should kill ghost
        hero(0).act(2);
        hero(0).up();

        hero(1).act(2);
        hero(1).up();

        tick();

        // then both heroes kill ghost
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    @Test
    public void shouldPotionOwnerGetScoresTo_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1)
                .bool(STEAL_POINTS, false);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when both heroes set simple potions
        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();
        hero(0).up();
        hero(1).up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "33&  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potions timers almost end
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "11&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "11&  \n", 1);


        // when hero0 explode all, both heroes should earn scores
        hero(0).act(2);

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 0);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    @Test
    public void shouldNotPotionOwnerGetScores_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1)
                .bool(STEAL_POINTS, true);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        newPerk(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when both heroes set simple potions
        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();
        hero(0).up();
        hero(1).up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "33&  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potions timers almost end
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "11&  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "11&  \n", 1);

        // when hero0 explode all, only hero0 should kill ghost
        hero(0).act(2);

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", 1);

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => []\n");
    }

    @Test
    public void shouldBothHeroesGerPersonalHunterAfterKillingPerk_WithPE_Test1() {
        // given
        givenFl("     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n" +
                "  ☺  \n");

        // when hero0 plant Remote_Control potions. and go to position
        hero(0).addPerk(new PotionRemoteControl(1, PERK_TIMEOUT_FOR_TEST));
        hero(0).addPerk(new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));
        hero(1).addPerk(new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));


        hero(0).act();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        newPerk(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        assertF("  ☺  \n" +
                "     \n" +
                " 5r  \n" +
                "     \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "     \n" +
                " 5r  \n" +
                "     \n" +
                "  ☺  \n", 1);

        // when heroes explode potion and kill perk
        hero(0).act(2);
        hero(1).act(2);
        tick();

        // then
        events.verifyAllEvents(
                "listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");

        assertF("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", 1);

        assertEquals(2, field.hunters().size());

        // when next tick two ghostHunters should been visible
        tick();

        // then
        assertF("  ☺  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", 1);
    }

    @Test
    public void shouldBothHeroesGerPersonalHunterAfterKillingPerk_WithPE_Test2() {
        // given
        settings.bool(STEAL_POINTS, false);

        givenFl("     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n" +
                "  ☺  \n");

        //when hero0 plant potion and go to position
        hero(1).addPerk(new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        hero(0).act();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        newPerk(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        assertF("  ☺  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ☺  \n", 1);

        // when hero1 explode potion, hero0 get events after the potion timer end
        hero(1).act(2);
        tick();

        // then both heroes kill perk
        events.verifyAllEvents(
                "listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");

        assertF("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", 1);

        assertEquals(2, field.hunters().size());

        // when next tick two ghostHunters should been visible
        tick();

        // then
        assertF("  ☺  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", 1);
    }

    @Test
    public void shouldNotGetGhostHunterWhenPointsStealing_WithPE() {
        // given
        settings.bool(STEAL_POINTS, true);

        givenFl("     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n" +
                "  ☺  \n");

        //when hero0 plant potion and go to position
        hero(1).addPerk(new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        hero(0).act();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        newPerk(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        assertF("  ☺  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ☺  \n", 1);

        // when hero1 explode potion, hero0 does not get events after the potion timer end
        hero(1).act(2);
        tick();

        // then both heroes kill perk
        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DROP_PERK]\n");

        assertF("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", 1);

        assertEquals(1, field.hunters().size());

        // when next tick only one ghostHunter should been visible
        tick();

        // then
        assertF("  ☺  \n" +
                "     \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", 0);

        assertF("  ♥  \n" +
                "     \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", 1);
    }
}
