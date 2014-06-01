package clouds;

import java.awt.Canvas;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Clouds implements KeyListener, Orientation {

	private double zoom = 0.75;
	private double screenOnMapX = 0;
	private double screenOnMapY = 0;

	private List<Cloud> clouds;
	private Director director;
	private Player player;
	private List<FlyingTile> flyingTiles;
	private GoldenCloud goldenCloud = null;
	
	private Image imgBuff;
	private Graphics gfxBuff;
	
	private CloudsInterface cloudsInterface;
	
	private Random random;
	
	private boolean gameWon = false;
	
	public Clouds(CloudsInterface ci) {
		clouds = new ArrayList<Cloud>();
		
		random = new Random(System.currentTimeMillis());
		
		director = new Director(this);
		
		player = new Player(this, ci);
		
		flyingTiles = new ArrayList<FlyingTile>();
		
		cloudsInterface = ci;
		cloudsInterface.initGui(this);
		imgBuff = new BufferedImage(Constants.WIDTH, Constants.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		gfxBuff = imgBuff.getGraphics();
		gfxBuff.setFont(new Font("Arial", Font.PLAIN, 20));		
	}
	
	public void keyPressed(KeyEvent e) {
		//TODO: gdy trzyma sie klawisz UP, to gracz nie skacze w sposob ciagly
		// tak jak dzieje sie w wersji w Rubym
		//TODO: toporne prze³¹czanie sie pomiedzy ruchem w lewo i w prawo
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: System.exit(0); break; //TODO usunac w wersji release
		//case KeyEvent.VK_PAGE_UP: zoom *= 0.75; break;  //TODO usunac w wersji release
		//case KeyEvent.VK_PAGE_DOWN: zoom *= 1.5; break;
		case KeyEvent.VK_LEFT: player.moveStart(Direction.LEFT); break;
		case KeyEvent.VK_RIGHT: player.moveStart(Direction.RIGHT); break;
		case KeyEvent.VK_UP: player.jump(); break;
		/*
		case KeyEvent.VK_X:
			for(Cloud c : clouds) {
				flyingTiles.addAll(c.destroy());
			}
			break;
		*/
		}
	}

	public void keyReleased(KeyEvent e) { 
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT: player.moveStop(); break;
		case KeyEvent.VK_RIGHT: player.moveStop(); break;
		}
	}
	
	public void keyTyped(KeyEvent e) { }
	
	private void update() {
		scrOnMapCalc();
		
		List<Cloud> cloudsToDel = new ArrayList<Cloud>();
		for(Cloud c : clouds) {
			c.update();
			if(!c.onMap()) {
				cloudsToDel.add(c);
			}
		}
		for(Cloud c : cloudsToDel) {
			clouds.remove(c);
		}
		
		List<FlyingTile> tilesToDel = new ArrayList<FlyingTile>();
		for(FlyingTile f : flyingTiles) {
			f.update();
			if(!(f.onMap() || f.gettingCloserTo(player.x, player.y))) {
				tilesToDel.add(f);
			}
		}
		for(FlyingTile f : tilesToDel) {
			flyingTiles.remove(f);
		}
		
		director.update();
		player.update();
		
		gameWon = gameWon || goldenCloud.getCarriesPlayer();
		
	    if(gameWon && goldenCloud.onMap()) {
	    	flyingTiles.addAll(goldenCloud.destroy());
	    }
	}

	public void draw(Graphics graphics, ImageObserver imageObserver) {
		//clear screen buffer
		gfxBuff.setColor(Color.black);
		gfxBuff.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
		
		//draw everything in buffer
		for(Cloud c : clouds) {
			if(c.onScreen()) {
				c.draw(gfxBuff);
			}
		}
		
		for(FlyingTile f : flyingTiles) {
			if(f.onScreen()) {
				f.draw(gfxBuff);
			}
		}
		
		player.draw(gfxBuff);
		
		gfxBuff.setColor(Color.white);
		gfxBuff.drawString("alt: " + (-(long)player.y), 0, 32);
		
		if(gameWon) {
			gfxBuff.drawString("YOU WON!!!", 300, 32);
		}
		
		drawDebugInfo();
		
		//draw buffer on the screen
		graphics.drawImage(imgBuff, 0, 0, imageObserver);
	}
	
	public void play() {
		while (true) {
			update();
			cloudsInterface.draw(this);
			try {
				Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// screen on map calculation
	private void scrOnMapCalc() {
	    // screen moves with the player, with a buffer though
	    if(player.getScrX() < Constants.WIDTH * 0.25) {
	    	screenOnMapX = player.x + (Constants.WIDTH * 0.25) / zoom;
	    }
	    if(player.getScrX() > Constants.WIDTH * 0.75) {
	    	screenOnMapX = player.x - (Constants.WIDTH * 0.25) / zoom;
	    }
	    if(player.getScrY() < Constants.HEIGHT * 0.25) {
	    	screenOnMapY = player.y + (Constants.HEIGHT * 0.25) / zoom;
	    }
	    if(player.getScrY() > Constants.HEIGHT * 0.75) {
	    	screenOnMapY = player.y - (Constants.HEIGHT * 0.25) / zoom;
	    }
	}
	
	//API for player:
	//Collision detection
	//x1, y1, x2, y2 - player's rectangle after movement
	//velx, vely - player's movement vector
	public List<Collision> cloudCollisions(double x1, double y1, double x2, double y2, 
			double velx, double vely) {
    	List<Collision> cols = new ArrayList<Collision>();
		for(Cloud c : clouds) { 
			if(c.approxCollision(x1, y1, x2, y2)) {
				cols.addAll(c.detailedCollisions(x1, y1, x2, y2, velx, vely));
			}
			if(c.getCarriesPlayer()) {
				player.setCarryingCloud(c);
			}
		}
		return cols;
	}
	
	private void drawDebugInfo() {
		//String s = "jumpTest: " + Boolean.valueOf(jumpTest).toString(); 
		//gfxBuff.drawChars(s.toCharArray(), 0, s.length(), 10, 10);
	}
	

	public void addCloud(Cloud c) {
		clouds.add(c);
	}
	
	public int getCloudsNum() {
		return clouds.size();
	}
	
	public double getMapMinX() {
		return getScrOnMapX() - Constants.MAPSIZEX / 2;
	}

	public double getMapMinY() {
		return getScrOnMapY() - Constants.MAPSIZEY / 2;
	}	
	
	public double getZoom() {
		return zoom;
	}
	
	public double getScrOnMapX() {
		return screenOnMapX;
	}
	
	public double getScrOnMapY() {
		return screenOnMapY;
	}
	
	public int getRand(int n) {
		return random.nextInt(n);
	}
	
	public void addTile(FlyingTile tile) {
		flyingTiles.add(tile);
	}

	public void setGoldenCloud(GoldenCloud goldenCloud) {
		 this.goldenCloud = goldenCloud;
		 addCloud(goldenCloud);
	}
	
	public boolean getGameWon() {
		return gameWon;
	}
}
