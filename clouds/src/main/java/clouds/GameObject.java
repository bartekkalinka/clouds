package clouds;

public class GameObject {
	
	protected double x;
	protected double y;
	protected double vx;
	protected double vy;
	protected Orientation orient;
	
	GameObject(Orientation orient, double ax, double ay) {
		this.orient = orient;
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
	
	/*
	 * @return size of one tile on screen
	 */
	//TODO move to Orientation
	public int getTileSize() {
		return (new Double(Constants.MAPTILESIZE * orient.getZoom())).intValue();
	}
	
	/*
	 * @return horizontal position in screen coordinates counting from left to right
	 */
	public int getScrX() {
		return (new Double(x * orient.getZoom() + Constants.WIDTH / 2 - orient.getScrOnMapX() * orient.getZoom())).intValue();
	}

	/*
	 * @return vertical position in screen coordinates counting from top to bottom
	 */
	public int getScrY() {
		return (new Double(y * orient.getZoom() + Constants.HEIGHT / 2 - orient.getScrOnMapY() * orient.getZoom())).intValue();
	}
	
	public int getScrVelX() {
		return (new Double(vx * orient.getZoom())).intValue();
	}
	
	public int getScrVelY() {
		return (new Double(vy * orient.getZoom())).intValue();
	}
	
	public double[] getPos() {
		return new double[] {x, y};
	}
	
}
