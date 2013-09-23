(function() {
	var fields = [ {
		name: 'Blocked'
	}, {
		name: 'BlockedBy'
	}, {
		name: 'Done',
		type: 'boolean'
	} ];

	Ext.define('App.model.CustomEvent', {
		extend: 'Sch.model.Event',
		fields: fields,

		// Sencha Touch
		config: {
			fields: fields
		},

		block: function(userName) {
			this.set({
				Blocked: true,
				BlockedBy: userName
			});
		},

		unblock: function() {
			this.set({
				Blocked: false,
				BlockedBy: null
			});
		}
	});
}());
