// Clear all error messages
function clearErrors() {
  document.querySelectorAll(".error").forEach((errorElement) => {
    errorElement.remove();
  });
}

function addJobOffer() {
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
    document.getElementById("post-count-input-row").insertAdjacentElement("beforeend", pNode);
  } else if (posts > 127) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "posts-error");
    pNode.innerText = "Job offer cannot have more than 127 posts";
    document.getElementById("post-count-input-row").insertAdjacentElement("beforeend", pNode);
  }

  if (salary == "") {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "salary-error");
    pNode.innerText = "Job offer salary should not be empty";
    document.getElementById("salary-input-row").insertAdjacentElement("beforeend", pNode);
  } else if(salary.length > 30) {
    isValid = false;
    const pNode = document.createElement("span");
    pNode.classList.add("error", "col", "col-form-label", "error");
    pNode.setAttribute("id", "salary-error");
    pNode.innerText = "Job offer salary should not exceed 30 characters";
    document.getElementById("salary-input-row").insertAdjacentElement("beforeend", pNode);
  }

  if(isValid) {
    document.getElementById("add-job-offer-form").submit();
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