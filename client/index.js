function editInit() {
    let token = localStorage.getItem("token");
    let $form = document.getElementById("edit_form");
    let $isError = document.getElementById("is_error");
    let $errorText = document.getElementById("error_text");

    document.getElementById("cancel_button").addEventListener("click", function (e) {
        window.history.back();
    });

    $form.addEventListener("submit", function (e) {
        e.preventDefault();

        const reader = new FileReader();

        let display_name = document.getElementById("display_name").value;
        let anecdote = document.getElementById("anecdote").value;
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
                    display_name: display_name ? display_name : undefined,
                    anecdote: anecdote ? anecdote : undefined,
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

    fetch("/api/user_info", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
    })
        .then((response) => response.json())
        .then((data) => {
            document.getElementById("display_name").value = data["display_name"];
            document.getElementById("anecdote").value = data["anecdote"];
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

function logout() {
    localStorage.removeItem("token");
    window.location.replace("/login");
}

function edit() {
    window.open("/edit", "_self");
}

function openChats() {
    window.open("/chats", "_self");
}

function openFeed() {
    window.open("/feed", "_self");
}

function openUserInfo() {
    window.open("/user_info", "_self");
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
            if (data["photo_urls"] != null && data["photo_urls"].length > 0)
                document.getElementById("profile_image").src = data["photo_urls"][0];
        })
        .catch(() => {
            localStorage.removeItem("token");
            window.location.replace("/login");
        })
}

function feedInit() {
    let token = localStorage.getItem("token");
    if (token === null) {
        window.location.replace("/login");
    }

    getNextMatch(token);
}

function getNextMatch(token) {
    fetch("/api/match", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        },
    })
        .then((response) => response.json())
        .then((data) => {
            let user = data["user"];
            document.getElementById("display_name").innerText = user["display_name"];
            document.getElementById("username").innerText = user["username"];
            document.getElementById("anecdote").innerText = user["anecdote"];
            if (user["photo_urls"] != null && user["photo_urls"].length > 0)
                document.getElementById("profile_image").src = user["photo_urls"][0];
            sessionStorage.setItem("nextMatchSign", data["sign"]);
        })
        .catch((_) => {
            openUserInfo()
        })
}

function vote(value) {
    let token = localStorage.getItem("token");
    if (token === null) {
        window.location.replace("/login");
    }

    fetch("/api/vote", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            sign: sessionStorage.getItem("nextMatchSign"),
            action: value,
        }),
    })
        .then((_) => {
            sessionStorage.removeItem("nextMatchSign")
            window.location.replace("/feed");
        })
}

function onLike() {
    vote("match");
}

function onDislike() {
    vote("ignore");
}
