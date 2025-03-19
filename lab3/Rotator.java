public class Rotator implements Comparable<Rotator>
{
    private double pitch, yaw, roll;

    public Rotator(double pitch, double yaw, double roll)
    {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public double sumaRotacji()
    {
        return pitch + yaw + roll;
    }

    @Override
    public int compareTo(Rotator other)
    {
        return Double.compare(sumaRotacji(), other.sumaRotacji());
    }

    @Override
    public String toString()
    {
        return "(" + pitch + ", " + yaw + ", " + roll + ")";
    }
}
