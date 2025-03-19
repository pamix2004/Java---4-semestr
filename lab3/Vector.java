public class Vector implements Comparable<Vector>
{
    private double x, y, z;

    public Vector(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dlugosc()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public int compareTo(Vector other)
    {
        return Double.compare(dlugosc(), other.dlugosc());
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
