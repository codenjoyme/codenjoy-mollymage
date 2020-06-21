package com.codenjoy.dojo.bomberman.model;

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


import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.round.RoundSettingsWrapper;
import org.junit.Test;

import static com.codenjoy.dojo.bomberman.model.BombermanTest.DestroyWallAt;
import static com.codenjoy.dojo.bomberman.model.BombermanTest.MeatChopperAt;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SingleTest extends AbstractSingleTest {

    @Test
    public void shouldGameReturnsRealJoystick() {
        givenBoard();
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
        game(0).newGame();
        game(1).newGame();

        // then
        assertNotSame(joystick1, game(0).getJoystick());
        assertNotSame(joystick2, game(0).getJoystick());
    }

    @Test
    public void shouldGetTwoHeroesOnBoard() {
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [DIED]\n");
    }

    @Test
    public void shouldPrintOtherBombHero() {
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

        hero(0).right();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n", game(0));
    }
    
    // бомбермен может идти на митчопера, при этом он умирает
    @Test
    public void shouldKllOtherHeroWhenHeroGoToMeatChopper() {
        walls = new MeatChopperAt(2, 0, new WallsImpl());
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        hero(1).right();
        tick();
        // от имени наблюдателя вижу опасность - митчопера
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

        verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // если митчопер убил другого бомбермена, как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherHeroWhenMeatChopperGoToIt() {
        meatChopperAt(2, 0);
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        dice(meatDice, Direction.LEFT.value());
        tick();

        // от имени наблюдателя я там вижу опасность - митчопера, мне не интересны останки игроков
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

        verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // А что если бомбермен идет на митчопера а тот идет на встречу к нему - бомбермен проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherHeroWhenMeatChopperAndHeroMoves() {
        meatChopperAt(2, 0);
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", game(0));

        dice(meatDice, Direction.LEFT.value());
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

        verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    //  бомбермены не могут ходить по бомбам ни по своим ни по чужим
    @Test
    public void shouldHeroCantGoToBombFromAnotherHero() {
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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

    private void givenBoard() {
        super.givenBoard(2);
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
        when(settings.getHero(any(Level.class))).thenReturn(new Hero(level, heroDice), new Hero(level, heroDice));
        dice(heroDice,
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
        bombsCount = 1;
        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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
    public void shouldFourBombsOnBoard() {
        bombsCount = 2;

        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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
        bombsCount = 2;

        dice(heroDice,
                0, 0,
                1, 0);
        givenBoard();

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
        walls = new DestroyWallAt(0, 0, new WallsImpl());
        dice(heroDice,
                1, 0,
                1, 1);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [KILL_DESTROY_WALL]\n" +
                "listener(1) => []\n");
    }

    @Test
    public void shouldFireEventWhenKillMeatChopper() {
        walls = new MeatChopperAt(0, 0, new WallsImpl());
        dice(heroDice,
                1, 0,
                1, 1);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [KILL_MEAT_CHOPPER]\n" +
                "listener(1) => []\n");
    }

    @Test
    public void bug() {
        walls = new DestroyWallAt(0, 0, new MeatChopperAt(1, 0, new MeatChopperAt(2, 0, new WallsImpl())));
        dice(heroDice,
                1, 1,
                2, 1);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [KILL_MEAT_CHOPPER]\n" +
                "listener(1) => [KILL_MEAT_CHOPPER]\n");
    }

    @Override
    protected RoundSettingsWrapper getRoundSettings() {
        return BombermanTest.getRoundSettings();
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseDied() {
        walls = new DestroyWallAt(1, 0, new WallsImpl());
        dice(heroDice,
                0, 0,
                2, 0);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(1) => [DIED, KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenDestroyWall_caseAlive() {
        walls = new DestroyWallAt(1, 0, new WallsImpl());
        dice(heroDice,
                0, 0,
                2, 0);
        givenBoard();

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
                "҉H҉҉ \n", game(0));

        verifyAllEvents(
                "listener(0) => [KILL_DESTROY_WALL]\n" +
                "listener(1) => [KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseDied() {
        walls = new DestroyWallAt(2, 0,
                    new DestroyWallAt(1, 0,
                            new WallsImpl()));

        bombsPower = 2;

        dice(heroDice,
                0, 0,
                3, 0);
        givenBoard();

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
        verifyAllEvents(
                "listener(0) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(1) => [DIED, KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied() {
        walls = new DestroyWallAt(2, 2, new WallsImpl());

        dice(heroDice,
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

        verifyAllEvents(
                "listener(0) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(1) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(2) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(3) => [DIED, KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourDestroyWalls_caseDied_caseNotEqualPosition() {
        walls = new DestroyWallAt(1, 1,
                    new DestroyWallAt(2, 2,
                        new DestroyWallAt(0, 2,
                            new WallsImpl())));

        dice(heroDice,
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

        verifyAllEvents(
                "listener(0) => [DIED, KILL_DESTROY_WALL, KILL_DESTROY_WALL, KILL_DESTROY_WALL]\n" +
                "listener(1) => [DIED, KILL_DESTROY_WALL, KILL_DESTROY_WALL]\n" +
                "listener(2) => [DIED, KILL_DESTROY_WALL]\n" +
                "listener(3) => [DIED, KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenTwoDestroyWalls_caseAlive() {
        walls = new DestroyWallAt(2, 0,
                new DestroyWallAt(1, 0,
                        new WallsImpl()));
        bombsPower = 2;

        dice(heroDice,
                0, 0,
                3, 0);
        givenBoard();

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
        verifyAllEvents(
                "listener(0) => [KILL_DESTROY_WALL]\n" +
                "listener(1) => [KILL_DESTROY_WALL]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenMeatChopper_caseDied() {
        walls = new MeatChopperAt(1, 0, new WallsImpl());
        dice(heroDice,
                0, 0,
                2, 0);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [DIED, KILL_MEAT_CHOPPER]\n" +
                "listener(1) => [DIED, KILL_MEAT_CHOPPER]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenMeatChopper_caseAlive() {
        walls = new MeatChopperAt(1, 0, new WallsImpl());
        dice(heroDice,
                0, 0,
                2, 0);
        givenBoard();

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

        verifyAllEvents(
                "listener(0) => [KILL_MEAT_CHOPPER]\n" +
                "listener(1) => [KILL_MEAT_CHOPPER]\n");
    }

    @Test
    public void shouldCrossBlasts_checkingScores_whenFourMeatChoppers_caseDied() {
        walls = new MeatChopperAt(2, 2, new WallsImpl());

        dice(heroDice,
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

        verifyAllEvents(
                "listener(0) => [DIED, KILL_MEAT_CHOPPER]\n" +
                "listener(1) => [DIED, KILL_MEAT_CHOPPER]\n" +
                "listener(2) => [DIED, KILL_MEAT_CHOPPER]\n" +
                "listener(3) => [DIED, KILL_MEAT_CHOPPER]\n");
    }
}
