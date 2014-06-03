package clouds;

import java.awt.Color;

public class Director {

	private RandomSource randsrc;
	private Orientation orient;
	private Container container;
	
	private double[] randomSpot() {
		double coord[] = new double[2];
		coord[0] = randsrc.getRand(Constants.MAPSIZEX) + orient.getMapMinX();
		coord[1] = randsrc.getRand(Constants.MAPSIZEY) + orient.getMapMinY();
		return coord;
	}
	
	public Director(Orientation orient, RandomSource randsrc, Container container) {
		this.randsrc = randsrc;
		this.orient = orient;
		this.container = container;
		
		for(int i=0; i<Constants.CLOUDSNUM; i++) {
			randomCloud();
		}
		
		goldenCloud();
	}
	
	public void update() {
		if(container.getCloudsNum() < Constants.CLOUDSNUM) {
			randomCloud();
		}
	}
	
	public void goldenCloud() {
		int sizex = randsrc.getRand(15) + 5;
		int sizey = randsrc.getRand(9) + 5;
		double coord[] = randomSpot();
		GoldenCloud gc = new GoldenCloud(orient, randsrc, container, coord[0], coord[1], sizex, sizey);
		
		//if on screen, move it out, so it enters the screen soon
		while(gc.onScreen()) {
			gc.moveBackwards();
		}
		
		container.setGoldenCloud(gc);
	}
	
	public void randomCloud() {
		int c = randsrc.getRand(4);
		Color color = Color.red;
		switch(c) {
		case 0: color = Color.red; break;
		case 1: color = Color.white; break;
		case 2: color = Color.blue; break;
		case 3: color = Color.green; break;
		}
  
		int sizex = randsrc.getRand(15) + 5;
		int sizey = randsrc.getRand(9) + 5;
		
		double coord[] = randomSpot();
		
		Cloud cloud;
		cloud =	new Cloud(orient, randsrc, coord[0], coord[1], sizex, sizey, color);
		
		//if on screen, move it out, so it enters the screen soon
		while(cloud.onScreen()) {
			cloud.moveBackwards();
		}
		
		container.addCloud(cloud);
	}
}
