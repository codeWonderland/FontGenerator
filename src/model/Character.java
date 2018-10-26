package model;

import java.util.ArrayList;
import java.util.List;

public class Character {
    public static enum CASE {
        LOWERCASE,
        UPPERCASE
    }

    public static enum SYMBOL {
        a, b, c, d, e,
        f, g, h, i, j,
        k, l, m, n, o,
        p, q, r, s, t,
        u, v, w, x, y, z
    }

    public class Coordinate {
        public final int x;
        public final int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Character() {

    }

    public Character(SYMBOL symbol, CASE charCase) {

    }

    public void save() {
        // save char
    }

    public void openContour(int x, int y) {
        mCurrentContour = new ArrayList<Coordinate>();
        this.addPoint(x, y);
    }

    public void addPoint(int x, int y) {
        mCurrentContour.add(new Coordinate(x, y));
    }

    public void closeContour(int x, int y) {
        addPoint(x, y);
        mOutline.add(mCurrentContour);
    }

    public List<List<Coordinate>> getOutline() {
        return mOutline;
    }

    private String mFileLoc;
    private SYMBOL mSymbol;
    private CASE mCase;
    private List<Coordinate> mCurrentContour;
    private List<List<Coordinate>> mOutline;
}
