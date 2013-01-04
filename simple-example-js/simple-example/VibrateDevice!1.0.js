/*
 * JobServiceID:
 * urn:moat:{:your_application_id}:example:VibrateDevice:1.0
 * 
 * Description: Vibrate a device
 */

var moat = require('moat');
var context = moat.init();
var session = context.session;

session.log('vibrate', 'Start VibrateDevice!');

// Get dmJob arguments.
var args = context.clientRequest.dmjob.arguments;
session.log('vibrate', 'args => ' + args);

// Whether or not the args have options.
var pattern = null;
if (args && args.options) {
	// "options" : [int,int,...] 
	pattern = args.options;
}

// Run VibrationDevice#vibrate() method on the remote device!
var mapper = session.newModelMapperStub('VibrationDevice');
var device = mapper.newModelStub();
device.vibrate(session, pattern, {
	success: function(obj) {
		session.log('vibrate:vibrate', 'Successful!!');
	},
	error: function(type, code) {
		session.log('vibrate:vibrate:error', 'Type:' + type
			+ ', Unexpected status => ' + code);
		session.notifyAsync({
			result: 'error',
			code: code,
			type: type
		});
	}
});

session.log('vibrate', 'End VibrateDevice!');
