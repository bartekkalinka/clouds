package clouds;

import java.util.ArrayList;
import java.util.List;

public class CollisionDetector {
	private boolean[][] shape;
	private int sizex;
	private int sizey;

	private class CollisionTile {
		int x;
		int y;
		List<Direction> edges;
	}
	private List<CollisionTile> collisionShape;
	
	private class ColDirRes {
		double dist;
		double coord;
		double vel;
	}
	
	public CollisionDetector(boolean[][] shape, int sizex, int sizey) {
		this.shape = shape;
		this.sizex = sizex;
		this.sizey = sizey;
		generateCollisionShape();
	}
	
    //x1, y1, x2, y2 - player's rectangle after movement, cx, cy - cloud's coordinates
    public boolean approxCollision(double x1, double y1, double x2, double y2, double cx, double cy) {
    	double cx2 = cx + sizex * Constants.MAPTILESIZE;
    	double cy2 = cy + sizey * Constants.MAPTILESIZE;
    	//any player's corner inside cloud's box?
    	return (vir(x1, y1, cx, cy, cx2, cy2) 
    		|| vir(x1, y2, cx, cy, cx2, cy2) 
    		|| vir(x2, y1, cx, cy, cx2, cy2) 
    		|| vir(x2, y2, cx, cy, cx2, cy2));
    }

    //vertex inside rectangle?
    private boolean vir(double vrtx, double vrty, double x1, double y1, double x2, double y2) {
    	return vrtx >= x1 && vrtx <= x2 && vrty >= y1 && vrty <= y2;
    }
    
    private boolean safeBoolTabGet(boolean[][] tab, int x, int y) {
    	if(x<0 || y<0 || x>=sizex || y>=sizey) {
    		return false;
    	}
    	else {
    		return tab[x][y];
    	}
    }
    
    private void generateCollisionShape() {
    	collisionShape = new ArrayList<CollisionTile>();
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(shape[x][y]) {
					List<Direction> edges = new ArrayList<Direction>();
					if(!safeBoolTabGet(shape, x-1, y)) {
						edges.add(Direction.LEFT);
					}
					if(!safeBoolTabGet(shape, x, y-1)) {
						edges.add(Direction.UP);
					}
					if(!safeBoolTabGet(shape, x+1, y)) {
						edges.add(Direction.RIGHT);
					}
					if(!safeBoolTabGet(shape, x, y+1)) {
						edges.add(Direction.DOWN);
					}
					if(edges.size() > 0) {
						CollisionTile ctile = new CollisionTile();
						ctile.x = x;
						ctile.y = y;
						ctile.edges = edges;
						collisionShape.add(ctile);
					}
				}
			}
		}
    }
    
    private double[] tileCoord(int shapex, int shapey, double cx, double cy) {
    	double[] ret = new double[4];
    	ret[0] = cx + shapex * Constants.MAPTILESIZE;
    	ret[1] = cy + shapey * Constants.MAPTILESIZE;
    	ret[2] = cx + (shapex + 1) * Constants.MAPTILESIZE;
    	ret[3] = cy + (shapey + 1) * Constants.MAPTILESIZE;
    	return ret;
    }
    
    private boolean between(double x, double a, double b) {
    	return x>=a && x<=b;
    }
    
    private void resolveCollisionDirection(Direction dir, Direction dirChecked, double qx1, double qx2, double qy1, double x1, double x2, double y1, double y2, double py2, double velx, double vely, double cvy, ColDirRes cdr, boolean pfirst) {
    	// collision with the edge after movement?
    	if(dir == dirChecked && between(qy1, y1, y2) && (between(qx1, x1, x2) 
    			|| between(qx2, x1, x2) || between(x1, qx1, qx2))) {
    		// there was no collision before movement?
    		boolean noColBeforeMov;
    		if(pfirst) {
    			noColBeforeMov = (py2 + Constants.EPSILON >= qy1);
    		}
    		else {
    			noColBeforeMov = (qy1 + Constants.EPSILON >= py2);
    		}
    		if(noColBeforeMov && vely != 0) {
                // calculate the distance before movement
    			cdr.dist = Math.sqrt((Math.pow((((qy1 - py2) / vely) * velx), 2) 
    					+ Math.pow((qy1 - py2), 2)));
    			cdr.coord = qy1;
    			cdr.vel = cvy;
    		}
    	}
    }
    
    // detailed collisions
    // x1, y1, x2, y2 - player's rectangle after movement
    // pvelx, pvely - player's movement vector
    // cx, cy, cvx, cvy - cloud's coordinates and vector    
    public List<Collision> detailedCollisions(double x1, double y1, double x2, double y2, 
			double pvelx, double pvely, double cx, double cy, double cvx, double cvy) {
    	List<Collision> cols = new ArrayList<Collision>();
    	for(CollisionTile ctile : collisionShape) {
    		// cloud's tile coordinates
    		double[] tcoord = tileCoord(ctile.x, ctile.y, cx, cy);
    		double qx1 = tcoord[0];
    		double qy1 = tcoord[1];
    		double qx2 = tcoord[2];
    		double qy2 = tcoord[3];
    		// relative movement of player and cloud
    		double velx = pvelx - cvx;
    		double vely = pvely - cvy;
    	    // player's rectangle before movement
    		double px1 = x1 - velx;
    		double py1 = y1 - vely;
    		double px2 = x2 - velx;
    		double py2 = y2 - vely;
    	    // minimum distance to edge: first value - whole movement's distance
    	    double minDistToEdge = Math.sqrt(velx * velx + vely * vely);
    	    // the collision with minimum distance to edge
    	    Collision minDistCol = null;
    	    // for each edge of collision shape's tile
    	    for(Direction dir : ctile.edges) {
    	    	// find the edge with lowest distance between player before movement 
    	    	ColDirRes cdr = new ColDirRes();
    	    	cdr.dist = minDistToEdge;
    	    	// check if edge's direction is the given direction
    	    	// and if the player passes this edge during current movement
    	    	// and if so, calculate the distance to the edge before the movement
    	    	resolveCollisionDirection(dir, Direction.UP, qx1, qx2, qy1, x1, x2, y1, y2, py2, velx, vely, cvy, cdr, false);
    	    	resolveCollisionDirection(dir, Direction.RIGHT, qy1, qy2, qx2, y1, y2, x1, x2, px1, vely, velx, cvx, cdr, true);
    	    	resolveCollisionDirection(dir, Direction.DOWN, qx1, qx2, qy2, x1, x2, y1, y2, py1, velx, vely, cvy, cdr, true);
    	    	resolveCollisionDirection(dir, Direction.LEFT, qy1, qy2, qx1, y1, y2, x1, x2, px2, vely, velx, cvx, cdr, false);
    	    	if(cdr.dist < minDistToEdge) {
    	    		minDistToEdge = cdr.dist;
    	    		// save leading coordinate and velocity of the edge (with lowest distance)
    	    		minDistCol = new Collision(dir, cdr.coord, cdr.vel);
    	    	}    	    	
    	    }
    	    // if a collision for this tile is found, add it to the list
    	    if(minDistCol != null) {
    	    	cols.add(minDistCol);
    	    }
    	}
    	return cols;    	
    }
    
}
