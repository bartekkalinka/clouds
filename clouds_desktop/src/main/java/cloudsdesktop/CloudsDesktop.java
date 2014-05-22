package cloudsdesktop;

import java.awt.Canvas;

import javax.swing.JFrame;

import clouds.Clouds;
import clouds.Constants;

public class CloudsDesktop {
	
	public static void main(String args[]) {
		DesktopInterface di = new DesktopInterface();
		Clouds p = new Clouds(di);
		p.play();
	}
	
}
