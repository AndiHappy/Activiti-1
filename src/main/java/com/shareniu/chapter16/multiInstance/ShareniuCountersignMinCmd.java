package com.shareniu.chapter16.multiInstance;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class ShareniuCountersignMinCmd implements Command<Void> {

	protected String taskId;

	public ShareniuCountersignMinCmd(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl pec = commandContext
				.getProcessEngineConfiguration();
		TaskService taskService = pec.getTaskService();
		RuntimeService runtimeService = pec.getRuntimeService();
		TaskEntity task = (TaskEntity) taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		String executionId = task.getExecutionId();// 获取executionId
		ExecutionEntity execution = (ExecutionEntity) runtimeService
				.createExecutionQuery().executionId(executionId).singleResult();
		int loopCounter = ShareniuLoopVariableUtils.getLoopVariable(execution,
				"nrOfInstances");
		int nrOfCompletedInstances = ShareniuLoopVariableUtils.getLoopVariable(
				execution, "nrOfActiveInstances");
		ShareniuLoopVariableUtils.setLoopVariable(execution, "nrOfInstances",
				loopCounter - 1);
		ShareniuLoopVariableUtils.setLoopVariable(execution,
				"nrOfActiveInstances", nrOfCompletedInstances - 1);
		task.setProcessInstanceId(null);
		task.setExecutionId(null);
		taskService.saveTask(task);
		ExecutionEntityManager executionEntityManager = commandContext
				.getExecutionEntityManager();
		executionEntityManager.deleteProcessInstance(executionId, "shareniu",
				false);
		taskService.deleteTask(taskId, false);
		return null;
	}
}
