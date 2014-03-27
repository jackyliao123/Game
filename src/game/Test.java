package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.PriorityQueue;

import static org.lwjgl.opengl.GL11.*;

public class Test {

    static {
        try {
            NativeLoader.loadLwjgl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double rotx;
    public static double roty;
    public static double posX;
    public static double posY = 65;
    public static double posZ;
    public static double motionX;
    public static double motionY;
    public static double motionZ;
    public static boolean canJump;
    public static AABB boundingBox = new AABB(0, 0, 0, -0.4, 0, -0.4, 0.4, 1.8, 0.4);
    public static double speed = 0.005;
    public static boolean fly = true;
    public static double thirdPerson;
    public static int spaceTimer;
    public static int sprintTimer;
    public static float fov = 70;
    public static float targetFov = fov;
    public static boolean sprint = false;
    public static boolean hasFocus = true;
    public static final double block = 1e-8;
    public static VBO vbo = new VBO();
    public static double yOffset = 1.5;
    public static double reach = 5;
    public static CollisionSide sideSelected;
    public static XYZ blockSelected;
    public static World world;
    public static TextureLoader tl = new TextureLoader();
    public static ArrayList<Particle> particles = new ArrayList<Particle>();

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.setInitialBackground(0, 0.5f, 1);
            Display.create(new PixelFormat(8, 8, 0, 0));
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
            tl = new TextureLoader(Test.class.getResourceAsStream("/textures/tiles.png"), 32, 32, 8, 8);
            world = new World(tl);
            //glShadeModel(GL_FLAT);
            //glEnable(GL_MULTISAMPLE);
            Mouse.setGrabbed(true);
            /*Random random = new Random(0);
            for(int i = 0; i < 32; i ++){
				for(int j = 0; j < 32; j ++){
					for(int k = 0; k < 32; k ++){
						//if(random.nextInt() % 5 == 0){
							aabbs.add(new AABB(i, j, k, 0, 0, 0, 1, 1, 1));
						}
					//}
					//aabbs.add(new AABB(i, 0, j, 0, 0, 0, 1, 1, 1));
				}
			}*/

            vbo.glBegin(GL_QUADS);
            /*for(AABB aabb : aabbs){
                initAABB(aabb, 1, vbo);
			}*/
            vbo.glEnd();
            System.gc();

            //vbo.finalize();

            //aabbs.add(new AABB(10, 10, 10, 0, 0, 0, 2, 2, 2));
            try {
                texture = loadTexture(Test.class.getResourceAsStream("/textures/a.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            FloatBuffer fogColor = (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0).put(0.5f).put(1).put(1).flip();
            glFogi(GL_FOG_MODE, GL_EXP);
            glFog(GL_FOG_COLOR, fogColor);
            glFogf(GL_FOG_DENSITY, 0.02f);
            glHint(GL_FOG_HINT, GL_NICEST);
            glEnable(GL_FOG);

            while (!Display.isCloseRequested()) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
                glMatrixMode(GL_MODELVIEW);
                glColor3d(1, 1, 1);
                glBegin(GL_LINES);
                glVertex2d(Display.getWidth() / 2 - 10, Display.getHeight() / 2);
                glVertex2d(Display.getWidth() / 2 + 10, Display.getHeight() / 2);
                glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 - 10);
                glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 + 10);
                glEnd();
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                fov += (targetFov - fov) * 0.2;
                GLU.gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), 0.01F, 1000.0F);
                glMatrixMode(GL_MODELVIEW);

                //particles.add(new Particle(posX, posY, posZ));

                glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0f).put(0f).put(0f).put(1f).flip());
                glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0f).put(0f).put(0f).put(1f).flip());
                if (hasFocus && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    hasFocus = false;
                    Mouse.setGrabbed(false);
                }
                if (hasFocus && Mouse.isButtonDown(0) && blockSelected != null && breakTimer <= 0) {
                    world.setBlock(blockSelected.x, blockSelected.y, blockSelected.z, 0);
                    for (int i = 0; i < 100; i++) {
                        particles.add(new Particle(blockSelected.x + 0.5,
                                blockSelected.y + 0.5, blockSelected.z + 0.5, tl, Block.DIRT));
                        breakTimer = 10;
                    }
                }
                if (hasFocus && Mouse.isButtonDown(1) && sideSelected != null && placeTimer <= 0) {
                    switch (sideSelected.type) {
                        case CollisionSide.Xn:
                            world.setBlock(blockSelected.x + 1, blockSelected.y,
                                    blockSelected.z, Block.DIRT.id);
                            break;
                        case CollisionSide.Xp:
                            world.setBlock(blockSelected.x - 1, blockSelected.y,
                                    blockSelected.z, Block.DIRT.id);
                            break;
                        case CollisionSide.Yn:
                            world.setBlock(blockSelected.x, blockSelected.y + 1,
                                    blockSelected.z, Block.DIRT.id);
                            break;
                        case CollisionSide.Yp:
                            world.setBlock(blockSelected.x, blockSelected.y - 1,
                                    blockSelected.z, Block.DIRT.id);
                            break;
                        case CollisionSide.Zn:
                            world.setBlock(blockSelected.x, blockSelected.y,
                                    blockSelected.z + 1, Block.DIRT.id);
                            break;
                        case CollisionSide.Zp:
                            world.setBlock(blockSelected.x, blockSelected.y,
                                    blockSelected.z - 1, Block.DIRT.id);
                            break;
                    }
                    placeTimer = 10;
                }
                placeTimer--;
                breakTimer--;
                if (!hasFocus && Mouse.isButtonDown(0)) {
                    hasFocus = true;
                    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
                    Mouse.setGrabbed(true);
                }
                handleInput();
                glDisable(GL_LIGHTING);
                glColor4d(1, 1, 1, 1);
                glDisable(GL_CULL_FACE);
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, tl.getTextureId());
                glBegin(GL_QUADS);
                for (int i = 0; i < particles.size(); i++) {
                    particles.get(i).render();
                    particles.get(i).tick();
                }
                glEnd();
                glEnable(GL_CULL_FACE);
                glEnable(GL_LIGHTING);
                glEnable(GL_LIGHT0);
                //vbo.render();
                loadUnloadChunks(2);
                world.render.render();
                if (blockSelected != null) {
                    AABB aabb = world.getAABB(blockSelected.x, blockSelected.y, blockSelected.z);
                    if (aabb != null)
                        drawBoundingBox(aabb, 1);
                }
                //glDisable(GL_CULL_FACE);
                handleMovement();
                BlockRender.drawAABB(boundingBox, 1, texture);
                //drawCube(boundingBox.x - boundingBox.minX, boundingBox.y - boundingBox.minY, boundingBox.z - boundingBox.minZ, boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY, boundingBox.maxZ - boundingBox.minZ, 0.5);
