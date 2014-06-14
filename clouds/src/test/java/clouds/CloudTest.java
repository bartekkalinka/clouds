package clouds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import clouds.rand.RandomSource;
import clouds.rand.RandomSourceImpl;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CloudTest {
	
	private Orientation orientMock;
	private RandomSource randSrc;
	
	public void setUp(double zoom, double [] scrOnMapPos) {
		orientMock = mock(Orientation.class);
		when(orientMock.getZoom()).thenReturn(zoom);
		when(orientMock.getScrOnMapX()).thenReturn(scrOnMapPos[0]);
		when(orientMock.getScrOnMapY()).thenReturn(scrOnMapPos[1]);
		when(orientMock.getMapMinX()).thenReturn(scrOnMapPos[0]-Constants.MAPSIZEX/2);
		when(orientMock.getMapMinY()).thenReturn(scrOnMapPos[1]-Constants.MAPSIZEY/2);

		randSrc = new RandomSourceImpl();		
	}

	@Test
	public void orientation() {
		setUp(.75, new double [] {0, 0});

		//test
		Cloud cloud = new Cloud(orientMock, randSrc, 0, 0, 1, 1, 
				(new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.red);

		assertTrue(cloud.onScreen());
		assertTrue(cloud.onMap());
		
		int halfScreenOnMapY = (new Double(Constants.HEIGHT / (orientMock.getZoom() * 2))).intValue();
		
		cloud = new Cloud(orientMock, randSrc, 
				0, -halfScreenOnMapY - 10 * Constants.MAPTILESIZE,
				1, 1, (new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.red);
		
		assertFalse(cloud.onScreen());
		assertTrue(cloud.onMap());
		
		cloud = new Cloud(orientMock, randSrc, Constants.MAPSIZEX / 2 + 10, 0, 
				1, 1, (new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.red);
		
		assertFalse(cloud.onScreen());
		assertFalse(cloud.onMap());
		
	}
	
	private double[] measureVelocity(double[] pos1, double[] pos2) {
		return new double[] {pos2[0] - pos1[0], pos2[1] - pos1[1]};
	}
	
	@Test
	public void update() {
		setUp(.75, new double [] {10, 10});

		//Velocity is constant
		double startPos[] = {0, 0};
		Cloud cloud = new Cloud(orientMock, randSrc, startPos[0], startPos[1], 1, 1, 
				(new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.red);
		
		cloud.update();
		double pos2[] = cloud.getPos();
		double vel1[] = measureVelocity(startPos, pos2);
		cloud.update();
		double vel2[] = measureVelocity(pos2, cloud.getPos());
		assertEquals(vel1[0], vel2[0], Constants.EPSILON);
		assertEquals(vel1[1], vel2[1], Constants.EPSILON);
		
		//Velocity depends on weight/size
		startPos = new double[] {0, 0};
		cloud = new Cloud(orientMock, randSrc, startPos[0], startPos[1], 1, 1, 
				(new ShapeGenerator(randSrc, 5, 5)).generateShape(), Color.red);
		
		Cloud cloud2 = new Cloud(orientMock, randSrc, startPos[0], startPos[1], 1, 1, 
				(new ShapeGenerator(randSrc, 20, 20)).generateShape(), Color.red);
		
		cloud.update();
		cloud2.update();
		
		double cloudVel[] = measureVelocity(startPos, cloud.getPos());
		double cloud2Vel[] = measureVelocity(startPos, cloud2.getPos());
		assertTrue(cloud2Vel[0] < cloudVel[0]);
		assertTrue(cloud2Vel[1] < cloudVel[1]);
		
		//Top side collision with player results in upwards velocity decrement
		startPos = new double[] {0, 0};
		boolean shape[][] = {{true, true, true}, {true, true, true}, {true, true, true}};
		Shape cloudShape = new Shape(3, 3, shape);
		cloud = new Cloud(orientMock, randSrc, startPos[0], startPos[1], 0.05, -1, 
				cloudShape, Color.red);
		
		//mock collision with Player; 0.1 - Player's bottom coordinate AFTER movement
		cloud.detailedCollisions(0, -Constants.MAPTILESIZE, 3 * Constants.MAPTILESIZE, 0.1, 0, 0.1);
		
		cloud.update();
		pos2 = cloud.getPos();
		vel1 = measureVelocity(startPos, pos2);
		cloud.update();
		vel2 = measureVelocity(pos2, cloud.getPos());
		
		assertEquals(vel1[0], vel2[0], Constants.EPSILON); //no change in horizontal velocity
		assertTrue(Math.abs(vel2[1]) < Math.abs(vel1[1])); //slows down in going upwards
		
	}
	
	@Test
	public void draw() {
		setUp(.75, new double [] {10, 10});
		
		//mockist approach
		Graphics graphMock = mock(Graphics.class);
		Cloud cloud = new Cloud(orientMock, randSrc, 0, 0, 1, 1, 
				(new ShapeGenerator(randSrc, 5,5)).generateShape(), Color.blue);
		cloud.draw(graphMock);
		verify(graphMock).drawImage((Image) anyObject(), anyInt(), anyInt(), anyInt(), anyInt(), (ImageObserver) isNull());
		
		//statist approach
		BufferedImage imgBuff = new BufferedImage(Constants.WIDTH, Constants.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics gfxBuff = imgBuff.getGraphics();
		boolean shape[][] = {{true, false}, {false, true}};
		Shape cloudShape = new Shape(2, 2, shape);
		cloud = new Cloud(orientMock, randSrc, 0, 0, 1, 1, cloudShape, Color.blue);
		cloud.draw(gfxBuff);
		int pix = imgBuff.getRGB(cloud.getScrX(), cloud.getScrY());
		assertEquals(Color.blue.getRGB(), pix);
		pix = imgBuff.getRGB(cloud.getScrX() + cloud.getTileSize() - 1, cloud.getScrY());
		assertEquals(Color.blue.getRGB(), pix);
		pix = imgBuff.getRGB(cloud.getScrX() + cloud.getTileSize(), cloud.getScrY());
		assertEquals(0, pix);
	}

	//TODO test GameObject

}
