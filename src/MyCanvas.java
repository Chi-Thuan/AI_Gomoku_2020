
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

@SuppressWarnings(value = { "serial", "unused" })
public class MyCanvas extends JComponent {

	// I'm using constants rather than enum to avoid too many class files.
	static final int setBackground = 19, setPaint = 21, setStroke = 24, drawLine = 42, drawOval = 43, fillRect = 53,
			setColor = 57, setFont = 58, clear = 61, opaque = 62;

	public MyCanvas() {
		setFocusable(true);
	}

	public void setColor(Color c) {
		setPaint(c);
	} // toBuffer(setColor,c );}

	public void setBackground(Color color) {
		toBuffer(setBackground, color);
		toBuffer(clear, null);
	}

	public void setPaint(Paint paint) {
		toBuffer(setPaint, paint);
	}

	private List<Integer> a1 = new ArrayList<Integer>();
	private List<Object> a2 = new ArrayList<Object>();
	private List<Integer> a1x = new ArrayList<Integer>();
	private List<Object> a2x = new ArrayList<Object>();
	private boolean inBuffer = false;

	// state changes
	private Boolean theOpaque = null;
	private Color theBackground = null;

	private static Paint origPaint = Color.BLACK;
	private static Font origFont = new Font("Dialog", Font.PLAIN, 12);
	private static Stroke origStroke = new BasicStroke(1);

	private void doPaint(Graphics2D g, int s, Object o) {
		// process an operation from the buffer
		// System.out.println(s);
		Object o1 = null, o2 = null, o3 = null, o4 = null, o5 = null, o6 = null, o7 = null, o8 = null, o9 = null,
				o10 = null, o11 = null;
		if (o instanceof Object[]) {
			Object[] a = (Object[]) o;
			if (a.length > 0)
				o1 = a[0];
			if (a.length > 1)
				o2 = a[1];
			if (a.length > 2)
				o3 = a[2];
			if (a.length > 3)
				o4 = a[3];
			if (a.length > 4)
				o5 = a[4];
			if (a.length > 5)
				o6 = a[5];
			if (a.length > 6)
				o7 = a[6];
			if (a.length > 7)
				o8 = a[7];
			if (a.length > 8)
				o9 = a[8];
			if (a.length > 9)
				o10 = a[9];
			if (a.length > 10)
				o11 = a[10];
		}
		switch (s) {
		case clear:
			paintBackground(g, theBackground);
			break;
		case setBackground:
			g.setBackground((Color) o);
			break;
		case setPaint:
			g.setPaint((Paint) o);
			break;
		case setStroke:
			g.setStroke((Stroke) o);
			break;
		case drawLine:
			g.drawLine((Integer) o1, (Integer) o2, (Integer) o3, (Integer) o4);
			break;
		case drawOval:
			g.drawOval((Integer) o1, (Integer) o2, (Integer) o3, (Integer) o4);
			break;
		case fillRect:
			g.fillRect((Integer) o1, (Integer) o2, (Integer) o3, (Integer) o4);
			break;
		case setColor:
			g.setColor((Color) o);
			break;
		case setFont:
			g.setFont((Font) o);
			break;
		case opaque:
			super.setOpaque((Boolean) o);
			break;
		default:
			System.out.println("Unknown image operation " + s);
		}
	}

	private synchronized void toBuffer(int s, Object a) {
		a1.add(s);
		a2.add(a);
		if (s == clear) {
			clearBuffer();
		}
		if (s == opaque)
			theOpaque = (Boolean) a;
		if (s == setBackground)
			theBackground = (Color) a;
		if (inBuffer)
			return;
		if (isSetter(s))
			return;
		Graphics g = getGraphics();
		if (g == null)
			return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(origPaint);
		g2.setFont(origFont);
		g2.setStroke(origStroke);
		for (int i = 0; i < a1.size() - 1; i++) {
			int s1 = a1.get(i);
			Object s2 = a2.get(i);
			if (isSetter(s1))
				doPaint(g2, s1, s2);
		}
		doPaint((Graphics2D) g, s, a);
	}

