/**
 * author:ZHL 2015.10.27
 **/
jQuery.namespace('uwp');

$(document).ready(function() {
    var websocket = null;
    if (ws.browserSupportsWebSockets()) {
        websocket = new ws.webSocketClient();
    } else {
        var lMsg = ws.MSG_WS_NOT_SUPPORTED;
        showMessage(lMsg);
    }
    uwp.logon = function(host, port) {
        var lURL = ws.getServerURL("ws", host, port, "/webSocket");
        var thisDN = uwp.Agent.getInstance().getThisDN();
        var agentId = uwp.Agent.getInstance().getAgentId();
        var data = {"messageId":1,"thisDN":thisDN,"agentId":agentId};
        var lRes = websocket.logon(lURL, thisDN, agentId, $.toJSON(data), {
            OnOpen: function(aEvent) {
            	websocket.startKeepAlive({ immediate: false, thisDN: thisDN, agentId: agentId, interval: 30000 });            	
            },
            OnMessage: function(aEvent) {
            	var msg = $.parseJSON(aEvent.data);
				uwp.parseMessage(msg);
            },
            OnClose: function(aEvent) {
            	websocket.stopKeepAlive();
            }
        });
    };
    
    uwp.doOpen = function(host, port) {
	    if (!websocket.isConnected()) {
	        uwp.logon(host, port);
	    }
	};
	
    uwp.doClose = function() {
        if (websocket.isConnected()) {
        	websocket.stopKeepAlive();
            websocket.close();
        }
    };
	
	uwp.unsubscribe = function() {
		if (websocket) {
    		var thisDN = uwp.Agent.getInstance().getThisDN();
    		var agentId = uwp.Agent.getInstance().getAgentId();
		    var data = {"messageId":2,"thisDN":thisDN,"agentId":agentId};
		    uwp.send(data);
	    } else {
	    	showMessage("与消息服务器连接断开");
	    }
	}

    uwp.send = function(data) {
    	logMesssage(JSON.stringify(data));
        var thisDN = uwp.Agent.getInstance().getThisDN();
        var agentId = uwp.Agent.getInstance().getAgentId();
        if (websocket) {
            var lToken = {
                thisDN: thisDN,
                agentId: agentId,
                type: "request",
                message: $.toJSON(data)
            };
            websocket.sendToken(lToken);
        } else {
            showMessage("与消息服务器连接断开");
        }
    };
    
    uwp.parseMessage = function(data){
    	if(data == null)return;
    	uwp.handleEvent(data);
    };
});