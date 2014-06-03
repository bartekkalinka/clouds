package clouds;

import java.awt.Color;

public class GoldenCloud extends Cloud {
	
	private double vxNext;
	private double vyNext;
	Container container;
	
	GoldenCloud(Orientation orient, RandomSource randsrc, Container container, double ax, double ay, int aSizeX, int aSizeY) {
		super(orient, randsrc, ax, ay, aSizeX, aSizeY, Color.yellow);
		this.container = container;
		vxNext = vx;
		vxNext = vy;
	}
	
	public void update() {
		super.update();
		changeVel();
		emit();
	}

	private void changeVel() {
		vx = (vx + vxNext) / 2;
		vy = (vy + vyNext) / 2;
		if(randsrc.getRand(1000) == 6) {
			vxNext = -5 + ((double)randsrc.getRand(100)) / 10;
			vyNext = -5 + ((double)randsrc.getRand(100)) / 10;
		}
	}

	private double[] randomVector(int speed) {
		double velx = 0;
		double vely = 0;
		do {
			velx = -5 + (randsrc.getRand(11));
			vely = -5 + (randsrc.getRand(11));
		} while((vx==0) && (vy==0));
		double factor = Math.sqrt(((double)(speed * speed)) / (velx * velx + vely * vely));
		velx *= factor;
		vely *= factor;
		double[] vel = new double[2];
		vel[0] = velx;
		vel[1] = vely;
		return vel;
	}
	
	private void emit() {
		int prob = (int)(Constants.GCLOUDEMITPROB * 1000);
		if(randsrc.getRand(1000) <= prob) {
			double[] vel = randomVector(Constants.GCLOUDEMITSPEED);
			int[] mt = getMiddleTile();
			container.addTile(new FlyingTile(orient, x + (mt[0] - 1) * Constants.MAPTILESIZE, 
					y + (mt[1] - 1) * Constants.MAPTILESIZE, vel[0], vel[1], Color.yellow));
		}
	}
	
	public boolean onMap() {
		return !destroyed;
	}
}
