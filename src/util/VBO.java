package util;

import java.util.ArrayList;

public class VBO {
    private ArrayList<VBOObject> vbos = new ArrayList<VBOObject>();
    private VBOObject obj;
    private Vector3 normal = new Vector3(0, 0, 0);
    Point2 textureCoord = new Point2(0, 0);
    private Color color = new Color(1, 1, 1);

    public void glBegin(int type) {
        obj = new VBOObject(type);
    }

    public void glTexCoord2d(double x, double y) {
        textureCoord = new Point2(x, y);
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

    public void glColor4d(double r, double g, double b, double a) {
        color = new Color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
    }

    public void glColor3d(double r, double g, double b) {
        color = new Color((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
    }

    public void glVertex3d(double x, double y, double z) {
        obj.normal = normal;
        obj.color = color;
        obj.textureCoord = textureCoord;
        obj.glVertex3d(x, y, z);
    }

    public void glEnd() {
        vbos.add(obj);
        obj.init();
        obj = null;
    }

    public void render() {
        for (int i = 0; i < vbos.size(); i++) {
            vbos.get(i).render();
        }
    }

    public void finaliize() {
        //vbos.get(i).finalize();
    }
}
