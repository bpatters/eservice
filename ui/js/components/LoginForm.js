"use strict";

var UserStore = require('../stores/UserStore');
var UserActions = require('../actions/UserActions');
var UserConstants = require('../constants/UserConstants');
var ErrorDisplay = require("./ErrorDisplay");

var LoginForm = React.createClass({
	mixins: [React.addons.LinkedStateMixin],
	displayName: "LoginForm",
	propTypes: {
		onClose: React.PropTypes.func
	},
	getInitialState: function () {
		return {
			isLoggingIn: false,
			email: '',
			password: ''
		};
	},
	componentWillMount: function () {
		UserStore.addChangeListener(this._userStoreChanged);
		UserStore.addErrorListener(this._userStoreError);
	},
	componentWillUnmount: function () {
		UserStore.removeChangeListener(this._userStoreChanged);
		UserStore.removeErrorListener(this._userStoreError);
	},
	shouldComponentUpdate: function (np, nst) {
		return this.state.user !== nst.user ||
			this.state.email !== nst.email ||
			this.state.password !== nst.password ||
			this.state.isLoggingIn !== nst.isLoggingIn;
	},
	canLogin: function () {
		return this.state.email.length > 0 &&
			this.state.password.length > 0 &&
			this.state.isLoggingIn == false;
	},
	onLogin: function () {
		UserActions.loginUser({email: this.state.email, password: this.state.password});
		this.setState({isLoggingIn: true});
	},
	onKeyUp: function (ev) {
		if (ev.keyCode == 13) {
			ev.preventDefault();
			this.onLogin();
		}
	},
	render: function () {
		var Input = ReactBootstrap.Input;
		var Button = ReactBootstrap.Button;
		var Label = ReactBootstrap.Label;
		var Grid = ReactBootstrap.Grid;
		var Row = ReactBootstrap.Row;
		var Col = ReactBootstrap.Col;
		var Modal = ReactBootstrap.Modal;

		return (
			<div className="static-modal">
				<Modal title="Login "
					backdrop={true}
					animation={true}
					onRequestHide={this.props.onClose}
					closeButton={false}>
					<div className="modal-body">
						<Input
							name="email"
							type="text"
							placeholder="Email"
							ref="email"
							valueLink={this.linkState('email')} tabIndex="2">
						</Input>
						<ErrorDisplay stores={[UserStore]} actionTypes={[UserConstants.USER_LOGIN_FAILED]} className="pull-right" clearDelay={2000} placement="bottom"/>
						<Input name="password"
							type="password"
							placeholder="Password"
							ref="password"
							valueLink={this.linkState('password')} tabIndex="2" onKeyUp={this.onKeyUp}/>
						<div className="modal-footer">
							<span className="pull-left">
								<Button onClick={this.props.onClose} tabIndex="4">Cancel</Button>
							</span>
							<span className="pull-right">
								<Button onClick={this.onLogin} disabled={!this.canLogin()} bsStyle="primary" bsSize="small" tabIndex="3">Login</Button>
							</span>
						</div>
					</div>
				</Modal>
			</div>
		);
	},
	_userStoreChanged: function (action) {

		var newState = {
			error: null,
			user: UserStore.getCurrentUser(),
			email: '',
			password: ''
		};
		switch (action.actionType) {
			case UserConstants.USER_LOGIN_COMPLETED:
				newState.isLoggingIn = false;
				break;
		}
		if (!!this.props.onClose) {
			this.props.onClose();
		}
		this.setState(newState);
	},
	_userStoreError: function (action) {
		switch (action.actionType) {
			case UserConstants.USER_LOGIN_FAILED:
			case UserConstants.USER_LOGOUT_FAILED:
				this.setState({isLoggingIn: false});
				break;
		}
	}
});

module.exports = LoginForm;
