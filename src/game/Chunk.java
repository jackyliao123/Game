package game;

import util.VBOObject;

import java.util.Random;

public class Chunk {
	public final int x;
	public final int z;
	public final int xOffset;
	public final int zOffset;
	public final byte[][][] blocks = new byte[16][256][16];
	public VBOObject vbo;
	public World world;
	public Chunk(World world, int x, int z){
		this.world = world;
		this.x = x;
		this.z = z;
		Random random = new Random();
		for(int i = 0; i < 16; i ++){
			for(int j = 0; j < 64; j ++){
				for(int k = 0; k < 16; k ++){
				    blocks[i][j][k] = random.nextInt() % 10 == 0 ? Block.DIRT.id : 0;
//                    blocks[i][j][k] = Block.DIRT.id;
                }
			}
		}
		xOffset = x << 4;
		zOffset = z << 4;
		world.render.redrawList.add(new ChunkPosition(x, z));
		world.render.redrawList.add(new ChunkPosition(x, z));
		world.render.redrawList.add(new ChunkPosition(x + 1, z));
		world.render.redrawList.add(new ChunkPosition(x - 1, z));
		world.render.redrawList.add(new ChunkPosition(x, z + 1));
		world.render.redrawList.add(new ChunkPosition(x, z - 1));
	}
	public void redraw(){
		world.render.redrawList.add(new ChunkPosition(x, z));
		world.render.redrawList.add(new ChunkPosition(x + 1, z));
		world.render.redrawList.add(new ChunkPosition(x - 1, z));
		world.render.redrawList.add(new ChunkPosition(x, z + 1));
		world.render.redrawList.add(new ChunkPosition(x, z - 1));
	}
	public void setBlockID(int x, int y, int z, byte id){
		blocks[x][y][z] = id;
		redraw();
	}
	public byte getBlockID(int x, int y, int z){
		if(x < 0 || x >= 16 || y < 0 || y >= 256 || z < 0 || z >= 16){
			return 0;
		}
		return blocks[x][y][z];
	}
	public Block getBlock(int x, int y, int z){
		if(x < 0 || x >= 16 || y < 0 || y >= 256 || z < 0 || z >= 16){
			return null;
		}
		return Block.blocksList[blocks[x][y][z]];
	}
}
