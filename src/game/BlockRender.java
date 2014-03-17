package game;

import util.AABB;
import util.VBOObject;

import static org.lwjgl.opengl.GL11.*;

public class BlockRender {
    public static void drawAABB(AABB aabb, double alpha, int texture) {
        drawCube(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ(), aabb.getSizeX(), aabb.getSizeY(), aabb.getSizeZ(), alpha, texture);
    }

    public static void initAABB(AABB aabb, double alpha, VBOObject vbo) {
        vbo.glColor3i(1, 1, 1);
        initAllFaces(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ(), aabb.getSizeX(), aabb.getSizeY(), aabb.getSizeZ(), vbo);
    }

    public static void drawCube(double x, double y, double z, double xSize, double ySize, double zSize, double alpha, int texture) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glBegin(GL_QUADS);
        //back
        glColor4d(1, 1, 1, alpha);
        glNormal3d(0, 0, -1);
        glTexCoord2d(0, 1);
        glVertex3d(x, y, z);
        glTexCoord2d(0, 0);
        glVertex3d(x, y + ySize, z);
        glTexCoord2d(1, 0);
        glVertex3d(x + xSize, y + ySize, z);
        glTexCoord2d(1, 1);
        glVertex3d(x + xSize, y, z);
        //front
        glNormal3d(0, 0, 1);
        glTexCoord2d(0, 0);
        glVertex3d(x, y + ySize, z + zSize);
        glTexCoord2d(0, 1);
        glVertex3d(x, y, z + zSize);
        glTexCoord2d(1, 1);
        glVertex3d(x + xSize, y, z + zSize);
        glTexCoord2d(1, 0);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        //down
        glNormal3d(0, -1, 0);
        glVertex3d(x + xSize, y, z);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x, y, z);
        //up
        glNormal3d(0, 1, 0);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z);
        //left
        glNormal3d(-1, 0, 0);
        glTexCoord2d(0, 0);
        glVertex3d(x, y + ySize, z + zSize);
        glTexCoord2d(1, 0);
        glVertex3d(x, y + ySize, z);
        glTexCoord2d(1, 1);
        glVertex3d(x, y, z);
        glTexCoord2d(0, 1);
        glVertex3d(x, y, z + zSize);
        //right
        glNormal3d(1, 0, 0);
        glTexCoord2d(1, 1);
        glVertex3d(x + xSize, y, z);
        glTexCoord2d(1, 0);
        glVertex3d(x + xSize, y + ySize, z);
        glTexCoord2d(0, 0);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glTexCoord2d(0, 1);
        glVertex3d(x + xSize, y, z + zSize);
        glEnd();

        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        glDisable(GL_TEXTURE_2D);
    }

    public static void initFront(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //back
        vbo.glNormal3d(0, 0, -1);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x, y, z);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x, y + ySize, z);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x + xSize, y + ySize, z);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x + xSize, y, z);
    }

    public static void initBack(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //front
        vbo.glNormal3d(0, 0, 1);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x, y + ySize, z + zSize);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x, y, z + zSize);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x + xSize, y, z + zSize);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x + xSize, y + ySize, z + zSize);
    }

    public static void initDown(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //down
        vbo.glNormal3d(0, -1, 0);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x + xSize, y, z);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x + xSize, y, z + zSize);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x, y, z + zSize);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x, y, z);
    }

    public static void initUp(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //up
        vbo.glNormal3d(0, 1, 0);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x, y + ySize, z);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x, y + ySize, z + zSize);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x + xSize, y + ySize, z + zSize);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x + xSize, y + ySize, z);
    }

    public static void initLeft(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //left
        vbo.glNormal3d(-1, 0, 0);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x, y + ySize, z + zSize);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x, y + ySize, z);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x, y, z);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x, y, z + zSize);
    }

    public static void initRight(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        //right
        vbo.glNormal3d(1, 0, 0);
        vbo.glTexCoord2d(0, 0);
        vbo.glVertex3d(x + xSize, y, z);
        vbo.glTexCoord2d(0, 1);
        vbo.glVertex3d(x + xSize, y + ySize, z);
        vbo.glTexCoord2d(1, 1);
        vbo.glVertex3d(x + xSize, y + ySize, z + zSize);
        vbo.glTexCoord2d(1, 0);
        vbo.glVertex3d(x + xSize, y, z + zSize);
    }

    public static void initAllFaces(double x, double y, double z, double xSize, double ySize, double zSize, VBOObject vbo) {
        initFront(x, y, z, xSize, ySize, zSize, vbo);
        initBack(x, y, z, xSize, ySize, zSize, vbo);
        initUp(x, y, z, xSize, ySize, zSize, vbo);
        initDown(x, y, z, xSize, ySize, zSize, vbo);
        initLeft(x, y, z, xSize, ySize, zSize, vbo);
        initRight(x, y, z, xSize, ySize, zSize, vbo);
    }
}
