package query.controller;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
public class HighlightTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final Color rollOverRowColor = Color.yellow;
	public String q;

	@Override
	public void updateUI() {
		setTextSelectionColor(null);
		setTextNonSelectionColor(null);
		setBackgroundSelectionColor(null);
		setBackgroundNonSelectionColor(null);
		super.updateUI();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JComponent c = (JComponent) super.getTreeCellRendererComponent(tree,
				value, isSelected, expanded, leaf, row, hasFocus);
		
		// DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		if (isSelected) {
			c.setOpaque(false);
			c.setForeground(getTextSelectionColor());
			// c.setBackground(Color.BLUE); //getBackgroundSelectionColor());
		} else {
			c.setOpaque(true);
			String a = ""+value.toString();
			if (q != null && !q.isEmpty() && a.contains(q)) {
				c.setForeground(getTextNonSelectionColor());
				c.setBackground(rollOverRowColor);
			} else {
				c.setForeground(getTextNonSelectionColor());
				c.setBackground(getBackgroundNonSelectionColor());
			}
		}
		return c;
	}
}