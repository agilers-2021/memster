function loginInit() {
    let token = localStorage.getItem("token");
    if (token !== null) {
        window.location.replace("/user_info");
    }

    let $createAccount = document.getElementById("create_account_button");
    let $form = document.getElementById("login_form");

    $createAccount.addEventListener("click", function () {
        window.location.replace("/register");
    });
    $form.addEventListener("submit", function (e) {
        e.preventDefault();

        let username = document.getElementById("username").value;
        let password = document.getElementById("password").value;

        let form = e.target;
        fetch(form.action, {
            method: form.method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        })
            .then((response) => response.json())
            .then((data) => {
                localStorage.setItem("token", data["token"])
                window.location.replace("/user_info");
            })
    })
}

function registerInit() {
    let $form = document.getElementById("create_user_form");

    $form.addEventListener("submit", function (e) {
        e.preventDefault();

        let username = document.getElementById("username").value;
        let displayName = document.getElementById("display_name").value;
        let password = document.getElementById("password").value;

        let form = e.target;
        fetch(form.action, {
            method: form.method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                display_name: displayName,
                password: password,
            }),
        })
            .then((response) => response.json())
            .then((data) => {
                localStorage.setItem("token", data["token"])
                window.location.replace("/user_info");
            });
    })
}

function userInfoInit() {
    let token = localStorage.getItem("token");
    if (token === null) {
        window.location.replace("/login");
    }

    fetch("/api/user_info", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
    })
        .then((response) => response.json())
        .then((data) => {
            document.title = data["display_name"];
            document.getElementById("display_name").innerText = data["display_name"];
            document.getElementById("username").innerText = data["username"];
            document.getElementById("anecdote").innerText = data["anecdote"];
            if (data["current_photo_url"] != null)
                document.getElementById("profile_image").src = data["current_photo_url"];
        })
        .catch(() => {
            localStorage.removeItem("token");
            window.location.replace("/login");
        })

    let $logout = document.getElementById("logout_button");
    $logout.addEventListener("click", function () {
        localStorage.removeItem("token");
        window.location.replace("/login");
    })
}
