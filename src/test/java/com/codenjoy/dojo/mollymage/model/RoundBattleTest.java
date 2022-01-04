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

import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.TREASURE_BOX_COUNT;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.*;

public class RoundBattleTest extends AbstractGameTest {

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

    // во время старта игры, когда не прошло timeBeforeStart тиков,
    // все игроки неактивны (видно их трупики)
    @Test
    public void shouldAllPlayersOnBoardIsInactive_whenStart() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "☺☺   \n");

        // when then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "♣♣   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "Ѡ♣   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "♣Ѡ   \n", 2);
    }

    // после старта идет отсчет обратного времени
    @Test
    public void shouldCountdownBeforeRound_whenTicksOnStart() {
        // given
        shouldAllPlayersOnBoardIsInactive_whenStart();

        verifyAllEvents("");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [[....4....]]\n" +
                "listener(1) => [[....4....]]\n" +
                "listener(2) => [[....4....]]\n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [[...3...]]\n" +
                "listener(1) => [[...3...]]\n" +
                "listener(2) => [[...3...]]\n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [[..2..]]\n" +
                "listener(1) => [[..2..]]\n" +
                "listener(2) => [[..2..]]\n");

        // when
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [[.1.]]\n" +
                "listener(1) => [[.1.]]\n" +
                "listener(2) => [[.1.]]\n");
    }

    // пока идет обратный отсчет я не могу ничего предпринимать, а герои отображаются на карте как трупики
    // но после объявления раунда я могу начать играть
    @Test
    public void shouldActiveAndCanMove_afterCountdown() {
        // given
        shouldCountdownBeforeRound_whenTicksOnStart();

        // пока еще не активны
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "♣♣   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "Ѡ♣   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♣   \n" +
                "♣Ѡ   \n", 2);

        // when
        // и я не могу ничего поделать с ними
        hero(0).up();
        hero(1).right();
        hero(2).up();

        tick();

        // then
        // после сообщения что раунд начался
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        // можно играть - игроки видны как активные
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "♥♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♥   \n" +
                "☺♥   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♥   \n" +
                "♥☺   \n", 2);

        // when
        // ... и когда я муваю героев, они откликаются
        hero(0).up();
        hero(1).up();
        hero(2).right();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                "♥    \n" +
                "  ♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                " ♥   \n" +
                "☺    \n" +
                "  ♥  \n", 1);

        assertF("     \n" +
                "     \n" +
                " ♥   \n" +
                "♥    \n" +
                "  ☺  \n", 2);
    }

    // если один игрок вынесет другого но на поле есть едще игроки,
    // то тот, которого вынесли появится в новом месте в виде трупика
    @Test
    public void shouldMoveToInactive_whenKillSomeone() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1); // TODO а что будет если тут 0 игра хоть начнется?

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "☺☺   \n");

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
                " ☺   \n" +
                "♥♥   \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♥   \n" +
                "☺♥   \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ♥   \n" +
                "♥☺   \n", 2);

        // when
        // когда я выношу одного игрока
        hero(0).dropPotion();
        tick();

        hero(0).right();
        tick();

        hero(0).up();
        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                " 1   \n" +
                "♥♥   \n", 0);

        // игрок активный и живой
        assertEquals(true, hero(2).isActive());
        assertEquals(true, hero(2).isAlive());
        assertEquals(true, player(2).wantToStay());
        assertEquals(false, player(2).shouldLeave());

        // when
        tick();

        // then
        // игрок активный но неживой (cервер ему сделает newGame)
        assertEquals(true, hero(2).isActive());
        assertEquals(false, hero(2).isAlive());
        // тут без изменений
        assertEquals(true, player(2).wantToStay());
        assertEquals(false, player(2).shouldLeave());

        assertF("     \n" +
                "     \n" +
                " ҉☺  \n" +
                "҉҉҉  \n" +
                "♥♣   \n", 0);

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(2) => [HERO_DIED]\n");

        // when
        tick();

        // новые координаты для героя
        dice(3, 4);
        field().newGame(player(2)); // это сделает сервер в ответ на isAlive = false

        // then
        // игрок уже живой но неактивный до начала следующего раунда
        assertEquals(false, hero(2).isActive());
        assertEquals(true, hero(2).isAlive());
        // тут без изменений
        assertEquals(true, player(2).wantToStay());
        assertEquals(false, player(2).shouldLeave());

        assertF("   ♣ \n" +
                "     \n" +
                "  ☺  \n" +
                "     \n" +
                "♥    \n", 0);
    }

    // проверил как отрисуется привидение если под ним будет трупик героя:
    // - от имени наблюдателя я там вижу опасность - привидение, мне не интересны останки игроков
    // - от имени жертвы я вижу свой трупик, мне пофиг уже что на карте происходит, главное где поставить памятник герою
    @Test
    public void shouldDrawGhost_onPlaceOfDeath() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 20);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "☺☺☺  \n");

        Ghost ghost = ghost(1, 1);

        // when
        tick();

        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        // then
        // ставлю зелье
        hero(0).dropPotion();
        tick();

        // и тикать
        hero(0).up();
        tick();

        hero(0).up();
        tick();
        tick();

        // when
        // взрыв
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [HERO_DIED]\n");

        // when
        // идем назад
        hero(0).down();
        tick();

        hero(0).down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "☺♣♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "♥Ѡ♥  \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "♥♣☺  \n", 2);

        // when
        // попробуем привидением сходить на место падшего героя
        ghost.move(DOWN.change(ghost));

        // then
        // от имени наблюдателя в клеточке с останками я вижу живого привидения
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&♥  \n", 0);

        // от имени пострадавшего в клеточке я вижу свои останки, привидение хоть и есть там, я его не вижу
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥Ѡ♥  \n", 1);

        // от имени наблюдателя в клеточке с останками я вижу живое привидение
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥&☺  \n", 2);
    }

    // проверил как отрисуется привидение если под ним будет не только трупик героя но и зелье:
    // - от имени наблюдателя я там вижу опасность - привидения, мне не интересны останки игроков
    // - от имени жертвы я вижу свой трупик, мне пофиг уже что на карте происходит, главное где поставить памятник герою
    // но если привидения нет, и зелье с останками, то подобно описанному выше:
    // - я от имени наблюдателя вижу тикающее зелье
    // - а от имени пострадавшего - свои останки
    // приоритет прорисовки такой: 1) привидение 2) зелье 3) останки
    @Test
    public void shouldDrawGhost_onPlaceOfDeath_withBomb() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 20);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "☺☺☺  \n");

        Ghost ghost = ghost(1, 1);

        // when
        tick();


        // then
        verifyAllEvents(
                "listener(0) => [START_ROUND, [Round 1]]\n" +
                "listener(1) => [START_ROUND, [Round 1]]\n" +
                "listener(2) => [START_ROUND, [Round 1]]\n");

        // when
        // ставлю зелье
        hero(0).dropPotion();
        tick();

        // и тикать
        hero(0).up();
        tick();

        hero(0).up();
        tick();
        tick();

        // взрыв
        tick();

        // then
        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [HERO_DIED]\n");

        // when
        // идем назад
        hero(0).down();
        tick();

        hero(0).down();
        tick();

        hero(0).right();
        hero(0).dropPotion();
        tick();

        hero(0).left();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "☺3♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "♥Ѡ♥  \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " &   \n" +
                "♥3☺  \n", 2);

        // when
        // попробуем привидением сходить на место падшего героя
        ghost.move(DOWN.change(ghost));

        // then
        // от имени наблюдателя в клеточке с останками
        // я вижу живое привидение, он по моему опаснее чем зелье
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&♥  \n", 0);

        // от имени пострадавшего в клеточке я вижу свои
        // останки, привиедние хоть и есть там, я его не вижу
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥Ѡ♥  \n", 1);

        // от имени наблюдателя в клеточке с останками
        // я вижу живое привидение, он по моему опаснее чем зелье
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥&☺  \n", 2);
    }

    // останки другого героя не являются препятствием для прохождения любым героем
    // так же отрисовка живого и мертвого героя в одной клетке от имени трех типов героев
    // 1) тот которого вынесли видит свой трупик
    // 2) тот кто стоит в той же клетке видит себя
    // 3) сторонний наблюдатель видит живого соперника
    @Test
    public void shouldPlaceOfDeath_isNotABarrierForOtherHero() {
        // given
        givenCaseWhenPlaceOfDeathOnMyWay();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♥  \n", 0);

        // when
        // а вот и попытка пойти на место трупика
        hero(0).right();
        tick();

        // then
        // от имени того кто стоит на месте смерти другого героя он видет себя
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺♥  \n", 0);

        // от имени того кого вынесли он видит свой трупик
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ♥  \n", 1);

        // от имени стороннего наблюдателя - он видит живую угрозу
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ♥☺  \n", 2);
    }

    private void givenCaseWhenPlaceOfDeathOnMyWay() {
        // given
        settings().integer(ROUNDS_PLAYERS_PER_ROOM, 3)
                .integer(ROUNDS_TIME_BEFORE_START, 1)
                .integer(ROUNDS_TIME, 20);

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

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥☺♥  \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥♥☺  \n", 2);

        // when
        // когда я выношу одного игрока
        hero(0).dropPotion();
        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "1♥♥  \n", 0);

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉♣♥  \n", 0);

        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                "҉    \n" +
                "҉Ѡ♥  \n", 1);

        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                "҉    \n" +
                "҉♣☺  \n", 2);

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [HERO_DIED]\n");

        // when
        hero(0).down();
        tick();

        hero(0).down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♥  \n", 0);
    }

    // я не могу подрывать уже убитого героя
    // а в отрисовке, на месте трупика я не вижу
    // взрывной волны, там всегда будет трупик
    @Test
    public void shouldCantDestroyHeroPlaceOfDeath() {
        // given
        givenCaseWhenPlaceOfDeathOnMyWay();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♣♥  \n", 0);

        // when
        hero(0).dropPotion();
        tick();

        hero(0).up();
        tick();

        hero(0).up();
        tick();

        tick();
        tick();

        // then
        verifyAllEvents("");

        // на месте героя которого вынесли я как сторонний наблюдатель
        // вижу его останки, а не взрывную волну
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉♣♥  \n", 0);

        // я как тот которого вынесли, на месте взрыва вижу себя
        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                "҉    \n" +
                "҉Ѡ♥  \n", 1);

        // на месте героя которого вынесли я как сторонний наблюдатель
        // вижу его останки, а не взрывную волну
        assertF("     \n" +
                "     \n" +
                "♥    \n" +
                "҉    \n" +
                "҉♣☺  \n", 2);
    }

    // люой герой может зайти на место трупика и там его можно прибить, так что
    // будет у нас двап трупика в одной клетке
    @Test
    public void shouldDestroySecondHero_whenItOnDeathPlace() {
        // given
        shouldPlaceOfDeath_isNotABarrierForOtherHero();

        // вижу себя в клетке, где еще трупик
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺♥  \n", 0);

        // вижу свой трупик, раз меня вынесли
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ♥  \n", 1);

        // вижу своего соперника в клетке, где трупик
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ♥☺  \n", 2);

        // when
        // ставим зелье и убегаем
        hero(2).dropPotion();
        tick();

        hero(2).right();
        tick();

        hero(2).up();
        tick();

        tick();
        tick();


        // then
        // что в результате
        // я вижу свой трупик в клетке, где есть еще один такой же
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "  ҉♥ \n" +
                " Ѡ҉҉ \n", 0);

        // я вижу свой трупик в клетке, где есть еще один такой же
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "  ҉♥ \n" +
                " Ѡ҉҉ \n", 1);

        // я вижу трупик одного из убитых там героев (их там двое)
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "  ҉☺ \n" +
                " ♣҉҉ \n", 2);

        verifyAllEvents(
                "listener(0) => [HERO_DIED]\n" +
                "listener(2) => [KILL_OTHER_HERO, WIN_ROUND]\n");
    }

    // просто любопытно как рванут два героя, вместе с привидение и трупом под зельем
    @Test
    public void shouldDestroyGhost_withOtherHeroes_onDeathPlace() {
        // given
        shouldDrawGhost_onPlaceOfDeath_withBomb();

        // when
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡx♣  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣Ѡ♣  \n", 1);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "♣xѠ  \n", 2);

        // победителей нет
        verifyAllEvents(
                "listener(0) => [HERO_DIED, KILL_OTHER_HERO, KILL_GHOST]\n" +
                "listener(2) => [HERO_DIED]\n");

        // when
        tick();

        // then
        verifyAllEvents("");
    }
}