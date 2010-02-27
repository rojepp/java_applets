import java.applet.Applet;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Color;
import java.awt.Event;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.*;
import java.util.StringTokenizer;

//import java.awt.*;
//import java.io.*;

/**
 * WrApp wrapper class for all Durius applets.
 *
 * @author Robert Bögniel.
 */

public class WrApp extends Applet implements Runnable, MouseListener, MouseMotionListener {
	Thread kicker;
	DirectColorModel dcm;
	MemoryImageSource mis;
	Image img;
	static final String strLoading = "Applet initializing.", strProtocol = "file";
	String s, strBrokenImageName;
	private long lngStart;
	private long lngEnd;
	static long lngFPS = 1000/50;
	private boolean running = true;
	boolean boolInitialized = false, boolImageError = false;
	protected int intBackgroundColor, intAppletWidth, intAppletHeight, intAppKey;
	private URL urlDurius, urlThis;
	private String urlTarget[];
	private StringTokenizer tokURL;
	private int intButtonCount, intRegcode, intOrientation;
	private String strFrame = "";
	protected Graphics gfx;
	
	public void init() 
	{
		try { urlThis = getDocumentBase(); } catch(Exception e) {};
		try { urlDurius = new URL("http://www.durius.com/"); } catch(Exception e) { };

		addMouseListener(this);
		addMouseMotionListener(this);
		s = getParameter("bg"); 
		if (s != null) {
			intBackgroundColor = Integer.valueOf(s,16).intValue();
			setBackground( new Color(intBackgroundColor) );
		} else setBackground(new Color(0,0,0));

		// Default settings
		intRegcode = 1; intButtonCount = 0; intOrientation = 0;

		s = getParameter("width"); if (s != null) intAppletWidth = Integer.parseInt(s);
		s = getParameter("height"); if (s != null) intAppletHeight = Integer.parseInt(s);
		s = getParameter("orientation"); if(s != null) intOrientation = (s.toLowerCase().compareTo("v")) == 0 ? 0 : 1;

		/*
		* URL forming
		*/ 
		s = getParameter("url");
		if (s != null && s.length() > 1) {
			tokURL = new StringTokenizer(s);
			urlTarget = new String[tokURL.countTokens()];
		}
		s = getParameter("target"); if (s != null) strFrame = s;

		if(tokURL != null) {
			this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			while (tokURL.hasMoreTokens()) {
				s = tokURL.nextToken();
				String ustring = s;
				s = s.toLowerCase();
				//				String strPort = "";
				//				if(urlThis.getPort() > 1)
				//					strPort = ":" +strPort.valueOf(urlThis.getPort);
				if (s.startsWith("/")) ustring = urlThis.getProtocol().toString() + urlThis.getHost().toString() + ustring;
				else if (s.startsWith("mailto:")) {;}
				else if (s.startsWith("javascript:")) {;}
				else if (s.startsWith("http://")) {;}
				else if (s.startsWith("https://")) {;}
				else if (s.startsWith(".")) { ustring = urlThis.toString().substring(0,urlThis.toString().lastIndexOf("/")+1) + ustring;}
				else if ((s.startsWith("/") == false) && (s.startsWith("javascript:") == false)) { ustring = urlThis.toString().substring(0,urlThis.toString().lastIndexOf("/")+1) + ustring;}
				urlTarget[intButtonCount] = ustring;
				intButtonCount++;
			}
		}
		
		
		gfx = getGraphics();
		kicker = new Thread(this);
		kicker.setPriority(Thread.MAX_PRIORITY);
		kicker.start();
	}

	public final void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
			navigate(getURL(x, y));
	}

	// Navigate a hyperlink
	public final void navigate(String url)
	{
		if(url.startsWith("javascript:")) 
		{
			executeJavascriptCommand(url);
			return;
		}
		try
		{
			
			if(strFrame != "")
			{
				getAppletContext().showDocument(new URL(url), strFrame);
			}
			else 
			{
				getAppletContext().showDocument(new URL(url));
			}
		} catch(Exception e) {}
		
	}
	
	// Rather than checking the browser vendor to determine what action
	// to take, we test for the presence of the JSObject class.
	final private void executeJavascriptCommand(String s)
	{
/*		Class jsObjectClass;
		try { 
			jsObjectClass = Class.forName("netscape.javascript.JSObject");
		} catch(ClassNotFoundException e) {
			jsObjectClass = null; 
		}
		if(jsObjectClass != null) {
			try {
				netscape.javascript.JSObject window;
				window = netscape.javascript.JSObject.getWindow(this);
				//window = (netscape.javascript.JSObject)window.getMember("top");
				window.eval(s.substring(11,s.length())); 
			} catch(Exception shite) { }
		}
		else {
			try {
				getAppletContext().showDocument(new URL(s));
			} catch(Exception shite2) { }
		}
*/
}
	
	public void mouseEntered(MouseEvent e) {
			if(intButtonCount != 0)
				showStatus(getURL(e.getX(),e.getY()));
	}

	public void mouseMoved(MouseEvent e) {
			if(intButtonCount != 0)
				showStatus(getURL(e.getX(),e.getY()));
		if(boolImageError) {
			String imageerror = new String(urlThis.toString());
			
			imageerror = imageerror.substring(0, (imageerror.lastIndexOf("/")));
			showStatus("Image '" + imageerror + "/" + strBrokenImageName + "' not found!");
		}
	}

	public void render() {}
	public void mouseExited(MouseEvent e) {/* showStatus("") ;*/	}
	public void mousePressed(MouseEvent e) {};
	public void mouseDragged(MouseEvent e) {};
	public void mouseReleased(MouseEvent e) {};

	public final void paint(Graphics g) { }
	public final void update(Graphics g) { }

	public final void run() {
		while (kicker!=null && running) {
			if(boolInitialized) {
				lngStart = System.currentTimeMillis();
					render();
				lngEnd = System.currentTimeMillis();
				long lngSleep = lngFPS-(lngEnd - lngStart);
				try { 
					if(lngSleep > 1) {
						kicker.sleep(lngSleep); 
//										showStatus(Long.toString(lngSleep));
					} else {
						kicker.sleep(5); 
	//									showStatus(Long.toString(lngSleep));
					}
				} catch(Exception e) { ; }
			} else {
				try {
					Thread.sleep(100);
				} catch(Exception e) {}
			}
		} 
	}

	public final void start() {	}
	public final void stop() {
		if ( kicker != null ) {
			running = false;
			try {
				kicker.join ( 1000 );
			} catch ( Exception e ) { }
			kicker = null;	      
		}	
	}

	public final String getAppletInfo() { return "(c) Durius http://www.durius.com/"; }

	private final String getURL(int x,int y) {
		try {
			if(intButtonCount >= 1) {
				if(intOrientation == 0) 
					return urlTarget[Math.min(intButtonCount-1,(int)((double)y/(intAppletHeight/intButtonCount)))];
				else 
					return urlTarget[Math.min(intButtonCount-1,(int)((double)x/(intAppletWidth/intButtonCount)))];
			} else return "";
		} catch (Exception e) {
			return "";
		}
	}
	
	
	/* End Wrapper */
	
}


