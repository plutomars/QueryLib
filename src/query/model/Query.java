package query.model;

import java.util.HashMap;
import java.util.List;

/**
 * @ * @author FYYANG
 * 
 * @version 1.0<br>
 *          100/07/28<br>
 *          查詢條件介面<br>
 *          實做此介面需覆寫此方法<br>
 *          回傳下達SQL語法後之資料供Controller和View做Binding
 * @since 1.0
 */
public interface Query {
	public static final int NOTYPE = -99;
	/**
	 * 
	 * 依據輸入之保單號碼取得對應之資料並在Table中做好Binding<br>
	 * 配合DBConnection.getResultList(ResultSet rs)做使用
	 * 
	 * @param policy_no
	 *            保單號碼
	 * @param type
	 *            若有兩個以上的取得SQL,依type做區分
	 */
	public List<HashMap<String, String>> getResultList(String policy_no,
			int type);
}
