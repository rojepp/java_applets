
/**
 * BumpMap class.
 *
 * Makes a bumpmap from a Screen32.
 * 6 Tables: delta x, y for r, g & b channels.
 * Just instanciate with the Screen32 and desired (double)height.
 * @author: Robert Bögniel.
 * @version: haha.
 *
 */

public final class BumpMap {
	public int mDeltaX[], mDeltaY[];

	//Constructor
	public BumpMap(Screen32 source, double bumpheight) {
		int sc1, sc2, sc3, sc4;
		double r1, g1, b1, r2, g2, b2;
		double r3, g3, b3, r4, g4, b4;
		int width = source.getwidth();
		int height = source.getheight();

		mDeltaX = new int[source.getwidthheight()];
		mDeltaY = new int[source.getwidthheight()];

		for(int i=1; i<height-1; i++) {
			for(int j=0; j<width; j++) {
				sc1 = source.data[source.ytab[i+1]+j] ;
				sc2 = source.data[source.ytab[i-1]+j] ;
				sc3 = source.data[source.ytab[i]+j+1] ;
				sc4 = source.data[source.ytab[i]+j-1] ;

				r1 = (sc1>>16)&255; g1 = (sc1>>8)&255; b1 = (sc1)&255;
				r2 = (sc2>>16)&255; g2 = (sc2>>8)&255; b2 = (sc2)&255;
				r3 = (sc3>>16)&255; g3 = (sc3>>8)&255; b3 = (sc3)&255;
				r4 = (sc4>>16)&255; g4 = (sc4>>8)&255; b4 = (sc4)&255;

				r1 = (r1+g1+b1)/3;
				r2 = (r2+g2+b2)/3;
				r3 = (r3+g3+b3)/3;
				r4 = (r4+g4+b4)/3;

				mDeltaY[source.ytab[i]+j] = (int)( ((r1-r2)/512)*bumpheight  );
				mDeltaX[source.ytab[i]+j] = (int)( ((r3-r4)/512)*bumpheight  );

				}
			}
		}

	}
