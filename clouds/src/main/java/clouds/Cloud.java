package clouds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import clouds.rand.RandomSource;

public class Cloud extends GameObject {

	private Color color;
	private int sizex;
	private int sizey;
	private Shape shape;
	private BufferedImage shapeImage;
	private CollisionDetector collisionDetector;
	private int weight;
	private double weightFactor;
	private int energy;
	private double velChange;
	private boolean carriesPlayer;
	
	protected boolean destroyed;
	protected RandomSource randsrc;
	
	/* Cloud constructor
	 * 
	 * Aside from setting attributes according to parameters,
	 * sets some internal attributes, generates random shape and vector
	 * 
	 * @param orient orientation service
	 * @param randsrc random number service
	 * @param ax start position in map coordinates - x coordinate
	 * @param ay start position in map coordinates - y coordinate
	 * @param vx initial momentum - x coordinate
	 * @param vy initial momentum - y coordinate
	 * @param shape cloud's shape including size
	 * @param aColor color 
	 */
	public Cloud(Orientation orient, RandomSource randsrc, 
			double ax, double ay, double vx, double vy,
			Shape shape, Color aColor) {
		super(orient, ax, ay);
		this.randsrc = randsrc;
		color = aColor;
		sizex = shape.sizex;
		sizey = shape.sizey;
		this.shape = shape;
		weight = 0;
		weightFactor = 0;
		carriesPlayer = false;
		destroyed = false;
		
		generateImage();
		setVelocity(vx, vy);
		generateCollisionShape();
		
		energy = weight * Constants.ENERGYSLOWFACTOR;
		velChange = Math.abs(2 * vy / energy);
	}
	
	public void update() {
		energyAndVelocityUpdate();
		move();
	}
	
	public void draw(Graphics gfxBuff) {
		gfxBuff.drawImage(shapeImage, getScrX(), getScrY(), sizex * getTileSize(), 
				sizey * getTileSize(), null);
	}
	
	/*
	 * Set velocity based on momentum and weight
	 * 
	 * @param vx momentum x coordinate
	 * @param vy momentum y coordintate
	 */
	private void setVelocity(double vx, double vy) {
		this.vx = vx * weightFactor;
		this.vy = vy * weightFactor;
	}

	/*
	 * @return is (any part of) cloud visible on screen
	 */
	public boolean onScreen() {
		return (getScrX() + sizex * getTileSize() >= 0)
		    && (getScrX() < Constants.WIDTH)
		    && (getScrY() + sizey * getTileSize() >= 0)
		    && (getScrY() < Constants.HEIGHT);
	}
	
	/*
	 * @return is (any part of) cloud on map buffer
	 */	
	public boolean onMap() {
		return !destroyed
		    && (x + sizex * Constants.MAPTILESIZE >= orient.getMapMinX())
		    && (x < orient.getMapMinX() + Constants.MAPSIZEX)
		    && (y + sizey * Constants.MAPTILESIZE >= orient.getMapMinY())
		    && (y < orient.getMapMinY() + Constants.MAPSIZEY);
	}
	
    //x1, y1, x2, y2 - player's rectangle after movement
    public boolean approxCollision(double x1, double y1, double x2, double y2) {
    	return collisionDetector.approxCollision(x1, y1, x2, y2, x, y);
    }
    
    /*
     * @param x1, y1, x2, y2 - Player's rectangle after movement (left, top, right, bottom)
     * @param pvelx, pvely - Player's velocity vector
     * @return list of collisions between Player's and Cloud's edges
     */
    public List<Collision> detailedCollisions(double x1, double y1, double x2, double y2, 
			double pvelx, double pvely) {
    	List<Collision> cols = collisionDetector.detailedCollisions(x1, y1, x2, y2, pvelx, pvely, x, y, vx, vy);
    	carriesPlayer = colsIncludeUp(cols);
    	return cols;
    }
    
    private boolean colsIncludeUp(List<Collision> cols) {
    	for(Collision col : cols) {
    		if(col.dir == Direction.UP) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private void generateImage() {
    	//generate shape as array of tiles
		weight = shape.getWeight();
		weightFactor = 50 / (((double)weight) + 10);
		
		//draw array of tiles onto buffered image
		shapeImage = new BufferedImage(sizex * getTileSize(), sizey * getTileSize(), 
				BufferedImage.TYPE_INT_ARGB);
		Graphics gfxBuff = shapeImage.getGraphics();
		gfxBuff.setColor(color);		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(shape.shape[x][y]) {
					int qx = x * getTileSize();
					int qy = y * getTileSize();
					gfxBuff.fillRect(qx, qy, getTileSize(), getTileSize());
				}
			}
		}
    }
    
    private void generateCollisionShape() {
    	collisionDetector = new CollisionDetector(shape.shape, sizex, sizey); //TODO use Shape
    }
    
    private void energyAndVelocityUpdate() {
    	if(carriesPlayer) {
    		energyCharge(1);
    	}
    }
    
    public void energyCharge(int times) {
    	energy -= times;
    	vy += (times * velChange);
    }
    
    public boolean getCarriesPlayer() {
    	return carriesPlayer;
    }
    
    public List<FlyingTile> destroy() {
    	destroyed = true;
    	List<FlyingTile> flyingTiles = new ArrayList<FlyingTile>();
		for(int xx=0; xx<sizex; xx++) {
			for(int yy=0; yy<sizey; yy++) {
				if(shape.shape[xx][yy]) {
					double tvx = vx;
					double tvy = vy;
					tvx += (-2 + randsrc.getRand(40) / 10);
					tvy += (-2 + randsrc.getRand(40) / 10);
					flyingTiles.add(new FlyingTile(orient, x + (xx - 1) * Constants.MAPTILESIZE, y + (yy - 1) * Constants.MAPTILESIZE, tvx, tvy, color));
				}
			}
		}
		//TODO instead of returning flying tiles, might use Container.addTile as elsewhere
		return flyingTiles;
    }
    
    //TODO move to Shape
    protected int[] getMiddleTile() {
    	int chosenTile = weight / 2;
    	int currTile = 0;
    	
		for(int xx=0; xx<sizex; xx++) {
			for(int yy=0; yy<sizey; yy++) {
				if(shape.shape[xx][yy]) {
					currTile++;
					if(chosenTile == currTile) {
						int[] ret = new int[2];
						ret[0] = xx;
						ret[1] = yy;
						return ret;
					}
				}
			}
		}
		return null;
    }
 
}
