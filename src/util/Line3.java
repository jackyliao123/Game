package util;

public class Line3 {
	public Point3[] points = new Point3[2];
	public Line3(Point3 p1, Point3 p2){
		points[0] = p1;
		points[1] = p2;
	}
	public Line2 flattenX(){
		return new Line2(points[0].flattenX(), points[1].flattenX());
	}
	public Line2 flattenY(){
		return new Line2(points[0].flattenY(), points[1].flattenY());
	}
	public Line2 flattenZ(){
		return new Line2(points[0].flattenZ(), points[1].flattenZ());
	}
	public AABB toAABB(){
		return new AABB(Math.min(points[0].x, points[1].x), Math.min(points[0].y, points[1].y), Math.min(points[0].z, points[1].z), 0, 0, 0, Math.abs(points[0].x - points[1].x), Math.abs(points[0].y - points[1].y), Math.abs(points[0].z - points[1].z));
	}
}
