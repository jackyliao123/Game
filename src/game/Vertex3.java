package game;

public class Vertex3 {

    public final Point3 position;
    public final Point2 textureCoord;
    public final Color color;
    public final Vector3 normal;

    public Vertex3(Point3 position, Point2 textureCoord, Color color, Vector3 normal) {
        this.position = position;
        this.textureCoord = textureCoord;
        this.color = color;
        this.normal = normal;
    }

}
