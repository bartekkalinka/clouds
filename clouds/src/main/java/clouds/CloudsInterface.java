package clouds;

import java.awt.image.ImageObserver;
import java.awt.Image;

public interface CloudsInterface {
	public void initGui(Clouds clouds);
	
	public void draw(Clouds clouds);
	
	public Image[] getImages();
	
	//TODO no need for imageObserver?  in Player img.getWidth null works?
	public ImageObserver getImageObserver();
}
