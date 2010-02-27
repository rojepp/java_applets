/**
 *
 * Class Water.
 *
 * @author Robert Bögniel.
 *
 */

public final class Waterpic {
	public int data[];
	int ytab[];
	public int width, height;
	int hwidth, hheight;
	int dim;
	int ptrData;
	int ptrData2;
	int dotsize;
	int dotdepth;
	int ndotsize;

	
	int ptrIndex, dInd, ptrDestData, v, newx, newy, y, x;
	
	//Constructor
	public Waterpic(int w, int h) {
		width = w;
		height = h;
		hwidth = w >> 1;
		hheight = h >> 1;
		dotsize = 9;
		dotdepth = 450;
		ndotsize = 6;
		dim = 5;
		data = new int[(width) * (height+2) * 4];
		ptrData = width + 1;
		ptrData2 = 4 + (width * (height+3));

		ytab = new int[height+2];
		int yval = 0;
		for(int i = 0; i<height+2; i++) {
			ytab[i] = yval;
			yval+=width;
		}

	}

	// 1 to 1
	protected final void calcPersp(Screen32 d, Screen32 t) {
		ptrIndex = ptrData;
		dInd = 0;
		ptrDestData = ptrData2;

		for (y=0; y<height ; y++) {
			for (x=0; x<width ; x++) {
//				v = ((data[ptrIndex-width] + data[ptrIndex+width] +
//						 data[ptrIndex-1] + data[ptrIndex+1])>>1);
				v = ((
					  data[ptrIndex-width] + 
					  data[ptrIndex+width] +
					data[ptrIndex-1] + 
					  data[ptrIndex+1]+
					  data[ptrIndex-width-1] + 
					  data[ptrIndex-width+1] +
					data[ptrIndex+width-1] + 
					  data[ptrIndex+width+1]
					  )>>2);

				v-= data[ptrDestData];
				v-=v>>dim;
				data[ptrDestData++]=v;

				v=(1024-v);

				newx = ((( x-hwidth)*v)>>10)+hwidth;
				newx = newx >= width ? width-1 : newx < 0 ? 0 : newx;
				newy = ((( y-hheight)*v)>>10)+hheight;
				newy = newy >= height ? height-1 : newy < 0 ? 0 : newy;

				d.data[dInd++] = t.data[newx+t.ytab[newy]];
				ptrIndex++;
			}
		}
	}

	protected final void calcWaterOnly() {
		ptrIndex = ptrData;
		dInd = 0;
		ptrDestData = ptrData2;
		
		int iterations = (height*width);
		for (y=0; y<iterations ; y++) {
//				v = ((data[ptrIndex-width] + data[ptrIndex+width] +
//						 data[ptrIndex-1] + data[ptrIndex+1])>>1);
				v = ((
					  data[ptrIndex-width] + 
					  data[ptrIndex+width] +
					data[ptrIndex-1] + 
					  data[ptrIndex+1]+
					  data[ptrIndex-width-1] + 
					  data[ptrIndex-width+1] +
					data[ptrIndex+width-1] + 
					  data[ptrIndex+width+1]
					  )>>2);
				v-= data[ptrDestData];
				v-=v>>dim;
				data[ptrDestData++]=v;
				ptrIndex++;
		}
	}

	protected final void TransformWater2(Screen32 d, Screen32 t)
	{
		dInd = 0;
		
		for (y=0; y<d.height ; y++) {
			for (x=0; x<d.width ; x++) {
				v = 1024 - data[ptrData2 + this.ytab[(y>>1)] + (x>>1)];
				newx = ((( x-d.hwidth)*v)>>10)+d.hwidth;
				newx = newx >= d.width ? d.width-1 : newx < 0 ? 0 : newx;
				newy = ((( y-d.hheight)*v)>>10)+d.hheight;
				newy = newy >= d.height ? d.height-1 : newy < 0 ? 0 : newy;
				d.data[dInd++] = t.data[newx+t.ytab[newy]];
			}
		}
	}

	protected final void TransformWaterFree(Screen32 d, Screen32 t)
	{
		dInd = 0;
		int xadd = (width<<16) / d.width;		
		int yadd = (height<<16) / d.height;		
		int xsum = 0;
		int ysum = 0;
		for (y=0; y<d.height ; y++) {
			for (x=0; x<d.width ; x++) {
				v = 1024 - data[ptrData2 + this.ytab[(ysum>>16)] + (xsum>>16)];
				newx = ((( x-d.hwidth)*v)>>10)+d.hwidth;
				newx = newx >= d.width ? d.width-1 : newx < 0 ? 0 : newx;
				
				newy = ((( y-d.hheight)*v)>>10)+d.hheight;
				newy = newy >= d.height ? d.height-1 : newy < 0 ? 0 : newy;
				d.data[dInd++] = t.data[newx+t.ytab[newy]];
				xsum += xadd;
			}
				ysum += yadd;
				xsum = 0;
		}
	}

	public final void render(Screen32 d, Screen32 t)
	{
		if(this.width==d.width)
		{
			calcPersp(d, t);
		}
		else if(((this.width<<1)== d.width) && ((this.height<<1)== d.height))
		{
			calcWaterOnly();
			TransformWater2(d, t);
		}
		else
		{
			//All other sizes here
//			for(int perfp = 0; perfp<101; perfp++)
			calcWaterOnly();
			TransformWaterFree(d, t);
		}
	}
	
	public final void flip() {
		if ( ptrData == width+1 ) { ptrData=ptrData2; ptrData2=width+1; }
		else { ptrData2=ptrData; ptrData=width+1; }
	}

	public final void setDot(int x, int y, int c, int s, int appletwidth, int appletheight) {
		y = (y*height)/appletheight;
		x = (x*width)/appletwidth;
		int ysize = (s*height)/appletheight;
		int xsize = (s*width)/appletwidth;
		for(int i = 0; i < ysize; i++)
			for(int j = 0; j < xsize; j++)
				if( ((y+i+1)>0) && ((y+i+1) < (height-ysize)) )
					data[ptrData2+ytab[y+i+1]+x+j+1]+= c;
	}
};

