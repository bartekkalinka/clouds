package clouds;

public class GameObject {
	
	protected double x;
	protected double y;
	protected double vx;
	protected double vy;
	protected Orientation orient;
	
	GameObject(Orientation aorient, double ax, double ay) {
		orient = aorient;
		x = ax;
		y = ay;
		vx = 0;
		vy = 0;
	}
	
	public void move() {
		x += vx;
		y += vy;
	}
	
	public void moveBackwards() {
		x -= vx;
		y -= vy;
	}
	
	public int getTileSize() {
		return (new Double(Constants.MAPTILESIZE * orient.getZoom())).intValue();
	}
	
	public int getScrX() {
		return (new Double(x * orient.getZoom() + Constants.WIDTH / 2 - orient.getScrOnMapX() * orient.getZoom())).intValue();
	}

	public int getScrY() {
		return (new Double(y * orient.getZoom() + Constants.HEIGHT / 2 - orient.getScrOnMapY() * orient.getZoom())).intValue();
	}
	
	public int getScrVelX() {
		return (new Double(vx * orient.getZoom())).intValue();
	}
	
	public int getScrVelY() {
		return (new Double(vy * orient.getZoom())).intValue();
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double ax) {
		x = ax;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double ay) {
		y = ay;
	}	
	
	public double getVX() {
		return vx;
	}
	
	public double getVY() {
		return vy;
	}

}
