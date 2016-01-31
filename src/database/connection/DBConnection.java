package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import query.controller.FlowerControl;

/**
 * @ * @author FYYANG
 * 
 * @version 1.0,100/06/30
 * 
 @since 1.0
 */
public class DBConnection {
	private static Connection conn;
	private static boolean connected = false;
	private static String user;
	public  static String DBSERVER = "hp_tn";
	private static String SecServer = "hp_dn";
	private static String urlA = "jdbc:informix-sqli://172.25.1.2:1528:informixserver=";
	private static String urlB = "jdbc:informix-sqli://172.25.1.2:1528:informixserver=";
	private static FlowerControl controller;
	public static Date bgnTime;
	public static String user_dept;

	/**
	 * 建立DATABASE CONNECTION到指定位置
	 * 
	 * @param account
	 *            連線帳號
	 * @param password
	 *            連線密碼
	 * @param type
	 *            連線位置如(hp_tn,hp_dn)
	 * @return boolean TRUE/FALSE 連線建立成功與否
	 */
	public static boolean connect(String account, String password, String type) {
		String url = urlA + type + ";user=" + account + ";password=" + password;
		user = account;
		StringTokenizer st = new StringTokenizer(url, ":");
		String token;
		String newUrl = "";
		for (int i = 0; i < 4; ++i) {
			if (!st.hasMoreTokens()) {
				JOptionPane.showMessageDialog(null,
						"FAILED: incorrect URL format!", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				connected = false;
				return connected;
			}
			token = st.nextToken();
			if (newUrl != "")
				newUrl += ":";
			newUrl += token;
		}

		newUrl += "/life";

		while (st.hasMoreTokens()) {
			newUrl += ":" + st.nextToken();
		}
		try {
			Class.forName("com.informix.jdbc.IfxDriver");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"FAILED: failed to load Informix JDBC driver.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	
		try {
			Properties pr = new Properties();
			pr.put("IFX_USE_STRENC", "true");
			pr.put("CLIENT_LOCALE", "zh_TW.big5");
			pr.put("DB_LOCALE", "zh_TW.big5");
			pr.put("DBLANG", "zh_TW.big5");
			pr.put("NEWCODESET", "MS950,big5,57352");

			conn = DriverManager.getConnection(newUrl, pr);

			Statement ps = conn.createStatement();
			List<HashMap<String, String>> list = getResultList(ps.executeQuery("select nvl(a.desc,'3') desc,b.dept_code dept_code"+
																			   " from etab a,edp_base:usrdat b"+ 
			                                                                   " where a.code = 'CC' and a.e_type = 'ENV'"+
																			   " and b.user_code = '"+ user +"'"));
			if (list.get(0).get("desc").toString().trim().equals("0") )
				{
				user_dept = list.get(0).get("dept_code").toString().trim();
				connected = true;   // 成功連線且數值正常後才設為true
				}
			else{
				if (JOptionPane.showConfirmDialog(null, getServerName()+"正處於關機狀態,是否切換至備援環境？",
						"請再按一次登入", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
				{
					urlA = urlB;
					DBSERVER = SecServer;
				}
				connected = false;
			}
			ps.close();
			
		} catch (SQLException e) {
			if (e.getErrorCode() != -951)
			{

//				System.out.println(urlIP);
//				System.out.println("newUrl"+newUrl);
				if (JOptionPane.showConfirmDialog(null, "無法連接"+ getServerName()+",是否切換至備援環境？",
						"請再按一次登入", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
				{
//					System.out.println("urlIP");
					urlA = urlB;
					DBSERVER = SecServer;
				}
				
			}else
			JOptionPane.showMessageDialog(null, "FAILED: " + e.getMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return connected;
	}

	public static String getUser() {
		return user;
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
        bgnTime = new Date();
//		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//	    System.out.println("bgn = "+fmt.format(bgnTime));
		try {
			if (connected)
				return conn.createStatement();
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-1");
		}
		return null;
	}

	public static Statement createLiteStatement() {
		try {
//			Class.forName("org.sqlite.JDBC");
//
//			Connection connLite = DriverManager
//					.getConnection("jdbc:sqlite:test.db");

			// return conn.createStatement();
			return DBLiteConnection.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR1",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
		// return null;
	}

	/**
	 * 建立一statement供執行SQL語法
	 * 
	 * @return Statement 連結未成功回傳null,成功的連線回傳Statement
	 */
	public static PreparedStatement createPreparedStatement(String sql) {
		try {
			// if (connected)
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-2");
		}
		return null;
	}

	public static void setPreparedStatementString(PreparedStatement ps,
			int index, String value) {
		try {
			// if (connected)
			ps.setString(index, value);
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-3");
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
			showErrowMessage(e.getMessage()+"-4");
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-5");
		}
		return rs;
	}

	public static ResultSet executeQuery(PreparedStatement stmt) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery();
		} catch (NullPointerException e) {
			showErrowMessage(e.getMessage()+"-6");
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-7");
		}
		return rs;
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
//			showErrowMessage(e.getMessage()+"-8");
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-9");
		}
		return arrlist1;
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
			showErrowMessage(e.getMessage()+"-10");
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
			showErrowMessage(e.getMessage()+"-11");
		}

	}

	public static void stopSearching(Statement stmt) {
		try {
			stmt.cancel();
		} catch (NullPointerException e) {
			
//			showErrowMessage(e.getMessage()+"-8");
		}catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-12");
		}
	}

	public static void setFlowerControl(FlowerControl control) {
		controller = control;
	}

	private static void showErrowMessage(String mess) {
		JOptionPane.showMessageDialog(null, mess, "ERROR",
				JOptionPane.ERROR_MESSAGE);
		controller.closeFlower();
	}
	
    public static String getServerName()
    {
    	if (DBConnection.DBSERVER.equals("hp_tn"))
    	   return "測試環境";
    	else if (DBConnection.DBSERVER.equals("hp_pn"))
    		return "正式環境";
    	else
		return "備援環境";
    }	
}
