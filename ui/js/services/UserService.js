"user strict";

var client = require("./network");

var UserService = {
	loadCurrentUser: function () {
		return client({
			path:"/ws/user",
			headers: {"Content-Type": "application/json"}
		});
	},
	loginUser: function (formData) {
		return client({
			path: "/ws/user/login",
			method: "POST",
			entity: formData,
			headers: {"Content-Type": "application/json"}
		});
	},
	logoutUser: function () {
		return client({
			path: "/ws/user/logout",
			method: "POST",
			headers: {"Content-Type": "application/json"}
		});
	},
	registerUser: function (formData) {
		return client({
			path: "/ws/user",
			method: "POST",
			entity: formData,
			headers: {"Content-Type": "application/json"}
		});
	}
};

module.exports = UserService;