package com.codenjoy.dojo.mollymage.game;

import com.codenjoy.dojo.mollymage.model.items.Potion;
import com.codenjoy.dojo.mollymage.model.items.blast.Blast;
import com.codenjoy.dojo.services.Point;
import org.junit.Test;

import java.util.List;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTIONS_COUNT;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.POTION_POWER;
import static org.junit.Assert.*;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class PotionTest extends AbstractGameTest {

    @Test
    public void shouldPotionDropped_whenHeroDropPotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
    }

    @Test
    public void shouldPotionDropped_whenHeroDropPotionAtAnotherPlace() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");
    }

    @Test
    public void shouldPotionsDropped_whenHeroDropThreePotion() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(3);

        hero().up();
        field.tick();

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "2☻   \n" +
                "     \n");
    }

    // проверить, что герой не может класть зелья больше,
    // чем у него в settings прописано
    @Test
    public void shouldOnlyTwoPotions_whenLevelApproveIt() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().up();
        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☻    \n" +
                "     \n");

        hero().up();
        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☻    \n" +
                "3    \n" +
                "     \n");

        hero().up();
        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "☺    \n" +
                "3    \n" +
                "2    \n" +
                "     \n");
    }

    // герой не может класть два зелья на одно место
    @Test
    public void shouldOnlyOnePotionPerPlace() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);

        hero().act();
        field.tick();

        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        assertEquals(1, field.potions().size());

        hero().right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");

        hero().right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        field.tick();   // зелья больше нет, иначе тут был бы взрыв второй

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBoom_whenDroppedPotionHas5Ticks() {
        givenBr("     \n" +
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

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    // проверить, что я могу поставить еще одно зелье, когда другое рвануло
    @Test
    public void shouldCanDropNewPotion_whenOtherBoom() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        shouldBoom_whenDroppedPotionHas5Ticks();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        hero().act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☻  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed() {
        givenBr("     \n" +
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

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    @Test
    public void shouldBlastAfter_whenPotionExposed_inOtherCorner() {
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        gotoMaxUp();
        gotoMaxRight();

        hero().act();
        field.tick();

        hero().left();
        field.tick();

        hero().left();
        field.tick();

        field.tick();
        field.tick();

        asrtBrd("  ☺҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldWallProtectsHero() {
        givenBr("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        hero().act();
        goOut();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼ ☼ ☼\n" +
                "☼1  ☼\n" +
                "☼☼☼☼☼\n");

        field.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉҉ ☼\n" +
                "☼☼☼☼☼\n");

        assertHeroAlive();
    }

    @Test
    public void shouldWallProtectsHero2() {
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
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
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
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
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
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
        givenBr("☼☼☼☼☼☼☼☼☼\n" +
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
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        canDropPotions(2);
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
    }

    @Test
    public void shouldReturnShouldNotSynchronizedPotionsList_whenUseBoardApi() {
        givenBr("     \n" +
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

        List<Potion> potions2 = field.potions().all();
        assertEquals(0, potions2.size());
        assertEquals(0, potions1.size());
        assertEquals(potions1.toString(), potions2.toString());
    }

    @Test
    public void shouldChangeBlast_whenUseBoardApi() {  // TODO а нода вообще такое? стреляет по перформансу перекладывать объекты и усложняет код
        givenBr("     \n" +
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
        settings.integer(POTION_POWER, 3);
        givenBr("☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼     ☼\n" +
                "☼ ☼ ☼ ☼\n" +
                "☼☺    ☼\n" +
                "☼☼☼☼☼☼☼\n");

        asrtBrd("☼☼☼☼☼☼☼\n" +
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

        asrtBrd("☼☼☼☼☼☼☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼҉☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼ ☼Ѡ☼ ☼\n" +
                "☼  ҉  ☼\n" +
                "☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldStopBlastWhenHeroOrDestroyWalls() {
        potionsPower(5);

        givenBr("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺      \n");

        int count = 1;
        boxesCount(count);
        boxAt(3, 0);

        when(dice.next(anyInt())).thenReturn(101); // don't drop perk by accident

        hero().act();
        hero().up();
        field.tick();

        hero().up();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "Ѡ      \n" +
                "҉      \n" +
                "҉҉҉H   \n");
    }

    @Test
    public void shouldStopBlastWhenGhost() {
        potionsPower(5);

        givenBr("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "☺      \n");

        ghostsCount(1);
        ghostAt(4, 0).stop();

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

        asrtBrd("       \n" +
                "҉      \n" +
                "҉      \n" +
                "҉☺     \n" +
                "҉      \n" +
                "҉      \n" +
                "҉҉҉҉x  \n");
    }

    // на поле можно чтобы каждый поставил то количество
    // зелья которое ему позволено и не более того
    @Test
    public void shouldTwoPotionsOnBoard() {
        settings.integer(POTIONS_COUNT, 1);

        dice(dice,
                0, 0,
                1, 0);
        givenBr(2);

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
        givenBr(2);
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
        givenBr(2);

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
        givenBr(2);

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
}
