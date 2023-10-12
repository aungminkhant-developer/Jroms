// DOM Elements
const domElements = {
  addJobTitleModal: document.getElementById("add-job-title-modal"),
  addJobTitleForm: document.getElementById("add-job-title-form"),
  editJobTitleModal: document.getElementById("edit-job-title-modal"),
  editJobTitleForm: document.getElementById("edit-job-title-form"),
  deleteJobTitleInput: document.getElementById("delete-job-title"),
  deleteJobTitleForm: document.getElementById("delete-job-title-form"),

  addJobLevelModal: document.getElementById("add-job-level-modal"),
  addJobLevelForm: document.getElementById("add-job-level-form"),
  editJobLevelModal: document.getElementById("edit-job-level-modal"),
  editJobLevelForm: document.getElementById("edit-job-level-form"),
  deleteJobLevelInput: document.getElementById("delete-job-level"),
  deleteJobLevelForm: document.getElementById("delete-job-level-form"),

  addJobTypeModal: document.getElementById("add-job-type-modal"),
  addJobTypeForm: document.getElementById("add-job-type-form"),
  editJobTypeModal: document.getElementById("edit-job-type-modal"),
  editJobTypeForm: document.getElementById("edit-job-type-form"),
  deleteJobTypeInput: document.getElementById("delete-job-type"),
  deleteJobTypeForm: document.getElementById("delete-job-type-form"),

  addInterviewStageModal: document.getElementById("add-interview-stage-modal"),
  addInterviewStageForm: document.getElementById("add-interview-stage-form"),
  editInterviewStageModal: document.getElementById("edit-interview-stage-modal"),
  editInterviewStageForm: document.getElementById("edit-interview-stage-form"),
  deleteInterviewStageInput: document.getElementById("delete-interview-stage"),
  deleteInterviewStageForm: document.getElementById("delete-interview-stage-form")
};

domElements.addJobTitleModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addJobTitleForm.reset();
});

function addJobTitle() {
  clearErrors();

  const jobTitleElement = document.getElementById("job-title");
  jobTitleElement.value = jobTitleElement.value.trim();
  const jobTitle = jobTitleElement.value;

  var isValid = true;

  if(jobTitle == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job title cannot be empty";
    jobTitleElement.after(pNode);
  } else if(jobTitle.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job title cannot exceed 45 characters";
    jobTitleElement.after(pNode);
  }

  if(isValid) {
    domElements.addJobTitleForm.submit();
  }
}

function updateJobTitle() {
  clearErrors();

  const jobTitleElement = document.querySelector("#edit-job-title-form #job-title")
  jobTitleElement.value = jobTitleElement.value.trim();
  const jobTitle = jobTitleElement.value;

  var isValid = true;

  if(jobTitle == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job title cannot be empty";
    jobTitleElement.after(pNode);
  } else if(jobTitle.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job title cannot exceed 45 characters";
    jobTitleElement.after(pNode);
  }

  if(isValid) {
    domElements.editJobTitleForm.submit();
  }

}

// set the id of job title to be deleted
document.querySelectorAll(".jt-delete-btn").forEach(el => {
  el.onclick = (e) => {
    domElements.deleteJobTitleInput.value = e.target.dataset.id;
  }
})

function deleteJobTitle() {
  domElements.deleteJobTitleForm.submit();
}

// Manage Job Level
domElements.addJobLevelModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addJobLevelForm.reset();
});

function addJobLevel() {
  clearErrors();

  const jobLevelElement = document.getElementById("job-level");
  jobLevelElement.value = jobLevelElement.value.trim();
  const jobLevel = jobLevelElement.value;

  var isValid = true;

  if(jobLevel == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Level cannot be empty";
    jobLevelElement.after(pNode);
  } else if(jobLevel.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Level cannot exceed 45 characters";
    jobLevelElement.after(pNode);
  }

  if(isValid) {
    domElements.addJobLevelForm.submit();
  }
}

function updateJobLevel() {
  clearErrors();

  const jobLevelElement = document.querySelector("#edit-job-level-form #job-level")
  jobLevelElement.value = jobLevelElement.value.trim();
  const jobLevel = jobLevelElement.value;

  var isValid = true;

  if(jobLevel == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Level cannot be empty";
    jobLevelElement.after(pNode);
  } else if(jobLevel.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Level cannot exceed 45 characters";
    jobLevelElement.after(pNode);
  }

  if(isValid) {
    domElements.editJobLevelForm.submit();
  }

}

// set the id of job Level to be deleted
document.querySelectorAll(".jl-delete-btn").forEach(el => {
  el.onclick = (e) => {
    domElements.deleteJobLevelInput.value = e.target.dataset.id;
  }
})

function deleteJobLevel() {
  domElements.deleteJobLevelForm.submit();
}

// Manage Job Type
domElements.addJobTypeModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addJobTypeForm.reset();
});

