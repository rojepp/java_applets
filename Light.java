/**
 * Class for light.
 *
 * @author Robert Bögniel
 */

public final class Light extends Screen32 {
	int xpos, ypos;
	int oldx, oldy;
	private double dblXDegree = 0, dblYDegree = 0, dblXAdd = 0, dblYAdd = 0;
	private int phongTab[];

	// public int hwidth, hheight; // HalfWidth, HalfHeight. (used for centering..)

	//Constructor
	public Light(int w,double dblDelay) {
		super(w, w);
		dblXAdd = (Math.random()-0.5);
		if (dblXAdd < 0.09 || dblXAdd >-0.09) dblXAdd += 0.09;
		dblXAdd = dblXAdd/dblDelay;
		dblYAdd = (Math.random()-0.5);
		if (dblYAdd < 0.09 || dblYAdd >-0.09) dblYAdd += 0.09;
		dblYAdd = dblYAdd/dblDelay;

		xpos = oldx = ypos = oldy = 0;
		phongTab = new int[256];
	}

	public final void addFrame(int w, int h, double span) {
		this.oldx = this.xpos;
		this.oldy = this.ypos;
		dblXDegree += dblXAdd;
		dblYDegree += dblYAdd;
		int spanw = (int)(w*span);
		int spanh = (int)(h*span);
		this.xpos = (int)(Math.sin(dblXDegree)*(spanw>>1))+(w>>1);
		this.ypos = (int)(Math.sin(dblYDegree)*(spanh>>1))+(h>>1);
		
	}

	public final void createPhongBall(int r, int g, int b) {
		double nx, ny, nz;
		double nr, ng, nb;
		int vr, vg, vb;
		int phongPal[];
		double intensity;

		int reflect = 512;
		int ambient = 0;

		phongPal = new int[256];
		for(int h=0; h<256; h++) {
			intensity = Math.cos((255 - h)/512.0 * Math.PI);

			nr = r * ambient / 255 + r * intensity + Math.pow(intensity, reflect) * 200;
			ng = g * ambient / 255 + g * intensity + Math.pow(intensity, reflect) * 200;
			nb = b * ambient / 255 + b * intensity + Math.pow(intensity, reflect) * 200;

			vr = (int)nr;
			vg = (int)ng;
			vb = (int)nb;
			if (vr>255) vr = 255;
			if (vg>255) vg = 255;
			if (vb>255) vb = 255;
			phongPal[h] = vb|(vg<<8)|(vr<<16);
		}

		for(int i=0; i<this.height; i++) {
			for(int j=0; j<this.width; j++) {
				nx = (j-hwidth);
				nx = nx/hwidth;
				ny = (i-hheight);
				ny = ny/hheight;
				nz = 1 - Math.sqrt(nx*nx+ny*ny);
				if(nz<0) nz = 0;

				vr = (int)(nz*255);
				this.data[j+this.ytab[i]] = phongPal[vr];

			}
		}

	}


	public final void illuminateAmbient(Screen32 source, Screen32 dest, int r, int g, int b) {
		int temp, tr,tg,tb;
		for(int i = 0; i<dest.getwidthheight(); i++) {
			temp = source.data[i];
			tr = ((temp>>16)&255)*r;
			tg = ((temp>>8)&255)*g;
			tb = ((temp)&255)*b;
			if(tr>65535) tr = 65535;
			if(tg>65535) tg = 65535;
			if(tb>65535) tb = 65535;

			dest.data[i] = ( ((tr<<8)&0xff0000) + (tg&0xff00) + ((tb>>8)&0xff) );
		}
	}

	public final void restoreOld(Screen32 source, Screen32 dest) {
		int ssize = source.getwidth()*source.getheight();
		int sourcemodulo = source.getwidth()-this.width;
		int cnt = oldx - hwidth+((oldy - hheight) * source.width);
		for(int i = 0; i<this.height; i++) {
			for(int j = 0; j<this.width; j++) {
				if((cnt>=0) && (cnt<ssize)) dest.data[cnt] = source.data[cnt];
				cnt++;
			}
			cnt += sourcemodulo;
		}
	}


	public final void illuminate(Screen32 source, Screen32 dest) {
		int lr, lg, lb;
		int dr, dg, db;

		int ssize = source.width*source.height;
		int index = xpos-hwidth+((ypos-hheight)*source.width);

		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {

				if((index>=0) && (index<ssize)) {
					if( ((xpos-hwidth+j)<source.width) && ((xpos-hwidth+j)>=0)) {

						lb = this.data[this.ytab[i]+j];
						lr = (lb>>16)&255;
						lg = (lb>>8)&255;
						lb = lb&255;

						db = dest.data[index];
						dr = (db>>16)&255;
						dg = (db>>8)&255;
						db &= 255;

						dr += lr;
						dg += lg;
						db += lb;

						if(dr>255) dr=255;
						if(dg>255) dg=255;
						if(db>255) db=255;

						dest.data[index] = (dr<<16)+(dg<<8)+db;
					}
				}
				index++;
			}
			index += source.width-this.width;
		}

	}

	public final void illuminateBump(Screen32 source, Screen32 dest, BumpMap b) {
		int lr, lg, lb;
		int dr, dg, db;
		int mDeltaX, mDeltaY, lightindex;

		int ssize = source.width*source.height;
		int index = xpos-hwidth+((ypos-hheight)*source.width);

		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {

				if((index>=0) && (index<ssize)) {
					if( ((xpos-hwidth+j)<source.width) && ((xpos-hwidth+j)>=0)) {

						db = dest.data[index];
						dr = (db>>16)&255;
						dg = (db>>8)&255;
						db &= 255;

						mDeltaX = j-hwidth -(b.mDeltaX[index] - hwidth);
						mDeltaY = i-hheight-(b.mDeltaY[index] - hheight);
						
						if (mDeltaX < 0 || mDeltaX >= this.width) mDeltaX = 0;
						if (mDeltaY < 0 || mDeltaY >= this.width) mDeltaY = 0;
						lightindex = this.ytab[mDeltaY]+mDeltaX;
						dr += ((this.data[lightindex]>>16)&255);
						dg += ((this.data[lightindex]>>8)&255);
						db += (this.data[lightindex]&255);

						if(dr>255) dr=255;
						if(dg>255) dg=255;
						if(db>255) db=255;

						dest.data[index]=(dr<<16)+(dg<<8)+db;
					}
				}
				index++;
			}
			index += source.width-this.width;
		}

	}



}

/*
Light class end here.
*/
