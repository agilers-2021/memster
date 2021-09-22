function loginInit() {
    let $createAccount = document.getElementById("create-account-button");
    let $form = document.getElementById("login-form");

    $createAccount.addEventListener("click", function () {
        window.location.replace("/create_account");
    });
    $form.addEventListener("submit", function (e) {
        // TODO api call
        e.preventDefault();
        window.location.replace("/me");
    })
}

function createUserInit() {
    let $form = document.getElementById("create-user-form");

    $form.addEventListener("submit", function (e) {
        // TODO api call
        e.preventDefault();
        window.location.replace("/me");
    })
}

function userPageInit() {
    let $logout = document.getElementById("logout-button");

    $logout.addEventListener("click", function () {
        // TODO logic
        window.location.replace("/login");
    })
}
