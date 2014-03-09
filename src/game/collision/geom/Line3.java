package game.collision.geom;

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
}
