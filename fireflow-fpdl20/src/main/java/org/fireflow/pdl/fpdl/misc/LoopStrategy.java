package org.fireflow.pdl.fpdl.misc;


public enum LoopStrategy {
    /**
     * 循环情况下，任务分配指示之一：重做<br>
     * 对于Tool类型和Subflow类型的task会重新执行一遍
     * 对于Form类型的Task，重新执行一遍，且将该任务实例分配给最近一次完成同一任务的操作员。
     */
    REDO("org.fireflow.constants.loop_strategy.REDO"),

    /**
     * 循环情况下，任务分配指示之二：忽略<br>
     * 循环的情况下该任务将被忽略，即在流程实例的生命周期里，仅执行一遍。
     */
    SKIP("org.fireflow.constants.loop_strategy.SKIP"),

    /**
     * 循环的情况下，任务分配指示之三：无<br>
     * 对于Tool类型和Subflow类型的task会重新执行一遍，和REDO效果一样的。<br>
     * 对于Form类型的Task，重新执行一遍，且工作流引擎仍然调用Performer属性的AssignmentHandler分配任务
     */
    NONE("org.fireflow.constants.loop_strategy.NONE");
    
    private String value;
    private LoopStrategy(String value){
    	this.value = value;
    }
    
    public String getValue(){
    	return this.value;
    }
    
	public static LoopStrategy fromValue(String v){
		LoopStrategy[] values =  LoopStrategy.values();
		for (LoopStrategy strategy : values){
			if (strategy.getValue().equals(v)){
				return strategy;
			}
		}
		return null;
	}	
}
