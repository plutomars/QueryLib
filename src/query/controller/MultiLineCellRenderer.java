package query.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
/**
 * 
 * @author fyyang
 *
 */
public class MultiLineCellRenderer extends JTextArea implements
		TableCellRenderer {
	private boolean header = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultiLineCellRenderer() {
		this(false);
	}

	public MultiLineCellRenderer(boolean header) {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);

		this.header = header;
		if (header) {
			setFont(new Font("新細明體", Font.PLAIN, 15));
			this.setBorder(UIManager.getBorder("CheckBox.border"));
			setBackground(UIManager.getColor("TableHeader.background"));
		} else {
			setFont(new Font("新細明體", Font.PLAIN, 13));
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		}
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (!header) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			if (hasFocus) {
				if (table.isCellEditable(row, column)) {
					setForeground(UIManager
							.getColor("Table.focusCellForeground"));
					setBackground(UIManager
							.getColor("Table.focusCellBackground"));

				}
			} else {
				setBorder(new EmptyBorder(1, 2, 1, 2));
			}
			if (condition(table, value, row, column)) {
				setBackground(getColor());
				setForeground(getFColor());
			}
		}
		setText((value == null) ? "" : value.toString());
		return this;
	}

	public Color getColor() {
		return Color.red;
	}

	public Color getFColor() {
		return Color.BLACK;
	}

	public boolean condition(JTable table, Object value, int row, int column) {
		return false;
	}
}