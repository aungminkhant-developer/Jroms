const nameElement = document.getElementById("name");
const usernameElement = document.getElementById("username");
const emailElement = document.getElementById("email");
const passElement = document.getElementById("password");
const confirmPassElement = document.getElementById("confirmPassword");

document.getElementById("toggle-password").addEventListener("click", (e) => {
  var type =
    passElement.getAttribute("type") === "password" ? "text" : "password";
  passElement.setAttribute("type", type);

  var icon = document.getElementById("toggle-password-icon");
  icon.classList.toggle("fa-eye");
  icon.classList.toggle("fa-eye-slash");
});

document
  .getElementById("toggle-confirm-password")
  .addEventListener("click", (e) => {
    var type =
      confirmPassElement.getAttribute("type") === "password"
        ? "text"
        : "password";
    confirmPassElement.setAttribute("type", type);

    var icon = document.getElementById("toggle-confirm-password-icon");
    icon.classList.toggle("fa-eye");
    icon.classList.toggle("fa-eye-slash");
  });

nameElement.addEventListener("keypress", () => {
  const node = document.getElementById("name-error");
  if (node != null) {
    node.remove();
  }
});

usernameElement.addEventListener("keypress", () => {
  const node = document.getElementById("username-error");
  if (node != null) {
    node.remove();
  }
});

emailElement.addEventListener("keypress", () => {
  const node = document.getElementById("email-error");
  if (node != null) {
    node.remove();
  }
});

passElement.addEventListener("keypress", () => {
  const node = document.getElementById("password-error");
  if (node != null) {
    node.remove();
  }
});

confirmPassElement.addEventListener("keypress", () => {
  const node = document.getElementById("password-error");
  if (node != null) {
    node.remove();
  }
});

function submitUserForm() {
  const elements = document.getElementsByClassName("error");
  while (elements.length > 0) {
    elements[0].parentNode.removeChild(elements[0]);
  }
  const nameElement = document.getElementById("name");
  const usernameElement = document.getElementById("username");
  const emailElement = document.getElementById("email");
  const passElement = document.getElementById("password");
  var name = nameElement.value;
  var username = usernameElement.value;
  var email = emailElement.value;
  var pass = passElement.value;
  var isValid = true;

  if (name == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "name cannot be empty";
    document
      .getElementById("name-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  if (username == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "username-error");
    pNode.innerText = "username cannot be empty";
    document
      .getElementById("username-input-row")
      .insertAdjacentElement("beforeend", pNode);
  } else if (username.includes(" ")) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "username-error");
    pNode.innerText = "username cannot contain white spaces";
    document
      .getElementById("username-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  var validRegex =
    /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  if (email == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "email-error");
    pNode.innerText = "email cannot be empty";
    document
      .getElementById("email-input-row")
      .insertAdjacentElement("beforeend", pNode);
  } else if (!email.match(validRegex)) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "email-error");
    pNode.innerText = "email is invalid";
    document
      .getElementById("email-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  var confirmPass = document.getElementById("confirmPassword").value;

  if (pass == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "password-error");
    pNode.innerText = "password cannot be empty";
    document
      .getElementById("password-input-row")
      .insertAdjacentElement("beforeend", pNode);
  } else if (pass != confirmPass) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label");
    pNode.setAttribute("id", "password-error");
    pNode.innerText = "passwords do not match";
    document
      .getElementById("password-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  if (isValid) {
    document.getElementById("user-form").submit();
  }
}
