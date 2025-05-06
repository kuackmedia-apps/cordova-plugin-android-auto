var exec = require('cordova/exec');

exports.startService = function(success, error) {
  exec(success, error, 'AndroidAutoPlugin', 'startService', []);
};
