var typeName = document.getElementById("typeName");
var nameError = document.getElementById("name-error");
var objectError = document.getElementById("objectError");
var questionBox = document.getElementById("questionBox");
var typeId = document.getElementById("typeId");
var addBtn = document.getElementById("add-btn");
var emailCreateBtn = document.getElementById("emailCreateBtn");
var addEmailForm = document.getElementById("addEmailForm");
var emailTemplateForm = document.getElementById("emailTemplateForm");
var editAddBtn = document.getElementById("edit-add-btn");
var titleEmailType = document.getElementById("titleEmailType");
var editId = document.getElementById("editId");
var emailTemplateId = document.getElementById("emailTemplateId");
var subject = document.getElementById("subject");
var typeSelect = document.getElementById("typeSelect");
var subjectError = document.getElementById("subjectError");
var messageError = document.getElementById("messageError");
var templateObjectError = document.getElementById("templateObjectError");
var templateSubjectError = document.getElementById("templateSubjectError");
var alertBoxQuestion = document.getElementById("alertBoxQuestion");
var myModal = new bootstrap.Modal(
  document.getElementById("add-email-type-modal")
);
var alertModal = new bootstrap.Modal(
  document.getElementById("alert-box-modal")
);
var emailRole = document.getElementById("emailRole");

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

if (objectError.innerHTML !== "") {
  myModal.show();
  setTimeout(function () {
    objectError.innerHTML = "";
  }, 4000);
}

if (nameError != null) {
  if (nameError.innerHTML != null || nameError.innerHTML != "") {
    myModal.show();
    setTimeout(function () {
      nameError.innerHTML = "";
    }, 4000);
  }
}

//function for prevent space input
function preventSpace(event) {
  const inputElement = event.target;
  const value = inputElement.value;
  const firstChar = value.charAt(0);

  if (event.keyCode === 32 && firstChar === "") {
    event.preventDefault();
  }
}

//function for remove space
function removeLeadingSpace(event) {
  const inputElement = event.target;
  const value = inputElement.value;
  const newValue = value.replace(/^\s+/, ""); // Remove leading spaces
  inputElement.value = newValue;
}

if (templateObjectError.innerHTML !== "") {
  setTimeout(() => {
    templateObjectError.innerHTML = "";
  }, 4000);
}
if (templateSubjectError != null) {
  if (
    templateSubjectError.innerHTML != null ||
    templateObjectError.innerHTML == ""
  ) {
    setTimeout(() => {
      templateSubjectError.innerHTML = "";
    }, 4000);
  }
}

function validateEmailName() {
  if (typeName.value === null || typeName.value === "") {
    setError(typeName, "Email type name is required");
    return false;
  }
  if (typeName.value.length > 45) {
    setError(typeName, "Email type name must be within 45 characters");
    return false;
  } else setSuccess(typeName);
  return true;
}

function addEmailType() {
  if ($("#emailRole").val() === "select") {
    $('.error').text('Choose Email Type.')
    setTimeout(function () {
        $('.error').text('')
    }, 4000);
    return false;
  }
  if (!validateEmailName()) {
    return false;
  }
  return true;
}

//to validate subject for email template
function validateSubject() {
  if (typeSelect.value === "Choose") {
    alertBoxQuestion.innerHTML = "Please Choose Email Type";
    alertModal.show();
    return false;
  }

  if (subject.value == null || subject.value.trim() == "") {
    subjectError.innerHTML = "Subject is required !!";
    setTimeout(function () {
      subjectError.innerHTML = "";
    }, 4000);
    return false;
  }
  if (subject.value.length > 200) {
    subjectError.innerHTML = "Subject Name must have within 200 characters !!";
    setTimeout(function () {
      subjectError.innerHTML = "";
    }, 4000);
    return false;
  }
  return true;
}

