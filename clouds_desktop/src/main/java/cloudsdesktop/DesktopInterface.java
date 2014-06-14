package cloudsdesktop;

import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import clouds.CloudsInterface;
import clouds.Constants;
import clouds.Clouds;

public class DesktopInterface implements CloudsInterface {
	
	private JFrame f;
	private Canvas c;
	
	public DesktopInterface() {
		f = null;
	}
	
	public void initGui(Clouds clouds) {
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Clouds 0.09");
		f.setResizable(false);
		c = new Canvas();
		c.setSize(Constants.WIDTH, Constants.HEIGHT);
		f.add(c);
		f.pack();
		f.setVisible(true);
		c.addKeyListener(clouds);
	}
	
	public void draw(Clouds clouds) {
		clouds.draw(c.getGraphics(), f);
	}
	
	public Image[] getImages() {
		Image[] images = new BufferedImage[5];		
		try {
		    images[0] = ImageIO.read(getClass().getResourceAsStream("/gfx/player_face.PNG"));
		    images[1] = ImageIO.read(getClass().getResourceAsStream("/gfx/player_left_1.PNG"));
		    images[2] = ImageIO.read(getClass().getResourceAsStream("/gfx/player_left_2.PNG"));
		    images[3] = ImageIO.read(getClass().getResourceAsStream("/gfx/player_right_1.PNG"));
		    images[4] = ImageIO.read(getClass().getResourceAsStream("/gfx/player_right_2.PNG"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return images;
	}
	
	public ImageObserver getImageObserver() {
		return f;
	}
}
