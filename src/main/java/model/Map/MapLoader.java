package model.Map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public interface MapLoader {


    String MAP_DIR = "./src/main/resources/maps";


    class Coordinate {
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
    BufferedImage getBackground();


}
