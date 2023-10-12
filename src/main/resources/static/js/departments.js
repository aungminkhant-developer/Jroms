const domElements = {
  addDepartmentModal: document.getElementById("add-department-modal"),
  addDepartmentForm: document.getElementById("add-department-form"),
  editDepartmentModal: document.getElementById("edit-department-modal"),
  editDepartmentForm: document.getElementById("edit-department-form"),
  deleteDepartmentInput: document.getElementById("delete-department"),
  deleteDepartmentForm: document.getElementById("delete-department-form"),
};

// Clear all error messages
function clearErrors() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });
}

domElements.addDepartmentModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addDepartmentForm.reset();
});

domElements.editDepartmentModal.addEventListener("hidden.bs.modal", (e) => {
  const el = document.getElementById("active-department-head");
  if(el != null) {
    el.remove();
  }
});

function addDepartment() {
  clearErrors();

  const departmentIdElement = document.querySelector("#add-department-form input[name='id']");
  const departmentNameElement = document.querySelector("#add-department-form input[name='name']");
  
  const isValid = validateDepartment(departmentIdElement, departmentNameElement);

  if (isValid) {
    domElements.addDepartmentForm.submit();
  }
}

function updateDepartment() {
  const departmentIdElement = document.querySelector("#edit-department-form input[name='id']");
  const departmentNameElement = document.querySelector("#edit-department-form input[name='name']");
  
  const isValid = validateDepartment(departmentIdElement, departmentNameElement);

  if(isValid) {
    domElements.editDepartmentForm.submit();
  }
  
}

// Validate the input fields: id and name
function validateDepartment(departmentIdElement, departmentNameElement) {
  departmentIdElement.value = departmentIdElement.value.trim();
  const departmentId = departmentIdElement.value;
  departmentNameElement.value = departmentNameElement.value.trim();
  const departmentName = departmentNameElement.value;

  var isValid = true;

  if (departmentId == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "id-error");
    pNode.innerText = "Department id cannot be empty";
    departmentIdElement.after(pNode);
  } else if (departmentId.length > 8) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "id-error");
    pNode.innerText = "Department id cannot exceed 8 characters";
    departmentIdElement.after(pNode);
  }

  if (departmentName == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Department name cannot be empty";
    departmentNameElement.after(pNode);
  } else if (departmentName.length > 100) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Department name cannot exceed 100 characters";
    departmentNameElement.after(pNode);
  }

  return isValid;
}

// set the id of job Level to be deleted
document.querySelectorAll(".delete-btn").forEach((el) => {
  el.onclick = (e) => {
    domElements.deleteDepartmentInput.value = e.target.dataset.id;
  };
});

function deleteDepartment() {
  domElements.deleteDepartmentForm.submit();
}

$(document).ready(function () {
  var errors = $(".error");
  if (errors.length > 0) {
    if (window.location.toString().includes("add")) {
      $("#add-department-modal").modal("show");
    } else {
      $('#edit-department-modal').find('#department-head').prepend(
        `<option id="active-department-head" value="${departmentHead.id}">${departmentHead.name}</option>`
      );
      $("#edit-department-modal").modal("show");
    }
  }
});

// Add Department
document.querySelector("#add-department-form input[name='id']").onkeydown = (e) => {
  const errElement = document.querySelector("#add-department-form #id-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#add-department-form input[name='name']").onkeydown = (e) => {
  const errElement = document.querySelector("#add-department-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

// Edit Department
document.querySelector("#edit-department-form input[name='id']").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-department-form #id-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#edit-department-form input[name='name']").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-department-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}