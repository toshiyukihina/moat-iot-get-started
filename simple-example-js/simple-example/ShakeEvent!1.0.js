/*
 * JobServiceID:
 * urn:moat:{:your_application_id}:example:ShakeEvent:1.0
 * 
 * Description: Update data from a device to the cloud database.
 */

var moat = require('moat');
var context = moat.init();
var session = context.session;
var clientRequest = context.clientRequest;
var database = context.database;

session.log('shake', 'Start ShakeEvent!');

var objects = clientRequest.objects;
var size = objects.length;
var count = 0;
for ( var i = 0; i < size; i++) {
	var container = objects[i];
	if (container.array) {
		var array = container.array;
		for ( var j = 0; j < array.length; j++) {
			save(array[j]);
		}
		count += array.length;
	} else {
		save(container);
		count++;
	}
}

session.log('shake', '=> ' + count + ' objects are stored.');

try {
	session.notifyAsync(objects);
} catch (e) {
	session.log('shake:error', e);
}


function save(entity) {
	// Now the given model object is stored into the database.
	var result;
	if (entity.uid) {
		// try to find
		var array = database.queryByUids('ZigBeeDevice', [entity.uid]);
		if (array && array.length > 0) {
			// Copy revision
			entity.rev = array[0].rev;
			result = database.update(entity);
		} else {
			result = database.insert(entity);
		}
	} else {
		result = database.insert(entity);
	}
	// The inserted object is internally associated with the device
	// where the
	// data origins.
	session.log('shake:save', 'The object@uid:' + result.uid
			+ ' has been saved to the database.');
}