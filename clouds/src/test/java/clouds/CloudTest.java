package clouds;

import java.awt.Color;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CloudTest {

	class RandomStub implements RandomSource
	{
		private Random random;

		//TODO remove duplication with Clouds class
		//TODO use both this and non-random stub version
		public RandomStub() {
			random = new Random(System.currentTimeMillis());
		}

		public int getRand(int n) {
			return random.nextInt(n);
		}
	}

	@Test
	public void orientation() {
		//setup/fixture
		Orientation orientMock = mock(Orientation.class);
		when(orientMock.getZoom()).thenReturn(0.75);
		when(orientMock.getScrOnMapX()).thenReturn((double)0);
		when(orientMock.getScrOnMapY()).thenReturn((double)0);
		when(orientMock.getMapMinX()).thenReturn((double)-Constants.MAPSIZEX/2);
		when(orientMock.getMapMinY()).thenReturn((double)-Constants.MAPSIZEY/2);

		RandomSource randomStub = new RandomStub();
		
		//test
		Cloud cloud = new Cloud(orientMock, randomStub, 0, 0, 5, 5, Color.red);

		assertTrue(cloud.onScreen());
		assertTrue(cloud.onMap());
		
		int halfScreenOnMapY = (new Double(Constants.HEIGHT / (orientMock.getZoom() * 2))).intValue();
		
		cloud = new Cloud(orientMock, randomStub, 
				0, -halfScreenOnMapY - 10 * Constants.MAPTILESIZE,
				5, 5, Color.red);
		
		assertFalse(cloud.onScreen());
		assertTrue(cloud.onMap());
		
		cloud = new Cloud(orientMock, randomStub, Constants.MAPSIZEX / 2 + 10, 0, 5, 5, Color.red);
		
		assertFalse(cloud.onScreen());
		assertFalse(cloud.onMap());
	}

}