//                glEnable(GL_CULL_FACE);
                Display.update();
                Display.sync(60);
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadUnloadChunks(int size) {
        int inChunkX = (int) Math.floor(posX / 16);
        int inChunkZ = (int) Math.floor(posZ / 16);
        PriorityQueue<ChunkPosition> queue = new PriorityQueue<ChunkPosition>();
        for (int i = inChunkX - size; i <= inChunkX + size; i++) {
            for (int j = inChunkZ - size; j <= inChunkZ + size; j++) {
                if (world.findChunk(i, j) == null && world.render.findRedrawList(i, j) == null) {
                    ChunkPosition c = new ChunkPosition(i, j);
                    c.distance = (inChunkX - i) * (inChunkX - i) + (inChunkZ - j) * (inChunkZ - j);
                    queue.add(c);
                    //world.loadedChunks.add(c);
                }
            }
        }
        int s = queue.size();
        for (int i = 0; i < s; i++) {
            ChunkPosition p = queue.poll();
            world.loadedChunks.add(new Chunk(world, p.x, p.z));
        }
        for (int i = 0; i < world.loadedChunks.size(); i++) {
            Chunk c = world.loadedChunks.get(i);
            if (Math.abs(c.x - inChunkX) > size + 1 || Math.abs(c.z - inChunkZ) > size + 1) {
                world.loadedChunks.remove(c);
            }
        }
        for (int i = 0; i < world.render.position.size(); i++) {
            ChunkPosition c = world.render.position.get(i);
            if (Math.abs(c.x - inChunkX) > size + 1 || Math.abs(c.z - inChunkZ) > size + 1) {
                world.render.position.remove(c);
            }
        }
    }

    private static void drawBoundingBox(AABB aabb, double alpha) {
        double offset = 0.01;
        glLineWidth(2);
        drawBoundingBox(aabb.getAbsMinX() - offset, aabb.getAbsMinY() - offset, aabb.getAbsMinZ() - offset, aabb.getSizeX() + offset, aabb.getSizeY() + offset, aabb.getSizeZ() + offset, alpha);
    }

    private static void drawBoundingBox(double x, double y, double z, double xSize, double ySize, double zSize, double alpha) {
        glColor4d(0, 0, 0, alpha);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glBegin(GL_LINES);
        //back
        glVertex3d(x, y, z);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x + xSize, y + ySize, z);
        glVertex3d(x + xSize, y, z);
        //front
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        //down
        glVertex3d(x + xSize, y, z);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x + xSize, y, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x, y, z + zSize);
        glVertex3d(x, y, z);
        glVertex3d(x + xSize, y, z);
        glVertex3d(x, y, z);
        //up
        glVertex3d(x, y + ySize, z);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y + ySize, z);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x + xSize, y + ySize, z);
        //left
        glVertex3d(x, y + ySize, z + zSize);
        glVertex3d(x, y + ySize, z);
        glVertex3d(x, y, z);
        glVertex3d(x, y, z + zSize);
        //right
        glVertex3d(x + xSize, y, z);
        glVertex3d(x + xSize, y + ySize, z);
        glVertex3d(x + xSize, y + ySize, z + zSize);
        glVertex3d(x + xSize, y, z + zSize);
        glEnd();
    }

    private static void raytrace() {
        blockSelected = null;
        sideSelected = null;
        double x = -Math.cos(Math.toRadians(rotx + 90)) * reach * Math.cos(Math.toRadians(roty)) + posX;
        double y = -Math.cos(Math.toRadians(roty - 90)) * reach + posY + yOffset;
        double z = -Math.sin(Math.toRadians(rotx + 90)) * reach * Math.cos(Math.toRadians(roty)) + posZ;

        Point3 center = new Point3(posX, posY + yOffset, posZ);

        Line3 ray = new Line3(center, new Point3(x, y, z));

        //AABB checkArea = new AABB(boundingBox.x, boundingBox.y, boundingBox.z, -reach, -reach, -reach, reach, reach, reach);
        AABB checkArea = ray.toAABB();
        PriorityQueue<CollisionSide> sides = new PriorityQueue<CollisionSide>();
        ArrayList<AABB> aabbs = getAllAABBs(checkArea);
        for (AABB aabb : aabbs) {
            if (!checkArea.intersects(aabb))
                continue;
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())),
                    aabb, center, CollisionSide.Yp));
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ())),
                    aabb, center, CollisionSide.Yn));
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                    aabb, center, CollisionSide.Xp));
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                    aabb, center, CollisionSide.Xn));
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())),
                    aabb, center, CollisionSide.Zp));
            sides.add(new CollisionSide(
                    new Quad3(
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
                            new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())),
                    aabb, center, CollisionSide.Zn));
        }
        boolean collide = false;
        int size = sides.size();
        //System.out.println("start");
        for (int i = 0; i < size; i++) {
            CollisionSide side = sides.poll();
            //if(side.aabb.getCenterPoint().distanceSqTo(side.qStat.getCenterPoint()) != 0.25){
            //System.out.println(side.aabb.getCenterPoint().distanceSqTo(side.qStat.getCenterPoint()));
            //}
            //System.out.println(side.aabb.getCenterPoint());
            if (side.type == CollisionSide.Xp && !collide) {
                if (AABB.intersectX(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            } else if (side.type == CollisionSide.Xn && !collide) {
                if (AABB.intersectX(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            } else if (side.type == CollisionSide.Yp && !collide) {
                if (AABB.intersectY(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            } else if (side.type == CollisionSide.Yn && !collide) {
                if (AABB.intersectY(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            } else if (side.type == CollisionSide.Zp && !collide) {
                if (AABB.intersectZ(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            } else if (side.type == CollisionSide.Zn && !collide) {
                if (AABB.intersectZ(side.qStat, ray)) {
                    collide = true;
                    blockSelected = side.aabb.getXYZ();
                    sideSelected = side;
                    return;
                }
            }
        }
    }

    public static void handleMovement() {
        boundingBox.x = posX;
        boundingBox.y = posY;
        boundingBox.z = posZ;
        canJump = false;
        //long l = System.nanoTime();
        raytrace();
        collide();
        //double t = (System.nanoTime() - l) / 1000000d;
        //System.out.println(t);
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
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
        //drawAABB(playerMovement, 0.25);
        Point3 center = boundingBox.getCenterPoint();
        ArrayList<AABB> aabbs = getAllAABBs(playerMovement);
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

    public static int breakTimer;
    public static int placeTimer;

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
        glTranslated(-posX, -posY - yOffset, -posZ);
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
                    for (int i = 0; i < 10; i++) {
                        particles.add(new Particle(posX,
                                posY + 0.5, posZ, tl, Block.DIRT));
                        breakTimer = 10;
                    }

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
    }

    static int texture;

    public static ArrayList<AABB> getAllAABBs(AABB area) {
        ArrayList<AABB> aabbs = new ArrayList<AABB>();
        int minX = (int) Math.floor(area.getAbsMinX()) - 1;
        int minY = (int) Math.floor(area.getAbsMinY()) - 1;
        int minZ = (int) Math.floor(area.getAbsMinZ()) - 1;
        int maxX = (int) Math.floor(area.getAbsMaxX()) + 1;
        int maxY = (int) Math.floor(area.getAbsMaxY()) + 1;
        int maxZ = (int) Math.floor(area.getAbsMaxZ()) + 1;
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (int k = minZ; k <= maxZ; k++) {
                    AABB aabb = world.getAABB(i, j, k);
                    if (aabb != null) {
                        aabbs.add(aabb);
                    }
                }
            }
        }
        return aabbs;
    }

    public static int loadTexture(InputStream f) throws IOException {
        BufferedImage image = ImageIO.read(f);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() << 4);
        for (int i = 0; i < pixels.length; i++) {
            int rgba = pixels[i];
            buffer.put((byte) (rgba >> 16 & 0xFF)).put((byte) (rgba >> 8 & 0xFF)).put((byte) (rgba & 0xFF)).put((byte) (rgba >> 24 & 0xFF));
        }
        buffer.flip();
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

}
