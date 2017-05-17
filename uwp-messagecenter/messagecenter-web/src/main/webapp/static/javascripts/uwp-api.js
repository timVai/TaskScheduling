/**
 * author:ZHL 2015.10.22
**/
jQuery.namespace('uwp');

uwp.agentLogin = function() {
	var agentId = uwp.Agent.getInstance().getAgentId();
	var data = {"messageId":1001,"agentId":agentId};
	uwp.send(data);
}
uwp.agentLogout = function() {
	var agentId = uwp.Agent.getInstance().getAgentId();
	var data = {"messageId":1002,"agentId":agentId};
	uwp.send(data);
}
uwp.stopTask = function (taskId) {
	var agentId = uwp.Agent.getInstance().getAgentId();
	var data = {"messageId":1003,"agentId":agentId,"mediaType":4,"taskType":1,"taskId":taskId};
	uwp.send(data);
}