package game;

import game.collision.AABB;
import org.lwjgl.util.Color;

public class GameObject {

    private AABB aabb;
    private Face[] faces;

    public GameObject(AABB aabb) {
        this.aabb = aabb;
        this.faces = createFaces();
    }

    private Face[] createFaces() {
        double minX = aabb.getAbsMinX();
        double minY = aabb.getAbsMinY();
        double minZ = aabb.getAbsMinZ();
        double maxX = aabb.getAbsMaxX();
        double maxY = aabb.getAbsMaxY();
        double maxZ = aabb.getAbsMaxZ();

        Face[] faces = new Face[6];
        // back
        faces[0] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(minX, minY, minZ),
                        new Color(255, 0, 0, 255)),
                new Vertex(
                        new Vector3(minX, maxY, minZ),
                        new Color(255, 0, 0, 255)),
                new Vertex(
                        new Vector3(maxX, maxY, minZ),
                        new Color(255, 0, 0, 255)),
                new Vertex(
                        new Vector3(maxX, minY, minZ),
                        new Color(255, 0, 0, 255))},
                new int[]{0, 1, 2, 3});
        // front
        faces[1] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(minX, maxY, maxZ),
                        new Color(0, 0, 255, 255)
                ),
                new Vertex(
                        new Vector3(minX, minY, maxZ),
                        new Color(0, 0, 255, 255)
                ),
                new Vertex(
                        new Vector3(maxX, minY, maxZ),
                        new Color(0, 0, 255, 255)
                ),
                new Vertex(
                        new Vector3(maxX, maxY, maxZ),
                        new Color(0, 0, 255, 255)
                )
        }, new int[]{0, 1, 2, 3});
        // down
        faces[2] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(maxX, minY, minZ),
                        new Color(255, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(maxX, minY, maxZ),
                        new Color(255, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(minX, minY, maxZ),
                        new Color(255, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(minX, minY, minZ),
                        new Color(255, 255, 0, 255))
        }, new int[]{0, 1, 2, 3});
        // up
        faces[3] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(minX, maxY, minZ),
                        new Color(0, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(minX, maxY, maxZ),
                        new Color(0, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(maxX, maxY, maxZ),
                        new Color(0, 255, 0, 255)
                ),
                new Vertex(
                        new Vector3(maxX, maxY, minZ),
                        new Color(0, 255, 0, 255)
                )
        }, new int[]{0, 1, 2, 3});
        // left
        faces[4] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(minX, maxY, maxZ),
                        new Color(255, 0, 255, 255)
                ),
                new Vertex(new Vector3(minX, maxY, minZ),
                        new Color(255, 0, 255, 255)
                ),
                new Vertex(new Vector3(minX, minY, minZ),
                        new Color(255, 0, 255, 255)
                ),
                new Vertex(new Vector3(minX, minY, maxZ),
                        new Color(255, 0, 255, 255)
                )
        }, new int[]{0, 1, 2, 3});
        faces[5] = new Face(new Vertex[]{
                new Vertex(
                        new Vector3(maxX, minY, minZ),
                        new Color(255, 255, 255, 255)
                ),
                new Vertex(
                        new Vector3(maxX, maxY, minZ),
                        new Color(255, 255, 255, 255)
                ),
                new Vertex(
                        new Vector3(maxX, maxY, maxZ),
                        new Color(255, 255, 255, 255)
                ),
                new Vertex(
                        new Vector3(maxX, minY, maxZ),
                        new Color(255, 255, 255, 255)
                )
        }, new int[]{0, 1, 2, 3});
        return faces;
    }

    public Face[] getFaces() {
        return faces;
    }

}
