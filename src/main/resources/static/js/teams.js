const domElements = {
  addTeamModal: document.getElementById("add-team-modal"),
  addTeamForm: document.getElementById("add-team-form"),
  editTeamModal: document.getElementById("edit-team-modal"),
  editTeamForm: document.getElementById("edit-team-form"),
  deleteTeamInput: document.getElementById("delete-team"),
  deleteTeamForm: document.getElementById("delete-team-form"),
};

// Clear all error messages
function clearErrors() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });
}

domElements.addTeamModal.addEventListener("hidden.bs.modal", (e) => {
  clearErrors();
  domElements.addTeamForm.reset();
});

domElements.editTeamModal.addEventListener("hidden.bs.modal", (e) => {
  const el = document.getElementById("active-team-leader");
  if(el != null) {
    el.remove();
  }
});

function addTeam() {
  clearErrors();

  const teamIdElement = document.querySelector(
    "#add-team-form input[name='id']"
  );
  const teamNameElement = document.querySelector(
    "#add-team-form input[name='name']"
  );

  const isValid = validateTeam(
    teamIdElement,
    teamNameElement
  );

  if (isValid) {
    domElements.addTeamForm.submit();
  }
}

function updateTeam() {
  const teamIdElement = document.querySelector(
    "#edit-team-form input[name='id']"
  );
  const teamNameElement = document.querySelector(
    "#edit-team-form input[name='name']"
  );

  const isValid = validateTeam(
    teamIdElement,
    teamNameElement
  );

  if (isValid) {
    domElements.editTeamForm.submit();
  }
}

// Validate the input fields: id and name
function validateTeam(teamIdElement, teamNameElement) {
  teamIdElement.value = teamIdElement.value.trim();
  const teamId = teamIdElement.value;
  teamNameElement.value = teamNameElement.value.trim();
  const teamName = teamNameElement.value;

  var isValid = true;

  if (teamId == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "id-error");
    pNode.innerText = "Team id cannot be empty";
    teamIdElement.after(pNode);
  } else if (teamId.length > 8) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "id-error");
    pNode.innerText = "Team id cannot exceed 8 characters";
    teamIdElement.after(pNode);
  }

  if (teamName == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Team name cannot be empty";
    teamNameElement.after(pNode);
  } else if (teamName.length > 100) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "text-danger", "text-end");
    pNode.setAttribute("id", "name-error");
    pNode.innerText = "Team name cannot exceed 100 characters";
    teamNameElement.after(pNode);
  }

  return isValid;
}

// set the id of team to be deleted
document.querySelectorAll(".delete-btn").forEach((el) => {
  el.onclick = (e) => {
    domElements.deleteTeamInput.value = e.target.dataset.id;
  };
});

function deleteTeam() {
  domElements.deleteTeamForm.submit();
}

$(document).ready(function () {
  var errors = $(".error");
  if (errors.length > 0) {
    if (window.location.toString().includes("add")) {
      $("#add-team-modal").modal("show");
    } else {
      $('#edit-team-modal').find('#team-leader').prepend(
        `<option id="active-team-leader" value="${teamLeader.id}">${teamLeader.name}</option>`
      );
      $("#edit-team-modal").modal("show");
    }
  }
});

// Add Team
document.querySelector("#add-team-form input[name='id']").onkeydown = (
  e
) => {
  const errElement = document.querySelector("#add-team-form #id-error");
  if (errElement != null) {
    errElement.remove();
  }
};

document.querySelector("#add-team-form input[name='name']").onkeydown = (
  e
) => {
  const errElement = document.querySelector("#add-team-form #name-error");
  if (errElement != null) {
    errElement.remove();
  }
};

// Edit Team
document.querySelector("#edit-team-form input[name='id']").onkeydown = (
  e
) => {
  const errElement = document.querySelector("#edit-team-form #id-error");
  if (errElement != null) {
    errElement.remove();
  }
};

document.querySelector("#edit-team-form input[name='name']").onkeydown = (
  e
) => {
  const errElement = document.querySelector(
    "#edit-team-form #name-error"
  );
  if (errElement != null) {
    errElement.remove();
  }
};
