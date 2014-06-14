package cloudsdesktop;

import clouds.Clouds;

public class CloudsDesktop {
	
	public static void main(String args[]) {
		DesktopInterface di = new DesktopInterface();
		Clouds p = new Clouds(di);
		p.play();
	}
	
}
