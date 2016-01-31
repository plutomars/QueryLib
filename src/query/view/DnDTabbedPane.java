package query.view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import query.controller.DataControl;


@SuppressWarnings("serial")
class DnDTabbedPane extends JTabbedPane {
	/**
	 * 
	 */
	private static final int LINEWIDTH = 3;
	private static final String NAME = "test";
	private final GhostGlassPane glassPane = new GhostGlassPane();
	private HashMap<String, DataControl> controlMap;
	private final Rectangle lineRect = new Rectangle();
	private final Color lineColor = new Color(0, 100, 255);
	private int dragTabIndex = -1;
	private String policy_info[];

	public void setPolicy_info(String policy_info[]) {
		this.policy_info = policy_info;

	}

	private void clickArrowButton(String actionKey) {
		ActionMap map = getActionMap();
		if (map != null) {
			Action action = map.get(actionKey);
			if (action != null && action.isEnabled()) {
				action.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, null, 0, 0));
			}
		}
	}

	private static Rectangle rBackward = new Rectangle();
	private static Rectangle rForward = new Rectangle();
	private static int rwh = 20;
	private static int buttonsize = 30;// XXX: magic number of scroll button
										// size

	public void add(String title, Component component, DataControl control) {
		if (controlMap == null) {
			controlMap = new HashMap<String, DataControl>();
		}
		controlMap.put(title, control);
		add(title, component);
	}

	private void autoScrollTest(Point glassPt) {
		Rectangle r = getTabAreaBounds();
		int tabPlacement = getTabPlacement();
		if (tabPlacement == TOP || tabPlacement == BOTTOM) {
			rBackward.setBounds(r.x, r.y, rwh, r.height);
			rForward.setBounds(r.x + r.width - rwh - buttonsize, r.y, rwh
					+ buttonsize, r.height);
		} else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
			rBackward.setBounds(r.x, r.y, r.width, rwh);
			rForward.setBounds(r.x, r.y + r.height - rwh - buttonsize, r.width,
					rwh + buttonsize);
		}
		rBackward = SwingUtilities.convertRectangle(getParent(), rBackward,
				glassPane);
		rForward = SwingUtilities.convertRectangle(getParent(), rForward,
				glassPane);
		if (rBackward.contains(glassPt)) {
			// System.out.println(new java.util.Date() + "Backward");
			clickArrowButton("scrollTabsBackwardAction");
		} else if (rForward.contains(glassPt)) {
			// System.out.println(new java.util.Date() + "Forward");
			clickArrowButton("scrollTabsForwardAction");
		}
	}

	public DnDTabbedPane() {
		super();
		final DragSourceListener dsl = new DragSourceListener() {
			
			//計算Tabpane位置
			JFrame queryFrame;
			Point gapPt = new Point(0,0);	//Panel和視窗中間間距
			Point framePt;				//JFrame左上角位置
			Point panelPt;				//JPanel左上角位置
			Dimension frameDn;			//JPanel長寬

			@Override
			public void dragEnter(DragSourceDragEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
				setFrameLocation();
			}

			@Override
			public void dragExit(DragSourceEvent e) {
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
				lineRect.setRect(0, 0, 0, 0);
				glassPane.setPoint(new Point(-1000, -1000));
				glassPane.repaint();
			}

			@Override
			public void dragOver(DragSourceDragEvent e) {
				Point glassPt = e.getLocation();
				Point mousePt = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(glassPt, glassPane);

				Rectangle r = getTabAreaBounds();

				if (mousePt.x >= panelPt.x && mousePt.x <= panelPt.x + r.width
						&& mousePt.y >= panelPt.y
						&& mousePt.y <= panelPt.y + r.height) {
					e.getDragSourceContext().setCursor(
							DragSource.DefaultMoveDrop);
					glassPane.setCursor(DragSource.DefaultMoveDrop);
				} else if (mousePt.x < panelPt.x
						|| mousePt.y < panelPt.y) {
					e.getDragSourceContext().setCursor(
							DragSource.DefaultMoveDrop);
					glassPane.setCursor(DragSource.DefaultMoveDrop);
				}else {
					e.getDragSourceContext().setCursor(
							DragSource.DefaultMoveNoDrop);
					glassPane.setCursor(DragSource.DefaultMoveNoDrop);

				}
			}

			@Override
			public void dragDropEnd(DragSourceDropEvent e) {
				setFrameLocation();
				// 拉出畫面新增一視窗
				Point glassPt = e.getLocation();
				// 拖曳滑鼠位置
				// 在窗外則remove並開一新視窗
				if (glassPt.x < (framePt.x + gapPt.x)
						|| glassPt.x > (framePt.x + gapPt.x + frameDn.getWidth())
						|| glassPt.y < (framePt.y + gapPt.y)
						|| glassPt.y > (framePt.y + gapPt.y + frameDn.getHeight())){
					
					// 拖曳至新視窗
					moveToFrame(glassPt);
				}	

				lineRect.setRect(0, 0, 0, 0);
				dragTabIndex = -1;
				glassPane.setVisible(false);
				if (hasGhost()) {
					glassPane.setVisible(false);
					glassPane.setImage(null);
				}
			}

			@Override
			public void dropActionChanged(DragSourceDragEvent e) {
			}
			
			public void setFrameLocation(){
				// 取得FRAME位置及計算TabPanel長寬
				panelPt = getLocation();
				Component frame = getParent();
				//取得JTab size
				frameDn = frame.getSize();
				gapPt.x = frame.getLocation().x;
				gapPt.y = frame.getLocation().y;
				panelPt.x += frame.getLocation().x;
				panelPt.y += frame.getLocation().y;

				int i =0;
				while (!(frame instanceof JFrame)) {
					frame = frame.getParent();
					if (i==0){
						gapPt.y += frame.getLocation().y;
						i++;
					}
					panelPt.x += frame.getLocation().x;
					panelPt.y += frame.getLocation().y;

				}
				gapPt.y += 32;	//加上視窗上BAR的位置
				
				queryFrame = (JFrame)frame;
				framePt = queryFrame.getLocation();
//				----------------------------------------------------
			}
		};
		final Transferable t = new Transferable() {
			private final DataFlavor FLAVOR = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType, NAME);

			@Override
			public Object getTransferData(DataFlavor flavor) {
				return DnDTabbedPane.this;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				DataFlavor[] f = new DataFlavor[1];
				f[0] = this.FLAVOR;
				return f;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.getHumanPresentableName().equals(NAME);
			}
		};
		final DragGestureListener dgl = new DragGestureListener() {
			@Override
			public void dragGestureRecognized(DragGestureEvent e) {
				if (getTabCount() <= 1)
					return;
				Point tabPt = e.getDragOrigin();
				dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
				// "disabled tab problem".
				if (dragTabIndex < 0 || !isEnabledAt(dragTabIndex))
					return;
				initGlassPane(e.getComponent(), e.getDragOrigin());
				try {
					e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
				} catch (InvalidDnDOperationException idoe) {
					idoe.printStackTrace();
				}
			}
		};
		new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE,
				new CDropTargetListener(), true);
		new DragSource().createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE, dgl);
	}

	// 將移出畫視之視窗新放至視窗內
	// 若為保戶訊息,則不可移出
	public void moveToFrame(final Point pt) {
		if (getTitleAt(getSelectedIndex()).equals("提示訊息")){
			JOptionPane.showMessageDialog(this, "提示訊息不可移出", "無法移出",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final DataControl control = controlMap
							.remove(getTitleAt(getSelectedIndex()));
					final JFrame frame = new JFrame(
							getTitleAt(getSelectedIndex()));
					// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							control.executeClearMission();
							frame.dispose();
						}
					});

					frame.setBounds(100, 100, 800, 700);
					frame.setLayout(new BorderLayout());
					frame.add(getSelectedComponent());

					JPanel panel2 = new JPanel();
					panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
