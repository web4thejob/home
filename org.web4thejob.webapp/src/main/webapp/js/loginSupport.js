function handleLogin() {
    var param = window.location.search.substring(1);
    if (param.slice(0,1) == "f" ) {
        showErrorMessage();
    } else if (param.slice(0,1) == "t" ) {
        showTimeoutMessage();
    }

    document.auth.j_username.focus();
}

function showErrorMessage() {
    alert("Login failed");
}

function showTimeoutMessage() {
    alert("Your session has expired. Please login to continue.");
}

function createCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    } else var expires = "";
    document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name, "", -1);
}