package clouds;

public interface Orientation {
	/*
	 * @return zoom factor of map displayed on screen 
	 * (<1 means objects look smaller than on map, >1 objects look larger than on map)
	 */	
	public double getZoom();
	
	/*
	 * @return middle of the screen in map coordinates - x coordinate
	 */
	public double getScrOnMapX();
	
	/*
	 * @return middle of the screen in map coordinates - y coordinate
	 */	
	public double getScrOnMapY();
	
	/*
	 * @return left edge of map buffer in map coordinates
	 */
	public double getMapMinX();

	/*
	 * @return top edge of map buffer in map coordinates
	 */
	public double getMapMinY();
}
