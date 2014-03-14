package game;

import game.collision.AABB;
import game.collision.CollisionSide;
import game.collision.geom.Point3;
import game.collision.geom.Quad3;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Test {

    public static double rotx;
    public static double roty;
    public static double posX;
    public static double posY;
    public static double posZ;
    public static double motionX;
    public static double motionY;
    public static double motionZ;
    public static boolean canJump;
    public static AABB boundingBox = new AABB(0, 0, 0, -0.4, 0, -0.4, 0.4, 1.8, 0.4);
    public static ArrayList<AABB> aabbs = new ArrayList<AABB>();
    public static ArrayList<GameObject> objects = new ArrayList<GameObject>();
    public static double speed = 0.005;
    public static int x;
    public static int y;
    public static int z;
    public static boolean fly = true;
    public static double thirdPerson;
    public static int spaceTimer;
    public static int sprintTimer;
    public static float fov = 70;
    public static float targetFov = fov;
    public static boolean sprint = false;
    public static boolean hasFocus = true;
    public static final double block = 1e-8;

    private static int vbo;
    private static int ibo;
    private static int nbo;
    private static int cbo;
    private static int size;

    public static void main(String[] args) {
        try {
            NativeLoader.loadLwjgl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CCW);
            glEnable(GL_LIGHTING);
            glEnable(GL_LIGHT0);
            glEnable(GL_COLOR_MATERIAL);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
            glEnable(GL_LINE_SMOOTH);
            glShadeModel(GL_SMOOTH);
            Mouse.setGrabbed(true);
            Random random = new Random(0);
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 32; j++) {
                    for (int k = 0; k < 32; k++) {
                        AABB aabb = new AABB(i, j, k, 0, 0, 0, 1, 1, 1);
                        aabbs.add(aabb);
                        objects.add(new GameObject(aabb));
                    }
//                    AABB aabb = new AABB(i, 0, j, 0, 0, 0, 1, 1, 1);
//                    aabbs.add(aabb);
//                    objects.add(new GameObject(aabb));
                }
            }
            initVbo();
//            aabbs.add(new AABB(0, 0, 0, 0, 0, 0, block, 1000, 1000));
//            aabbs.add(new AABB(1, 0, 0, 0, 0, 0, block, 1000, 1000));
//            aabbs.add(new AABB(10, 10, 10, 0, 0, 0, 2, 2, 2));
            while (!Display.isCloseRequested()) {
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                fov += (targetFov - fov) * 0.2;
                GLU.gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), 0.01F, 100.0F);
                glMatrixMode(GL_MODELVIEW);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0f).put(1f).put(0f).put(1f).flip());
                glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0f).put(1f).put(0f).put(1f).flip());
                if (hasFocus && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    hasFocus = false;
                    Mouse.setGrabbed(false);
                }
                if (!hasFocus && Mouse.isButtonDown(0)) {
                    hasFocus = true;
                    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
                    Mouse.setGrabbed(true);
                }
                handleInput();
                handleMovement();
                Display.update();
                Display.sync(60);
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private static void initVbo() {
        vbo = glGenBuffers();
        nbo = glGenBuffers();
        ibo = glGenBuffers();
        cbo = glGenBuffers();

        createGlobalVBO();
    }

    private static void createGlobalVBO() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < objects.size(); i++) {
            GameObject object = objects.get(i);
            for (int j = 0; j < object.getFaces().length; j++) {
                Face face = object.getFaces()[j];
                for (int k = 0; k < face.getVertices().length; k++) {
                    vertices.add(face.getVertices()[k]);
                    indices.add(face.getIndicies()[k] + 4 * j + i * 24);
                }
            }
        }
