package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestLeaveDemo {

	private String filename = "C:/work/codes/cd1806/activiti-demo/src/main/resources/LeaveDemo.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		// 1.发布流程
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		repositoryService.createDeployment().addInputStream("leaveDemo.bpmn20.xml",
				new FileInputStream(filename)).deploy();
		// 2.启动流程，开始一个流程实例
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		// 设置流程参数
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveDemo", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("processInstanceId:" + processInstance.getId() + ",processDefinitionId:"
				+ processInstance.getProcessDefinitionId());
		// 3.获取“申请”任务并提交
		Task task = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		System.out.println("taskId:" + task.getId() + ", taskDefinitionKey:" + task.getTaskDefinitionKey());
		// 设置“申请”任务的表单参数
		Map<String, String> taskFormData = new HashMap<String, String>();
		taskFormData.put("applyId", "1");
		taskFormData.put("applyName", "张三");
		taskFormData.put("applyHours", "16");
		taskFormData.put("applyReason", "结婚");
		// 提交“申请”任务
		activitiRule.getFormService().submitTaskFormData(task.getId(), taskFormData);
		// 4.获取“审批”任务并提交
		Task task2 = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		System.out.println("task2Id:" + task2.getId() + ", task2DefinitionKey:" + task2.getTaskDefinitionKey());
		// 设置“审批”任务的表单数据
		Map<String, String> task2FormData = new HashMap<String, String>();
		task2FormData.put("reviewId", "2");
		task2FormData.put("reviewName", "李四");
		task2FormData.put("reviewRemark", "OK");
		task2FormData.put("reviewResult", "true");
		// 提交“审批”任务
		activitiRule.getFormService().submitTaskFormData(task2.getId(), task2FormData);
		// 4.1.审批通过，流程结束
		Task task3 = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		Assert.assertNull(task3);
		System.out.println("流程结束");
		// 4.2.审批不通过，则获取“申请”任务
//		Task task3 = activitiRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
//		System.out.println("task3Id:" + task3.getId() + ", task3DefinitionKey:" + task3.getTaskDefinitionKey());
	}
}