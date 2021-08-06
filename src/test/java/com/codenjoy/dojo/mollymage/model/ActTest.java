package com.codenjoy.dojo.mollymage.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActTest {
    private Act act;

    @Test
    public void shouldTrue_whenACT_WithoutArguments() {
        act = new Act(null);
        assertEquals(true, act.act());
        act = new Act(new int[0]);
        assertEquals(true, act.act());
    }

    @Test
    public void shouldFalse_whenACT_WithArguments() {
        act = new Act(new int[]{1});
        assertEquals(false, act.act());
    }

    @Test
    public void shouldCorrectlyDetectArguments_WithArguments() {
        act = new Act(new int[]{1});
        assertEquals(true, act.act(1));
        assertEquals(false, act.act(2));
    }
}