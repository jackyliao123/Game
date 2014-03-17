package game;

import util.AABB;

public class Block {

    public final byte id;
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

    public static final Block DIRT = new Block(1, true);
    public static final Block GLASS = new Block(2, false);

}
