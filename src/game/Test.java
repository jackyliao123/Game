package game;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;

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
	public static void main(String[] args){
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
			glEnable(GL_LINE_SMOOTH);
			Mouse.setGrabbed(true);
			Random random = new Random(0);
			for(int i = 0; i < 16; i ++){
				for(int j = 0; j < 16; j ++){
					for(int k = 0; k < 16; k ++){
						if(random.nextInt() % 5 == 0){
							aabbs.add(new AABB(i, j, k, 0, 0, 0, 1, 1, 1));
						}
					}
				}
			}
			while(!Display.isCloseRequested()){
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				fov += (targetFov - fov) * 0.2;
				GLU.gluPerspective(fov, (float)Display.getWidth() / (float)Display.getHeight(), 0.01F, 100.0F);
				glMatrixMode(GL_MODELVIEW);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glLoadIdentity();
				glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer)BufferUtils.createFloatBuffer(4).put(0f).put(0f).put(0f).put(1f).flip());
				glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer)BufferUtils.createFloatBuffer(4).put(0f).put(0f).put(0f).put(1f).flip());
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
					hasFocus = false;
					Mouse.setGrabbed(false);
				}
				if(Mouse.isButtonDown(0)){
					hasFocus = true;
					Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
					Mouse.setGrabbed(true);
				}
				handleInput();
				for(AABB aabb : aabbs){
					drawCube(aabb.x + aabb.minX, aabb.y + aabb.minY, aabb.z + aabb.minZ, aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ, 1);
				}
				handleMovement();
				glDisable(GL_CULL_FACE);
				drawCube(boundingBox.x - boundingBox.minX, boundingBox.y - boundingBox.minY, boundingBox.z - boundingBox.minZ, boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY, boundingBox.maxZ - boundingBox.minZ, 0.5);
				glEnable(GL_CULL_FACE);
				Display.update();
				Display.sync(60);
			}
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	public static void handleMovement(){
		boundingBox.x = posX;
		boundingBox.y = posY;
		boundingBox.z = posZ;
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		collide();
		if(posY < 0){
			posY = 0;
			motionY = 0;
			canJump = true;
			if(fly){
				fly = false;
				targetFov /= 1.2;
			}
			motionX *= 0.9;
			motionZ *= 0.9;
		}
		else{
			canJump = false;
		}
		if(!fly){
			motionY -= 0.01;
		}
		motionX *= 0.95;
		motionY *= 0.95;
		motionZ *= 0.95;
	}
	public static void collide(){
		for(AABB aabb : aabbs){
			
		}
	}
	public static void handleInput(){
		if(hasFocus){
			rotx += (Mouse.getX() - Display.getWidth() / 2.0) / 5.0;
			roty -= (Mouse.getY() - Display.getHeight() / 2.0) / 5.0;
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		}
		if(roty > 90){
			roty = 90;
		}
		if(roty < -90){
			roty = -90;
		}
		thirdPerson -= Mouse.getDWheel() / 240D;
		if(thirdPerson < 0){
			thirdPerson = 0;
		}
		glRotated(roty, 1, 0, 0);
		glRotated(rotx, 0, 1, 0);
		double xoffset = -Math.cos(Math.toRadians(rotx + 90)) * thirdPerson * Math.cos(Math.toRadians(roty));
		double yoffset = -Math.cos(Math.toRadians(roty - 90)) * thirdPerson;
		double zoffset = -Math.sin(Math.toRadians(rotx + 90)) * thirdPerson * Math.cos(Math.toRadians(roty));
		glTranslated(xoffset, yoffset, zoffset);
		glTranslated(posX, -posY - 1.25, posZ);
		if(hasFocus){
			spaceTimer ++;
			sprintTimer ++;
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_SPACE){
					if(spaceTimer < 15){
						if(!fly){
							targetFov *= 1.2;
						}
						else{
							targetFov /= 1.2;
						}
						fly = !fly;
					}
					spaceTimer = 0;
				}
				if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_W){
					if(sprintTimer < 15){
						targetFov *= 1.2;
						sprint = true;
					}
					sprintTimer = 0;
				}
				if(!Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_W){
					if(sprint){
						targetFov /= 1.2;
					}
					sprint = false;
				}
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				if(sprint){
					speed *= 1.5;
				}
				motionX += Math.cos(Math.toRadians(rotx + 90)) * speed;
				motionZ += Math.sin(Math.toRadians(rotx + 90)) * speed;
				if(sprint){
					speed /= 1.5;
				}
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				motionX += Math.cos(Math.toRadians(rotx)) * speed;
				motionZ += Math.sin(Math.toRadians(rotx)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				motionX += Math.cos(Math.toRadians(rotx - 90)) * speed;
				motionZ += Math.sin(Math.toRadians(rotx - 90)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				motionX += Math.cos(Math.toRadians(rotx + 180)) * speed;
				motionZ += Math.sin(Math.toRadians(rotx + 180)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
				motionY -= speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				if(!fly){
					if(canJump){
						motionY = 0.2;
					}
				}
				else{
					motionY += speed;
				}
			}
		}
	}
	public static void drawCube(double x, double y, double z, double xSize, double ySize, double zSize, double alpha){
		x = -x;
		z = -z;
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
}
