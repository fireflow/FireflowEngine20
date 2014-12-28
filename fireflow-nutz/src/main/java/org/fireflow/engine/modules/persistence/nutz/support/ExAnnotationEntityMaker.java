package org.fireflow.engine.modules.persistence.nutz.support;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.fireflow.model.io.Util4Deserializer;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.dao.entity.annotation.TableMeta;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.AnnotationEntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.ManyLinkField;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.impl.entity.field.OneLinkField;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.dao.impl.entity.info.MappingInfo;
import org.nutz.dao.impl.entity.info.TableInfo;
import org.nutz.dao.impl.entity.info._Infos;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
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
	        boolean shouldUseColumn = false;
	        boolean hasColumnComment = false;
	        for (Field field : en.getMirror().getFields()) {
	            if (shouldUseColumn && hasColumnComment) {
	                break;
	            }
	            if (!shouldUseColumn && null != field.getAnnotation(Column.class)) {
	                shouldUseColumn = true;
	            }
	            if (!hasColumnComment && null != field.getAnnotation(Comment.class)) {
	                hasColumnComment = true;
	            }
	        }

	        en.setHasColumnComment(hasColumnComment);

	        return null;
	}

	/**
	 * 加载所有的映射文件
	 */
	protected void init()throws Exception{		
		InputStream instream = ExAnnotationEntityMaker.class.getResourceAsStream("/org/fireflow/engine/modules/persistence/nutz/ProcessInstanceImpl.nutzmp.xml");
		
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(instream);
		
		parseClassElment(document);
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
