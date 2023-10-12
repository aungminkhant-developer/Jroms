
			document.addEventListener("DOMContentLoaded", function (event) {
				const showNavbar = (toggleId, navId, bodyId, headerId) => {
					const toggle = document.getElementById(toggleId),
						nav = document.getElementById(navId),
						bodypd = document.getElementById(bodyId),
						headerpd = document.getElementById(headerId);

					if (toggle && nav && bodypd && headerpd) {
						toggle.addEventListener("click", () => {
							const isNavVisible = nav.classList.contains("show");

							// Toggle sidebar visibility
							nav.classList.toggle("show");
							// Toggle icon
							toggle.classList.toggle("bx-x");
							// Add/remove padding to body and header based on sidebar visibility
							bodypd.classList.toggle("body-pd");
							headerpd.classList.toggle("body-pd");

							// Remove active class from all links
							const linkColor = document.querySelectorAll(".nav_link");
							linkColor.forEach((l) => l.classList.remove("active"));

							// Check if any dropdowns are open and close them
							const openDropdowns = document.querySelectorAll(".collapse.show");
							openDropdowns.forEach((dropdown) =>
								dropdown.classList.remove("show")
							);

							// Toggle up/down caret icons of the dropdown links
							const dropdownLinks = document.querySelectorAll(
								"[data-bs-toggle='collapse']"
							);
							dropdownLinks.forEach((link) => toggleChevronIcon(link));

							// If the sidebar is closed and there were open dropdowns, reset the caret icons
							if (!isNavVisible && openDropdowns.length > 0) {
								const dropdownToggleIcons = document.querySelectorAll(
									".fa-solid.fa-caret-up"
								);
								dropdownToggleIcons.forEach((icon) =>
									icon.classList.replace("fa-caret-up", "fa-caret-down")
								);
							}
						});
					}
				};

				showNavbar("header-toggle", "nav-bar", "body-pd", "header");

				/*===== LINK ACTIVE =====*/
				const linkColor = document.querySelectorAll(".nav_link");

				function colorLink() {
					if (!this.classList.contains("active")) {
						// Remove active class from all links
						linkColor.forEach((l) => l.classList.remove("active"));
						// Add active class to the clicked link
						this.classList.add("active");

						// If the sidebar is closed, open it
						const nav = document.getElementById("nav-bar");
						if (!nav.classList.contains("show")) {
							nav.classList.add("show");
							const toggle = document.getElementById("header-toggle");
							toggle.classList.add("bx-x");
							const bodypd = document.getElementById("body-pd");
							const headerpd = document.getElementById("header");
							bodypd.classList.add("body-pd");
							headerpd.classList.add("body-pd");
						}
					}
				}

				linkColor.forEach((l) => l.addEventListener("click", colorLink));

				// Your code to run since DOM is loaded and ready
			});

			//up-down icon toggler
			function toggleChevronIcon(link) {
				const chevronIcon = link.querySelector(".fa-solid.fa-caret-up");

				if (chevronIcon) {
					chevronIcon.classList.toggle("fa-caret-down");
					chevronIcon.classList.toggle("fa-caret-up");
				} else {
					const chevronDownIcon = link.querySelector(".fa-solid.fa-caret-down");
					chevronDownIcon.classList.toggle("fa-caret-up");
					chevronDownIcon.classList.toggle("fa-caret-down");
				}
			}