	private synchronized void clearBuffer() {
		Font f = bufferFont();
		Paint p = bufferPaint();
		Stroke s = bufferStroke();
		a1.clear();
		a2.clear();
		if (f != origFont) {
			a1.add(setFont);
			a2.add(f);
		}
		if (p != origPaint) {
			a1.add(setPaint);
			a2.add(p);
		}
		if (s != origStroke) {
			a1.add(setStroke);
			a2.add(s);
		}
		a1.add(clear);
		a2.add(null);
	}

	private Stroke bufferStroke() {
		Stroke c = (Stroke) lookBuffer(setStroke);
		// if(c==null&&theStroke!=null) return theStroke;
		if (c == null)
			return origStroke;
		return c;
	}

	private Font bufferFont() {
		Font c = (Font) lookBuffer(setFont);
		// if(c==null&&theFont!=null) return theFont;
		// if(c==null)c=origFont;
		// if(c==null) return new Font("Dialog",Font.PLAIN,12);
		if (c == null)
			return origFont;
		return c;
	}

	private Paint bufferPaint() {
		Paint c = (Paint) lookBuffer(setPaint);
		// if(c==null&&thePaint!=null) return thePaint;
		// if(c==null) return Color.black;
		if (c == null)
			return origPaint;
		return c;
	}

	private synchronized Object lookBuffer(int s) {
		for (int i = a1.size() - 1; i >= 0; i--) {
			if (a1.get(i) == s)
				return a2.get(i);
		}
		return null;
	}

	// operations that change state but do not draw anything
	private static int[] setOp = { setBackground, setPaint, setStroke, setColor, setFont, };

	private boolean isSetter(int s) {
		for (int s1 : setOp)
			if (s == s1)
				return true;
		return false;
	}

	/** [Internal] */
	private void paintBackground(Graphics2D g2, Color theBackground) {
		Color color1 = g2.getColor();
		if (theBackground == null)
			theBackground = Color.white;
		g2.setColor(theBackground);
		g2.fillRect(0, 0, 30000, 30000);
		g2.setColor(color1);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		toBuffer(drawLine, mkArg(x1, y1, x2, y2));
	}

	public void drawOval(int x, int y, int width, int height) {
		toBuffer(drawOval, mkArg(x, y, width, height));
	}

	public void fillRect(int x, int y, int width, int height) {
		toBuffer(fillRect, mkArg(x, y, width, height));
	}

	private Object mkArg(Object... args) {
		return args;
	}

	/** [Internal] */
	public void paintComponent(Graphics g) {
		boolean opq = true;
		if (theOpaque != null)
			opq = theOpaque;
		super.setOpaque(opq);
		// if(theBackground!=null)super.setBackground(theBackground);
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Rectangle rt = getBounds();
		rt.x = 0;
		rt.y = 0;
		doBuffer(g2, opq, rt);
		chkFPS();
	}

	private int fpsCount = 0;
	private long fpsTime = 0;
	private int lastFPS = 10;

	private void chkFPS() {
		if (fpsCount == 0) {
			fpsTime = System.currentTimeMillis() / 1000;
			fpsCount++;
			return;
		}
		fpsCount++;
		long time = System.currentTimeMillis() / 1000;
		if (time != fpsTime) {
			lastFPS = fpsCount;
			fpsCount = 1;
			fpsTime = time;
		}
	}

	/** Compute the number of frames displayed per second */
	public int getFPS() {
		return lastFPS;
	}

	private AffineTransform origTransform = null;

	private synchronized void doBuffer(Graphics2D g2, boolean opq, Rectangle rt) {
		origTransform = g2.getTransform();
		if (opq && rt != null)
			g2.clearRect(rt.x, rt.y, rt.width, rt.height);
		g2.setPaint(origPaint);
		g2.setFont(origFont);
		g2.setStroke(origStroke);
		if (inBuffer) {// System.out.println("upps");
			for (int i = 0; i < a1x.size(); i++)
				doPaint(g2, a1x.get(i), a2x.get(i));
			origTransform = null;
			return;
		}
		for (int i = 0; i < a1.size(); i++)
			doPaint(g2, a1.get(i), a2.get(i));
		origTransform = null;
	}
}
