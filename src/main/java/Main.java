import indexer.CustomAnalyzer;
import indexer.Indexer;
import indexer.OpenNLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import parser.FileParser;
import searcher.Searcher;
import searcher.TrecResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final String BASELINE_KEY = "baselines";
    private static final String QUERY_KEY = "query";
    private static final String DOCUMENT_KEY = "document";

    private static CharArraySet stopWords = new CharArraySet(
            Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by",
                    "for", "if", "in", "into", "is", "it",
                    "no", "not", "of", "on", "or", "such",
                    "that", "the", "their", "then", "there", "these",
                    "they", "this", "to", "was", "will", "with"),
            true);


    private static CharArraySet stopWords2 = new CharArraySet(
            Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"),
            true);

    private static CharArraySet stopWords3 = new CharArraySet(
            Arrays.asList("a","about","above","after","again","against","ain","all","am","an","and","any","are","aren","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can","couldn","couldn't","d","did","didn","didn't","do","does","doesn","doesn't","doing","don","don't","down","during","each","few","for","from","further","had","hadn","hadn't","has","hasn","hasn't","have","haven","haven't","having","he","her","here","hers","herself","him","himself","his","how","i","if","in","into","is","isn","isn't","it","it's","its","itself","just","ll","m","ma","me","mightn","mightn't","more","most","mustn","mustn't","my","myself","needn","needn't","no","nor","not","now","o","of","off","on","once","only","or","other","our","ours","ourselves","out","over","own","re","s","same","shan","shan't","she","she's","should","should've","shouldn","shouldn't","so","some","such","t","than","that","that'll","the","their","theirs","them","themselves","then","there","these","they","this","those","through","to","too","under","until","up","ve","very","was","wasn","wasn't","we","were","weren","weren't","what","when","where","which","while","who","whom","why","will","with","won","won't","wouldn","wouldn't","y","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves","could","he'd","he'll","he's","here's","how's","i'd","i'll","i'm","i've","let's","ought","she'd","she'll","that's","there's","they'd","they'll","they're","they've","we'd","we'll","we're","we've","what's","when's","where's","who's","why's","would","able","abst","accordance","according","accordingly","across","act","actually","added","adj","affected","affecting","affects","afterwards","ah","almost","alone","along","already","also","although","always","among","amongst","announce","another","anybody","anyhow","anymore","anyone","anything","anyway","anyways","anywhere","apparently","approximately","arent","arise","around","aside","ask","asking","auth","available","away","awfully","b","back","became","become","becomes","becoming","beforehand","begin","beginning","beginnings","begins","behind","believe","beside","besides","beyond","biol","brief","briefly","c","ca","came","cannot","can't","cause","causes","certain","certainly","co","com","come","comes","contain","containing","contains","couldnt","date","different","done","downwards","due","e","ed","edu","effect","eg","eight","eighty","either","else","elsewhere","end","ending","enough","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","except","f","far","ff","fifth","first","five","fix","followed","following","follows","former","formerly","forth","found","four","furthermore","g","gave","get","gets","getting","give","given","gives","giving","go","goes","gone","got","gotten","h","happens","hardly","hed","hence","hereafter","hereby","herein","heres","hereupon","hes","hi","hid","hither","home","howbeit","however","hundred","id","ie","im","immediate","immediately","importance","important","inc","indeed","index","information","instead","invention","inward","itd","it'll","j","k","keep","keeps","kept","kg","km","know","known","knows","l","largely","last","lately","later","latter","latterly","least","less","lest","let","lets","like","liked","likely","line","little","'ll","look","looking","looks","ltd","made","mainly","make","makes","many","may","maybe","mean","means","meantime","meanwhile","merely","mg","might","million","miss","ml","moreover","mostly","mr","mrs","much","mug","must","n","na","name","namely","nay","nd","near","nearly","necessarily","necessary","need","needs","neither","never","nevertheless","new","next","nine","ninety","nobody","non","none","nonetheless","noone","normally","nos","noted","nothing","nowhere","obtain","obtained","obviously","often","oh","ok","okay","old","omitted","one","ones","onto","ord","others","otherwise","outside","overall","owing","p","page","pages","part","particular","particularly","past","per","perhaps","placed","please","plus","poorly","possible","possibly","potentially","pp","predominantly","present","previously","primarily","probably","promptly","proud","provides","put","q","que","quickly","quite","qv","r","ran","rather","rd","readily","really","recent","recently","ref","refs","regarding","regardless","regards","related","relatively","research","respectively","resulted","resulting","results","right","run","said","saw","say","saying","says","sec","section","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sent","seven","several","shall","shed","shes","show","showed","shown","showns","shows","significant","significantly","similar","similarly","since","six","slightly","somebody","somehow","someone","somethan","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specifically","specified","specify","specifying","still","stop","strongly","sub","substantially","successfully","sufficiently","suggest","sup","sure","take","taken","taking","tell","tends","th","thank","thanks","thanx","thats","that've","thence","thereafter","thereby","thered","therefore","therein","there'll","thereof","therere","theres","thereto","thereupon","there've","theyd","theyre","think","thou","though","thoughh","thousand","throug","throughout","thru","thus","til","tip","together","took","toward","towards","tried","tries","truly","try","trying","ts","twice","two","u","un","unfortunately","unless","unlike","unlikely","unto","upon","ups","us","use","used","useful","usefully","usefulness","uses","using","usually","v","value","various","'ve","via","viz","vol","vols","vs","w","want","wants","wasnt","way","wed","welcome","went","werent","whatever","what'll","whats","whence","whenever","whereafter","whereas","whereby","wherein","wheres","whereupon","wherever","whether","whim","whither","whod","whoever","whole","who'll","whomever","whos","whose","widely","willing","wish","within","without","wont","words","world","wouldnt","www","x","yes","yet","youd","youre","z","zero","a's","ain't","allow","allows","apart","appear","appreciate","appropriate","associated","best","better","c'mon","c's","cant","changes","clearly","concerning","consequently","consider","considering","corresponding","course","currently","definitely","described","despite","entirely","exactly","example","going","greetings","hello","help","hopefully","ignored","inasmuch","indicate","indicated","indicates","inner","insofar","it'd","keep","keeps","novel","presumably","reasonably","second","secondly","sensible","serious","seriously","sure","t's","third","thorough","thoroughly","three","well","wonder"),
            true);

    private static Logger logger = Logger.getGlobal();

    /**
     * to run mvn exec:java -Dexec.mainClass="Main"
     * 1. Initialise analyser
     * 2. Read and index documents
     * 3. Initialise searcher(similarity)
     * 4. Generate result file
     * 5. Trec_eval on the results
     */
    public static void main(String[] args) throws IOException {
        FileParser.initialize();

        Map<String, List> documentsMap = readFiles();
        /*logger.log(Level.INFO, "StandardAnalyzer, BM25Similarity:: ");
        runModel(documentsMap, new StandardAnalyzer(stopWords2), new BM25Similarity(),"bm25_stop2", 50);*/

        logger.log(Level.INFO, "CustomAnalyzer, BM25Similarity:: ");
        runModel(documentsMap, new CustomAnalyzer(), new BM25Similarity(),"results_final", 50);

//        logger.log(Level.INFO, "OpenNLPAnalyzer, BM25Similarity:: ");
//        runModel(documentsMap, new OpenNLPAnalyzer(), new BM25Similarity(),"bm25_openNlp", 50);

//        logger.log(Level.INFO, "EnglishAnalyzer, BM25Similarity:: ");
//        runModel(documentsMap, new EnglishAnalyzer(), new BM25Similarity(),"bm25_eng", 50);

//        logger.log(Level.INFO, "CustomAnalyzer, BM25Similarity:: ");
//        runModel(documentsMap, new CustomAnalyzer(), new BM25Similarity(),"bm25_custom_singleSearch", 50);

//        logger.log(Level.INFO, "StandardAnalyzer, ClassicSimilarity(Vector Space Model):: ");
//        runModel(documentsMap, new StandardAnalyzer(stopWords2), new ClassicSimilarity(), "classic_stop2" ,50);

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
    private static void runModel(Map<String, List> documents, Analyzer analyzer, Similarity similarity, String resultDirName, int hits) throws IOException {
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

        Path dirPath = FileParser.RESULTS_DIR.resolve(resultDirName + "/");
        FileParser.createDirectory(dirPath);
        Path resultsFile = dirPath.resolve("results.txt");
        Path trecOutputPath = dirPath.resolve("trec_out" + ".txt");
        Path imagePath = dirPath.resolve("pr" + ".jpeg");

        List<TrecResult> trecResults = searcher.searchAll(documents.get(QUERY_KEY), hits);
        FileParser.writeTrecToFile(trecResults, resultsFile);
        logger.log(Level.INFO, "Result Generated.");

        searcher.evaluateResults(resultsFile, trecOutputPath, imagePath);
        logger.log(Level.INFO, "TREC Evaluated.");

        logger.log(Level.INFO, "Processed");
    }
}
