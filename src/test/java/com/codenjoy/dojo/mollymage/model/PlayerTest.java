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


import com.codenjoy.dojo.mollymage.TestGameSettings;
import com.codenjoy.dojo.mollymage.services.Event;
import com.codenjoy.dojo.mollymage.services.GameSettings;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.field.Accessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.mockito.Mockito.*;

public class PlayerTest {

    private EventListener listener;
    private Field field;
    private GameSettings settings;
    private Dice dice;

    @Before
    public void setup() {
        settings = spy(new TestGameSettings());
        dice = mock(Dice.class);

        field = mock(Field.class);
        when(field.settings()).thenReturn(settings);
        when(field.freeRandom(any())).thenReturn(Optional.of(pt(0, 0)));
        when(field.heroes()).thenReturn(mock(Accessor.class));
        when(field.heroes()).thenReturn(mock(Accessor.class));

        listener = mock(EventListener.class);
    }

    protected void dice(int... values) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int value : values) {
            when = when.thenReturn(value);
        }
    }

    @Test
    public void shouldProcessEventWhenListenerIsNotNull() {
        // given
        Player player = new Player(listener, settings);
        dice(0, 0);
        player.newHero(field);

        // when
        player.event(Event.KILL_TREASURE_BOX);

        // then
        verify(listener).event(Event.KILL_TREASURE_BOX);
    }

    @Test
    public void shouldNotProcessEventWhenListenerNotNull() {
        // given
        Player player = new Player(null, settings);
        dice(0, 0);
        player.newHero(field);

        // when
        player.event(Event.KILL_TREASURE_BOX);

        // then
        verify(listener, never()).event(Event.KILL_TREASURE_BOX);
    }
}
