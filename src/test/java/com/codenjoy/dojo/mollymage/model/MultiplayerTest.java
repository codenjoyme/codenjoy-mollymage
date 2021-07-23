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


import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.perks.PotionImmune;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.PointImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_TEAMS_PER_ROOM;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MultiplayerTest extends AbstractMultiplayerTest {

    public static final int CATCH_PERK_SCORE_FOR_TEST = 10;

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void shouldGameReturnsRealJoystick() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(1).up();
        tick();

        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        assertFalse(hero(0).isAlive());
        assertTrue(hero(1).isAlive());

        Joystick joystick1 = game(0).getJoystick();
        Joystick joystick2 = game(0).getJoystick();

        // when
        dice(dice,
                0, 0,
                1, 0);
        game(0).newGame();
        game(1).newGame();

        // then
        assertNotSame(joystick1, game(0).getJoystick());
        assertNotSame(joystick2, game(0).getJoystick());
    }

    @Test
    public void shouldGetTwoHeroesOnBoard() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        assertSame(hero(0), game(0).getJoystick());
        assertSame(hero(1), game(1).getJoystick());

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n", game(1));
    }

    @Test
    public void shouldOnlyOneListenerWorksWhenOneHeroKillAnother() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉♣   \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [DIED]\n");
    }

    @Test
    public void shouldPrintOtherBombHero() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♠☺   \n", game(1));
    }

    @Test
    public void shouldHeroCantGoToAnotherHero() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).right();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", game(0));
    }
    
    // бомбермен может идти на приведение, при этом он умирает
    @Test
    public void shouldKllOtherHeroWhenHeroGoToGhost() {
        ghostAt(2, 0);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        hero(1).right();
        tick();
        // от имени наблюдателя вижу опасность - приведение
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +

                "☺ &  \n", game(0));

        // от имени жертвы вижу свой трупик
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥ Ѡ  \n", game(1));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // если митчопер убил другого бомбермена, как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherHeroWhenGhostGoToIt() {
        Ghost chopper = ghostAt(2, 0);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        chopper.setDirection(Direction.LEFT);
        tick();

        // от имени наблюдателя я там вижу опасность - приведение, мне не интересны останки игроков
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&   \n", game(0));

        // от имени жертвы я вижу свой трупик, мне пофиг уже что на карте происходит, главное где поставить памятник герою
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥Ѡ   \n", game(1));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // А что если бомбермен идет на приведение а тот идет на встречу к нему - бомбермен проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherHeroWhenGhostAndHeroMoves() {
        Ghost chopper = ghostAt(2, 0);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        chopper.setDirection(Direction.LEFT);
        hero(1).right();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&♣  \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥&Ѡ  \n", game(1));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    //  бомбермены не могут ходить по бомбам ни по своим ни по чужим
    @Test
    public void shouldHeroCantGoToBombFromAnotherHero() {
        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(1).act();
        hero(1).right();
        tick();

        hero(1).right();
        hero(0).right();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺3 ♥ \n", game(0));

        hero(1).left();
        tick();

        hero(1).left();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1♥  \n", game(0));
    }

    @Test
    public void shouldBombKillAllHero() {
        shouldHeroCantGoToBombFromAnotherHero();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉♣  \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣҉Ѡ  \n", game(1));
    }

    @Test
    public void shouldNewGamesWhenKillAll() {
        shouldBombKillAllHero();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉♣  \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣҉Ѡ  \n", game(1));

        Level level1 = settings.getLevel();
        Level level2 = settings.getLevel();
        when(settings.getHero(any(Level.class)))
                .thenReturn(
                        new Hero(level1),
                        new Hero(level2)
                );

        dice(dice,
                0, 0,
                1, 0);
        game(0).newGame();
        game(1).newGame();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n", game(1));
    }

    // на поле можно чтобы каждый поставил то количество бомб которое ему позволено и не более того
    @Test
    public void shouldTwoBombsOnBoard() {
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", game(0));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n", game(0));

    }

    @Test
    public void shouldNotTeammateGetPerk_AfterFirstPlayerPickUp_withEnemy() {
        settings.integer(POTIONS_COUNT, 1);
        settings.integer(CATCH_PERK_SCORE, CATCH_PERK_SCORE_FOR_TEST);
        settings.bool(PERK_WHOLE_TEAM_GET,false);
        settings.integer(ROUNDS_TEAMS_PER_ROOM,2);

        dice(dice,
                0, 0,
                1, 0,
                2, 0);

        //set up 3 players, 2 in one team, and 1 perk on field
        givenBoard(3);
        player(0).setTeamId(0);
        player(1).setTeamId(0);
        player(2).setTeamId(1);
        field.perks().add(new PerkOnBoard(new PointImpl(0, 1), new PotionImmune(settings.integer(TIMEOUT_POTION_IMMUNE))));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "i    \n" +
                "☺♥♡  \n", game(0));

        //heroes should not have any perks
        assertEquals(0, hero(0).getPerks().size());

        assertEquals(0, hero(1).getPerks().size());

        assertEquals(0, hero(2).getPerks().size());

        //when first hero get perk
        hero(0).up();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " ♥♡  \n", game(0));

        //teammate should not get perk
        assertEquals(1, hero(0).getPerks().size());
        assertEquals(0, hero(1).getPerks().size());
        assertEquals(0, hero(2).getPerks().size());
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                               "listener(1) => []\n" +
                               "listener(2) => []\n");

        assertEquals(CATCH_PERK_SCORE_FOR_TEST, hero(0).scores());
        assertEquals(0, hero(1).scores());
        assertEquals(0, hero(2).scores());
    }

    @Test
    public void shouldTeammateGetPerk_AfterFirstPlayerPickUp_withEnemy() {
        settings.integer(POTIONS_COUNT, 1);
        settings.integer(CATCH_PERK_SCORE, CATCH_PERK_SCORE_FOR_TEST);
        settings.bool(PERK_WHOLE_TEAM_GET, true);
        settings.integer(ROUNDS_TEAMS_PER_ROOM, 2);

        dice(dice,
                0, 0,
                1, 0,
                2, 0);

        //set up 3 players, 2 in one team, and 1 perk on field
        givenBoard(3);
        player(0).setTeamId(0);
        player(1).setTeamId(0);
        player(2).setTeamId(1);
        field.perks().add(new PerkOnBoard(new PointImpl(0, 1), new PotionImmune(settings.integer(TIMEOUT_POTION_IMMUNE))));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "i    \n" +
                "☺♥♡  \n", game(0));


        //heroes should not have any perks
        assertEquals(0,player(0).getHero().getPerks().size());
        assertEquals(0,player(1).getHero().getPerks().size());
        assertEquals(0,player(2).getHero().getPerks().size());

        //when first hero get perk
        hero(0).up();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " ♥♡  \n", game(0));

        //teammate should get perk to
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                               "listener(1) => []\n" +
                               "listener(2) => []\n");

        assertEquals(1,player(0).getHero().getPerks().size());
        assertEquals(1,player(1).getHero().getPerks().size());
        assertEquals(0,player(2).getHero().getPerks().size());

        //scores for perk earned only one hero, who picked up perk
        assertEquals(CATCH_PERK_SCORE_FOR_TEST,player(0).getHero().scores());
        assertEquals(0,player(1).getHero().scores());
        assertEquals(0,player(2).getHero().scores());
    }

    @Test
    public void shouldTwoBombsOnBoard_withEnemy() {
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);
        player(0).setTeamId(0);
        player(1).setTeamId(1);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♡   \n" +
                "44   \n", game(0));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺♡   \n" +
                "     \n" +
                "33   \n", game(0));

    }

    @Test
    public void shouldFourBombsOnBoard() {
        settings.integer(POTIONS_COUNT, 2);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", game(0));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n" +
                "33   \n", game(0));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        asrtBrd("     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n" +
                "22   \n", game(0));
    }

    @Test
    public void shouldFourBombsOnBoard_checkTwoBombsPerHero() {
        settings.integer(POTIONS_COUNT, 2);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "4♥   \n", game(0));

        hero(0).act();
        hero(0).up();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                "4    \n" +
                "3♥   \n", game(0));

        hero(0).act();
        hero(0).up();

        tick();

        asrtBrd("     \n" +
                "☺    \n" +
                "     \n" +
                "3    \n" +
                "2♥   \n", game(0));
    }

    @Test
    public void shouldFireEventWhenKillWallOnlyForOneHero() {
        destroyWallAt(0, 0);

        dice(dice,
                1, 0,
                1, 1);
        givenBoard(2);

        hero(0).act();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        tick();
        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => []\n");

        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n", game(0));
    }

    @Test
    public void shouldFireEventWhenKillGhost() {
        ghostAt(0, 0);

        dice(dice,
                1, 0,
                1, 1);
        givenBoard(2);

        hero(0).act();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        hero(0).right();
        hero(1).up();
        tick();
        tick();
        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => []\n");

        tick();

        asrtBrd(" ♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n", game(0));
    }

    @Test
    public void bug() {
        destroyWallAt(0, 0);
        ghostAt(1, 0);
        ghostAt(2, 0);

        dice(dice,
                1, 1,
                2, 1);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺♥  \n" +
                "#&&  \n", game(0));

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).left();
        hero(1).right();
        tick();

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺҉҉♥ \n" +
                "҉҉҉҉ \n" +
                "#xx  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺  ♥ \n" +
                "     \n" +
                "#    \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseDied() {
        destroyWallAt(1, 0);

        dice(dice,
                0, 0,
                2, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        destroyWallAt(1, 0);

        dice(dice,
                0, 0,
                2, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺#♥  \n", game(0));

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "3#3  \n", game(0));

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉H҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseDied() {
        destroyWallAt(2, 0);
        destroyWallAt(1, 0);

        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX]\n");

         tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied() {
        destroyWallAt(2, 2);

        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBoard(4);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "҉ѠH♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(1) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                        "listener(3) => [DIED, KILL_TREASURE_BOX]\n");

        tick();

        asrtBrd("     \n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied_caseNotEqualPosition() {
        destroyWallAt(1, 1);
        destroyWallAt(2, 2);
        destroyWallAt(0, 2);

        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBoard(4);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "HѠH♣҉\n" +  // первую стенку подбил монополист, центральную все
                " H♣҉ \n" +  // эту стенку подбили только лвое
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_TREASURE_BOX]\n");

        tick();

        asrtBrd("     \n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {
        destroyWallAt(2, 0);
        destroyWallAt(1, 0);

        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        tick();
        tick();

        asrtBrd("     \n" +
                "☺  ♥ \n" +
                "҉  ҉ \n" +
                "҉  ҉ \n" +
                "҉HH҉҉\n", game(0));

        // по 1 ачивке за стенку, потому что взрывная волна не проходит через стенку
        events.verifyAllEvents(
                "listener(0) => [KILL_TREASURE_BOX]\n" +
                "listener(1) => [KILL_TREASURE_BOX]\n");

        tick();

        asrtBrd("     \n" +
                "☺  ♥ \n" +
                "     \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseDied() {
        ghostAt(1, 0);

        dice(dice,
                0, 0,
                2, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();
        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenGhost_caseAlive() {
        ghostAt(1, 0);

        dice(dice,
                0, 0,
                2, 0);
        givenBoard(2);

        hero(0).act();
        hero(0).up();
        hero(1).act();
        hero(1).up();
        tick();

        hero(0).up();
        hero(1).up();
        tick();

        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "҉ ҉  \n" +
                "҉x҉҉ \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourGhosts_caseDied() {
        ghostAt(2, 2);

        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBoard(4);

        hero(0).act();
        hero(1).act();
        hero(2).act();
        hero(3).act();
        tick();

        tick();
        tick();
        tick();
        tick();

        asrtBrd("  ҉  \n" +
                " ҉♣҉ \n" +
                "҉Ѡx♣҉\n" +
                " ҉♣҉ \n" +
                "  ҉  \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_GHOST]\n" +
                "listener(2) => [DIED, KILL_GHOST]\n" +
                "listener(3) => [DIED, KILL_GHOST]\n");

        tick();

        asrtBrd("     \n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldPerkCantSpawnFromMeetChopper() {
        ghostAt(1, 0);

        dice(dice,
                0, 0);
        givenBoard(1);

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

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉x   \n", game(0));

        events.verifyAllEvents("[KILL_GHOST]");

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        destroyWallAt(2, 2);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBoard(4);

        // бомба, которой все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                "&24♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "☺    \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "x҉҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                "☺҉҉҉ \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        asrtBrd("     \n" +
                "   ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "☺    \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_withEnemies() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        destroyWallAt(2, 2);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBoard(4);
        player(0).setTeamId(0);
        player(1).setTeamId(0);
        player(2).setTeamId(1);
        player(3).setTeamId(1);

        // бомба, которой все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♡♡ \n",
                game(0));

        hero(0).move(0, 0);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                "&24♤&\n" +
                " 1#3 \n" +
                "&♠2♤&\n" +
                "☺    \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_ENEMY_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "x҉҉♧x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♧x\n" +
                "☺҉҉҉ \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        asrtBrd("     \n" +
                "   ♧ \n" +
                "     \n" +
                " ♣ ♧ \n" +
                "☺    \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom_caseKillAll() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

        destroyWallAt(2, 2);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBoard(4);

        // бомба, которой все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                "&☻4♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "     \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n" +
                "listener(1) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(2) => [DIED, KILL_OTHER_HERO, KILL_GHOST, KILL_TREASURE_BOX]\n" +
                "listener(3) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX, KILL_GHOST]\n");

        asrtBrd(" ҉҉҉ \n" +
                "xѠ҉♣x\n" +
                "҉҉H҉҉\n" +
                "x♣҉♣x\n" +
                " ҉҉҉ \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        asrtBrd("     \n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenNotBigBadaboom_caseKillAll() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, false)
                .perksSettings().dropRatio(0);

        destroyWallAt(2, 2);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBoard(4);

        // бомба, которой все пордорвем
        hero(0).move(1, 2);
        hero(0).act();
        tick();

        hero(0).move(1, 3);
        hero(0).act();
        hero(0).move(0, 0);

        hero(1).move(2, 1);
        hero(1).act();
        hero(1).move(1, 1);
        hero(1).act();
        hero(1).move(1, 0);
        tick();

        hero(2).move(3, 2);
        hero(2).act();
        hero(2).move(3, 1);
        hero(2).act();
        hero(2).move(2, 0);
        tick();

        hero(3).move(2, 3);
        hero(3).act();
        hero(3).move(3, 3);
        hero(3).act();
        hero(3).move(3, 0);

        tick();

        asrtBrd("     \n" +
                "&244&\n" +
                " 1#3 \n" +
                "&223&\n" +
                "☺♥♥♥ \n",
                game(0));

        hero(0).move(1, 3);
        hero(1).move(1, 1);
        hero(2).move(3, 1);
        hero(3).move(3, 3);

        asrtBrd("     \n" +
                "&☻4♠&\n" +
                " 1#3 \n" +
                "&♠2♠&\n" +
                "     \n",
                game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED, KILL_OTHER_HERO, KILL_TREASURE_BOX]\n" +
                "listener(1) => [DIED]\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        asrtBrd("     \n" +
                "&Ѡ3♠&\n" +
                "҉҉H2 \n" +
                "&11♠&\n" +
                "     \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(2) => [DIED]\n" +
                "listener(3) => []\n");

        asrtBrd(" ҉   \n" +
                "xѠ2♠&\n" +
                " ҉҉1 \n" +
                "x♣҉1&\n" +
                " ҉҉  \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(3) => [DIED]\n");

        asrtBrd("     \n" +
                " Ѡ11&\n" +
                "  ҉҉҉\n" +
                " ♣҉♣x\n" +
                "   ҉ \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => [KILL_GHOST]\n");

        asrtBrd("  ҉҉ \n" +
                " Ѡ҉♣x\n" +
                "  ҉҉ \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));

        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => []\n" +
                "listener(2) => []\n" +
                "listener(3) => []\n");

        asrtBrd("     \n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }
}
