// Clear all error messages
function clearErrors() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });
}

function updateJobOffer() {
  clearErrors();

  const postsElement = document.querySelector("input[name='posts']");
  const salaryElement = document.querySelector("input[name='salary']");

  postsElement.value = postsElement.value.trim();
  const posts = postsElement.value;
  salaryElement.value = salaryElement.value.trim();
  const salary = salaryElement.value;

  var isValid = true;
  if (posts <= 0) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "posts-error");
    pNode.innerText = "Job offer should have at least 1 post";
    document
      .getElementById("post-count-input-row")
      .insertAdjacentElement("beforeend", pNode);
  } else if (posts > 127) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "posts-error");
    pNode.innerText = "Job offer cannot have more than 127 posts";
    document
      .getElementById("post-count-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  if (salary == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "salary-error");
    pNode.innerText = "Job offer salary should not be empty";
    document
      .getElementById("salary-input-row")
      .insertAdjacentElement("beforeend", pNode);
  } else if (salary.length > 30) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "salary-error");
    pNode.innerText = "Job offer salary should not exceed 30 characters";
    document
      .getElementById("salary-input-row")
      .insertAdjacentElement("beforeend", pNode);
  }

  if (isValid) {
    document.getElementById("update-job-offer-form").submit();
  }
}

// Clear error on change
document.querySelector("input[name='posts']").onkeydown = (e) => {
  const errorElement = document.getElementById("posts-error");
  console.log(errorElement);
  if (errorElement != null) {
    errorElement.remove();
  }
};

document.querySelector("input[name='salary']").onkeydown = (e) => {
  const errorElement = document.getElementById("salary-error");
  if (errorElement != null) {
    errorElement.remove();
  }
};

// Open & Close Job Offer

var prevExpireDate = null;
var prevOpenDate = null;

function extendExpireDate(noOfDays) {
  const expireDateElement = document.querySelector("input[name='expireDate']");
  const openDateElement = document.querySelector("input[name='openDate']");

  let today = new Date();
  const openYear = today.getFullYear();
  const openMonth = (today.getMonth() + 1).toString().padStart(2, '0');
  const openDay = today.getDate().toString().padStart(2, '0');

  today.setDate(today.getDate() + noOfDays);
  const expireYear = today.getFullYear();
  const expireMonth = (today.getMonth() + 1).toString().padStart(2, '0');
  const expireDay = today.getDate().toString().padStart(2, '0');

  expireDateElement.value = expireYear + "-" + expireMonth + "-" + expireDay;
  openDateElement.value = openYear + "-" + openMonth + "-" + openDay;
}

function closeJobOffer() {
  const expireDateElement = document.querySelector("input[name='expireDate']");

  let today = new Date();
  const openYear = today.getFullYear();
  const openMonth = (today.getMonth() + 1).toString().padStart(2, '0');
  const openDay = today.getDate().toString().padStart(2, '0');

  const prevDate = new Date(prevExpireDate);
  if(prevDate > today) {
    expireDateElement.value = openYear + "-" + openMonth + "-" + openDay;
  } else {
    const dates = prevExpireDate.split("-");
    expireDateElement.value = dates[0] + "-" + dates[1] + "-" + dates[2];
  }
}

document.getElementById("job-status-toggle-btn").onclick = (e) => {
  const expireDateElement = document.querySelector("input[name='expireDate']");
  const openDateElement = document.querySelector("input[name='openDate']");
  const jobStatusBtn = e.target;

  // Set previous expire date
  if(prevExpireDate == null) {
    prevExpireDate = expireDateElement.value;
  }

  if(prevOpenDate == null) {
    prevOpenDate = openDateElement.value;
  }

  if (jobStatusBtn.innerText == "CLOSED") {
    jobStatusBtn.innerText = "OPEN";
    jobStatusBtn.classList.remove("btn-danger");
    jobStatusBtn.classList.add("btn-success");
    expireDateElement.removeAttribute("readonly");
    extendExpireDate(30);
  } else {
    jobStatusBtn.innerText = "CLOSED";
    jobStatusBtn.classList.remove("btn-success");
    jobStatusBtn.classList.add("btn-danger");
    expireDateElement.setAttribute("readonly", "true");
    expireDateElement.value = prevExpireDate;
    openDateElement.value = prevOpenDate;
    closeJobOffer();
  }
};
