let stompClient = null;

// Subscribe public notification
let socket = new SockJS("/ws");
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
  const role = document.getElementById("user-role").innerHTML;

  // Test if the logged in user is an interviewer
  if (
    role.includes("ROLE_DEPARTMENT_HEAD") ||
    role.includes("ROLE_TEAM_LEADER")
  ) {
    // Listen to interview notifications
    stompClient.subscribe("/user/specific", function (result) {
      showNotification(JSON.parse(result.body));
    });
  } else {
    // Listen to applicant notifications
    stompClient.subscribe("/all/messages/applicants", function (result) {
      showNotification(JSON.parse(result.body));
    });
  }
});

function clearEmptyNotiText() {
  let emptyNotiText = document.getElementById("empty-notification-text");
  if (emptyNotiText) {
    emptyNotiText.remove();
  }
}

function showNotification(noti) {
  clearEmptyNotiText();
  let notificationUrl = null;
  let hrefLink = null;
  const role = document.getElementById("user-role").innerHTML;
  var isInterviewer = role.includes("ROLE_DEPARTMENT_HEAD") || role.includes("ROLE_TEAM_LEADER");
  if (
    isInterviewer
  ) {
    notificationUrl = "/mng/notifications/interviews";
    hrefLink = "/mng/interview/candidateInterview?candidateId=";
  } else {
    notificationUrl = "/mng/notifications/applicants";
    hrefLink = "/mng/jobOffer/";

    if (localStorage.getItem("group-notification")) {
      notificationUrl += "/group";
    }
  }

  if (localStorage.getItem("group-notification") && !isInterviewer) {
    let msgContainer = document.getElementById("notification-dropdown");
    msgContainer.replaceChildren();
    $.ajax({
      url: notificationUrl,
    }).done(function (notifications) {
      notifications.forEach((noti) => {
        // <div class="text-wrap">Mary applied for the job: Senior Java Developer.</div>
        const msgDivElement = document.createElement("div");
        msgDivElement.classList.add("text-wrap");
        msgDivElement.innerHTML =
          "Job offer: " +
          noti.jobPosition +
          "[id=" +
          noti.jobDetailId +
          "]" +
          " has " +
          noti.applicantCount +
          " applicants.";

        // <div class="text-muted fw-normal text-end small-font">2023/8/8, 12:00 PM</div>
        const dateDivElement = document.createElement("div");
        dateDivElement.classList.add(
          "text-muted",
          "fw-normal",
          "text-end",
          "small-font"
        );
        dateDivElement.innerHTML = noti.lastAppliedDate;

        // <a href="#" class="dropdown-item">
        const aElement = document.createElement("a");
        aElement.classList.add("dropdown-item");
        aElement.setAttribute("href", hrefLink + noti.jobDetailId);
        aElement.append(msgDivElement, dateDivElement);

        // <div class="dropdown-divider"></div>
        const dividerElement = document.createElement("div");
        dividerElement.classList.add("dropdown-divider");

        // <li>
        const liElement = document.createElement("li");
        liElement.append(aElement, dividerElement);
        msgContainer.appendChild(liElement);
      });

      if (notifications.length <= 0) {
        let msgContainer = document.getElementById("notification-dropdown");
        // Create element
        const divElement = document.createElement("div");
        divElement.classList.add("p-3");
        divElement.id = "empty-notification-text";
        divElement.innerHTML = "No Notification Yet";
        msgContainer.appendChild(divElement);
      } else {
        // Remove the last divider
        let msgContainer = document.getElementById("notification-dropdown");
        msgContainer.lastChild.lastChild.remove();
      }
    });
  } else {
    let msgContainer = document.getElementById("notification-dropdown");

    // <div class="text-wrap">Mary applied for the job: Senior Java Developer.</div>
    const msgDivElement = document.createElement("div");
    msgDivElement.classList.add("text-wrap");
    msgDivElement.innerHTML = noti.message;

    // <div class="text-muted fw-normal text-end small-font">2023/8/8, 12:00 PM</div>
    const dateDivElement = document.createElement("div");
    dateDivElement.classList.add(
      "text-muted",
      "fw-normal",
      "text-end",
      "small-font"
    );
    dateDivElement.innerHTML = noti.createdDate;

    // <a href="#" class="dropdown-item">
    const aElement = document.createElement("a");
    aElement.classList.add("dropdown-item");
    if(isInterviewer) {
      aElement.setAttribute("href", hrefLink + noti.candidateId);
    } else {
      aElement.setAttribute("href", hrefLink + noti.jobDetailId);
    }
    
    aElement.append(msgDivElement, dateDivElement);

    // <div class="dropdown-divider"></div>
    const dividerElement = document.createElement("div");
    dividerElement.classList.add("dropdown-divider");

    // <li>
    const liElement = document.createElement("li");
    liElement.append(aElement, dividerElement);
    // msgContainer.insertBefore(liElement, msgContainer.firstChild);
    msgContainer.prepend(liElement);

    // Remove the last divider
    // msgContainer.lastChild.lastChild.remove();
  }

  // Show active notification icon bell
  document
    .getElementById("notificationDropdown")
    .classList.add("notification-bell-active");
}
