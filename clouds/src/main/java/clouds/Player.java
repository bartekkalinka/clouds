package clouds;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

public class Player extends GameObject {

	private Image[] imgFace;
	private Image[] imgLeft;
	private Image[] imgRight;
	private Image[] currImg;
	private int stepImg;
	private boolean inTheAir;
	private Cloud carryingCloud;
	private CloudsInterface ci;
	private GameResult gameresult;
	private Container container;
	
	Player(Orientation orient, Container container, GameResult gameresult, CloudsInterface ci) {
		super(orient, 0, 0);
		this.gameresult = gameresult;
		this.container = container;
		
		this.ci = ci;
		loadImages();
		currImg = imgFace;
		stepImg = 0;
		
		inTheAir = true;
	}
	
	public void update() {
		if(gameresult.getGameWon()) {
			vx = 0;
			vy = 0;
			return;
		}
		
		// gravity
	    if(vy < Constants.PLAYERSPEED) {
	        vy += 1;
	    }
		
	    // facing direction
	    if(vx > 0) {
	    	currImg = imgRight;
	    }
	    else if(vx < 0) {
	    	currImg = imgLeft;
	    }
	    else {
	    	currImg = imgFace;
	    }
	    
	    // movement
	    x += vx;
	    y += vy;
	    
	    // steps management
	    if(vx != 0 && !inTheAir) {
	    	stepImg = 0; //TODO Gosu::milliseconds / @@step_int % 2
	    }
	    
	    // collision detection
	    inTheAir = true;  //unless there are any collisions below
	    multipush(container.cloudCollisions(x, y, x + currImg[stepImg].getWidth(ci.getImageObserver()), y + currImg[stepImg].getHeight(ci.getImageObserver()), vx, vy));
	}
	
	private void multipush(List<Collision> cols) {
	    // initial vectors
	    double vecUp = 0;
	    double vecDown = 0;
	    double vecLeft = 0;
	    double vecRight = 0;
	    boolean isdowncol = false;
	    boolean isupcol = false;
	    for(Collision c : cols) {

	      // summing up all collisions:
	      if(c.dir == Direction.UP) {
	        vecUp = Math.min(vecUp, c.coord - currImg[stepImg].getHeight(ci.getImageObserver()) - y);
	        vy = c.vel;
	        isupcol = true;
	      }
	      else if(c.dir == Direction.DOWN) {
	        vecDown = Math.max(vecDown, c.coord - y);
	        isdowncol = true;
	      }
	      else if(c.dir == Direction.LEFT) {
	        vecLeft = Math.min(vecLeft, c.coord - currImg[stepImg].getWidth(ci.getImageObserver()) - x);
	      }
	      else if(c.dir == Direction.RIGHT) {
	        vecRight = Math.max(vecRight, c.coord - x);
	      }
	    }
	    x = x + vecLeft + vecRight;
	    y = y + vecUp + vecDown;
	    inTheAir = (!isupcol) || isdowncol;
	}
	
	public void draw(Graphics gfxBuff) {
		int scrWidth = Double.valueOf(currImg[stepImg].getWidth(null) * orient.getZoom()).intValue();
		int scrHeight = Double.valueOf(currImg[stepImg].getHeight(null) * orient.getZoom()).intValue();
		gfxBuff.drawImage(currImg[stepImg], getScrX(), getScrY(), scrWidth, scrHeight, null);
	}
	
	// player's horizontal movement caused by pressing arrow keys
	public void moveStart(Direction dir) {
	    // coordinates modification
	    if(dir == Direction.RIGHT) {
	    	vx = Constants.PLAYERSPEED; 
	    }
	    else if(dir == Direction.LEFT) {
	    	vx = -Constants.PLAYERSPEED;
	    }
	}
	
	public void moveStop() {
	    // stop horizontal movement on key release
	    vx = 0;
	}
	
	public void jump() {
		if(!inTheAir) {
			vy = -2 * Constants.PLAYERSPEED + carryingCloud.vy;
			carryingCloud.energyCharge((int)(4.5*Constants.PLAYERSPEED));
		}
	}
	
	private void loadImages() {
		Image[] images = ci.getImages();
		imgFace = new Image[1];
	    imgFace[0] = images[0];
	    imgLeft = new Image[2];
	    imgLeft[0] = images[1];
	    imgLeft[1] = images[2];
	    imgRight = new Image[2];
	    imgRight[0] = images[3];
	    imgRight[1] = images[4];
	}
	
	public void setCarryingCloud(Cloud c) {
		carryingCloud = c;
	}
}
