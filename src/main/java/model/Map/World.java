package model.Map;

import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


public class World {


    public static final String MAP_DIR = "./src/main/resources/maps";
    private final ArrayList<Continent> continents;
    private final ArrayList<Territory> territories;
    private final ArrayList<Coordinate> coordinates;
    private BufferedImage background;


    public static class Coordinate {
        private final double x;
        private final double y;

        Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }


    public World(File map) {
        this.continents  = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.coordinates = new ArrayList<>();
        try {
            InputStream         stream  = new FileInputStream(map);
            java.util.Map<String, Object> mapData = (new Yaml()).load(stream);
            this.parse(mapData);
            this.parseAdjacent(mapData);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.loadBackground(map);
        MapManager.getInstance().setTerritories(this.territories);
    }


    private void loadBackground(File mapFile) {
        String filename = mapFile.getPath().replace(".yaml", ".jpg");
        File file = new File(filename);
        try {
            this.background = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private void parse(Map<String, Object> data) {
        if (!(data.get("continents") instanceof List)) return;
        for (Map<String, Object> continentData : (List<Map<String, Object>>) data.get("continents")) {
            Continent continent = parseContinent(continentData);
            if (continent == null) continue;
            continents.add(continent);
            if (!(continentData.get("territories") instanceof List)) return;
            for (Map<String, Object> territoryData : (List<Map<String, Object>>) continentData.get("territories")) {
                Territory territory   = parseTerritory(territoryData, continent);
                Coordinate coordinate = parseCoordinate(territoryData);
                if (territory == null) continue;
                if (coordinate == null) continue;
                territories.add(territory);
                coordinates.add(coordinate);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private Continent parseContinent(Map<String, Object> continent) {
        if (!(continent.get("name") instanceof String)) return null;
        if (!(continent.get("bonus") instanceof Integer)) return null;
        if (!(continent.get("territories") instanceof List)) return null;
        String name = (String) continent.get("name");
        Integer bonus = (Integer) continent.get("bonus");
        List<Object> territories = (List<Object>) continent.get("territories");
        return new Continent(name, territories.size(), bonus);
    }


    private Territory parseTerritory(Map<String, Object> territory, Continent continent) {
        if (!(territory.get("name") instanceof String)) return null;
        String name = (String) territory.get("name");
        return new Territory(name, continent);
    }


    @SuppressWarnings("unchecked")
    private Coordinate parseCoordinate(Map<String, Object> territory) {
        if (!(territory.get("coordinates") instanceof List)) return null;
        ArrayList<Double> coordinates = (ArrayList<Double>) territory.get("coordinates");
        if (coordinates.size() != 2) return null;
        return new Coordinate(coordinates.get(0), coordinates.get(1));
    }


    @SuppressWarnings("unchecked")
    private void parseAdjacent(Map<String, Object> data) {
        if (!(data.get("continents") instanceof List)) return;
        for (Map<String, Object> continentData : (List<Map<String, Object>>) data.get("continents")) {
            if (!(continentData.get("territories") instanceof List)) return;
            for (Map<String, Object> territoryData : (List<Map<String, Object>>) continentData.get("territories")) {
                if (!(territoryData.get("name") instanceof String)) continue;
                if (!(territoryData.get("neighbors") instanceof List)) continue;
                String name = (String) territoryData.get("name");
                List<String> neighbors = (List<String>) territoryData.get("neighbors");
                Territory territory = getTerritoryByName(name);
                for (String neighborName : neighbors) {
                    Territory neighborTerritory = getTerritoryByName(neighborName);
                    territory.addAdjacentTerritory(neighborTerritory);
                }
            }
        }
    }


    private Territory getTerritoryByName(String name) {
        return (Territory) this.territories.stream()
                .filter((Territory territory) ->
                        Objects.equals(territory.getName(), name))
                .toArray()[0];
    }


    public List<Continent> getContinents() {
        return this.continents;
    }


    public List<Territory> getTerritories() {
        return this.territories;
    }


    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }


    public BufferedImage getBackground() {
        return this.background;
    }


    public int getWidth() {
        return this.background.getWidth();
    }


    public int getHeight() {
        return this.background.getHeight();
    }


    public static Map<String, File> getMapFiles() {
        Map<String, File> maps = new HashMap<>();
        Arrays.stream(Objects.requireNonNull((new File(MAP_DIR)).listFiles()))
                .filter((File file) -> !file.toPath().endsWith(".yaml"))
                .forEach((File file) -> {
                    String mapName = file.getName().split("\\.")[0];
                    maps.put(mapName, file);
                });
        return maps;
    }


}