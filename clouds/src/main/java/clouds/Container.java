package clouds;

import java.util.List;

public interface Container {
	public void addTile(FlyingTile tile);
	
	public void addCloud(Cloud c);
	
	public void setGoldenCloud(GoldenCloud goldenCloud);	
	
	//TODO add other types of gameobjects here?
	
	public int getCloudsNum();
	
	public List<Collision> cloudCollisions(double x1, double y1, double x2, double y2, 
			double velx, double vely);
}
