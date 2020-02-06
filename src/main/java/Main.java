import org.apache.lucene.document.Document;
import parser.FileParser;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getGlobal();

    public static void main(String[] args) {
        FileParser.initialize();

        ArrayList<Set> baselines = FileParser.readBaselines();
        ArrayList<String> queries = FileParser.readQueries();
        ArrayList<Document> documents = FileParser.getLuceneDocuments();
//        System.out.println("here");
        logger.log(Level.INFO, "here");
    }
}
