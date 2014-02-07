/**
 * WAMP client manager for ExtJs
 * wrapper of WAMP API http://autobahn.ws/js/reference/
 * 
 * @author dbs
 * @version V0.1 initial version
 */
Ext.define ('Ext.ux.ws.wamp.Manager', {
	alternateClassName: ['Ext.WampMgr'],
	singleton: true,
	mixins: {
        observable: 'Ext.util.Observable'
    },
	/*
	 * WebSocket URI of WAMP server.
	 */
	wsuri: window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1) + 'wamp',
	baseUri: window.location.href.substring(0, window.location.href.lastIndexOf('/')+1),
	absession: null, // the session object
	appKey: null,
	appSecret: null,
	maxRetries: 10,
	retryDelay: 300,
	
	constructor: function(config) {
		Ext.apply(this, config || {});
        this.addEvents(
            /**
             * @event connect
             *
             * Fires after successful connection.
             *
             * @param {object} session The client session with WAMP server.
             */
            'connect',
            
            /**
             * @event hangup
             *
             * Fires when session has been closed, lost or failed to be established in the first place.
             *
             * @param {String} code.
             * @param {String} reason
             */
            'hangup',
            
            /**
             * @event auth
             *
             * Fires when connection is authenticated with permissions.
             *
             * @param {Object} permissions.
             */
            'auth'
        );
		this.mixins.observable.constructor.call(this);
	},
	setBaseUri: function(uri){
		this.baseUri = uri;
		return this;
	},
	isActive: function(){
		return this.absession !== null;
	},
	setDebug: function(isDebug){
		ab.debug(this.debug = isDebug);
		return this;
	},
	start: function(){
		if(this.isActive())
			return;
		var me = this;
		ab._construct = function(url, protocols) {
			return new SockJS(url);
		};
		ab.connect(me.wsuri, me.onconnect, me.onhangup, {	//params
			maxRetries: me.maxRetries,
			retryDelay: me.retryDelay,
			skipSubprotocolCheck: true
		});
	},
	/**
	 * Close the session
	 */
	stop: function(){
		if(this.isActive())
			this.absession.close();
	},
	/**
	 * register PREFIX on WAMP server
	 * reduce URI verbosity by using CURIE (Compact URI Expression) 
	 */
	regPrefix: function(prefix){
		if(this.isActive() && prefix){
			this.absession.prefix(prefix, Ext.String.format('{0}{1}#', this.baseUri, prefix));
			this.topicPrefix = prefix;
		}
	},
	/**
	 * Callback fired when session has been established.
	 * @private
	 */
	onconnect: function(sess){
		var me = Ext.WampMgr;
		me.absession = sess;

		console.log('WAMP connected! ' + sess);
		me.fireEvent('connect', sess);

		if (me.appKey !== null) // anonymous auth
			me.absession.authreq(me.appKey, extra).then(function(challenge) {
				var signature = me.absession.authsign(challenge, me.appSecret);
				me.absession.auth(signature).then(

				function(permissions) {
					me.permissions = permissions;
					// console.log("Connection authenticated with permissions: ", permissions);

					me.fireEvent('auth', permissions);
				}, ab.log);
			}, ab.log);
		else
			sess.authreq().then(function() {
				sess.auth().then(function(permissions) {
					me.permissions = permissions;
					// console.log("Connection authenticated with permissions: ", permissions);

					me.fireEvent('auth', permissions);
				}, ab.log);
			}, ab.log);
	},
	/**
	 * Callback fired when session has been closed, lost or failed to be established in the first place
	 * @private
	 */
	onhangup: function(code, reason, detail) {
		var me = Ext.WampMgr;
		me.absession = null;
		switch (code) {
			case ab.CONNECTION_UNSUPPORTED:
				//window.location = "http://absession.ws/unsupportedbrowser";
				break;
			case ab.CONNECTION_CLOSED:
				console.log("WAMP connection closed!", code, reason, detail);
				//window.location.reload();
				break;
			default:
				console.log(code, reason, detail);
				break;
		}
	},
	/**
	 * subscribe
	 */
	subscribe: function(prefix, topic, fn){
		if(!this.isActive())
			return;
		this.absession.subscribe(prefix ? Ext.String.format('{0}:{1}', prefix, topic) : topic, fn);
	}
});