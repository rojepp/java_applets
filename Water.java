/**
 *
 * Class Water.
 *
 * @author Robert Bögniel.
 *
 */

public final class Water {
	public int data[];
	int ytab[];
	int palette[];
	public int width, height;
	int hwidth, hheight;
	int dim;
	private int wInd;
	private int wInd2;
	int dotsize;
	int dotdepth;
	int ndotsize;
	int logoheight;
	int logoheight2;

	//Constructor
	public Water(int w, int h) {
		width = w;
		height = h;
		hwidth = w >> 1;
		hheight = h >> 1;

		logoheight = 824;
		logoheight2 = 200;
		dotsize = 9;
		dotdepth = 450;
		ndotsize = 6;
		dim = 5;
		data = new int[(width) * (height+2) * 2];
		palette = new int[1024];
		wInd = width;
		wInd2 = width * (height+3);

		ytab = new int[height+2];
		int yval = 0;
		for(int i = 0; i<height+2; i++) {
			ytab[i] = yval;
			yval+=width;
		}

	}

	public final void createPalette(int r1, int g1, int b1, int r2, int g2, int b2, int r3,
									int g3, int b3, int r4, int g4, int b4, int r5, int g5, int b5) {

		double dr1, dg1, db1, dr2, dg2, db2 ,dr3, dg3, db3, dr4, dg4, db4;
		double cr1, cg1, cb1, cr2, cg2, cb2, cr3, cg3, cb3, cr4, cg4, cb4;

		dr1 = r2-r1; dg1 = g2-g1; db1 = b2-b1;
		dr1 /= 256; dg1 /= 256; db1 /= 256;
		cr1 = r1; cg1 = g1; cb1 = b1;

		dr2 = r3-r2; dg2 = g3-g2; db2 = b3-b2;
		dr2 /= 256; dg2 /= 256; db2 /= 256;
		cr2 = r2; cg2 = g2; cb2 = b2;

		dr3 = r4-r3; dg3 = g4-g3; db3 = b4-b3;
		dr3 /= 256; dg3 /= 256; db3 /= 256;
		cr3 = r3; cg3 = g3; cb3 = b3;

		dr4 = r5-r4; dg4 = g5-g4; db4 = b5-b4;
		dr4 /= 256; dg4 /= 256; db4 /= 256;
		cr4 = r4; cg4 = g4; cb4 = b4;

		for(int i=0; i<256; i++) {
			r1 = (int)cr1; g1 = (int)cg1; b1 = (int)cb1;
			r2 = (int)cr2; g2 = (int)cg2; b2 = (int)cb2;
			r3 = (int)cr3; g3 = (int)cg3; b3 = (int)cb3;
			r4 = (int)cr4; g4 = (int)cg4; b4 = (int)cb4;
			palette[i] = (r1<<16) + (g1<<8) + (b1);
			palette[i+256] = (r2<<16) + (g2<<8) + (b2);
			palette[i+512] = (r3<<16) + (g3<<8) + (b3);
			palette[i+768] = (r4<<16) + (g4<<8) + (b4);
			cr1 += dr1; cg1 += dg1; cb1 += db1;
			cr2 += dr2; cg2 += dg2; cb2 += db2;
			cr3 += dr3; cg3 += dg3; cb3 += db3;
			cr4 += dr4; cg4 += dg4; cb4 += db4;
		}

	}

	public final void calcOverlay(Screen32 d, Screen32 t) {
		int dwInd = wInd;
		int v;
		for (int y=0; y<height*width; y++) {
			v = ((data[dwInd-width] + data[dwInd+width] +
				 data[dwInd-1] + data[dwInd+1])>>1);
			v -= data[wInd2+y];
			v=(v-(v>>dim));
			data[wInd2+y]=v;

			if(v<-511) v=-511;
			if(v>511) v=511;
			v=(512+v)&0x3ff;
			if( (t.data[y]&0xff000000)!=0) {
				if( (v>logoheight2) && (v<logoheight) ) {
					d.data[y] = t.data[y];
				}
				else d.data[y] = palette[v];
			}
			else d.data[y] = palette[v];
			dwInd++;
		}
	}

	public final void calcWater(Screen32 d) {
		int dwInd = wInd;
		int v;
		int count = height*width;
		for (int y=0; y<count; y++) {
			v = ((data[dwInd-width] + data[dwInd+width] +
				 data[dwInd-1] + data[dwInd+1])>>1);
			v -= data[wInd2+y];
			v=(v-(v>>dim));
			data[wInd2+y]=v;
			if(v<-511) v=-511;
			if(v>511) v=511;
			v=(512+v)&0x3ff;
			d.data[y] = palette[v];
			dwInd++;
		}
	}

	public final void flip() {
		if ( wInd == width ) { wInd=wInd2; wInd2=width; }
		else { wInd2=wInd; wInd=width; }
	}

	public final void setDot(int x, int y, int c, int s) {
		for(int i = 0; i < s; i++)
			for(int j = 0; j < s; j++)
				if( ((y+i+1)>0) && ((y+i+1) < (height-s)) )
					data[wInd2+ytab[y+i+1]+x+j+1]+= c;



	}
};

