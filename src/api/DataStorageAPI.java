package api;

import project.annotations.ProcessAPI;

import java.util.List;

@ProcessAPI
public interface DataStorageAPI {

    List<Integer> readInputData(String inputSource);
}