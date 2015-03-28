"use strict";

var Dispatcher = require('../dispatcher/AppDispatcher');
var UserConstants = require('../constants/UserConstants');
var UserServices = require('../services/UserService');

var UserActions = {
	loadCurrentUser: function () {
		UserServices.loadCurrentUser().then(function (data) {
				Dispatcher.dispatch({
					actionType: UserConstants.USER_LOAD_COMPLETED,
					data: {
						statusCode: data.status.code,
						body: data.entity
					}
				})
			},
			function (data) {
				Dispatcher.dispatch({
					actionType: UserConstants.USER_LOAD_FAILED,
					data: {
						statusCode: data.status.code,
						body: data.entity
					}
				})
			});
	},
	loginUser: function (formData) {
		UserServices.loginUser(formData).then(function(data){
				Dispatcher.dispatch({
					actionType: UserConstants.USER_LOGIN_COMPLETED,
					data: {
						statusCode: data.status.code,
						body: data.entity
					}
				})
		},
		function (data) {
			Dispatcher.dispatch({
				actionType: UserConstants.USER_LOGIN_FAILED,
				data: {
					statusCode: data.status.code,
					body: data.entity
				}
			});
		});
	},
	logoutUser: function (formData) {
		UserServices.logoutUser().then(function(data){
				Dispatcher.dispatch({
					actionType: UserConstants.USER_LOGOUT_COMPLETED,
					data: {
						statusCode: data.status.code,
						body: data.entity
					}
				})
			},
			function (data) {
				Dispatcher.dispatch({
					actionType: UserConstants.USER_LOGOUT_FAILED,
					data: {
						statusCode: data.status.code,
						body: data.entity
					}
				});
			});
	},
	registerUser: function (formData) {
		var self = this;
		UserServices.registerUser(formData).then(function (data) {
			Dispatcher.dispatch({
				actionType: UserConstants.USER_REGISTER_COMPLETED
			});
		}, function(data) {
			Dispatcher.dispatch({
				actionType: UserConstants.USER_REGISTER_FAILED,
				data: {
					statusCode: data.status.code,
					body: data.entity
				}
			})
		});

	}
};


module.exports = UserActions;