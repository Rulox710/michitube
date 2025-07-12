package app.saver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class VTuberWriter {

    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();

    public void addSection(String sectionName) {
        sections.putIfAbsent(sectionName, new LinkedHashMap<>());
    }

    public void put(String sectionName, String key, Object value) {
        addSection(sectionName);
        String v = (value != null)? value.toString(): "null";
        sections.get(sectionName).put(key, v);
    }

    public void saveToFile(String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (var entry : sections.entrySet()) {
                writer.write("[" + entry.getKey() + "]");
                writer.newLine();
                for (var kv : entry.getValue().entrySet()) {
                    writer.write(kv.getKey() + "=" + kv.getValue());
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}
