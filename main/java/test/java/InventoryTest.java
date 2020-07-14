package test.java;


import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory object = null;

    @Before
    public void setUp() throws Exception {
        Field instance = Inventory.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        assertNotNull(Inventory.getInstance());
        assertEquals(Inventory.getInstance(),object);
    }

    @Test
    public void load() {

        object.load(new BookInventoryInfo[]{new BookInventoryInfo("HarriPoter",1,15),new BookInventoryInfo("HarriPoter2",1,17)});

        assertEquals(15,object.checkAvailabilityAndGetPrice("HarriPoter"));
        assertEquals(17,object.checkAvailabilityAndGetPrice("HarriPoter2"));
        assertEquals(1,object.getAmount("HarriPoter"));
        assertEquals(1,object.getAmount("HarriPoter2"));
    }

    @Test
    public void take() {
        assertEquals("NOT_IN_STOCK",object.take("noob"));
        object.load(new BookInventoryInfo[]{new BookInventoryInfo("noob",1,17)});
        assertEquals("SUCCESSFULLY_TAKEN",object.take("noob"));
        assertEquals(0,object.getAmount("noob"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        assertEquals(-1,object.checkAvailabilityAndGetPrice("noob"));
        object.load(new BookInventoryInfo[]{new BookInventoryInfo("noob",1,17)});
        assertEquals(17,object.checkAvailabilityAndGetPrice("noob"));
    }

    @Test
    public void printInventoryToFile() {
    }
}