package org.fireflow.engine.modules.persistence.nutz;

import org.fireflow.client.query.Criterion;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;

public class NutzPersisterUtils {
	public static SqlExpression fireCriterion2NutzSqlExpression(org.fireflow.client.query.Criterion fireCriterion){
		String operation = fireCriterion.getOperation().trim();
		
		if (Criterion.OPERATION_EQ.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_NE.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_LIKE.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), "LIKE", fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_GT.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_LT.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}	
		else if (Criterion.OPERATION_GE.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}		
		else if (Criterion.OPERATION_LE.equals(operation)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), operation, fireCriterion.getValues()[0]);
		}
		else if (operation.equals(Criterion.OPERATION_IS_NULL)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), "IS", null);
		}	
		else if (operation.equals(Criterion.OPERATION_IS_NOT_NULL)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), "IS NOT", null);
		}	
		else if (operation.equals(Criterion.OPERATION_IN)){
			return Cnd.exp(fireCriterion.getEntityProperty().getPropertyName(), "IN", fireCriterion.getValues());
		}	
//		else if (Criterion.OPERATION_BETWEEN.equals(operation)){
//			return Restrictions.between(fireCriterion.getEntityProperty().getPropertyName(),fireCriterion.getValues()[0],fireCriterion.getValues()[1]);
//		}		
		else if (Criterion.OPERATION_AND.equals(operation)){
			org.fireflow.client.query.Criterion left = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[0];
			org.fireflow.client.query.Criterion right = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[1];
			SqlExpression hLeft = fireCriterion2NutzSqlExpression(left);
			SqlExpression hRight = fireCriterion2NutzSqlExpression(right);
			if (hLeft instanceof SqlExpressionGroup){
				return ((SqlExpressionGroup)hLeft).and(hRight);
			}else{
				return Cnd.exps(hLeft).and(hRight);
			}
			
			
		}		
		else if (Criterion.OPERATION_OR.equals(operation)){
			org.fireflow.client.query.Criterion left = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[0];
			org.fireflow.client.query.Criterion right = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[1];
			SqlExpression hLeft = fireCriterion2NutzSqlExpression(left);
			SqlExpression hRight = fireCriterion2NutzSqlExpression(right);
			if (hLeft instanceof SqlExpressionGroup){
				return ((SqlExpressionGroup)hLeft).or(hRight);
			}else{
				return Cnd.exps(hLeft).or(hRight);
			}
			
		}	
		return null;
	}
	

}
