package model.Map;

import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;


public class World {


    private final ArrayList<Continent> continents;
    private final ArrayList<Territory> territories;
    private BufferedImage background;


    public World(String world) {
        this.continents  = new ArrayList<>();
        this.territories = new ArrayList<>();
        try {
            InputStream stream  = this.getClass().getClassLoader()
                    .getResourceAsStream(world);
            if (stream == null) {
                throw new Error("Unable to find world config \"" + world + "\"");
            }
            java.util.Map<String, Object> mapData = (new Yaml()).load(stream);
            this.parse(mapData);
            this.parseAdjacent(mapData);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to Load World");
        }
        this.loadBackground(world);
        MapManager.getInstance().setTerritories(this.territories);
    }


    private void loadBackground(String mapFile) {
        String filename = "/" + mapFile.replace(".yaml", ".jpg");
        try {
            URL url = getClass().getResource(filename);
            if (url == null) {
                throw new Error("Unable to find world image \"" + filename + "\"");
            }
            this.background = ImageIO.read(url);
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
                if (territory == null) continue;
                territories.add(territory);
                continent.addTerritory(territory);
            }
        }
    }

    private Continent parseContinent(Map<String, Object> continent) {
        if (!(continent.get("name") instanceof String)) return null;
        if (!(continent.get("bonus") instanceof Integer)) return null;
        if (!(continent.get("territories") instanceof List)) return null;
        String name = (String) continent.get("name");
        Integer bonus = (Integer) continent.get("bonus");
        return new Continent(name, bonus);

    }


    @SuppressWarnings("unchecked")
    private Territory parseTerritory(Map<String, Object> territoryData, Continent continent) {
        if (!(territoryData.get("name") instanceof String)) return null;
        if (!(territoryData.get("coordinates") instanceof List)) return null;
        String name = (String) territoryData.get("name");
        ArrayList<Double> coordinates = (ArrayList<Double>) territoryData.get("coordinates");
        if (coordinates.size() != 2) return null;
        Territory territory = new Territory(name, continent);
        territory.setPosX(coordinates.get(0));
        territory.setPosY(coordinates.get(1));
        return territory;
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


    public BufferedImage getBackground() {
        return this.background;
    }


    public int getWidth() {
        return this.background.getWidth();
    }


    public int getHeight() {
        return this.background.getHeight();
    }


    public static Map<String, String> getMapFiles() {
        Map<String, String> maps = new HashMap<>();
        try {
            InputStream stream = World.class.getResourceAsStream("/maps/index");
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream), StandardCharsets.UTF_8);
            BufferedReader buffer = new BufferedReader(reader);
            Stream<String> lines = buffer.lines();
            lines.forEach((String name) -> {
                maps.put(name, "maps/" + name + ".yaml");
            });
            reader.close();
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maps;
    }


}
