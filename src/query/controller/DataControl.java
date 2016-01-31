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
 *          ����Model�Mview��������ƥ洫<br>
 *          �n��@�T�Ӥ�k<br>
 *          1.executeMission<br>
 * 
 * @since 1.0
 */
public abstract class DataControl {
	protected boolean isErr = false;
	protected StringBuilder errMes = new StringBuilder();

	/**
	 * �U�갵�����������O�����мg����k
	 * <p>
	 * ����U����
	 * 
	 */
	public abstract void executeMission();

	/**
	 * �U�갵�����������O�����мg����k
	 * <p>
	 * �̾ڿ�J���O�渹�X���o��������ƨæbTable�����nBinding
	 * 
	 * @param policy_info
	 *            [] �O�渹�X,�Ȥ�ID,�Ȥ�m�W
	 */
	public abstract void setPolicyInfo(String[] policy_info);

	/**
	 * �U�갵�����������O�����мg����k
	 * <p>
	 * �̾ڬO�_�֦��D���v�ӨM�w�O��d�߫���n��Enable or disable
	 * 
	 */
	public abstract boolean isControllable();

	/**
	 * �U�갵�����������O�����мg����k
	 * <p>
	 * ���^�W������ɤ��O�渹�X<br>
	 * policy_no��executeMission��k�I�s�ɳ]�m
	 * 
	 * @return policy_no �O�渹�X
	 */
	public abstract String getOldPolicy_no();

	/**
	 * �U�갵�����������O�����мg����k
	 * <p>
	 * ���o�UController�������ޤ��e��Panel
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
	 * ��clearMission����M��List �����e
	 */
	public abstract void executeClearMission();

	/**
	 * ��clearMission����M��Null �����ܼ�
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
