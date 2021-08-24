package com.codenjoy.dojo.mollymage.game.multiplayer;

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
import com.codenjoy.dojo.mollymage.game.multiplayer.AbstractMultiplayerTest;
import com.codenjoy.dojo.mollymage.model.Hero;
import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.model.items.ghost.GhostHunter;
import com.codenjoy.dojo.mollymage.model.items.perks.*;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.PointImpl;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_TEAMS_PER_ROOM;
import static org.junit.Assert.*;

public class MultiplayerTest extends AbstractMultiplayerTest {

    public static final int CATCH_PERK_SCORE_FOR_TEST = 10;
    public static final int PERK_TIMEOUT_FOR_TEST = 10;

    @Before
    public void setup() {
        super.setup();
    }

// _____________________________________________________GAME_TEST_______________________________________________________

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
    public void shouldPrintOtherPotionHero() {
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
    public void shouldNewGamesWhenKillAll() {
        shouldPotionKillAllHero();

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

    @Test
    public void bug() {
        ghostAt(1, 0);
        ghostAt(2, 0);

        dice(dice,
                1, 1,
                2, 1);
        givenBoard(2);

        boxesCount(1);
        boxAt(0, 0);

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

// ______________________________________________________MOVEMENT_______________________________________________________

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

    // герои не могут ходить по зелью ни по своему ни по чужому
    @Test
    public void shouldHeroCantGoToPotionFromAnotherHero() {
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

// _______________________________________________________POTION________________________________________________________

    // на поле можно чтобы каждый поставил то количество
    // зелья которое ему позволено и не более того
    @Test
    public void shouldTwoPotionsOnBoard() {
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
    public void shouldTwoPotionsOnBoard_withEnemy() {
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);
        player(0).inTeam(0);
        player(1).inTeam(1);

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
    public void shouldFourPotionsOnBoard() {
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
    public void shouldFourPotionsOnBoard_checkTwoPotionsPerHero() {
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

// ____________________________________________________KILL_/_DEATH_____________________________________________________

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

    // герой может идти на привидение, при этом он умирает
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
        // от имени наблюдателя вижу опасность - привидение
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

    // если привидение убил другого героя,
    // как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherHeroWhenGhostGoToIt() {
        Ghost ghost = ghostAt(2, 0);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        ghost.setDirection(Direction.LEFT);
        tick();

        // от имени наблюдателя я там вижу опасность - привидение, мне не интересны останки игроков
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

    // А что если герой идет на привидение а тот идет на
    // встречу к нему - герой проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherHeroWhenGhostAndHeroMoves() {
        Ghost ghost = ghostAt(2, 0);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        ghost.setDirection(Direction.LEFT);
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

    @Test
    public void shouldPotionKillAllHero() {
        shouldHeroCantGoToPotionFromAnotherHero();

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

// _______________________________________________________EVENTS________________________________________________________

    @Test
    public void shouldFireEventWhenKillWallOnlyForOneHero() {

        dice(dice,
                1, 0,
                1, 1);
        givenBoard(2);

        boxesCount(1);
        boxAt(0, 0);

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

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd(" ♥  #\n" +
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
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseDied() {
        dice(dice,
                0, 0,
                2, 0);
        givenBoard(2);

        boxesCount(1);
        boxAt(1, 0);

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

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd("    #\n" +
                "     \n" +
                "     \n" +
                "Ѡ ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        dice(dice,
                0, 0,
                2, 0);

        givenBoard(2);

        boxesCount(1);
        boxAt(1, 0);

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

        dice(dice, 4, 4); // новая коробка
        tick();

        asrtBrd("    #\n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseDied() {
        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBoard(2);

        boxesCount(2);
        boxAt(2, 0);
        boxAt(1, 0);

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

        dice(dice, // новые коробки
                4, 4,
                4, 3);
        tick();

        asrtBrd("    #\n" +
                "    #\n" +
                "     \n" +
                "Ѡ  ♣ \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied() {
        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBoard(4);

        boxesCount(1);
        boxAt(2, 2);

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

        dice(dice, // новые коробки
                4, 4);
        tick();

        asrtBrd("    #\n" +
                "  ♣  \n" +
                " Ѡ ♣ \n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied_caseNotEqualPosition() {
        dice(dice,
                1, 2,
                2, 1,
                3, 2,
                2, 3);
        givenBoard(4);

        boxesCount(3);
        boxAt(1, 1);
        boxAt(2, 2);
        boxAt(0, 2);

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

        dice(dice, // новые коробки
                4, 4,
                4, 3,
                4, 2);
        tick();

        asrtBrd("    #\n" +
                "  ♣ #\n" +
                " Ѡ ♣#\n" +
                "  ♣  \n" +
                "     \n", game(0));
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {

        settings.integer(POTION_POWER, 2);

        dice(dice,
                0, 0,
                3, 0);
        givenBoard(2);

        boxesCount(2);
        boxAt(2, 0);
        boxAt(1, 0);

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


        dice(dice, // новые коробки
                4, 4,
                4, 3);
        tick();

        asrtBrd("    #\n" +
                "☺  ♥#\n" +
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
    public void shouldCrossBlasts_checkingScores_whenBigBadaboom() {
        settings.integer(POTIONS_COUNT, 2)
                .bool(BIG_BADABOOM, true)
                .perksSettings().dropRatio(0);

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

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
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

        boxesCount(0); // больше не надо коробок
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

        dice(dice,
                0, 0,
                1, 0,
                2, 0,
                3, 0);
        givenBoard(4);

        ghostsCount(4);
        ghostAt(0, 1);
        ghostAt(0, 3);
        ghostAt(4, 1);
        ghostAt(4, 3);

        boxesCount(1);
        boxAt(2, 2);

        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);
        player(3).inTeam(1);

        // зелье, которым все пордорвем
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

        boxesCount(0); // больше не надо коробок
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

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
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

        dice(dice, // новые коробки
                4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n");

        asrtBrd("    #\n" +
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

        boxesCount(1);
        boxAt(2, 2);

        // зелье, которым все пордорвем
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

        dice(dice, // новые коробки
                4, 4);
        tick();

        events.verifyAllEvents(
                "listener(0) => [KILL_GHOST]\n" +
                        "listener(1) => [KILL_OTHER_HERO, KILL_GHOST]\n" +
                        "listener(2) => [DIED]\n" +
                        "listener(3) => []\n");

        asrtBrd(" ҉  #\n" +
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

        asrtBrd("    #\n" +
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

        asrtBrd("  ҉҉#\n" +
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

        asrtBrd("    #\n" +
                " Ѡ ♣ \n" +
                "     \n" +
                " ♣ ♣ \n" +
                "     \n", game(0));
    }

// ________________________________________________________PERKS________________________________________________________

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
        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);
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
        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);
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

    /**  hero1 should get score for killing hero2 when then different blasts crossed
     *   PT - Poison Thrower
     */
    @Test
    public void shouldKillEnemyByPTAndScorePoints_whenCrossBlast() {
        // given
        givenBoardForPoisonThrower();
        int killScore = 10;
        settings.integer(KILL_OTHER_HERO_SCORE, killScore);
        Hero hero1 = hero(0);
        Hero hero2 = hero(1);
        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "☺ ♥  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "♥ ☺  \n", game(1));

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        field.tick();

        // then
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        //when heroes are going on the position
        hero1.up();
        hero2.up();
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "     \n" +
                "  3  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "     \n" +
                "  3  \n", game(1));

        // when potion boom, hero1 should shoot by poison thrower
        field.tick();
        field.tick();
        hero1.right();
        hero1.act(1);
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺҉♣  \n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥҉Ѡ  \n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_OTHER_HERO]\n" +
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
        givenBoardForPoisonThrower();
        ghostAt(2,2);
        int killScore = 10;
        settings.integer(KILL_GHOST_SCORE, killScore);
        Hero hero1 = hero(0);
        Hero hero2 = hero(1);
        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        asrtBrd("     \n" +
                "     \n" +
                "  &  \n" +
                "T    \n" +
                "☺ ♥  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "  &  \n" +
                "T    \n" +
                "♥ ☺  \n", game(1));

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        field.tick();

        hero1.up();
        hero2.right();
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺ &  \n" +
                "   ♥ \n" +
                "  3  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥ &  \n" +
                "   ☺ \n" +
                "  3  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potion boom, hero1 should shoot by poison thrower
        field.tick();
        field.tick();
        hero1.right();
        hero1.act(1);
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺҉x  \n" +
                "  ҉♥ \n" +
                "҉҉҉҉҉\n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥҉x  \n" +
                "  ҉☺ \n" +
                "҉҉҉҉҉\n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_GHOST]\n" +
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
        givenBoardForPoisonThrower();
        boxesCount(1);
        boxAt(2,2);
        int killScore = 10;
        settings.integer(KILL_WALL_SCORE, killScore);
        Hero hero1 = hero(0);
        Hero hero2 = hero(1);
        assertEquals(0, hero1.scores());
        assertEquals(0, hero2.scores());

        // then
        asrtBrd("     \n" +
                "     \n" +
                "  #  \n" +
                "T    \n" +
                "☺ ♥  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "  #  \n" +
                "T    \n" +
                "♥ ☺  \n", game(1));

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        field.tick();

        hero1.up();
        hero2.right();
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺ #  \n" +
                "   ♥ \n" +
                "  3  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥ #  \n" +
                "   ☺ \n" +
                "  3  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potion boom - hero1 should shoot by poison thrower
        field.tick();
        field.tick();
        hero1.right();
        hero1.act(1);
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺҉H  \n" +
                "  ҉♥ \n" +
                "҉҉҉҉҉\n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥҉H  \n" +
                "  ҉☺ \n" +
                "҉҉҉҉҉\n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_TREASURE_BOX]\n" +
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
        givenBoardForPoisonThrower();
        int killScore = 10;
        settings.integer(KILL_GHOST_SCORE, killScore);
        Hero hero1 = hero(0);
        Hero hero2 = hero(1);

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "☺ ♥  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "T    \n" +
                "♥ ☺  \n", game(1));

        // when hero2 set potion, hero1 get perk
        hero1.up();
        hero2.act();
        hero2.up();
        field.tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺ ♥  \n" +
                "  4  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "  4  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");


        // when move heroes on position and set perk for destroy
        hero1.up();
        hero2.right();
        field.tick();
        hero2.right();
        field.tick();
        hero2.up();
        field.tick();
        perkAt(2,2,new PotionCountIncrease(1,10));

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺ c ♥\n" +
                "     \n" +
                "  1  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥ c ☺\n" +
                "     \n" +
                "  1  \n", game(1));


        // when both heroes kill one perk
        hero1.right();
        hero1.act(1);
        field.tick();

        // then two GhostHunters should born on the one Point
        asrtBrd("     \n" +
                "     \n" +
                "☺҉x ♥\n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥҉x ☺\n" +
                "  ҉  \n" +
                "҉҉҉҉҉\n", game(1));
        events.verifyAllEvents("listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");
        assertEquals(2, field.hunters().size());


        // when field tick
        field.tick();

        // then both hunters are visible and haunting heroes
        asrtBrd("     \n" +
                "     \n" +
                "☺x x♥\n" +
                "     \n" +
                "     \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥x x☺\n" +
                "     \n" +
                "     \n", game(1));
    }

    private void givenBoardForPoisonThrower() {
        dice(dice,
                0, 0, 2, 0);
        givenBoard(2);
        perkAt(0, 1, new PoisonThrower(10));
        settings.integer(POTION_POWER, 2);
        settings.integer(CATCH_PERK_SCORE, 0);
    }

    @Test
    public void shouldPerkCantSpawnFromGhost() {
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
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test1() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        //when hero0 catch perk and both heroes act and move
        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));
        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "44   \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        //when hero0 with PE perk explode own potion and hero1's simple potion
        hero(0).act(2);
        hero(0).up();

        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", game(1));
        events.verifyAllEvents("listener(0) => []\n" +
                "listener(1) => []\n");
    }

    @Test
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test2() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);
        //when both heroes set Remote_Control potions. Hero0 get PE perk
        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));
        hero(0).addPerk(new PotionRemoteControl(1, 10));
        hero(1).addPerk(new PotionRemoteControl(1, 10));

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "55   \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "55   \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when hero0 uses PE perk and explode both potions
        hero(0).act(2);
        hero(0).up();

        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉҉  \n", game(1));
        events.verifyAllEvents("listener(0) => []\n" +
                "listener(1) => []\n");
    }

    // Remote Control and Perk Exploder should works together.
    @Test
    public void shouldExplodeBothPotionsOnBoard_WithPE_Test3() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when hero0 sets usually potion. Hero1 sets RC potion.
        hero(1).addPerk(new PotionRemoteControl(1, 10));
        ghostAt(2, 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "45&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "45&  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");


        // when hero0 explode all, hero1 explode own remote control perk
        hero(0).act(2);
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then both heroes kill ghost
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    // Both Heroes have Perk Exploder.
    @Test
    public void shouldExplodeBothPotionsOnBoardAndKillGhost_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);

        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));
        perkAt(1, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

        // when heroes plant potions and catch perk
        ghostAt(2, 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "44&  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => [CATCH_PERK]\n");

        // when hero0 and hero1 explode all, both should kill ghost
        hero(0).act(2);
        hero(0).up();

        hero(1).act(2);
        hero(1).up();

        tick();

        // then both heroes kill ghost
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    @Test
    public void shouldPotionOwnerGetScoresTo_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1);
        settings.bool(STEAL_POINTS, false);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);
        ghostAt(2, 0);
        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

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
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "33&  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potions timers almost end
        tick();
        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "11&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "11&  \n", game(1));


        // when hero0 explode all, both heroes should earn scores
        hero(0).act(2);

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(0));
        events.verifyAllEvents("listener(0) => [KILL_GHOST]\n" +
                "listener(1) => [KILL_GHOST]\n");
    }

    @Test
    public void shouldNotPotionOwnerGetScores_WithPE() {
        // given
        settings.integer(POTIONS_COUNT, 1);
        settings.bool(STEAL_POINTS, true);

        dice(dice,
                0, 0,
                1, 0);
        givenBoard(2);
        ghostAt(2, 0);
        perkAt(0, 1, new PotionExploder(1, PERK_TIMEOUT_FOR_TEST));

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
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "33&  \n", game(1));
        events.verifyAllEvents("listener(0) => [CATCH_PERK]\n" +
                "listener(1) => []\n");

        // when potions timers almost end
        tick();
        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "11&  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n" +
                "11&  \n", game(1));

        // when hero0 explode all, only hero0 should kill ghost
        hero(0).act(2);

        tick();

        // then
        asrtBrd("     \n" +
                "     \n" +
                "☺♥   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(0));
        asrtBrd("     \n" +
                "     \n" +
                "♥☺   \n" +
                "҉҉   \n" +
                "҉҉x  \n", game(1));
        events.verifyAllEvents("listener(0) => [KILL_GHOST]\n" +
                "listener(1) => []\n");
    }

    @Test
    public void shouldBothHeroesGerPersonalHunterAfterKillingPerk_WithPE_Test1() {
        // given
        dice(dice,
                1, 2,
                2, 0);
        givenBoard(2);

        //when hero0 plant Remote_Control potions. and go to position
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
        perkAt(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        asrtBrd("  ☺  \n" +
                "     \n" +
                " 5r  \n" +
                "     \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "     \n" +
                " 5r  \n" +
                "     \n" +
                "  ☺  \n", game(1));

        // when heroes explode potion and kill perk
        hero(0).act(2);
        hero(1).act(2);
        tick();

        // then
        events.verifyAllEvents("listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");
        asrtBrd("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", game(1));
        assertEquals(2, field.hunters().size());

        // when next tick two ghostHunters should been visible
        tick();

        // then
        asrtBrd("  ☺  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", game(1));
    }

    @Test
    public void shouldBothHeroesGerPersonalHunterAfterKillingPerk_WithPE_Test2() {
        // given
        dice(dice,
                1, 2,
                2, 0);
        givenBoard(2);
        settings.bool(STEAL_POINTS, false);

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
        perkAt(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        asrtBrd("  ☺  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ☺  \n", game(1));

        // when hero1 explode potion, hero0 get events after the potion timer end
        hero(1).act(2);
        tick();

        // then both heroes kill perk
        events.verifyAllEvents("listener(0) => [DROP_PERK]\n" +
                "listener(1) => [DROP_PERK]\n");
        asrtBrd("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", game(1));
        assertEquals(2, field.hunters().size());

        // when next tick two ghostHunters should been visible
        tick();

        // then
        asrtBrd("  ☺  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "  x  \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", game(1));
    }

    @Test
    public void shouldNotGetGhostHunterWhenPointsStealing_WithPE() {
        // given
        dice(dice,
                1, 2,
                2, 0);
        givenBoard(2);
        settings.bool(STEAL_POINTS, true);

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
        perkAt(2, 2, new PotionRemoteControl(10, PERK_TIMEOUT_FOR_TEST));

        // then
        asrtBrd("  ☺  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "     \n" +
                " 1r  \n" +
                "     \n" +
                "  ☺  \n", game(1));

        // when hero1 explode potion, hero0 does not get events after the potion timer end
        hero(1).act(2);
        tick();

        // then both heroes kill perk
        events.verifyAllEvents("listener(0) => []\n" +
                "listener(1) => [DROP_PERK]\n");
        asrtBrd("  ☺  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                " ҉   \n" +
                "҉҉x  \n" +
                " ҉   \n" +
                "  ☺  \n", game(1));
        assertEquals(1, field.hunters().size());

        // when next tick only one ghostHunter should been visible
        tick();

        // then
        asrtBrd("  ☺  \n" +
                "     \n" +
                "     \n" +
                "  x  \n" +
                "  ♥  \n", game(0));
        asrtBrd("  ♥  \n" +
                "     \n" +
                "     \n" +
                "  x  \n" +
                "  ☺  \n", game(1));
    }

// _____________________________________________________________________________________________________________________
}
