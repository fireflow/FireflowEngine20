package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.entity.runtime.impl.VariableHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.nutz.dao.Cnd;

public class VariablePersisterNutzImpl extends AbsPersisterNutzImpl implements
		VariablePersister {

	public Variable findVariable(String scopeId, String name) {
		return dao().fetch(VariableImpl.class, Cnd.where(VariableProperty.SCOPE_ID.getPropertyName(), "=", scopeId)
									.and(VariableProperty.NAME.getPropertyName(), "=", name));

	}

	public List<Variable> findVariables(String scopeId) {
		List result = dao().query(VariableImpl.class,
				 Cnd.where(VariableProperty.SCOPE_ID.getPropertyName(), "=", scopeId));
		
//		List<Variable> result2 = new ArrayList<Variable>();
//		if (result!=null){
//			for (Variable v :result ){
//				result2.add(v);
//			}
//		}
		return result;
	}

	public void deleteAllVariables() {
		dao().clear(VariableImpl.class);

	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return VariableImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return VariableHistory.class;
	}
	
//	public static void main(String[] args){
//		List<VariableImpl> l = new ArrayList<VariableImpl>();
//		l.add(new VariableImpl());
//		l.add(new VariableImpl());
//		
//		List l3 = l;
//		List<Variable> l2 = (List<Variable>)l3;
//		System.out.println(l2);
//	}

}
