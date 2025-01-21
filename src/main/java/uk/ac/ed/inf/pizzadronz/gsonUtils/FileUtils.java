package uk.ac.ed.inf.pizzadronz.gsonUtils;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static void saveToFile(String content, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }
}
