package com.hotel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public final class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static Path saveCsv(Window owner, String defaultFileName, List<String> headers, List<List<String>> rows)
        throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV");
        fileChooser.setInitialFileName(defaultFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        java.io.File file = fileChooser.showSaveDialog(owner);
        if (file == null) {
            return null;
        }

        StringBuilder csvContent = new StringBuilder();
        csvContent.append(toCsvRow(headers)).append(System.lineSeparator());
        for (List<String> row : rows) {
            csvContent.append(toCsvRow(row)).append(System.lineSeparator());
        }

        Path outputPath = file.toPath();
        Files.writeString(outputPath, csvContent.toString(), StandardCharsets.UTF_8);
        return outputPath;
    }

    private static String toCsvRow(List<String> values) {
        return values.stream().map(CsvExportUtil::escapeCsv).reduce((a, b) -> a + "," + b).orElse("");
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
