/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

    document.getElementById("loginbutton").addEventListener('click', (e) => {
        let form = e.target.closest("form");
        if (form.checkValidity()) {
            makeCall("POST", 'CheckLogin', e.target.closest("form"),
                function(x) {
                    if (x.readyState === XMLHttpRequest.DONE) {
                        let message = x.responseText;
                        switch (x.status) {
                            case 200:
                                sessionStorage.setItem('username', message);
                                window.location.href = "Home.html";
                                break;
                            case 400: // bad request
                                document.getElementById("errormessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errormessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errormessage").textContent = message;
                                break;
                            default :
                                document.getElementById("errormessage").textContent = "Unknown error";
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });



    document.getElementById("registerbutton").addEventListener('click', (e) => {
        let form = e.target.closest("form");

        let message;

        let name = document.getElementById("name").value,
            surname = document.getElementById("surname").value,
            email = document.getElementById("email").value,
            username = document.getElementById("username").value,
            password = document.getElementById("password").value,
            password2 = document.getElementById("password2").value;

        let mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

        if(name == null || surname == null || email == null || username == null || password == null || password2 == null || name === "" || surname === "" || email === "" || username === "" || password === "" || password2 === ""){
            message = "Missing or empty credential value";
        } else if(email.match(mailformat)){
            message = "Email not valid";
        } else if(password !== password2){
            message = "Passwords are different";
        } else if (form.checkValidity()) {
            makeCall("POST", 'RegisterServlet', e.target.closest("form"),
                function(x) {
                    if (x.readyState === XMLHttpRequest.DONE) {
                        message = x.responseText;
                    }
                }
            );
        } else {
            form.reportValidity();
        }
        document.getElementById("errormessage2").textContent = message;
    });

})();