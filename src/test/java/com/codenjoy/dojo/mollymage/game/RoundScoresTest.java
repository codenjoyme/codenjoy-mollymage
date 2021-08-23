package com.codenjoy.dojo.mollymage.game;

import com.codenjoy.dojo.mollymage.services.GameSettings;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.*;
import static org.junit.Assert.assertEquals;

public class RoundScoresTest extends AbstractGameTest {

    @Override
    protected GameSettings settings() {
        return super.settings()
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
        settings.integer(ROUNDS_PLAYERS_PER_ROOM, DEFAULT_COUNT)
                .integer(ROUNDS_TIME_BEFORE_START, 1);


        dice(dice,
                1, 1, // первый игрок
                0, 1, // второй
                1, 0); // третий

        givenBr(DEFAULT_COUNT);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                " ♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                " ♥   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥♥   \n" +
                " ☺   \n", game(2));

        // когда я выношу одного игрока
        hero(0).act();
        tick();

        hero(0).right();
        tick();

        hero(0).up();
        tick();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                " ♥   \n", game(0));

        tick();

        asrtBrd("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ♣   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ♣   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " Ѡ   \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_OTHER_HERO, WIN_ROUND]\n" +
                        "listener(1) => [DIED]\n" +
                        "listener(2) => [DIED]\n");
    }

    @Test
    public void shouldGetWinRoundScores_whenKillAllEnemyHeroAndOtherHero() {
        settings.integer(ROUNDS_PLAYERS_PER_ROOM, DEFAULT_COUNT)
                .integer(ROUNDS_TIME_BEFORE_START, 1);


        dice(dice,
                1, 1, // первый игрок
                0, 1, // второй
                1, 0); // третий

        givenBr(DEFAULT_COUNT);
        player(0).setTeamId(0);
        player(1).setTeamId(0);
        player(2).setTeamId(1);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                " ♡   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                " ♡   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♡♡   \n" +
                " ☺   \n", game(2));

        // когда я выношу одного игрока
        hero(0).act();
        tick();

        hero(0).right();
        tick();

        hero(0).up();
        tick();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                " ♡   \n", game(0));

        tick();

        asrtBrd("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ♧   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ♧   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♡  \n" +
                "♧҉҉  \n" +
                " Ѡ   \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_ENEMY_HERO, KILL_OTHER_HERO, WIN_ROUND]\n" +
                        "listener(1) => [DIED]\n" +
                        "listener(2) => [DIED]\n");
    }

    // если на карте один вынес другого, а последний противник покинул игру
    // - очки победителю положено вручить
    @Test
    public void shouldGetWinRoundScores_whenKillOneAndAnotherLeaveTheGame() {
        settings.integer(ROUNDS_PLAYERS_PER_ROOM, DEFAULT_COUNT)
                .integer(ROUNDS_TIME_BEFORE_START, 1);


        dice(dice,
                1, 1, // первый игрок, кто побежит
                0, 1, // второй, жертва
                4, 4); // третий, тот кто покинет комнату

        givenBr(DEFAULT_COUNT);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("    ♥\n" +
                "     \n" +
                "     \n" +
                "♥☺   \n" +
                "     \n", game(0));

        asrtBrd("    ♥\n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n", game(1));

        asrtBrd("    ☺\n" +
                "     \n" +
                "     \n" +
                "♥♥   \n" +
                "     \n", game(2));

        // когда я выношу одного игрока
        hero(0).act();
        tick();

        hero(0).right();
        tick();

        hero(0).up();
        tick();

        tick();

        asrtBrd("    ♥\n" +
                "     \n" +
                "  ☺  \n" +
                "♥1   \n" +
                "     \n", game(0));

        tick();

        asrtBrd("    ♥\n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ҉   \n", game(0));

        asrtBrd("    ♥\n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ҉   \n", game(1));

        asrtBrd("    ☺\n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " ҉   \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                        "listener(1) => [DIED]\n" +
                        "listener(2) => []\n");

        // а теперь самое интересное - выходим из комнаты оставшимся игроком
        field.remove(player(2));

        asrtBrd("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "♣҉҉  \n" +
                " ҉   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "Ѡ҉҉  \n" +
                " ҉   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                " ҉♥  \n" +
                "♣҉҉  \n" +
                " ҉   \n", game(2));

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "  ☺  \n" +
                "♣    \n" +
                "     \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "  ♥  \n" +
                "Ѡ    \n" +
                "     \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "  ♥  \n" +
                "♣    \n" +
                "     \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [WIN_ROUND]\n" + // заслуженная победа
                        "listener(1) => []\n" +
                        "listener(2) => [DIED]\n"); // за то что он трус )

    }

    // если на карте один вынес другого, а последний противник покинул игру
    // - очки победителю положено вручить
    // но полсе этого если покинет комнату и второй, то мы не должны получить еще раз победные очки
    @Test
    public void shouldNotGetWinRoundScoresTwice_whenDieThenLeaveRoom() {
        // тут один игрок вынес другого, а третий после покинул комнату,
        // за что победитель получил свои очки, а все проигравшие - штрафы
        shouldGetWinRoundScores_whenKillOneAndAnotherLeaveTheGame();

        // а теперь самое интересное - выходим из комнаты оставшимся игроком
        field.remove(player(1));

        // никто больше не должен ничего получить
        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n");

    }

    // если на поле трое, и один игрок имеет преимущество по очкам за вынос другого игрока
    // то по истечении таймаута раунда он получит очки за победу в раунде
    @Test
    public void shouldGetWinRoundScores_whenKillOneOtherHeroAdvantage_whenRoundTimeout() {
        int count = 3;

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1);


        dice(dice,
                1, 1, // первый игрок
                0, 2, // второй - его не накроет волной
                1, 0); // третий - его накроет волной

        givenBr(count);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("     \n" +
                "     \n" +
                "♥    \n" +
                " ☺   \n" +
                " ♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                " ♥   \n" +
                " ♥   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "♥    \n" +
                " ♥   \n" +
                " ☺   \n", game(2));

        // когда я выношу одного игрока
        hero(0).act();
        tick();

        hero(0).right();
        tick();

        hero(0).up();
        tick();

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                " 1   \n" +
                " ♥   \n", game(0));

        tick();

        asrtBrd("     \n" +
                "     \n" +
                "♥҉☺  \n" +
                "҉҉҉  \n" +
                " ♣   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "☺҉♥  \n" +
                "҉҉҉  \n" +
                " ♣   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "♥҉♥  \n" +
                "҉҉҉  \n" +
                " Ѡ   \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                        "listener(1) => []\n" +
                        "listener(2) => [DIED]\n");

        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        tick();
        tick();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "♥ ☺  \n" +
                "     \n" +
                " ♣   \n", game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n");

        // вот он последний тик раунда, тут все и случится
        dice(dice,
                0, 0,
                1, 0,
                1, 1);
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "Ѡ♣   \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [WIN_ROUND]\n" +
                        "listener(1) => [[Time is over]]\n" +
                        "listener(2) => []\n");
    }

    // если на поле группа игроков, два из них активны и расставляют зелье
    // так вот после окончания таймаута раунда тот из них победит,
    // кто большее количество игроков вынес
    @Test
    public void shouldGetWinRoundScores_whenKillsAdvantage_whenRoundTimeout() {
        int count = 5;

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        dice(dice,
                1, 1, // первый активный игрок - будет победителем
                3, 3, // второй активный игрок - будет проигравшим
                1, 0, // жертва первого
                0, 1, // жертва первого
                3, 4); // единственная жертва второго, потому он проиграет по очкам

        givenBr(count);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n" +
                        "listener(3) => [START_ROUND, [Round 1]]\n" +
                        "listener(4) => [START_ROUND, [Round 1]]\n");

        assertBoards(
                "game(0)\n" +
                        "   ♥ \n" +
                        "   ♥ \n" +
                        "     \n" +
                        "♥☺   \n" +
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
                        "♥♥   \n" +
                        " ☺   \n" +
                        "\n" +
                        "game(3)\n" +
                        "   ♥ \n" +
                        "   ♥ \n" +
                        "     \n" +
                        "☺♥   \n" +
                        " ♥   \n" +
                        "\n" +
                        "game(4)\n" +
                        "   ☺ \n" +
                        "   ♥ \n" +
                        "     \n" +
                        "♥♥   \n" +
                        " ♥   \n" +
                        "\n");

        // пошла движуха
        hero(0).act();
        hero(1).act();
        tick();

        hero(0).right();
        hero(1).left();
        tick();

        hero(0).right();
        hero(1).left();
        tick();

        tick();

        asrtBrd("   ♥ \n" +
                " ♥ 1 \n" +
                "     \n" +
                "♥1 ☺ \n" +
                " ♥   \n", game(0));

        tick();

        assertBoards(
                "game(0)\n" +
                        "   ♣ \n" +
                        " ♥҉҉҉\n" +
                        " ҉ ҉ \n" +
                        "♣҉҉☺ \n" +
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
                        "♣҉҉♥ \n" +
                        " Ѡ   \n" +
                        "\n" +
                        "game(3)\n" +
                        "   ♣ \n" +
                        " ♥҉҉҉\n" +
                        " ҉ ҉ \n" +
                        "Ѡ҉҉♥ \n" +
                        " ♣   \n" +
                        "\n" +
                        "game(4)\n" +
                        "   Ѡ \n" +
                        " ♥҉҉҉\n" +
                        " ҉ ҉ \n" +
                        "♣҉҉♥ \n" +
                        " ♣   \n" +
                        "\n");

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_OTHER_HERO]\n" +
                        "listener(1) => [KILL_OTHER_HERO]\n" +
                        "listener(2) => [DIED]\n" +
                        "listener(3) => [DIED]\n" +
                        "listener(4) => [DIED]\n");

        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        tick();
        tick();
        tick();
        tick();

        asrtBrd("   ♣ \n" +
                " ♥   \n" +
                "     \n" +
                "♣  ☺ \n" +
                " ♣   \n", game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n" +
                        "listener(4) => []\n");

        // вот он последний тик раунда, тут все и случится
        dice(dice,
                0, 2,  // размещаем всех в свободные места
                1, 2,
                2, 2,
                3, 2,
                4, 2);
        tick();
        newGameForAllDied(); // это сделает сервер (вообще он это сделал намного раньше, но для наглядности тут)

        asrtBrd("     \n" +
                "     \n" +
                "Ѡ♣♣♣♣\n" +
                "     \n" +
                "     \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [WIN_ROUND]\n" +
                        "listener(1) => [[Time is over]]\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n" +
                        "listener(4) => []\n");
    }

    // если на поле группа игроков, два из них активны и расставляют зелье
    // и даже уничтожили одинаковое количество игроков
    // так вот после окончания таймаута раунда тот из них победит,
    // кто большее очков заработал во время своего экшна (в данном случае коробку)
    // еще проверяем, что спаунится на месте трупиков нельзя (пусть даже они тоже ждут спауна)
    @Test
    public void shouldGetWinRoundScores_whenKillsAdvantagePlusOneBox_whenRoundTimeout() {
        int count = 6;

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1);

        dice(dice,
                1, 1, // первый активный игрок - будет проигравшим
                3, 3, // второй активный игрок - будет победителем, потому как снесет еще корбку
                1, 0, // жертва первого
                0, 1, // жертва первого
                3, 4, // жертва второго
                4, 3); // жертва второго

        givenBr(count);

        boxesCount(1);
        boxAt(3, 2);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n" +
                        "listener(3) => [START_ROUND, [Round 1]]\n" +
                        "listener(4) => [START_ROUND, [Round 1]]\n" +
                        "listener(5) => [START_ROUND, [Round 1]]\n");

        assertBoards(
                "game(0)\n" +
                        "   ♥ \n" +
                        "   ♥♥\n" +
                        "   # \n" +
                        "♥☺   \n" +
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
                        "   ♥♥\n" +
                        "   # \n" +
                        "♥♥   \n" +
                        " ☺   \n" +
                        "\n" +
                        "game(3)\n" +
                        "   ♥ \n" +
                        "   ♥♥\n" +
                        "   # \n" +
                        "☺♥   \n" +
                        " ♥   \n" +
                        "\n" +
                        "game(4)\n" +
                        "   ☺ \n" +
                        "   ♥♥\n" +
                        "   # \n" +
                        "♥♥   \n" +
                        " ♥   \n" +
                        "\n" +
                        "game(5)\n" +
                        "   ♥ \n" +
                        "   ♥☺\n" +
                        "   # \n" +
                        "♥♥   \n" +
                        " ♥   \n" +
                        "\n");

        // пошла движуха
        hero(0).act();
        hero(1).act();
        tick();

        hero(0).right();
        hero(1).left();
        tick();

        hero(0).right();
        hero(1).left();
        tick();

        tick();

        asrtBrd("   ♥ \n" +
                " ♥ 1♥\n" +
                "   # \n" +
                "♥1 ☺ \n" +
                " ♥   \n", game(0));

        tick();

        assertBoards(
                "game(0)\n" +
                        "   ♣ \n" +
                        " ♥҉҉♣\n" +
                        " ҉ H \n" +
                        "♣҉҉☺ \n" +
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
                        " ♥҉҉♣\n" +
                        " ҉ H \n" +
                        "♣҉҉♥ \n" +
                        " Ѡ   \n" +
                        "\n" +
                        "game(3)\n" +
                        "   ♣ \n" +
                        " ♥҉҉♣\n" +
                        " ҉ H \n" +
                        "Ѡ҉҉♥ \n" +
                        " ♣   \n" +
                        "\n" +
                        "game(4)\n" +
                        "   Ѡ \n" +
                        " ♥҉҉♣\n" +
                        " ҉ H \n" +
                        "♣҉҉♥ \n" +
                        " ♣   \n" +
                        "\n" +
                        "game(5)\n" +
                        "   ♣ \n" +
                        " ♥҉҉Ѡ\n" +
                        " ҉ H \n" +
                        "♣҉҉♥ \n" +
                        " ♣   \n" +
                        "\n");

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, KILL_OTHER_HERO]\n" +
                        "listener(1) => [KILL_OTHER_HERO, KILL_OTHER_HERO, KILL_TREASURE_BOX]\n" +
                        "listener(2) => [DIED]\n" +
                        "listener(3) => [DIED]\n" +
                        "listener(4) => [DIED]\n" +
                        "listener(5) => [DIED]\n");

        // затем пройдет еще некоторое количество тиков, до общего числа = timePerRound
        boxesCount(0); // больше коробок нам не надо
        tick();
        tick();
        tick();
        tick();

        asrtBrd("   ♣ \n" +
                " ♥  ♣\n" +
                "     \n" +
                "♣  ☺ \n" +
                " ♣   \n", game(0));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n" +
                        "listener(4) => []\n" +
                        "listener(5) => []\n");

        // вот он последний тик раунда, тут все и случится
        dice(dice,
                0, 1,  // на трупики нельзя!
                1, 0,  // на трупики нельзя!
                0, 2,  // теперь размещаем всех в свободные места
                1, 2,
                2, 2,
                3, 2,
                4, 2,
                4, 1);
        tick();
        newGameForAllDied(); // это сделает сервер (вообще он это сделал намного раньше, но для наглядности тут)

        asrtBrd("     \n" +
                "     \n" +
                "Ѡ♣♣♣♣\n" +
                "    ♣\n" +
                "     \n", game(0));

        events.verifyAllEvents(
                "listener(0) => [[Time is over]]\n" +
                        "listener(1) => [WIN_ROUND]\n" +
                        "listener(2) => []\n" +
                        "listener(3) => []\n" +
                        "listener(4) => []\n" +
                        "listener(5) => []\n");
    }

    // проверяем, что при clearScore обнуляется:
    // - таймеры раунда
    // - очки заработанные в этом раунде
    // - и все игроки пересоздаются снова
    @Test
    public void shouldCleanEverything_whenCleanScores() {
        int count = 3;

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 60); // до конца раунда целая минута


        dice(dice,
                4, 4, // первый игрок
                4, 3, // второй
                3, 4); // третий

        givenBr(count);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("   ♥☺\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", game(0));

        asrtBrd("   ♥♥\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n", game(1));

        asrtBrd("   ☺♥\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", game(2));

        // бахнем зелье
        hero(2).act();
        tick();

        hero(2).left();
        tick();

        hero(2).left();
        tick();

        tick();
        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED]\n" +
                        "listener(1) => []\n" +
                        "listener(2) => [KILL_OTHER_HERO]\n");

        assertEquals(0, hero(0).scores());
        assertEquals(0, hero(1).scores());
        assertEquals(200, hero(2).scores()); // за победу

        assertEquals(true, hero(0).isActive());
        assertEquals(false, hero(0).isAlive()); // убит
        assertEquals(true, hero(1).isActiveAndAlive());
        assertEquals(true, hero(2).isActiveAndAlive());

        // делаем очистку очков
        dice(dice,
                0, 0, // первый игрок
                0, 1, // второй
                1, 0); // третий
        field.clearScore();
        resetHeroes();

        // после этого тика будет сразу же новый раунд
        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥    \n" +
                "☺♥   \n", game(0));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "♥♥   \n", game(1));

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "♥    \n" +
                "♥☺   \n", game(2));

        // и очки обнулятся
        assertEquals(0, hero(0).scores());
        assertEquals(0, hero(1).scores());
        assertEquals(0, hero(2).scores());

        // и все игроки активны
        assertEquals(true, hero(0).isActiveAndAlive());
        assertEquals(true, hero(1).isActiveAndAlive());
        assertEquals(true, hero(2).isActiveAndAlive());
    }

    @Test
    public void shouldGetKillEnemyScore() {
        int count = 3;

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, count)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 60); // до конца раунда целая минута


        dice(dice,
                4, 4, // первый игрок
                4, 3, // второй
                3, 4); // третий

        givenBr(count);
        player(0).setTeamId(0);
        player(1).setTeamId(1);
        player(2).setTeamId(1);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");

        asrtBrd("   ♡☺\n" +
                "    ♡\n" +
                "     \n" +
                "     \n" +
                "     \n", game(0));

        asrtBrd("   ♥♡\n" +
                "    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n", game(1));

        asrtBrd("   ☺♡\n" +
                "    ♥\n" +
                "     \n" +
                "     \n" +
                "     \n", game(2));

        // бахнем зелье
        hero(2).act();
        tick();

        hero(2).left();
        tick();

        hero(2).left();
        tick();

        tick();
        tick();

        events.verifyAllEvents(
                "listener(0) => [DIED]\n" +
                        "listener(1) => []\n" +
                        "listener(2) => [KILL_ENEMY_HERO]\n");

        assertEquals(0, hero(0).scores());
        assertEquals(0, hero(1).scores());
        assertEquals(500, hero(2).scores()); // за победу (enemy)

        assertEquals(true, hero(0).isActive());
        assertEquals(false, hero(0).isAlive()); // убит
        assertEquals(true, hero(1).isActiveAndAlive());
        assertEquals(true, hero(2).isActiveAndAlive());
    }

    // в этом тесте проверяется что взрывная волна не проходит через живого героя,
    // но его останки не являются препятствием
    @Test
    public void shouldPlaceOfDeath_isNotABarrierForBlast() {

        settings.integer(ROUNDS_PLAYERS_PER_ROOM, DEFAULT_COUNT)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(POTION_POWER, 3) // зелье с большим радиусом, чем обычно
                .integer(ROUNDS_TIME, 60)
                .integer(ROUNDS_TIME_FOR_WINNER, 15); // после победы я хочу еще чуть повисеть на уровне


        dice(dice,
                0, 0, // первый игрок
                1, 0, // второй
                2, 0); // третий

        givenBr(DEFAULT_COUNT);

        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                        "listener(1) => [START_ROUND, [Round 1]]\n" +
                        "listener(2) => [START_ROUND, [Round 1]]\n");


        // выношу одного игрока мощным снарядом
        hero(0).act();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♥♥  \n", game(0));

        tick();

        // второй не погибает - его экранирует обычный герой
        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♥  \n", game(0));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♥  \n", game(1));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣☺  \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                        "listener(1) => [DIED]\n" +
                        "listener(2) => []\n");

        hero(0).left();
        tick();

        hero(0).down();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♥  \n", game(0));

        // а теперь пробую то же, но через останки только что
        // поверженного соперника - они не должны мешать взрывной волне
        hero(0).act();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♣♥  \n", game(0));

        tick();

        // второй так же падет
        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♣  \n", game(0));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♣  \n", game(1));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣Ѡ  \n", game(2));

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO, WIN_ROUND]\n" +
                        "listener(1) => []\n" +
                        "listener(2) => [DIED]\n");

        // ну и напоследок вернемся на место
        hero(0).left();
        tick();

        hero(0).down();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♣  \n", game(0));

        // а теперь посмотрим как взорвется зелье на двух трупиках
        // они должны быть полностью прозрачна для взрывной волны
        hero(0).act();
        tick();

        hero(0).up();
        tick();

        hero(0).right();
        tick();
        tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "1♣♣  \n", game(0));

        tick();

        // второй так же падет
        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉♣♣҉ \n", game(0));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉Ѡ♣҉ \n", game(1));

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉♥   \n" +
                "҉♣Ѡ҉ \n", game(2));

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n");
    }

    // в этом тесте я проверяю, что после победы героя на уровне
    // в случае, если timeForWinner > 1 то герой повисит некоторое время на поле сам
    // и в конечном счете начнется новый раунд
    @Test
    public void shouldWinScore_whenTimeoutBy_timeForWinner() {
        settings.integer(ROUNDS_TIME, 60)
                .integer(ROUNDS_TIME_FOR_WINNER, 15); // после победы я хочу еще чуть повисеть на уровне

        shouldPlaceOfDeath_isNotABarrierForBlast();

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

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n");

        // и начнется новый раунд
        tick();

        events.verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 2]]\n" +
                        "listener(1) => [START_ROUND, [Round 2]]\n" +
                        "listener(2) => [START_ROUND, [Round 2]]\n");

        // а дальше все как обычно
        tick();

        events.verifyAllEvents(
                "listener(0) => []\n" +
                        "listener(1) => []\n" +
                        "listener(2) => []\n");
    }
}
