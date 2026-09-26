package api;

import project.annotations.ProcessAPIPrototype;

import java.util.List;

public class DataStoragePrototype {

    @ProcessAPIPrototype
    public void prototypeDataStorage(DataStorageAPI api) {
        List<Integer> mockData = api.readInputData("input.txt");
    }
}