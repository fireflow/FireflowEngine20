package org.fireflow.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.fireflow.demo.security.bean.Group;
import org.fireflow.demo.security.bean.OkErpPermission;
import org.fireflow.demo.security.bean.User;
import org.fireflow.demo.security.bean.UserRole;
import org.fireflow.engine.modules.persistence.nutz.support.ExAnnotationEntityMaker;
import org.nutz.dao.Cnd;
import org.nutz.dao.SqlManager;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

public class FireflowDemoDao extends NutDao {
	public static final String GROUP_CODE_KEY = "T_security_group";
	public static final String ORGANIZATION_CODE_KEY = "T_security_organization";
	public static final String COMPANY_CODE_KEY = "T_cmn_company";
	public static final String CONTRACT_CODE_KEY = "t_crm_contract";
	public static final String FINANCIAL_INVOICE_CODE_KEY = "t_financial_invoice_table";
	public static final String CRM_CONTRACT_SPLIT = "t_crm_contract_split";
	public static final String CRM_ORDER = "t_crm_order";
	public static final String CRM_INVOICE_REQUEST_DETAIL_KEY = "t_crm_invoice_request_detail";
	public static final String ROLE_CODE_KEY = "T_security_role";
	public static final String CRM_CONTRACT_FILE = "T_crm_contract_file";
	public static final String COMPANY_CONTACTS_CODE = "T_cmn_company_contacts";
	public static final String ORDER_CANCEL_APPLY_CODE = "T_crm_order_cancel_apply";
	public static final String ORDER_File_CODE = "T_crm_order_file";
	public static final String PROJECT_CODE = "T_crm_project";
	public static final String DEPARTMENT_CODE = "T_hr_departments";
	public static final String EMPLOYEE_BANK_ACCOUNT_CODE = "T_fnc_employee_bank_account";
	public static final String POSITION_CODE = "T_hr_position_expense";
	public FireflowDemoDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FireflowDemoDao(DataSource dataSource, SqlManager sqlManager) {
		super(dataSource, sqlManager);
		// TODO Auto-generated constructor stub
	}

	public FireflowDemoDao(DataSource dataSource) {
		super(dataSource);
		// TODO Auto-generated constructor stub
	}

	protected EntityMaker createEntityMaker() {
        return new ExAnnotationEntityMaker(dataSource, expert, holder);
    }
	
