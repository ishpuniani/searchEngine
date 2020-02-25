package indexer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.util.Arrays;

public class CustomAnalyzer extends Analyzer {

    public static CharArraySet stopWords = new CharArraySet(
            Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"),
            true);

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
//        Tokenizer source = new NGramTokenizer(2, 5);
//        Tokenizer source = new StandardTokenizer();
        Tokenizer source = new ClassicTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(source);
        tokenStream = new StopFilter(tokenStream, stopWords);
        tokenStream = new EnglishPossessiveFilter(tokenStream); // removes trailing 's'
//        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);
//        CharArraySet stopSet = CharArraySet.copy(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        return new TokenStreamComponents(source, tokenStream);
    }
}