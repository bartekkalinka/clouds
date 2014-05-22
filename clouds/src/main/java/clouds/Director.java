package clouds;

import java.awt.Color;

public class Director {

	private Clouds game;
	
	private double[] randomSpot() {
		double coord[] = new double[2];
		coord[0] = game.getRand(Constants.MAPSIZEX) + game.getMapMinX();
		coord[1] = game.getRand(Constants.MAPSIZEY) + game.getMapMinY();
		return coord;
	}
	
	public Director(Clouds game) {
		this.game = game;
		
		for(int i=0; i<Constants.CLOUDSNUM; i++) {
			randomCloud();
		}
		
		goldenCloud();
	}
	
	public void update() {
		if(game.getCloudsNum() < Constants.CLOUDSNUM) {
			randomCloud();
		}
	}
	
	public void goldenCloud() {
		int sizex = game.getRand(15) + 5;
		int sizey = game.getRand(9) + 5;
		double coord[] = randomSpot();
		GoldenCloud gc = new GoldenCloud(game, coord[0], coord[1], sizex, sizey);
		
		//if on screen, move it out, so it enters the screen soon
		while(gc.onScreen()) {
			gc.moveBackwards();
		}
		
		game.setGoldenCloud(gc);
	}
	
	public void randomCloud() {
		int c = game.getRand(4);
		Color color = Color.red;
		switch(c) {
		case 0: color = Color.red; break;
		case 1: color = Color.white; break;
		case 2: color = Color.blue; break;
		case 3: color = Color.green; break;
		}
  
		int sizex = game.getRand(15) + 5;
		int sizey = game.getRand(9) + 5;
		
		double coord[] = randomSpot();
		
		Cloud cloud;
		cloud =	new Cloud(game, coord[0], coord[1], sizex, sizey, color);
		
		//if on screen, move it out, so it enters the screen soon
		while(cloud.onScreen()) {
			cloud.moveBackwards();
		}
		
		game.addCloud(cloud);
	}
}
