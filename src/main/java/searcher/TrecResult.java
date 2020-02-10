package searcher;

public class TrecResult {

    private int qid;
    private int did;
    private float score;

    public TrecResult() {}

    public TrecResult(int qid, int did, float score) {
        this.qid = qid;
        this.did = did;
        this.score = score;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format("%d\tITER\t%d\tRANK\t%f\tRUN\n", qid, did, score);
    }
}
