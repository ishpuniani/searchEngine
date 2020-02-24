package parser;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import searcher.TrecResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileParser {

    private static Logger logger = Logger.getGlobal();

    private static final Path RESOURCE_DIR = Paths.get("src/main/resources/");
    public static final Path DOCS_DIR = RESOURCE_DIR.resolve("cran/");
    public static final Path INDEX_DIR = RESOURCE_DIR.resolve("index/");
    public static final Path RESULTS_DIR = RESOURCE_DIR.resolve("results/");

    private static final Path DOCS_FILE = DOCS_DIR.resolve("cran.all.1400");
    private static final Path QUERY_FILE = DOCS_DIR.resolve("cran.qry");
//    public static final Path BASELINE_FILE = DOCS_DIR.resolve("cranqrel");
    public static final Path BASELINE_FILE = DOCS_DIR.resolve("QRelsCorrectedforTRECeval");


    public static void initialize() {
        createDirectory(INDEX_DIR);
        createDirectory(DOCS_DIR);
    }

    public static void createDirectory(Path path) {
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

    public static List<DocumentModel> readDocuments() {
        List<DocumentModel> models = new ArrayList<>();
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
                models.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read docs file failed");
            System.exit(1);
        }
        return models;
    }

    public static List<Document> getLuceneDocuments() {
        List<Document> documents = new ArrayList<>();
        List<DocumentModel> documentModels = readDocuments();
        documentModels.forEach(d -> documents.add(modelToLuceneDoc(d)));
//        ArrayList<Document> documents = new ArrayList<Document>(documentModels.stream().map(d -> modelToLuceneDoc(d)).collect(Collectors.toList()));
        return documents;
    }

    public static Document modelToLuceneDoc(DocumentModel document){
        Document luceneDoc = new Document();

        StringField id = new StringField(DocumentModel.ID, Integer.toString(document.getId()), Field.Store.YES);
        TextField title = new TextField(DocumentModel.TITLE, document.getTitle(), Field.Store.YES);
        TextField author = new TextField(DocumentModel.AUTHOR, document.getAuthor(), Field.Store.YES);
        TextField source = new TextField(DocumentModel.SOURCE, document.getSource(), Field.Store.YES);
        TextField content = new TextField(DocumentModel.CONTENT, document.getContent(), Field.Store.YES);

        luceneDoc.add(id);
        luceneDoc.add(title);
        luceneDoc.add(author);
        luceneDoc.add(source);
        luceneDoc.add(content);

        return luceneDoc;
    }

    public static List<String> readQueries() {
        List<String> queries = new ArrayList<>();
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

    public static List<List<String>> readBaselines() {
        List<List<String>> baselines = new ArrayList<>(new HashSet<>());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BASELINE_FILE.toFile()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split("\\s+");
                int queryId = Integer.parseInt(items[0]);
                int documentId = Integer.parseInt(items[1]);
                int relevance = Integer.parseInt(items[2]);

                List<String> row = new ArrayList<>();
                row.add(Integer.toString(queryId));
                row.add("0");
                row.add(Integer.toString(documentId));
                row.add(Integer.toString(relevance));
                baselines.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read baseline file failed");
            System.exit(1);
        }

        return baselines;
    }

    /**
     * Write trec eval results to file and plot precision-recall graph
     * @param inputStream
     * @param outputFile
     * @param append
     * @throws IOException
     */
    public static void writeStdoutToFile(InputStream inputStream, Path outputFile, boolean append, Path imagePath) throws IOException {
        XYSeries prSeries = new XYSeries("PR");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.toFile(), append));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.contains("iprec_at_recall")) {
                String prLine = line;
                String[] prArr = prLine.split("\t");
                double x = Double.parseDouble(prArr[0].trim().split("_")[3]);
                double y = Double.parseDouble(prArr[2].trim());
                prSeries.add(x, y);
            }
            writer.write(line + "\n");
        }
        writer.flush();
        writer.close();

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries(prSeries);

        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                "P/R Graph",
                "Recall",
                "Precision",
                dataset,
                PlotOrientation.VERTICAL,
                false, false, false);

        int width = 640;
        int height = 480;
        ChartUtilities.saveChartAsJPEG(imagePath.toFile(), xylineChart, width, height);
    }

    public static void writeTrecToFile(List<TrecResult> trecResults, Path resultsFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile.toFile()));

        for (TrecResult res : trecResults) {
            String str = String.format("%d\tITER\t%d\tRANK\t%f\tRUN\n", res.getQid(), res.getDid(), res.getScore());
            writer.write(str);
        }
        writer.flush();
        writer.close();
    }
}