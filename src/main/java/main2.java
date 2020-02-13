import parser.FileParser;
import searcher.TrecResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class main2 {
    public static void main(String[] args) throws IOException {
        List<List<String>> baselines = FileParser.readBaselines();
        System.out.println("here");

        writeToFile(baselines, FileParser.DOCS_DIR.resolve("cranqrel3"));
    }

    public static void writeToFile(List<List<String>> rows, Path resultsFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile.toFile()));

        for (List<String> row : rows) {
            String str = String.join(" ", row) + "\n";
            writer.write(str);
        }
        writer.flush();
        writer.close();
    }
}
