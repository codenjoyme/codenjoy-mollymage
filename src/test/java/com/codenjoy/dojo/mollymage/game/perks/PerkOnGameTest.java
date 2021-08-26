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
import com.codenjoy.dojo.mollymage.model.items.perks.PotionBlastRadiusIncrease;
import org.junit.Test;

import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.CATCH_PERK_SCORE;
import static com.codenjoy.dojo.mollymage.services.GameSettings.Keys.KILL_WALL_SCORE;
import static com.codenjoy.dojo.services.PointImpl.pt;
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
        givenBr("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(50);

        dice(dice, 10); // must drop 2 perks

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
        asrtBrd("######\n" +
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
        givenBr("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");
        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 5, 3);
        perks.dropRatio(20); // 20%
        dice(dice, 10, 30); // must drop 1 perk

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

    // проверяем, что перк удалится с поля через N тиков если его никто не возьмет
    @Test
    public void shouldRemovePerk_whenPickTimeout() {
        // given
        givenBr("######\n" +
                "# # ##\n" +
                "#    #\n" +
                "# # ##\n" +
                "#☺   #\n" +
                "######\n");

        perks.put(Element.POTION_BLAST_RADIUS_INCREASE, 4, 3);
        perks.dropRatio(20); // 20%
        perks.pickTimeout(5);

        dice(dice, 10); // must drop 2 perks

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
        reset(listener());

        hero().right();
        field.tick();

        // then
        asrtBrd("######\n" +
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
        hero().move(3, 4);
        boxAt(1, 2); // две коробки подорвали, две добавили
        boxAt(1, 3);
        field.walls().add(new Wall(1, 4));

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
        reset(listener());

        hero().right();
        field.tick();

        // then
        asrtBrd("######\n" +
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
        reset(listener());

        hero().right();
        field.tick();

        // then
        asrtBrd("######\n" +
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
        hero().die();
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
        givenBr("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");;
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
}
