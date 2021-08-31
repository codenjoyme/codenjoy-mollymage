package com.codenjoy.dojo.mollymage.game.perks;

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

import com.codenjoy.dojo.games.mollymage.Element;
import com.codenjoy.dojo.mollymage.game.AbstractGameTest;
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.perks.PerkOnBoard;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionBlastRadiusIncrease;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionCountIncrease;
import org.junit.Test;

import java.util.Comparator;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.CATCH_PERK_SCORE;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.KILL_WALL_SCORE;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

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

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        field.tick();

        // when
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        assertPerks("[]");

        events.verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [0,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [1,0]}]");

        // when
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        hero().left();
        field.tick();

        hero().left();
        field.tick();

        int before = hero().scores();
        assertEquals(2 * settings.integer(KILL_WALL_SCORE), before);

        // when
        // go for perk
        hero().left();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "☺    #\n" +
                "#+####\n");

        events.verifyAllEvents("[CATCH_PERK]");
        assertEquals(before + settings.integer(CATCH_PERK_SCORE), hero().scores());
        assertEquals("Hero had to acquire new perk", 1, player().getHero().getPerks().size());
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

        hero().act();
        field.tick();

        hero().up();
        field.tick();

        hero().up();
        field.tick();

        hero().right();
        field.tick();

        // when
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#҉# ##\n" +
                "H҉҉  #\n" +
                "#H####\n");

        // when
        boxesCount(boxesCount() - 2); // две коробки потрачено
        field.tick();

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

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        field.tick();

        // when
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        assertPerks("[]");

        // when
        boxesCount(boxesCount() - 2); // две коробки потрачено
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=4} at [0,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=4} at [1,0]}]");

        // when
        field.tick();
        field.tick();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=1} at [0,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=1} at [1,0]}]");

        // when
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "   ☺ #\n" +
                "# ####\n");

        assertPerks("[]");

    }

    // проверяем, что уничтожение перка порождает злого-анти-привидение :)
    @Test
    public void shouldDropPerk_generateNewGhost() {
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener());

        hero().right();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        hero().up();
        field.tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        events.verifyAllEvents("[]");

        // when
        field.tick();

        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉   #\n" +
                "#҉#☺##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // такой себе хак, мы в домике
        hero().move(3, 4);
        newBox(1, 2); // две коробки подорвали, две добавили
        newBox(1, 3);
        field.walls().add(new Wall(1, 4));

        // when
        field.tick();

        // привидение начало свое движение
        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "### ##\n" +
                " x   +\n" +
                "# ####\n");

        field.tick();

        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "#H# ##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "# H ##\n" +
                "     +\n" +
                "# ####\n");

        boxesCount(boxesCount() - 1); // минус коробка
        field.tick();

        assertF("#+####\n" +
                "#☼#☺##\n" +
                "##x  #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        boxesCount(boxesCount() - 1); // минус коробка
        field.tick();

        assertF("#+####\n" +
                "#☼#☺##\n" +
                "## x #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [5,1]}]");

        field.tick();

        assertF("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[DIED]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=44} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=44} at [5,1]}]");

        field.tick();

        assertF("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=43} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=39} at [3,4]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=43} at [5,1]}]");

        dice(1, 1);
        field.tick();
        newGameForDied(); // это сделает сервер

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=42} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [3,4]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=42} at [5,1]}]");

        field.tick();

        assertF("#+####\n" +
                "#☼#+##\n" +
                "##   #\n" +
                "#   ##\n" +
                " ☺   +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=41} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [3,4]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=41} at [5,1]}]");


        field.tick();

        assertF("#+####\n" +
                "#☼#+##\n" +
                "##   #\n" +
                "#   ##\n" +
                " ☺   +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=40} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=36} at [3,4]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=40} at [5,1]}]");
    }

    // а теперь пробуем убить анти-привидение
    @Test
    public void shouldDropPerk_generateNewGhost_thenKillIt() {
        canDropPotions(2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener());

        hero().right();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().act();
        hero().up();
        field.tick();

        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#3# ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        events.verifyAllEvents("[]");

        // when
        field.tick();

        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☺  #\n" +
                "#2# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        field.tick();

        // пивидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#1# ##\n" +
                " x   +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [5,1]}]");

        // when
        field.tick();

        // приведение нарвалось на зелье
        assertF("#+####\n" +
                "# # ##\n" +
                "#҉☺  #\n" +
                "H&H ##\n" +
                " ҉   +\n" +
                "# ####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=39} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [5,1]}]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");


        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [0,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [2,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [5,1]}]");

        // when
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [0,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [2,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [5,1]}]");
    }

    // а теперь пробуем убить анти-привидение и одновременно с этим выпиливаемся на той же бомбе
    @Test
    public void shouldKillGhostWithSuicide() {
        canDropPotions(2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener());
        hero().getPerks().clear(); // удаляем любые перки
        potionsPower(5); // взрывная волна большая

        hero().right();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().act();
        hero().up();
        field.tick();

        field.tick();

        hero().act();
        hero().up();
        field.tick();

        hero().right();
        field.tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#3# ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        events.verifyAllEvents("[]");

        // when
        field.tick();

        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☺  #\n" +
                "#2# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        hero().left();
        field.tick();

        // привидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "#1# ##\n" +
                " x   +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [5,1]}]");

        // when
        field.tick();

        // приведение нарвалось на зелье
        // но и мы подорвались с ним
        assertF("#+####\n" +
                "# # ##\n" +
                "#Ѡ   #\n" +
                "H&H ##\n" +
                " ҉   +\n" +
                "#҉####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DIED, KILL_GHOST, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=39} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [5,1]}]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше

        dice(0, 1);
        newGameForDied(); // это сделает сервер

        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "+++ ##\n" +
                "☺    +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [0,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [2,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [5,1]}]");

        // when
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "+++ ##\n" +
                "☺    +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [0,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [1,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [2,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [5,1]}]");
    }

    // а теперь пробуем убить анти-привидение сразу после того как оно меня скушает
    @Test
    public void shouldKillGhostAfterEatMe() {
        canDropPotions(2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener());

        hero().right();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().act();
        hero().up();
        field.tick();

        field.tick();

        hero().up();
        field.tick();

        hero().right();
        field.tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "# # ##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        events.verifyAllEvents("[]");

        // when
        hero().act();
        field.tick();

        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉☻  #\n" +
                "#҉# ##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        hero().left();
        field.tick();

        // пивидение начало свое движение
        assertF("#+####\n" +
                "# # ##\n" +
                "#☺3  #\n" +
                "# # ##\n" +
                " x   +\n" +
                "# ####\n");

        // when
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#☺2  #\n" +
                "#x# ##\n" +
                "     +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [5,1]}]");

        // when
        field.tick();

        // приведение скушало героя
        assertF("#+####\n" +
                "# # ##\n" +
                "#Ѡ1  #\n" +
                "# # ##\n" +
                "     +\n" +
                "# ####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DIED]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [5,1]}]");

        // when
        dice(0, 1);
        newGameForDied(); // это сделает сервер

        field.tick();

        // умирающий охотник подорвался на оставшейся после героя бомбе
        assertF("#+####\n" +
                "# H ##\n" +
                "#&҉҉ #\n" +
                "# H ##\n" +
                "☺    +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        // появился перк
        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=39} at [1,3]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [5,1]}]");

        // when
        field.tick();

        assertF("#+####\n" +
                "# + ##\n" +
                "#+   #\n" +
                "# + ##\n" +
                "☺#   +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        // и еще два после рахрушенных стен
        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [1,3]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [1,5]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [2,2]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=49} at [2,4]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [5,1]}]");
    }

    // генерим три привидение и смотрим как они бегут за мной
    @Test
    public void shouldDropPerk_generateThreeGhosts() {
        shouldDropPerk_generateNewGhost_thenKillIt();

        assertF("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        // бамбанули между двух перков, хак (перк при этом не взяли)
        hero().move(1, 2);
        hero().act();

        // строим оборону
        field.boxes().remove(pt(5, 5));
        field.boxes().remove(pt(5, 4));
        field.boxes().remove(pt(4, 4));
        field.boxes().remove(pt(4, 5));

        field.walls().add(new Wall(4, 4));
        field.walls().add(new Wall(4, 5));

        hero().move(5, 5); // убегаем в укрытие

        boxesCount(boxesCount() - 4); // на 4 коробки меньше
        field.tick();
        assertEquals(0, hero().getPerks().size()); // перк не взяли

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+4+ ##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();
        field.tick();
        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+1+ ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        // породили три чудовища
        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#҉   #\n" +
                "xxx ##\n" +
                " ҉   +\n" +
                "# ####\n");

        events.verifyAllEvents("[DROP_PERK, DROP_PERK, DROP_PERK]");

        // и они пошли за нами
        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                " xxx##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  x #\n" +
                "  xx##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  xx#\n" +
                "   x##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");
    }

    // если анти-привидения не могут найти к тебе короткий путь - они выпиливаются
    // вместо них будут перки
    @Test
    public void shouldDropPerk_generateTwoGhosts_noWayNoPain() {
        shouldDropPerk_generateThreeGhosts();

        assertF("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  xx#\n" +
                "   x##\n" +
                "     +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [5,1]}]");


        // но стоит забарикадироваться
        field.walls().add(new Wall(5, 4));
        field.tick();

        // как привидения нормальнеют
        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  &&#\n" +
                "   &##\n" +
                "     +\n" +
                "# ####\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=43} at [3,2]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=32} at [3,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=43} at [4,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [5,1]}]");

        // и после выпиливаются
        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  ++#\n" +
                "   +##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");


        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=36} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=42} at [3,2]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=31} at [3,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=42} at [4,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=36} at [5,1]}]");

        // перки дальше тикаются нормально
        field.tick();

        assertF("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  ++#\n" +
                "   +##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=35} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=41} at [3,2]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=30} at [3,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=41} at [4,3]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=35} at [5,1]}]");

    }

    private void assertPerks(String expected) {
        assertEquals(expected,
                fix(field.perks().stream()
                        .sorted(Comparator.comparing(PerkOnBoard::copy))
                        .collect(toList())
                        .toString()));
    }

    private String fix(String string) {
        return string.replace("}, {", "},\n {");
    }

    // если мы вызвали потустороннюю нечисть, то наш суицид ее успокоит, отправив обратно
    @Test
    public void shouldDropPerk_generateNewGhost_thenSuicide_willKillChopperAlso() {
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener());

        hero().right();
        field.tick();

        // then
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero().act();
        field.tick();

        hero().right();
        field.tick();

        hero().right();
        field.tick();

        hero().up();
        field.tick();

        // перед взрывом
        assertF("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                " 1   #\n" +
                "#+####\n");

        // все тихо
        events.verifyAllEvents("[]");

        // when
        field.tick();

        // перк разрушен
        // а вместо него злое привидение
        assertF("#H####\n" +
                "#҉# ##\n" +
                "#҉   #\n" +
                "#҉#☺##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // охотник идет
        boxesCount(boxesCount() - 2); // две коробки потрачено взрывом
        field.tick();
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                "  x  +\n" +
                "#  ###\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=48} at [5,1]}]");

        // мувнули героя и кикнули его
        hero().die();
        boxesCount(boxesCount() - 1); // одна коробка потречена злым привидением
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  &  +\n" +
                "#  ###\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=39} at [2,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=47} at [5,1]}]");

        events.verifyAllEvents("[DIED]");

        // превратился в перк обратно
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  +  +\n" +
                "#  ###\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=38} at [2,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=46} at [5,1]}]");

        // и тикается каждую секунду как и тот, что не трогали на поле
        field.tick();

        assertF("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  +  +\n" +
                "#  ###\n");

        assertPerks("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [1,5]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=37} at [2,1]},\n " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=45} at [5,1]}]");

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
        field.tick();
        field.tick();
        field.tick();

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

        assertPerks("[{PerkOnBoard {POTION_COUNT_INCREASE('c') value=1, timeout=4, timer=4, pick=0} at [0,1]},\n" +
                " {PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=2, timeout=4, timer=4, pick=0} at [0,1]}]");

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

        assertPerks("[]");

        assertEquals("[{POTION_COUNT_INCREASE('c') value=1, timeout=4, timer=3, pick=0}, " +
                        "{POTION_BLAST_RADIUS_INCREASE('+') value=2, timeout=4, timer=3, pick=-1}]" ,
                hero().getPerks().toString());

        // when
        hero().up();
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertPerks("[]");

        assertEquals("[{POTION_COUNT_INCREASE('c') value=1, timeout=4, timer=2, pick=0}, " +
                        "{POTION_BLAST_RADIUS_INCREASE('+') value=2, timeout=4, timer=2, pick=-1}]" ,
                hero().getPerks().toString());

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertPerks("[]");

        assertEquals("[{POTION_COUNT_INCREASE('c') value=1, timeout=4, timer=1, pick=0}, " +
                        "{POTION_BLAST_RADIUS_INCREASE('+') value=2, timeout=4, timer=1, pick=-1}]" ,
                hero().getPerks().toString());

        // when
        tick();

        // then
        assertF("     \n" +
                "     \n" +
                "☺    \n" +
                "     \n" +
                "     \n");

        assertPerks("[]");

        assertEquals("[]" ,
                hero().getPerks().toString());

    }
}
