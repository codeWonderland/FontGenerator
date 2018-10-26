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
        public final double x;
        public final double y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public Character() {

    }

    public Character(SYMBOL symbol, CASE charCase) {
        this.mSymbol = symbol;
        this.mCase = charCase;

        this.mOutline = new ArrayList<List<Coordinate>>();
    }

    public void save(String fileName) {
        // save char
    }

    public void load(String fileName) {

    }

    public void openContour(double x, double y) {
        mCurrentContour = new ArrayList<Coordinate>();
        this.addPoint(x, y);
    }

    public void addPoint(double x, double y) {
        mCurrentContour.add(new Coordinate(x, y));
    }

    public void closeContour(double x, double y) {
        addPoint(x, y);
        mOutline.add(mCurrentContour);
    }

    public List<List<Coordinate>> getOutline() {
        return mOutline;
    }

    private SYMBOL mSymbol;
    private CASE mCase;
    private List<Coordinate> mCurrentContour;
    private List<List<Coordinate>> mOutline;
}
