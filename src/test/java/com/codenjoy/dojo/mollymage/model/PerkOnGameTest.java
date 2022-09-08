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

import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionBlastRadiusIncrease;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionCountIncrease;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;

public class PerkOnGameTest extends AbstractGameTest {

    // BBRI = Potion Blast Radius Increase perk
    // проверяем, что перков может появиться два
    // проверяем, что перки не пропадают на следующий тик
    // проверяем, что перк можно подобрать
    @Test
    public void shouldHeroAcquirePerk_whenMoveToFieldWithPerk() {
        // given
        givenFl("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(50);

        // must drop 2 perks
        dice(10);

        hero().dropPotion();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();

        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#1 ☺ #\n" +
                "######\n");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        assertFieldPerks("");

        verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=49} at [0,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=49} at [1,0]}");

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
        hero().left();
        tick();

        hero().left();
        tick();

        int before = hero().scores();
        assertEquals(2 * settings().integer(OPEN_TREASURE_BOX_SCORE), before);

        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+☺   #\n" +
                "#+####\n");

        // when
        // go for perk
        hero().left();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "☺    #\n" +
                "#+####\n");

        verifyAllEvents("[CATCH_PERK]");
        assertEquals(before + settings().integer(CATCH_PERK_SCORE), hero().scores());
        assertEquals("Hero had to acquire new perk",
                1, player().getHero().getPerks().size());
    }

    @Test
    public void shouldPerkBeDropped_whenWallIsDestroyed() {
        // given
        givenFl("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 5, 3);
        perks.dropRatio(20); // 20%
        // must drop 1 perk
        dice(10, 30);

        hero().dropPotion();
        tick();

        hero().up();
        tick();

        hero().up();
        tick();

        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "# # ##\n" +
                "#1   #\n" +
                "######\n");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#҉# ##\n" +
                "H҉҉  #\n" +
                "#H####\n");

        verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // две коробки потрачено
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "# # ##\n" +
                "+    #\n" +
                "# ####\n");
    }

    // проверяем, что перк удалится с поля через N тиков если его никто не возьмет
    @Test
    public void shouldRemovePerk_whenPickTimeout() {
        // given
        givenFl("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(5);

        // must drop 2 perks
        dice(10);

        hero().dropPotion();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        tick();

        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#1 ☺ #\n" +
                "######\n");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertFieldPerks("");

        // when
        removeBoxes(2); // две коробки потрачено
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=4} at [0,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=4} at [1,0]}");

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

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=1} at [0,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=1} at [1,0]}");

        // when
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "   ☺ #\n" +
                "# ####\n");

        assertFieldPerks("");
    }

    // проверяем, что уничтожение перка порождает злого-анти-привидение :)
    @Test
    public void shouldDropPerk_generateNewGhost() {
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();

        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().dropPotion();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        hero().up();
        tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        verifyAllEvents("");

        // when
        tick();

        // then
        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉   #\n" +
                "#҉#☺##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // given
        // такой себе хак, мы в домике
        hero().move(3, 4);
        newBox(1, 2); // две коробки подорвали, две добавили
        newBox(1, 3);
        field().walls().add(new Wall(pt(1, 4)));

        // when
        tick();

        // then
        // привидение начало свое движение
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "### ##\n" +
                " x   +\n" +
                "# ####\n");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "#H# ##\n" +
                "     +\n" +
                "# ####\n");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "# H ##\n" +
                "     +\n" +
                "# ####\n");

        // when
        removeBoxes(1); // минус коробка
        tick();

        // then
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##x  #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        // when
        removeBoxes(1); // минус коробка
        tick();

        // then
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "## x #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("[HERO_DIED]");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=44} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=44} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=43} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=39} at [3,4]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=43} at [5,1]}");

        // when
        dice(1, 1);
        tick();
        // это сделает сервер
        game().newGame(); // это сделает сервер

        // then
        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=42} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [3,4]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=42} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#+##\n" +
                "##   #\n" +
                "#   ##\n" +
                " ☺   +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=41} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=37} at [3,4]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=41} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "#☼#+##\n" +
                "##   #\n" +
                "#   ##\n" +
                " ☺   +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=40} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=36} at [3,4]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=40} at [5,1]}");
    }

    // а теперь пробуем убить анти-привидение
    @Test
    public void shouldDropPerk_generateNewGhost_thenKillIt() {
        // given
        settings().integer(POTIONS_COUNT, 2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();

        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#3# ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        verifyAllEvents("");

        // when
        tick();

        // then
        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☺  #\n" +
                "#2# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше
        tick();

        // then
        // привидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#1# ##\n" +
                " x   +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [5,1]}");

        // when
        tick();

        // then
        // приведение нарвалось на зелье
        assertF("#+####\n" +
                "# # ##\n" +
                "#҉☺  #\n" +
                "H&H ##\n" +
                " ҉   +\n" +
                "# ####\n");

        // пошел сигнал об этом
        verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=39} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [5,1]}");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");


        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [0,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [2,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [0,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=37} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [2,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [5,1]}");
    }

    // а теперь пробуем убить анти-привидение и одновременно с этим выпиливаемся на той же бомбе
    @Test
    public void shouldKillGhostWithSuicide() {
        // given
        settings().integer(POTIONS_COUNT, 2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        hero().getPerks().clear(); // удаляем любые перки

        // взрывная волна большая
        settings().integer(POTION_POWER, 5);

        // when
        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        tick();

        hero().dropPotion();
        hero().up();
        tick();

        hero().right();
        tick();

        // then
        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#3# ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        verifyAllEvents("");

        // when
        tick();

        // then
        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☺  #\n" +
                "#2# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше
        hero().left();
        tick();

        // then
        // привидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "#1# ##\n" +
                " x   +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [5,1]}");

        // when
        tick();

        // then
        // приведение нарвалось на зелье
        // но и мы подорвались с ним
        assertF("#+####\n" +
                "# # ##\n" +
                "#Ѡ   #\n" +
                "H&H ##\n" +
                " ҉   +\n" +
                "#҉####\n");

        // пошел сигнал об этом
        verifyAllEvents("[HERO_DIED, KILL_GHOST, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=39} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [5,1]}");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше

        dice(0, 1);
        // это сделает сервер
        game().newGame(); // это сделает сервер

        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "+++ ##\n" +
                "☺    +\n" +
                "# ####\n");

        verifyAllEvents("");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [0,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [2,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "+++ ##\n" +
                "☺    +\n" +
                "# ####\n");

        verifyAllEvents("");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [0,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=37} at [1,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=46} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [2,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [5,1]}");
    }

    // а теперь пробуем убить анти-привидение сразу после того как оно меня скушает
    @Test
    public void shouldKillGhostAfterEatMe() {
        // given
        settings().integer(POTIONS_COUNT, 2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();

        // when
        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        // when
        hero().dropPotion();
        hero().up();
        tick();

        tick();

        hero().up();
        tick();

        hero().right();
        tick();

        // then
        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "# # ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        verifyAllEvents("");

        // when
        hero().dropPotion();
        tick();

        // then
        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☻  #\n" +
                "#҉# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        removeBoxes(2); // на две взорвавшиеся коробки меньше
        hero().left();
        tick();

        // then
        // привидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "#☺3  #\n" +
                "# # ##\n" +
                " x   +\n" +
                "# ####\n");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#☺2  #\n" +
                "#x# ##\n" +
                "     +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [5,1]}");

        // when
        tick();

        // then
        // приведение скушало героя
        assertF("#+####\n" +
                "# # ##\n" +
                "#Ѡ1  #\n" +
                "# # ##\n" +
                "     +\n" +
                "# ####\n");

        // пошел сигнал об этом
        verifyAllEvents("[HERO_DIED]");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [5,1]}");

        // when
        dice(0, 1);
        // это сделает сервер
        game().newGame(); // это сделает сервер

        tick();

        // then
        // умирающий охотник подорвался на оставшейся после героя бомбе
        assertF("#+####\n" +
                "# H ##\n" +
                "#&҉҉ #\n" +
                "# H ##\n" +
                "☺    +\n" +
                "# ####\n");

        verifyAllEvents("");

        // появился перк
        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=39} at [1,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [5,1]}");

        // when
        tick();

        // then
        assertF("#+####\n" +
                "# + ##\n" +
                "#+   #\n" +
                "# + ##\n" +
                "☺#   +\n" +
                "# ####\n");

        verifyAllEvents("");

        // и еще два после рахрушенных стен
        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [1,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [2,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=49} at [2,4]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [5,1]}");
    }

    // генерим три привидение и смотрим как они бегут за мной
    @Test
    public void shouldDropPerk_generateThreeGhosts() {
        // given
        shouldDropPerk_generateNewGhost_thenKillIt();

        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        // when
        // бамбанули между двух перков, хак (перк при этом не взяли)
        hero().move(1, 2);
        hero().dropPotion();

        // given
        // строим оборону
        field().boxes().removeAt(pt(5, 5));
        field().boxes().removeAt(pt(5, 4));
        field().boxes().removeAt(pt(4, 4));
        field().boxes().removeAt(pt(4, 5));

        field().walls().add(new Wall(pt(4, 4)));
        field().walls().add(new Wall(pt(4, 5)));

        hero().move(5, 5); // убегаем в укрытие

        // when
        removeBoxes(4); // на 4 коробки меньше
        tick();

        // then
        assertEquals(0, hero().getPerks().size()); // перк не взяли

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+4+ ##\n" +
                "     +\n" +
                "# ####\n");

        // when
        tick();
        tick();
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+1+ ##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");

        // when
        // породили три чудовища
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#҉   #\n" +
                "xxx ##\n" +
                " ҉   +\n" +
                "# ####\n");

        verifyAllEvents("[DROP_PERK, DROP_PERK, DROP_PERK]");

        // when
        // и они пошли за нами
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                " xxx##\n" +
                "     +\n" +
                "# ####\n");


        // when
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  x #\n" +
                "  xx##\n" +
                "     +\n" +
                "# ####\n");

        // when
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  xx#\n" +
                "   x##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");
    }

    // если анти-привидения не могут найти к тебе короткий путь - они выпиливаются
    // вместо них будут перки
    @Test
    public void shouldDropPerk_generateTwoGhosts_noWayNoPain() {
        // given
        shouldDropPerk_generateThreeGhosts();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  xx#\n" +
                "   x##\n" +
                "     +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [5,1]}");

        // when
        // но стоит забарикадироваться
        field().walls().add(new Wall(pt(5, 4)));
        tick();

        // then
        // как привидения нормальнеют
        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  &&#\n" +
                "   &##\n" +
                "     +\n" +
                "# ####\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=37} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=43} at [3,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=32} at [3,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=43} at [4,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=37} at [5,1]}");

        // when
        // и после выпиливаются
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  ++#\n" +
                "   +##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=36} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=42} at [3,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=31} at [3,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=42} at [4,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=36} at [5,1]}");

        // when
        // перки дальше тикаются нормально
        tick();

        // then
        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  ++#\n" +
                "   +##\n" +
                "     +\n" +
                "# ####\n");

        verifyAllEvents("");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=4, timeout=3, timer=3, pick=35} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=41} at [3,2]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=30} at [3,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=41} at [4,3]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=35} at [5,1]}");
    }

    // если мы вызвали потустороннюю нечисть, то наш суицид ее успокоит, отправив обратно
    @Test
    public void shouldDropPerk_generateNewGhost_thenSuicide_willKillChopperAlso() {
        // given
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();

        // when
        hero().right();
        tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        // when
        hero().dropPotion();
        tick();

        hero().right();
        tick();

        hero().right();
        tick();

        hero().up();
        tick();

        // then
        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        verifyAllEvents("");

        // when
        tick();

        // then
        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉   #\n" +
                "#҉#☺##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        // охотник идет
        removeBoxes(2); // две коробки потрачено взрывом
        tick();
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                "  x  +\n" +
                "#  ###\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=48} at [5,1]}");

        // when
        // мувнули героя и кикнули его
        hero().die();
        removeBoxes(1); // одна коробка потречена злым привидением
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  &  +\n" +
                "#  ###\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=39} at [2,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=47} at [5,1]}");

        verifyAllEvents("[HERO_DIED]");

        // when
        // превратился в перк обратно
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  +  +\n" +
                "#  ###\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=38} at [2,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=46} at [5,1]}");

        // when
        // и тикается каждую секунду как и тот, что не трогали на поле
        tick();

        // then
        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  +  +\n" +
                "#  ###\n");

        assertFieldPerks(
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [1,5]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=37} at [2,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=4, timeout=3, timer=3, pick=45} at [5,1]}");
    }

    // проверяем, что перк пропадает после таймаута
    @Test
    public void shouldPerkBeDeactivated_whenTimeout() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
        int timeout = 3; // время работы перка

        int value = 4;   // показатель его влияния, в тесте не интересно
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, value, timeout);
        perks.dropRatio(20);

        player().getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));
        assertEquals("Hero had to acquire new perk",
                1, player().getHero().getPerks().size());

        // when
        tick();
        tick();
        tick();

        // then
        assertEquals("Hero had to lose perk",
                0, player().getHero().getPerks().size());
    }

    @Test
    public void shouldCatchSeveralPerks_whenTherAreInTheOneCell() {
        // given
        givenFl("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        int timeout = 4;
        newPerk(0, 1, new PotionCountIncrease(1, timeout));
        newPerk(0, 1, new PotionBlastRadiusIncrease(2, timeout));

        assertFieldPerks(
                "{PerkOnBoard {POTION_COUNT_INCREASE('c')\n" +
                "  value=1, timeout=4, timer=4, pick=0} at [0,1]}\n" +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=2, timeout=4, timer=4, pick=0} at [0,1]}");

        assertEquals("[]" ,
                hero().getPerks().toString());

        assertF("     \n" +
                "     \n" +
                "     \n" +
                "c    \n" +
                "☺    \n");

        // when
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");

        verifyAllEvents("[CATCH_PERK, CATCH_PERK]");

        assertFieldPerks("");

        assertHeroPerks(
                "{POTION_COUNT_INCREASE('c')\n" +
                "  value=1, timeout=4, timer=3, pick=0}\n" +
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=2, timeout=4, timer=3, pick=0}");

        // when
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertFieldPerks("");

        assertHeroPerks(
                "{POTION_COUNT_INCREASE('c')\n" +
                "  value=1, timeout=4, timer=2, pick=0}\n" +
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" +
                "  value=2, timeout=4, timer=2, pick=0}");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertFieldPerks("");

        assertHeroPerks(
                "{POTION_COUNT_INCREASE('c')\n" + 
                "  value=1, timeout=4, timer=1, pick=0}\n" +
                "{POTION_BLAST_RADIUS_INCREASE('+')\n" + 
                "  value=2, timeout=4, timer=1, pick=0}");

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertFieldPerks("");

        assertEquals("[]" ,
                hero().getPerks().toString());
    }
}