package game.collision.geom;

public class Util {

    public static boolean lineIntersect(Point2 p1, Point2 p2, Point2 p3, Point2 p4) {
        double denom = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y);
        if (denom == 0.0) {
            return false;
        }
        double ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / denom;
        double ub = ((p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x)) / denom;
        return ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f;
    }

}
