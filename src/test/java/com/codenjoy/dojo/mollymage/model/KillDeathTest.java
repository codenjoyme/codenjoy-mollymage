package com.codenjoy.dojo.mollymage.model;

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

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;

public class KillDeathTest extends AbstractGameTest {

    // если герой стоит на зелье то он умирает после его взрыва
    @Test
    public void shouldKillHero_whenPotionExploded() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().dropPotion();
        hero().right();
        tick();

        tick();
        tick();

        tick();

        // then
        assertHeroAlive();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    // после смерти ходить больше нельзя
    @Test
    public void shouldException_whenTryToMoveIfDead_goLeft() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        killPotioner();

        // when
        hero().left();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
    }

    private void killPotioner() {
        // when
        hero().up();
        tick();

        hero().right();
        hero().dropPotion();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goUp() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        killPotioner();

        // when
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goDown() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        killPotioner();

        // when
        hero().down();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goRight() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        killPotioner();

        // when
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_dropPotion() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        killPotioner();

        // when
        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
    }

    // если герой стоит под действием ударной волны, он умирает
    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromLeft() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().dropPotion();
        tick();

        hero().right();
        tick();

        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");
        assertHeroAlive();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromRight() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().right();
        tick();

        hero().dropPotion();
        tick();

        hero().left();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1   \n");
        assertHeroAlive();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉҉  \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromUp() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().up();
        hero().dropPotion();
        tick();

        hero().down();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "1    \n" +
                "☺    \n");
        assertHeroAlive();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉   \n" +
                "Ѡ    \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromDown() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().down();
        tick();

        hero().dropPotion();
        tick();

        hero().up();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "1    \n");
        assertHeroAlive();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "҉҉   \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "     \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    @Test
    public void shouldNoKillHero_whenPotionExploded_blastWaveAffect_fromDownRight() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().down();
        tick();

        hero().right();
        tick();

        hero().dropPotion();
        tick();

        hero().up();
        tick();

        hero().left();
        tick();

        tick();

        // then
        assertHeroAlive();
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " 1   \n");

        // when
        tick();

        // then
        assertHeroAlive();
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_HeroDie() {
        // given
        givenFl("     \n" +
                "     \n" +
                "  ☺  \n" +
                "     \n" +
                "     \n");

        // when
        hero().dropPotion();
        tick();

        hero().down();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "  ҉  \n" +
                " ҉҉҉ \n" +
                "  Ѡ  \n" +
                "     \n");

        verifyAllEvents("[HERO_DIED]");
        assertHeroDie();

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "  Ѡ  \n" +
                "     \n");

        verifyAllEvents("");
        assertHeroDie();
    }

    // они взрываются от ударной волны
    @Test
    public void shouldDestroyWallsDestroyed_whenPotionExploded() {
        // given
        givenFl("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        hero().up();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        // then
        assertF("#####\n" +
                "#  ☺#\n" +
                "# # #\n" +
                "#1  #\n" +
                "#####\n");

        // when
        tick();

        // then
        assertF("#####\n" +
                "#  ☺#\n" +
                "#҉# #\n" +
                "H҉҉ #\n" +
                "#H###\n");

        verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");
    }

    // привидение умирает, если попадает под взывающееся зелье
    @Test
    public void shouldDieMonster_whenPotionExploded() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        settings().integer(GHOSTS_COUNT, 1);
        // координата и направление движения привидения
        dice(9, 9,
            1, Direction.DOWN.value());
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        // when
        tick();
        tick();
        tick();
        tick();
        tick();
        tick();

        // направление движения привидения
        dice(1, Direction.LEFT.value());
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().up();
        tick();

        tick();
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        // when
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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

        verifyAllEvents("[KILL_GHOST]");

        // when
        // координата и направление движения привидения
        dice(9, 9,
                Direction.DOWN.value());
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼☼\n" +
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
        // given
        settings().integer(POTION_POWER, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺  & \n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        hero().right();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉x \n");

        verifyAllEvents("[KILL_GHOST]");

        // when
        // координата и направление движения привидения
        dice(2, 2, Direction.DOWN.value());
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

    @Test
    public void shouldOnlyOneListenerWorksWhenOneHeroKillAnother() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).dropPotion();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉♣   \n", 0);

        verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [HERO_DIED]\n");
    }

    // герой может идти на привидение, при этом он умирает
    @Test
    public void shouldKllOtherHeroWhenHeroGoToGhost() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", 0);

        // when
        hero(1).right();
        tick();
        // от имени наблюдателя вижу опасность - привидение
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺ &  \n", 0);

        // then
        // от имени жертвы вижу свой трупик
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥ Ѡ  \n", 1);

        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n");
    }

    // если привидение убил другого героя,
    // как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherHeroWhenGhostGoToIt() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        Ghost ghost = ghost(2, 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", 0);

        // when
        ghost.start();
        ghost.setDirection(Direction.LEFT);
        tick();

        // then
        // от имени наблюдателя я там вижу опасность - привидение, мне не интересны останки игроков
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&   \n", 0);

        // от имени жертвы я вижу свой трупик, мне пофиг уже что на карте происходит, главное где поставить памятник герою
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥Ѡ   \n", 1);

        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n");
    }

    // А что если герой идет на привидение а тот идет на
    // встречу к нему - герой проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherHeroWhenGhostAndHeroMoves() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺&  \n");

        Ghost ghost = ghost(2, 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺♥&  \n", 0);

        // when
        ghost.setDirection(Direction.LEFT);
        hero(1).right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺&♣  \n", 0);

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥&Ѡ  \n", 1);

        verifyAllEvents(
                "listener(1) => [HERO_DIED]\n");
    }
}