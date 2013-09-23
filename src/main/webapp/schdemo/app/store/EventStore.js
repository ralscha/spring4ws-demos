Ext.define('App.store.EventStore', {
	extend: "Sch.data.EventStore",

	requires: [ 'App.model.CustomEvent' ],

	config: Ext.versions.touch ? {
		model: 'App.model.CustomEvent'
	} : null,

	model: 'App.model.CustomEvent',
	mixins: [ 'App.store.mixin.WampIO' ],

	proxy: {
		type: 'memory',
		reader: {
			type: 'json'
		}
	},

	constructor: function() {
		this.callParent(arguments);
		this.initSocket();
	}
});