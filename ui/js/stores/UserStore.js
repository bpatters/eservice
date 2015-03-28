/**
 * Created by bpatterson on 2/15/15.
 */
"use strict";

var Dispatcher      = require('../dispatcher/AppDispatcher');
var EventEmitter    = require('events').EventEmitter;
var UserConstants   = require('../constants/UserConstants');
var CHANGE_EVENT    = "change";
var ERROR_EVENT     = "error";

var _user;

function updateCurrentUser(user) {
	_user =  user;
};

var UserStore = _.extend(new EventEmitter(), {
	getCurrentUser: function () {
		return _user;
	},

	emitChange: function (action) {
		this.emit(CHANGE_EVENT, action);
	},
	emitError: function (action) {
		this.emit(ERROR_EVENT, action);
	},

	addChangeListener: function (callback) {
		this.on(CHANGE_EVENT, callback);
	},
	removeChangeListener: function(callback) {
		this.removeListener(CHANGE_EVENT, callback);
	},
	addErrorListener: function (callback) {
		this.on(ERROR_EVENT, callback);
	},
	removeErrorListener: function(callback) {
		this.removeListener(ERROR_EVENT, callback);
	}
});


Dispatcher.register(function (action) {
	switch (action.actionType) {
		case UserConstants.USER_LOAD_COMPLETED:
			updateCurrentUser(action.data.body);
			UserStore.emitChange(action);
			break;
		case UserConstants.USER_LOAD_FAILED:
			UserStore.emitError(action);
			break;
		case UserConstants.USER_LOGIN_COMPLETED:
			updateCurrentUser(action.data.body);
			UserStore.emitChange(action);
			break;
		case UserConstants.USER_LOGIN_FAILED:
			UserStore.emitError(action);
			break;
		case UserConstants.USER_LOGOUT_COMPLETED:
			updateCurrentUser(action.data.body);
			UserStore.emitChange(action);
			break;
		case UserConstants.USER_LOGOUT_FAILED:
			UserStore.emitError(action);
			break;
		case UserConstants.USER_REGISTER_COMPLETED:
			UserStore.emitChange(action);
			break;
		case UserConstants.USER_REGISTER_FAILED:
			UserStore.emitError(action);
			break;
	}
});


module.exports = UserStore;

