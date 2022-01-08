package com.codenjoy.dojo.mollymage.services;

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
import com.codenjoy.dojo.services.printer.CharElement;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.client.Utils.split;
import static org.junit.Assert.assertEquals;

public class SpritesTest {

    @Test
    public void shouldAllSpritesExists() {
        // given
        List<String> errors = new LinkedList<>();
        String game = "mollymage";
        CharElement[] elements = Element.values();

        // when then
        for (CharElement element : elements) {
            String path = "./src/main/webapp/resources/%s/sprite/%s.png";
            File file = new File(String.format(path, game, element.name().toLowerCase()));
            if (!file.exists()) {
                errors.add("Sprite not found: " + file.getAbsolutePath());
            }
        }

        // then
        assertEquals("[]", split(errors, ", \nSprite"));
    }
}