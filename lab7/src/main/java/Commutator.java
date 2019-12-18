public class Commutator {
    private String leftBound;
    private String rightBound;
    private long time;

    public Commutator(String leftBound, String rightBound, long time) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.time = time;
    }

    public String getLeftBound() {
        return leftBound;
    }

    public String getRightBound() {
        return rightBound;
    }

    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }

    public boolean intersect(String v) {
        return  Integer.parseInt(leftBound) <= Integer.parseInt(v) && Integer.parseInt(v) <= Integer.parseInt(rightBound);
    }
}
