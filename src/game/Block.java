package game;

import util.AABB;

public class Block {

    public final byte id;
    public int[] textureId = new int[6];
    public static Block[] blocksList = new Block[127];
    private final boolean isOpaque;

    public Block(int id, boolean isOpaque) {
        this.id = (byte) id;
        blocksList[id] = this;
        this.isOpaque = isOpaque;
    }

    public AABB getAABB(int x, int y, int z) {
        return new AABB(x, y, z, 0, 0, 0, 1, 1, 1, x, y, z);
    }

    public boolean isOpaque() {
        return isOpaque;
    }

    public Block setTexture(int[] texture) {
        textureId = texture;
        return this;
    }

    public static final Block DIRT = new Block(1, true).setTexture(new int[]{2, 2, 2, 2, 2, 2});
    public static final Block GRASS = new Block(2, true).setTexture(new int[]{0, 1, 1, 1, 1, 2});

}
