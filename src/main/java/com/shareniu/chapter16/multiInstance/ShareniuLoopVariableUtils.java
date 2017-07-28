package com.shareniu.chapter16.multiInstance;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

public class ShareniuLoopVariableUtils {
	public static void setLoopVariable(ExecutionEntity execution,
			String variableName, Object value) {
		// 获取执行实例的父级
		ActivityExecution parent = execution.getParent();
		// 设置变量
		parent.setVariableLocal(variableName, value);
	}
	public static Integer getLoopVariable(ExecutionEntity execution,
			String variableName) {
		// 获取变量
		Object value = execution.getVariableLocal(variableName);
		ActivityExecution parent = execution.getParent();
		while (value == null && parent != null) {
			value = parent.getVariableLocal(variableName);
			parent = parent.getParent();
		}
		return (Integer) (value != null ? value : 0);
	}
}
