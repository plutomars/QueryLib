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
	 * �إ�DATABASE CONNECTION����w��m
	 * 
	 * @param account
	 *            �s�u�b��
	 * @param password
	 *            �s�u�K�X
	 * @param type
	 *            �s�u��m�p(hp_tn,hp_dn)
	 * @return boolean TRUE/FALSE �s�u�إߦ��\�P�_
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
				connected = true;   // ���\�s�u�B�ƭȥ��`��~�]��true
				}
			else{
				if (JOptionPane.showConfirmDialog(null, getServerName()+"���B���������A,�O�_�����ܳƴ����ҡH",
						"�ЦA���@���n�J", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
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
				if (JOptionPane.showConfirmDialog(null, "�L�k�s��"+ getServerName()+",�O�_�����ܳƴ����ҡH",
						"�ЦA���@���n�J", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
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
	 * ���oConnection �s�u����
	 * 
	 * @return Connection �s�������\�^��null,���\���s�u�^��Connection
	 */
	public static Connection getConn() {
		if (connected)
			return conn;
		else
			return null;
	}

	/**
	 * �إߤ@statement�Ѱ���SQL�y�k
	 * 
	 * @return Statement �s�������\�^��null,���\���s�u�^��Statement
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
	 * �إߤ@statement�Ѱ���SQL�y�k
	 * 
	 * @return Statement �s�������\�^��null,���\���s�u�^��Statement
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
	 * ����SQL���O
	 * <p>
	 * ��JSQL�y�k�ð���,�ݦ��\�إ߳s�uDBConnection
	 * 
	 * @param stmt
	 *            ������SQL�y�k��Statement ����
	 * @param sql
	 *            SQL�y�k,�p"select * from polf"
	 * @return ResultSet SQL���O���浲�G
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
	 * �N�����ResultSet��JCollection(HashMap)�æ^�� , �bHashMap������ϥ�column name ���o�������
	 * <p>
	 * ex.List<HashMap<String, String>> v = a.getResultList("677500092478");<br>
	 * for (HashMap<String, String> aResult : v) {<br>
	 * HashMap<String, String> hashmap = aResult;<br>
	 * for (String name:hashmap.keySet()) { <br>
	 * System.out.print(name + "\t" + hashmap.get(name)); <br>
	 * } System.out.println(""); }
	 * 
	 * @param rs
	 *            �w����SQL�ᤧ����ResultSet ����
	 * @return List<HashMap<String,String>> �^�Ǧs��List����HashMap���
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
	 * ����Statement
	 * 
	 * @param stmt
	 *            �ǤJ��������Statement
	 */
	public static void closeStatement(Statement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			showErrowMessage(e.getMessage()+"-10");
		}
	}

	/**
	 * �����M��ƮwCONNECTION
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
    	   return "��������";
    	else if (DBConnection.DBSERVER.equals("hp_pn"))
    		return "��������";
    	else
		return "�ƴ�����";
    }	
}
