package com.crowd.service.testcenter;

import org.json.JSONObject;

import com.crowd.service.base.CrowdContext;
import com.crowd.service.base.CrowdInitContext;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.service.type.GUID;

public class TestCenterService implements CrowdService {

	private AgentServer agentServer;

	public void init(CrowdInitContext context) throws Throwable {
		agentServer = new AgentServer();
		new Thread(agentServer).start(); 
	}

	public String getName() {
		return "testserver";
	}

	@CrowdMethod
	public void doTest(CrowdContext context, JSONObject input, JSONObject output) throws Throwable {
		// TODO：查询注册的AgentClient，根据client情况分解任务，并调用，处理并合并结果返回
		String serviceName = input.getString("serviceName");
		String arguments = input.getString("arguments");
		String symbol = input.getString("symbol");
		String rate = input.getString("rate");
		String dateSource = input.getString("dateSource");

		JSONObject taskInfo = new JSONObject();
		taskInfo.put("id", GUID.randomID().toString());
		taskInfo.put("serviceName", serviceName);
		taskInfo.put("arguments", arguments);
		taskInfo.put("symbol", symbol);
		taskInfo.put("rate", rate);
		taskInfo.put("dateSource", dateSource);

		this.agentServer.requestTestTask(taskInfo);
	}

}
