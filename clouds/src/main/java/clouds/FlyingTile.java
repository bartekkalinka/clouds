package clouds;

import java.awt.Color;
import java.awt.Graphics;

public class FlyingTile extends GameObject {
	private Color color;
	
	public FlyingTile(Orientation orient, double ax, double ay, double velx, double vely, Color aColor) {
		super(orient, ax, ay);
		this.color = aColor;
		vx = velx;
		vy = vely;
	}
	
	public void update() {
		move();
	}
	
	public void draw(Graphics gfxBuff) {
		gfxBuff.setColor(color);
		gfxBuff.fillRect(getScrX(), getScrY(), getTileSize(), getTileSize());
	}
	
	public boolean onScreen() {
		return (getScrX() + getTileSize() >= 0)
		    && (getScrX() < Constants.WIDTH)
		    && (getScrY() + getTileSize() >= 0)
		    && (getScrY() < Constants.HEIGHT);
	}
	
	public boolean onMap() {
		return (x + Constants.MAPTILESIZE >= orient.getMapMinX())
		    && (x < orient.getMapMinX() + Constants.MAPSIZEX)
		    && (y + Constants.MAPTILESIZE >= orient.getMapMinY())
		    && (y < orient.getMapMinY() + Constants.MAPSIZEY);
	}
	
	public boolean gettingCloserTo(double gx, double gy) {
		double currDistPow2 = Math.pow(gx - x, 2) + Math.pow(gy - y, 2);
		double nextDistPow2 = Math.pow(gx - (x + vx), 2) + Math.pow(gy - (y + vy), 2);
		return currDistPow2 >= nextDistPow2;
	}


}
