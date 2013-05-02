/*
 * JobServiceID:
 * urn:moat:{:your_application_id}:example:ShowTextOnLCD:1.0
 * 
 * Description: Show text on the LCD
 */

var moat = require('moat');
var context = moat.init();
var session = context.session;

session.log('showTextOnLcd', 'Start ShowTextOnLCD!');

// Get dmJob arguments.
var args = context.clientRequest.dmjob.arguments;
session.log('showTextOnLcd', 'args => ' + args);

// Whether or not the args have options.
var text = "";
if (args && args.text) {
	// "text" : "some-text-up-to-17-chars"
	text = args.text;
}

// Run ZigBeeDevice#showTextOnLcd() method on the remote device!
var mapper = session.newModelMapperStub('ZigBeeDevice');
var device = mapper.newModelStub(args.uid);
device.showTextOnLcd(session, text, {
	success: function(obj) {
		session.log('showTextOnLcd', 'Successful!!');
	},
	error: function(type, code) {
		session.log('showTextOnLcd:error', 'Type:' + type
			+ ', Unexpected status => ' + code);
		session.notifyAsync({
			result: 'error',
			code: code,
			type: type
		});
	}
});

session.log('showTextOnLcd', 'End ShowTextOnLCD!');
