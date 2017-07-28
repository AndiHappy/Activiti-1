package com.shareniu.chapter16.multiInstance.weight;

import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class ShareniuActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
	@Override
	public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(
			ActivityImpl activity,
			AbstractBpmnActivityBehavior innerActivityBehavior) {
		return new ShareniuParallelMultiInstanceBehavior(activity,
				innerActivityBehavior);
	}
}
