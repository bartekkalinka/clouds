package clouds;

public class GameObject {
	
	protected double x;
	protected double y;
	protected double vx;
	protected double vy;
	protected Clouds game;
	
	GameObject(Clouds agame, double ax, double ay) {
		game = agame;
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
		return (new Double(Constants.MAPTILESIZE * game.getZoom())).intValue();
	}
	
	public int getScrX() {
		return (new Double(x * game.getZoom() + Constants.WIDTH / 2 - game.getScrOnMapX() * game.getZoom())).intValue();
	}

	public int getScrY() {
		return (new Double(y * game.getZoom() + Constants.HEIGHT / 2 - game.getScrOnMapY() * game.getZoom())).intValue();
	}
	
	public int getScrVelX() {
		return (new Double(vx * game.getZoom())).intValue();
	}
	
	public int getScrVelY() {
		return (new Double(vy * game.getZoom())).intValue();
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
