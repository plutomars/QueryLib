package query.model;

import java.util.HashMap;
import java.util.List;

/**
 * @ * @author FYYANG
 * 
 * @version 1.0<br>
 *          100/07/28<br>
 *          �d�߱��󤶭�<br>
 *          �갵���������мg����k<br>
 *          �^�ǤU�FSQL�y�k�ᤧ��ƨ�Controller�MView��Binding
 * @since 1.0
 */
public interface Query {
	public static final int NOTYPE = -99;
	/**
	 * 
	 * �̾ڿ�J���O�渹�X���o��������ƨæbTable�����nBinding<br>
	 * �t�XDBConnection.getResultList(ResultSet rs)���ϥ�
	 * 
	 * @param policy_no
	 *            �O�渹�X
	 * @param type
	 *            �Y����ӥH�W�����oSQL,��type���Ϥ�
	 */
	public List<HashMap<String, String>> getResultList(String policy_no,
			int type);
}
