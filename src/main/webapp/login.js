{ // avoid variables ending up in the global scope

    // page components
    let loginwizard, registerwizard,
        pageOrchestrator = new PageOrchestrator(); // main controller

    window.addEventListener("load", () => {
        pageOrchestrator.start(); // initialize the components
        pageOrchestrator.refresh();
    }, false);

    function LoginWizard(wizardId, errormessage, errormessage2) {
        this.wizard = wizardId;
        this.errormessage = errormessage;
        this.errormessage2 = errormessage2;

        this.registerEvents = function() {

            // Manage login button
            this.wizard.querySelector("input[type='button'].login").addEventListener('click', (e) => {
                this.errormessage2.textContent = "";

                let form = e.target.closest("form");

                if (form.checkValidity()) {
                    let self = this;
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
                                        self.errormessage.textContent = message;
                                        break;
                                    case 401: // unauthorized
                                        self.errormessage.textContent = message;
                                        break;
                                    case 500: // server error
                                        self.errormessage.textContent = message;
                                        break;
                                    default :
                                        self.errormessage.textContent = "Unknown error";
                                        break;
                                }
                            }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        };
    }

    function RegisterWizard(wizardId, errormessage, errormessage2) {
        this.wizard = wizardId;
        this.errormessage = errormessage;
        this.errormessage2 = errormessage2;

        this.registerEvents = function() {

            // Manage register button
            this.wizard.querySelector("input[type='button'].register").addEventListener('click', (e) => {
                this.errormessage.textContent = "";

                let form = e.target.closest("form");

                let name = document.getElementById("name").value,
                    surname = document.getElementById("surname").value,
                    email = document.getElementById("email").value,
                    username = document.getElementById("username").value,
                    password = document.getElementById("password").value,
                    password2 = document.getElementById("password2").value;

                let mailformat = new RegExp('[a-z0-9]+@[a-z]+\\.[a-z]{2,3}');

                if(name == null || surname == null || email == null || username == null || password == null || password2 == null || name === "" || surname === "" || email === "" || username === "" || password === "" || password2 === ""){
                    this.errormessage2.textContent = "Missing or empty credential value";
                } else if(!email.match(mailformat)){
                    this.errormessage2.textContent = "Email not valid";
                } else if(password !== password2){
                    this.errormessage2.textContent = "Passwords are different";
                } else if (form.checkValidity()) {
                    let self = this
                    makeCall("POST", 'RegisterServlet', e.target.closest("form"),
                        function(x) {
                            if (x.readyState === XMLHttpRequest.DONE) {
                                if(x.status === 200){
                                    self.errormessage2.textContent = "Account created correctly!";
                                } else {
                                    self.errormessage2.textContent = x.responseText;
                                }
                            }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        };
    }

    function PageOrchestrator() {
        let errormessage = document.getElementById("errormessage");
        let errormessage2 = document.getElementById("errormessage2");

        this.start = function() {

            loginwizard = new LoginWizard(document.getElementById("loginbutton"), errormessage, errormessage2);
            loginwizard.registerEvents(this);  // the orchestrator passes itself --this-- so that the wizard can call its refresh function after creating a conference

            registerwizard = new RegisterWizard(document.getElementById("registerbutton"), errormessage, errormessage2);
            registerwizard.registerEvents(this);  // the orchestrator passes itself --this-- so that the wizard can call its refresh function after creating a conference
        };
    }
}
