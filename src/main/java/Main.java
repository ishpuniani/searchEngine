import indexer.Indexer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import parser.FileParser;
import searcher.Searcher;
import searcher.TrecResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final String BASELINE_KEY = "baselines";
    private static final String QUERY_KEY = "query";
    private static final String DOCUMENT_KEY = "document";

    private static Logger logger = Logger.getGlobal();

    /**
     * 1. Initialise analyser
     * 2. Read and index documents
     * 3. Initialise searcher(similarity)
     * 4. Generate result file
     * 5. Trec_eval on the results
     */
    public static void main(String[] args) throws IOException {
        FileParser.initialize();

        Map<String, List> documentsMap = readFiles();
        logger.log(Level.INFO, "StandardAnalyzer, BM25Similarity:: ");
        runModel(documentsMap, new StandardAnalyzer(), new BM25Similarity(),"results");

//        logger.log(Level.INFO, "StandardAnalyzer, ClassicSimilarity(Vector Space Model):: ");
//        runModel(documentsMap, new StandardAnalyzer(), new ClassicSimilarity());

        logger.log(Level.INFO, "Done");
    }

    private static Map<String, List> readFiles() {
        Map<String, List> docs = new HashMap<>();

        List<List<String>> baselines = FileParser.readBaselines();
        List<String> queries = FileParser.readQueries();
        List<Document> documents = FileParser.getLuceneDocuments();

        docs.put(BASELINE_KEY, baselines);
        docs.put(QUERY_KEY, queries);
        docs.put(DOCUMENT_KEY, documents);

        logger.log(Level.INFO, "Files read");
        return docs;
    }

    /**
     * @param analyzer
     * @param similarity
     * @param documents
     */
    private static void runModel(Map<String, List> documents, Analyzer analyzer, Similarity similarity, String resultFileName) throws IOException {
        logger.log(Level.INFO, "Running Model:- ");

        logger.log(Level.INFO, "Create index:- ");
        Indexer indexer = new Indexer(analyzer);
        indexer.createIndex(FileParser.INDEX_DIR, documents.get(DOCUMENT_KEY));
        logger.log(Level.INFO, "Created index.");

        Searcher searcher = new Searcher(analyzer, similarity);
        searcher.readIndex(FileParser.INDEX_DIR);

        logger.log(Level.INFO, "Read index.");

        //String query = "what is the basic mechanism of the transonic aileron buzz .";
        //List<TrecResult> trecResults = searcher.search(query , 10);

        Path resultsFile = FileParser.RESULTS_DIR.resolve(resultFileName + ".txt");
        Path trecOutputPath = FileParser.RESULTS_DIR.resolve(resultFileName + "_trec_out" + ".txt");

        List<TrecResult> trecResults = searcher.searchAll(documents.get(QUERY_KEY));
        FileParser.writeTrecToFile(trecResults, resultsFile);
        logger.log(Level.INFO, "Result Generated.");

        searcher.evaluateResults(resultsFile, trecOutputPath);
        logger.log(Level.INFO, "TREC Evaluated.");

        logger.log(Level.INFO, "Processed");
    }
}
