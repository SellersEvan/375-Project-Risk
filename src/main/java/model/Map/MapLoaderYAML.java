package model.Map;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapLoaderYAML implements MapLoader {


    private final ArrayList<Continent> continents;
    private final ArrayList<Territory> territories;
    private final ArrayList<Coordinate> coordinates;


    public MapLoaderYAML(File map) {
        this.continents  = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.coordinates = new ArrayList<>();
        try {
            InputStream         stream  = new FileInputStream(map);
            Map<String, Object> mapData = (new Yaml()).load(stream);
            this.parse(mapData);
            this.parseAdjacent(mapData);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MapManager.getInstance().setTerritories(this.territories);
    }


    @SuppressWarnings("unchecked")
    private void parse(Map<String, Object> data) {
        if (!(data.get("continents") instanceof List)) return;
        for (Map<String, Object> continentData : (List<Map<String, Object>>) data.get("continents")) {
            Continent continent = parseContinent(continentData);
            if (continent == null) continue;
            continents.add(continent);
            // FIXME Parse Coordinates
            if (!(continentData.get("territories") instanceof List)) return;
            for (Map<String, Object> territoryData : (List<Map<String, Object>>) continentData.get("territories")) {
                Territory territory = parseTerritory(territoryData, continent);
                if (territory == null) continue;
                territories.add(territory);
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


    @Override
    public List<Continent> getContinents() {
        return this.continents;
    }


    @Override
    public List<Territory> getTerritories() {
        return this.territories;
    }


    @Override
    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }


}