function createEmailTemplate() {
  var editor = tinymce.get("email");
  var parser = new DOMParser();
  var htmlDoc = parser.parseFromString(editor.getContent(), "text/html");
  var textContent = htmlDoc.body.textContent;
  if (!validateSubject()) {
    return false;
  }
  if (editor.getContent() === null || textContent.trim() === "") {
    messageError.innerHTML = "Email message is required !!";
    setTimeout(() => {
      messageError.innerHTML = "";
    }, 4000);
    return false;
  }
  if (typeSelect.value === "Choose") {
    alertBoxQuestion.innerHTML = "Please Choose Email Type";
    alertModal.show();
    return false;
  }
  return true;
}

const setError = (element, message) => {
  const inputControl = element.parentElement;
  const errorDisplay = inputControl.querySelector(".error");

  errorDisplay.innerText = message;
  inputControl.classList.add("error");
  setTimeout(function () {
    errorDisplay.innerText = "";
    inputControl.classList.remove("error");
  }, 4000);
  inputControl.classList.remove("success");
};

const setSuccess = (element) => {
  const inputControl = element.parentElement;
  const errorDisplay = inputControl.querySelector(".error");

  errorDisplay.innerText = "";
  inputControl.classList.add("success");
  inputControl.classList.remove("error");
};

//to delete email type
document.querySelectorAll(".type-delete-btn").forEach((el) => {
  el.onclick = (e) => {
    questionBox.innerText =
      "Are you sure to delete  '" + e.target.dataset.name + "'  ?";
    typeId.value = e.target.dataset.id;
  };
});

document.getElementById("add-btn").addEventListener("click", function (e) {
  e.preventDefault();
  addEmailForm.removeAttribute("action");
  addEmailForm.setAttribute("action", "/mng/mail/addEmailType");
  editAddBtn.innerHTML = "Add";
  typeName.value = null;
  titleEmailType.innerHTML = "Add Email Type";
});

document.querySelectorAll(".edit-btn").forEach((el) => {
  el.onclick = (e) => {
    var role=e.target.dataset.role;
    if(role === 'INTERVIEW_INVITATION'){
        $('#emailRole option:eq(1)').prop('selected',true);
    }if(role === 'JOB_OFFER'){
        $('#emailRole option:eq(2)').prop('selected',true);
    }
    typeName.value = e.target.dataset.name;
    editId.value = e.target.dataset.id;
    addEmailForm.removeAttribute("action");
    addEmailForm.setAttribute("action", "/mng/mail/editEmailType");
    editAddBtn.innerHTML = "Update";
    titleEmailType.innerHTML = "Update Email Type";
  };
});

document.getElementById("typeSelect").addEventListener("change", function () {
  var typeId = typeSelect.value;
  var editor = tinymce.get("email");
  if (typeId !== "Choose") {
    fetch("/mng/mail/getEmailType", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        [header]: token,
        "X-XSRF-Token": token,
      },
      body: JSON.stringify(typeId),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Response is not ok");
        }
        return response.json();
      })
      .then((data) => {
        if (data.subject == null) {
          emailCreateBtn.innerHTML = "Create";
          subject.value = null;
          emailTemplateForm.removeAttribute("action");
          emailTemplateForm.setAttribute(
            "action",
            "/mng/mail/addEmailTemplate"
          );
          emailTemplateId.value = 0;
          editor.setContent("");
        } else {
          console.log("t id:" + data.id);
          emailCreateBtn.innerHTML = "Update";
          subject.value = data.subject;
          emailTemplateId.value = data.id;
          emailTemplateForm.removeAttribute("action");
          emailTemplateForm.setAttribute(
            "action",
            "/mng/mail/editEmailTemplate"
          );
          editor.setContent(data.bodyText);
        }
      })
      .catch((error) => {
        console.error("Error :" + error);
      });
  }
}); //end tag of email type select

if (emailCreateBtn.innerHTML === "Create") {
  emailTemplateForm.removeAttribute("action");
  emailTemplateForm.setAttribute("action", "/mng/mail/addEmailTemplate");
}
if (emailCreateBtn.innerHTML === "Update") {
  emailTemplateForm.removeAttribute("action");
  emailTemplateForm.setAttribute("action", "/mng/mail/editEmailTemplate");
}
