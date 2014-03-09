package game;

public class Quad3 {

    public Point3[] points = new Point3[4];

    public Quad3(Point3 p1, Point3 p2, Point3 p3, Point3 p4) {
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
    }

    public Point3 getCenterPoint() {
        return new Point3(
                (points[0].x + points[1].x + points[2].x + points[3].x) / 4.0,
                (points[0].y + points[1].y + points[2].y + points[3].y) / 4.0,
                (points[0].z + points[1].z + points[2].z + points[3].z) / 4.0
        );
    }

}
