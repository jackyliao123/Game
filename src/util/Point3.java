package util;

public class Point3 {
	public double x;
	public double y;
	public double z;
	public Point3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Point3 add(Vector3 velocity){
		return new Point3(x + velocity.x, y + velocity.y, z + velocity.z);
	}
	public Point2 flattenX(){
		return new Point2(y, z);
	}
	public Point2 flattenY(){
		return new Point2(x, z);
	}
	public Point2 flattenZ(){
		return new Point2(x, y);
	}
	public double distanceSqTo(Point3 point){
		double xdist = x - point.x;
		double ydist = y - point.y;
		double zdist = z - point.z;
		return xdist * xdist + ydist * ydist + zdist * zdist;
	}
	public String toString(){
		return "[x: " + x + ", y: " + y + ", z: " + z + "]";
	}
}
