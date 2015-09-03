/*global cordova, module*/

module.exports = {
    init: function (user, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "init", [user]);
    },
    write: function (message, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "write", [message]);
    }
};