"use strict";

var UserStore = require('../stores/UserStore');
var UserActions = require('../actions/UserActions');
var UserConstants = require('../constants/UserConstants');
var ErrorDisplay = require('./ErrorDisplay');


var RegisterForm = React.createClass({
	mixins: [React.addons.LinkedStateMixin],
	propTypes: {},
	getInitialState: function () {
		return {
			error: null,
			validationMessage: null,
			email: '',
			password: '',
			confirmPassword: '',
			isRegistering:false
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
		return (
		this.state.email != nst.email ||
		this.state.password != nst.password ||
		this.state.confirmPassword != nst.confirmPassword ||
		this.state.error != nst.error ||
		this.state.validationMessage != nst.validationMessage ||
		this.state.isRegistering != nst.isRegistering
		);
	},
	componentDidUpdate: function (np, ns) {
		this.validateForm();
	},
	emailValidationState: function () {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (this.state.email.length === 0) {
			return 'warning';
		}
		if (re.test(this.state.email)) {
			return 'success';
		} else {
			return 'error';
		}
	},
	validateForm: function () {
		var userGood = this.emailValidationState() != 'error';
		var passGood = this.validatePassword(this.state.password) != 'error';
		var samePass = this.state.password == this.state.confirmPassword;

		if (userGood && passGood && samePass) {
			this.setState({validationMessage: null});
		} else {
			if (!userGood) {
				this.setState({validationMessage: "Email address is invalid"});
			}
			if (!samePass) {
				this.setState({validationMessage: "Passwords do not match."});
			}
			if (!passGood) {
				this.setState({validationMessage: "Passwords must be at least 8 characters and contain a number."});
			}
		}

	},
	validatePassword: function (password) {
		var length = password.length;
		var hasNumber = /[0-9]+/;
		if (length === 0) {
			return 'warning';
		}
		if (length >= 8 && hasNumber.test(password)) {
			return 'success';
		} else {
			return 'error';
		}
	},
	canRegister: function () {
		return this.emailValidationState() == "success" &&
			this.validatePassword(this.state.password) == "success" &&
			this.validatePassword(this.state.confirmPassword) == "success" &&
			this.state.password == this.state.confirmPassword &&
			this.state.isRegistering == false;
	},
	handleRegister: function () {
		var self = this;

		this.setState({isRegistering:true});
		UserActions.registerUser({email: this.state.email, password: this.state.password});
	},
	toggleWindow: function () {
		this.props.onClose();
	},
	render: function () {
		var Input = ReactBootstrap.Input;
		var Button = ReactBootstrap.Button;
		var Label = ReactBootstrap.Label;
		var Modal = ReactBootstrap.Modal;
		var Alert = ReactBootstrap.Alert;
		var validEmail = this.emailValidationState();
		return (
			<div className="static-modal">
				<Modal title="Create New User"
					backdrop={true}
					animation={true}
					container={this.props.mountNode}
					onRequestHide={this.toggleWindow}
					closeButton={false}>
					<div className="modal-body">
						<Input name="email"
							type="text"
							valueLink={this.linkState('email')}
							placeholder="Email"
							bsStyle={validEmail}
							ref="email"
							groupClassName="form-group" tabIndex="1" >
						</Input>
						<Input name="password"
							type="password"
							valueLink={this.linkState('password')}
							placeholder="password"
							bsStyle={this.validatePassword(this.state.password)}
							ref="password"
							groupClassName="form-group" tabIndex="2"/>
						<Input
							type="password"
							placeholder="confirm password"
							bsStyle={this.validatePassword(this.state.confirmPassword)}
							ref="confirmPassword"
							groupClassName="form-group"
							valueLink={this.linkState('confirmPassword')} tabIndex="3"/>
						<h4 hidden={(this.state.validationMessage == null) && (this.state.error == null)}>
							<Alert bsStyle="danger">
                                {this.state.validationMessage || (this.state.error != null && this.state.error.message)}
							</Alert>
						</h4>
						<div className="modal-footer">
							<span className="pull-left">
								<Button onClick={this.toggleWindow} tabIndex="4">Cancel</Button>
							</span>
							<span className="pull-right">
								<Button onClick={this.handleRegister} bsStyle="primary" disabled={!this.canRegister()} tabIndex="5">Create User</Button>
							</span>
						</div>
					</div>
				</Modal>
			</div>
		);
	},
	_userStoreChanged: function (action) {
		switch (action.actionType) {
			case UserConstants.USER_REGISTER_COMPLETED:
				this.setState({error: null, isRegistering:false});
				this.toggleWindow();
				break;
		}
	},
	_userStoreError: function (action) {
		switch (action.actionType) {
			case UserConstants.USER_REGISTER_FAILED:
				this.setState({error: action.data.body, isRegistering:false});
				break;
		}
	}
});

module.exports = RegisterForm;
