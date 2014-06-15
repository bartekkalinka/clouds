package clouds;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import clouds.rand.RandomSource;
import clouds.rand.RandomSourceImpl;

@RunWith(JUnit4.class)
public class GameObjectTest {

	
	private Orientation orientMock;
	private RandomSource randSrc;	
	private Container containMock;
	private GameResult resultMock;
	private CloudsInterface interfMock;
	
	//TODO remove duplication with CloudTest
	public void setUp(double zoom, double [] scrOnMapPos) {
		orientMock = mock(Orientation.class);
		when(orientMock.getZoom()).thenReturn(zoom);
		when(orientMock.getScrOnMapX()).thenReturn(scrOnMapPos[0]);
		when(orientMock.getScrOnMapY()).thenReturn(scrOnMapPos[1]);
		when(orientMock.getMapMinX()).thenReturn(scrOnMapPos[0]-Constants.MAPSIZEX/2);
		when(orientMock.getMapMinY()).thenReturn(scrOnMapPos[1]-Constants.MAPSIZEY/2);

		randSrc = new RandomSourceImpl();
		
		containMock = mock(Container.class);
		when(containMock.cloudCollisions(anyDouble(), anyDouble(),anyDouble(), anyDouble(),anyDouble(), anyDouble())
				).thenReturn((List<Collision>) new ArrayList<Collision>());
		resultMock = mock(GameResult.class);
		interfMock = mock(CloudsInterface.class);
		Image[] images = new BufferedImage[5];
		images[0] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		when(interfMock.getImages()).thenReturn(images);
	}
	
	/*
	 * Creates one instance of each GameObject subclass
	 * with nonzero velocity
	 * 
	 * @return list of GameObjects instances
	 */
	private List<GameObject> getObjects() {
		List<GameObject> objects = new ArrayList<GameObject>();
		
		Cloud cloud = new Cloud(orientMock, randSrc, 0, 0, 1, 1, 
				(new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.red);
		objects.add(cloud);
		
		Player player = new Player(orientMock, containMock, resultMock, interfMock);
		player.update(); //to increase velocity by falling a little
		objects.add(player);
		
		FlyingTile tile = new FlyingTile(orientMock, 0, 0, 1, 1, Color.blue);
		objects.add(tile);
		
		return objects;
	}
	
	@Test
	public void move() {
		setUp(.75, new double[] {0, 0});
		
		for(GameObject obj : getObjects()) {
			double[] pos1 = obj.getPos();
			obj.move();
			double [] pos2 = obj.getPos();
			//there should be some movement
			assertFalse(pos1[0] == pos2[0] && pos1[1] == pos2[1]);
			double [] vel = measureVelocity(pos1, pos2);
			obj.move();
			double [] pos3 = obj.getPos();
			double [] vel2 = measureVelocity(pos2, pos3);
			//velocity is constant
			assertEquals(vel[0], vel2[0], Constants.EPSILON);
		}

	}
	
	//TODO remove duplication with CloudTest
	private double[] measureVelocity(double[] pos1, double[] pos2) {
		return new double[] {pos2[0] - pos1[0], pos2[1] - pos1[1]};
	}
	
	//TODO more tests

}
