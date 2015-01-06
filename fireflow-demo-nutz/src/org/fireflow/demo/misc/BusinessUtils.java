package org.fireflow.demo.misc;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
*
* 业务相同算法处理类
*
*/
public class BusinessUtils {
	private static final String[] months={"01","02","03","04","05","06","07","08","09","10","11","12"};
	
	/**
	 * 返回翻页业务需要的参数（如果有其他业务扩展，可以参考另写一个新的方法）。
	 * @param currentYear 当前年份
	 * @param lastYear 上一年
	 * @param nextYear 下一年
	 * @param lastYearMonth 上一个月（页面点击参数，如果有值进来，说明页面端用户点击的是“上一月”）
	 * @param nextYearMonth 下一个月（页面点击参数，如果有值进来，说明页面端用户点击的是“下一月”）
	 * @return Map<String,Object>
	 */
	public static Map<String,Object> pageYearMonth(String currentYear,int lastYear,int nextYear,String lastYearMonth,String nextYearMonth){
		Map<String,Object> result = new HashMap<String,Object>();
		
		//这两个值是计算放在前台展示“上月”“下月”连接用
		String newLastYearMonth = "";
		String newNextYearMonth ="";
		
		//获取执行单的月份列表where yearMonth in()
		String sqlYearMonth ="";
		
		//页面上显示的2014年08月
		List<String> showYearMonth=new ArrayList<String>(); 
		
		//初始化当年的1到12个月区间
		if(StringUtils.isBlank(lastYearMonth)&&StringUtils.isBlank(nextYearMonth)){
			//初始化上个月点击按钮是上年12月
			newLastYearMonth = String.valueOf(lastYear)+"12";
			//同理下个月是下一年的1月
			newNextYearMonth = String.valueOf(nextYear)+"01";
			
			//初始化查询条件
			StringBuilder sb = new StringBuilder();
			for(int j=0; j < BusinessUtils.months.length;j++){
				if(j!=11){
					sb.append("'"+currentYear+BusinessUtils.months[j]+"',");
				}else{
					sb.append("'"+currentYear+BusinessUtils.months[j]+"'");
				}
//				showYearMonth.add(currentYear+"年"+OrderModule.months[j]+"月");
				showYearMonth.add(currentYear+"."+BusinessUtils.months[j]+"");//为了节省界面空间，将”年“，”月“二字省略，陈乜云，2014-08-18
			}
			
			sqlYearMonth=sb.toString();
		}else{
			SimpleDateFormat yyyymm = new SimpleDateFormat("yyyyMM");
			//点击上个月减一
			if(StringUtils.isNotBlank(lastYearMonth)){
				Calendar calendar = Calendar.getInstance();
				Date lastMM = null;
				try {
					lastMM = yyyymm.parse(lastYearMonth);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calendar.setTime(lastMM);
				calendar.add(Calendar.MONTH, -1);
				//重置“上一个”按钮月为当前月减一
				newLastYearMonth = yyyymm.format(calendar.getTime());
				
				//重置“下一个”按钮月为当前月加12个月
				Date nextMM = null;
				try {
					nextMM = yyyymm.parse(lastYearMonth);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calendar.setTime(nextMM);
				calendar.add(Calendar.MONTH, 12);
				newNextYearMonth = yyyymm.format(calendar.getTime());
				
				
				String subM = lastYearMonth.substring(4, lastYearMonth.length());
				String subY = lastYearMonth.substring(0, 4);
				//初始化查询条件
				StringBuilder sb = new StringBuilder();
				//以当前月往后取
				int count=0;
				for(int j=0; j < BusinessUtils.months.length; j++){
					if( Integer.parseInt(BusinessUtils.months[j]) < Integer.parseInt(subM) ){
						continue;
					}
					count++;
//					showYearMonth.add(subY+"年"+OrderModule.months[j]+"月");//为了节省界面空间，将”年“，”月“二字省略，陈乜云，2014-08-18
					showYearMonth.add(subY+"."+BusinessUtils.months[j]+"");
					if(count==12){
						sb.append("'"+subY+BusinessUtils.months[j]+"'");
					}else{
						sb.append("'"+subY+BusinessUtils.months[j]+"',");
					}
				}
				
				if(count < 12){
					int subCount = 12 - count;
					int nyear = Integer.parseInt(subY) + 1;
					
					//从前往后取，补全12个月
					for(int j=0; j < subCount; j++){
						if(j == (subCount-1) ){
							sb.append("'"+nyear+BusinessUtils.months[j]+"'");
						}else{
							sb.append("'"+nyear+BusinessUtils.months[j]+"',");
						}
//						showYearMonth.add(nyear+"年"+OrderModule.months[j]+"月");//为了节省界面空间，将”年“，”月“二字省略，陈乜云，2014-08-18
						showYearMonth.add(nyear+"."+BusinessUtils.months[j]+"");
					}
				}
				sqlYearMonth=sb.toString();
			}else if(StringUtils.isNotBlank(nextYearMonth)){
				//点击下个月加一
				Calendar calendar = Calendar.getInstance();
				Date nextMM = null;
				try {
					nextMM = yyyymm.parse(nextYearMonth);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calendar.setTime(nextMM);
				calendar.add(Calendar.MONTH, 1);
				//重置下一个月为当前月加一
				newNextYearMonth = yyyymm.format(calendar.getTime());
				
				//重置上一个月为当前月减去12个月
				Date lastMM = null;
				try {
					lastMM = yyyymm.parse(nextYearMonth);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calendar.setTime(lastMM);
				calendar.add(Calendar.MONTH, -12);
				newLastYearMonth = yyyymm.format(calendar.getTime());
				
				String subM = nextYearMonth.substring(4, nextYearMonth.length());
				String subY = nextYearMonth.substring(0, 4);
				//初始化查询条件
				StringBuilder sb = new StringBuilder();
				//从前往后取
				int count=0;
				
				//如果点击下一月刚好是12月，那么不取去年的数据
				if(!"12".equals(subM)){
					int nyear = Integer.parseInt(subY) - 1;
					//从后往前取，补全12个月
					for(int j=0; j < BusinessUtils.months.length; j++){
						if(Integer.parseInt(BusinessUtils.months[j]) <= Integer.parseInt(subM)){
							continue;
						}
						count++;
						sb.append("'"+nyear+BusinessUtils.months[j]+"',");
//						showYearMonth.add(nyear+"年"+OrderModule.months[j]+"月");//为了节省界面空间，将”年“，”月“二字省略，陈乜云，2014-08-18
						showYearMonth.add(nyear+"."+BusinessUtils.months[j]+"");
					}
				}
				
				//补余下的月份
				for(int j=0; j < (BusinessUtils.months.length-count); j++){
//					showYearMonth.add(subY+"年"+OrderModule.months[j]+"月");//为了节省界面空间，将”年“，”月“二字省略，陈乜云，2014-08-18
					showYearMonth.add(subY+"."+BusinessUtils.months[j]+"");
					if(subM.equals(BusinessUtils.months[j])){
						sb.append("'"+subY+BusinessUtils.months[j]+"'");
						break;
					}else{
						sb.append("'"+subY+BusinessUtils.months[j]+"',");
					}
				}
				
				sqlYearMonth=sb.toString();
			}
		}
		
		result.put("lastYearMonth",newLastYearMonth);
		result.put("nextYearMonth",newNextYearMonth);
		result.put("showYearMonth",showYearMonth);
		result.put("sqlYearMonth",sqlYearMonth);
		
		return result;
	}
	
	
	/**
	 * 根据传进来的字符串分解组装为sql里面的in字符串
	 * @param string ("abc,12d,ddd")
	 * @return String ("'abc','12d','ddd'")
	 */
	public static String getSplitString(String string){
		String reString ="";
		String[] splitArray=string.split(",");
		if(splitArray!=null && splitArray.length>0){
			//初始化查询条件
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i<splitArray.length ; i++){
				if(i != (splitArray.length-1)){
					sb.append("'"+splitArray[i]+"',");
				}else{
					sb.append("'"+splitArray[i]+"'");
				}
			}
			reString=sb.toString();
		}
		
		return reString;
	} 
	
	/**
	 * 根据传进来的数字类型，获取对应的人民币大写
	 * : 抽象类 Number 是 BigDecimal、BigInteger、Byte、Double、Float、Integer、Long 和 Short 类的超类
	 * @param Number number
	 * @return String
	 */
	 public static String toChineseCurrency(Number number) {  
	        String s = new DecimalFormat("#.00").format(number);  
	        //System.out.println(s);  
	        s = s.replaceAll("\\.", "");  
	        char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };  
	        String unit = "仟佰拾兆仟佰拾亿仟佰拾万仟佰拾元角分";  
	        int l = unit.length();  
	        StringBuffer sb = new StringBuffer(unit);  
	        for (int i = s.length() - 1; i >= 0; i--)  
	            sb = sb.insert(l - s.length() + i, digit[(s.charAt(i) - 0x30)]);  
	        s = sb.substring(l - s.length(), l + s.length());  
	        s = s.replaceAll("零[拾佰仟]", "零").replaceAll("零{2,}", "零").replaceAll("零([兆万元])", "$1").replaceAll("零[角分]", "");  
	        if (s.endsWith("角"))  
	            s += "零分";  
	        if (!s.contains("角") && !s.contains("分") && s.contains("元"))  
	            s += "整";  
	        if (s.contains("分") && !s.contains("整") && !s.contains("角"))  
	            s = s.replace("元", "元零");  
	        return s;  
	}  
	 
	/**
	 * 根据传进来的银行账号，格式化后输出
	 * 原则：保留前4位和后4位，中间的其他数字格式化为注释号(*)
	 * 原来：6225121212127891
	 * 返回：6225********7891
	 * @param String account
	 * @return String
	 */
	 public static String forMatBankAccount(String account) {  
		  if(account != null && !"".equals(account.trim()) && account.length() > 8){
				String prefix = account.substring(0, 4);
				String suffix = account.substring(account.length()-4, account.length());
				StringBuilder sb = new StringBuilder();
				sb.append(prefix);
				for(int i=0;i<account.length()-8;i++){
					sb.append("*");
				}
				sb.append(suffix);
				
				String lastFormat =sb.toString();
				return lastFormat;  
		  }
	      return account;  
	} 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Scanner s = new Scanner(System.in);  
        System.out.println("请输入人民币金额:");  
        double number = 1;  
        while (number > 0.000000001) {  
            number = s.nextDouble();  
            System.out.println(toChineseCurrency(new Double(number)));  
        }  
        System.exit(0);*/
		
		/*String tempD = String.valueOf(0.106 * 100);
		int dian=tempD.indexOf(".");
		//获取小数点后一位，判断如果是0，那么只取，整数
		String subString = tempD.substring(dian+1, dian+2);
		if("0".equals(subString)){
			tempD=tempD.substring(0, dian);
		}
		System.out.println(tempD);*/
		
		/* System.out.println("四舍五入取整:(2.4)=" + new BigDecimal(0.244).setScale(2, BigDecimal.ROUND_HALF_UP)); 
		   System.out.println("四舍五入取整:(2.5)=" + new BigDecimal(0.255).setScale(2, BigDecimal.ROUND_HALF_UP)); 
		   System.out.println("四舍五入取整:(2.9)=" + new BigDecimal(0.299).setScale(2, BigDecimal.ROUND_HALF_UP));*/
		
	}

}
