package org.fireflow.engine.modules.persistence.nutz.support;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fireflow.model.io.Util4Deserializer;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.eject.EjectByGetter;
import org.nutz.lang.inject.InjectBySetter;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExAnnotationEntityMaker extends AnnotationEntityMaker {
    private static final Log log = Logs.get();
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	
	private static final String ELM_CLASS = "class";
	private static final String ELM_PROPERTY = "property";
	
	public static final String FIREFLOW_ENTITY_PKG_PREFIX = "org.fireflow.engine.entity";
	
	private static List<String> allMappingFiles = new ArrayList<String>();
	static {
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ActivityInstanceHistory.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ActivityInstanceImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/FireflowConfigImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/LocalWorkItemImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ProcessDescriptor.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ProcessInstanceHistory.nutzmp.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ProcessInstanceImpl.nutzmp.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ProcessRepositoryImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ReassignConfigImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/RemoteWorkItemImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ScheduleJobHistory.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/ScheduleJobImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/TokenHistory.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/TokenImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/VariableHistory.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/VariableImpl.nutzmap.xml");
		allMappingFiles.add("/org/fireflow/engine/modules/persistence/nutz/WorkItemHistory.nutzmap.xml");
	}
	
	private Map<Class,Element> classDefinitionsMap = new HashMap<Class,Element>();

	
    private DataSource datasource;

    private JdbcExpert expert;
    
    private EntityHolder holder;


	public ExAnnotationEntityMaker(DataSource datasource, JdbcExpert expert,
			EntityHolder holder) {
		super(datasource, expert, holder);
		
        this.datasource = datasource;
        this.expert = expert;
        this.holder = holder;
        
		try{
			init();
		}catch(Exception e){
			log.error("初始化ExAnnotationEntityMaker错误。", e);
		}
		
	}
	
	public <T> Entity<T> make(Class<T> type) {
		if (type!=null && type.getName().startsWith(FIREFLOW_ENTITY_PKG_PREFIX)){
			Element classElm = classDefinitionsMap.get(type);
			if (classElm!=null){
				Entity<T> result = makeEntity(type,classElm);
				return result;
			}
		}
		return super.make(type);
	}
	
//    private TableInfo _createTableInfo(Class<?> type) {
//        TableInfo info = new TableInfo();
//        Mirror<?> mirror = Mirror.me(type);
//        info.annTable = mirror.getAnnotation(Table.class);
//        info.annView = mirror.getAnnotation(View.class);
//        info.annMeta = mirror.getAnnotation(TableMeta.class);
//        info.annPK = mirror.getAnnotation(PK.class);
//        info.annIndexes = mirror.getAnnotation(TableIndexes.class);
//        info.tableComment = mirror.getAnnotation(Comment.class);
//        return info;
//    }
	
	protected <T> Entity<T> makeEntity(Class<T> type,Element classElm){
	       NutEntity<T> en = _createNutEntity(type);
	        /*
	         * 获取实体的扩展描述
	         */
	        // 全局
	        if (null != expert.getConf()) {
	            for (String key : expert.getConf().keySet())
	                en.getMetas().put(key, expert.getConf().get(key));
	        }


	        /*
	         * 获得表名以及视图名称及注释
	         */
	        String tableName = classElm.getAttribute("table");
	        String viewName = classElm.getAttribute("view");
	        if (viewName==null || viewName.trim().equals("")){
	        	viewName = tableName;
	        }
	        en.setTableName(tableName);
	        en.setViewName(viewName);

	        Element tableCommentElm = Util4Deserializer.child(classElm, "comment");
	        if (tableCommentElm!=null){
		        en.setHasTableComment(true);
		        en.setTableComment(tableCommentElm.getTextContent());
	        }


	        /*
	         * 获取所有的数据库字段
	         */
	        // 字段里面是不是有声明过 '@Column' @Comment
//	        boolean shouldUseColumn = false;
//	        boolean hasColumnComment = false;
//	        for (Field field : en.getMirror().getFields()) {
//	            if (shouldUseColumn && hasColumnComment) {
//	                break;
//	            }
//	            if (!shouldUseColumn && null != field.getAnnotation(Column.class)) {
//	                shouldUseColumn = true;
//	            }
//	            if (!hasColumnComment && null != field.getAnnotation(Comment.class)) {
//	                hasColumnComment = true;
//	            }
//	        }
//
//	        en.setHasColumnComment(hasColumnComment);
	        
	        /*
	         * 循环获取实体字段
	         */
	        //首先获取id，此处的id即nutz的name
	        Element idElm = Util4Deserializer.child(classElm, "id");
	        if (idElm!=null){
	        	
	        	MappingField mf = createMappingField(en,type,idElm,false,true);
	        	en.addMappingField(mf);
	        	
	        }
	        
	        List<Element> propertyElms =  Util4Deserializer.children(classElm, ELM_PROPERTY);
	        for (Element elm : propertyElms){
	        	MappingField mf = createMappingField(en,type,elm,false,false);
	        	en.addMappingField(mf);
	        }
	        holder.set(en); // 保存一下，这样别的实体映射到这里时会用的到
	        return en;
	}
	
 
	
	private MappingField createMappingField(Entity en,Class type,Element fieldElm, boolean isId,boolean isName){
		NutMappingField ef = new NutMappingField(en);
		
    	String fieldName = fieldElm.getAttribute("name");
    	String s = null;
    	if (fieldName.length()>1){
    		s = fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
    	}else{
    		s = fieldName.toUpperCase();
    	}
    	String setterName = "set"+s;
    	String getterName = "get"+s;
    	Method setter = null;
    	Method getter = null;
    	
//    	Field field;
		try {
			Method[] allMethods = type.getMethods();
			for (Method m : allMethods){
				if(m.getName().equals(setterName)){
					setter = m;
				}else if (m.getName().equals(getterName)){
					getter = m;
				}
			}
		}  catch (SecurityException e1) {
			throw new RuntimeException(e1);
		}
    	Element columnElem = Util4Deserializer.child(fieldElm,"column");
    	
        // 字段的 Java 名称
        ef.setName(fieldName);
        if (getter!=null){
        	ef.setType(getter.getGenericReturnType());
        }
        

        // 字段的数据库名
        if (null == columnElem || Strings.isBlank(columnElem.getAttribute("name")))
            ef.setColumnName(fieldName);
        else
            ef.setColumnName(columnElem.getAttribute("name"));

        // 字段的注释
        if (columnElem!=null ){
        	Element commentElm =  Util4Deserializer.child(columnElem,"comment");
            boolean hasColumnComment = null != commentElm;
            ef.setHasColumnComment(hasColumnComment);
            if (hasColumnComment) {
            	
                String comment = commentElm.getTextContent();
                if (Strings.isBlank(comment)) {
                    ef.setColumnComment(fieldName);
                } else {
                    ef.setColumnComment(comment);
                }
            }
        }


        // Id 字段
        if (isId) {
            ef.setAsId();
        }

        // Name 字段
        if (isName) {
            ef.setAsName();
        }

        // 检查 @Id 和 @Name 的冲突
        if (ef.isId() && ef.isName())
            throw Lang.makeThrow("Field '%s'(%s) can not be @Id and @Name at same time!",
                                 ef.getName(),
                                 ef.getEntity().getType().getName());

//        // 检查 PK
//        if (null != info.annPK) {
//            // 用 @PK 的方式声明的主键
//            if (info.annPK.value().length == 1) {
//                if (Lang.contains(info.annPK.value(), info.name)) {
//                    if (ef.getTypeMirror().isIntLike())
//                        ef.setAsId();
//                    else
//                        ef.setAsName();
//                }
//            }
//            // 看看是不是复合主键
//            else if (Lang.contains(info.annPK.value(), info.name))
//                ef.setAsCompositePk();
//        }




        // 只读
//        if (null != info.annReadonly)
//            ef.setAsReadonly();

        // 字段更多定义
        if (null != columnElem) {
            // 类型
//            ef.setColumnType(info.annDefine.type());
        	Jdbcs.guessEntityFieldColumnType(ef);
        	
            // 宽度
        	String length = columnElem.getAttribute("length");
        	if (length==null || length.trim().equals("")){
                if (ef.getWidth() == 0 && ef.getColumnType() == ColType.VARCHAR) {
                	ef.setWidth(50);
                }
        	}else{
        		ef.setWidth(Integer.parseInt(length));
        	}
            

//            // 精度
//            ef.setPrecision(info.annDefine.precision());
            
            // 无符号
//            if (info.annDefine.unsigned())
//                ef.setAsUnsigned();
            // 非空约束
        	String notNull = columnElem.getAttribute("not-null");
        	if (notNull!=null && !notNull.trim().equals("")){
                if (notNull.equalsIgnoreCase("true") || notNull.equalsIgnoreCase("t") || notNull.equalsIgnoreCase("1")){
                	ef.setAsNotNull();
                }
        	}


            // 自增，如果 @Id(auto=false)，则忽略
//            if (info.annDefine.auto() && !ef.isId())
//                ef.setAsAutoIncreasement();

            // 是否为自定义类型呢?
//            if (info.annDefine.customType().length() > 0) {
//                ef.setCustomDbType(info.annDefine.customType());
//            }

            // 插入更新操作
//            ef.setInsert(info.annDefine.insert());
        	String update = fieldElm.getAttribute("update");
        	if (update!=null){
        		ef.setUpdate(Boolean.parseBoolean(update));
        	}
        	
            // 默认值
            if (columnElem!=null ){
            	String defaultValue = columnElem.getAttribute("default");
                if (null != null  ){
                	if (defaultValue.equals("CURRENT_TIMESTAMP")){
                		ef.setUpdate(false);//mysql 数据库自动设值
                		ef.setInsert(false);//mysql 数据库自动设值
                	}else{
                		ef.setDefaultValue(new CharSegment(defaultValue));
                	}
                }
                    
            }
            
        }
        // 猜测字段类型
        else {
            Jdbcs.guessEntityFieldColumnType(ef);
        }

        // 字段值的适配器
        String adaptorClzName = fieldElm.getAttribute("adaptor");
        if (null==adaptorClzName || adaptorClzName.trim().equals("")){
        	ef.setAdaptor(expert.getAdaptor(ef));
        }else{
        	try {
        		Class clz = Class.forName(adaptorClzName);
				ValueAdaptor adaptor = (ValueAdaptor)clz.newInstance();
				ef.setAdaptor(adaptor);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}catch (ClassNotFoundException e){
				throw new RuntimeException(e);
			}
        }
        

        // 输入输出
        ef.setInjecting(new InjectBySetter(setter));
        ef.setEjecting(new EjectByGetter(getter));
        
        return ef;
	}

	public static void main(String[] args){
//		File f = new File("C:/Users/SX-7F-01-006/git/FireflowEngine20/fireflow-nutz/src/main/resources/org/fireflow/engine/modules/persisence/nutz");
//		File[] allxmls = f.listFiles();
//		for (File x :allxmls){
//			System.out.println("allMappingFiles.add(\"/org/fireflow/engine/modules/persistence/nutz/"+x.getName()+"\");");
//		}
		System.out.println("Begin~~~~~~~~~~~~~~~~~~~~~~~~");
		ExAnnotationEntityMaker maker = new ExAnnotationEntityMaker(null,null,null);
		System.out.println("end!!!");
		
	}
	
	/**
	 * 加载所有的映射文件
	 */
	protected void init()throws Exception{		
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		
		for (String fileName : allMappingFiles){
			try{
				InputStream instream = ExAnnotationEntityMaker.class.getResourceAsStream(fileName);


				Document document = docBuilder.parse(instream);
				
				parseClassElment(document);
				
				instream.close();
			}catch(Throwable t){
				t.printStackTrace();
				throw new RuntimeException(t);
			}

		}

	}
	protected void parseClassElment(Document document) throws ClassNotFoundException{
		Element nutzMappingElm = document.getDocumentElement();
		
		List<Element> classElmList = Util4Deserializer.children(nutzMappingElm, ELM_CLASS);
		
		for (Element clzElm : classElmList){
			String className = clzElm.getAttribute("name");
			
			Class clz = Class.forName(className);
			
			
			classDefinitionsMap.put(clz, clzElm);
		}
	}
}
