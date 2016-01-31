package query.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class TipJTable {
	 JTable tipTable ;

	public TipJTable(final String tipText){
		tipTable = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIdx, int colIdx) {
				return false;
			}
			
			public String getToolTipText(MouseEvent me) {
				String tooltip = null;
				tooltip = tipText;
				return tooltip;
			}
		};
		
		
		tipTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				tipTable.getToolTipText(arg0);
			}
		});
//		return tipTable;
	}
	
	public JTable getTable()
	{
		
		return tipTable;
	}
}
