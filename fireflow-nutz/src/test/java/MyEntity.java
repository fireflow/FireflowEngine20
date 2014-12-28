import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.runtime.WorkItemState;


public class MyEntity extends AbsWorkflowEntity {

	private WorkItemState state = null;

	public WorkItemState getState() {
		return state;
	}

	public void setState(WorkItemState state) {
		this.state = state;
	}
	
	
}
