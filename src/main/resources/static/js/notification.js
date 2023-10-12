function loadNotification(checked) {
  // Get CSRF Token
  var token = $("meta[name='_csrf']").attr("content");
  var header = $("meta[name='_csrf_header']").attr("content");
  $(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  let notificationUrl = null;
  let hrefLink = null;
  const infoElement = document.getElementById("user-role");
  const role = infoElement.innerHTML;
  const username = infoElement.dataset.username;
  const isInterviewer =
    role.includes("ROLE_DEPARTMENT_HEAD") || role.includes("ROLE_TEAM_LEADER");
  if (isInterviewer) {
    const groupByCard = document.getElementById("noti-group-by-card");
    if(groupByCard) {
      groupByCard.remove();
    }
    notificationUrl = "/mng/notifications/interviews?username=" + username;
    hrefLink = "/mng/interview/candidateInterview?candidateId=";
  } else {
    notificationUrl = "/mng/notifications/applicants";
    hrefLink = "/mng/jobOffer/";

    if (localStorage.getItem("group-notification")) {
      notificationUrl += "/group";
    }
  }

  $.ajax({
    url: notificationUrl,
  }).done(function (notifications) {
    if (checked && !isInterviewer) {
      let msgContainer = document.getElementById("notification-dropdown");
      msgContainer.replaceChildren();
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
      return;
    }

    let msgContainer = document.getElementById("notification-dropdown");
    msgContainer.replaceChildren();
    notifications.forEach((noti) => {
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
}

$(function () {
  // Remove active icon on click
  document.getElementById("notificationDropdown").onclick = (e) => {
    e.target.classList.remove("notification-bell-active");
  };

  document.getElementById("noti-group-by-toggle").onchange = (e) => {
    if (e.target.checked) {
      localStorage.setItem("group-notification", "true");
    } else {
      localStorage.removeItem("group-notification");
    }
    notificationUrl = "/mng/notifications/applicants";
    hrefLink = "/mng/jobOffer/";

    if (e.target.checked) {
      notificationUrl += "/group";
    }
    loadNotification(e.target.checked);
  };

  const isChecked = document.getElementById("noti-group-by-toggle").checked;
  loadNotification(isChecked);
});

/*
<li>
  <a href="#" class="dropdown-item">
    <div class="text-wrap">Mary applied for the job: Senior Java Developer.</div>
    <div class="text-muted fw-normal text-end small-font">2023/8/8, 12:00 PM</div>
  </a>
  <div class="dropdown-divider"></div>
</li>
 */
