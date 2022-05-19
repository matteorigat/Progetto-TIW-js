/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      var name = document.getElementById("name").value;
      var surname = document.getElementById("surname").value;
      var email = document.getElementById("email").value;
      var username = document.getElementById("username").value;
      var password = document.getElementById("password").value;
      var password2 = document.getElementById("password2").value;

      if(name == null || surname == null || email == null || username == null || password == null || password2 == null || name == "" || surname == "" || email == "" || username == "" || password == "" || password2 == ""){
        document.getElementById("errormessage2").textContent = "2Missing or empty credential value";
        return;
      }

      if(password !== password2){
        document.getElementById("errormessage2").textContent = "not same password";
        return;
      }

      makeCall("POST", 'RegisterServlet', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200:
                document.getElementById("errormessage2").textContent = message;
                break;
              case 400: // bad request
                document.getElementById("errormessage2").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage2").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage2").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();