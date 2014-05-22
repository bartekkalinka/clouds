package clouds;

import java.util.List;
import java.util.ArrayList;

public class CloudShape {
	private int sizex;
	private int sizey;
	private boolean shape[][];
	private Clouds game;
	
	public CloudShape(Clouds game, int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		this.game = game;
		do {
			generateShape4();
		} while (failBeautyStats());
	}
	
	public boolean[][] getShape() {
		return shape;
	}
	
	private void generateShape4() {
		shape = new boolean[sizex][sizey];
		int [][] noise = getSmoothNoiseTable(scaleNoiseTable(getNoiseTable()));
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				shape[x][y] = (noise[x][y] >= 500);
			}
		}
		
		shape = cutOffLooseFragments(shape);
	}
	
	private int[][] getNoiseTable() {
		int [][] ret = new int[sizex][sizey];
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				ret[x][y] = game.getRand(1000);
			}
		}
		
		return ret;
	}
	
	private void safeNoiseSet(int[][] noise, int x, int y, int value) {
		if(x>=0 && y>=0 && x<sizex && y<sizey) {
			noise[x][y] = value;
		}
	}
	
	private int safeNoiseGet(int[][] noise, int x, int y) {
		if(x>=0 && y>=0 && x<sizex && y<sizey) {
			return noise[x][y];
		}
		else {
			return 0;
		}
	}	
	
	private int[][] scaleNoiseTable(int[][] noise) {
		int [][] ret = new int[sizex][sizey];
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				ret[x][y] = 0;
			}
		}
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				safeNoiseSet(ret, 2 * x, 2 * y, noise[x][y]);
				safeNoiseSet(ret, 2 * x + 1, 2 * y, noise[x][y]);
				safeNoiseSet(ret, 2 * x, 2 * y + 1, noise[x][y]);
				safeNoiseSet(ret, 2 * x + 1, 2 * y + 1, noise[x][y]);
			}
		}
		
		return ret;
	}
	
	private int[][] getSmoothNoiseTable(int[][] noise) {
		int[][] smooth = new int[sizex][sizey];
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				smooth[x][y] = 0;
			}
		}
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				double corners = (safeNoiseGet(noise, x-1, y-1)+safeNoiseGet(noise, x+1, y-1)+safeNoiseGet(noise, x-1, y+1)+safeNoiseGet(noise, x+1, y+1)) / 16;
				double sides = (safeNoiseGet(noise, x-1, y)+safeNoiseGet(noise, x+1, y)+safeNoiseGet(noise, x, y-1)+safeNoiseGet(noise, x, y+1)) / 8;
				double center = safeNoiseGet(noise, x, y) / 4;
				smooth[x][y] = (int)(corners + sides + center);
			}
		}
		
		return smooth;
	}
	
	private int weight(boolean[][] shape) {
		int ret = 0;
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(shape[x][y]) ret++;
			}
		}
		
		return ret;
	}
	
	public int weight() {
		return weight(shape);
	}

	private int[] findFirstPoint(boolean[][] shape) {
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(shape[x][y]) {
					int[] ret = new int[2];
					ret[0] = x;
					ret[1] = y;
					return ret;
				}
			}
		}
		return null;
	}
	
	private int[][] buildNilShape() {
		int[][] ret = new int[sizex][sizey];
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				ret[x][y] = -1;
			}
		}
		
		return ret;
	}
	
	private boolean[][] convert3ValuesShape(int[][] shape) {
		boolean[][] ret = new boolean[sizex][sizey];
		
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				ret[x][y] = (shape[x][y]==1);
			}
		}
		
		return ret;		
	}
	
	private boolean[][] substractShape(boolean[][] fromShape, boolean[][] subShape) {
		for(int x=0; x<sizex; x++) {
			for(int y=0; y<sizey; y++) {
				if(fromShape[x][y] && subShape[x][y]) {
					fromShape[x][y] = false;
				}
			}
		}
		return fromShape;
	}
	
	private void getWholeShape(int[][] currChunk, boolean[][] mapShape, int x, int y) {
		if(x>=0 && y>=0 && x<sizex && y<sizey) {
			if(currChunk[x][y] != -1) {
				return;
			}
			currChunk[x][y] = 0;
			if(mapShape[x][y]) {
				currChunk[x][y] = 1;
				getWholeShape(currChunk, mapShape, x - 1, y - 1);
				getWholeShape(currChunk, mapShape, x - 1, y);
				getWholeShape(currChunk, mapShape, x - 1, y + 1);
				getWholeShape(currChunk, mapShape, x, y - 1);
				getWholeShape(currChunk, mapShape, x, y + 1);
				getWholeShape(currChunk, mapShape, x + 1, y - 1);
				getWholeShape(currChunk, mapShape, x + 1, y);
				getWholeShape(currChunk, mapShape, x + 1, y + 1);				
			}
		}
	}

	private List<boolean[][]> divideIntoWholeShapes(boolean[][] shape) {
		boolean[][] leftOver = shape;
		List<boolean[][]> chunks = new ArrayList<boolean[][]>();
		while(weight(leftOver) > 0) {
			int[] coord = findFirstPoint(leftOver);
			int[][] chunk = buildNilShape();
			getWholeShape(chunk, leftOver, coord[0], coord[1]);
			boolean[][] bChunk = convert3ValuesShape(chunk);
			chunks.add(bChunk);
			leftOver = substractShape(leftOver, bChunk);
		}
		return chunks;
	}
	
	private boolean[][] cutOffLooseFragments(boolean[][] shape) {
		List<boolean[][]> chunks = divideIntoWholeShapes(shape);
		int maxWeight = 0;
		boolean[][] maxChunk = shape;
		for(boolean[][] chunk : chunks) {
			int chunkWeight = weight(chunk);
			if(chunkWeight > maxWeight) {
				maxChunk = chunk;
				maxWeight = chunkWeight;
			}
		}
		return maxChunk;
	}

	private boolean failBeautyStats() {
		int w = weight(shape);
		int s = sizex * sizey;
		return(w <= 6 || (w >= 7 && w <= 12 && s >= 4 * w));
	}
}