//					System.out.println(policy_info[0]);
//					System.out.println(policy_info[1]);
//					System.out.println(policy_info[4]);
					JLabel label1 = new JLabel("保單號碼");
					JTextField field1 = new JTextField(policy_info[0]);
					field1.setColumns(10);
					field1.setEditable(false);
					JTextField field2 = new JTextField(policy_info[1]);
					field2.setColumns(10);
					field2.setEditable(false);
					JTextField field3 = new JTextField(policy_info[4]);
					field3.setColumns(10);
					field3.setEditable(false);
					JTextField field4 = new JTextField(policy_info[6]);
					field4.setColumns(10);
					field4.setForeground(new Color(139, 0, 0));
					field4.setBackground(new Color(255, 250, 205));
					field4.setEditable(false);
					JLabel label2 = new JLabel("要保人");
					JLabel label3 = new JLabel("被保人");
					JLabel label4 = new JLabel("          ");

					panel2.add(label1);
					panel2.add(field1);
					panel2.add(label2);
					panel2.add(field2);
					panel2.add(label3);
					panel2.add(field3);
					panel2.add(label4);
					panel2.add(field4);
					frame.add(panel2, BorderLayout.NORTH);
					frame.setLocation(pt);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class CDropTargetListener implements DropTargetListener {
		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (isDragAcceptable(e))
				e.acceptDrag(e.getDropAction());
			else
				e.rejectDrag();
		}

		@Override
		public void dragExit(DropTargetEvent e) {
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
		}

		private Point _glassPt = new Point();

		@Override
		public void dragOver(final DropTargetDragEvent e) {
			Point glassPt = e.getLocation();
			if (getTabPlacement() == JTabbedPane.TOP
					|| getTabPlacement() == JTabbedPane.BOTTOM) {
				initTargetLeftRightLine(getTargetTabIndex(glassPt));

			} else {
				initTargetTopBottomLine(getTargetTabIndex(glassPt));
			}
			if (hasGhost()) {
				glassPane.setPoint(glassPt);
			}
			if (!_glassPt.equals(glassPt))
				glassPane.repaint();
			_glassPt = glassPt;
			autoScrollTest(glassPt);
		}

		@Override
		public void drop(DropTargetDropEvent e) {

			if (isDropAcceptable(e)) {
				convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
				e.dropComplete(true);
			} else {
				e.dropComplete(false);
			}
			repaint();
		}

		private boolean isDragAcceptable(DropTargetDragEvent e) {
			Transferable t = e.getTransferable();
			if (t == null)
				return false;
			DataFlavor[] f = e.getCurrentDataFlavors();
			if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
				return true;
			}
			return false;
		}

		private boolean isDropAcceptable(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			if (t == null)
				return false;
			DataFlavor[] f = t.getTransferDataFlavors();
			if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
				return true;
			}
			return false;
		}
	}

	private boolean hasGhost = true;

	public void setPaintGhost(boolean flag) {
		hasGhost = flag;
	}

	public boolean hasGhost() {
		return hasGhost;
	}

	private boolean isPaintScrollArea = true;

	public void setPaintScrollArea(boolean flag) {
		isPaintScrollArea = flag;
	}

	public boolean isPaintScrollArea() {
		return isPaintScrollArea;
	}

	private int getTargetTabIndex(Point glassPt) {
		Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt,
				DnDTabbedPane.this);
		boolean isTB = getTabPlacement() == JTabbedPane.TOP
				|| getTabPlacement() == JTabbedPane.BOTTOM;
		for (int i = 0; i < getTabCount(); i++) {
			Rectangle r = getBoundsAt(i);
			if (isTB)
				r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
			else
				r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
			if (r.contains(tabPt))
				return i;
		}
		Rectangle r = getBoundsAt(getTabCount() - 1);
		if (isTB)
			r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
		else
			r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
		return r.contains(tabPt) ? getTabCount() : -1;
	}

	private void convertTab(int prev, int next) {
		if (next < 0 || prev == next) {
			return;
		}
		Component cmp = getComponentAt(prev);
		Component tab = getTabComponentAt(prev);
		String str = getTitleAt(prev);
		Icon icon = getIconAt(prev);
		String tip = getToolTipTextAt(prev);
		boolean flg = isEnabledAt(prev);
		int tgtindex = prev > next ? next : next - 1;
		remove(prev);
		insertTab(str, icon, cmp, tip, tgtindex);
		setEnabledAt(tgtindex, flg);
		// When you drag'n'drop a disabled tab, it finishes enabled and
		// selected.
		// pointed out by dlorde
		if (flg)
			setSelectedIndex(tgtindex);

		// I have a component in all tabs (jlabel with an X to close the tab)
		// and when i move a tab the component disappear.
		// pointed out by Daniel Dario Morales Salas
		setTabComponentAt(tgtindex, tab);
	}

	private void initTargetLeftRightLine(int next) {
		if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
			lineRect.setRect(0, 0, 0, 0);
		} else if (next == 0) {
			Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0),
					glassPane);
			lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
		} else {
			Rectangle r = SwingUtilities.convertRectangle(this,
					getBoundsAt(next - 1), glassPane);
			lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH,
					r.height);
		}
	}

	private void initTargetTopBottomLine(int next) {
		if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
			lineRect.setRect(0, 0, 0, 0);
		} else if (next == 0) {
			Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0),
					glassPane);
			lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
		} else {
			Rectangle r = SwingUtilities.convertRectangle(this,
					getBoundsAt(next - 1), glassPane);
			lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width,
					LINEWIDTH);
		}
	}

	private void initGlassPane(Component c, Point tabPt) {
		getRootPane().setGlassPane(glassPane);
		if (hasGhost()) {
			Rectangle rect = getBoundsAt(dragTabIndex);
			BufferedImage image = new BufferedImage(c.getWidth(),
					c.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			c.paint(g);
			rect.x = rect.x < 0 ? 0 : rect.x;
			rect.y = rect.y < 0 ? 0 : rect.y;
			image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			glassPane.setImage(image);
		}
		Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
		glassPane.setPoint(glassPt);
		glassPane.setVisible(true);
	}

	private Rectangle getTabAreaBounds() {
		Rectangle tabbedRect = getBounds();

		Component comp = getSelectedComponent();
		int idx = 0;
		while (comp == null && idx < getTabCount())
			comp = getComponentAt(idx++);
		Rectangle compRect = (comp == null) ? new Rectangle() : comp
				.getBounds();
		int tabPlacement = getTabPlacement();
		if (tabPlacement == TOP) {
			tabbedRect.height = tabbedRect.height - compRect.height;
		} else if (tabPlacement == BOTTOM) {
			tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
			tabbedRect.height = tabbedRect.height - compRect.height;
		} else if (tabPlacement == LEFT) {
			tabbedRect.width = tabbedRect.width - compRect.width;
		} else if (tabPlacement == RIGHT) {
			tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
			tabbedRect.width = tabbedRect.width - compRect.width;
		}
		tabbedRect.grow(2, 2);
		return tabbedRect;
	}

	class GhostGlassPane extends JPanel {
		/**
	 * 
	 */
		private static final long serialVersionUID = 2100818062275929372L;
		private final AlphaComposite composite;
		private Point location = new Point(0, 0);
		private BufferedImage draggingGhost = null;

		public GhostGlassPane() {
			setOpaque(false);
			composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					0.5f);
			// http://bugs.sun.com/view_bug.do?bug_id=6700748
			// setCursor(null);
		}

		public void setImage(BufferedImage draggingGhost) {
			this.draggingGhost = draggingGhost;
		}

		public void setPoint(Point location) {
			this.location = location;
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(composite);
			if (isPaintScrollArea()
					&& getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
				g2.setPaint(Color.RED);
				g2.fill(rBackward);
				g2.fill(rForward);
			}
			if (draggingGhost != null) {
				double xx = location.getX()
						- (draggingGhost.getWidth(this) / 2d);
				double yy = location.getY()
						- (draggingGhost.getHeight(this) / 2d);
				g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
			}
			if (dragTabIndex >= 0) {
				g2.setPaint(lineColor);
				g2.fill(lineRect);
			}
		}
	}
}