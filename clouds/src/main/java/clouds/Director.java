package clouds;

import java.awt.Color;

public class Director {

	private RandomSource randsrc;
	private Orientation orient;
	private Container container;
	
	public Director(Orientation orient, RandomSource randsrc, Container container) {
		this.randsrc = randsrc;
		this.orient = orient;
		this.container = container;
		
		for(int i=0; i<Constants.CLOUDSNUM; i++) {
			standardCloud();
		}
		
		goldenCloud();
	}
	
	public void update() {
		if(container.getCloudsNum() < Constants.CLOUDSNUM) {
			standardCloud();
		}
	}
	
	private void goldenCloud() {
		GoldenCloud gc = (GoldenCloud)randomCloud(true, null);
		container.setGoldenCloud(gc);
	}
	
	/* 
	 * Create a new standard cloud with many parameters taken from random numbers source
	 * 
	 */
	private void standardCloud() {
		int c = randsrc.getRand(4);
		Color color = Color.red;
		switch(c) {
		case 0: color = Color.red; break;
		case 1: color = Color.white; break;
		case 2: color = Color.blue; break;
		case 3: color = Color.green; break;
		}
  
		Cloud cloud = randomCloud(false, color);
		
		container.addCloud(cloud);
	}
	
	private Cloud randomCloud(boolean golden, Color color) {
		int size[] = randomSize();
		double coord[] = randomSpot();
		double vel[] = randomVelocity();
		ShapeGenerator sg = new ShapeGenerator(randsrc, size[0], size[1]);
		Shape shape = sg.generateShape();
		
		Cloud cloud = null;
		
		if(golden) {
			cloud = new GoldenCloud(orient, randsrc, container, coord[0], coord[1], 
					vel[0], vel[1],	shape);
		}
		else {
			cloud =	new Cloud(orient, randsrc, coord[0], coord[1], vel[0], vel[1], 
					shape, color);
		}
		
		//if on screen, move it out, so it enters the screen soon
		while(cloud.onScreen()) {
			cloud.moveBackwards();
		}
		
		return cloud;
	}
	
	private int[] randomSize() {
		int[] size = new int[2];
		size[0] = randsrc.getRand(15) + 5;
		size[1] = randsrc.getRand(9) + 5;		
		return size;
	}
	
	private double[] randomVelocity() {
		double vel[] = new double[2];
		do {
			vel[0] = -5 + (randsrc.getRand(11));
			vel[1] = -5 + (randsrc.getRand(11));
		} while((vel[0]==0) && (vel[1]==0));
		return vel;		
	}
	
	private double[] randomSpot() {
		double coord[] = new double[2];
		coord[0] = randsrc.getRand(Constants.MAPSIZEX) + orient.getMapMinX();
		coord[1] = randsrc.getRand(Constants.MAPSIZEY) + orient.getMapMinY();
		return coord;
	}	
}
