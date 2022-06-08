{ // avoid variables ending up in the global scope

	// page components
	let conferencesList, conferencesList2, usersList, wizard, wizardUsers,
		pageOrchestrator = new PageOrchestrator(); // main controller
	let usersToShow, numGuests, attempt;

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
			let self = this;
			makeCall("GET", "getConferences", null,
				function(req) {
					if (req.readyState === 4) {
						let message = req.responseText;
						if (req.status === 200) {
							let conferencesToShow = JSON.parse(req.responseText);
							if (conferencesToShow.length === 0) {
								self.alert.textContent = "No conferences yet!";
								return;
							}
							self.update(conferencesToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status === 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = "ERROR: " + message;
						}}
				}
			);
		};


		this.update = function(arrayConferences) {
			let row, cell;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			let self = this;
			arrayConferences.forEach(function(conference) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.textContent = conference.title;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.date;
				row.appendChild(cell);
				cell = document.createElement("td");
				const [time, modifier] = conference.duration.split(' ');
				let [hours, minutes] = time.split(':');
				if (hours === '12') {hours = '00';}
				if (modifier === 'PM') {hours = parseInt(hours, 10) + 12;}
				cell.textContent = hours + ":" + minutes;
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
			let self = this;
			makeCall("GET", "getConferences2", null,
				function(req) {
					if (req.readyState === 4) {
						let message = req.responseText;
						if (req.status === 200) {
							let conferencesToShow = JSON.parse(req.responseText);
							if (conferencesToShow.length === 0) {
								self.alert.textContent = "No conferences yet!";
								return;
							}
							self.update(conferencesToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status === 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = "ERROR: " + message;
						}}
				}
			);
		};


		this.update = function(arrayConferences) {
			let row, cell;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			let self = this;
			arrayConferences.forEach(function(conference) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.textContent = conference.title;
				row.appendChild(cell);
				cell = document.createElement("td");
				cell.textContent = conference.date;
				row.appendChild(cell);
				cell = document.createElement("td");
				const [time, modifier] = conference.duration.split(' ');
				let [hours, minutes] = time.split(':');
				if (hours === '12') {hours = '00';}
				if (modifier === 'PM') {hours = parseInt(hours, 10) + 12;}
				cell.textContent = hours + ":" + minutes;
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
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			let self = this;
			self.update(usersToShow); // self visible by closure
			if (next) next(); // show the default element of the list if present
		};


		this.update = function(arrayUsers) {
			let row, cell, checkbox;
			this.listcontainerbody.innerHTML = "";
			let self = this;
			arrayUsers.forEach(function(user) { // self visible here, not this
				row = document.createElement("tr");
				cell = document.createElement("td");
				checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				checkbox.name = "userscheckbox";
				checkbox.value = user.id;
				checkbox.checked = user.checked;
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
		this.wizard = wizardId;
		this.alert = alert;

		this.registerEvents = function(orchestrator) {

			// Manage submit button
			this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				let form = e.target.closest("form"),
					valid = true;

				for (let i = 0; i < form.elements.length; i++) {
					if (!form.elements[i].checkValidity()) {
						form.elements[i].reportValidity();
						valid = false;
						break;
					}
				}

				numGuests = form.elements[4].value;
				attempt = 0;

				if (valid) {
					let self = this;
					makeCall("POST", 'CreateConference', e.target.closest("form"),
						function(req) {
							if (req.readyState === XMLHttpRequest.DONE) {
								let message = req.responseText;
								if (req.status === 200) {
									usersToShow = JSON.parse(req.responseText);
									if (usersToShow.length === 0) {
										self.alert.textContent = "No users yet!";
									}
									orchestrator.refresh("modalWindow");
								} else if (req.status === 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								}
								else {
									self.alert.textContent = "ERROR: " + message;
									self.reset();
								}
							}
						}
					);
				}
			});
		};
	}

	function WizardUsers(wizardId, alert, alert_users, alert_attempts) {

		this.wizard = wizardId;
		this.alert = alert;
		this.alert_users = alert_users;
		this.alert_attempts = alert_attempts;


		this.registerEvents = function(orchestrator) {

			// Manage submit button
			this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				let newusers = e.target.closest("form"),
					valid = 0;
				for (let i = 0; i < newusers.elements.length; i++) {
					if (newusers.elements[i].checked) {
						valid++;
					}
				}

				if (valid > 0 && valid<numGuests){
					let self = this;
					makeCall("POST", 'CheckBoxUsers', e.target.closest("form"),
						function(req) {
							if (req.readyState === XMLHttpRequest.DONE) {
								let message = req.responseText;
								if (req.status === 200) { //created
									self.alert.textContent = "Conference created!";
									orchestrator.refresh();
								} else if (req.status === 403) {
									window.location.href = req.getResponseHeader("Location");
									window.sessionStorage.removeItem('username');
								} else {  //error
									self.alert.textContent = "ERROR: " + message;
									orchestrator.refresh();
								}
							}
						}
					);
				} else {
					if (valid === 0){
						this.alert_users.textContent = "Select at least one partecipant";
					} else if(attempt<2){
						usersToShow = newusers;
						attempt++;
						this.alert_users.textContent = "Too many partecitants, delete at least " + (valid-numGuests);
						this.alert_attempts.textContent = "You still have " + (3-attempt) + " attempts";
					} else {
						this.alert.textContent = "Three attempts to define a conference with too many participants, the conference will not be created";
						attempt = 0;
						orchestrator.refresh();
					}
				}
			});

			// Manage cancel button
			this.wizard.querySelector("input[type='button'].cancel").addEventListener('click', () => {
				this.alert_users.textContent = "";
				this.alert_attempts.textContent = "";
				orchestrator.refresh();
			});

			// Manage clear button
			this.wizard.querySelector("input[type='button'].clear").addEventListener('click', (e) => {
				let eventform = e.target.closest("form");
				for (let i = 0; i < eventform.elements.length; i++) {
					eventform.elements[i].checked = false;
				}
				this.alert_users.textContent = "";
			});
		};
	}

	function PageOrchestrator() {
		let alertContainer = document.getElementById("id_alert");
		let alertContainer_users = document.getElementById("id_alert_users");
		let alertContainer_attempts = document.getElementById("id_alert_attempts");

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

			wizardUsers = new WizardUsers(document.getElementById("id_usersform"), alertContainer, alertContainer_users, alertContainer_attempts);
			wizardUsers.registerEvents(this);


			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		};

		this.refresh = function(message) { // currentConference initially null at start

			if(message === "modalWindow"){
				alertContainer.textContent = "";
				document.getElementById("id_createconferenceform").style.pointerEvents = "none";
				document.getElementById("modalbackground").style.visibility = "visible";
				usersList.reset();
				usersList.show();
			} else {
				document.getElementById("id_createconferenceform").style.pointerEvents = "auto";
				document.getElementById("modalbackground").style.visibility = "hidden";
				conferencesList.reset();
				conferencesList2.reset();
				usersList.reset();
				conferencesList.show(); // closure preserves visibility of this
				conferencesList2.show();
			}
		};
	}
}
