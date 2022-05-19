/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("registerbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
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