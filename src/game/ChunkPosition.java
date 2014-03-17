package game;

import util.VBO;
import util.VBOObject;

public class ChunkPosition implements Comparable<ChunkPosition>{
	public int x;
	public int z;
	public double distance;
	public VBOObject vbo;
	public ChunkPosition(int x, int z){
		this.x = x;
		this.z = z;
	}
	public int compareTo(ChunkPosition p){
		return p.distance < distance ? -1 : (p.distance == distance ? 0 : 1);
	}
}
