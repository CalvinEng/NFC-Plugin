/*global cordova, module*/

module.exports = {
    init: function (user, url, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "init", [user, url]);
    },
    write: function (message, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "write", [message]);
    }
};