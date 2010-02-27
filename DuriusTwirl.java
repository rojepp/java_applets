
import java.applet.Applet;
import java.awt.*;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Graphics;
//import java.awt.Event;
import java.net.URL;
import java.awt.image.*;

/*
Waterpic Applet Class
*/

public final class DuriusTwirl extends WrApp implements Runnable {
	private Screen32 screenarea, texture;
	private Light white;
	private double sqrt[];


	private int texturewidth, textureheight, light = 128, ang = 40;
	private double angle, xadd, yadd, counter2, counter3, counter;

	public final void init() {
		intAppKey = 17565;
		super.init();
		screenarea = new Screen32(intAppletWidth, intAppletHeight);
		screenarea.clear(intBackgroundColor);				
		dcm = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
		mis = new  MemoryImageSource(screenarea.getwidth(), screenarea.getheight(),
									 dcm, screenarea.getdata(), 0, screenarea.getwidth() );
		img = createImage(mis);

		s = getParameter("image");

		Image im = getImage( getDocumentBase(), s );
		MediaTracker m = new MediaTracker(this);
		m.addImage(im,0);
		try { m.waitForAll();
		} catch(InterruptedException e) {}
		if(m.isErrorAny()) { boolImageError = true; strBrokenImageName = s;}
		texturewidth = im.getWidth(this);
		textureheight = im.getHeight(this);

		counter = 0;
		angle = 0;
		s = getParameter("angle");  if (s != null) ang = Integer.parseInt(s);

		int lightsize = 70;
		s = getParameter("lightsize"); if (s != null) lightsize = Integer.parseInt(s);
		s = getParameter("light"); if (s != null) light = Integer.parseInt(s);

		texture = new Screen32(texturewidth,textureheight);

		white = new Light(lightsize,15.0);
		white.createPhongBall(0xcc,0xcc,0xcc);
		texture.load(im);

		boolInitialized = true;

	}

	public final void render() {
		twirl();

		if (light != 0) {
			white.addFrame(screenarea.width,screenarea.height, 1.0);
			white.illuminate(screenarea, screenarea);
		}

		angle = Math.sin(counter)*ang;
		xadd = Math.sin(counter2)*(intAppletWidth>>1);
		yadd = Math.cos(counter3)*(intAppletHeight>>1);

		counter -= 0.054;
		counter2 += 0.038;
		counter3 += 0.041;
		img.flush();
		gfx.drawImage(img, 0, 0, null);


	}

	private final void twirl() {
		int i, j, k, xcenter, ycenter, y, x, f1, f3;
		double f7, f8, f9, f11, f6, f4, f5;

		//		double f = 0.0D;
		//	double f2 = 0.0D;
		double f10 = intAppletWidth / 2;

		if(f10 > (double)(intAppletHeight / 2)) f10 = intAppletHeight / 2;
		xcenter = intAppletWidth / 2+(int)xadd;
		ycenter = intAppletHeight / 2+(int)yadd;
		for(y = 0; y < intAppletHeight; y++) {
			for(x = 0; x < intAppletWidth; x++) {
				f7 = x - xcenter;
				f8 = y - ycenter;
				//            f6 = sqrt[x+y*intAppletWidth];
				f6 = Math.sqrt(f7 * f7 + f8 * f8);
				//						f6 = (f7 + f8 );
				f9 = (((angle - (f6 * angle) / f10) * 3.141592654) / 180D);
				f11 = Math.sin(f9);
				f9 = Math.cos(f9);

				f1 = (int)((f7 * f9 - f8 * f11) + xcenter);
				f3 = (int)(f7 * f11 + f8 * f9 + ycenter);
				if(f1 > 0 && f1 < (intAppletWidth - 1) && f3 > 0 && f3 < (intAppletHeight - 1))	screenarea.data[screenarea.ytab[y]+x] = texture.data[texture.ytab[f3]+f1];
				else 	screenarea.data[screenarea.ytab[y]+x] = intBackgroundColor;
			}

		}
	}

}

/* End Waterpic */