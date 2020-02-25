package indexer;

import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.opennlp.OpenNLPPOSFilter;
import org.apache.lucene.analysis.opennlp.OpenNLPTokenizer;
import org.apache.lucene.analysis.opennlp.tools.NLPPOSTaggerOp;
import org.apache.lucene.analysis.opennlp.tools.NLPSentenceDetectorOp;
import org.apache.lucene.analysis.opennlp.tools.NLPTokenizerOp;
import org.apache.lucene.analysis.opennlp.tools.OpenNLPOpsFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenNLPAnalyzer extends Analyzer {

    private static Logger logger = Logger.getGlobal();

    protected TokenStreamComponents createComponents(String fieldName) {

        try {
            ResourceLoader resourceLoader = new ClasspathResourceLoader(ClassLoader.getSystemClassLoader());

            TokenizerModel tokenizerModel = OpenNLPOpsFactory.getTokenizerModel("openNlpModels/en-token.bin", resourceLoader);
            NLPTokenizerOp tokenizerOp = new NLPTokenizerOp(tokenizerModel);

            SentenceModel sentenceModel = OpenNLPOpsFactory.getSentenceModel("openNlpModels/en-sent.bin", resourceLoader);
            NLPSentenceDetectorOp sentenceDetectorOp = new NLPSentenceDetectorOp(sentenceModel);

            Tokenizer source = new OpenNLPTokenizer(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, sentenceDetectorOp, tokenizerOp);

            POSModel posModel = OpenNLPOpsFactory.getPOSTaggerModel("openNlpModels/en-pos-maxent.bin", resourceLoader);
            NLPPOSTaggerOp posTaggerOp = new NLPPOSTaggerOp(posModel);

            TokenStream tokenStream = new LowerCaseFilter(source);
            tokenStream = new StopFilter(tokenStream, CustomAnalyzer.stopWords);
            tokenStream = new EnglishPossessiveFilter(tokenStream); // removes trailing 's'
            tokenStream = new EnglishMinimalStemFilter(tokenStream);
            tokenStream = new OpenNLPPOSFilter(tokenStream, posTaggerOp);

            return new TokenStreamComponents(source, tokenStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Failed to create OpenNLPAnalyzer components");
            System.exit(1);
        }
        return null;
    }
}