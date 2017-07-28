package com.shareniu.chapter16.multiInstance.weight;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEventDispatcher;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shareniu.chapter3.DeploymentBuilderTest;

public class App {
	// 获取到Activiti ProcessEngine
	ProcessEngine processEngine = null;
	// 获取RepositoryService 实例对象
	RepositoryService repositoryService = null;
	// 资源名称
	String resourceName = "shareniu_addInputStream.bpmn";
	IdentityService identityService;
	RuntimeService runtimeService;
	TaskService taskService;
	ActivitiEventDispatcher eventDispatcher;

	@Before
	public void init() {
		InputStream in = App.class.getClassLoader().getResourceAsStream(
				"com/shareniu/chapter16/multiInstance/weight/activiti.cfg.xml");
		ProcessEngineConfiguration pcf = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromInputStream(in);
		processEngine = pcf.buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		ProcessEngineConfigurationImpl pc = (ProcessEngineConfigurationImpl) processEngine
				.getProcessEngineConfiguration();
		eventDispatcher = pc.getEventDispatcher();
	}
	
	
	@Test
	public void addInputStreamTest() throws IOException {
		//taskJuel.bpmn        subProcess.bpmn   activitybehavior.bpmn20.xml    /activiti/src/main/java/com/shareniu/chapter16/jump/jump.bpmn
		//  sequential.bpmn    multiInstance.bpmn   collectionmultiInstance.bpmn  collectionmultiInstance.bpmn
		InputStream inputStream = DeploymentBuilderTest.class.getClassLoader()
				.getResource("com/shareniu/chapter16/multiInstance/weight/collectionmultiInstance.bpmn").openStream();
		// 流程定义的分类
		String category = "shareniu_addInputStream";
		// 构造DeploymentBuilder对象
		DeploymentBuilder deploymentBuilder = repositoryService
				.createDeployment().category(category)
				.addInputStream(resourceName, inputStream);
		// 部署
		Deployment deploy = deploymentBuilder.deploy();
		System.out.println(deploy);
	}
	@Test
	public void startProcessInstanceById() throws IOException {
		Map<String, Object> vars=new HashMap<String, Object>();
		String[]v={"shareniu1","shareniu2","shareniu3"};
		vars.put("assigneeList",  Arrays.asList(v));
		runtimeService.startProcessInstanceById("wegitInstance:1:4",vars);
	}
	
	@Test
	public void testComplete() throws IOException {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("pass", true);
		taskService.complete("22521",map);
		map.put("pass", true);
		taskService.complete("22526",map);
		map.put("pass", false);
		taskService.complete("22516",map);
	}
	@Test
	public void testObjectMapper() throws IOException {
		Map<String, Object> info=new HashMap<>();
		info.put("shareniu1", 0.8);
		info.put("shareniu2", 0.1);
		info.put("shareniu3", 0.1);
		Map<String, Object> map=new HashMap<>();
		map.put("info", info);
		map.put("conditions", 0.6);
		ObjectMapper objectMapper = new ObjectMapper();
		String writeValueAsString = objectMapper.writeValueAsString(map);
		System.out.println(writeValueAsString);
	}
	
}
