package query.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * 
 * @author cychu
 * 
 * @version 1.0<br>
 *          100/08/04<br>
 *          ����ScrollPane(�tJTable)
 * 
 */
public class VerticalPane extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable headerTable, dataTable;
	private Object referObject;

	public VerticalPane() {

	}

	/**
	 * ���ͪ���ScrollPane�A�å[�J�@�Ӥ��t���Y��r��JTable�C ���Y��r�N�ѥt�@��JTable��ܡC
	 * 
	 * @param headers
	 *            ���Y�r��Array
	 * @param tblTitle
	 *            �����D
	 * @param headerWidth
	 *            ���Y���e��
	 * @param cellHeight
	 *            ����l����
	 */
	public VerticalPane(String[] headers, String tblTitle, int headerWidth,
			int cellHeight) {
		setTableFormat(headers, tblTitle, headerWidth, cellHeight);
	}

	public void setTableFormat(String[] headers, String tblTitle,
			int headerWidth, int cellHeight) {

		// �����D�]�w
		if (!tblTitle.trim().isEmpty()) {
			JLabel labelTitle = new JLabel(tblTitle, JLabel.LEADING);
			labelTitle.setForeground(Color.BLUE);
			JPanel titlePanel = new JPanel();
			titlePanel.setBackground(new Color(170, 240, 195));
			titlePanel.add(labelTitle);
			setColumnHeaderView(titlePanel);

			JPanel cornerPanel = new JPanel();
			cornerPanel.setBackground(new Color(170, 240, 195));
			setCorner(UPPER_LEFT_CORNER, cornerPanel);
		}

		// ���YTable�]�w
		headerTable = new JTable(headers.length, 1) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIdx, int colIdx) {
				return false;
			}
		};
		headerTable.setEnabled(false);

		setHeader(headers, tblTitle);
		headerTable.getColumn(headerTable.getColumnName(0)).setCellRenderer(
				new DefaultTableCellRenderer() {
					private static final long serialVersionUID = 1L;

					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						// Setup default cell attribute
						setForeground(Color.black);
						setBackground(new Color(185, 210, 255));
						setHorizontalAlignment(JLabel.LEFT);
						setValue(value==null?"":value.toString());
						return this;
					}
				});
		TableColumn headerCol = headerTable.getColumnModel().getColumn(0);
		headerCol.setPreferredWidth(headerWidth);
		headerTable.setRowHeight(cellHeight);

		// ���YPanel
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(headerTable.getTableHeader()
				.getPreferredSize().width, headerTable.getSize().height));
		headerPanel.add(headerTable, BorderLayout.CENTER);
		setRowHeaderView(headerPanel);

		// ���eTable�]�w
		dataTable = new JTable(headers.length, 1) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIdx, int colIdx) {
				return false;
			}
		};
		dataTable.setSelectionBackground(Color.WHITE);
		dataTable.setSelectionForeground(Color.BLACK);
		dataTable.setRowHeight(cellHeight);
		dataTable.setFillsViewportHeight(true);
		dataTable.getTableHeader().setVisible(false);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// ���ePanel
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout());
		dataPanel.add(dataTable, BorderLayout.CENTER);
		setViewportView(dataPanel);
	}

	public void setListSelectionListener(boolean set, Object refer) {
		if (set) {
			this.referObject = refer;
			dataTable.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							// TODO Auto-generated method stub
							if (((DefaultListSelectionModel) arg0.getSource())
									.getValueIsAdjusting()) {
								int row = dataTable.getSelectedRow();
								if (row != -1) {
									if (referObject instanceof JTextField)
										((JTextField) referObject)
												.setText(dataTable.getValueAt(
														row, 0).toString());
								}
							}
						}

					});
		}

	}


	/**
	 * �]�w����ܤ����Y���D���e�C ���D���e�ݦۦ�T�{�P��椺�e���ǬۦP�C
	 * 
	 * @param values
	 *            ���D�r��Array
	 */
	private void setHeader(String[] header, String tblHead) {
		for (int i = 0; i < header.length; i++) {
			headerTable.setValueAt(header[i], i, 0);
		}
	}

	/**
	 * �]�w����ܤ����ƭȤ��e�C �ƭȤ��e�ݦۦ�T�{�P���Y���D���ǬۦP�C
	 * 
	 * @param values
	 *            ���e�r��Array
	 */
	public void setValue(String[] values) {
		for (int i = 0; i < values.length; i++) {
			dataTable.setValueAt(values[i], i, 0);
		}
	}

	public String[] getValues() {
		int rows = dataTable.getRowCount();
		String values[] = new String[rows];

		for (int i = 0; i < rows; i++)
			values[i] = dataTable.getValueAt(i, 0).toString();
		return values;
	}
	
		

	public JTable getDataTable() {
		return dataTable;
	}

	public void clearValues() {
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			dataTable.setValueAt("", i, 0);
		}
	}
}
