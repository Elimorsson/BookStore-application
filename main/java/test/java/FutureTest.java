package test.java;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future <Integer> object;

    @Before
    public void setUp() throws Exception {
        object = new Future<Integer>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        object.resolve(14);
        assertEquals(14, (long)object.get());
    }

    @Test
    public void resolve() {
        object.resolve(15);
        assertEquals(15,(long)object.get());
        assertTrue(object.isDone());
    }

    @Test
    public void isDone() {
        assertFalse(object.isDone());
        object.resolve(16);
        assertTrue(object.isDone());
    }

    @Test
    public void get1() {
        assertNull(object.get(200, TimeUnit.MILLISECONDS));
        object.resolve(17);
        assertEquals(17,(long)object.get(200,TimeUnit.MILLISECONDS));
    }
}