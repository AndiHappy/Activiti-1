package com.shareniu.chapter16.multiInstance.weight;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShareniuParallelMultiInstanceBehavior extends
		ParallelMultiInstanceBehavior {

	public ShareniuParallelMultiInstanceBehavior(ActivityImpl activity,
			AbstractBpmnActivityBehavior originalActivityBehavior) {
		super(activity, originalActivityBehavior);
	}

	@Override
	public void leave(ActivityExecution execution) {
		Map<String, List<ExtensionElement>> extensionElements = (Map<String, List<ExtensionElement>>) activity
				.getProperty("shareniuExt");
		System.err.println(extensionElements);
		Iterator<Entry<String, List<ExtensionElement>>> it = extensionElements
				.entrySet().iterator();
		String json = "";
		while (it.hasNext()) {
			Entry<String, List<ExtensionElement>> entry = it.next();
			// 获取根标签的名称
			System.err.println("rookey= " + entry.getKey());
			List<ExtensionElement> value = entry.getValue();
			for (ExtensionElement e : value) {
				Map<String, List<ExtensionElement>> childElements = e
						.getChildElements();
				Iterator<Entry<String, List<ExtensionElement>>> it1 = childElements
						.entrySet().iterator();
				while (it1.hasNext()) {
					Entry<String, List<ExtensionElement>> entry1 = it1.next();
					System.err.println("childKey= " + entry1.getKey());
					List<ExtensionElement> value1 = entry1.getValue();
					for (ExtensionElement e1 : value1) {
						String elementText = e1.getElementText();// 获取文本
						json = elementText;
						System.err.println(elementText);
						Map<String, List<ExtensionAttribute>> attributes = e1
								.getAttributes();
						Collection<List<ExtensionAttribute>> values = attributes
								.values();
						for (List<ExtensionAttribute> list : values) {
							for (ExtensionAttribute extensionAttribute : list) {
								System.err.println(extensionAttribute.getName()
										+ "," + extensionAttribute.getValue());
							}
						}
					}
				}
			}
		}
		String id = execution.getId();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			TaskService taskService = execution.getEngineServices()
					.getTaskService();
			Task task = taskService.createTaskQuery().executionId(id)
					.singleResult();
			Map<String, Object> map = objectMapper.readValue(json, Map.class);// 将json转化为Map
			Map<String, Integer> info = (Map<String, Integer>) map.get("info");// 获取权重信息
			String assignee = task.getAssignee();//获取任务处理人
			int weight = info.get(assignee);// 当前当前处理人的权重值
			Object variable = execution.getVariable("pass");// 获取变量
			int x = 0;
			if (variable instanceof Boolean) {
				boolean flag = (boolean) variable;
				if (flag) {//同意
					if (execution.getParent() != null) {
						Integer loopVariable = getLoopVariable(execution,"weight");//从数据库中获取weight变量
						x = weight + loopVariable;
						// shareniuX
						setLoopVariable(execution.getParent(), "weight", x);//从设置weight变量
					}
				} else {//不同意
					if (execution.getParent() != null) {
						Integer loopVariable = getLoopVariable(execution,"weight");
						x = loopVariable - weight;
						setLoopVariable(execution.getParent(), "weight", x);
					}
				}
			}
			if (x >= (int) map.get("conditions")) {//获取通过的权重值
				execution.setVariable("shareniuX", true);//表示多实例可以结束
			} else {
				execution.setVariable("shareniuX", false);
			}
			super.leave(execution);
		} catch (Exception e) {
		}

	}
}
