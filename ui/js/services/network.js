"use strict";

var rest = require("rest");
var mime = require('rest/interceptor/mime');
var csrf = require('rest/interceptor/csrf');
var errorCode = require('rest/interceptor/errorCode');
function calcToken(a){return a?(a^Math.random()*16>>a/4).toString(16):([1e16]+1e16).replace(/[01]/g,calcToken)};
var token = calcToken();

document.cookie = "X-CSRF-TOKEN="+token;

var client = rest.wrap(mime).wrap(csrf, {token: token}).wrap(errorCode);

module.exports = client;
