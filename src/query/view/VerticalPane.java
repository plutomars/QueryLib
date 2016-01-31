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
 *          直式ScrollPane(含JTable)
 * 
 */
public class VerticalPane extends JScrollPane {

	private static final long serialVersionUID = 1L;
	private JTable headerTable, dataTable;
	private Object referObject;

	public VerticalPane() {

	}

	/**
	 * 產生直式ScrollPane，並加入一個不含表頭文字之JTable。 表頭文字將由另一個JTable顯示。
	 * 
	 * @param headers
	 *            表頭字串Array
	 * @param tblTitle
	 *            表格標題
	 * @param headerWidth
	 *            表頭欄位寬度
	 * @param cellHeight
	 *            表內格子高度
	 */
	public VerticalPane(String[] headers, String tblTitle, int headerWidth,
			int cellHeight) {
		setTableFormat(headers, tblTitle, headerWidth, cellHeight);
	}

	public void setTableFormat(String[] headers, String tblTitle,
			int headerWidth, int cellHeight) {

		// 表格標題設定
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

		// 表頭Table設定
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

		// 表頭Panel
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setPreferredSize(new Dimension(headerTable.getTableHeader()
				.getPreferredSize().width, headerTable.getSize().height));
		headerPanel.add(headerTable, BorderLayout.CENTER);
		setRowHeaderView(headerPanel);

		// 內容Table設定
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

		// 內容Panel
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
	 * 設定欲顯示之表頭標題內容。 標題內容需自行確認與表格內容順序相同。
	 * 
	 * @param values
	 *            標題字串Array
	 */
	private void setHeader(String[] header, String tblHead) {
		for (int i = 0; i < header.length; i++) {
			headerTable.setValueAt(header[i], i, 0);
		}
	}

	/**
	 * 設定欲顯示之表格數值內容。 數值內容需自行確認與表頭標題順序相同。
	 * 
	 * @param values
	 *            內容字串Array
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
