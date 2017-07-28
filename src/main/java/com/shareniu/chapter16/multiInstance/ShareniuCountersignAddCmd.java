package com.shareniu.chapter16.multiInstance;

import java.util.Date;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

public class ShareniuCountersignAddCmd  implements Command<Void>{
	
	protected String executionId;//执行实例
	protected String assignee;//执行实例
	@Override
	public Void execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl pec = commandContext.getProcessEngineConfiguration();
		RuntimeService runtimeService = pec.getRuntimeService();
		TaskService taskService = pec.getTaskService();
		IdGenerator idGenerator = pec.getIdGenerator();//获取id生成器
		//获取Execution实例对象
		Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
			ExecutionEntity ee = (ExecutionEntity) execution;
			ExecutionEntity parent = ee.getParent();//获取父级ExecutionEntity实例对象
			ExecutionEntity newExecution = parent.createExecution();//创建ExecutionEntity实例对象
			newExecution.setActive(true);//设置为激活状态
			//该属性表示创建的newExecution对象为分支，非常重要,不可缺少
			newExecution.setConcurrent(true);
			newExecution.setScope(false);
			Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
			TaskEntity t = (TaskEntity) newTask;
			TaskEntity taskEntity = new TaskEntity();
			taskEntity.setCreateTime(new Date());
			taskEntity.setTaskDefinition(t.getTaskDefinition());
			taskEntity.setProcessDefinitionId(t.getProcessDefinitionId());
			taskEntity.setTaskDefinitionKey(t.getTaskDefinitionKey());
			taskEntity.setProcessInstanceId(t.getProcessInstanceId());
			taskEntity.setExecutionId(newExecution.getId());
			taskEntity.setName(newTask.getName());
			String taskId = idGenerator.getNextId();
			taskEntity.setId(taskId);
			taskEntity.setExecution(newExecution);
			taskEntity.setAssignee(assignee);
			taskService.saveTask(taskEntity);
			int loopCounter = ShareniuLoopVariableUtils.getLoopVariable(newExecution, "nrOfInstances");
			int nrOfCompletedInstances = ShareniuLoopVariableUtils.getLoopVariable(newExecution,
					"nrOfActiveInstances");
			ShareniuLoopVariableUtils.setLoopVariable(newExecution, "nrOfInstances", loopCounter + 1);
			ShareniuLoopVariableUtils.setLoopVariable(newExecution, "nrOfActiveInstances",nrOfCompletedInstances + 1);
		return null;
	}
	public ShareniuCountersignAddCmd(String executionId, String assignee) {
		this.executionId = executionId;
		this.assignee = assignee;
	}
	
}
