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

import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.services.Point;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class PotionTest extends AbstractGameTest {

    @Test
    public void shouldPotionDropped_whenHeroDropPotion() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
    }

    @Test
    public void shouldPotionDropped_whenHeroDropPotionAtAnotherPlace() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().up();
        tick();

        hero().right();
        tick();

        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");
    }

    @Test
    public void shouldPotionsDropped_whenHeroDropThreePotion() {
        // given
        settings.integer(POTIONS_COUNT, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().up();
        tick();

        hero().act();
        tick();

        hero().right();
        tick();

        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "2☻   \n" +
                "     \n");
    }

    // проверить, что герой не может класть зелья больше,
    // чем у него в settings прописано
    @Test
    public void shouldOnlyTwoPotions_whenLevelApproveIt() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().up();
        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☻    \n" +
                "     \n");

        hero().up();
        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "☻    \n" +
                "3    \n" +
                "     \n");

        hero().up();
        hero().act();
        tick();

        assertF("     \n" +
                "☺    \n" +
                "3    \n" +
                "2    \n" +
                "     \n");
    }

    // герой не может класть два зелья на одно место
    @Test
    public void shouldOnlyOnePotionPerPlace() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        tick();

        hero().act();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        assertEquals(1, field.potions().size());

        hero().right();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");

        hero().right();
        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        tick();   // зелья больше нет, иначе тут был бы взрыв второй

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBoom_whenDroppedPotionHas5Ticks() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    // проверить, что я могу поставить еще одно зелье, когда другое рвануло
    @Test
    public void shouldCanDropNewPotion_whenOtherBoom() {
        shouldBoom_whenDroppedPotionHas5Ticks();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        hero().act();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☻  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        field.tick();
        field.tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_inOtherCorner() {
        givenFl("    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");

        hero().act();
        field.tick();

        hero().left();
        field.tick();

        hero().left();
        field.tick();

        field.tick();
        field.tick();

        assertF("  ☺҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldWallProtectsHero() {
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero().act();
        goOut();

        assertF("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼ ☼ ☼\n" +
                "☼1  ☼\n" +
                "☼☼☼☼☼\n");

        field.tick();

        assertF("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉҉ ☼\n" +
                "☼☼☼☼☼\n");

        assertHeroAlive();
    }

    @Test
    public void shouldWallProtectsHero2() {
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertPotionPower(5,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉ ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertHeroAlive();
    }

    // разрыв зелья длинной указанной в settings
    @Test
    public void shouldChangePotionPower_to2() {
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertPotionPower(2,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉    ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangePotionPower_to3() {
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertPotionPower(3,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉   ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangePotionPower_to6() {
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertPotionPower(6,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉҉☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    // я немогу модифицировать список зелья на доске, меняя getPotions
    // но список зелья, что у меня на руках обязательно синхронизирован
    // с теми, что на поле
    @Test
    public void shouldNoChangeOriginalPotionsWhenUseBoardApiButTimersSynchronized() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        hero().right();
        field.tick();
        hero().act();
        hero().right();
        field.tick();

        List<Potion> potions1 = field.potions().all();
        List<Potion> potions2 = field.potions().all();
        List<Potion> potions3 = field.potions().all();
        assertEquals(potions1.toString(), potions2.toString());
        assertEquals(potions2.toString(), potions3.toString());
        assertEquals(potions3.toString(), potions1.toString());

        Potion potion11 = potions1.get(0);
        Potion potion12 = potions2.get(0);
        Potion potion13 = potions3.get(0);
        assertEquals(potion11.toString(), potion12.toString());
        assertEquals(potion12.toString(), potion13.toString());
        assertEquals(potion13.toString(), potion11.toString());

        Potion potion21 = potions1.get(1);
        Potion potion22 = potions2.get(1);
        Potion potion23 = potions3.get(1);
        assertEquals(potion21.toString(), potion22.toString());
        assertEquals(potion22.toString(), potion23.toString());
        assertEquals(potion23.toString(), potion21.toString());

        field.tick();
        field.tick();

        assertFalse(potion11.isExploded());
        assertFalse(potion12.isExploded());
        assertFalse(potion13.isExploded());

        field.tick();

        assertTrue(potion11.isExploded());
        assertTrue(potion12.isExploded());
        assertTrue(potion13.isExploded());

        assertFalse(potion21.isExploded());
        assertFalse(potion22.isExploded());
        assertFalse(potion23.isExploded());

        field.tick();

        assertTrue(potion21.isExploded());
        assertTrue(potion22.isExploded());
        assertTrue(potion23.isExploded());

        events.verifyAllEvents("[DIED]");
    }

    @Test
    public void shouldReturnShouldNotSynchronizedPotionsList_whenUseBoardApi() {
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        hero().right();
        field.tick();

        List<Potion> potions1 = field.potions().all();
        assertEquals(1, potions1.size());

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        events.verifyAllEvents("[DIED]");

        List<Potion> potions2 = field.potions().all();
        assertEquals(0, potions2.size());
        assertEquals(0, potions1.size());
        assertEquals(potions1.toString(), potions2.toString());
    }

    @Test
    public void shouldChangeBlast_whenUseBoardApi() {  // TODO а нода вообще такое? стреляет по перформансу перекладывать объекты и усложняет код
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();
        hero().right();
        field.tick();
        hero().right();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        List<Blast> blasts1 = field.blasts().all();
        List<Blast> blasts2 = field.blasts().all();
        List<Blast> blasts3 = field.blasts().all();
        assertEquals(blasts1.toString(), blasts2.toString());
        assertEquals(blasts2.toString(), blasts3.toString());
        assertEquals(blasts3.toString(), blasts1.toString());

        Point blast11 = blasts1.get(0);
        Point blast12 = blasts2.get(0);
        Point blast13 = blasts3.get(0);
        assertEquals(blast11.toString(), blast12.toString());
        assertEquals(blast12.toString(), blast13.toString());
        assertEquals(blast13.toString(), blast11.toString());

        Point blast21 = blasts1.get(1);
        Point blast22 = blasts2.get(1);
        Point blast23 = blasts3.get(1);
        assertEquals(blast21.toString(), blast22.toString());
        assertEquals(blast22.toString(), blast23.toString());
        assertEquals(blast23.toString(), blast21.toString());
    }

    // взрывная волна не проходит через непробиваемую стенку
    @Test
    public void shouldBlastWaveDoesNotPassThroughWall() {
        // given
        settings.integer(POTION_POWER, 3);

        givenFl("☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼☺    ☼\n" +
                "☼☼☼☼☼☼☼\n");

        assertF("☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼☺    ☼\n" +
                "☼☼☼☼☼☼☼\n");

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        hero().up();
        field.tick();

        hero().act();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        assertF("☼☼☼☼☼☼☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼҉☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼Ѡ☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼☼☼☼☼☼☼\n");

        events.verifyAllEvents("[DIED]");
    }

    @Test
    public void shouldStopBlastWhenHeroOrDestroyWalls() {
        // given
        settings.integer(POTION_POWER, 5);

        givenFl("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺  #   \n");

        when(dice.next(anyInt())).thenReturn(101); // don't drop perk by accident

        hero().act();
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        assertF("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "Ѡ      \n" +
                "҉      \n" +
                "҉҉҉H   \n");

        events.verifyAllEvents("[DIED, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldStopBlastWhenGhost() {
        // given
        settings.integer(POTION_POWER, 5);

        givenFl("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺   &  \n");

        hero().act();
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().right();
        field.tick();
        field.tick();

        assertF("       \n" +
                "҉      \n" +
                "҉      \n" +
                "҉☺     \n" +
                "҉      \n" +
                "҉      \n" +
                "҉҉҉҉x  \n");

        events.verifyAllEvents("[KILL_GHOST]");
    }

    // на поле можно чтобы каждый поставил то количество
    // зелья которое ему позволено и не более того
    @Test
    public void shouldTwoPotionsOnBoard() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n", 0);
    }

    @Test
    public void shouldTwoPotionsOnBoard_withEnemy() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        player(0).inTeam(0);
        player(1).inTeam(1);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♡   \n" +
                "44   \n", 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "☺♡   \n" +
                "     \n" +
                "33   \n", 0);
    }

    @Test
    public void shouldFourPotionsOnBoard() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n" +
                "33   \n", 0);

        hero(0).act();
        hero(0).up();

        hero(1).act();
        hero(1).up();

        tick();

        assertF("     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n" +
                "22   \n", 0);
    }

    @Test
    public void shouldFourPotionsOnBoard_checkTwoPotionsPerHero() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        hero(0).act();
        hero(0).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "4♥   \n", 0);

        hero(0).act();
        hero(0).up();

        tick();

        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "4    \n" +
                "3♥   \n", 0);

        hero(0).act();
        hero(0).up();

        tick();

        assertF("     \n" +
                "☺    \n" +
                "     \n" +
                "3    \n" +
                "2♥   \n", 0);
    }
}