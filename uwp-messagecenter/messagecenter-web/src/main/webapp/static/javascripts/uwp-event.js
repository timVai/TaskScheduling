/**
 * author:ZHL 2015.10.27
**/
jQuery.namespace('uwp');

var msgState = CHANNELSTATE.Closed;

var debug = true;

var initialized = false;

uwp.handleEvent = function(data){
	if(data.mediaType==MEDIATYPE.Voice) {
		uwp.handleCTIEvent(data);
	} else if(data.mediaType==MEDIATYPE.Im) {
		uwp.handleIMEvent(data);
	} else if(data.mediaType==MEDIATYPE.Biz) {
		uwp.handleBIZEvent(data);
	}
};

uwp.handleCTIEvent = function(data){
	
};

uwp.handleBIZEvent = function(data) {
	logMesssage(JSON.stringify(data));
	try{
		if(data.messageId==MessageID.EventLinkConnected){
			msgState = CHANNELSTATE.Opened;
			if(!initialized) {
				initialized = true;
				uwp.agentLogin();
			}
		}else if(data.messageId==MessageID.EventLinkDisconnected){
			showMessage("与消息服务器连接已断开");
			msgState = CHANNELSTATE.Closed;
		}else if(data.messageId==MessageID.EventError){
			showMessage(data.errorMessage);
		}else if(data.messageId==MessageID.EventAgentLogin){
			uwp.handleAgentEvent(data);
		}else if(data.messageId==MessageID.EventAgentLogout){
			uwp.handleAgentEvent(data);
		}else{
			uwp.handleTaskEvent(data);
		}
	}catch(e){
		if (window.console && window.console.log) {
			window.console.log(e);
		}
	}
};

uwp.handleIMEvent = function(data) {

};

uwp.handleAgentEvent = function(data) {
	if(data.messageId==MessageID.EventAgentLogin) {
		$("#mediaState").text("Logged On");
		$('#agentStateMenu').menu('disableItem','#logon');
		$('#agentStateMenu').menu('enableItem','#logoff');
	}else if(data.messageId==MessageID.EventAgentLogout) {
		$("#mediaState").text("Logged Off");
		$('#agentStateMenu').menu('enableItem','#logon');
		$('#agentStateMenu').menu('disableItem','#logoff');
	}
};

uwp.handleTaskEvent = function(data){
	var taskId = data.taskId;
	if(data.messageId==MessageID.EventTaskStopped) {
		if ($('#taskTabs').tabs('exists', "任务"+taskId)) {
			$('#taskTabs').tabs('close', "任务"+taskId);
		}
	}else if(data.messageId==MessageID.EventTaskInfo) {
		$('#taskTabs').tabs('add', {
			title: "任务"+taskId,
			href: 'task.jsp?taskId='+taskId,
			closable: true
		});
	}
};

function logMesssage(result) {
	if (debug && window.console && window.console.log) {
		window.console.log(result);
	}
}