function addJobType() {
  clearErrors();

  const jobTypeElement = document.getElementById("job-type");
  jobTypeElement.value = jobTypeElement.value.trim();
  const jobType = jobTypeElement.value;

  var isValid = true;

  if(jobType == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Type cannot be empty";
    jobTypeElement.after(pNode);
  } else if(jobType.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Type cannot exceed 45 characters";
    jobTypeElement.after(pNode);
  }

  if(isValid) {
    domElements.addJobTypeForm.submit();
  }
}

function updateJobType() {
  clearErrors();

  const jobTypeElement = document.querySelector("#edit-job-type-form #job-type")
  jobTypeElement.value = jobTypeElement.value.trim();
  const jobType = jobTypeElement.value;

  var isValid = true;

  if(jobType == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Type cannot be empty";
    jobTypeElement.after(pNode);
  } else if(jobType.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Job Type cannot exceed 45 characters";
    jobTypeElement.after(pNode);
  }

  if(isValid) {
    domElements.editJobTypeForm.submit();
  }

}

// set the id of job Level to be deleted
document.querySelectorAll(".jtype-delete-btn").forEach(el => {
  el.onclick = (e) => {
    domElements.deleteJobTypeInput.value = e.target.dataset.id;
  }
})

function deleteJobType() {
  domElements.deleteJobTypeForm.submit();
}

// Manage Interview Stage
domElements.addInterviewStageModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addInterviewStageForm.reset();
});

function addInterviewStage() {
  clearErrors();

  const interviewStageElement = document.getElementById("interview-stage");
  interviewStageElement.value = interviewStageElement.value.trim();
  const interviewStage = interviewStageElement.value;

  var isValid = true;

  if(interviewStage == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Interview stage cannot be empty";
    interviewStageElement.after(pNode);
  } else if(interviewStage.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Interview stage cannot exceed 45 characters";
    interviewStageElement.after(pNode);
  }

  if(isValid) {
    domElements.addInterviewStageForm.submit();
  }
}

function updateInterviewStage() {
  clearErrors();

  const interviewStageElement = document.querySelector("#edit-interview-stage-form #interview-stage")
  interviewStageElement.value = interviewStageElement.value.trim();
  const interviewStage = interviewStageElement.value;

  var isValid = true;

  if(interviewStage == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Interview stage cannot be empty";
    interviewStageElement.after(pNode);
  } else if(interviewStage.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Interview stage cannot exceed 45 characters";
    interviewStageElement.after(pNode);
  }

  if(isValid) {
    domElements.editInterviewStageForm.submit();
  }

}

// set the id of job Level to be deleted
document.querySelectorAll(".is-delete-btn").forEach(el => {
  el.onclick = (e) => {
    domElements.deleteInterviewStageInput.value = e.target.dataset.id;
  }
})

function deleteInterviewStage() {
  domElements.deleteInterviewStageForm.submit();
}


// reshow the modal if it has errors
$(document).ready(function () {
  var errors = $(".error");
  if (errors.length > 0) {
    const urlStr = window.location.toString();
    if (urlStr.includes("job-title")) {
      if (urlStr.includes("add")) {
        $("#add-job-title-modal").modal("show");
      } else {
        $("#edit-job-title-modal").modal("show");
      }
    } else if (urlStr.includes("job-level")) {
      if (urlStr.includes("add")) {
        $("#add-job-level-modal").modal("show");
      } else {
        $("#edit-job-level-modal").modal("show");
      }
    } else if (urlStr.includes("job-type")) {
      if (urlStr.includes("add")) {
        $("#add-job-type-modal").modal("show");
      } else {
        $("#edit-job-type-modal").modal("show");
      }
    } else if (urlStr.includes("interview-stage")) {
      if (urlStr.includes("add")) {
        $("#add-interview-stage-modal").modal("show");
      } else {
        $("#edit-interview-stage-modal").modal("show");
      }
    }
  }
});

// Clear all error messages
function clearErrors() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });
}

// Clear each error message on keypress
// Job Title
document.querySelector("#add-job-title-form #job-title").onkeydown = (e) => {
  const errElement = document.querySelector("#add-job-title-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#edit-job-title-form #job-title").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-job-title-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

// Job Level
document.querySelector("#add-job-level-form #job-level").onkeydown = (e) => {
  const errElement = document.querySelector("#add-job-level-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#edit-job-level-form #job-level").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-job-level-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

// Job Type
document.querySelector("#add-job-type-form #job-type").onkeydown = (e) => {
  const errElement = document.querySelector("#add-job-type-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#edit-job-type-form #job-type").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-job-type-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

// Interview Stage
document.querySelector("#add-interview-stage-form #interview-stage").onkeydown = (e) => {
  const errElement = document.querySelector("#add-interview-stage-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}

document.querySelector("#edit-interview-stage-form #interview-stage").onkeydown = (e) => {
  const errElement = document.querySelector("#edit-interview-stage-form #name-error");
  if(errElement != null) {
    errElement.remove();
  }
}