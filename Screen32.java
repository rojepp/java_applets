import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.*;

/**
 * Class for 32-bit image arrays.
 * @author Robert Bögniel
 */


public class Screen32 {
	public int data[];
	public int ytab[];
	public int width, height;
	public int hwidth, hheight;
	private int widthheight, i;

	//Constructor
	public Screen32(int w, int h) {
		width = w;
		height = h;
		widthheight = w*h;
		hheight = h>>1;
		hwidth = w>>1;
		data = new int[widthheight];
		ytab = new int[height];
		int yval = 0;
		for(i = 0; i<height; i++) {
			ytab[i] = yval;
			yval+=width;
		}
	}

	public final void load(Image im) { 
		PixelGrabber pg;
		pg = new PixelGrabber(im, 0,0, width, height, data, 0, width); 
		try { pg.grabPixels(); } catch (InterruptedException e) {
		}
		pg = null;
	}

	public final void copy(Screen32 source) {
		for(i = this.widthheight; --i >= 0; ) this.data[i] = source.data[i]; 
	}
	public final void clear(int c) { for(i = this.widthheight; --i >= 0; ) data[i] = c; }
	public final int getwidth() {return(width); }
	public final int getheight() {return(height); }
	public final int getwidthheight() {return(widthheight); }
	public final int[] getdata() {return(data);}
};

/* End Screen32 */

