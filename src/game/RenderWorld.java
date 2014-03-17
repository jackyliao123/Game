package game;

import org.lwjgl.opengl.GL11;
import util.VBOObject;

import java.util.ArrayList;

public class RenderWorld {

    public World world;
    public ArrayList<ChunkPosition> position = new ArrayList<ChunkPosition>();
    public ArrayList<ChunkPosition> redrawList = new ArrayList<ChunkPosition>();

    public RenderWorld(World world) {
        this.world = world;
    }

    public void redraw(int x, int z) {
        ChunkPosition pos = findChunk(x, z);
        if (pos == null) {
            pos = new ChunkPosition(x, z);
            position.add(pos);
        }
        pos.vbo = new VBOObject(GL11.GL_QUADS);
        int xOffset = x << 4;
        int zOffset = z << 4;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 16; k++) {
                    Block block = world.getBlockWithoutGenerating(i + xOffset, j, k + zOffset);
                    if (block == null) {
                        continue;
                    }
                    if (!block.isOpaque()) {
                        BlockRender.initAllFaces(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                        continue;
                    }
                    Block l = world.getBlockWithoutGenerating(i + xOffset - 1, j, k + zOffset);
                    Block r = world.getBlockWithoutGenerating(i + xOffset + 1, j, k + zOffset);
                    Block u = world.getBlockWithoutGenerating(i + xOffset, j + 1, k + zOffset);
                    Block d = world.getBlockWithoutGenerating(i + xOffset, j - 1, k + zOffset);
                    Block f = world.getBlockWithoutGenerating(i + xOffset, j, k + zOffset - 1);
                    Block b = world.getBlockWithoutGenerating(i + xOffset, j, k + zOffset + 1);
                    if (l == null || !l.isOpaque()) {
                        BlockRender.initLeft(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                    if (r == null || !r.isOpaque()) {
                        BlockRender.initRight(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                    if (d == null || !d.isOpaque()) {
                        BlockRender.initDown(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                    if (u == null || !u.isOpaque()) {
                        BlockRender.initUp(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                    if (f == null || !f.isOpaque()) {
                        BlockRender.initFront(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                    if (b == null || !b.isOpaque()) {
                        BlockRender.initBack(i + xOffset, j, k + zOffset, 1, 1, 1, pos.vbo);
                    }
                }
            }
        }
        pos.vbo.init();
    }

    public ChunkPosition findChunk(int x, int z) {
        for (int i = 0; i < position.size(); i++) {
            ChunkPosition c = position.get(i);
            if (c.x == x && c.z == z) {
                return c;
            }
        }
        return null;
    }

    public ChunkPosition findRedrawList(int x, int z) {
        for (int i = 0; i < redrawList.size(); i++) {
            ChunkPosition c = redrawList.get(i);
            if (c.x == x && c.z == z) {
                return c;
            }
        }
        return null;
    }

    public void render() {
        if (!redrawList.isEmpty()) {
            redraw(redrawList.get(redrawList.size() - 1).x, redrawList.get(redrawList.size() - 1).z);
            redrawList.remove(redrawList.size() - 1);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Test.texture1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        for (ChunkPosition p : position) {
            p.vbo.render();
        }
    }

}
