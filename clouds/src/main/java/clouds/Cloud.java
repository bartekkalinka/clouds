package clouds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Cloud extends GameObject {

	private Color color;
	private int sizex;
	private int sizey;
	private CloudShape cloudShape;
	private boolean shape[][];
	private BufferedImage shapeImage;
	private CollisionDetector collisionDetector;
	private int weight;
	private double weightFactor;
	private int energy;
	private double velChange;
	private boolean carriesPlayer;
	
	protected boolean destroyed;
	
	Cloud(Clouds agame, double ax, double ay, int aSizeX, int aSizeY, Color aColor) {
		super(agame, ax, ay);
		color = aColor;
		sizex = aSizeX;
		sizey = aSizeY;
		weight = 0;
		weightFactor = 0;
		carriesPlayer = false;
		destroyed = false;
		
		generateShape();
		setVelocity();
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
		//debugDraw();
	}
	
	private void debugDraw() {
		if(carriesPlayer) {
			System.out.print("\ncarriesPlayer");
		}
		if((vx==0) && (vy==0)) {
			System.out.print("\nstopped cloud");
		}
	}
	
	private void setVelocity() {
		do {
			vx = -5 + (game.getRand(11));
			vy = -5 + (game.getRand(11));
		} while((vx==0) && (vy==0));
		vx *= weightFactor;
		vy *= weightFactor;
		//System.out.print("\nvx " + vx + " vy " + vy + " weightFactor " + weightFactor);
	}

	public boolean onScreen() {
		return (getScrX() + sizex * getTileSize() >= 0)
		    && (getScrX() < Constants.WIDTH)
		    && (getScrY() + sizey * getTileSize() >= 0)
		    && (getScrY() < Constants.HEIGHT);
	}
	
	public boolean onMap() {
		return !destroyed
		    && (x + sizex * Constants.MAPTILESIZE >= game.getMapMinX())
		    && (x < game.getMapMinX() + Constants.MAPSIZEX)
		    && (y + sizey * Constants.MAPTILESIZE >= game.getMapMinY())
		    && (y < game.getMapMinY() + Constants.MAPSIZEY);
	}
	
    //x1, y1, x2, y2 - player's rectangle after movement
    public boolean approxCollision(double x1, double y1, double x2, double y2) {
    	return collisionDetector.approxCollision(x1, y1, x2, y2, x, y);
    }
    
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
    
    private void generateShape() {
    	//generate shape as array of tiles
		cloudShape = new CloudShape(game, sizex, sizey);
		shape = cloudShape.getShape();
		weight = cloudShape.weight();
		weightFactor = 50 / (((double)weight) + 10);
		
		//draw array of tiles onto buffered image
		shapeImage = new BufferedImage(sizex * getTileSize(), sizey * getTileSize(), 
				BufferedImage.TYPE_INT_ARGB);
		Graphics gfxBuff = shapeImage.getGraphics();
		gfxBuff.setColor(color);		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(shape[x][y]) {
					int qx = x * getTileSize();
					int qy = y * getTileSize();
					gfxBuff.fillRect(qx, qy, getTileSize(), getTileSize());
				}
			}
		}
    }
    
    private void generateCollisionShape() {
    	collisionDetector = new CollisionDetector(shape, sizex, sizey);
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
				if(shape[xx][yy]) {
					double tvx = vx;
					double tvy = vy;
					tvx += (-2 + game.getRand(40) / 10);
					tvy += (-2 + game.getRand(40) / 10);
					flyingTiles.add(new FlyingTile(game, x + (xx - 1) * Constants.MAPTILESIZE, y + (yy - 1) * Constants.MAPTILESIZE, tvx, tvy, color));
				}
			}
		}
		return flyingTiles;
    }
    
    protected int[] getMiddleTile() {
    	int chosenTile = weight / 2;
    	int currTile = 0;
    	
		for(int xx=0; xx<sizex; xx++) {
			for(int yy=0; yy<sizey; yy++) {
				if(shape[xx][yy]) {
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
