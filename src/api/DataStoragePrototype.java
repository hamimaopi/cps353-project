package api;

import project.annotations.ProcessAPIPrototype;
import java.util.ArrayList;
import java.util.List;

public class DataStoragePrototype {

    @ProcessAPIPrototype
    public void prototypeDataStorage(DataStorageAPI api) {
        InputConfig inputSource = new FileInputConfig("input.txt");
        OutputConfig outputDestination = new FileOutputConfig("output.txt");

        List<Integer> mockInputData = api.readInputData(inputSource);

        List<String> outputData = new ArrayList<>();
        outputData.add("10:4");

        WriteResult writeResult = api.writeOutputData(outputDestination, outputData);
    }
}