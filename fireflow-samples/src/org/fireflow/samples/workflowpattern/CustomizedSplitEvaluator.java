package org.fireflow.samples.workflowpattern;

import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.pdl.fpdl20.behavior.router.AbsSplitEvaluator;
import org.fireflow.pdl.fpdl20.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl20.process.Transition;

public class CustomizedSplitEvaluator extends AbsSplitEvaluator implements SplitEvaluator {

	protected boolean canBeFired(Transition transition,WorkflowSession session,
			RuntimeContext rtCtx,Map<String, Object> scriptEngineVariableContext){
		if (transition.getName().equals("Transition2")){
			return true;
		}else{
			return super.canBeFired(transition, session, rtCtx, scriptEngineVariableContext);
		}
	}

	
	/**
	 * 此处描述分支逻辑，便于流程图阅读理解
	 */
	public String getSplitDescription() {
		String desc = "本分支逻辑忽略transition2上的条件判断，正常执行Transition3上的条件判断";
		return desc;
	}

}
