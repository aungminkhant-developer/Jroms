function generateRandomPassword(length) {
							const uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
							const lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
							const numberChars = "0123456789";
							const specialChars = "!@#$%^&*()-=_+[]{}|;:,.<>?";

							const allChars =
								uppercaseChars + lowercaseChars + numberChars + specialChars;
							let password = "";

							for (let i = 0; i < length; i++) {
								const randomIndex = Math.floor(Math.random() * allChars.length);
								password += allChars.charAt(randomIndex);
							}

							return password;
						}

						// JavaScript code to handle the click event on the dice icon and generate a random password
						function generateAndShowRandomPassword() {
							const passwordInput = document.getElementById("inputPassword");
							const confirmPasswordInput =
								document.getElementById("confirmPassword");
							const generatedPassword = generateRandomPassword(12);

							passwordInput.value = generatedPassword;
							confirmPasswordInput.value = generatedPassword;

							// Copy the generated password to clipboard
							const tempInput = document.createElement("input");
							tempInput.setAttribute("type", "text");
							tempInput.style.position = "absolute";
							tempInput.style.left = "-10000px";
							tempInput.value = generatedPassword;
							document.body.appendChild(tempInput);

							// Copy the generated password from the temporary input to clipboard
							tempInput.select();
							document.execCommand("copy");

							// Remove the temporary input element
							document.body.removeChild(tempInput);
						}