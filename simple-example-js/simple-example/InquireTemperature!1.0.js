/*
 * JobServiceID:
 * urn:moat:{:your_application_id}:example:InquireTemperature:1.0
 * 
 * Description: Show text on the LCD
 */

var moat = require('moat');
var context = moat.init();
var session = context.session;

session.log('inquireTemperature', 'Start InquireTemperature!');

// Get dmJob arguments.
var args = context.clientRequest.dmjob.arguments;
session.log('inquireTemperature', 'args => ' + args);

// Run ZigBeeDevice#inquireTemperature() method on the remote device!
var mapper = session.newModelMapperStub('ZigBeeDevice');
var device = mapper.newModelStub(args.uid);
device.inquireTemperature(session, null, {
	success: function(obj) {
		session.log('inquireTemperature', 'Successful!!');
	},
	error: function(type, code) {
		session.log('inquireTemperature:error', 'Type:' + type
			+ ', Unexpected status => ' + code);
		session.notifyAsync({
			result: 'error',
			code: code,
			type: type
		});
	}
});

session.log('inquireTemperature', 'End InquireTemperature!');
