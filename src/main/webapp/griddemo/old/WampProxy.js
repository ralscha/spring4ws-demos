Ext.define('AB.data.proxy.WampProxy', {
	extend: 'Ext.data.proxy.Server',
	alias: 'proxy.wamp',

	constructor: function(config) {
		var me = this;
		config = config || {};

		me.addEvents(
		/**
		 * @event exception Fires when the WAMP server returns an exception in response to a RPC
		 * @param {Ext.data.proxy.Proxy}
		 *            this
		 * @param {Object}
		 *            error The WAMP error object returned for the RPC
		 * @param {Ext.data.Operation}
		 *            operation The operation that triggered request
		 */
		'exception',

		/**
		 * @event oncreate Fires when an object was (remotely) created
		 * @param {Ext.data.proxy.Proxy}
		 *            this
		 * @param {Object}
		 *            id The object created
		 */
		'oncreate',

		/**
		 * @event onupdate Fires when an object was (remotely) update
		 * @param {Ext.data.proxy.Proxy}
		 *            this
		 * @param {Object}
		 *            id The object delta for the update (plus the object ID)
		 */
		'onupdate',

		/**
		 * @event ondestroy Fires when an object was (remotely) deleted
		 * @param {Ext.data.proxy.Proxy}
		 *            this
		 * @param {Object}
		 *            id The ID of the object deleted
		 */
		'ondestroy');

		me.callParent([ config ]);

		me.session = absession;
		// absession is not available on load time. only when autobahn connect has been established!
		// The problem is that most store classes get the session config at the class level on load time.
		// This is the reaseon why instead we use the global absession object subsequently.

		me.api = Ext.apply({}, config.api || me.api);
		// console.log('wampproxy ', arguments);
		if (me.api.oncreate) {
			me.session.subscribe(me.api.oncreate, function(topic, event) {
				if (me.debug) {
					console.log("AB.data.proxy.WampProxy.oncreate", event);
				}
				var obj = event;
				me.fireEvent('oncreate', me, obj);
			});
		}

		if (me.api.onupdate) {
			me.session.subscribe(me.api.onupdate, function(topic, event) {
				if (me.debug) {
					console.log("AB.data.proxy.WampProxy.onupdate", event);
				}
				var obj = event;
				me.fireEvent('onupdate', me, obj);
			});
		}

		if (me.api.ondestroy) {
			me.session.subscribe(me.api.ondestroy, function(topic, event) {
				if (me.debug) {
					console.log("AB.data.proxy.WampProxy.ondestroy", event);
				}
				var id = event;
				me.fireEvent('ondestroy', me, id);
			});
		}
	},

	doRequest: function(operation, callback, scope) {
		var me = this, writer = me.getWriter(), request = me.buildRequest(operation), params, fn;

		fn = me.api[request.action];

		if (operation.allowWrite()) {
			request = writer.write(request);
		}

		if (operation.action == 'read') {
			params = request.params;
		} else {
			params = request.jsonData;
		}

		// issue WAMP RPC
		me.session.call(fn, params).then(
		// process WAMP RPC success result
		function(response) {
			me.processResponse(true, operation, request, response, callback, scope);
		},

		// process WAMP RPC error result
		function(err) {
			me.processResponse(false, operation, request, response, callback, scope);
		});

	},

	buildRequest: function(operation) {
		var me = this, params = operation.params = Ext.apply({}, operation.params, me.extraParams), request;

		Ext.applyIf(params, me.getParams(operation));

		if (operation.id !== undefined && params[me.idParam] === undefined) {
			params[me.idParam] = operation.id;
		}

		request = new Ext.data.Request({
			params: params,
			action: operation.action,
			records: operation.records,
			operation: operation
		});

		operation.request = request;
		return request;
	}

});