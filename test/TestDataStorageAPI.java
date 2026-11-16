import api.DataStorageAPI;
import api.DataStorageAPIImpl;
import api.FileInputConfig;
import api.FileOutputConfig;
import api.InputConfig;
import api.WriteResult;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestDataStorageAPI {

    @Test
    public void testReadInputDataSmoke() throws IOException {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        Path tempFile = Files.createTempFile("test_input_smoke", ".txt");
        try {
            Files.writeString(tempFile, "10\n20\n30");
            InputConfig inputConfig = new FileInputConfig(tempFile.toString());

            List<Integer> result = dataStorage.readInputData(inputConfig);

            Assertions.assertNotNull(result);
            Assertions.assertFalse(result.isEmpty());
            Assertions.assertEquals(List.of(10, 20, 30), result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testReadInputDataWithVariousDelimiters() throws IOException {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        Path tempFile = Files.createTempFile("test_input_delimiters", ".txt");
        try {
            Files.writeString(tempFile, "10, 20; 30 \n 40");
            InputConfig inputConfig = new FileInputConfig(tempFile.toString());

            List<Integer> result = dataStorage.readInputData(inputConfig);

            Assertions.assertEquals(List.of(10, 20, 30, 40), result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testReadInputDataMissingFileReturnsEmpty() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        InputConfig inputConfig = new FileInputConfig("non_existent_file_path_12345.txt");

        List<Integer> result = dataStorage.readInputData(inputConfig);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testWriteOutputDataSuccess() throws IOException {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        Path tempFile = Files.createTempFile("test_output", ".txt");
        try {
            FileOutputConfig outputConfig = new FileOutputConfig(tempFile.toString());
            WriteResult writeResult = dataStorage.writeOutputData(outputConfig, List.of("4", "8", "25"));

            Assertions.assertEquals(WriteResult.Status.SUCCESS, writeResult.getStatus());
            String writtenContent = Files.readString(tempFile);
            Assertions.assertEquals("4,8,25", writtenContent);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testWriteOutputDataNullDestinationReturnsFailure() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        WriteResult writeResult = dataStorage.writeOutputData(null, List.of("1", "2"));

        Assertions.assertEquals(WriteResult.Status.FAILURE, writeResult.getStatus());
    }

    @Test
    public void testReadInputDataNullConfigReturnsEmpty() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        List<Integer> result = dataStorage.readInputData(null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testReadInputDataBlankPathReturnsEmpty() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        List<Integer> result = dataStorage.readInputData(() -> "   ");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testWriteOutputDataNullOutputDataReturnsFailure() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        FileOutputConfig outputConfig = new FileOutputConfig("out.txt");
        WriteResult writeResult = dataStorage.writeOutputData(outputConfig, null);

        Assertions.assertEquals(WriteResult.Status.FAILURE, writeResult.getStatus());
        Assertions.assertTrue(writeResult.getMessage().toLowerCase().contains("null"));
    }

    @Test
    public void testWriteOutputDataBlankPathReturnsFailure() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        WriteResult writeResult = dataStorage.writeOutputData(() -> "   ", List.of("1"));

        Assertions.assertEquals(WriteResult.Status.FAILURE, writeResult.getStatus());
    }

    @Test
    public void testFileInputConfigNullPathThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileInputConfig(null));
    }

    @Test
    public void testFileOutputConfigNullPathThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileOutputConfig(null));
    }

    @Test
    public void testReadInputDataInvalidPathReturnsEmptySentinel() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        List<Integer> result = Assertions.assertDoesNotThrow(() -> dataStorage.readInputData(() -> "\0invalid-path"));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testWriteOutputDataInvalidPathReturnsFailureStatus() {
        DataStorageAPI dataStorage = new DataStorageAPIImpl();
        WriteResult result = Assertions.assertDoesNotThrow(() -> dataStorage.writeOutputData(() -> "\0invalid-path", List.of("1", "2")));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(WriteResult.Status.FAILURE, result.getStatus());
    }
}

