document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("login-form");

    axios.get('/csrf-token')
        .then(function (response) {
            const csrfToken = response.data.csrfToken;

            const csrfInput = document.createElement("input");
            csrfInput.setAttribute("type", "hidden");
            csrfInput.setAttribute("name", "csrfToken");
            csrfInput.setAttribute("value", csrfToken);

            loginForm.appendChild(csrfInput);
        })
        .catch(function (error) {
            console.error("Error fetching CSRF token:", error);
        });

    if (loginForm) {
        loginForm.onsubmit = function (event) {
            event.preventDefault();

            const username = document.getElementById("username").value;

            sessionStorage.setItem("chatBotUsername", username);
            sessionStorage.setItem("chatBotLoggedIn", "true");

            this.submit();
        };
    } else {
        console.error("Login form not found!");
    }
});
