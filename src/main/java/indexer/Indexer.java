package indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import parser.FileParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexer {

    private static Logger logger = Logger.getGlobal();

    private Analyzer analyzer = new StandardAnalyzer();

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Indexer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public void createIndex(Path indexPath, List<Document> documentList) {
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            Directory dir = FSDirectory.open(indexPath);
            IndexWriter writer = new IndexWriter(dir, config);
            writer.addDocuments(documentList);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Index failed: " + e.toString());
            System.exit(1);
        }
    }

}