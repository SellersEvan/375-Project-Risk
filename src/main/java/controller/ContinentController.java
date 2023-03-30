package controller;

import model.Map.Continent;

import java.util.List;

public class ContinentController {
    private final List<Continent> continents;

    public ContinentController(List<Continent> continents) {
        this.continents = continents;
    }

    public List<Continent> getContinents(){
        return this.continents;
    }
}
