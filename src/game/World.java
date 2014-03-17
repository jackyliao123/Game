package game;

import util.AABB;

import java.util.ArrayList;

public class World {
    public ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();
    RenderWorld render;

    public World(TextureLoader tl) {
        render = new RenderWorld(this, tl);
    }

    public byte getBlock(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return 0;
        }
        int cx = x >> 4;
        int cz = z >> 4;
        Chunk c = findChunk(cx, cz);
        if (c == null) {
            c = new Chunk(this, cx, cz);
            loadedChunks.add(c);
        }
        return c.getBlockID(x & 0xF, y, z & 0xF);
    }

    public Block getBlockWithoutGenerating(int x, int y, int z) {
        if (y < 0 || y > 255) {
            return null;
        }
        int cx = x >> 4;
        int cz = z >> 4;
        Chunk c = findChunk(cx, cz);
        if (c == null) {
            return null;
        }
        return c.getBlock(x & 0xF, y, z & 0xF);
    }

    public void setBlock(int x, int y, int z, int id) {
        if (y < 0 || y > 255) {
            return;
        }
        int cx = x >> 4;
        int cz = z >> 4;
        Chunk c = findChunk(cx, cz);
        if (c == null) {
            c = new Chunk(this, cx, cz);
            loadedChunks.add(c);
        }
        c.setBlockID(x & 0xF, y, z & 0xF, (byte) id);
    }

    public AABB getAABB(int x, int y, int z) {
        if (getBlock(x, y, z) == 0) {
            return null;
        }
        return Block.blocksList[getBlock(x, y, z)].getAABB(x, y, z);
    }

    public Chunk findChunk(int x, int z) {
        for (int i = 0; i < loadedChunks.size(); i++) {
            Chunk c = loadedChunks.get(i);
            if (c.x == x && c.z == z) {
                return c;
            }
        }
        return null;
    }
}
