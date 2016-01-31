package query.controller;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.connection.DBConnection;

/**
 * @ * @author FYYANG
 * 
 * @version 1.0<br>
 *          100/07/28<br>
 *          控制Model和view之間的資料交換<br>
 *          要實作三個方法<br>
 *          1.executeMission<br>
 * 
 * @since 1.0
 */
public abstract class DataControl {
	protected boolean isErr = false;
	protected StringBuilder errMes = new StringBuilder();

	/**
	 * 各實做此介面的類別的須覆寫此方法
	 * <p>
	 * 執行各任務
	 * 
	 */
	public abstract void executeMission();

	/**
	 * 各實做此介面的類別的須覆寫此方法
	 * <p>
	 * 依據輸入之保單號碼取得對應之資料並在Table中做好Binding
	 * 
	 * @param policy_info
	 *            [] 保單號碼,客戶ID,客戶姓名
	 */
	public abstract void setPolicyInfo(String[] policy_info);

	/**
	 * 各實做此介面的類別的須覆寫此方法
	 * <p>
	 * 依據是否擁有主控權來決定保單查詢按扭要為Enable or disable
	 * 
	 */
	public abstract boolean isControllable();

	/**
	 * 各實做此介面的類別的須覆寫此方法
	 * <p>
	 * 取回上次執行時之保單號碼<br>
	 * policy_no由executeMission方法呼叫時設置
	 * 
	 * @return policy_no 保單號碼
	 */
	public abstract String getOldPolicy_no();

	/**
	 * 各實做此介面的類別的須覆寫此方法
	 * <p>
	 * 取得各Controller對應控管之畫面Panel
	 * 
	 * @param JPanel
	 */
	public abstract JPanel getPanel();

	public void clearMission() {
		executeClearMission();
		executeNullMission();
		getPanel().removeAll();
		System.gc();
	}
	/**
	 * 供clearMission執行清空List 內內容
	 */
	public abstract void executeClearMission();

	/**
	 * 供clearMission執行清空Null 全域變數
	 */
	public abstract void executeNullMission();

	protected List<HashMap<String, String>> setErrResultInd(
			List<HashMap<String, String>> list, String storeProcedure) {
		this.saveLog(storeProcedure);
		if (list == null)
			return null;
		if (list.size() > 0) {
			HashMap<String, String> hsmp = list.get(0);
			try {
				if (hsmp.get("r_result_ind") == null) {
					return list;
				} else if (!hsmp.get("r_result_ind").equals("0")) {
					isErr = true;
					errMes.append(storeProcedure + "("
							+ hsmp.get("r_result_ind") + ")");
				}
			} catch (NullPointerException e) {
				return list;
			}

		}
		return list;
	}

	protected void showMessageDialog(JPanel panel, String panelname,
			String message) {
		if (isErr)
			JOptionPane.showMessageDialog(panel, errMes + message, panelname,
					JOptionPane.INFORMATION_MESSAGE);
		errMes = new StringBuilder();
		isErr = false;
	}

	protected void saveLog(String ProcedureName) {
		// System.out.println("bgn in err = "+DBConnection.bgnTime);
		SimpleDateFormat fmtBgn = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date endTime = new Date();
		long diffTime = (endTime.getTime() - DBConnection.bgnTime.getTime());
		// System.out.println("end in err = "+endTime+" intvl = "+ diffTime);
		Statement st = DBConnection.createStatement();
		String cmd = "insert into cclg values ('" + ProcedureName + "','','"
				+ DBConnection.getUser() + "','"
				+ fmtBgn.format(DBConnection.bgnTime) + "','"
				+ (double) diffTime / 1000 + "')";
		// System.out.println("cmd = "+cmd);
		try {
			st.execute(cmd);
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
