/*global cordova, module*/

module.exports = {
    init: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "init", []);
    },
    write: function (message, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "init", [message]);
    }
};