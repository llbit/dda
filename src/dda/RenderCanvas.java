/**
 * Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
 */
package dda;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Illustrate Digital Differential Analysis
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("serial")
public class RenderCanvas extends Canvas {

	private static final int IMAGE_SIZE = 26;
	private static final int PIXEL_SCALE = 32;
	private static final int RED = 0xFFFF4444;
	private static final int ORNG = 0xFFFF8800;
	private static final Color COL_ORNG = new Color(ORNG);
	private static final Color COL_INTERSECT = new Color(0xFF000000);
	private static final int YELLOW = 0xFFFFFF00;
	private static final int GREEN = 0xFF44FF44;
	private static final int BLUE = 0xFF4444FF;

	private BufferStrategy bufferStrategy = null;
	private final BufferedImage buffer;
	private final int width;
	private final int height;
	private final int windowWidth;
	private final int windowHeight;

	public RenderCanvas() {
		this.width = IMAGE_SIZE;
		this.height = IMAGE_SIZE;
		this.windowWidth = width * PIXEL_SCALE;
		this.windowHeight = height * PIXEL_SCALE;
		setPreferredSize(new Dimension(windowWidth, windowHeight));

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public synchronized void update(double angle, boolean drawIntersections) {
		Graphics g = buffer.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		g.dispose();
		double dx0 = Math.cos(angle);
		double dy0 = -Math.sin(angle);
		double dx = Math.cos(angle);
		double dy = -Math.sin(angle);
		double yoffset = 0.5;
		double xoffset = 0.5;
		double x0 = xoffset + width/2.0;
		double y0 = yoffset + height/2.0;
		int xmod = (int)Math.signum(dx), ymod = (int)Math.signum(dy);
		int xo = (1+xmod)/2, yo = (1+ymod)/2;
		dx = Math.abs(dx);
		dy = Math.abs(dy);
		int ix = (int) x0;
		int iy = (int) y0;
		double xp = x0;
		double yp = y0;
		int color = 0xFF888888;
		List<Double> intersections = new ArrayList<Double>();
		if (dx > dy) {
			double m = dy/dx;
			double xrem = xmod * (ix+xo - xp);
			double ylimit = xrem*m;
			for (int i = 0; ; ++i) {
				if (!draw(ix, iy, color)) break;
				double yrem = ymod * (iy+yo - yp);
				if (yrem < ylimit) {
					iy += ymod;
					if (!draw(ix, iy, GREEN)) break;
					intersections.add(i/dx + yrem/dy);
					intersections.add((i+xrem)/dx);
					ix += xmod;
				} else {
					ix += xmod;
					intersections.add((i+xrem)/dx);
					if (yrem <= m) {
						if (!draw(ix, iy, RED)) break;
						intersections.add(i/dx + yrem/dy);
						iy += ymod;
					}
				}
				color = BLUE;
				yp = y0 + ymod * (i+1) * m;
			}
		} else {
			double m = dx/dy;
			double yrem = ymod * (iy+yo - yp);
			double xlimit = yrem*m;
			for (int i = 0; ; ++i) {
				if (!draw(ix, iy, color)) break;
				double xrem = xmod * (ix+xo - xp);
				if (xrem < xlimit) {
					ix += xmod;
					if (!draw(ix, iy, GREEN)) break;
					intersections.add(i/dy + xrem/dx);
					intersections.add((i+yrem)/dy);
					iy += ymod;
				} else {
					iy += ymod;
					intersections.add((i+yrem)/dy);
					if (xrem <= m) {
						if (!draw(ix, iy, RED)) break;
						intersections.add(i/dy + xrem/dx);
						ix += xmod;
					}
				}
				color = BLUE;
				xp = x0 + xmod * (i+1) * m;
			}
		}

		if (bufferStrategy == null) {
			createBufferStrategy(2);
			bufferStrategy = getBufferStrategy();
		}
		do {
			do {
				g = bufferStrategy.getDrawGraphics();
				g.drawImage(buffer, 0, 0, windowWidth, windowHeight, null);
				g.setColor(COL_ORNG);
				int lx0 = (int) (x0*PIXEL_SCALE);
				int ly0 = (int) (y0*PIXEL_SCALE);
				g.drawLine(lx0, ly0, (int)(dx0*300*PIXEL_SCALE)+lx0, (int)(dy0*300*PIXEL_SCALE)+ly0);
				if (drawIntersections) {
					for (double t: intersections) {
						int xx = (int) ((x0 + t*dx0)*PIXEL_SCALE);
						int yy = (int) ((y0 + t*dy0)*PIXEL_SCALE);
						g.setColor(Color.BLACK);
						g.fillRect(xx-1, yy-1, 3, 3);
						g.setColor(Color.WHITE);
						g.drawRect(xx-1, yy-1, 3, 3);
					}
				}
				g.dispose();
			} while (bufferStrategy.contentsRestored());
			bufferStrategy.show();
		} while (bufferStrategy.contentsLost());
	}

	private boolean draw(int ix, int iy, int argb) {
		if (ix >= 0 && ix < width &&
				iy >= 0 && iy < height) {
			buffer.setRGB(ix, iy, argb);
			return true;
		}
		return false;
	}
	private boolean draw2(boolean draw, int ix, int iy, int argb) {
		if (ix >= 0 && ix < width &&
				iy >= 0 && iy < height) {
			if (draw) buffer.setRGB(ix, iy, argb);
			return true;
		}
		return false;
	}
}
