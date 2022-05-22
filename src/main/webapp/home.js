{ // avoid variables ending up in the global scope

	// page components
	let conferencesList, conferencesList2, usersList, wizard, wizardUsers,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	// Constructors of view components

	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = "Nice to see you again " + this.username;
		}

		this.reset = function() {
			messagecontainer.style.visibility = "hidden";
		}
	}

	function ConferencesList(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "getConferences", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var conferencesToShow = JSON.parse(req.responseText);
							if (conferencesToShow.length == 0) {
								self.alert.textContent = "No conferences yet!";
								return;
							}
							self.update(conferencesToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}}
				}
			);
		};


		this.update = function(arrayConferences) {
			var row, cell;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			arrayConferences.forEach(function(conference) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.textContent = conference.title;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.date;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.duration;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.guests;
				row.appendChild(cell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";

		}
	}

	function ConferencesList2(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "getConferences2", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var conferencesToShow = JSON.parse(req.responseText);
							if (conferencesToShow.length == 0) {
								self.alert.textContent = "No conferences yet!";
								return;
							}
							self.update(conferencesToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}}
				}
			);
		};


		this.update = function(arrayConferences) {
			var row, cell;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			arrayConferences.forEach(function(conference) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.textContent = conference.title;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.date;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.duration;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.guests;
				row.appendChild(cell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";

		}
	}

	function UserList(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "getUsers", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var usersToShow = JSON.parse(req.responseText);
							if (usersToShow.length == 0) {
								self.alert.textContent = "No conferences yet!";
								return;
							}
							self.update(usersToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}}
				}
			);
		};


		this.update = function(arrayUsers) {
			var row, cell, checkbox, submit, form;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;

			arrayUsers.forEach(function(user) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				checkbox.name = "userscheckbox";
				checkbox.value = user.id;
				if(user.checked)
					checkbox.checked = true;
				cell.appendChild(checkbox);
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = user.name;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = user.surname;
				row.appendChild(cell);
				self.listcontainerbody.appendChild(row);
			});

			this.listcontainer.style.visibility = "visible";
		}
	}


	function Wizard(wizardId, alert) {
		// minimum date the user can choose, in this case now and in the future
		var now = new Date(),
			formattedDate = now.toISOString().substring(0, 10);
		this.wizard = wizardId;
		this.alert = alert;

		this.wizard.querySelector('input[type="date"]').setAttribute("min", formattedDate);

		this.registerEvents = function(orchestrator) {

			// Manage submit button
			this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				var eventfieldset = e.target.closest("fieldset"),
					valid = true;
				for (i = 0; i < eventfieldset.elements.length; i++) {
					if (!eventfieldset.elements[i].checkValidity()) {
						eventfieldset.elements[i].reportValidity();
						valid = false;
						break;
					}
				}

				if (valid) {
					var self = this;
					makeCall("POST", 'CreateConference', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText; // error message or conference id
								if (req.status == 200) {
									orchestrator.refresh(message); // id of the new conference passed
								} else if (req.status == 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								}
								else {
									self.alert.textContent = message;
									self.reset();
								}
							}
						}
					);
				}
			});
		};

		this.reset = function() {
			var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
			fieldsets[0].hidden = false;
			fieldsets[1].hidden = true;
			fieldsets[2].hidden = true;

		}
	}

	function WizardUsers(wizardId, alert) {

		this.wizard = wizardId;
		this.alert = alert;


		this.registerEvents = function(orchestrator) {

			// Manage submit button
			this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				valid = true;

				if (valid) {
					var self = this;
					makeCall("POST", 'CheckBoxUsers', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText; // error message or conference id
								if (req.status == 200) {
									orchestrator.refresh(message); // id of the new conference passed
								} else if (req.status == 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								}
								else {
									self.alert.textContent = message;
									self.reset();
								}
							}
						}
					);
				}
			});
		};

		this.reset = function() {
			var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
			fieldsets[0].hidden = false;
			fieldsets[1].hidden = true;
			fieldsets[2].hidden = true;

		}
	}

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();


			conferencesList = new ConferencesList(
				alertContainer,
				document.getElementById("id_listcontainer"),
				document.getElementById("id_listcontainerbody"));

			conferencesList2 = new ConferencesList2(
				alertContainer,
				document.getElementById("id_listcontainer2"),
				document.getElementById("id_listcontainerbody2"));

			usersList = new UserList(
				alertContainer,
				document.getElementById("id_listcontainer3"),
				document.getElementById("id_listcontainerbody3"));


			wizard = new Wizard(document.getElementById("id_createconferenceform"), alertContainer);
			wizard.registerEvents(this);  // the orchestrator passes itself --this-- so that the wizard can call its refresh function after creating a conference

			wizardUsers = new WizardUsers(document.getElementById("id_usersform"), alertContainer);
			wizardUsers.registerEvents(this);


			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		};

		this.refresh = function(message) { // currentConference initially null at start
			alertContainer.textContent = "";// not null after creation of status change
			if(message == null){
				document.getElementById("modalbackground").style.visibility = "hidden";
				conferencesList.reset();
				conferencesList2.reset();
				conferencesList.show(); // closure preserves visibility of this
				conferencesList2.show();
			} else {
				document.getElementById("modalbackground").style.visibility = "visible";
				usersList.reset();
				usersList.show();
			}
			wizard.reset();
		};
	}
};
