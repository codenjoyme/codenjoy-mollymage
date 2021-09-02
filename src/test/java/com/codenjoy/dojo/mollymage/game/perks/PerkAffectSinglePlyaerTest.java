package com.codenjoy.dojo.mollymage.game.perks;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.game.AbstractGameTest;
import com.codenjoy.dojo.mollymage.model.items.perks.*;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static org.junit.Assert.assertEquals;

public class PerkAffectSinglePlyaerTest extends AbstractGameTest {

    public static final int PERKS_TIMEOUT = 10;

    private PotionExploder getPotionExploderPerk() {
        return new PotionExploder(1, PERKS_TIMEOUT);
    }

    // перк исчезает спустя некоторое время
    @Test
    public void shouldPerkDisappearWhenTimeout() {
        // given
        settings.integer(PERK_PICK_TIMEOUT, 5);

        givenFl("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%

        // must drop 2 perks
        dice(10);

        hero().act();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        events.verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        // when
        tick();
        tick();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "   ☺ #\n" +
                "# ####\n");
    }

    // новый герой не может появиться на перке
    @Test
    public void shouldHeroCantSpawnOnPerk() {
        // given
        givenFl("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%

        // must drop 2 perks
        dice(10);

        hero().act();
        tick();

        hero().right();
        tick();

        tick();

        tick();

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉Ѡ  #\n" +
                "#H####\n");

        events.verifyAllEvents("[DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // две коробки потрачено
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+ Ѡ  #\n" +
                "#+####\n");

        // when
        // вот он последний тик перед взрывом, тут все и случится
        dice(0, 1, // пробуем разместить героя поверх перка1
            1, 0,  // пробуем разместить героя поверх перка2
            3, 3); // а потом в свободное место
        tick();
        game().newGame(); // это сделает сервер

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#  ☺ #\n" +
                "# # ##\n" +
                "+    #\n" +
                "#+####\n");
    }

    // Проверяем длинну волны взрывной в отсутствии перка BBRI
    @Test
    public void shouldPotionBlastRadiusIncrease_whenNoBBRIperk() {
        // given
        givenFl("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#☺         #\n" +
                "############\n");

        hero().act();
        tick();

        tick();
        tick();
        tick();

        // when
        tick();

        // then
        assertF("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "#҉# # # # ##\n" +
                "HѠ҉        #\n" +
                "#H##########\n");

        events.verifyAllEvents("[DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");
    }

    @Test
    public void shouldNotThrowPoison_withoutPTperk() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3);

        givenFl("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        // when
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");
    }

    @Test
    public void shouldNotDoAnythingWhenACTWithoutMove_withPTperk() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3);

        givenFl("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        int timeout = 10;
        player().getHero().addPerk(new PoisonThrower(timeout));

        // when
        // just ACT(1) without sending direction
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");
    }

    @Test
    public void shouldThrowPoisonThroughThePotion_withPTperk() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3);

        givenFl("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼☺       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");

        int timeout = 30;
        player().getHero().addPerk(new PoisonThrower(timeout));

        // when
        // hero set the potion and shoot through it
        hero().up();
        tick();

        hero().up();
        tick();

        hero().act();
        hero().down();
        tick();

        hero().down();
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼3       ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼☺       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");

        // when hero throw poison
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼҉       ☼\n" +
                "☼҉☼ ☼ ☼ ☼☼\n" +
                "☼2       ☼\n" +
                "☼҉☼ ☼ ☼ ☼☼\n" +
                "☼☺       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldDetonatePotionWhenThrowPoison_withPTperk_withBadaBoom() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3)
                .bool(BIG_BADABOOM, true);

        givenFl("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼☺       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");

        int timeout = 30;
        player().getHero().addPerk(new PoisonThrower(timeout));

        // when
        // hero set the potion and shoot through it
        hero().up();
        tick();

        hero().up();
        tick();

        hero().act();
        hero().down();
        tick();

        hero().down();
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼        ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼3       ☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼☺       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");

        // when
        // hero throw poison
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("☼☼☼☼☼☼☼☼☼☼\n" +
                "☼ ☼ ☼ ☼ ☼☼\n" +
                "☼҉       ☼\n" +
                "☼҉☼ ☼ ☼ ☼☼\n" +
                "☼҉       ☼\n" +
                "☼҉☼ ☼ ☼ ☼☼\n" +
                "☼҉҉҉҉҉   ☼\n" +
                "☼҉☼ ☼ ☼ ☼☼\n" +
                "☼Ѡ       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼\n");

        events.verifyAllEvents("[DIED]");
    }

    @Test
    public void shouldPerkWorksAfterCombine_WithPTPerk() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 1);

        givenFl("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        int timeout = 10;
        newPerk(1, 2, new PoisonThrower(timeout));

        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼T☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        // when
        // hero get perk
        hero().up();
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
        events.verifyAllEvents("[CATCH_PERK]");

        // when
        // hero used perk
        hero().down();
        hero().act(1);
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼☺☼ ☼\n" +
                "☼҉  ☼\n" +
                "☼☼☼☼☼\n");
        assertEquals(timeout - 2, hero().getPerk(Element.POISON_THROWER).getTimer());

        // when
        // hero picked one more perk
        newPerk(1, 3, new PoisonThrower(timeout));
        hero().up();
        tick();

        // then
        // perk timer should be doubled minus few steps
        assertF("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
        events.verifyAllEvents("[CATCH_PERK]");
        assertEquals(timeout * 2 - 3, hero().getPerk(Element.POISON_THROWER).getTimer());

        // when
        // hero use PT
        hero().down();
        hero().act(1);
        tick();

        // then
        assertF("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉  ☼\n" +
                "☼☼☼☼☼\n");

        assertEquals(timeout * 2 - 4,
                hero().getPerk(Element.POISON_THROWER).getTimer());
    }

    @Test
    public void shouldThrowPoison_whenPTperk() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3);

        givenFl("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        player().getHero().addPerk(new PoisonThrower(10));

        // when
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        // when
        // recharge hero should not throw poison
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        // when
        // recharge hero should not throw poison
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        // when
        // recharge done should throw again
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        // when
        // and going to recharge again
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");
    }

    @Test
    public void shouldThrowPoisonWithIncreasedPower_withPT_withPBRI_perks() {
        // given
        settings.integer(POTION_POWER, 4)
                .integer(POISON_THROWER_RECHARGE, 3);

        givenFl("##########\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#        #\n" +
                "# # # # ##\n" +
                "#☺       #\n" +
                "##########\n");

        int value = 2;
        int timeout = 10;
        player().getHero().addPerk(new PoisonThrower(timeout));
        player().getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));

        // when
        hero().up();
        hero().act(1);
        tick();

        // then
        assertF("##########\n" +
                "# # # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#҉       #\n" +
                "#҉# # # ##\n" +
                "#☺       #\n" +
                "##########\n");
    }

    // Проверяем что перк BBRI увеличивает длинну взрывной волны зелья
    @Test
    public void shouldPotionBlastRadiusIncrease_whenBBRIperk() {
        // given
        givenFl("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#☺         #\n" +
                "############\n");

        int value = 4;   // на сколько клеток разрывная волна увеличится (по умолчанию 1)
        int timeout = 5; // сколько это безобразие будет длиться

        player().getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));

        hero().act();
        tick();

        tick();
        tick();
        tick();

        // when
        tick();

        // then
        assertF("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "#҉# # # # ##\n" +
                "#҉         #\n" +
                "#҉# # # # ##\n" +
                "#҉         #\n" +
                "#҉# # # # ##\n" +
                "HѠ҉҉҉҉҉    #\n" +
                "#H##########\n");

        events.verifyAllEvents("[DIED, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");
    }

    // Проверяем что два перка BBRI увеличивают длинну взрывной волны зелья на размер второго перка
    // При этом общее время суммируется. Но так же важно, что перк влияет только на будущие зелья,
    // а не те, которые уже на поле. И после того как он отработает, все вернется как было.
    @Test
    public void shouldPotionBlastRadiusIncreaseTwice_whenBBRIperk() {
        // given
        givenFl("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#☺         #\n" +
                "############\n");

        int value = 4;   // на сколько клеток разрывная волна увеличится (по умолчанию 1)
        int timeout = 5; // сколько это безобразие будет длиться (времени должно хватить)

        player().getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));

        assertHeroPerks(
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=5, timer=5, pick=0}");

        // when
        hero().act();
        hero().up();
        tick();

        hero().up();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        // then
        assertHeroPerks(
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=5, timer=1, pick=0}");

        // given
        // второй перк взятый в самый последний момент перед взрывом
        // зелья повлияет не на нее, а на следующее зелье
        int newValue = 3; // проверим, что эти значения суммируются
        int newTimeout = 7;
        player().getHero().addPerk(new PotionBlastRadiusIncrease(newValue, newTimeout));

        assertHeroPerks(
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=7, timeout=8, timer=8, pick=0}");

        // when
        tick();

        // then
        assertF("############\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "# # # # # ##\n" +
                "#          #\n" +
                "#҉# # # # ##\n" +
                "#҉         #\n" +
                "#҉# # # # ##\n" +
                "#҉ ☺       #\n" +
                "#҉# # # # ##\n" +
                "H҉҉҉҉҉҉    #\n" +
                "#H##########\n");

        events.verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        hero().act();
        hero().right();

        // новые коробки
        dice(9, 1,
            9, 2);
        tick();

        hero().right();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        tick();

        // then
        assertF("###H########\n" +
                "# #҉# # # ##\n" +
                "#  ҉       #\n" +
                "# #҉# # # ##\n" +
                "#  ҉       #\n" +
                "# #҉# # # ##\n" +
                "#  ҉ ☺     #\n" +
                "# #҉# # # ##\n" +
                "H҉҉҉҉҉҉҉҉҉҉H\n" +
                "# #҉# # ####\n" +
                "   ҉     # #\n" +
                "# #H########\n");

        events.verifyAllEvents(
                "[KILL_TREASURE_BOX, KILL_TREASURE_BOX, " +
                "KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertHeroPerks(
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=7, timeout=8, timer=2, pick=0}");

        // when
        // новые коробки
        dice(9, 10,
            9, 9,
            9, 8,
            9, 7);
        tick();

        // последний шанс воспользоваться, но мы не будем
        assertHeroPerks(
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=7, timeout=8, timer=1, pick=0}");

        tick();

        assertHeroPerks("");

        // ставим новое зелье, чтобы убедиться, что больше перк не сработает
        hero().act();
        tick();

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("### ########\n" +
                "# # # # ####\n" +
                "#        # #\n" +
                "# # # # ####\n" +
                "#        # #\n" +
                "# # #҉# # ##\n" +
                "#   ҉Ѡ҉    #\n" +
                "# # #҉# # ##\n" +
                "            \n" +
                "# # # # ####\n" +
                "         # #\n" +
                "# # ########\n");

        events.verifyAllEvents("[DIED]");

        assertHeroPerks("");
    }

    // BCI - Potion Count Increase perk
    @Test
    public void shouldPotionCountIncrease_whenBCIPerk() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        hero().act();

        // obe potion by default on lel 1
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        hero().right();
        tick();

        // when
        hero().act();

        // then
        // no more potions :(
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");

        // when
        // add perk that gives 1+3 = 4 player's potions in total on the board
        player().getHero().addPerk(new PotionCountIncrease(3, 3));

        hero().act();
        hero().right();
        tick();

        hero().act();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "34☻  \n");

        // when
        hero().right();
        tick();

        hero().act();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "234☻ \n");

        // when
        hero().right();
        tick();

        hero().act();

        // then
        // 4 potions and no more
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1234☺\n");
    }

    // BI - Potion Immune perk
    @Test
    public void shouldHeroKeepAlive_whenBIperk() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        hero().act();
        hero().right();

        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");

        // when
        player().getHero().addPerk(new PotionImmune(6));

        tick();
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉☺   \n");

        // when
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        // when
        tick();
        tick();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n");

        events.verifyAllEvents("[DIED]");
    }

    // BRC - Potion remote control perk
    @Test
    public void shouldPotionBlastOnAction_whenBRCperk_caseTwoPotions() {
        // given
        settings.integer(POTIONS_COUNT, 2);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        player().getHero().addPerk(new PotionRemoteControl(2, 1));

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // поставили первое радиоуправляемое зелье
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "5☺   \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // видим, что она стоит и ждет
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // взорвали ее
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉☺   \n" +
                "҉҉   \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // ставим еще одну
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // отошли, смотрим
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                " 5   \n" +
                "     \n");

        // when
        hero().left();
        tick();

        // долго потикали, ничего не меняется, таймаутов нет
        tick();
        tick();
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
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                " 5   \n" +
                "     \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // взорвали ее
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n" +
                " ҉   \n");

        assertHeroPerks("");

        // when
        // ставим новую
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☻    \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // если отойдем, то увидим, что это обычная
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "3☺   \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // еще одну, у нас ведь их две
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "24☺  \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // больше не могу
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "13 ☺ \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // еще не могу
        hero().right();
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "҉    \n" +
                "҉2  ☺\n" +  // взрывная волна кстати не перекрывает зелье
                "҉    \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // и только когда ударная волна уйдет, тогда смогу
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " 1  ☻\n" +  // взрывная волна кстати не перекрывает зелье
                "     \n" +
                "     \n");

        assertHeroPerks("");
    }

    @Test
    public void shouldPotionBlastOnAction_whenBRCperk_caseOnePotion() {
        // given
        settings.integer(POTIONS_COUNT, 1);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        player().getHero().addPerk(new PotionRemoteControl(2, 1));

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // поставили первое радиоуправляемое зелье
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "5☺   \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // видим, что она стоит и ждет
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=2, timeout=1, timer=1, pick=0}");

        // when
        // взорвали ее
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉☺   \n" +
                "҉҉   \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // ставим еще одну
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // отошли, смотрим
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                " 5   \n" +
                "     \n");

        // when
        hero().left();
        tick();

        // долго потикали, ничего не меняется, таймаутов нет
        tick();
        tick();
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
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                " 5   \n" +
                "     \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // взорвали ее
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n" +
                " ҉   \n");

        assertHeroPerks("");

        // when
        // ставим новую
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☻    \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // если отойдем, то увидим, что это обычная
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "3☺   \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // больше не могу - у меня одна
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "2 ☺  \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // больше не могу
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "1  ☺ \n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // и теперь не могу - есть еще взрывная волна
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "҉    \n" +
                "҉҉  ☺\n" +
                "҉    \n" +
                "     \n");

        assertHeroPerks("");

        // when
        // а теперь пожалуйста
        hero().act();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "    ☻\n" +
                "     \n" +
                "     \n");

        assertHeroPerks("");
    }

    @Test
    public void shouldSuicide_whenBRCPerk_shouldRemoveAfterDeath_andCollectScores() {
        // given
        settings.integer(POTIONS_COUNT, 1)
                .integer(POTION_POWER, 3);

        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "#    \n" +
                "☺  & \n");

        player().getHero().addPerk(new PotionRemoteControl(1, 1));

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // поставили радиоуправляемое зелье
        hero().act();
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "#    \n" +
                "5☺ & \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // идем к привидению на верную смерть
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "#    \n" +
                "5 ☺& \n");

        assertHeroPerks(
                "{POTION_REMOTE_CONTROL('r')\n" +
                "  value=1, timeout=1, timer=1, pick=0}");

        // when
        // самоубился и всех выпилил )
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "H    \n" +
                "҉҉҉Ѡ \n");

        events.verifyAllEvents("[DIED, KILL_GHOST, KILL_TREASURE_BOX]");

        // только сейчас перк забрался
        assertHeroPerks("");

        // when
        // новая коробка
        dice(4, 4);
        tick();

        // then
        assertF("    #\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "   Ѡ \n");

        events.verifyAllEvents("[]");

        assertHeroPerks("");
    }

    @Test
    public void shouldDestroyAllPotion_WithPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        newPerk(0, 1, getPotionExploderPerk());
        hero().addPerk(new PotionCountIncrease(3, 30));

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "A    \n" +
                "☺    \n");

        assertEquals(1, hero().getPerks().size());

        // when
        // hero get perk PE
        hero().up();
        tick();

        // then
        events.verifyAllEvents("[CATCH_PERK]");
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");

        assertEquals(2, hero().getPerks().size());

        // when
        // hero plant different potions
        hero().act();
        hero().up();
        tick();

        hero().addPerk(new PotionRemoteControl(3, 30));
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                " ☺   \n" +
                "5    \n" +
                "2    \n" +
                "     \n");

        assertEquals(3, hero().getPerks().size());

        // when hero explode all potions
        hero().act(2);
        tick();

        // then
        assertF("     \n" +
                "҉☺   \n" +
                "҉҉   \n" +
                "҉҉   \n" +
                "҉    \n");
    }

    // PE - Potion Explored
    @Test
    public void shouldNotDestroyAllPotion_WithoutPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        hero().addPerk(new PotionCountIncrease(3, 30));

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        // hero plant different potions
        hero().act();
        hero().up();
        tick();

        hero().addPerk(new PotionRemoteControl(3, 30));
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n" +
                "2    \n");

        // when
        // hero tried explode all potions but can't without PE perk
        hero().act(2);
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n" +
                "1    \n");
    }

    @Test
    public void shouldMoveWhileUsingPerk_WithPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        newPerk(0, 1, getPotionExploderPerk());

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "A    \n" +
                "☺    \n");

        // when
        // hero catch perk and plant potion
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "3    \n");

        events.verifyAllEvents("[CATCH_PERK]");

        // when
        // hero explode potions and move right
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉ ☺  \n" +
                "҉҉   \n");
    }

    @Test
    public void shouldMoveWhileUsingPerk_WithoutPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        // when
        // hero plant potion
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "3    \n");

        // when
        // hero tried explode potions(but cant) and move right
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n" +
                "2    \n");
    }

    @Test
    public void shouldWorkOnlyOneTime_WithPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        newPerk(0, 1, getPotionExploderPerk());

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "A    \n" +
                "☺    \n");

        // when
        // hero catch perk and plant potion
        hero().act();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "3    \n");

        events.verifyAllEvents("[CATCH_PERK]");

        // when
        // hero explode potions and move right
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "҉ ☺  \n" +
                "҉҉   \n");

        // when
        // hero plant again
        hero().act();
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "  ☺  \n" +
                "  4  \n" +
                "     \n");

        // when
        // hero act(2) potion shouldn't boom
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "   ☺ \n" +
                "  3  \n" +
                "     \n");

    }

    // PE - Potion Explored
    @Test
    public void shouldCombinePerk_WithPerkPE() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        newPerk(0, 1, getPotionExploderPerk());

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "A    \n" +
                "☺    \n");

        // when
        // hero catch perk and plant potion
        hero().act();
        hero().up();
        tick();

        // then
        events.verifyAllEvents("[CATCH_PERK]");
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "4    \n");


        // when
        // one more perk
        newPerk(0, 2, getPotionExploderPerk());

        // hero catch it
        hero().up();
        tick();

        // then
        events.verifyAllEvents("[CATCH_PERK]");
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "3    \n");


        // when
        // hero explode potion and move right
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                " ☺   \n" +
                "҉    \n" +
                "҉҉   \n");

        // when
        // hero plant again
        hero().act();
        hero().up();
        tick();

        // then
        assertF("     \n" +
                " ☺   \n" +
                " 4   \n" +
                "     \n" +
                "     \n");

        // when
        // hero act(2) potion should boom
        hero().act(2);
        hero().right();
        tick();

        // then
        assertF("     \n" +
                " ҉☺  \n" +
                "҉҉҉  \n" +
                " ҉   \n" +
                "     \n");
    }

    // привидения тоже могут ставить зелье

    // Возможность проходить через стены. Герой прячется под
    // ней для других польхзователей виден как стена, и только
    // владелец видит себя там где он сейчас

    // Взорвать разом все зелья на поле. Вместе с ним
    // подрываются все зелья на поле

    // Огнемет. Командой ACT + LEFT/RIGHT/UP/DOWN посылается
    // ударная волна как от взрыва зелья в заданном направлении
    // на N клеточек

    // Возможность построить стену на месте героя.
    // Сам герой при этом прячется под ней, как в модификаторе
    // прохода через стену

    // Возможность запустить привидения в заданном
    // направлении. Командой ACT + LEFT/RIGHT/UP/DOWN
    // посылается привидение. Если это привидение поймает
    // другого героя, то очки засчитаются герою отославшему
    // это привидение (если герой жив до сих пор)

    // Атомное зелье которое прожигает стены насквозь
    // с максимальной ударной волной. О форме ударной
    // волны надо еще подумать

    // Создаеться клон который на протяжении какого-то
    // времени будет ходить и рандомно ставить зелье
    // от имение героя его породившего.
}
