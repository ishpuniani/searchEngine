package parser;

public class DocumentModel {

    private int id;
    private String title;
    private String author;
    private String source;
    private String content;

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String SOURCE = "source";
    public static final String CONTENT = "content";

    DocumentModel(int id, String title, String author, String source, String content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.source = source;
        this.content = content;
    }

    int getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getAuthor() {
        return author;
    }

    String getSource() {
        return source;
    }

    String getContent() {
        return content;
    }
}