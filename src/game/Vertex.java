package game;

import org.lwjgl.util.Color;

public class Vertex {

    private Vector3 position;
    private Color color;
    // Texture later

    public Vertex(Vector3 position, Color color) {
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

}