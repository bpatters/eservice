"use strict";

var ErrorDisplay = React.createClass({
	mixins: [React.addons.LinkedStateMixin],
	displayName: "ErrorDisplay",
	propTypes: {
		stores: React.PropTypes.arrayOf(React.PropTypes.object.isRequired),
		actionTypes: React.PropTypes.arrayOf(React.PropTypes.string.isRequired),
		bsStyle: React.PropTypes.string,
		clearDelay: React.PropTypes.number,
		defaultVisibility: React.PropTypes.bool
	},
	getInitialState: function () {
		return {
			errorMessage: this.props.errorMessage || null,
			timer: null
		};
	},
	componentWillMount: function () {
		_.each(this.props.stores, function (store) {
				store.addErrorListener(this._errorEvent)
			},
			this);
	},
	componentWillUnmount: function () {
		_.each(this.props.stores, function (store) {
				store.removeErrorListener(this._errorEvent)
			},
			this);
		if (this.state.timer != null) {
			clearInterval(this.state.timer);
		}
	},
	shouldComponentUpdate: function (np, nst) {
		return (this.state.errorMessage !== nst.errorMessage ||
				this.defaultVisibility != np.defaultVisibility);
	},
	render: function () {
		var Popover = ReactBootstrap.Popover;
		var OverlayTrigger = ReactBootstrap.OverlayTrigger;
		return (
			<OverlayTrigger {...this.props} overlay={
				<Popover placement={this.props.placement}>
					<span className="text-danger">{this.state.errorMessage}</span>
				</Popover>
				} ref="popover" trigger="manual" defaultOverlayShown={this.props.defaultVisibility}>
				<span> </span>
			</OverlayTrigger>
		);
	},
	show: function (errorMessage) {
		// clear to ensure we don't leak a timer
		if (this.state.timer != null) {
			clearInterval(this.state.timer);
		}
		var timer = null;
		if (this.props.clearDelay != 0) {
			timer = setInterval(this.hide, this.props.clearDelay);
		}
		this.setState({errorMessage: errorMessage, timer: timer});
		this.refs.popover.show();
	},
	hide: function () {
		if (this.state.timer != null) {
			clearInterval(this.state.timer);
		}
		this.setState({errorMessage: null, timer: null});
		this.refs.popover.hide();
	},
	_errorEvent: function (action) {
		var shouldProcess = _.find(this.props.actionTypes, function (actionType) {
			return (action.actionType == actionType)
		});
		if (typeof shouldProcess !== 'undefined') {
			this.show(action.data.body.message);
		}
	}
});

module.exports = ErrorDisplay;
