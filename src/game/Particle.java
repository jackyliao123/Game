package game;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.lwjgl.opengl.GL11;

import util.AABB;
import util.CollisionSide;
import util.Line3;
import util.Point3;
import util.Quad3;
import util.Vector3;

public class Particle{
	public double x;
	public double y;
	public double z;
	public double motionX;
	public double motionY;
	public double motionZ;
	public int life = 60;
	public static final double block = 1e-8;
	public Particle(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		motionX = Math.random() - 0.5;
		motionY = Math.random() - 0.5;
		motionZ = Math.random() - 0.5;
	}
	public void tick(){
		life --;
		if(life < 0){
			Test.particles.remove(this);
		}
		collide();
		x += motionX;
		y += motionY;
		z += motionZ;
		if(y < 0){
			y = 0;
		}
		motionX *= 0.95;
		motionY -= 0.05;
		motionZ *= 0.95;
	}
	public void collide(){
		double minX = motionX > 0 ? 0 : motionX;
		double minY = motionY > 0 ? 0 : motionY;
		double minZ = motionZ > 0 ? 0 : motionZ;
		
		double maxX = motionX < 0 ? 0 : motionX;
		double maxY = motionY < 0 ? 0 : motionY;
		double maxZ = motionZ < 0 ? 0 : motionZ;
		AABB playerMovement = new AABB(x, y, z, minX, minY, minZ, maxX, maxY, maxZ);
		PriorityQueue<CollisionSide> sides = new PriorityQueue<CollisionSide>();
		//drawAABB(playerMovement, 0.25);
		Point3 center = new Point3(x, y, z);
		ArrayList<AABB> aabbs = Test.getAllAABBs(playerMovement);
		for(AABB aabb : aabbs){
			if(!playerMovement.intersects(aabb))
				continue;
			if(motionY > 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())), 
					aabb, center, CollisionSide.Yp));
			}
			if(motionY < 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ())), 
					aabb, center, CollisionSide.Yn));
			}
			if(motionX > 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())), 
					aabb, center, CollisionSide.Xp));
			}
			if(motionX < 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())), 
					aabb, center, CollisionSide.Xn));
			}
			if(motionZ > 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMinZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMinZ())), 
					aabb, center, CollisionSide.Zp));
			}
			if(motionZ < 0){
				sides.add(new CollisionSide(
					new Quad3(
						new Point3(aabb.getAbsMinX(), aabb.getAbsMinY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMinX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMaxY(), aabb.getAbsMaxZ()),
						new Point3(aabb.getAbsMaxX(), aabb.getAbsMinY(), aabb.getAbsMaxZ())), 
					aabb, center, CollisionSide.Zn));
			}
		}
		boolean xCollide = false;
		boolean yCollide = false;
		boolean zCollide = false;
		int size = sides.size();
		for(int i = 0; i < size; i ++){
			CollisionSide side = sides.poll();
			if(side.type == CollisionSide.Xp && !xCollide){
				if(AABB.intersectX(side.qStat, calculateMotion())){
					xCollide = true;
					x = side.aabb.getAbsMinX() - block;
					motionX = 0;
				}
			}
			else if(side.type == CollisionSide.Xn && !xCollide){
				if(AABB.intersectX(side.qStat, calculateMotion())){
					xCollide = true;
					x = side.aabb.getAbsMaxX() + block;
					motionX = 0;
				}
			}
			else if(side.type == CollisionSide.Yp && !yCollide){
				if(AABB.intersectY(side.qStat, calculateMotion())){
					yCollide = true;
					y = side.aabb.getAbsMinY() - block;
					motionY = 0;
				}
			}
			else if(side.type == CollisionSide.Yn && !yCollide){
				if(AABB.intersectY(side.qStat, calculateMotion())){
					yCollide = true;
					y = side.aabb.getAbsMaxY() + block;
					motionY = 0;
				}
			}
			else if(side.type == CollisionSide.Zp && !zCollide){
				if(AABB.intersectZ(side.qStat, calculateMotion())){
					zCollide = true;
					z = side.aabb.getAbsMinZ() - block;
					motionZ = 0;
				}
			}
			else if(side.type == CollisionSide.Zn && !zCollide){
				if(AABB.intersectZ(side.qStat, calculateMotion())){
					z = side.aabb.getAbsMaxZ() + block;
					motionZ = 0;
				}
			}
		}
	}
	public void render(){
		GL11.glVertex3d(x, y, z);
	}
	public Line3 calculateMotion(){
		return new Line3(new Point3(x, y, z), new Point3(x + motionX, y + motionY, z + motionZ));
	}
}