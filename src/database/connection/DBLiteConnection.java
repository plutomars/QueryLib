package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * @ * @author FYYANG
 * 
 * @version 1.0,100/10/04
 * 
 @since 1.0
 */
public class DBLiteConnection{
	private static Connection conn;
	private static boolean connected = false;
	
	/**
	 * 建立DATABASE CONNECTION到指定位置
	 * 
	 * 
	 * @return boolean TRUE/FALSE 連線建立成功與否
	 */

	public static boolean connect() {
		try {
			Class.forName("org.sqlite.JDBC");

			conn = DriverManager.getConnection("jdbc:sqlite:test.db");
			connected = true;
			Statement stat = DBLiteConnection.createStatement();
			stat.execute("drop table if exists jpolf;");

			String tabColumns = "create table jpolf (policy_no ,currency,po_sts_code,modx,method,basic_plan_code"
					+ ",basic_rate_scale,po_issue_date,bill_to_date,paid_to_date,last_check_date"
					+ ",mode_prem,prem_susp,edp_susp,misc_susp,mail_addr_ind,home_addr_ind"
					+ ",app_apply_date,in_force_date,matured_date,expired_date,loan_date,loan_amt"
					+ ",div_option ,div_desc"
					+ ",accumulated_div,total_div_declared,auto_pay_ind,uw_write_date,other_susp"
					+ ",insurance_type,free_look_opt,misc_susp_check,plan_prem"
					+ ",policy_year,deposit_amt_accu ,nf_option ,reinstate_apply"
					+ ",po_sub_sts_code,apl_date ,apl_amt ,sign_paper_date"
					+ ",sign_input_date,paid_to_date_coi,gap_start_date,group_code"
					+ ",part_wd_amt_accu,ori_target_prem,target_prem,prem_refund_year"
					+ ",free_part_wd_yr,tg_prem_last_year,es_prem_last_year,prem_refund_flag"
					+ ",free_part_wd_cnt,tg_prem_this_year,es_prem_this_year,non_lapse_flag"
					+ ",free_switching_yr,tg_prem_next_year,es_prem_next_year,free_switching_mn"
					+ ",target_prem_accu,excess_prem_accu,free_switching_cnt,fy_billing_ind,class_type"
					+ ",o1_id,o1_name,o1_sex,o1_birth,o1_occu" // ,o1_occu_level"
					+ ",i1_id,i1_name,i1_sex,i1_birth,i1_occu" // ,i1_occu_level"
					+ ",mail_zip_code,mail_addr,mail_tel"
					+ ",mail_fax,mail_addr_sts,s_ag_code,s_ag_name,o1_sign_ptn_card,i1_sign_ptn_card);";
			stat.execute(tabColumns);
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "FAILED: " + e.getMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
			connected = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"FAILED: failed to load Informix JDBC driver.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			connected = false;
		}
		return connected;
	}
	/**
	 * 取得Connection 連線物件
	 * 
	 * @return Connection 連結未成功回傳null,成功的連線回傳Connection
	 */
	public static Connection getConn() {
		if (connected)
			return conn;
		else
			return null;
	}

	/**
	 * 建立一statement供執行SQL語法
	 * 
	 * @return Statement 連結未成功回傳null,成功的連線回傳Statement
	 */
	public static Statement createStatement() {
		try {
			if (connected)
				return conn.createStatement();
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return null;
	}

	/**
	 * 關閉Statement
	 * 
	 * @param stmt
	 *            傳入欲關閉之Statement
	 */
	public static void closeStatement(Statement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
	}

	/**
	 * 關閉和資料庫CONNECTION
	 */
	public static void closeCon() {
		try {
			connected = false;
			conn.close();
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}

	}

	/**
	 * 建立一statement供執行SQL語法
	 * 
	 * @return Statement 連結未成功回傳null,成功的連線回傳Statement
	 */
	public static PreparedStatement createPreparedStatement(String sql) {
		try {
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return null;
	}

	public static void setPreparedStatementString(PreparedStatement ps,
			int index, String value) {
		try {
			ps.setString(index, value);
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
	}

	/**
	 * 執行SQL指令
	 * <p>
	 * 輸入SQL語法並執行,需成功建立連線DBConnection
	 * 
	 * @param stmt
	 *            欲執行SQL語法之Statement 物件
	 * @param sql
	 *            SQL語法,如"select * from polf"
	 * @return ResultSet SQL指令執行結果
	 */
	public static ResultSet executeQuery(Statement stmt, String sql) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage());
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return rs;
	}

	public static ResultSet executeQuery(PreparedStatement stmt) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();
		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage());
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return rs;
	}
	public static int executeUpdate(PreparedStatement stmt) {
		int i=0;
		try {
			i = stmt.executeUpdate();
		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage());
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return i;
	}
	public static int executeUpdate(Statement stmt,String sql) {
		int i=0;
		try {
			i = stmt.executeUpdate(sql);
		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage());
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return i;
	}

	/**
	 * 將執行後ResultSet放入Collection(HashMap)並回傳 , 在HashMap中能夠使用column name 取得對應資料
	 * <p>
	 * ex.List<HashMap<String, String>> v = a.getResultList("677500092478");<br>
	 * for (HashMap<String, String> aResult : v) {<br>
	 * HashMap<String, String> hashmap = aResult;<br>
	 * for (String name:hashmap.keySet()) { <br>
	 * System.out.print(name + "\t" + hashmap.get(name)); <br>
	 * } System.out.println(""); }
	 * 
	 * @param rs
	 *            已執行SQL後之有效ResultSet 物件
	 * @return List<HashMap<String,String>> 回傳存至List中的HashMap資料
	 */
	public static List<HashMap<String, String>> getResultList(ResultSet rs) {
		ArrayList<HashMap<String, String>> arrlist1 = null;
		try {
			arrlist1 = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> hmap;
			ResultSetMetaData mdrs = rs.getMetaData();
			while (rs.next()) {
				hmap = new HashMap<String, String>();
				for (int i = 1; i <= mdrs.getColumnCount(); i++) {
					hmap.put(mdrs.getColumnName(i), rs.getString(i));
				}
				arrlist1.add(hmap);
			}
			return arrlist1;

		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage());
		} catch (SQLException e) {
			showErrowMessage(e.getMessage());
		}
		return arrlist1;
	}

	protected static void showErrowMessage(String mess) {
		JOptionPane.showMessageDialog(null, mess, "ERROR",
				JOptionPane.ERROR_MESSAGE);
		// controller.closeFlower();
	}
	

}
