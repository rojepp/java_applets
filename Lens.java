/**
 *
 * Class Lens.
 *
 * @author Robert Bögniel.
 *
 */

public final class Lens {
	public int width, height;
	private double dblXDegree = 0, dblYDegree = 0, dblXAdd = 0, dblYAdd = 0;
	public int depth;
	int data[];
	int ytab[];
	int xpos, ypos;
	int oldx, oldy;
	int hwidth, hheight;
	int intBackgroundColor;
	boolean boolSkipDraw = false;
	//Constructor
	public Lens(int w, int h, double xadd, double yadd, int intBG) {
		dblXAdd = xadd;
		dblYAdd = yadd;
		intBackgroundColor = intBG;
		width = w;
		height = h;
		hwidth = w >> 1;
		hheight = h >> 1;
		xpos = hwidth;
		ypos = hheight;
		oldx = hwidth;
		oldy = hheight;
		depth = 1024;
		data = new int[width*height];

		ytab = new int[height];
		int yval = 0;
		for(int i = 0; i<height; i++) {
			ytab[i] = yval;
			yval+=width;
			}
		
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
	
	
	public final void setpos(int xpo, int ypo, boolean boolUpdateOld) {
		if (boolUpdateOld == true) {
			this.oldx = this.xpos;
			this.oldy = this.ypos;
		}
		this.xpos = xpo;
		this.ypos = ypo;  
		}
/*
	public final void restoreOld(Screen32 source, Screen32 dest) {
		if ((this.oldy == this.ypos) && (this.oldx == this.oldy)) {
			boolSkipDraw = true;
			return;
		}			
		int sSize = source.width*source.height;
		int cnt = oldx - hwidth+((oldy - hheight) * source.width);
		for(int i = 0; i<this.height; i++) {
			for(int j = 0; j<this.width; j++) {
				if((cnt>=0) && (cnt<sSize)) {
					dest.data[cnt] = 0xff00ff; //source.data[cnt];
					}
				cnt++; 
				}  
			cnt += source.width-this.width;
			}
		}
*/
	public final void restoreOld2(Screen32 source, Screen32 dest) {
		
		int intStripXStart, intStripYStart;
		int intStripWidth, intStripHeight;
		int intSourceModulo, intTargetModulo;

		if (this.oldx > this.xpos) {
			intStripXStart = this.oldx+this.hwidth-(this.oldx-this.xpos);
			intStripWidth = this.oldx-this.xpos+2;
		} else {
			intStripXStart = this.oldx-this.hwidth;
			intStripWidth = this.xpos - this.oldx;
		}

		intStripYStart=this.oldy-this.hheight;
		intStripHeight=this.height;		
		
		if (intStripXStart < 0) {
			intStripWidth += intStripXStart;
			intStripXStart = 0;
		}
		if ((intStripXStart+intStripWidth) > source.width) {
			intStripWidth -= ((intStripXStart+intStripWidth) - source.width);
		}

		if (intStripYStart < 0) {
			intStripHeight += intStripYStart;
			intStripYStart = 0;
		}
		if ((intStripYStart+intStripHeight) > source.height) {
			intStripHeight -= ((intStripYStart+intStripHeight) - source.height);
		}
		
		intSourceModulo = source.width - intStripWidth;

		int cnt = intStripXStart + (intStripYStart * source.width);
		for(int i = 0; i<intStripHeight; i++) {
			for(int j = 0; j<intStripWidth; j++) {
				dest.data[cnt] = source.data[cnt];
				cnt++; 
				}  
			cnt += intSourceModulo;
			}

		if (this.oldy > this.ypos) {
			intStripYStart = this.oldy+this.hheight-(this.oldy-this.ypos);
			intStripHeight = this.oldy-this.ypos+2;
		} else {
			intStripYStart = this.oldy-this.hheight;
			intStripHeight = this.ypos - this.oldy;
		}

		intStripXStart=this.oldx-this.hwidth;
		intStripWidth=this.width;

		if (intStripXStart < 0) {
			intStripWidth += intStripXStart;
			intStripXStart = 0;
		}
		if ((intStripXStart+intStripWidth) > source.width) {
			intStripWidth -= ((intStripXStart+intStripWidth) - source.width);
		}

		if (intStripYStart < 0) {
			intStripHeight += intStripYStart;
			intStripYStart = 0;
		}
		if ((intStripYStart+intStripHeight) > source.height) {
			intStripHeight -= ((intStripYStart+intStripHeight) - source.height);
		}
		
		intSourceModulo = source.width - intStripWidth;

		cnt = intStripXStart + (intStripYStart * source.width);
		for(int i = 0; i<intStripHeight; i++) {
			for(int j = 0; j<intStripWidth; j++) {
				dest.data[cnt] = source.data[cnt];
				cnt++; 
				}  
			cnt += intSourceModulo;
			}
	}

	public final void createPhongDot() {
		double nx, ny, nz;  
		double nr;
		//    double r = 255;
		int vr;

		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				nx = (j-width/2);
				nx = nx/(width/2);
				ny = (i-height/2);
				ny = ny/(height/2);
				nz = 1 - Math.sqrt(nx*nx+ny*ny);
				if(nz<0) nz = 0;

				vr = (int)(nz*(this.depth<<4));
				//        vr = (int)(nz*(1024<<4));
				this.data[j+this.ytab[i]] = vr;
				}
			}

		}

	public final void calcPersp(Screen32 d, Screen32 t) {
		if (boolSkipDraw) {
			boolSkipDraw = false;
			return;
		}
		int v;
		int newx, newy;
		int curx, cury, curx2;
		int starty = 0, startx = 0;
		int endy = this.height, endx = this.width;

		cury = ypos-hheight;
		curx2 = xpos-hwidth;
		if ((cury+this.height)>= d.height) { endy = (d.height-cury); }
		if (cury<0) { starty = (-cury); cury = 0; }
		if ((curx2+this.width)>= d.width) { endx = (d.width-curx2); }
		if (curx2<0) { startx = (-curx2); curx2 = 0; }

		for (int y = starty; y<endy ; y++) {
			curx = curx2;
			for (int x = startx; x<endx ; x++) {

				v=(65536-(this.data[x+ytab[y]]));
				newx = ((( curx-t.hwidth)*v)>>16)+t.hwidth;
				newy = ((( cury-t.hheight)*v)>>16)+t.hheight;

				if((newx>=t.width || newx<0) || (newy>=t.height || newy<0)) { 
				d.data[curx+d.ytab[cury]] = intBackgroundColor; 
				}
				else {
				d.data[curx+d.ytab[cury]] = t.data[newx+t.ytab[newy]]; 
				}
				curx++;
				}
			cury++;
			} 
		}
	};

