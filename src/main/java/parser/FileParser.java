package parser;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileParser {

    private static Logger logger = Logger.getGlobal();

    private static final Path RESOURCE_DIR = Paths.get("src/main/resources/");
    private static final Path DOCS_DIR = RESOURCE_DIR.resolve("cran/");
    static final Path INDEX_DIR = RESOURCE_DIR.resolve("index/");

    static final Path DOCS_FILE = DOCS_DIR.resolve("cran.all.1400");
    static final Path QUERY_FILE = DOCS_DIR.resolve("cran.qry");
    static final Path BASELINE_FILE = DOCS_DIR.resolve("cranqrel");


    public static void initialize() {
        createDirectory(INDEX_DIR);
        createDirectory(DOCS_DIR);
    }

    private static void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @FunctionalInterface
    interface DocumentProcessor {
        void process(DocumentModel model);
    }

    public static ArrayList<DocumentModel> readDocuments() {
        ArrayList<DocumentModel> models = new ArrayList<>();
        try {
            String text = String.join(" ", Files.readAllLines(DOCS_FILE));
            String lines[] = text.split("\\.I\\s*");
            ArrayList<String> docs = new ArrayList<>(Arrays.asList(lines));
            docs.remove(0);
            for (String doc: docs) {
                String splits[] = doc.split("\\s*\\.[TABW]\\s*");
                String items[] = new String[5];
                Arrays.fill(items, "");
                System.arraycopy(splits, 0, items, 0, Math.min(splits.length, 5));
                DocumentModel model = new DocumentModel(Integer.parseInt(items[0]), items[1], items[2], items[3], items[4]);
                //processor.process(model);
                models.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read docs file failed");
            System.exit(1);
        }
        return models;
    }

    public static ArrayList<Document> getLuceneDocuments() {
        ArrayList<Document> documents = new ArrayList<>();
        ArrayList<DocumentModel> documentModels = readDocuments();
        documentModels.forEach(d -> documents.add(modelToLuceneDoc(d)));
//        ArrayList<Document> documents = new ArrayList<Document>(documentModels.stream().map(d -> modelToLuceneDoc(d)).collect(Collectors.toList()));
        return documents;
    }

    public static Document modelToLuceneDoc(DocumentModel document){
        Document luceneDoc = new Document();

        StringField id = new StringField(DocumentModel.ID, Integer.toString(document.getId()), Field.Store.YES);
        TextField title = new TextField(DocumentModel.TITLE, document.getTitle(), Field.Store.NO);
        TextField author = new TextField(DocumentModel.AUTHOR, document.getAuthor(), Field.Store.NO);
        TextField source = new TextField(DocumentModel.SOURCE, document.getSource(), Field.Store.NO);
        TextField content = new TextField(DocumentModel.CONTENT, document.getContent(), Field.Store.NO);

        luceneDoc.add(id);
        luceneDoc.add(title);
        luceneDoc.add(author);
        luceneDoc.add(source);
        luceneDoc.add(content);

        return luceneDoc;
    }

    public static ArrayList<String> readQueries() {
        ArrayList<String> queries = new ArrayList<>();
        try {
            String text = String.join(" ", Files.readAllLines(QUERY_FILE));
            text = text.replace("?", "");
            String lines[] = text.split("\\.I.*?.W");
            Collections.addAll(queries, lines);
            queries.remove(0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read queries file failed");
            System.exit(1);
        }

        return queries;
    }

    public static ArrayList<Set> readBaselines() {
        ArrayList<Set> baselines = new ArrayList<>(new HashSet<>());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BASELINE_FILE.toFile()));
            String line = null;
            int oldQueryId = 1;
            HashSet<Integer> set = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                String[] items = line.split("\\s+");
                int queryId = Integer.parseInt(items[0]);
                int documentId = Integer.parseInt(items[1]);
                int relevance = Integer.parseInt(items[2]);

                // New queryId starts or not
                if (queryId != oldQueryId) {
                    oldQueryId = queryId;
                    baselines.add(set);
                    set = new HashSet<>();
                }
                if (relevance <= 3) {
                    set.add(documentId);
                }
            }
            // last queryId
            baselines.add(set);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read baseline file failed");
            System.exit(1);
        }

        return baselines;
    }
}