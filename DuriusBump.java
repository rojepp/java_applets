
import java.applet.Applet;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Graphics;
import java.awt.Event;
import java.net.URL;
import java.awt.image.*;
import java.util.StringTokenizer;
/*
Bump Applet Class
*/

public final class DuriusBump extends WrApp implements Runnable {
	private Screen32 screenarea, textureA;
	private BumpMap bump;
	private Light light[];
	private int intBumpHeight = 200;
	private int intLightCount = 0;
	private int intAmbience = 0;
	private double span = 1;
	private boolean boolClearIndividual = false;

	public final void init() {
		Screen32 texture;

		intAppKey = 16133;
		super.init();
		screenarea = new Screen32(intAppletWidth,intAppletHeight);
		screenarea.clear(intBackgroundColor);
		dcm = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
		mis = new MemoryImageSource(screenarea.getwidth(), screenarea.getheight(), 
									dcm, screenarea.getdata(), 0, screenarea.getwidth() );
		img = createImage(mis);
		showStatus(strLoading);
		
		int texturewidth, textureheight;

		intBumpHeight = 200;

		s = getParameter("image");
		Image im = getImage( getDocumentBase(), s );
		MediaTracker m = new MediaTracker(this);
		m.addImage(im,0);
		try { m.waitForAll(); } catch(InterruptedException e) {}
		if(m.isErrorAny()) { boolImageError = true; strBrokenImageName = s;} 

		texturewidth = im.getWidth(this);
		textureheight = im.getHeight(this);

		texture = new Screen32(texturewidth,textureheight);
		textureA = new Screen32(texturewidth,textureheight);

		texture.load(im);

		while (getParameter("light" + String.valueOf(intLightCount+1)) != null ) {
			intLightCount++;
		}

		StringTokenizer tokTmp;
		light = new Light[intLightCount];
		for (int i = 0; i < intLightCount; i++) {
			
			tokTmp = new StringTokenizer(getParameter("light" + String.valueOf(i+1)));
			
			int intSize = 120, intColor = 0xffffff, intDelay = 15;
			if (tokTmp.hasMoreTokens())
				intSize = Integer.parseInt(tokTmp.nextToken());
			if (tokTmp.hasMoreTokens())
				intDelay = Integer.parseInt(tokTmp.nextToken());
			if (tokTmp.hasMoreTokens())
				intColor = Integer.valueOf(tokTmp.nextToken(),16).intValue();

			light[i] = new Light(intSize,(double)intDelay);
			light[i].createPhongBall((intColor>>16)&0xff, (intColor>>8)&0xff, intColor&0xff);
			tokTmp = null;
		}

		s = getParameter("bumpheight");	if (s != null) intBumpHeight = Integer.parseInt(s);
		s = getParameter("ambience"); if (s != null) intAmbience = Integer.valueOf(s,16).intValue();

		s = getParameter("span");if (s != null) span = ((double)(Integer.parseInt(s)))/100;
		if (span < 0.05) span = 0.05;

		light[0].illuminateAmbient(texture, textureA, (intAmbience>>16)&0xff, (intAmbience>>8)&0xff, intAmbience&0xff);
		screenarea.copy(textureA);

		if(intBumpHeight!=0) { bump = new BumpMap(texture, (double)(intBumpHeight)); }

		int intTotalArea = 0;
		for (int i=0;i<intLightCount;i++) {
			intTotalArea += light[i].getwidthheight();
		}

		intTotalArea = (intTotalArea*100) / 70;
		if (intTotalArea < screenarea.getwidthheight()) { boolClearIndividual = true; }
		else { boolClearIndividual = false; }

		boolInitialized = true;
		showStatus("");
		
		
	}
	public final void render() {
		for (int i=0;i<intLightCount;i++) {
			light[i].addFrame(screenarea.width, screenarea.height, span);
		}
		if (boolClearIndividual) {
			for (int i=0;i<intLightCount;i++) {
				light[i].restoreOld(textureA, screenarea);
			}
		} else {
			screenarea.copy(textureA);
		}
		if(intBumpHeight!=0) {
			
			for (int i=0;i<intLightCount;i++) {
				light[i].illuminateBump(textureA, screenarea, bump);
			}
		}
		else {
			for (int i=0;i<intLightCount;i++) {
				light[i].illuminate(textureA, screenarea);
			}
		}
		img.flush();
		gfx.drawImage(img, 0, 0, null);
	}
}

	
/* End Bump */