package com.codenjoy.dojo.mollymage.game;

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

import com.codenjoy.dojo.mollymage.model.items.ghost.Ghost;
import com.codenjoy.dojo.services.Direction;
import org.junit.Test;

public class KillDeathTest extends AbstractGameTest {

    // если герой стоит на зелье то он умирает после его взрыва
    @Test
    public void shouldKillHero_whenPotionExploded() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().act();
        hero().right();
        field.tick();

        field.tick();
        field.tick();

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    // после смерти ходить больше нельзя
    @Test
    public void shouldException_whenTryToMoveIfDead_goLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().left();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    private void killPotioner() {
        hero().up();
        field.tick();

        hero().right();
        hero().act();
        field.tick();

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goDown() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().down();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_dropPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    // если герой стоит под действием ударной волны, он умирает
    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromLeft() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().act();
        field.tick();

        hero().right();
        field.tick();

        field.tick();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().right();
        field.tick();

        hero().act();
        field.tick();

        hero().left();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1   \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉҉  \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromUp() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().up();
        hero().act();
        field.tick();

        hero().down();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "1    \n" +
                "☺    \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉   \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromDown() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().down();
        field.tick();

        hero().act();
        field.tick();

        hero().up();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "1    \n");
        assertHeroAlive();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "҉҉   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "     \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldNoKillHero_whenPotionExploded_blastWaveAffect_fromDownRight() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().down();
        field.tick();

        hero().right();
        field.tick();

        hero().act();
        field.tick();

        hero().up();
        field.tick();

        hero().left();
        field.tick();

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " 1   \n");

        field.tick();

        assertHeroAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_HeroDie() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoBoardCenter();

        hero().act();
        field.tick();

        hero().down();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "  ҉  \n" +
                " ҉҉҉ \n" +
                "  Ѡ  \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "  Ѡ  \n" +
                "     \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    // они взрываются от ударной волны
    @Test
    public void shouldDestroyWallsDestroyed_whenPotionExploded() {
        givenBr("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");

        hero().act();
        goOut();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "# # #\n" +
                "#1  #\n" +
                "#####\n");

        field.tick();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "#҉# #\n" +
                "H҉҉ #\n" +
                "#H###\n");
    }

    // привидение умирает, если попадает под взывающееся зелье
    @Test
    public void shouldDieMonster_whenPotionExploded() {
        givenBr("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        ghostsCount(1);
        ghostAt(9, 9).start();
        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, 1, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        dice(dice, 1, Direction.LEFT.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      & ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼1 &      ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼҉x       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(dice, level.size() - 2, level.size() - 2, Direction.DOWN.value());
        field.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldGhostAppearAfterKill() {
        potionsPower(3);

        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        dice(dice, 3, 0, Direction.DOWN.value());
        ghostsCount(1);

        hero().act();
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉x \n");

        dice(dice, 2, 2, Direction.DOWN.value());
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

    @Test
    public void shouldOnlyOneListenerWorksWhenOneHeroKillAnother() {
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

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
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        ghostAt(2, 0);

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
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        Ghost ghost = ghostAt(2, 0);

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
        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

        Ghost ghost = ghostAt(2, 0);

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
}