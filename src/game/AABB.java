package game;

public class AABB {

    public double x;
    public double y;
    public double z;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    public Face[] faces;

    public AABB(double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.faces = new Face[6];
        setUpFaces();
    }

    public void render() {
        for (Face face : faces) {
            face.render();
        }
    }

    private void setUpFaces() {
        double x = -this.x;
        double z = -this.z;

        double xSize = maxX - minX;
        double ySize = maxY - minY;
        double zSize = maxZ - minZ;

        Vertex[] vertices = new Vertex[8];
        int[] indices = new int[24];
        Color[] colors = new Color[6];
        Vertex[] normals = new Vertex[6];

        vertices[0] = new Vertex(x, y, z);
        vertices[1] = new Vertex(x, y + ySize, z);
        vertices[2] = new Vertex(x + xSize, y + ySize, z);
        vertices[3] = new Vertex(x + xSize, y, z);
        vertices[4] = new Vertex(x, y + ySize, z + zSize);
        vertices[5] = new Vertex(x, y, z + zSize);
        vertices[6] = new Vertex(x + xSize, y, z + zSize);
        vertices[7] = new Vertex(x + xSize, y + ySize, z + zSize);

        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 2;
        indices[3] = 3;
        indices[4] = 4;
        indices[5] = 5;
        indices[6] = 6;
        indices[7] = 7;
        indices[8] = 3;
        indices[9] = 6;
        indices[10] = 5;
        indices[11] = 0;
        indices[12] = 1;
        indices[13] = 4;
        indices[14] = 7;
        indices[15] = 2;
        indices[16] = 4;
        indices[17] = 1;
        indices[18] = 0;
        indices[19] = 5;
        indices[20] = 3;
        indices[21] = 2;
        indices[22] = 7;
        indices[23] = 6;

        colors[0] = new Color(255, 0, 0, 255);
        colors[1] = new Color(0, 255, 0, 255);
        colors[2] = new Color(255, 255, 0, 255);
        colors[3] = new Color(255, 255, 255, 255);
        colors[4] = new Color(0, 0, 255, 255);
        colors[5] = new Color(255, 0, 255, 255);

        normals[0] = new Vertex(0, 0, -1);
        normals[1] = new Vertex(0, 0, 1);
        normals[2] = new Vertex(0, -1, 0);
        normals[3] = new Vertex(0, 1, 0);
        normals[4] = new Vertex(-1, 0, 0);
        normals[5] = new Vertex(1, 0, 0);

        faces[0] = new Face(vertices, indices, colors, normals, 0);
        faces[1] = new Face(vertices, indices, colors, normals, 1);
        faces[2] = new Face(vertices, indices, colors, normals, 2);
        faces[3] = new Face(vertices, indices, colors, normals, 3);
        faces[4] = new Face(vertices, indices, colors, normals, 4);
        faces[5] = new Face(vertices, indices, colors, normals, 5);
    }

}
