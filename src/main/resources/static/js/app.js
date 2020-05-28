'use strict';


// var baseUrl = require.toUrl('');
// if (baseUrl === '') {
//   baseUrl = '/';
// }


// Start the main app logic.
requirejs([ 'require', 'turbolinks', '/js/utils/bulma.toggle.menu.js'],
function   (require, turbolinks , bulmatogglemenu         ) {
	 var baseUrl = require.toUrl('');
	 if (baseUrl === '') {
	   baseUrl = '/';
	 }
});