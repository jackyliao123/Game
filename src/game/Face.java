package game;

import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glBufferData;

public class Face {

    private int vbo;
    private int cbo;
    private int ibo;
    private int nbo;
    private Vertex[] vertices;
    private int[] indices;
    private Color[] colors;
    private Vertex[] normals;

    public Face(Vertex[] vertices, int[] indices, Color[] colors, Vertex[] normals, int number) {
        Vertex[] newVertices = new Vertex[4];
        Color[] newColor;
        Vertex[] newNormals;
        newVertices[0] = vertices[indices[number * 4]];
        System.out.println(newVertices[0].toString());
        newVertices[1] = vertices[indices[number * 4 + 1]];
        newVertices[2] = vertices[indices[number * 4 + 2]];
        newVertices[3] = vertices[indices[number * 4 + 3]];
        this.vertices = newVertices;
        this.indices = new int[]{0, 1, 2, 3};
        newColor = new Color[]{colors[number], colors[number], colors[number], colors[number]};
        newNormals = new Vertex[]{normals[number], normals[number], normals[number], normals[number]};
        this.colors = newColor;
        this.normals = newNormals;

        vbo = glGenBuffers();
        cbo = glGenBuffers();
        ibo = glGenBuffers();
        nbo = glGenBuffers();
        bind();
    }

    private void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer(), GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer(), GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer(), GL_STATIC_DRAW);
    }

    public void render() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexPointer(3, GL_FLOAT, 12, 0);

        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glNormalPointer(GL_FLOAT, 12, 0);

        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glColorPointer(4, GL_DOUBLE, 32, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(GL_QUADS, indices.length, GL_UNSIGNED_INT, 0);
    }

    public FloatBuffer verticesBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length * 3);
        for (Vertex vertex : vertices) {
            buffer.put((float) vertex.getX());
            buffer.put((float) vertex.getY());
            buffer.put((float) vertex.getZ());
        }
        buffer.flip();
        return buffer;
    }

    public FloatBuffer normalsBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(normals.length * 3);
        for (Vertex normal : normals) {
            buffer.put((float) normal.getX());
            buffer.put((float) normal.getY());
            buffer.put((float) normal.getZ());
        }
        buffer.flip();
        return buffer;
    }

    public IntBuffer indicesBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(24);
        for (int index : indices) {
            buffer.put(index);
        }
        buffer.flip();
        return buffer;
    }

    public DoubleBuffer colorsBuffer() {
        DoubleBuffer buffer = BufferUtils.createDoubleBuffer(4 * colors.length);
        for (Color color : colors) {
            buffer.put(color.getR() / 255d);
            buffer.put(color.getG() / 255d);
            buffer.put(color.getB() / 255d);
            buffer.put(color.getA() / 255d);
        }
        buffer.flip();
        return buffer;
    }

}
