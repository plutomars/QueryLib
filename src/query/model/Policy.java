package query.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import database.connection.DBConnection;

/**
 * @author FYYANG
 * 
 @version 1.0,100/06/30
 * 
 @since 1.0
 */
public class Policy {

	/**
	 * �^�ǳ̪�ΤU���O��g�~��
	 * <p>
	 * ��J�O��ͮĤ���H�Υثe���,���|�~���ˮ֨æ^�ǫO��g�~��
	 * 
	 * @param issue_date
	 *            �O��ͮĤ��
	 * @param curr_date
	 *            �ثe���
	 * @param next_flag
	 *            TRUE/�U���O��g�~��,FALSE/�W���O��g�~��
	 * @return date �O��g�~��
	 */
	public static String policyAnniversary(String issue_date, String curr_date,
			boolean next_flag) {
		Calendar cal = Calendar.getInstance(); // �Ψӭp��|�~

		// �Y issue_date �Ϊ� curr_date �� NULL �h�Ǧ^ NULL
		if (issue_date.length() == 0 || curr_date.length() == 0) {
			return null;
		}

		// �N�ͮĤ���Υثe����ഫ���~�B��B����
		int issue_arr[] = separateYMD(issue_date);
		int curr_arr[] = separateYMD(curr_date);

		// �D���g�~��
		// �Y�ͮĤ鬰�|�~2��29��ɻݰ��S��վ�
		issue_arr[0] = curr_arr[0];

		if (issue_arr[1] == 2 && issue_arr[2] == 29 && curr_arr[1] == 2
				&& curr_arr[2] == 28
				&& !((GregorianCalendar) cal).isLeapYear(curr_arr[0] + 1911)) {
			curr_arr[2] = 29;
		}

		if (issue_arr[1] > curr_arr[1]
				|| (issue_arr[1] == curr_arr[1] && curr_arr[2] >= issue_arr[2])
				&& next_flag) {
			issue_arr[0] = issue_arr[0] + 1;
		}

		if (issue_arr[1] < curr_arr[1]
				|| (issue_arr[1] == curr_arr[1] && curr_arr[2] < issue_arr[2])
				&& !next_flag) {
			issue_arr[0] = issue_arr[0] + 1;
		}

		if (issue_arr[1] == 2 && issue_arr[2] == 29
				&& !((GregorianCalendar) cal).isLeapYear(curr_arr[0] + 1911)) {
			issue_arr[2] = 28;
		}

		return getDate(issue_arr[0], issue_arr[1], issue_arr[2]);
	}

	/**
	 * �N���ꤧ[�~][��][��]�����X��
	 * <p>
	 * ��J������,�p 100/05/05->�E�X
	 * 
	 * @param date
	 *            ������
	 * @return int[] array[0]�~,array[1]��,array[2]��
	 */
	public static int[] separateYMD(String date) {
		int seperatedate[] = new int[3];
		
		seperatedate[0] = Integer.parseInt(date.substring(0, 3));
		seperatedate[1] = Integer.parseInt(date.substring(4, 6));
		seperatedate[2] = Integer.parseInt(date.substring(7, 9));

		return seperatedate;
	}

	/**
	 * ���o������
	 * <p>
	 * ��J[�~][��][��]���o������->�E�X
	 * 
	 * @param year
	 *            ����~
	 * @param month
	 *            �����
	 * @param day
	 *            �����
	 * @return date ������,�p100/05/05
	 */
	public static String getDate(int year, int month, int day) {
		String date;
		if (year < 100)
			date = "0" + year;
		else
			date = "" + year;

		if (month < 10)
			date = date + "0" + month;
		else
			date = date + month;

		if (day < 10)
			date = date + "0" + day;
		else
			date = date + day;
		return date;
	}

