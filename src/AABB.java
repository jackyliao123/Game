public class AABB{
	public double x;
	public double y;
	public double z;
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	public AABB(double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ){
		this.x = x;
		this.y = y;
		this.z = z;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
}
