package game;

import org.lwjgl.util.Color;

public class Vertex {

    private Vector3 position;

    private Vector3 normal;
    private Color color;
    // Texture later

    public Vertex(Vector3 position, Vector3 normal, Color color) {
        this.position = position;
        this.normal = normal;
        this.color = color;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public Vector3 getNormal() {
        return normal;
    }

}