	public static String[] getRemDate(String policy_no, String po_sts_code,
			String insurance_type, String paid_to_date_coi, String paid_to_date) {
		try {
			String rem1_d = "";
			String rem2_d = "";
			String ol_rem_d = "";
			String as_date = "";
			String ptd = "";
			String form = "";

			// database connection (account,password,database)
			// DBConnection dbcon = new DBConnection("mis","fool01","hp_tn");
			ResultSet rs;
			Statement st = DBConnection.createStatement();
			if (po_sts_code.trim().equals("74")) {
				rs = DBConnection.executeQuery(st,
						"SELECT MAX(crt_date) max FROM psol"
								+ " WHERE policy_no = '" + policy_no
								+ "' AND ol_sw=5");
				if (rs.next())
					ol_rem_d = rs.getString("max");
			} else {
				rs = DBConnection.executeQuery(st,
						"SELECT MAX(crt_date) max FROM psol"
								+ " WHERE policy_no = '" + policy_no
								+ "' AND ol_sw=0");
				if (rs.next())
					ol_rem_d = rs.getString("max");
			}
			if (ol_rem_d == null)
				ol_rem_d = "";

			int tmp_sts_code = Integer.parseInt(po_sts_code);
			if (tmp_sts_code <= 46) {
				return null;
			}

			if (tmp_sts_code == 47 || tmp_sts_code == 48 || tmp_sts_code == 50
					|| tmp_sts_code == 66 || tmp_sts_code == 67
					|| tmp_sts_code == 73 || tmp_sts_code == 74) {
				if (insurance_type.trim().equals("V")
						|| insurance_type.trim().equals("U")
						|| insurance_type.trim().equals("N"))
					ptd = paid_to_date_coi;
				else
					ptd = paid_to_date;
			}

			rs = DBConnection.executeQuery(st,
					"SELECT as_date,form_type,crt_date FROM psrm"
							+ " WHERE policy_no = '" + policy_no
							+ "' AND paid_to_date = '" + ptd
							+ "' AND rem_flag in (' ','4')"
							+ " AND process_date in"
							+ "     (SELECT MAX(process_date) FROM psrm"
							+ " WHERE policy_no = '" + policy_no
							+ "' AND paid_to_date = '" + ptd
							+ "' AND rem_flag in (' ','4'))");
			if (rs.next()) {
				as_date = rs.getString("as_date");
				form = rs.getString("form_type");
				rem1_d = rs.getString("crt_date");
			}

			if (form.trim().endsWith("0") || form.trim().endsWith("4")
					|| form.trim().endsWith("5") || form.trim().endsWith("6"))
				rem1_d = "";

			rs = DBConnection.executeQuery(st,
					"SELECT as_date,crt_date FROM psrm"
							+ " WHERE policy_no = '" + policy_no
							+ "' AND paid_to_date = '" + ptd
							+ "' AND rem_flag in ('2','5')"
							+ " AND process_date in"
							+ "     (SELECT MAX(process_date) FROM psrm"
							+ " WHERE policy_no = '" + policy_no
							+ "' AND paid_to_date = '" + ptd
							+ "' AND rem_flag in ('2','5'))");

			if (rs.next()) {
				as_date = rs.getString("as_date");
				rem2_d = rs.getString("crt_date");
			}

			String args[] = new String[4];
			args[0] = rem1_d;
			args[1] = rem2_d;
			args[2] = ol_rem_d;
			args[3] = as_date;

			System.out.println(args[0]);
			System.out.println(args[1]);
			System.out.println(args[2]);
			System.out.println(args[3]);

			return args;
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	/**
	 * ���o���������t�Z
	 * <p>
	 * ��J[�~][��][��]���o������->�E�X
	 * 
	 * @param bgnDate
	 *            ����~
	 * @param endDate
	 *            �����

	 * @return int ���������Ѽ�
	 */
	public static int period(String bgnDate, String endDate) {
		int[] datebgn = separateYMD(bgnDate);
		int[] dateend = separateYMD(endDate);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date t1 = null;
		Date t2 = null;
		try {

			t1 = formatter.parse((datebgn[0]+1911)+"/"+datebgn[1]+"/"+datebgn[2]);
			t2 = formatter.parse((dateend[0]+1911)+"/"+dateend[1]+"/"+dateend[2]);

		} catch (java.text.ParseException e) {

			System.out.println("unparseable using" + formatter);

		}

		long diff = t2.getTime() - t1.getTime();

		int days = (int)(diff / (1000 * 60 * 60 * 24));

		return days;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Policy.policyAnniversary("089/02/29", "121/02/28", true);
//		Policy.getRemDate("110300016787", "66", "D", "", "088/03/07");
		System.out.println(Policy.period("096/10/11","096/10/12"));
	}

}
