/*global cordova, module*/

module.exports = {
    greet: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "greet", [name]);
    },
    init: function (successCallback, erroCallback) {
        cordova.exec(successCallback, errorCallback, "Nfc", "init", []);
    }
};