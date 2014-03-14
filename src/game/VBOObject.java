package game;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class VBOObject {

    public final int type;
    Vector3 normal = new Vector3(0, 0, 0);
    Color color = new Color(255, 255, 255);
    private ArrayList<Vertex3> vertex = new ArrayList<Vertex3>();
    private DoubleBuffer verticies;
    private ByteBuffer colors;
    private DoubleBuffer normals;
    private int vbo;
    private int cbo;
    private int nbo;
    private int size;

    public VBOObject(int type) {
        this.type = type;
    }

    public void glNormal3d(double x, double y, double z) {
        normal = new Vector3(x, y, z);
    }

    public void glColor4i(int r, int g, int b, int a) {
        color = new Color(r, g, b, a);
    }

    public void glColor3i(int r, int g, int b) {
        color = new Color(r, g, b);
    }

    public void glVertex3d(double x, double y, double z) {
        vertex.add(new Vertex3(new Point3(x, y, z), color, normal));
    }

    public void init() {
        size = vertex.size();
        verticies = BufferUtils.createDoubleBuffer(size * 3);
        colors = BufferUtils.createByteBuffer(size * 4);
        normals = BufferUtils.createDoubleBuffer(size * 3);
        for (int i = 0; i < size; i++) {
            Vertex3 v = vertex.get(i);
            Point3 p = v.position;
            Color c = v.color;
            Vector3 n = v.normal;
            verticies.put(p.x).put(p.y).put(p.z);
            normals.put(n.x).put(n.y).put(n.z);
            colors.put(c.r).put(c.g).put(c.b).put(c.a);
        }
        verticies.flip();
        colors.flip();
        normals.flip();

        vbo = glGenBuffers();
        nbo = glGenBuffers();
        cbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_DYNAMIC_DRAW);

        vertex = null;
        verticies = null;
        colors = null;
        normals = null;
    }

    public void render() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexPointer(3, GL_DOUBLE, 0, 0);

        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glNormalPointer(GL_DOUBLE, 0, 0);

        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glColorPointer(4, GL_UNSIGNED_BYTE, 0, 0);

        glDrawArrays(type, 0, size);
    }

}
