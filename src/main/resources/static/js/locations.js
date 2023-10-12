var selectedId = -1;

function addLocation() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });

  const buildingElement = document.querySelector(
    "#add-new-location-modal #building"
  );
  const townshipElement = document.querySelector(
    "#add-new-location-modal #township"
  );
  const divisionElement = document.querySelector(
    "#add-new-location-modal #division"
  );

  var isValid = validateForm(buildingElement, townshipElement, divisionElement);

  if (isValid) {
    $("#add-location-form").submit();
  }
}

function updateLocation() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });

  const buildingElement = document.querySelector(
    "#edit-location-modal #building"
  );
  const townshipElement = document.querySelector(
    "#edit-location-modal #township"
  );
  const divisionElement = document.querySelector(
    "#edit-location-modal #division"
  );

  var isValid = validateForm(buildingElement, townshipElement, divisionElement);

  if (isValid) {
    $("#edit-location-form").submit();
  }
}

function validateForm(buildingElement, townshipElement, divisionElement) {
  buildingElement.value = buildingElement.value.trim();
  const building = buildingElement.value;
  townshipElement.value = townshipElement.value.trim();
  const township = townshipElement.value;
  divisionElement.value = divisionElement.value.trim();
  const division = divisionElement.value;

  var isValid = true;
  if (building == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "building-error");
    pNode.innerText = "Building name cannot be empty";
    buildingElement.parentElement.after(pNode);
  } else if (building.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "building-error");
    pNode.innerText = "Building name cannot exceed 45 characters";
    buildingElement.parentElement.after(pNode);
  }

  if (township == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "township-error");
    pNode.innerText = "Township name cannot be empty";
    townshipElement.parentElement.after(pNode);
  } else if (township.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "township-error");
    pNode.innerText = "Township name cannot exceed 45 characters";
    townshipElement.parentElement.after(pNode);
  }

  if (division == "") {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "division-error");
    pNode.innerText = "Division name cannot be empty";
    divisionElement.parentElement.after(pNode);
  } else if (division.length > 45) {
    isValid = false;
    const pNode = document.createElement("p");
    pNode.classList.add("error", "mb-3", "pe-4", "text-danger", "text-end");
    pNode.setAttribute("id", "division-error");
    pNode.innerText = "Division name cannot exceed 45 characters";
    divisionElement.parentElement.after(pNode);
  }

  return isValid;
}

function deleteLocation() {
  const deleteLocationForm = document.getElementById("delete-location-form");
  deleteLocationForm.submit();
}



$(document).ready(function () {
  var errors = $(".error");
  if (errors.length > 0) {
    if (window.location.toString().includes("add")) {
      $("#add-new-location-modal").modal("show");
    } else {
      $("#edit-location-modal").modal("show");
    }
  }
});

// Clear the error message when user rewrite the input form
// Add Form
// const addForm = document.querySelector("#add-new-location-modal");
const buildingElement = document.querySelector(
  "#add-new-location-modal #building"
);
buildingElement.addEventListener("keypress", () => {
  const node = document.getElementById("building-error");
  if (node != null) {
    node.remove();
  }
});

const townshipElement = document.querySelector(
  "#add-new-location-modal #township"
);
townshipElement.addEventListener("keypress", () => {
  const node = document.getElementById("township-error");
  if (node != null) {
    node.remove();
  }
});

const divisionElement = document.querySelector(
  "#add-new-location-modal #division"
);
divisionElement.addEventListener("keypress", () => {
  const node = document.getElementById("division-error");
  if (node != null) {
    node.remove();
  }
});
// Edit Form
// const editForm = document.getElementById("edit-location-modal");
const editBuildingElement = document.querySelector(
  "#edit-location-modal #building"
);
editBuildingElement.addEventListener("keypress", () => {
  const node = document.getElementById("building-error");
  if (node != null) {
    node.remove();
  }
});

const editTownshipElement = document.querySelector(
  "#edit-location-modal #township"
);
editTownshipElement.addEventListener("keypress", () => {
  const node = document.getElementById("township-error");
  if (node != null) {
    node.remove();
  }
});

const editDivisionElement = document.querySelector(
  "#edit-location-modal #division"
);
editDivisionElement.addEventListener("keypress", () => {
  const node = document.getElementById("division-error");
  if (node != null) {
    node.remove();
  }
});
