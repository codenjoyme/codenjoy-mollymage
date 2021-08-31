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

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.GHOSTS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;

public class KillDeathTest extends AbstractGameTest {

    // если герой стоит на зелье то он умирает после его взрыва
    @Test
    public void shouldKillHero_whenPotionExploded() {
        givenFl("     \n" +
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
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        assertF("     \n" +
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
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().left();
        field.tick();

        assertF("     \n" +
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

        assertF("     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goUp() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().up();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goDown() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().down();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goRight() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().right();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_dropPotion() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        killPotioner();

        hero().act();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    // если герой стоит под действием ударной волны, он умирает
    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromLeft() {
        givenFl("     \n" +
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

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");
        assertHeroAlive();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromRight() {
        givenFl("     \n" +
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

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1   \n");
        assertHeroAlive();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉҉  \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromUp() {
        givenFl("     \n" +
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

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "1    \n" +
                "☺    \n");
        assertHeroAlive();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉   \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldKillHero_whenPotionExploded_blastWaveAffect_fromDown() {
        givenFl("     \n" +
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

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "1    \n");
        assertHeroAlive();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "҉҉   \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "     \n");

        events.verifyAllEvents("[]");
        assertHeroDie();
    }

    @Test
    public void shouldNoKillHero_whenPotionExploded_blastWaveAffect_fromDownRight() {
        givenFl("     \n" +
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
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " 1   \n");

        field.tick();

        assertHeroAlive();
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_HeroDie() {
        givenFl("     \n" +
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

        assertF("     \n" +
                "  ҉  \n" +
                " ҉҉҉ \n" +
                "  Ѡ  \n" +
                "     \n");

        events.verifyAllEvents("[DIED]");
        assertHeroDie();

        field.tick();
        assertF("     \n" +
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
        givenFl("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");

        hero().act();
        goOut();

        assertF("#####\n" +
                "#  ☺#\n" +
                "# # #\n" +
                "#1  #\n" +
                "#####\n");

        field.tick();

        assertF("#####\n" +
                "#  ☺#\n" +
                "#҉# #\n" +
                "H҉҉ #\n" +
                "#H###\n");
    }

    // привидение умирает, если попадает под взывающееся зелье
    @Test
    public void shouldDieMonster_whenPotionExploded() {
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

        settings.integer(GHOSTS_COUNT, 1);
        dice(9, 9, 1, Direction.DOWN.value());
        field.tick();

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

        field.tick();

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

        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        dice(1, Direction.LEFT.value());
        field.tick();

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

        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        field.tick();
        field.tick();

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

        field.tick();

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

        dice(level.size() - 2, level.size() - 2, Direction.DOWN.value());
        field.tick();

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
        settings.integer(POTION_POWER, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺  & \n");

        hero().act();
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        assertF("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉x \n");

        dice(2, 2, Direction.DOWN.value());
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

    @Test
    public void shouldOnlyOneListenerWorksWhenOneHeroKillAnother() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        hero(0).act();
        hero(0).up();
        tick();

        hero(0).up();
        tick();

        tick();
        tick();
        tick();

        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "҉    \n" +
                "҉♣   \n", 0);

        events.verifyAllEvents(
                "listener(0) => [KILL_OTHER_HERO]\n" +
                "listener(1) => [DIED]\n");
    }

    // герой может идти на привидение, при этом он умирает
    @Test
    public void shouldKllOtherHeroWhenHeroGoToGhost() {
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

        hero(1).right();
        tick();
        // от имени наблюдателя вижу опасность - привидение
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺ &  \n", 0);

        // от имени жертвы вижу свой трупик
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "♥ Ѡ  \n", 1);

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // если привидение убил другого героя,
    // как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherHeroWhenGhostGoToIt() {
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

        ghost.start();
        ghost.setDirection(Direction.LEFT);
        tick();

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

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }

    // А что если герой идет на привидение а тот идет на
    // встречу к нему - герой проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherHeroWhenGhostAndHeroMoves() {
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

        ghost.setDirection(Direction.LEFT);
        hero(1).right();
        tick();

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

        events.verifyAllEvents(
                "listener(0) => []\n" +
                "listener(1) => [DIED]\n");
    }
}
