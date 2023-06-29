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

import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionExploder;
import com.codenjoy.dojo.services.Point;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTIONS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;

public class PotionTest extends AbstractGameTest {

    @Test
    public void shouldKillHero_whenPotionExploded_usingPerkPotionExploder() {
        // given
        givenFl("     \n" +
            "     \n" +
            "     \n" +
            "     \n" +
            "☺☺   \n");

        hero(0).addPerk(new PotionExploder(5, 100));
        assertHeroPerks(
            "{POTION_EXPLODER('A')\n" +
                "  value=5, timeout=100, timer=100, pick=0}");

        // when
        hero(0).dropPotion();
        hero(1).dropPotion();
        hero(1).setActive(false);
        hero(0).explodeAllPotions();
        tick();

        // then
        assertF("     \n" +
            "     \n" +
            "     \n" +
            "҉҉   \n" +
            "Ѡ♣҉  \n");

        verifyAllEvents("listener(0) => [HERO_DIED]\n"
            + "listener(1) => [KILL_OTHER_HERO]\n");
        assertHeroDie();
    }

    @Test
    public void shouldPotionDropped_whenHeroDropPotion() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
    }

    @Test
    public void shouldPotionDropped_whenHeroDropPotionAtAnotherPlace() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().up();
        tick();

        hero().right();
        tick();

        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");
    }

    @Test
    public void shouldPotionsDropped_whenHeroDropThreePotion() {
        // given
        settings().integer(POTIONS_COUNT, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().up();
        tick();

        hero().dropPotion();
        tick();

        hero().right();
        tick();

        hero().dropPotion();
        tick();

        // then
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
        settings().integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().up();
        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☻    \n" +
                "     \n");

        // when
        hero().up();
        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☻    \n" +
                "3    \n" +
                "     \n");

        // when
        hero().up();
        hero().dropPotion();
        tick();

        // then
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
        settings().integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().dropPotion();
        tick();

        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        assertEquals(1, field().potions().size());

        // when
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");

        // when
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        // when
        tick();   // зелья больше нет, иначе тут был бы взрыв второй

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBoom_whenDroppedPotionHas5Ticks() {
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

        hero().right();
        tick();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    // проверить, что я могу поставить еще одно зелье, когда другое рвануло
    @Test
    public void shouldCanDropNewPotion_whenOtherBoom() {
        // given
        shouldBoom_whenDroppedPotionHas5Ticks();

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        // when
        hero().dropPotion();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☻  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed() {
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

        hero().right();
        tick();

        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_inOtherCorner() {
        // given
        givenFl("    ☺\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");

        // when
        hero().dropPotion();
        tick();

        hero().left();
        tick();

        hero().left();
        tick();

        tick();
        tick();

        // then
        assertF("  ☺҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldWallProtectsHero() {
        // given
        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        hero().dropPotion();
        goOut();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼ ☼ ☼\n" +
                "☼1  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉҉ ☼\n" +
                "☼☼☼☼☼\n");

        assertHeroAlive();
    }

    @Test
    public void shouldWallProtectsHero2() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        // when then
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

    private void assertPotionPower(int power, String expected) {
        // given
        settings().integer(POTION_POWER, power);

        // when
        hero().dropPotion();
        goOut();
        tick();

        // then
        assertF(expected);
    }

    private void goOut() {
        hero().right();
        tick();

        hero().right();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();
    }

    // разрыв зелья длинной указанной в settings
    @Test
    public void shouldChangePotionPower_to2() {
        // given
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        // when then
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
        // given
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        // when then
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
        // given
        givenFl("☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼☺      ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        // when then
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
        settings().integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().dropPotion();
        hero().right();
        tick();

        hero().dropPotion();
        hero().right();
        tick();

        // then
        List<Potion> potions1 = field().potions().all();
        List<Potion> potions2 = field().potions().all();
        List<Potion> potions3 = field().potions().all();
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

        // when
        tick();
        tick();

        // then
        assertEquals(false, potion11.isExploded());
        assertEquals(false, potion12.isExploded());
        assertEquals(false, potion13.isExploded());

        // when
        tick();

        // then
        assertEquals(true, potion11.isExploded());
        assertEquals(true, potion12.isExploded());
        assertEquals(true, potion13.isExploded());

        assertEquals(false, potion21.isExploded());
        assertEquals(false, potion22.isExploded());
        assertEquals(false, potion23.isExploded());

        // when
        tick();

        // then
        assertEquals(true, potion21.isExploded());
        assertEquals(true, potion22.isExploded());
        assertEquals(true, potion23.isExploded());

        verifyAllEvents("[HERO_DIED]");
    }

    @Test
    public void shouldReturnShouldNotSynchronizedPotionsList_whenUseBoardApi() {
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

        // then
        List<Potion> potions1 = field().potions().all();
        assertEquals(1, potions1.size());

        // when
        tick();
        tick();
        tick();
        tick();

        // then
        verifyAllEvents("[HERO_DIED]");

        List<Potion> potions2 = field().potions().all();
        assertEquals(0, potions2.size());
        assertEquals(0, potions1.size());
        assertEquals(potions1.toString(), potions2.toString());
    }

    @Test
    public void shouldChangeBlast_whenUseBoardApi() {  // TODO а нода вообще такое? стреляет по перформансу перекладывать объекты и усложняет код
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
        hero().right();
        tick();
        tick();
        tick();
        tick();

        // then
        List<Blast> blasts1 = field().blasts().all();
        List<Blast> blasts2 = field().blasts().all();
        List<Blast> blasts3 = field().blasts().all();
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
        settings().integer(POTION_POWER, 3);

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

        // when
        hero().right();
        tick();

        hero().right();
        tick();

        hero().up();
        tick();

        hero().dropPotion();
        tick();
        tick();
        tick();
        tick();
        tick();

        // then
        assertF("☼☼☼☼☼☼☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼҉☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼Ѡ☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼☼☼☼☼☼☼\n");

        verifyAllEvents("[HERO_DIED]");
    }

    @Test
    public void shouldStopBlastWhenHeroOrDestroyWalls() {
        // given
        settings().integer(POTION_POWER, 5);

        givenFl("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺  #   \n");

        // when
        dice(101); // don't drop perk by accident

        hero().dropPotion();
        hero().up();
        tick();

        hero().up();
        tick();

        tick();
        tick();
        tick();

        // then
        assertF("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "Ѡ      \n" +
                "҉      \n" +
                "҉҉҉H   \n");

        verifyAllEvents("[HERO_DIED, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldStopBlastWhenGhost() {
        // given
        settings().integer(POTION_POWER, 5);

        givenFl("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺   &  \n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        hero().right();
        tick();
        tick();

        // then
        assertF("       \n" +
                "҉      \n" +
                "҉      \n" +
                "҉☺     \n" +
                "҉      \n" +
                "҉      \n" +
                "҉҉҉҉x  \n");

        verifyAllEvents("[KILL_GHOST]");
    }

    // на поле можно чтобы каждый поставил то количество
    // зелья которое ему позволено и не более того
    @Test
    public void shouldTwoPotionsOnBoard() {
        // given
        settings().integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n", 0);
    }

    @Test
    public void shouldTwoPotionsOnBoard_withEnemy() {
        // given
        settings().integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        player(0).inTeam(0);
        player(1).inTeam(1);

        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺ö   \n" +
                "44   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺ö   \n" +
                "     \n" +
                "33   \n", 0);
    }

    @Test
    public void shouldFourPotionsOnBoard() {
        // given
        settings().integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺♥   \n" +
                "44   \n" +
                "33   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        hero(1).dropPotion();
        hero(1).up();

        tick();

        // then
        assertF("     \n" +
                "☺♥   \n" +
                "     \n" +
                "33   \n" +
                "22   \n", 0);
    }

    @Test
    public void shouldFourPotionsOnBoard_checkTwoPotionsPerHero() {
        // given
        settings().integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺☺   \n");

        // when
        hero(0).dropPotion();
        hero(0).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "4♥   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "4    \n" +
                "3♥   \n", 0);

        // when
        hero(0).dropPotion();
        hero(0).up();

        tick();

        // then
        assertF("     \n" +
                "☺    \n" +
                "     \n" +
                "3    \n" +
                "2♥   \n", 0);
    }
}