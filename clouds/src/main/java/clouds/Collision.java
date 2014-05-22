package clouds;

public class Collision {
	public Direction dir; //collision impact direction
	public double coord; //cloud's edge coordinate
	public double vel; //cloud's (edge) velocity
	
	public Collision(Direction dir, double coord, double vel) {
		this.dir = dir;
		this.coord = coord;
		this.vel = vel;
	}
}
