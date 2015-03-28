"use strict";

var _              = require("underscore");
var buffer         = require("vinyl-buffer");
var gulp           = require('gulp');
var forEach        = require("gulp-foreach");
var util           = require("gulp-util");
var uglify         = require("gulp-uglify");
var size           = require("gulp-size");
var concat         = require("gulp-concat");
var sourcemaps     = require("gulp-sourcemaps");
var clean          = require("gulp-clean");
var jest           = require("gulp-jest");
var browserify     = require("browserify");
var source         = require("vinyl-source-stream");
var runSequence    = require('run-sequence');
var watchify       = require('watchify');

/*
 * Configuration
 */
var APPS_GLOB   = "js/app.js";
var APP_DIST_DIR = "./www";
var JS_DIST_DIR = APP_DIST_DIR+"/js";
var JS_LIB_DIST_DIR = JS_DIST_DIR+"/lib";

var EXTERNAL_LIBS = {
	underscore: "./node_modules/underscore/underscore-min.js",
	react: "./node_modules/react/dist/react-with-addons.js",
	reactBootstrap: "./node_modules/react-bootstrap/amd/react-bootstrap.min.js",
	moment: "./node_modules/moment/min/moment-with-locales.min.js"
};
var BROWSERIFY_TRANSFORMS = [];

var SIZE_OPTS = {
	showFiles: true,
	gzip: true
};

gulp.task('default', function() {
	return runSequence('clean','html','css','fonts','js-common-lib','autobuild');
});


gulp.task("autobuild", function() {
	return gulp.src(APPS_GLOB)
		.pipe(forEach(function(stream, file) {
			// Get our bundler just like in the "build" task, but wrap it with watchify and use the watchify default args (options).
			var bundler = watchify(getBundler(file, watchify.args));

			function rebundle() {
				// When an automatic build happens, create a flag file so that we can prevent committing these bundles because of
				// the full paths that they have to include.  A Git pre-commit hook will look for and block commits if this file exists.
				// A manual build is require before bundled assets can be committed as it will remove this flag file.
				//shell.exec("touch " + AUTOBUILD_FLAG_FILE);

				return bundle(file, bundler);
			}

			bundler.on("update", rebundle);
			bundler.on("error",function () {});

			return rebundle();
		}));
});

gulp.task('html', function() {
	return gulp.src("html/**/*.html")
		.pipe(gulp.dest(APP_DIST_DIR));
});

gulp.task('css', function() {
	return gulp.src("css/**/*.css")
		.pipe(gulp.dest(APP_DIST_DIR+"/css"));
});
gulp.task('fonts', function() {
	return gulp.src("fonts/**/*")
		.pipe(gulp.dest(APP_DIST_DIR+"/fonts"));
});

gulp.task('clean', function() {
	return gulp.src(APP_DIST_DIR+"/*").pipe(clean());
});


/*
* Externalize all site-wide libraries into one file.  Since these libraries are all sizable, it would be better for the
* client to request it individually once and then retreive it from the cache than to include all of these files into
* each and every browserified application.
*/
gulp.task("js-common-lib", function() {
	var paths = [];

	// Get just the path to each externalizable lib.
	_.forEach(EXTERNAL_LIBS, function(path) {
		paths.push(path);
	});

	return gulp.src(paths)
		// Log each file that will be concatenated into the common.js file.
		.pipe(size(SIZE_OPTS))
		// Concatenate all files.
		.pipe(concat("common.min.js"))
		// Minify the result.
		.pipe(buffer())
		// Save the source map for later (uglify will remove it since it is a comment)
		.pipe(sourcemaps.init({loadMaps: true}))
		.pipe(sourcemaps.write())
	//	.pipe(uglify())
		// Log the new file size.
		.pipe(size(SIZE_OPTS))
		// Save that file to the appropriate location.
		.pipe(gulp.dest(JS_LIB_DIST_DIR));
});



/**
 * Browserify and minify each individual application found with APPS_GLOB.  Each file therein represents a separate
 * application and should have its own resultant bundle.
 */
gulp.task("js",["js-common-lib"], function() {
	var stream = gulp.src(APPS_GLOB)
		.pipe(forEach(function(stream, file) {
			bundle(file, getBundler(file));
		}));

	// A normal build has completed, remove the flag file.
//	shell.rm("-f", AUTO_BUILD_FLAG_FILE);

	return stream;
});

/*
* Get a properly configured bundler for manual (browserify) and automatic (watchify) builds.
*
* @param {object} file The file to bundle (a Vinyl file object).
* @param {object|null} options Options passed to browserify.
*/
function getBundler(file, options) {
	options = _.extend(options || {}, {
		// Enable source maps.
		debug: true,
		// Configure transforms.
		transform: BROWSERIFY_TRANSFORMS
	});

	// Initialize browserify with the file and options provided.
	var bundler = browserify(file.path, options).on("error", function (){});

	// Exclude externalized libs (those from build-common-lib).
	Object.keys(EXTERNAL_LIBS).forEach(function(lib) {
		bundler.external(lib);
	});

	return bundler;
}

/*
 * Build a single application with browserify creating two differnt versions: one normal and one minified.
 *
 * @param {object} file The file to bundle (a Vinyl file object).
 * @param {browserify|watchify} bundler  The bundler to use.  The "build" task will use browserify, the "autobuild" task will use watchify.
 */
function bundle(file, bundler) {
	// Remove file.base from file.path to create a relative path.  For example, if file looks like
	//   file.base === "/Users/johnsonj/dev/web/super-project/applications/client/<i>apps</i>/"
	//   file.path === "/Users/johnsonj/dev/web/super-project/applications/client/<i>apps</i>/login/reset-password/confirm.js"
	// then result is "login/reset-password/confirm.js"
	var relativeFilename = file.path.replace(file.base, "");

	return bundler
		// Log browserify errors
		.on("error", util.log.bind(util, "Browserify Error"))
		// Bundle the application
		.bundle()
		// Rename the bundled file to relativeFilename
		.pipe(source(relativeFilename))
		// Convert stream to a buffer
		.pipe(buffer())
		// Save the source map for later (uglify will remove it since it is a comment)
		.pipe(sourcemaps.init({loadMaps: true}))
		.pipe(sourcemaps.write())
		//.pipe(uglify())
		.pipe(size(SIZE_OPTS))
		// Save normal source (useful for debugging)
		.pipe(gulp.dest(JS_DIST_DIR));

		// Minify source for production
/*		.pipe(uglify())
		// Restore the sourceMap
		.pipe(sourcemaps.write())
		// Add the .min suffix before the extension
		.pipe(rename({suffix: ".min"}))
		// Debuging output
		.pipe(size(SIZE_OPTS))
		// Write the minified file.
		.pipe(gulp.dest(APPS_DIST_DIR));
*/
}

gulp.task('jest', function () {
	return gulp.src('./js').pipe(jest({
		scriptPreprocessor: "../specs/preprocessor.js",
		unmockedModulePathPatterns: [
			"node_modules/react",
			"node_modules/react-bootstrap",
			"node_modules/underscore",
			"node_modules/rest",
			"node_modules/keymirror"
		],
		setupEnvScriptFile: "../specs/setupTestEnv.js",
		testDirectoryName: "__tests__",
		moduleFileExtensions: [
			"js",
			"react"
		]
	}));
});