//        System.out.println(vertices.size());
        DoubleBuffer vertexBuffer = BufferUtils.createDoubleBuffer(vertices.size() * 3);
        DoubleBuffer normalBuffer = BufferUtils.createDoubleBuffer(vertices.size() * 3);
        ByteBuffer colorBuffer = BufferUtils.createByteBuffer(vertices.size() * 4);
        for (Vertex vertex : vertices) {
            size++;
            vertexBuffer.put(vertex.getPosition().x);
            vertexBuffer.put(vertex.getPosition().y);
            vertexBuffer.put(vertex.getPosition().z);
            normalBuffer.put(vertex.getNormal().x);
            normalBuffer.put(vertex.getNormal().y);
            normalBuffer.put(vertex.getNormal().z);
            colorBuffer.put(vertex.getColor().getRedByte());
            colorBuffer.put(vertex.getColor().getGreenByte());
            colorBuffer.put(vertex.getColor().getBlueByte());
            colorBuffer.put(vertex.getColor().getAlphaByte());
        }
        vertexBuffer.flip();
        normalBuffer.flip();
        colorBuffer.flip();
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        for (Integer index : indices) {
            indexBuffer.put(index);
        }
        indexBuffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }

    public static void draw() {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

        glMatrixMode(GL_MODELVIEW);

        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexPointer(3, GL_DOUBLE, 0, 0);

        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glNormalPointer(GL_DOUBLE, 0, 0);

        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, cbo);
        glColorPointer(4, GL_UNSIGNED_BYTE, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(GL_QUADS, size, GL_UNSIGNED_INT, 0);
    }

    public static void drawAABB(AABB aabb) {
        drawBoundingBox(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ(), aabb.getSizeX(), aabb.getSizeY(), aabb.getSizeZ(), 1);
    }

    public static void handleMovement() {
        canJump = false;
        collide();
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        boundingBox.x = posX;
        boundingBox.y = posY;
        boundingBox.z = posZ;
        if (posY < 0) {
            posY = 0;
            motionY = 0;
            canJump = true;
            if (fly) {
                fly = false;
                targetFov /= 1.2;
            }
            motionX *= 0.95;
            motionZ *= 0.95;
        }
        if (!fly) {
            motionY -= 0.01;
        }

        motionX *= 0.95;
        motionY *= 0.95;
        motionZ *= 0.95;
    }

    public static void collide() {
        double minX = motionX > 0 ? boundingBox.minX : boundingBox.minX + motionX;
        double minY = motionY > 0 ? boundingBox.minY : boundingBox.minY + motionY;
        double minZ = motionZ > 0 ? boundingBox.minZ : boundingBox.minZ + motionZ;

        double maxX = motionX < 0 ? boundingBox.maxX : boundingBox.maxX + motionX;
        double maxY = motionY < 0 ? boundingBox.maxY : boundingBox.maxY + motionY;
        double maxZ = motionZ < 0 ? boundingBox.maxZ : boundingBox.maxZ + motionZ;
        AABB playerMovement = new AABB(boundingBox.x, boundingBox.y, boundingBox.z, minX, minY, minZ, maxX, maxY, maxZ);
        PriorityQueue<CollisionSide> sides = new PriorityQueue<CollisionSide>();
        Point3 center = boundingBox.getCenterPoint();
        for (AABB aabb : aabbs) {
            if (!playerMovement.intersects(aabb))
                continue;
            if (motionY > 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())),
                        aabb, center, CollisionSide.Yp));
            }
            if (motionY < 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ())),
                        aabb, center, CollisionSide.Yn));
            }
            if (motionX > 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                        aabb, center, CollisionSide.Xp));
            }
            if (motionX < 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                        aabb, center, CollisionSide.Xn));
            }
            if (motionZ > 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())),
                        aabb, center, CollisionSide.Zp));
            }
            if (motionZ < 0) {
                sides.add(new CollisionSide(
                        new Quad3(
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                                new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                        aabb, center, CollisionSide.Zn));
            }
            //posX = aabb.getAbsMinX() - boundingBox.maxX;
            //posX = aabb.getAbsMaxX() - boundingBox.minX;
            //posY = aabb.getAbsMinY() - boundingBox.maxY;
            //posY = aabb.getAbsMaxY() - boundingBox.minY;
            //posZ = aabb.getAbsMinZ() - boundingBox.maxZ;
            //posZ = aabb.getAbsMaxZ() - boundingBox.minZ;
            if (boundingBox.intersects(aabb)) {
                glEnable(GL_CULL_FACE);
            }
        }
        boolean xCollide = false;
        boolean yCollide = false;
        boolean zCollide = false;
        int size = sides.size();
        //System.out.println("start");
        for (int i = 0; i < size; i++) {
            CollisionSide side = sides.poll();
            //if(side.aabb.getCenterPoint().distanceSqTo(side.qStat.getCenterPoint()) != 0.25){
            //System.out.println(side.aabb.getCenterPoint().distanceSqTo(side.qStat.getCenterPoint()));
            //}
            //System.out.println(side.aabb.getCenterPoint());
            if (side.type == CollisionSide.Xp && !xCollide) {
                if (AABB.intersectX(new Quad3(
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    xCollide = true;
                    posX = side.aabb.getAbsMinX() - boundingBox.maxX - block;
                    boundingBox.x = posX;
                    motionX = 0;
                }
            } else if (side.type == CollisionSide.Xn && !xCollide) {
                if (AABB.intersectX(new Quad3(
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    xCollide = true;
                    posX = side.aabb.getAbsMaxX() - boundingBox.minX + block;
                    boundingBox.x = posX;
                    motionX = 0;
                }
            } else if (side.type == CollisionSide.Yp && !yCollide) {
                if (AABB.intersectY(new Quad3(
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    yCollide = true;
                    posY = side.aabb.getAbsMinY() - boundingBox.maxY - block;
                    boundingBox.y = posY;
                    motionY = 0;
                }
            } else if (side.type == CollisionSide.Yn && !yCollide) {
                if (AABB.intersectY(new Quad3(
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    yCollide = true;
                    posY = side.aabb.getAbsMaxY() - boundingBox.minY + block;
                    boundingBox.y = posY;
                    canJump = true;
                    if (fly) {
                        fly = false;
                        targetFov /= 1.2;
                    }
                    motionY = 0;
                }
            } else if (side.type == CollisionSide.Zp && !zCollide) {
                if (AABB.intersectZ(new Quad3(
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMaxZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMaxZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    zCollide = true;
                    posZ = side.aabb.getAbsMinZ() - boundingBox.maxZ - block;
                    boundingBox.z = posZ;
                    motionZ = 0;
                }
            } else if (side.type == CollisionSide.Zn && !zCollide) {
                if (AABB.intersectZ(new Quad3(
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMinX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMaxY(), boundingBox.getAbsMinZ()),
                        new Point3(boundingBox.getAbsMaxX(), boundingBox.getAbsMinY(), boundingBox.getAbsMinZ())),
                        side.qStat, new Vector3(motionX, motionY, motionZ))) {
                    zCollide = true;
                    posZ = side.aabb.getAbsMaxZ() - boundingBox.minZ + block;
                    boundingBox.z = posZ;
                    motionZ = 0;
                }
            }
        }
    }

    public static void handleInput() {
        if (hasFocus) {
            rotx += (Mouse.getX() - Display.getWidth() / 2.0) / 5.0;
            roty -= (Mouse.getY() - Display.getHeight() / 2.0) / 5.0;
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        }
        if (roty > 90) {
            roty = 90;
        }
        if (roty < -90) {
            roty = -90;
        }
        thirdPerson -= Mouse.getDWheel() / 240D;
        if (thirdPerson < 0) {
            thirdPerson = 0;
        }
        glRotated(roty, 1, 0, 0);
        glRotated(rotx, 0, 1, 0);
        double xoffset = -Math.cos(Math.toRadians(rotx + 90)) * thirdPerson * Math.cos(Math.toRadians(roty));
        double yoffset = -Math.cos(Math.toRadians(roty - 90)) * thirdPerson;
        double zoffset = -Math.sin(Math.toRadians(rotx + 90)) * thirdPerson * Math.cos(Math.toRadians(roty));
        glTranslated(xoffset, yoffset, zoffset);
        glTranslated(-posX, -posY - 1.5, -posZ);
        if (hasFocus) {
            spaceTimer++;
            sprintTimer++;
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                    if (spaceTimer < 15) {
                        if (!fly) {
                            targetFov *= 1.2;
                        } else {
                            targetFov /= 1.2;
                        }
                        fly = !fly;
                    }
                    spaceTimer = 0;
                }
                if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_W) {
                    if (sprintTimer < 15) {
                        targetFov *= 1.2;
                        sprint = true;
                    }
                    sprintTimer = 0;
                }
                if (!Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_W) {
                    if (sprint) {
                        targetFov /= 1.2;
                    }
                    sprint = false;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                if (sprint) {
                    speed *= 1.5;
                }
                motionX += Math.cos(Math.toRadians(rotx - 90)) * speed;
                motionZ += Math.sin(Math.toRadians(rotx - 90)) * speed;
                if (sprint) {
                    speed /= 1.5;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                motionX += Math.cos(Math.toRadians(rotx + 180)) * speed;
                motionZ += Math.sin(Math.toRadians(rotx + 180)) * speed;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                motionX += Math.cos(Math.toRadians(rotx + 90)) * speed;
                motionZ += Math.sin(Math.toRadians(rotx + 90)) * speed;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                motionX += Math.cos(Math.toRadians(rotx)) * speed;
                motionZ += Math.sin(Math.toRadians(rotx)) * speed;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                motionY -= speed;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                if (!fly) {
                    if (canJump) {
                        motionY = 0.2;
                    }
                } else {
                    motionY += speed;
                }
            }
        }
        draw();
        drawPlayer();
    }

    private static void drawPlayer() {
        drawAABB(boundingBox);
    }

    public static void drawBoundingBox(double x, double y, double z, double xSize, double ySize, double zSize, double alpha) {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glBegin(GL_QUADS);
        //back
        glColor4d(1, 0, 0, alpha);
        glNormal3d(0, 0, -1);
        glVertex3d(x, y, z);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x + xSize, y + ySize, z);
        glVertex3d(x + xSize, y, z);
        //front
        glColor4d(0, 1, 0, alpha);
        glNormal3d(0, 0, 1);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        //down
        glColor4d(1, 1, 0, alpha);
        glNormal3d(0, -1, 0);
        glVertex3d(x + xSize, y, z);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x, y, z);
        //up
        glColor4d(0, 0, 1, alpha);
        glNormal3d(0, 1, 0);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z);
        //left
        glColor4d(1, 0, 1, alpha);
        glNormal3d(-1, 0, 0);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x, y, z);
        glVertex3d(x, y, z + zSize);
        //right
        glColor4d(1, 1, 1, alpha);
        glNormal3d(1, 0, 0);
        glVertex3d(x + xSize, y, z);
        glVertex3d(x + xSize, y + ySize, z);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y, z + zSize);
        glEnd();

        glColor3d(0, 0, 0);
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
//		glBegin(GL_LINES);
//		//back
//		glVertex3d(x, y, z);
//		glVertex3d(x, y + ySize, z);
//		glVertex3d(x + xSize, y + ySize, z);
//		glVertex3d(x + xSize, y, z);
//		//front
//		glVertex3d(x, y + ySize, z + zSize);
//		glVertex3d(x, y, z + zSize);
//		glVertex3d(x + xSize, y, z + zSize);
//		glVertex3d(x + xSize, y + ySize, z + zSize);
//		//down
//		glVertex3d(x + xSize, y, z);
//		glVertex3d(x + xSize, y, z + zSize);
//		glVertex3d(x + xSize, y, z + zSize);
//		glVertex3d(x, y, z + zSize);
//		glVertex3d(x, y, z + zSize);
//		glVertex3d(x, y, z);
//		glVertex3d(x + xSize, y, z);
//		glVertex3d(x, y, z);
//		//up
//		glVertex3d(x, y + ySize, z);
//		glVertex3d(x, y + ySize, z + zSize);
//		glVertex3d(x, y + ySize, z + zSize);
//		glVertex3d(x + xSize, y + ySize, z + zSize);
//		glVertex3d(x + xSize, y + ySize, z + zSize);
//		glVertex3d(x + xSize, y + ySize, z);
//		glVertex3d(x, y + ySize, z);
//		glVertex3d(x + xSize, y + ySize, z);
//		//left
//		glVertex3d(x, y + ySize, z + zSize);
//		glVertex3d(x, y + ySize, z);
//		glVertex3d(x, y, z);
//		glVertex3d(x, y, z + zSize);
//		//right
//		glVertex3d(x + xSize, y, z);
//		glVertex3d(x + xSize, y + ySize, z);
//		glVertex3d(x + xSize, y + ySize, z + zSize);
//		glVertex3d(x + xSize, y, z + zSize);
//		glEnd();
    }

}
