function Nfc() {}

Nfc.prototype.init = function (user, url, successCallback, errorCallback) {
	cordova.exec(successCallback, errorCallback, "Nfc", "init", [user, url]);
};

Nfc.prototype.write: function (message, successCallback, errorCallback) {
	cordova.exec(successCallback, errorCallback, "Nfc", "write", [message]);
};

Nfc.install = function () {
    if (!window.plugins) {
        window.plugins = {};
    }

    window.plugins.Nfc = new Nfc();
    return window.plugins.Nfc;
};

cordova.addConstructor(Nfc.install);