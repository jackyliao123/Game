package game;

public class AABB {

    public double x;
    public double y;
    public double z;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public AABB(double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
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

    public double getAbsMinX() {
        return x + minX;
    }

    public double getAbsMinY() {
        return y + minY;
    }

    public double getAbsMinZ() {
        return z + minZ;
    }

    public double getAbsMaxX() {
        return x + maxX;
    }

    public double getAbsMaxY() {
        return y + maxY;
    }

    public double getAbsMaxZ() {
        return z + maxZ;
    }

    public double getSizeX() {
        return maxX - minX;
    }

    public double getSizeY() {
        return maxY - minY;
    }

    public double getSizeZ() {
        return maxZ - minZ;
    }

    public Point3 getCenterPoint() {
        return new Point3(x + (minX + maxX) / 2.0, y + (minY + maxY) / 2.0, z + (minZ + maxZ) / 2.0);
    }

    public boolean intersects(AABB aabb) {
        double minx = Math.max(getAbsMinX(), aabb.getAbsMinX());
        double miny = Math.max(getAbsMinY(), aabb.getAbsMinY());
        double minz = Math.max(getAbsMinZ(), aabb.getAbsMinZ());
        double maxx = Math.min(getAbsMaxX(), aabb.getAbsMaxX());
        double maxy = Math.min(getAbsMaxY(), aabb.getAbsMaxY());
        double maxz = Math.min(getAbsMaxZ(), aabb.getAbsMaxZ());
        if (minx > maxx || miny > maxy || minz > maxz) {
            return false;
        }
        return true;
    }

    public static boolean contains(Point2[] points, Point2 point) {
        int j = points.length - 1;
        boolean oddNodes = false;
        for (int i = 0; i < points.length; j = i++) {
            if ((((points[i].y <= point.y) && (point.y < points[j].y)) ||
                    ((points[j].y <= point.y) && (point.y < points[i].y))) &&
                    (point.x < (points[j].x - points[i].x) * (point.y - points[i].y) / (points[j].y - points[i].y) + points[i].x))
                oddNodes = !oddNodes;
        }
        return oddNodes;
    }

    public static boolean intersects(Quad2 q, Line2 l) {
        if (Util.lineIntersect(q.points[0], q.points[1], l.points[0], l.points[1]))
            return true;
        if (Util.lineIntersect(q.points[1], q.points[2], l.points[0], l.points[1]))
            return true;
        if (Util.lineIntersect(q.points[2], q.points[3], l.points[0], l.points[1]))
            return true;
        if (Util.lineIntersect(q.points[3], q.points[0], l.points[0], l.points[1]))
            return true;
        if (contains(q.points, l.points[0]))
            return true;
        if (contains(q.points, l.points[1]))
            return true;
        return false;
    }

    public static boolean intersectX(Quad3 quad, Quad3 qSta, Vector3 velocity) {
        Quad2 q1 = new Quad2(quad.points[0].flattenY(), quad.points[1].flattenY(), quad.points[1].add(velocity).flattenY(), quad.points[0].add(velocity).flattenY());
        Quad2 q2 = new Quad2(quad.points[1].flattenZ(), quad.points[2].flattenZ(), quad.points[2].add(velocity).flattenZ(), quad.points[1].add(velocity).flattenZ());
        Line2 l1 = new Line2(qSta.points[0].flattenY(), qSta.points[1].flattenY());
        Line2 l2 = new Line2(qSta.points[1].flattenZ(), qSta.points[2].flattenZ());
        if (intersects(q1, l1) && intersects(q2, l2)) {
            return true;
        }
        return false;
    }

    public static boolean intersectX(Quad3 quad, Line3 line) {
        Line2 q1 = new Line2(quad.points[0].flattenY(), quad.points[1].flattenY());
        Line2 q2 = new Line2(quad.points[1].flattenZ(), quad.points[2].flattenZ());
        Line2 l1 = line.flattenY();
        Line2 l2 = line.flattenZ();
        return Util.lineIntersect(q1.points[0], q1.points[1], l1.points[0], l1.points[1]) && Util.lineIntersect(q2.points[0], q2.points[1], l2.points[0], l2.points[1]);
    }

    public static boolean intersectY(Quad3 quad, Quad3 qSta, Vector3 velocity) {
        Quad2 q1 = new Quad2(quad.points[0].flattenX(), quad.points[1].flattenX(), quad.points[1].add(velocity).flattenX(), quad.points[0].add(velocity).flattenX());
        Quad2 q2 = new Quad2(quad.points[1].flattenZ(), quad.points[2].flattenZ(), quad.points[2].add(velocity).flattenZ(), quad.points[1].add(velocity).flattenZ());
        Line2 l1 = new Line2(qSta.points[0].flattenX(), qSta.points[1].flattenX());
        Line2 l2 = new Line2(qSta.points[1].flattenZ(), qSta.points[2].flattenZ());
        if (intersects(q1, l1) && intersects(q2, l2)) {
            return true;
        }
        return false;
    }

    public static boolean intersectY(Quad3 quad, Line3 line) {
        Line2 q1 = new Line2(quad.points[0].flattenX(), quad.points[1].flattenX());
        Line2 q2 = new Line2(quad.points[1].flattenZ(), quad.points[2].flattenZ());
        Line2 l1 = line.flattenX();
        Line2 l2 = line.flattenZ();
        return Util.lineIntersect(q1.points[0], q1.points[1], l1.points[0], l1.points[1]) && Util.lineIntersect(q2.points[0], q2.points[1], l2.points[0], l2.points[1]);
    }

    public static boolean intersectZ(Quad3 quad, Quad3 qSta, Vector3 velocity) {
        Quad2 q1 = new Quad2(quad.points[0].flattenX(), quad.points[1].flattenX(), quad.points[1].add(velocity).flattenX(), quad.points[0].add(velocity).flattenX());
        Quad2 q2 = new Quad2(quad.points[1].flattenY(), quad.points[2].flattenY(), quad.points[2].add(velocity).flattenY(), quad.points[1].add(velocity).flattenY());
        Line2 l1 = new Line2(qSta.points[0].flattenX(), qSta.points[1].flattenX());
        Line2 l2 = new Line2(qSta.points[1].flattenY(), qSta.points[2].flattenY());
        if (intersects(q1, l1) && intersects(q2, l2)) {
            return true;
        }
        return false;
    }

    public static boolean intersectZ(Quad3 quad, Line3 line) {
        Line2 q1 = new Line2(quad.points[0].flattenX(), quad.points[1].flattenX());
        Line2 q2 = new Line2(quad.points[1].flattenY(), quad.points[2].flattenY());
        Line2 l1 = line.flattenX();
        Line2 l2 = line.flattenY();
        return Util.lineIntersect(q1.points[0], q1.points[1], l1.points[0], l1.points[1]) && Util.lineIntersect(q2.points[0], q2.points[1], l2.points[0], l2.points[1]);
    }

}
