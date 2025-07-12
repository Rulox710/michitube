package app.saver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class VTuberReader {

    private final Map<String, Map<String, String>> sections = new LinkedHashMap<>();

    public void loadFromFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            String currentSection = null;

            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    sections.putIfAbsent(currentSection, new LinkedHashMap<>());
                } else if (currentSection != null && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    sections.get(currentSection).put(parts[0].strip(), parts[1].strip());
                }
            }
        }
    }

    public String get(String section, String key) {
        return sections.getOrDefault(section, Collections.emptyMap()).get(key);
    }

    public int getInt(String section, String key) {
        return Integer.parseInt(get(section, key));
    }

    public boolean getBoolean(String section, String key) {
        return Boolean.parseBoolean(get(section, key));
    }

    public Set<String> getSections() {
        return sections.keySet();
    }

    public Map<String, String> getSection(String section) {
        return sections.getOrDefault(section, Collections.emptyMap());
    }
}
