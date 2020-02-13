package searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import parser.DocumentModel;
import parser.FileParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Searcher {

    private static final int NUM_TOP_HITS = 50;
    private static final String TREC_EVAL_PATH = "src/main/resources/trec_eval-9.0.7/trec_eval";
    private static Logger logger = Logger.getGlobal();

    private Analyzer analyzer;
    private Similarity similarity;
    private IndexSearcher indexSearcher;

    public Searcher(Analyzer analyzer, Similarity similarity) {
        this.analyzer = analyzer;
        this.similarity = similarity;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public void readIndex(Path indexPath) {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
            indexSearcher = new IndexSearcher(reader);
            indexSearcher.setSimilarity(similarity);

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Read index failed");
            System.exit(1);
        }
    }

    public List<TrecResult> search(String queryStr, int topHitsCount) {
        List<TrecResult> trecResults = new ArrayList<>();
        String fields[] = new String[] { DocumentModel.TITLE, DocumentModel.AUTHOR, DocumentModel.SOURCE, DocumentModel.CONTENT };
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

        try {
            Query query = parser.parse(queryStr);
            ScoreDoc[] hits = indexSearcher.search(query, topHitsCount).scoreDocs;

            for (ScoreDoc hit: hits) {
                Document doc = indexSearcher.doc(hit.doc);
                int docId = Integer.parseInt(doc.get(DocumentModel.ID));
//                logger.log(Level.INFO, "docId:: " + docId + " :: " + hit.doc);
                float score = hit.score;

                TrecResult res = new TrecResult();
                res.setDid(docId);
                res.setScore(score);
                trecResults.add(res);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Can't parse query");
            System.exit(1);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return trecResults;
    }

    public List<TrecResult> searchAll(List<String> queries) {
        List<TrecResult> results = new ArrayList<>();
        for (int i = 0; i < queries.size(); i++) {
            int qNum = i + 1;
            List<TrecResult> searchRes = search(queries.get(i), NUM_TOP_HITS);
            searchRes.forEach(t -> t.setQid(qNum));
            results.addAll(searchRes);
        }
        return results;
    }

    public void evaluateResults(Path resultsFile, Path trecOutputFile) throws IOException {
        String cmd = TREC_EVAL_PATH + " " + FileParser.BASELINE_FILE.toAbsolutePath().toString() + " " + resultsFile.toAbsolutePath().toString();
        logger.log(Level.INFO, "cmd:: " + cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream stdout = proc.getInputStream();
        FileParser.writeStdoutToFile(stdout, trecOutputFile, false);

        InputStream errOut = proc.getErrorStream();
        FileParser.writeStdoutToFile(errOut, trecOutputFile, true);
    }
}