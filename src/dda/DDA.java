/**
 * Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
 */
package dda;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * Illustrate Digital Differential Analysis
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DDA extends JApplet {

	private final RenderCanvas canvas = new RenderCanvas();
	private final Thread renderThread;
	protected int mouseX;
	protected int mouseY;
	protected int canvasWidth;
	protected int canvasHeight;

	public static void main(String[] args) {
		final DDA pt = new DDA();
		JFrame frame = new JFrame("DDA Test");
		frame.add(pt);

		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				pt.init();
				pt.start();
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				pt.stop();
				pt.destroy();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

	}

	synchronized protected void mouseAt(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	public DDA() {
		add(canvas);
		canvas.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				synchronized (DDA.this) {
					canvasWidth = canvas.getWidth();
					canvasHeight = canvas.getHeight();
					mouseAt(canvasWidth/2, canvasHeight/2);
				}
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseAt(e.getX(), e.getY());
			}
			@Override
			public void mouseDragged(MouseEvent e) {
			}
		});

		renderThread = new Thread() {
			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						int x, y;
						synchronized (DDA.this) {
							x = mouseX;
							y = mouseY;
						}
						double angle = Math.atan2(-(y-canvasHeight/2), x-canvasWidth/2);
						canvas.update(angle, true);
						sleep(100);
					}
				} catch (InterruptedException e) {
				}
			}
		};
	}

	@Override
	public void init() {
		renderThread.start();
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void destroy() {
		renderThread.interrupt();
		try {
			renderThread.join();
		} catch (InterruptedException e) {
		}
	}
}
