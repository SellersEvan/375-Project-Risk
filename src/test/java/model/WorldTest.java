package model;

import model.Map.World;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldTest {


    @Test
    void GetMaps() {
        assertTrue(World.getMapFiles().containsKey("Earth"));
    }


    @Test
    void LoadedBackground() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(1600, world.getWidth());
        assertEquals(900, world.getHeight());
    }


    @Test
    void LoadedContinentAustralia() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(6, world.getContinents().size());
        assertEquals("Australia", world.getContinents().get(5).name);
        assertEquals(2, world.getContinents().get(5).bonus);
        assertEquals(4, world.getContinents().get(5).territories);
    }


    @Test
    void LoadedContinentAfrica() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(6, world.getContinents().size());
        assertEquals("Africa", world.getContinents().get(2).name);
        assertEquals(3, world.getContinents().get(2).bonus);
        assertEquals(6, world.getContinents().get(2).territories);
    }


    @Test
    void LoadedTerritoryBrazil() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals("Brazil", world.getTerritories().get(12).getName());
        assertEquals("South America", world.getTerritories().get(12).getContinent().name);
    }


    @Test
    void LoadedTerritoryWAustralia() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals("W_Australia", world.getTerritories().get(39).getName());
        assertEquals("Australia", world.getTerritories().get(39).getContinent().name);
    }


    @Test
    void LoadedTerritoryScandinavia() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals("Scandinavia", world.getTerritories().get(24).getName());
        assertEquals("Europe", world.getTerritories().get(24).getContinent().name);
    }


    @Test
    void LoadedCoordinatesBrazil() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals(0.21875, world.getTerritories().get(12).getPosX());
        assertEquals(0.82222, world.getTerritories().get(12).getPosY());
    }


    @Test
    void LoadedCoordinatesWAustralia() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals(0.4625, world.getTerritories().get(39).getPosX());
        assertEquals(0.82222, world.getTerritories().get(12).getPosY());
    }


    @Test
    void LoadedCoordinatesScandinavia() {
        File file   = World.getMapFiles().get("Earth");
        World world = new World(file);
        assertEquals(42, world.getTerritories().size());
        assertEquals(0.78125, world.getTerritories().get(24).getPosX());
        assertEquals(0.12222, world.getTerritories().get(24).getPosY());
    }


}
