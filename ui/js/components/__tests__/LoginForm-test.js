// __tests__/LoginForm-test.js
"use strict";

jest.autoMockOff();

describe('A user is presented', function() {
    var LoginForm = require('../LoginForm');
	var UserStore = require('../../stores/UserStore')
    var TestUtils = React.addons.TestUtils;
	var Dispatcher = require("../../dispatcher/AppDispatcher");
	var UserConstants = require("../../constants/UserConstants");
    var loginForm;

    beforeEach(function() {
        loginForm = TestUtils.renderIntoDocument(<LoginForm/> );
    }),

    it("with a login form that allows the user to enter a email and password", function() {
        var inputTags = TestUtils.scryRenderedDOMComponentsWithTag(loginForm, "input");
        var emailInput = _.find(inputTags, function(input) { return input.props.name == "email";});
        var passwordInput = _.find(inputTags, function(input) { return input.props.name == "password";});

        expect(emailInput.getDOMNode().value).toEqual("");
        expect(passwordInput.getDOMNode().value).toEqual("");

        expect(emailInput.props.placeholder).toEqual("Email");
        expect(passwordInput.props.placeholder).toEqual("Password");

        TestUtils.Simulate.change(emailInput.getDOMNode(), {target: {value: "bret@bretpatterson.com"}});
        TestUtils.Simulate.change(passwordInput.getDOMNode(), {target: {value: "password"}});

        expect(emailInput.getDOMNode().value).toEqual("bret@bretpatterson.com");
        expect(passwordInput.getDOMNode().value).toEqual("password");
    }),

    it ("the user to submit the form to log them into the system", function() {
        var inputTags = TestUtils.scryRenderedDOMComponentsWithTag(loginForm, "input");
        var emailInput = _.find(inputTags, function(input) { return input.props.name == "email";});
        var passwordInput = _.find(inputTags, function(input) { return input.props.name == "password";});
        var submitButton = TestUtils.findRenderedDOMComponentWithTag(loginForm, "button");

        expect(submitButton.getDOMNode()["disabled"]).toBeTruthy();
        TestUtils.Simulate.change(emailInput.getDOMNode(), {target: {value: "bret@bretpatterson.com"}});
        TestUtils.Simulate.change(passwordInput.getDOMNode(), {target: {value: "password"}});

        expect(submitButton.getDOMNode()["disabled"]).toBeFalsy();
    }),
	it ("if a user is logged in it displays their username and presents a logout button", function() {
		// update user store with the current user
		Dispatcher.dispatch({
			actionType: UserConstants.USER_LOGIN_COMPLETED,
			data: {
				statusCode: 200,
				body: { email:"bret@bretpatterson.com", id:"asdfsaf"}
			}
		});

		var spanTags = TestUtils.scryRenderedDOMComponentsWithTag(loginForm, "span");
		var email = _.find(spanTags, function(span) { return span.props.name == "email";});

		expect(email).toBeTruthy();

	})
});
