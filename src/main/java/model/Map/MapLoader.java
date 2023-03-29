package model.Map;

import java.io.File;
import java.util.*;

public interface MapLoader {


    String MAP_DIR = "./src/main/resources/maps";


    class Coordinate {
        public int x;
        public int y;

        Coordinate (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    static Map<String, File> getMapFiles() {
        Map<String, File> maps = new HashMap<>();
        Arrays.stream(Objects.requireNonNull((new File(MAP_DIR)).listFiles()))
                .filter((File file) -> !file.toPath().endsWith(".yaml"))
                .forEach((File file) -> {
                    String mapName = file.getName().split("\\.")[0];
                    maps.put(mapName, file);
                });
        return maps;
    }


    List<Continent> getContinents();
    List<Territory> getTerritories();
    List<Coordinate> getCoordinates();
//    Image getBackground();


}
