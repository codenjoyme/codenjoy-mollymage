package com.codenjoy.dojo.mollymage.model;

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
import com.codenjoy.dojo.mollymage.model.items.Wall;
import com.codenjoy.dojo.mollymage.model.items.box.TreasureBox;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionBlastRadiusIncrease;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionCountIncrease;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionImmune;
import com.codenjoy.dojo.mollymage.model.items.perks.PotionRemoteControl;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

public class PerksTest extends AbstractGameTest {

    @Test
    public void shouldPerkBeDropped_whenWallIsDestroyed() {
        // given
        givenBoardWithBoxes(6);
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 5, 3);
        perks.dropRatio(20); // 20%
        dice(dice, 10, 30); // must drop 1 perk

        hero.act();
        field.tick();

        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.right();
        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#҉# ##\n" +
                "H҉҉  #\n" +
                "#H####\n");

        // when
        boxesCount(boxesCount() - 2); // две коробки потрачено
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "# # ##\n" +
                "+    #\n" +
                "# ####\n");
    }

    // новый герой не может появиться на перке
    @Test
    public void shouldHeroCantSpawnOnPerk() {
        // given
        givenBoardWithBoxes(6);

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%

        dice(dice, 10); // must drop 2 perks

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        field.tick();

        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉Ѡ  #\n" +
                "#H####\n");

        // when
        boxesCount(boxesCount() - 2); // две коробки потрачено
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+ Ѡ  #\n" +
                "#+####\n");

        // when
        // вот он последний тик перед взрывом, тут все и случится
        dice(dice,
                0, 1,   // пробуем разместить героя поверх перка1
                1, 0,   // пробуем разместить героя поверх перка2
                3, 3);  // а потом в свободное место
        field.tick();
        newGameForDied(); // это сделает сервер

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#  ☺ #\n" +
                "# # ##\n" +
                "+    #\n" +
                "#+####\n");
    }

    // BBRI = Potion Blast Radius Increase perk
    // проверяем, что перков может появиться два
    // проверяем, что перки не пропадают на следующий тик
    // проверяем, что перк можно подобрать
    @Test
    public void shouldHeroAcquirePerk_whenMoveToFieldWithPerk() {
        // given
        givenBoardWithBoxes(6);

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(50);

        dice(dice, 10); // must drop 2 perks

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        assertEquals("[]", field.perks().toString());

        events.verifyAllEvents("[KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        hero.left();
        field.tick();

        hero.left();
        field.tick();

        int before = hero.scores();
        assertEquals(2 * settings.integer(KILL_WALL_SCORE), before);

        // when
        // go for perk
        hero.left();
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "☺    #\n" +
                "#+####\n");

        events.verifyAllEvents("[CATCH_PERK]");
        assertEquals(before + settings.integer(CATCH_PERK_SCORE), hero.scores());
        assertEquals("Hero had to acquire new perk", 1, player.getHero().getPerks().size());
    }

    // проверяем, что перк удалится с поля через N тиков если его никто не возьмет
    @Test
    public void shouldRemovePerk_whenPickTimeout() {
        // given
        givenBoardWithBoxes(6);

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(5);

        dice(dice, 10); // must drop 2 perks

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "#҉# ##\n" +
                "H҉҉☺ #\n" +
                "#H####\n");

        assertEquals("[]", field.perks().toString());

        // when
        boxesCount(boxesCount() - 2); // две коробки потрачено
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertEquals("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=4} at [0,1]}, " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=4} at [1,0]}]", field.perks().toString());

        // when
        field.tick();
        field.tick();
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "+  ☺ #\n" +
                "#+####\n");

        assertEquals("[{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=1} at [0,1]}, " +
                "{PerkOnBoard {POTION_BLAST_RADIUS_INCREASE('+') value=4, timeout=3, timer=3, pick=1} at [1,0]}]", field.perks().toString());

        // when
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "   ☺ #\n" +
                "# ####\n");

        assertEquals("[]", field.perks().toString());

    }

    // проверяем, что уничтожение перка порождает злого-анти-привидение :)
    @Test
    public void shouldDropPerk_generateNewGhost() {
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener);

        hero.right();
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        hero.up();
        field.tick();

        // перед взрывом
        asrtBrd("######\n" +
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
        asrtBrd("#H####\n" +
                "#҉# ##\n" +
                "#҉   #\n" +
                "#҉#☺##\n" +
                "҉҉҉҉҉H\n" +
                "#x####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[DROP_PERK, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // такой себе хак, мы в домике
        hero.move(3, 4);
        boxAt(1, 2); // две коробки подорвали, две добавили
        boxAt(1, 3);
        field.objects().add(new Wall(1, 4));

        // when
        field.tick();

        // привидение начало свое движение
        asrtBrd("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "### ##\n" +
                " x   +\n" +
                "# ####\n");

        field.tick();

        asrtBrd("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "#H# ##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        asrtBrd("#+####\n" +
                "#☼#☺##\n" +
                "##   #\n" +
                "# H ##\n" +
                "     +\n" +
                "# ####\n");

        boxesCount(boxesCount() - 1); // минус коробка
        field.tick();

        asrtBrd("#+####\n" +
                "#☼#☺##\n" +
                "##x  #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        boxesCount(boxesCount() - 1); // минус коробка
        field.tick();

        asrtBrd("#+####\n" +
                "#☼#☺##\n" +
                "## x #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        asrtBrd("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[DIED]");

        field.tick();

        asrtBrd("#+####\n" +
                "#☼#Ѡ##\n" +
                "##   #\n" +
                "#   ##\n" +
                "     +\n" +
                "# ####\n");

        dice(dice,
                1, 1);
        field.tick();
        newGameForDied(); // это сделает сервер

        field.tick();

        asrtBrd("#+####\n" +
                "#☼#+##\n" +
                "##   #\n" +
                "#   ##\n" +
                " ☺   +\n" +
                "# ####\n");
    }

    // а теперь пробуем убить анти-привидение
    @Test
    public void shouldDropPerk_generateNewGhost_thenKillIt() {
        canDropPotions(2);

        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener);

        hero.right();
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero.act();
        hero.up();
        field.tick();

        field.tick();

        hero.act();
        hero.up();
        field.tick();

        hero.right();
        field.tick();

        // перед взрывом
        asrtBrd("######\n" +
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
        asrtBrd("#H####\n" +
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
        asrtBrd("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "#1# ##\n" +
                " x   +\n" +
                "# ####\n");

        // when
        field.tick();

        // приведение нарвалось на зелье
        asrtBrd("#+####\n" +
                "# # ##\n" +
                "#҉☺  #\n" +
                "H&H ##\n" +
                " ҉   +\n" +
                "# ####\n");

        // пошел сигнал об этом
        events.verifyAllEvents("[KILL_GHOST, KILL_TREASURE_BOX, KILL_TREASURE_BOX]");

        // when
        boxesCount(boxesCount() - 2); // на две взорвавшиеся коробки меньше
        field.tick();

        asrtBrd("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");
    }


    // генерим три привидение и смотрим как они бегут за мной
    @Test
    public void shouldDropPerk_generateThreeGhosts() {
        shouldDropPerk_generateNewGhost_thenKillIt();

        asrtBrd("#+####\n" +
                "# # ##\n" +
                "# ☺  #\n" +
                "+++ ##\n" +
                "     +\n" +
                "# ####\n");

        // бамбанули между двух перков, хак (перк при этом не взяли)
        hero.move(1, 2);
        hero.act();

        // строим оборону
        field.boxes().remove(pt(5, 5));
        field.boxes().remove(pt(5, 4));
        field.boxes().remove(pt(4, 4));
        field.boxes().remove(pt(4, 5));

        field.walls().add(new Wall(4, 4));
        field.walls().add(new Wall(4, 5));

        hero.move(5, 5); // убегаем в укрытие

        boxesCount(boxesCount() - 4); // на 4 коробки меньше
        field.tick();
        assertEquals(0, hero.getPerks().size()); // перк не взяли

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+4+ ##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();
        field.tick();
        field.tick();

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                "+1+ ##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");

        // породили три чудовища
        field.tick();

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#҉   #\n" +
                "xxx ##\n" +
                " ҉   +\n" +
                "# ####\n");

        events.verifyAllEvents("[DROP_PERK, DROP_PERK, DROP_PERK]");

        // и они пошли за нами
        field.tick();

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#    #\n" +
                " xxx##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  x #\n" +
                "  xx##\n" +
                "     +\n" +
                "# ####\n");

        field.tick();

        asrtBrd("#+##☼☺\n" +
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

        asrtBrd("#+##☼☺\n" +
                "# # ☼ \n" +
                "#  xx#\n" +
                "   x##\n" +
                "     +\n" +
                "# ####\n");

        // но стоит забарикадироваться
        field.walls().add(new Wall(5, 4));
        field.tick();

        // как привидения нормальнеют
        asrtBrd("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  &&#\n" +
                "   &##\n" +
                "     +\n" +
                "# ####\n");

        // и после выпиливаются
        field.tick();

        asrtBrd("#+##☼☺\n" +
                "# # ☼☼\n" +
                "#  ++#\n" +
                "   +##\n" +
                "     +\n" +
                "# ####\n");

        events.verifyAllEvents("[]");
    }

    // если мы вызвали потустороннюю нечисть, то наш суицид ее успокоит, отправив обратно
    @Test
    public void shouldDropPerk_generateNewGhost_thenSuicide_willKillChopperAlso() {
        shouldHeroAcquirePerk_whenMoveToFieldWithPerk();
        reset(listener);

        hero.right();
        field.tick();

        // then
        asrtBrd("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                " ☺   #\n" +
                "#+####\n");

        hero.act();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        hero.up();
        field.tick();

        // перед взрывом
        asrtBrd("######\n" +
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
        asrtBrd("#H####\n" +
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

        asrtBrd("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #☺##\n" +
                "  x  +\n" +
                "#  ###\n");

        // мувнули героя и кикнули его
        hero.die();
        boxesCount(boxesCount() - 1); // одна коробка потречена злым привидением
        field.tick();

        asrtBrd("#+####\n" +
                "# # ##\n" +
                "#    #\n" +
                "# #Ѡ##\n" +
                "  &  +\n" +
                "#  ###\n");

        events.verifyAllEvents("[DIED]");

    }

    // проверяем, что перк пропадает после таймаута
    @Test
    public void shouldPerkBeDeactivated_whenTimeout() {
        // given
        int timeout = 3; // время работы перка

        int value = 4;   // показатель его влияния, в тесте не интересно
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, value, timeout);
        perks.dropRatio(20);

        player.getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));
        assertEquals("Hero had to acquire new perk",
                1, player.getHero().getPerks().size());

        // when
        field.tick();
        field.tick();
        field.tick();

        // then
        assertEquals("Hero had to lose perk",
                0, player.getHero().getPerks().size());
    }

    // Проверяем длинну волны взрывной в отсутствии перка BBRI
    @Test
    public void shouldPotionBlastRadiusIncrease_whenNoBBRIperk() {
        // given
        givenBoardWithBoxes(12);

        hero.act();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("############\n" +
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
    }

    // Проверяем что перк BBRI увеличивает длинну взрывной волны зелья
    @Test
    public void shouldPotionBlastRadiusIncrease_whenBBRIperk() {
        // given
        givenBoardWithBoxes(12);

        int value = 4;   // на сколько клеток разрывная волна увеличится (по умолчанию 1)
        int timeout = 5; // сколько это безобразие будет длиться

        player.getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));

        hero.act();
        field.tick();

        field.tick();
        field.tick();
        field.tick();

        // when
        field.tick();

        // then
        asrtBrd("############\n" +
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
    }

    // Проверяем что два перка BBRI увеличивают длинну взрывной волны зелья на размер второго перка
    // При этом общее время суммируется. Но так же важно, что перк влияет только на будущие зелья,
    // а не те, которые уже на поле. И после того как он отработает, все вернется как было.
    @Test
    public void shouldPotionBlastRadiusIncreaseTwice_whenBBRIperk() {
        // given
        givenBoardWithBoxes(12);

        int value = 4;   // на сколько клеток разрывная волна увеличится (по умолчанию 1)
        int timeout = 5; // сколько это безобразие будет длиться (времени должно хватить)

        player.getHero().addPerk(new PotionBlastRadiusIncrease(value, timeout));

        assertEquals("[{POTION_BLAST_RADIUS_INCREASE('+') " +
                        "value=4, timeout=5, timer=5, pick=0}]" ,
                hero.getPerks().toString());

        hero.act();
        hero.up();
        field.tick();

        hero.up();
        field.tick();

        hero.right();
        field.tick();

        hero.right();
        field.tick();

        assertEquals("[{POTION_BLAST_RADIUS_INCREASE('+') " +
                        "value=4, timeout=5, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // второй перк взятый в самый последний момент перед взрывом
        // зелья повлияет не на нее, а на следующее зелье
        int newValue = 3; // проверим, что эти значения суммируются
        int newTimeout = 7;
        player.getHero().addPerk(new PotionBlastRadiusIncrease(newValue, newTimeout));

        assertEquals("[{POTION_BLAST_RADIUS_INCREASE('+') " +
                        "value=7, timeout=8, timer=8, pick=0}]" ,
                hero.getPerks().toString());

        // when
        field.tick();

        // then
        asrtBrd("############\n" +
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


        // when
        hero.act();
        hero.right();

        dice(dice,  // новые коробки
                9, 1,
                9, 2);
        field.tick();

        hero.right();
        field.tick();

        hero.up();
        field.tick();

        hero.up();
        field.tick();

        field.tick();

        // then
        asrtBrd("###H########\n" +
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

        assertEquals("[{POTION_BLAST_RADIUS_INCREASE('+') " +
                        "value=7, timeout=8, timer=2, pick=0}]" ,
                hero.getPerks().toString());

        // when
        dice(dice,  // новые коробки
                9, 10,
                9, 9,
                9, 8,
                9, 7);
        field.tick();

        // последний шанс воспользоваться, но мы не будем
        assertEquals("[{POTION_BLAST_RADIUS_INCREASE('+') " +
                        "value=7, timeout=8, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        field.tick();

        assertEquals("[]" ,
                hero.getPerks().toString());

        // ставим новое зелье, чтобы убедиться, что больше перк не сработает
        hero.act();
        field.tick();

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        // then
        asrtBrd("### ########\n" +
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

        assertEquals("[]" ,
                hero.getPerks().toString());
    }

    // BCI - Potion Count Increase perk
    @Test
    public void shouldPotionCountIncrease_whenBCIPerk() {
        hero.act();
        // obe potion by default on lel 1
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        hero.right();
        field.tick();
        hero.act();
        // no more potions :(
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");

        // add perk that gives 1+3 = 4 player's potions in total on the board
        player.getHero().addPerk(new PotionCountIncrease(3, 3));
        hero.act();
        hero.right();
        field.tick();
        hero.act();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "34☻  \n");

        hero.right();
        field.tick();
        hero.act();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "234☻ \n");

        hero.right();
        field.tick();
        hero.act();
        // 4 potions and no more
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1234☺\n");

    }

    // BI - Potion Immune perk
    @Test
    public void shouldHeroKeepAlive_whenBIperk() {
        hero.act();
        hero.right();
        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");

        player.getHero().addPerk(new PotionImmune(6));

        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉☺   \n");

        hero.act();
        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
//                " 3☺  \n");
                " ☻   \n");

        field.tick();
        field.tick();
        field.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n");
    }

    // BRC - Potion remote control perk
    @Test
    public void shouldPotionBlastOnAction_whenBRCperk_caseTwoPotions() {

        canDropPotions(2);
        player.getHero().addPerk(new PotionRemoteControl(2, 1));

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // поставили первое радиоуправляемое зелье
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "5☺   \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // видим, что она стоит и ждет
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // взорвали ее
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉☺   \n" +
                "҉҉   \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // ставим еще одну
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // отошли, смотрим
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                " ☺   \n" +
                " 5   \n" +
                "     \n");

        hero.left();
        field.tick();

        // долго потикали, ничего не меняется, таймаутов нет
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                " 5   \n" +
                "     \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // взорвали ее
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n" +
                " ҉   \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // ставим новую
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☻    \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // если отойдем, то увидим, что это обычная
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "3☺   \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // еще одну, у нас ведь их две
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "24☺  \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // больше не могу
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "13 ☺ \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // еще не могу
        hero.right();
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "҉    \n" +
                "҉2  ☺\n" +  // взрывная волна кстати не перекрывает зелье
                "҉    \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // и только когда ударная волна уйдет, тогда смогу
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                " 1  ☻\n" +  // взрывная волна кстати не перекрывает зелье
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());
    }

    @Test
    public void shouldPotionBlastOnAction_whenBRCperk_caseOnePotion() {

        canDropPotions(1);
        player.getHero().addPerk(new PotionRemoteControl(2, 1));

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // поставили первое радиоуправляемое зелье
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "5☺   \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // видим, что она стоит и ждет
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "5    \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=2, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // взорвали ее
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉☺   \n" +
                "҉҉   \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // ставим еще одну
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // отошли, смотрим
        hero.up();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                " ☺   \n" +
                " 5   \n" +
                "     \n");

        hero.left();
        field.tick();

        // долго потикали, ничего не меняется, таймаутов нет
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺    \n" +
                " 5   \n" +
                "     \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]" ,
                hero.getPerks().toString());

        // взорвали ее
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n" +
                " ҉   \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // ставим новую
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☻    \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // если отойдем, то увидим, что это обычная
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "3☺   \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // больше не могу - у меня одна
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "2 ☺  \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // больше не могу
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "1  ☺ \n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // и теперь не могу - есть еще взрывная волна
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "҉    \n" +
                "҉҉  ☺\n" +
                "҉    \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());

        // а теперь пожалуйста
        hero.act();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "    ☻\n" +
                "     \n" +
                "     \n");

        assertEquals("[]" ,
                hero.getPerks().toString());
    }

    @Test
    public void shouldSuicide_whenBRCperk_shouldRemoveAfterDeath_andCollectScores() {
        boxesCount(1);
        boxAt(0, 1);

        ghostAt(3, 0);

        canDropPotions(1);
        potionsPower(3);
        player.getHero().addPerk(new PotionRemoteControl(1, 1));

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]",
                hero.getPerks().toString());

        // поставили радиоуправляемое зелье
        hero.act();
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "#    \n" +
                "5☺ & \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]",
                hero.getPerks().toString());

        // идем к привидению на верную смерть
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "#    \n" +
                "5 ☺& \n");

        assertEquals("[{POTION_REMOTE_CONTROL('r') " +
                        "value=1, timeout=1, timer=1, pick=0}]",
                hero.getPerks().toString());

        // самоубился и всех выпилил )
        hero.right();
        field.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "H    \n" +
                "҉҉҉Ѡ \n");

        events.verifyAllEvents("[DIED, KILL_GHOST, KILL_TREASURE_BOX]");

        // только сейчас перк забрался
        assertEquals("[]",
                hero.getPerks().toString());

        dice(dice, 4, 4); // новая коробка
        field.tick();

        asrtBrd("    #\n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "   Ѡ \n");

        events.verifyAllEvents("[]");

        assertEquals("[]",
                hero.getPerks().toString());
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
