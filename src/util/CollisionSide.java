package util;

public class CollisionSide implements Comparable<CollisionSide>{
	public Quad3 qStat;
	public byte type;
	public AABB aabb;
	public Point3 center;
	public Point3 qCenter;
	public static final byte Xn = 1;
	public static final byte Yn = 2;
	public static final byte Zn = 3;
	public static final byte Xp = 4;
	public static final byte Yp = 5;
	public static final byte Zp = 6;
	public CollisionSide(Quad3 qStat, AABB aabb, Point3 center, byte type){
		this.qStat = qStat;
		this.type = type;
		this.aabb = aabb;
		this.center = center;
		qCenter = qStat.getCenterPoint();
	}
	public int compareTo(CollisionSide o) {
		double dist = center.distanceSqTo(qCenter);
		double dist1 = center.distanceSqTo(o.qCenter);
		return dist > dist1 ? 1 : (dist == dist1 ? 0 : -1);
	}
}
