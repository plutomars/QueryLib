package query.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.Timer;

class WaitGlassPane extends JComponent implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean mIsRunning;
	private boolean mIsFadingOut;
	private Timer mTimer;

	private int mAngle;
	private int mFadeCount;
	private int mFadeLimit = 15;

	public WaitGlassPane() {
		// blocks all user input
		addMouseListener(new MouseAdapter() {
		});
		addMouseMotionListener(new MouseMotionAdapter() {
		});
		addKeyListener(new KeyAdapter() {
		});

		setFocusTraversalKeysEnabled(false);
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent evt) {
				requestFocusInWindow();
			}
		});

		setBackground(Color.WHITE);
		setFont(new Font("Default", Font.BOLD, 16));
	}

	@Override
	protected void paintComponent(Graphics g) {
		int w = this.getWidth();
		int h = this.getHeight();

		// Paint the view.
		// super.paint (g, this);
		// super.paint(g);
//		repaint();
		if (!mIsRunning) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		float fade = (float) mFadeCount / (float) mFadeLimit;
		// Gray it out.
		Composite urComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				.5f * fade));
		g2.fillRect(0, 0, w, h);
		g2.setComposite(urComposite);

		// Paint the wait indicator.
		int s = Math.min(w, h) / 10;
		int cx = w / 2;
		int cy = h / 2;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2.setPaint(Color.white);
		g2.rotate(Math.PI * mAngle / 180, cx, cy);
		for (int i = 0; i < 12; i++) {
			float scale = (11.0f - (float) i) / 11.0f;
			g2.drawLine(cx + s, cy, cx + s * 2, cy);
			g2.rotate(-Math.PI / 6, cx, cy);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					scale * fade));
		}

		g2.dispose();
	}

	public void actionPerformed(ActionEvent e) {
		if (mIsRunning) {
			firePropertyChange("tick", 0, 1);
			mAngle += 3;
			if (mAngle >= 360) {
				mAngle = 0;
			}
			if (mIsFadingOut) {
				if (--mFadeCount <= 0) {
					mFadeCount = 0;
					mIsRunning = false;
					mTimer.stop();
				}
			} else if (mFadeCount < mFadeLimit) {
				mFadeCount++;
			}
			repaint();
		}
	}

	public void start() {
		
		if (mIsRunning) {
			return;
		}

		// Run a thread for animation.
		mIsRunning = true;
		mIsFadingOut = false;
		mFadeCount = 0;
		int fps = 24;
		int tick = 1000 / fps;
		mTimer = new Timer(tick, this);

		mTimer.start();
		
	}

	public void stop() {
		mIsFadingOut = true;
	}
}