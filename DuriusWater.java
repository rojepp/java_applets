
import java.applet.Applet;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.*;

/*
Waterpic Applet Class
*/

public final class DuriusWater extends WrApp implements Runnable {
	private Screen32 screenarea, texture, opentexture;
	private Water water;
	int texturewidth, textureheight, timeleft;
	private boolean noise, mouse, timer, usestexture = false;

	public void init() {
		intAppKey = 11128;
		super.init();
		screenarea = new Screen32(intAppletWidth, intAppletHeight);
		screenarea.clear(intBackgroundColor);
		dcm = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
		mis = new  MemoryImageSource(screenarea.getwidth(), screenarea.getheight(),
									 dcm, screenarea.getdata(), 0, screenarea.getwidth() );
		img = createImage(mis);

		showStatus(strLoading);

		s = getParameter("image");
		if (s != null) {
			usestexture = true;
			Image im = getImage( getDocumentBase(), s );
			MediaTracker m = new MediaTracker(this);
			m.addImage(im,0);

			try { m.waitForAll(); } catch(InterruptedException e) {}
			if(m.isErrorAny()) { boolImageError = true; strBrokenImageName = s;} 

			texturewidth = im.getWidth(this);
			textureheight = im.getHeight(this);

			opentexture = new Screen32(texturewidth,textureheight);
			opentexture.load(im);
		}
		else {
			texturewidth = 3;
			textureheight = 3;
			opentexture = new Screen32(texturewidth,textureheight);
		}

		water = new Water(intAppletWidth, intAppletHeight);

		noise = false;
		mouse = true;
		timer = false;

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

		s = getParameter("logoheight");
		if (s != null) {
			water.logoheight = 512+(Integer.parseInt(s));
			water.logoheight2 = 512-(Integer.parseInt(s));
		}

		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0;
		s = getParameter("col1"); if (s != null) c1 = Integer.valueOf(s,16).intValue();
		s = getParameter("col2"); if (s != null) c2 = Integer.valueOf(s,16).intValue();
		s = getParameter("col3"); if (s != null) c3 = Integer.valueOf(s,16).intValue();
		s = getParameter("col4"); if (s != null) c4 = Integer.valueOf(s,16).intValue();
		s = getParameter("col5"); if (s != null) c5 = Integer.valueOf(s,16).intValue();

		water.createPalette((c1>>16)&0xff, (c1>>8)&0xff, c1&0xff,
							(c2>>16)&0xff, (c2>>8)&0xff, c2&0xff,
							(c3>>16)&0xff, (c3>>8)&0xff, c3&0xff,
							(c4>>16)&0xff, (c4>>8)&0xff, c4&0xff,
							(c5>>16)&0xff, (c5>>8)&0xff, c5&0xff);

		texture = new Screen32(intAppletWidth, intAppletHeight);

		int currentpos = (intAppletWidth>>1)-(texturewidth>>1)+( (intAppletHeight>>1)-(textureheight>>1) )* intAppletWidth;
		for(int i = 0; i<textureheight; i++) {
			for(int j = 0; j<texturewidth; j++) {
				texture.data[currentpos] = opentexture.data[j+opentexture.ytab[i]];
				currentpos++;
			}
			currentpos += intAppletWidth-texturewidth;
		}

		boolInitialized = true;
		showStatus("");
		
		
		
	}

	public final void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if(mouse) { 
			int x = e.getX();
			int y = e.getY();
			x = x - (water.dotsize / 2);
			y = y - (water.dotsize / 2);
			if(x>(intAppletWidth-water.dotsize))	x=intAppletWidth-water.dotsize;
			if(y>(intAppletHeight-water.dotsize))	y=intAppletHeight-water.dotsize;
			if(x<0)	x=0;
			if(y<0)	y=0;
			water.setDot(x,	y, water.dotdepth,	water.dotsize);
		}
	}

	public final void render() {
		water.flip();
		if(noise) {
			if(timer) {
				if(timeleft > 0) {
					water.setDot((int)((Math.random()*(water.width>>1)+(water.width>>2))-(water.ndotsize>>1)), (int)((Math.random()*(water.height>>1)+(water.height>>2))-(water.ndotsize>>1)), water.dotdepth,	water.ndotsize);
					timeleft -= lngFPS;
				} else { }
			} else water.setDot((int)((Math.random()*(water.width>>1)+(water.width>>2))-(water.ndotsize>>1)), (int)((Math.random()*(water.height>>1)+(water.height>>2))-(water.ndotsize>>1)), water.dotdepth,	water.ndotsize);
		}
		if (usestexture) { water.calcOverlay(screenarea, texture); } else { water.calcWater(screenarea); }
		
		img.flush();
		gfx.drawImage(img, 0, 0, null);
	}
}

/* End Water */