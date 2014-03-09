package game;

public class CollisionSide implements Comparable<CollisionSide> {

    public Quad3 quad;
    public Quad3 qStat;
    public Vector3 velocity;
    public byte type;
    public AABB aabb;
    public static final byte Xn = 1;
    public static final byte Yn = 2;
    public static final byte Zn = 3;
    public static final byte Xp = 4;
    public static final byte Yp = 5;
    public static final byte Zp = 6;

    public CollisionSide(Quad3 quad, Quad3 qStat, Vector3 velocity, AABB aabb, byte type) {
        this.quad = quad;
        this.qStat = qStat;
        this.velocity = velocity;
        this.type = type;
        this.aabb = aabb;
    }

    public int compareTo(CollisionSide o) {
        double dist = quad.getCenterPoint().distanceSqTo(qStat.getCenterPoint());
        double dist1 = o.quad.getCenterPoint().distanceSqTo(o.qStat.getCenterPoint());
        return dist > dist1 ? -1 : (dist == dist1 ? 0 : 1);
    }

    public boolean intersect() {
        if (type == Xn || type == Xp) {
            return AABB.intersectX(quad, qStat, velocity);
        } else if (type == Yn || type == Yp) {
            return AABB.intersectY(quad, qStat, velocity);
        } else if (type == Zn || type == Zp) {
            return AABB.intersectZ(quad, qStat, velocity);
        }
        throw new RuntimeException();
    }

}
