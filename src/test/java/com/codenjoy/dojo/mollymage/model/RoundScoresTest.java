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

import com.codenjoy.dojo.mollymage.services.GameSettings;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.*;

public class RoundScoresTest extends AbstractGameTest {

    @Override
    protected GameSettings setupSettings() {
        return super.setupSettings()
                .bool(ROUNDS_ENABLED, true)
                .integer(ROUNDS_TIME_BEFORE_START, 5)
                .integer(ROUNDS_PER_MATCH, 3)
                .integer(ROUNDS_MIN_TICKS_FOR_WIN, 1)
                .integer(ROUNDS_TIME, 10)
                .integer(ROUNDS_TIME_FOR_WINNER, 2)
                .integer(TREASURE_BOX_COUNT, 0);
    }

    // если один игрок вынесет обоих, то должен получить за это очки
    @Test
    public void shouldGetWinRoundScores_whenKillAllOtherHeroes() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n" +
                " ☺   \n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                " ♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                " ♥   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥♥   \n" +
                " ☺   \n", 2);

        // when
        // когда я выношу одного игрока
        hero(1).dropPotion();
        tick();

        hero(1).right();
        tick();

        hero(1).up();
        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                " ♥   \n", 1);

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ♣   \n", 0);

        assertF("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ♣   \n", 1);

        assertF("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " Ѡ   \n", 2);

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +
                "listener(1) => [KILL_OTHER_HERO, KILL_OTHER_HERO, WIN_ROUND]\n" +
                "listener(2) => [HERO_DIED]\n");

        assertScores("hero(1)=1400");
    }

    @Test
    public void shouldGetWinRoundScores_whenKillAllEnemyHeroAndOtherHero() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n" +
                " ☺   \n");

        player(0).inTeam(0);
        player(1).inTeam(0);
        player(2).inTeam(1);

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                " ö   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                " ö   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "öö   \n" +
                " ☺   \n", 2);

        // when
        // когда я выношу одного игрока
        hero(1).dropPotion();
        tick();

        hero(1).right();
        tick();

        hero(1).up();
        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                " ö   \n", 1);

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ø   \n", 0);

        assertF("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ø   \n", 1);

        assertF("     \n" +
                "     \n" +
                " ҉ö  \n" +
                "ø҉҉  \n" +
                " Ѡ   \n", 2);

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +
                "listener(1) => [KILL_ENEMY_HERO, KILL_OTHER_HERO, WIN_ROUND]\n" +
                "listener(2) => [HERO_DIED]\n");

        assertScores("hero(1)=1700");
    }

    // если на карте один вынес другого, а последний противник покинул игру
    // - очки победителю положено вручить
    @Test
    public void shouldGetWinRoundScores_whenKillOneAndAnotherLeaveTheGame() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        givenFl("    ☺\n" + // тот кто покинет комнату
                "     \n" +
                "     \n" +
                "☺☺   \n" + // жертва и тот, кто побежит
                "     \n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");


        assertF("    ☺\n" +
                "     \n" +
                "     \n" +
                "♥♥   \n" +
                "     \n", 0);

        assertF("    ♥\n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n", 1);

        assertF("    ♥\n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n", 2);

        // when
        // когда я выношу одного игрока
        hero(2).dropPotion();
        tick();

        hero(2).right();
        tick();

        hero(2).up();
        tick();

        tick();

        // then
        assertF("    ♥\n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                "     \n", 2);

        // when
        tick();

        // then
        assertF("    ☺\n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " ҉   \n", 0);

        assertF("    ♥\n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ҉   \n", 1);

        assertF("    ♥\n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ҉   \n", 2);

        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n" +
                "listener(2) => [KILL_OTHER_HERO]\n");

        // when
        // а теперь самое интересное - выходим из комнаты оставшимся игроком
        field().remove(player(0));

        // then
        assertF("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " ҉   \n", 0);

        assertF("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ҉   \n", 1);

        assertF("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ҉   \n", 2);

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ♥  \n" +
                "♣    \n" +
                "     \n", 0);

        assertF("     \n" +
                "     \n" +
                "  ♥  \n" +
                "Ѡ    \n" +
                "     \n", 1);

        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♣    \n" +
                "     \n", 2);

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +   // за то что он трус )
                "listener(2) => [WIN_ROUND]\n"); // заслуженная победа

        assertScores("hero(2)=1200");

    }

    // если на карте один вынес другого, а последний противник покинул игру
    // - очки победителю положено вручить
    // но полсе этого если покинет комнату и второй, то мы не должны получить еще раз победные очки
    @Test
    public void shouldNotGetWinRoundScoresTwice_whenDieThenLeaveRoom() {
        // given
        // тут один игрок вынес другого, а третий после покинул комнату,
        // за что победитель получил свои очки, а все проигравшие - штрафы
        shouldGetWinRoundScores_whenKillOneAndAnotherLeaveTheGame();

        // when
        // а теперь самое интересное - выходим из комнаты оставшимся игроком
        field().remove(player(0));

        // then
        // никто больше не должен ничего получить
        verifyAllEvents("");

        assertScores("hero(2)=1200");
    }

    // если на поле трое, и один игрок имеет преимущество по очкам за вынос другого игрока
    // то по истечении таймаута раунда он получит очки за победу в раунде
    @Test
    public void shouldGetWinRoundScores_whenKillOneOtherHeroAdvantage_whenRoundTimeout() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        givenFl("     \n" +
                "     \n" +
                "☺    \n" + // его не накроет волной
                " ☺   \n" +
                " ☺   \n"); // его накроет волной

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                " ♥   \n" +
                " ♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                " ☺   \n" +
                " ♥   \n", 1);

        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                " ♥   \n" +
                " ☺   \n", 2);

        // when
        // когда я выношу одного игрока
        hero(1).dropPotion();
        tick();

        hero(1).right();
        tick();

        hero(1).up();
        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                " 1   \n" +
                " ♥   \n", 1);

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉♥  \n" +
                "҉҉҉  \n" +
                " ♣   \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥҉☺  \n" +
                "҉҉҉  \n" +
                " ♣   \n", 1);

        assertF("     \n" +
                "     \n" +
                "♥҉♥  \n" +
                "҉҉҉  \n" +
                " Ѡ   \n", 2);

        verifyAllEvents(
                "listener(1) => [KILL_OTHER_HERO]\n" +
                "listener(2) => [HERO_DIED]\n");

        // when
        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "     \n" +
                " ♣   \n", 1);

        verifyAllEvents("");

        // when
        // вот он последний тик раунда, тут все и случится
        dice(0, 0,
            1, 0,
            1, 1);
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "Ѡ♣   \n", 0);

        verifyAllEvents(
                "listener(0) => [[Time is over]]\n" +
                "listener(1) => [WIN_ROUND]\n");

        assertScores("");
    }

    // если на поле группа игроков, два из них активны и расставляют зелье
    // так вот после окончания таймаута раунда тот из них победит,
    // кто большее количество игроков вынес
    @Test
    public void shouldGetWinRoundScores_whenKillsAdvantage_whenRoundTimeout() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 5)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        givenFl("   ☺ \n" + // 3, 4 - единственная жертва второго, потому он проиграет по очкам
                "   ☺ \n" + // 3, 3 - второй активный игрок - будет проигравшим
                "     \n" + // 0, 1 - жертва первого
                "☺☺   \n" + // 1, 1 - первый активный игрок - будет победителем
                " ☺   \n"); // 1, 0 - жертва первого

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n" +
                "listener(3) => [START_ROUND, [Round 1]]\n" +
                "listener(4) => [START_ROUND, [Round 1]]\n");

        assertA("game(0)\n" +
                "   ☺ \n" +
                "   ♥ \n" +
                "     \n" +
                "♥♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(1)\n" +
                "   ♥ \n" +
                "   ☺ \n" +
                "     \n" +
                "♥♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(2)\n" +
                "   ♥ \n" +
                "   ♥ \n" +
                "     \n" +
                "☺♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(3)\n" +
                "   ♥ \n" +
                "   ♥ \n" +
                "     \n" +
                "♥☺   \n" +
                " ♥   \n" +
                "\n" +
                "game(4)\n" +
                "   ♥ \n" +
                "   ♥ \n" +
                "     \n" +
                "♥♥   \n" +
                " ☺   \n" +
                "\n");

        // when
        // пошла движуха
        hero(3).dropPotion();
        hero(1).dropPotion();
        tick();

        hero(3).right();
        hero(1).left();
        tick();

        hero(3).right();
        hero(1).left();
        tick();

        tick();

        // then
        assertF("   ♥ \n" +
                " ♥ 1 \n" +
                "     \n" +
                "♥1 ☺ \n" +
                " ♥   \n", 3);

        // when
        tick();

        // then
        assertA("game(0)\n" +
                "   Ѡ \n" +
                " ♥҉҉҉\n" +
                " ҉ ҉ \n" +
                "♣҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(1)\n" +
                "   ♣ \n" +
                " ☺҉҉҉\n" +
                " ҉ ҉ \n" +
                "♣҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(2)\n" +
                "   ♣ \n" +
                " ♥҉҉҉\n" +
                " ҉ ҉ \n" +
                "Ѡ҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(3)\n" +
                "   ♣ \n" +
                " ♥҉҉҉\n" +
                " ҉ ҉ \n" +
                "♣҉҉☺ \n" +
                " ♣   \n" +
                "\n" +
                "game(4)\n" +
                "   ♣ \n" +
                " ♥҉҉҉\n" +
                " ҉ ҉ \n" +
                "♣҉҉♥ \n" +
                " Ѡ   \n" +
                "\n");

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +
                "listener(1) => [KILL_OTHER_HERO]\n" +
                "listener(2) => [HERO_DIED]\n" +
                "listener(3) => [KILL_OTHER_HERO, KILL_OTHER_HERO]\n" +
                "listener(4) => [HERO_DIED]\n");

        // when
        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        tick();
        tick();
        tick();
        tick();

        // then
        assertF("   ♣ \n" +
                " ♥   \n" +
                "     \n" +
                "♣  ☺ \n" +
                " ♣   \n", 3);

        verifyAllEvents("");

        // when
        // вот он последний тик раунда, тут все и случится
        // размещаем всех победивших в свободные места
        dice(0, 2,
            1, 2);
        tick();

        // then
        verifyAllEvents(
                "listener(1) => [[Time is over]]\n" +
                "listener(3) => [WIN_ROUND]\n");

        // when
        // а это уже сделает сам фреймворк для всех проигравших
        dice(2, 2);
        game(0).newGame();

        dice(3, 2);
        game(2).newGame();

        dice(4, 2);
        game(4).newGame();

        // then
        assertF("     \n" +
                "     \n" +
                "♣♣Ѡ♣♣\n" +
                "     \n" +
                "     \n", 0);

        assertScores("");
    }

    // если на поле группа игроков, два из них активны и расставляют зелье
    // и даже уничтожили одинаковое количество игроков
    // так вот после окончания таймаута раунда тот из них победит,
    // кто большее очков заработал во время своего экшна (в данном случае коробку)
    // еще проверяем, что спаунится на месте трупиков нельзя (пусть даже они тоже ждут спауна)
    @Test
    public void shouldGetWinRoundScores_whenKillsAdvantagePlusOneBox_whenRoundTimeout() {
        // given
        int count = 6;

        settings().integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        // 1, 1 - первый активный игрок - будет проигравшим
        // 3, 3 - второй активный игрок - будет победителем, потому как снесет еще корбку
        // 1, 0 - жертва первого
        // 0, 1 - жертва первого
        // 3, 4 - жертва второго
        // 4, 3 - жертва второго
        givenFl("   ☺ \n" +
                "   ☺☺\n" +
                "   # \n" +
                "☺☺   \n" +
                " ☺   \n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n" +
                "listener(3) => [START_ROUND, [Round 1]]\n" +
                "listener(4) => [START_ROUND, [Round 1]]\n" +
                "listener(5) => [START_ROUND, [Round 1]]\n");

        assertA("game(0)\n" +
                "   ☺ \n" +
                "   ♥♥\n" +
                "   # \n" +
                "♥♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(1)\n" +
                "   ♥ \n" +
                "   ☺♥\n" +
                "   # \n" +
                "♥♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(2)\n" +
                "   ♥ \n" +
                "   ♥☺\n" +
                "   # \n" +
                "♥♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(3)\n" +
                "   ♥ \n" +
                "   ♥♥\n" +
                "   # \n" +
                "☺♥   \n" +
                " ♥   \n" +
                "\n" +
                "game(4)\n" +
                "   ♥ \n" +
                "   ♥♥\n" +
                "   # \n" +
                "♥☺   \n" +
                " ♥   \n" +
                "\n" +
                "game(5)\n" +
                "   ♥ \n" +
                "   ♥♥\n" +
                "   # \n" +
                "♥♥   \n" +
                " ☺   \n" +
                "\n");

        // when
        // пошла движуха
        hero(4).dropPotion();
        hero(1).dropPotion();
        tick();

        hero(4).right();
        hero(1).left();
        tick();

        hero(4).right();
        hero(1).left();
        tick();

        tick();

        // then
        assertF("   ♥ \n" +
                " ♥ 1♥\n" +
                "   # \n" +
                "♥1 ☺ \n" +
                " ♥   \n", 4);

        // when
        tick();

        // then
        assertA("game(0)\n" +
                "   Ѡ \n" +
                " ♥҉҉♣\n" +
                " ҉ H \n" +
                "♣҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(1)\n" +
                "   ♣ \n" +
                " ☺҉҉♣\n" +
                " ҉ H \n" +
                "♣҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(2)\n" +
                "   ♣ \n" +
                " ♥҉҉Ѡ\n" +
                " ҉ H \n" +
                "♣҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(3)\n" +
                "   ♣ \n" +
                " ♥҉҉♣\n" +
                " ҉ H \n" +
                "Ѡ҉҉♥ \n" +
                " ♣   \n" +
                "\n" +
                "game(4)\n" +
                "   ♣ \n" +
                " ♥҉҉♣\n" +
                " ҉ H \n" +
                "♣҉҉☺ \n" +
                " ♣   \n" +
                "\n" +
                "game(5)\n" +
                "   ♣ \n" +
                " ♥҉҉♣\n" +
                " ҉ H \n" +
                "♣҉҉♥ \n" +
                " Ѡ   \n" +
                "\n");

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +
                "listener(1) => [KILL_OTHER_HERO, KILL_OTHER_HERO, KILL_TREASURE_BOX]\n" +
                "listener(2) => [HERO_DIED]\n" +
                "listener(3) => [HERO_DIED]\n" +
                "listener(4) => [KILL_OTHER_HERO, KILL_OTHER_HERO]\n" +
                "listener(5) => [HERO_DIED]\n");

        // when
        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        // больше коробок нам не надо
        removeBoxes(1);
        tick();
        tick();
        tick();
        tick();

        // then
        assertF("   ♣ \n" +
                " ♥  ♣\n" +
                "     \n" +
                "♣  ☺ \n" +
                " ♣   \n", 4);

        verifyAllEvents("");

        // when
        // вот он последний тик раунда, тут все и случится
        // размещаем всех победивших в свободные места
        dice(0, 1, // место занято другим игроком
            1, 0,  // место занято другим игроком
            0, 2,
            1, 2);
        tick();

        // then
        verifyAllEvents(
                "listener(1) => [WIN_ROUND]\n" +
                "listener(4) => [[Time is over]]\n");

        // when
        dice(2, 2);
        game(0).newGame();

        dice(3, 2);
        game(2).newGame();

        dice(4, 2);
        game(3).newGame();

        dice(4, 1);
        game(5).newGame();

        // then
        assertF("     \n" +
                "     \n" +
                "♣♣Ѡ♣♣\n" +
                "    ♣\n" +
                "     \n", 0);

        assertScores("");
    }

    // проверяем, что при clearScore обнуляется:
    // - таймеры раунда
    // - очки заработанные в этом раунде
    // - и все игроки пересоздаются снова
    @Test
    public void shouldCleanEverything_whenCleanScores() {
        // given
        int count = 3;

        settings().integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 60); // до конца раунда целая минута

        givenFl("   ☺☺\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("   ☺♥\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", 0);

        assertF("   ♥☺\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", 1);

        assertF("   ♥♥\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n", 2);

        // when
        // бахнем зелье
        hero(2).dropPotion();
        tick();

        hero(2).left();
        tick();

        hero(2).left();
        tick();

        tick();
        tick();

        // then
        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n" +
                "listener(2) => [KILL_OTHER_HERO]\n");

        assertScores("hero(2)=200"); // за победу

        assertEquals(true, hero(0).isActiveAndAlive());
        assertEquals(true, hero(1).isActive());
        assertEquals(false, hero(1).isAlive()); // убит
        assertEquals(true, hero(2).isActiveAndAlive());

        assertF("   ☺♣\n" +
                "  ♥҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n", 0);

        // when
        // делаем очистку очков
        dice(0, 0, // первый игрок
            0, 1,  // второй
            1, 0); // третий
        field().clearScore();

        verifyAllEvents("");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♣    \n" +
                "Ѡ♣   \n", 0);

        // после этого тика будет сразу же новый раунд
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥    \n" +
                "☺♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "♥♥   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "♥    \n" +
                "♥☺   \n", 2);

        // и очки обнулятся
        assertScores("");

        // и все игроки активны
        assertEquals(true, hero(0).isActiveAndAlive());
        assertEquals(true, hero(1).isActiveAndAlive());
        assertEquals(true, hero(2).isActiveAndAlive());
    }

    @Test
    public void shouldGetKillEnemyScore() {
        // given
        int count = 3;

        settings().integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 60); // до конца раунда целая минута

        givenFl("   ☺☺\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n");

        player(0).inTeam(0);
        player(1).inTeam(1);
        player(2).inTeam(0);

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        assertF("   ☺ö\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", 0);

        assertF("   ö☺\n" +
                "    ö\n" +
                "     \n" +
                "     \n" +
                "     \n", 1);

        assertF("   ♥ö\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n", 2);

        // when
        // бахнем зелье
        hero(2).dropPotion();
        tick();

        hero(2).left();
        tick();

        hero(2).left();
        tick();

        tick();
        tick();

        // then
        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n" +
                "listener(2) => [KILL_ENEMY_HERO]\n");

        // за победу (enemy)
        assertScores("hero(2)=500");

        assertEquals(true, hero(0).isActiveAndAlive());
        assertEquals(true, hero(1).isActive());
        assertEquals(false, hero(1).isAlive()); // убит
        assertEquals(true, hero(2).isActiveAndAlive());
    }

    // в этом тесте проверяется что взрывная волна не проходит через живого героя,
    // но его останки не являются препятствием
    @Test
    public void shouldPlaceOfDeath_isNotABarrierForBlast() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(POTION_POWER, 3) // зелье с большим радиусом, чем обычно
                .integer(ROUNDS_TIME, 60)
                .integer(ROUNDS_TIME_FOR_WINNER, 15); // после победы я хочу еще чуть повисеть на уровне

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺☺  \n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        // when
        // выношу одного игрока мощным снарядом
        hero(0).dropPotion();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♥♥  \n", 0);

        // when
        tick();

        // then
        // второй не погибает - его экранирует обычный герой
        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♥  \n", 0);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♥  \n", 1);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣☺  \n", 2);

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [HERO_DIED]\n");

        // when
        hero(0).left();
        tick();

        hero(0).down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♥  \n", 0);

        // when
        // а теперь пробую то же, но через останки только что
        // поверженного соперника - они не должны мешать взрывной волне
        hero(0).dropPotion();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♣♥  \n", 0);

        // when
        tick();

        // then
        // второй так же падет
        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♣  \n", 0);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♣  \n", 1);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣Ѡ  \n", 2);

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, WIN_ROUND]\n" +
                "listener(2) => [HERO_DIED]\n");

        // when
        // ну и напоследок вернемся на место
        hero(0).left();
        tick();

        hero(0).down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♣  \n", 0);

        // when
        // а теперь посмотрим как взорвется зелье на двух трупиках
        // они должны быть полностью прозрачна для взрывной волны
        hero(0).dropPotion();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♣♣  \n", 0);
        // when
        tick();

        // then
        // второй так же падет
        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♣҉ \n", 0);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♣҉ \n", 1);

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣Ѡ҉ \n", 2);

        verifyAllEvents("");

        assertScores("hero(0)=1400");
    }

    // в этом тесте я проверяю, что после победы героя на уровне
    // в случае, если timeForWinner > 1 то герой повисит некоторое время на поле сам
    // и в конечном счете начнется новый раунд
    @Test
    public void shouldWinScore_whenTimeoutBy_timeForWinner() {
        // given
        settings().integer(ROUNDS_TIME, 60)
                .integer(ROUNDS_TIME_FOR_WINNER, 15); // после победы я хочу еще чуть повисеть на уровне

        shouldPlaceOfDeath_isNotABarrierForBlast();

        // when
        // пройдет еще некоторое число тиков до общего числа timeForWinner
        tick();
        tick();
        tick();
        tick();
        tick();
        tick();
        tick();
        tick();
        tick();

        // then
        verifyAllEvents("");

        // when
        // и начнется новый раунд
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 2]]\n" +
                "listener(1) => [START_ROUND, [Round 2]]\n" +
                "listener(2) => [START_ROUND, [Round 2]]\n");

        // when
        // а дальше все как обычно
        tick();

        // then
        verifyAllEvents("");

        assertScores("");
    }
}