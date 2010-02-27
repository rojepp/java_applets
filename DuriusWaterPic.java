
import java.applet.Applet;
import java.awt.*;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Graphics;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.*;

/*
Waterpic Applet Class
*/

public final class DuriusWaterPic extends WrApp implements Runnable 
{
	private Screen32 screenarea, texture;
	private Waterpic water;
	int texturewidth, textureheight, timeleft;
	private boolean noise, mouse, timer;
	private int scale = 100;
						   
	public void init() {
		intAppKey = 15637;
		super.init();	

		screenarea = new Screen32(intAppletWidth, intAppletHeight);
		screenarea.clear(intBackgroundColor);

		dcm = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
		mis = new MemoryImageSource(screenarea.getwidth(), screenarea.getheight(),
									dcm, screenarea.getdata(), 0, screenarea.getwidth() );

		mis.setAnimated(true);
		mis.setFullBufferUpdates(true);
		img = createImage(mis);

		showStatus(strLoading);

		s = getParameter("waterscale"); if (s != null) scale = Integer.parseInt(s);

		texture = new Screen32(intAppletWidth, intAppletHeight);
//		water = new Waterpic(intAppletWidth, intAppletHeight);
		water = new Waterpic(intAppletWidth*scale/100, intAppletHeight*scale/100);
//		water = new Waterpic(300, 220);

		noise = false;
		timer = false;
		timeleft = 0;
		mouse = true;

		s = getParameter("mouse");	
		if (s != null) {
			water.dotsize = Integer.parseInt(s);
			if(Integer.parseInt(s) == 0)
				mouse = false;
		}
		s = getParameter("noise");	
		if (s != null) {
			water.ndotsize = Integer.parseInt(s);
			if(Integer.parseInt(s) != 0) {
				s =	getParameter("timer");
				if (s != null) {
					if (Integer.parseInt(s) >= 1) {
						timer = true; timeleft = Integer.parseInt(s); 
					}
				}
				noise = true;
			}
		}

		s = getParameter("dim"); if (s != null) water.dim = Integer.parseInt(s);
		s = getParameter("strength"); if (s != null) water.dotdepth	= Integer.parseInt(s);
		
		s = getParameter("image");

		Image im = getImage( getDocumentBase(), s );
		MediaTracker m = new MediaTracker(this);
		m.addImage(im,0);
		try { 
			m.waitForAll();
		} catch(InterruptedException e) {
			showStatus("InterruptedException");
		}
		if(m.isErrorAny()) { 
			boolImageError = true; 
			strBrokenImageName = s;
			boolInitialized = true;
			return;
		} 
		
		texturewidth = im.getWidth(this);
		textureheight = im.getHeight(this);
		texture.load(im);
		
		boolInitialized = true;
		showStatus("");
		return;
	}
	
	public final void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if(mouse) { 
			int x = e.getX();
			int y = e.getY();
			x = x - (water.dotsize / 2);
			y = y - (water.dotsize / 2);
			if(x>(intAppletWidth-water.dotsize)) x=intAppletWidth-water.dotsize;
			if(y>(intAppletHeight-water.dotsize)) y=intAppletHeight-water.dotsize;
			if(x<0) x=0;
			if(y<0) y=0;
			water.setDot(x, y, water.dotdepth, water.dotsize, intAppletWidth, intAppletHeight);
		}
	}

	public final void render() {
		water.flip();
		if(noise) {
			if(timer) {
				if(timeleft > 0) {
					water.setDot((int)((Math.random()*(water.width>>1)+(water.width>>2))-(water.ndotsize>>1)), (int)((Math.random()*(water.height>>1)+(water.height>>2))-(water.ndotsize>>1)), water.dotdepth/2, water.ndotsize, water.width, water.height);
					timeleft -= lngFPS;
				} else { }
			} else water.setDot((int)((Math.random()*(water.width>>1)+(water.width>>2))-(water.ndotsize>>1)), (int)((Math.random()*(water.height>>1)+(water.height>>2))-(water.ndotsize>>1)), water.dotdepth/2, water.ndotsize, water.width,water.height);
		}
		
		water.render(screenarea, texture);
		mis.newPixels();
//		img.flush();
		gfx.drawImage(img, 0, 0, null); 
	}
}
