
import java.applet.Applet;
import java.awt.*;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Graphics;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.*;

/*
Lens Applet	Class
*/

public final class DuriusLens	extends	WrApp	implements Runnable	{
	private	Screen32 screenarea, texture;
	private	Lens lens;
	private	int	texturewidth,	textureheight;
	private	int	mousex,	mousey;
	private	double xadd, yadd, span	= 1;
	private	boolean	boolMouse =	true;
	private	boolean	boolNoise =	true;
	private	boolean	boolMouseActive	= false;
	private	boolean	boolMouseWasActive = false;
	
	public final void init() {
		intAppKey =	13214;
		super.init();
		
		screenarea = new Screen32(intAppletWidth, intAppletHeight);
		screenarea.clear(intBackgroundColor);
		dcm	=	new	DirectColorModel(32, 0xff0000, 0xff00, 0xff);
		mis	=	new	MemoryImageSource(screenarea.getwidth(), screenarea.getheight(),
									  dcm,	screenarea.getdata(), 0, screenarea.getwidth());
		//		showStatus(loading);
		img	= createImage(mis);

		s =	getParameter("image");
		Image im = getImage(getDocumentBase(), s);
		MediaTracker m = new MediaTracker(this);
		m.addImage(im,0);
		try	{ m.waitForAll(); }	catch(InterruptedException e)	{}
		if(m.isErrorAny()) { boolImageError	= true;	strBrokenImageName = s;	boolInitialized	= true;	return;} 

		texturewidth = im.getWidth(this);
		textureheight	=	im.getHeight(this);

		int	lightsizex = 100;
		int	lightsizey = 100;
		int	xadder = 19;
		int	yadder = 29;

		s =	getParameter("lenssizex"); if (s !=	null) lightsizex = Integer.parseInt(s);
		s =	getParameter("lenssizey"); if (s !=	null) lightsizey = Integer.parseInt(s);
		s =	getParameter("xadd"); if (s	!= null) xadder	= Integer.parseInt(s);
		s =	getParameter("yadd"); if (s	!= null) yadder	= Integer.parseInt(s);
		xadd = (double)xadder;
		yadd = (double)yadder;
		xadd = xadd/1000;
		yadd = yadd/1000;

		s	=	getParameter("span");if	(s != null)	span = ((double)(Integer.parseInt(s)))/100;
		if (span < 0.05) span =	0.05;

		s =	getParameter("mouse");	
		if (s != null) {
			if(Integer.parseInt(s) == 0)
				boolMouse =	false;
		}
		
		s =	getParameter("noise");	
		if (s != null) {
			if(Integer.parseInt(s) != 0) {
				boolNoise =	true;
			} else boolNoise = false;
		}
		
		texture	=	new	Screen32(texturewidth,textureheight);
		lens = new Lens(lightsizex,	lightsizey,	xadd, yadd,	intBackgroundColor);
		s =	getParameter("depth"); if (s!=null)	lens.depth = Integer.parseInt(s);
		lens.createPhongDot();
		
		texture.load(im);
		screenarea.copy(texture);

		boolInitialized	= true;
		showStatus(" ");

		
	}

	public final void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		if (boolMouse) {
			boolMouseActive	= true;
		}
	}

	public final void mouseExited(MouseEvent e)	{
		super.mouseExited(e);
		if (boolMouse) {
			boolMouseActive	= false;
		}
	}

	public final void mouseMoved(MouseEvent	e) {
		super.mouseMoved(e);
		if(boolMouse ==	true) {	
			int	x =	e.getX();
			int	y =	e.getY();
			mousex = x;
			mousey = y;
		}
	}

	public final void render() {
		if(boolImageError)	{}
		else {
			if (boolMouseActive	== false) {
				if (boolNoise) {
					lens.addFrame(screenarea.width,	screenarea.height, span);
				} else {
					lens.setpos(1000, 1000,	true);
				}
			} else {
				lens.setpos(mousex,	mousey,	true);
			}
			lens.restoreOld2(texture, screenarea);
			if (boolNoise == false && boolMouseActive == false)	{} else	{
				lens.calcPersp(screenarea, texture);
			}
		}
		

		img.flush();
		gfx.drawImage(img, 0,	0, null);

	}
}

/* End Waterpic	*/