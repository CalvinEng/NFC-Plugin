var nfc = {
    createEvent: function (password, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Nfc', // mapped to our native Java class called "Nfc"
            'init', // with this action name
            [password]
        );
    }
}

module.exports = calendar;