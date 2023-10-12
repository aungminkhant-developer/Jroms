const jobPositionModal = document.getElementById("select-job-position");

jobPositionModal.addEventListener("shown.bs.modal", () => {
  console.log("modal element completely visible!");
  const titleOptions = jobPositionModal.querySelectorAll("#job-title-menu li");
  const levelOptions = jobPositionModal.querySelectorAll("#job-level-menu li");
  const resetBtn = jobPositionModal.querySelector(".reset-btn");
  const okBtn = jobPositionModal.querySelector(".ok-btn");
  var selectedTitle = "Select Job Position";
  var selectedLevel = "";

  titleOptions.forEach((tOpt) => {
    if(tOpt.classList.contains("selected")) {
        selectedTitle = tOpt.innerText;
    }
    tOpt.onclick = () => {
      selectedTitle = tOpt.innerText;
      titleOptions.forEach((opt) => opt.classList.remove("selected"));
      tOpt.classList.add("selected");
    };
  });

  levelOptions.forEach((lvlOpt) => {
    if(lvlOpt.classList.contains("selected")) {
        selectedLevel = lvlOpt.innerText;
    }
    lvlOpt.onclick = () => {
      selectedLevel = lvlOpt.innerText;
      levelOptions.forEach((opt) => opt.classList.remove("selected"));
      lvlOpt.classList.add("selected");
    };
  });

  resetBtn.onclick = () => {
    titleOptions.forEach((opt) => opt.classList.remove("selected"));
    levelOptions.forEach((opt) => opt.classList.remove("selected"));
    selectedTitle = "Select Job Position";
    selectedLevel = "";
  }

  okBtn.onclick = () => {
    document.getElementById("job-position-text").innerText = selectedLevel + " " + selectedTitle;
  }

  
});

function openJobOffer() {
  console.log("New Job Offer created");
}