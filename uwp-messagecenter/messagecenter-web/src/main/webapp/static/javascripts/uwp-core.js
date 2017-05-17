/**
 * author:ZHL 2015.10.26
**/
jQuery.namespace('uwp');

uwp.Agent = function(){
	this.state = 'Logout';
	this.thisDN = '';
	this.agentId = '';
};

uwp.Agent.instance = null;

uwp.Agent.getInstance = function(){
	if (uwp.Agent.instance == null) {
		uwp.Agent.instance = new uwp.Agent();
	}
	return uwp.Agent.instance;
};

uwp.Agent.prototype.init = function(thisDN, agentId){
	this.thisDN = thisDN;
	this.agentId = agentId;
};

uwp.Agent.prototype.setState = function(state){
	this.state = state;
};

uwp.Agent.prototype.getState =function(){
	return this.state;
};

uwp.Agent.prototype.getThisDN = function(){
	return this.thisDN;
};

uwp.Agent.prototype.getAgentId = function(){
	return this.agentId;
};

uwp.Agent.prototype.changeAgentState = function(messageId, state){
	this.setState(state);
};

uwp.Agent.prototype.reset = function(){
	this.state = 'Logout';
};