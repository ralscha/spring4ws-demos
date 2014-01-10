Ext.define('chat.model.User', {
	extend: 'Ext.data.Model',
	
	idProperty: 'username',
	
	fields: [ {
		name: 'username',
		type: 'string'
	}, {
		name: 'browser',
		type: 'string'
	}, {
		name: 'image',
		type: 'string'
	}, {
		name: 'supportsWebRTC',
		type: 'boolean'
	}]
});