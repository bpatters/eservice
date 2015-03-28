"use strict";

// initialize the global app object
var UserStore = require('./../stores/UserStore');
var UserActions = require('./../actions/UserActions');
var UserConstants = require('./../constants/UserConstants');
var ErrorDisplay = require('./ErrorDisplay');
var LoginForm = require('./LoginForm');
var RegisterForm = require('./RegisterForm');

var Grid = ReactBootstrap.Grid;
var Row = ReactBootstrap.Row;
var Col = ReactBootstrap.Col;
var ModalTrigger = ReactBootstrap.ModalTrigger;
var Button = ReactBootstrap.Button;
var DropdownButton = ReactBootstrap.DropdownButton;
var MenuItem = ReactBootstrap.MenuItem;

var EServiceApp = React.createClass({

    getInitialState: function () {
        return {
            loginVisible: false,
            registerVisible: false,
        };
    },
    componentDidMount: function () {
        UserStore.addChangeListener(this._userStoreChanged);
        UserActions.loadCurrentUser();
    },
    componentWillUnmount: function () {
        UserStore.removeChangeListener(this._userStoreChanged);
    },
    _logout: function () {
        UserActions.logoutUser();
    },
    _toggleLogin: function () {
        this.setState({loginVisible: !this.state.loginVisible});
    },
    _toggleRegister: function () {
        this.setState({registerVisible: !this.state.registerVisible});
    },
    renderLoginForm: function () {
        if (this.state.loginVisible) {
            return ( <LoginForm onClose={this._toggleLogin}/> )
        }
    },
    renderRegisterForm: function () {
        if (this.state.registerVisible) {
            return ( <RegisterForm onClose={this._toggleRegister}/> )
        }
    },
    renderNavBar: function () {
        var user = UserStore.getCurrentUser();
        var divStyle = {
            margin: "5px",
            float: "right"
        };
        if (!user) {
            return (
                <div style={divStyle}>
                    <Button bsStyle="primary" onClick={this._toggleLogin}>
                        Login
                    </Button>
                    <Button style={{"margin-left": "5px"}} bsStyle="primary" onClick={this._toggleRegister}>
                        Register
                    </Button>
                </div>
            );
        } else {
            return (
                <div style={divStyle}>
                    <Button bsStyle="success" onClick={this._logout}>
                        {user.email} - Logout
                    </Button>
                </div>
            );
        }
        return
    },
    render: function () {
        var stores = [UserStore];
        var actionTypes = [UserConstants.USER_LOGIN_FAILED, UserConstants.USER_REGISTER_FAILED];
        var titleBarStyles = {
            "width": "100%",
            "position": "fixed",
            "top": "0px",
            "left": "0px",
            "height": "49px",
            "background-color": "White",
            "box-shadow": "0px 1px 1px #BBBBBB"
        }

        return (
            <div >
                {this.renderLoginForm()}
                {this.renderRegisterForm()}
                <div className="fixed-element" style={titleBarStyles}>
                    <Grid fluid={true} style={{"padding": "0px", "margin": "auto"}}>
                        <Row fluid={true}>
                            <Col lg={12}>
                                {this.renderNavBar()}
                            </Col>
                        </Row>
                    </Grid>
                </div>
                <div>
                    Place your desired content here
                </div>
            </div>
        );
    },
    _userStoreChanged: function (action) {
        // trigger  re-render
        switch (action.actionType) {
            case UserConstants.USER_LOAD_COMPLETED:
            case UserConstants.USER_LOGOUT_COMPLETED:
                this.setState({loginVisible: false});
                break;
        }
    }


})


module.exports = EServiceApp;