	/**
	 * 获得数据库系统时间
	 * @return
	 */
	public Date getSysDate(){
		Sql sql = Sqls.create("select now()");
		sql.setCallback(new SqlCallback() {
			public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				Date d = null;
				if (rs.next()){
					d = rs.getDate(1);
				}
					
				return d;
			}
		});
		this.execute(sql);
		Date d = (Date)sql.getResult();
		return d;
	}
	
	public String generateBillCode(){
		Date now = this.getSysDate();
		
		Sql sql = Sqls.create("select get_next_value('"+MainModule.BILL_CODE_KEY+"','D')");
		sql.setCallback(new SqlCallback() {
			public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				Long l = null;
				if (rs.next()){
					l = rs.getLong(1);
				}
					
				return l;
			}
		});
		this.execute(sql);
		Long bill_code = (Long)sql.getResult();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateStr = format.format(now);
		
		String theBillCode = dateStr+codeToFixLenthStr(bill_code,4);
		
		return theBillCode;
	}
	
	/**
	 * 产生群组编号,群组编号用"GROUP"作为前缀
	 * @return
	 */
	public String generateGroupCode(){
		return "GROUP_"+commonGenerateCode(GROUP_CODE_KEY);//用群组表的表名为key
	}
	
	/**
	 * 产生部门编号,部门编号用"DEP"作为前缀
	 * @return
	 */
	public String generateDepartmentCode(){
		return "DEP_"+commonGenerateCode(DEPARTMENT_CODE);//用部门表的表名为key
	}
	public String generateEmployeeBankAccount(){
		return commonGenerateCode(EMPLOYEE_BANK_ACCOUNT_CODE);
	}
	
	public String generatePosition(){
		return "POSITION_"+commonGenerateCode(POSITION_CODE);//岗位code
	}
	public String generateRoleCode(){
		return "ROLE_"+commonGenerateCode(ROLE_CODE_KEY);//用角色表的表名为key
	}
	
	public String generateOrganizationCode(){
		return commonGenerateCode(ORGANIZATION_CODE_KEY);//用群组表的表名为key
	}

	/**
	 * company是一个6位数字，不足补零
	 * @return
	 */
	public String generateCompanyCode(){
		Long theCode = generateCodeAsLong(COMPANY_CODE_KEY);
		return codeToFixLenthStr(theCode,6);
	}

	public String generateInvoiceTableCode(){
		return commonGenerateCode(FINANCIAL_INVOICE_CODE_KEY);//用公司表的表名为key
	}
	
	/**
	 * 产生合同编号
	 * 每年滚动从1开始
	 *  roll_flag=Y,序列发生器每年滚动
	 * @return
	 */
	public String generateContractCode(){
		Sql sql = Sqls.create("select get_next_value('"+CONTRACT_CODE_KEY+"','Y')");
		sql.setCallback(new SqlCallback() {
			public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				Long l = null;
				if (rs.next()){
					l = rs.getLong(1);
				}
					
				return l;
			}
		});
		this.execute(sql);
		Long code = (Long)sql.getResult();
		return Long.toString(code);
	}
	
	/**
	 * 产生执行单分解编号
	 * @return
	 */
	public String generateContractsplitCode(){
		return commonGenerateCode(CRM_CONTRACT_SPLIT);//
	}
	
	/**
	 * 产生执行单编号
	 * @return
	 */
	public String generateOrderCode(){
		return commonGenerateCode(CRM_ORDER);//
	}
	
	public String generateInvoiceRequestDetailSn(){
		return commonGenerateCode(CRM_INVOICE_REQUEST_DETAIL_KEY);
	}
	
	/**
	 * 产生非标合同文件表编号
	 * @return
	 */
	public String generateContractFileCode(){
		return commonGenerateCode(CRM_CONTRACT_FILE);//
	}
	/**
	 * 产生客户联系方式编号
	 * @return
	 */
	public String generateCompanyContactsCode(){
		return commonGenerateCode(COMPANY_CONTACTS_CODE);//
	}
	
	/**
	 * 产生执行单撤销申请编号
	 * @return
	 */
	public String generateOrderCancelApplyCode(){
		//return commonGenerateCode(ORDER_CANCEL_APPLY_CODE);//
		
		return commonGenerateCode(FINANCIAL_INVOICE_CODE_KEY);//注：12、撤销申请的业务单据号和发票申请采用同一个生成函数
	}
	
	/**
	 * 产生执行单原件编号
	 * @return
	 */
	public String generateOrderFileCode(){
		return commonGenerateCode(ORDER_File_CODE);//
	}
	/**
	 * 产生执行单原件编号
	 * @return
	 */
	public String generateProjectCode(){
		return commonGenerateCode(PROJECT_CODE);//
	}
	/**
	 * 通用的产生序列号的函数，
	 * @param keyName
	 * @return
	 */
	private Long generateCodeAsLong(String keyName){
		Sql sql = Sqls.create("select get_next_value('"+keyName+"','X')");
		sql.setCallback(new SqlCallback() {
			public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				Long l = null;
				if (rs.next()){
					l = rs.getLong(1);
				}
					
				return l;
			}
		});
		this.execute(sql);
		Long code = (Long)sql.getResult();
		return code;
	}
	
	
	private String commonGenerateCode(String keyName){
		Long theCode = generateCodeAsLong(keyName);
		return Long.toString(theCode);
	}
	
	
	private String codeToFixLenthStr(long bill_code,int theLength){
		String s = Long.toString(bill_code);
		if (s.length()>theLength){
			return s;
		}else{
			int numOfZero = theLength-s.length();
			StringBuffer prefix = new StringBuffer("");
			for (int i=0;i<numOfZero;i++){
				prefix.append("0");
			}
			return prefix.append(s).toString();
		}
	}
	
	/**
	 * 根据用户登录名，查询用户的所有权限，
	 * @param loginName
	 * @return
	 */
	public Map<String,List<OkErpPermission>> findUserPermissions(User user){
		Map<String,List<OkErpPermission>> permissions = new HashMap<String,List<OkErpPermission>>();

		
		//1、首先到T_security_permissions表查用户自身的OkErpPermission,将查到的OkErpPermission转换为FunctionCodePermission
		/*List<OkErpPermission> userPermissionList = dao().query(OkErpPermission.class, Cnd.where("granteeCode", "=", user.getLoginName())
											.and("granteeType", "=", OkErpPermission.GRANTEE_TYPE_USER));*/
		
		List<OkErpPermission> userPermissionList = this.query(OkErpPermission.class,Cnd.where("granteeCode", "=", user.getLoginName()).and("granteeType", "=", OkErpPermission.GRANTEE_TYPE_USER));
		
		permissions.put("用户自身", userPermissionList);
		

		//2、将用户所属的组的OkErpPermission也查询出来，转换成OkErpPermission
		Map<String,List<OkErpPermission>> tmp = findGroupPermissions(user.getGroupCode());
		if (tmp!=null){
			permissions.putAll(tmp);
		}
		
		//3、将用户所属的角色的OkErpPermission也查询出来，转换成OkErpPermission
		List<UserRole> userRoleList = this.query(UserRole.class, Cnd.where("userCode", "=", user.getLoginName()));
		if (userRoleList!=null && userRoleList.size()>0){
			for (UserRole ur : userRoleList){
				List<OkErpPermission> tmpList = this.query(OkErpPermission.class,Cnd.where("granteeCode", "=", ur.getRoleCode()).and("granteeType", "=", OkErpPermission.GRANTEE_TYPE_ROLE));

				if (tmpList!=null){
					permissions.put("角色["+ur.getRoleName()+"]", tmpList);
				}
			}
		}
		
		
		return permissions;
	}
	
	private Map<String,List<OkErpPermission>> findGroupPermissions(String groupCode){
		Group group = this.fetch(Group.class, groupCode);
		
		if (group==null)return null;
		
		List<OkErpPermission> groupPermissionList = this.query(OkErpPermission.class, Cnd.where("granteeCode", "=", group.getCode())
				.and("granteeType", "=", OkErpPermission.GRANTEE_TYPE_GROUP));
		
		Map<String,List<OkErpPermission>> permissions = new HashMap<String,List<OkErpPermission>>();
		
		permissions.put("群组["+group.getName()+"]", groupPermissionList);
		
		Map<String,List<OkErpPermission>> tmp = findGroupPermissions(group.getParentCode());
		
		if (tmp!=null){
			permissions.putAll(tmp);
		}

		return permissions;
	}
}
