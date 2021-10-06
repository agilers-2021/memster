function editInit() {
    let token = localStorage.getItem("token");
    let $form = document.getElementById("edit_form");

    $form.addEventListener("submit", function (e) {
        e.preventDefault();

        const reader = new FileReader();

        let username = document.getElementById("username").value;
        let image = document.getElementById("img").files[0];
        let form = e.target;

        if (image)
            reader.readAsDataURL(image)
        let f = function () {
            fetch(form.action, {
                method: form.method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    display_name: username ? username : undefined,
                    set_photo: reader.result ? reader.result.split(',')[1] : undefined
                }),
            }).then((_) => {
                window.open("/user_info", "_self")
            }).catch((error) => {
                $isError.checked = true;
                $errorText.innerText = error.message;
            });
        }
        if (image === undefined) {
            f()
        } else {
            reader.onloadend = f
        }
    })

}

function loginInit() {
    let token = localStorage.getItem("token");
    if (token !== null) {
        window.location.replace("/user_info");
    }

    let $createAccount = document.getElementById("create_account_button");
    let $form = document.getElementById("login_form");
    let $isError = document.getElementById("is_error");
    let $errorText = document.getElementById("error_text");

    $createAccount.addEventListener("click", function () {
        window.location.href = "/register";
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
            .then((response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    response.text()
                        .then((text) => {
                            document.getElementById("password").value = "";
                            $isError.checked = true;
                            if (text !== "") {
                                $errorText.innerText = text;
                            } else {
                                $errorText.innerText = `Error ${response.status} (${response.statusText})`;
                            }
                        });
                }
            })
            .then((data) => {
                localStorage.setItem("token", data["token"])
                window.location.replace("/user_info");
            })
            .catch((error) => {
                $isError.checked = true;
                $errorText.innerText = error.message;
            })
    })
}

function registerInit() {
    let $form = document.getElementById("create_user_form");
    let $isError = document.getElementById("is_error");
    let $errorText = document.getElementById("error_text");

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
            .then((response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    response.text()
                        .then((text) => {
                            $isError.checked = true;
                            if (text !== "") {
                                $errorText.innerText = text;
                            } else {
                                $errorText.innerText = `Error ${response.status} (${response.statusText})`;
                            }
                        });
                }
            })
            .then((data) => {
                localStorage.setItem("token", data["token"])
                window.location.replace("/user_info");
            })
            .catch((error) => {
                $isError.checked = true;
                $errorText.innerText = error.message;
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

    let $edit = document.getElementById("edit_button");
    $edit.addEventListener("click", function () {
        window.open("/edit", "_self");
    })
}
