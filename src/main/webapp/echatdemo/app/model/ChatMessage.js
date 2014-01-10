Ext.define('chat.model.ChatMessage', {
	extend: 'Ext.data.Model',

	fields: [ {
		name: 'username',
		type: 'string'
	}, {
		name: 'message',
		type: 'string'
	} ]
});