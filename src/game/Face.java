package game;

public class Face {

    private Vertex[] vertices;
    private int[] indicies;

    public Face(Vertex[] vertices, int[] indicies) {
        this.vertices = vertices;
        this.indicies = indicies;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int[] getIndicies() {
        return indicies;
    }

}
