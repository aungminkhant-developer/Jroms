const dateColumnIndex = 3;
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
var confirmInterviewResultModal = new bootstrap.Modal(
  document.getElementById("confirm-interview-result-modal")
);
var candidateId = document.getElementById("candidateId");
var jobDetailId = document.getElementById("jobDetailId");
let mySelectedItems = [];


//to show success alert box when intrerview schedule update
if (interviewUpdateSuccess != null) {
  togglePopupSuccess("Success", interviewUpdateSuccess);
}

//to select auto All in interview final stage
$("#finalStage option:eq(0)").prop("selected", true);

//multiple select box for interviewer
if (interviewerDepart !== null) {
  dropdownButton.innerText = "Department Head";
}
if (interviewerTeam !== null) {
  dropdownButton.innerText = "Team Leader";
}
if (interviewerDepart !== null && interviewerTeam !== null) {
  dropdownButton.innerText = "Department Head, Team Leader";
}

if (
  dropdownButton.innerText.includes("Depart") ||
  dropdownButton.innerText.includes("Team")
) {
  if (dropdownButton.innerText.includes(",")) {
    mySelectedItems.push("Department Head"),
      mySelectedItems.push("Team Leader");
  } else mySelectedItems.push(dropdownButton.innerText);
}

//multiple select box for interviewer
function handleCB(event) {
  const checkbox = event.target;
  const label = checkbox.parentElement.textContent.trim();
  if (checkbox.checked) {
    mySelectedItems.push(label);
  } else {
    mySelectedItems = mySelectedItems.filter((item) => item !== label);
  }

  dropdownButton.innerText =
    mySelectedItems.length > 0
      ? mySelectedItems.join(", ")
      : "Choose Interviewer...";
}

document.getElementById("dropdown-menu").addEventListener("change", handleCB);

function hidePopup() {
  popupDiv.classList.remove("show");
  document.removeEventListener("click", closePopupOnClickOutside);
}

// Click event listener to close the popup when clicked outside
function closePopupOnClickOutside(event) {
  if (!popupDiv.contains(event.target)) {
    hidePopup();
  }
}

//for interview popUp
function togglePopup() {
  popupDiv.classList.toggle("show");
  document.addEventListener("click", closePopupOnClickOutside);
  if (popupDivEmail.classList.contains("show")) {
    popupDivEmail.classList.remove("show");
  }
}

function closePopUp() {
  //to reset interview select
  $("#dropdown-menu input").eq(0).prop("checked", false);
  $("#dropdown-menu input").eq(1).prop("checked", false);
  //to remove interview stage
  for (var i = 0; i < stage.options.length; i++) {
    stage.options[i].removeAttribute("selected", "selected");
  }
  dropdownButton.innerText = "Choose Interviewer...";
  mySelectedItems = [];
  interviewDate1.value = null;
  timeInput1.value = null;
  timeInput2.value = null;
  popupDiv.classList.toggle("show");
}

function isAuthorizedInterviewer(user) {
  const userRole = document.getElementById("user-role");
  const username = userRole.dataset.username;
  const role = userRole.innerText;
  const isInterviewer =
    role.includes("DEPARTMENT_HEAD") || role.includes("TEAM_LEADER");

  return isInterviewer && username == user;
}

function isUserAuthorized(user) {
  const userRole = document.getElementById("user-role");
  const username = userRole.dataset.username;
  const role = userRole.innerText;
  const isInterviewer =
    role.includes("DEPARTMENT_HEAD") || role.includes("TEAM_LEADER");

  return !isInterviewer || username == user;
}

function isAnAdmin() {
  const userRole = document.getElementById("user-role");
  const role = userRole.innerText;
  return role.includes("ADMIN");
}

$(function () {
  $(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });

  // Update DataTable AJAX URL on dropdown change
  $("#finalStage").on("change", function () {
    var stage = $("#finalStage").val();
    var dynamicURL;

    if (stage == 1) {
      dynamicURL = "/mng/rest/allInterviews/finalStage";
    } else {
      dynamicURL = "/mng/rest/interviews";
    }
    // Update DataTable AJAX URL
    table.ajax.url(dynamicURL).load();
  });

  // Handle search input and trigger DataTable search
  $("#searchInput").on("keyup", function () {
    table.search(this.value).draw();
  });

  const table = $("table#interviews").DataTable({
    ajax: {
      contentType: "application/json",
      url: "/mng/rest/test",
      type: "POST",
      data: function (d) {
        return JSON.stringify(d);
      },
    },
    language: {
      infoFiltered: "",
      searchPlaceholder: "Search query",
    },
    stateSave: true,
    serverSide: true,
    columns: [
      {
        data: "id",
        render: function (data, type, row, meta) {
          return meta.row + meta.settings._iDisplayStart + 1;
        },
        className: "align-middle",
        orderable: false,
      },
      { data: "candidate.name", className: "align-middle" },
      {
        data: "interviewerList",
        render: function (data, type, row) {
          var nameList = "";
          data.forEach((interviewer) => {
            nameList += interviewer.name + ", ";
          });
          // Remove the trailing comma and space
          return nameList.slice(0, -2);
        },
        createdCell: function (td, cellData, rowData, row, col) {
          $(td).attr("style", "width:8%");
        },
        className: "align-middle",
      },
      {
        data: "interviewSchedule.interviewDate",
        render: function (data, type, row) {
          const date = new Date(data);
          var month;
          if (date.getMonth() + 1 < 10) {
            month = "0" + (date.getMonth() + 1);
          } else {
            month = date.getMonth() + 1;
          }
          var day;
          if (date.getDate() < 10) {
            day = "0" + date.getDate();
          } else {
            day = date.getDate();
          }
          return date.getFullYear() + "-" + month + "-" + day;
        },
        className: "align-middle",
        searchable: false,
      },
      {
        data: "interviewSchedule.interviewType.name",
        className: "align-middle",
      },
      { data: "interviewStage.name", className: "align-middle" },
      {
        data: "result",
        render: function (data, type, row) {
          var interviewId = row.id;
          var mailStatus = row.mailStatus;
          const isAnAuthorizedInterviewer =
            isAuthorizedInterviewer(
              row.jobDetail.team.department.departmentHead.username
            ) || isAuthorizedInterviewer(row.jobDetail.team.teamLeader);
          const isAuthorized = isAnAuthorizedInterviewer || isAnAdmin();
          if (
            mailStatus === "NEXT_MAIL_SEND" ||
            mailStatus === "NOT_SEND" ||
            !isAuthorized
          ) {
            return (
              '<td><input type="hidden" class="resultValue" value="' +
              interviewId +
              '">' +
              '<p style="text-align:center;margin-top:15px">' +
              data +
              "</p></td>"
            );
          } else {
            return (
              " <td >" +
              '<input type="hidden" class="resultValue" value="' +
              interviewId +
              '">' +
              '<select style="color:#696969;width:200px;height:50px" id="mySelect" data-mailstatus="' +
              mailStatus +
              '"' +
              ' data-show-content="true" class="form-select fa"' +
              'aria-label="Default select example"' +
              '><option value="NEXT_INTERVIEW"' +
              (data === "NEXT_INTERVIEW" ? " selected" : "") +
              "><span" +
              ">" +
              (data === "NEXT_INTERVIEW" ? "&#xf058;" : "") +
              "</span>" +
              " Next Interview</option>" +
              '<option value="PASSED"' +
              (data === "PASSED" ? " selected" : "") +
              "><span" +
              ">" +
              (data === "PASSED" ? "&#xf058;" : "") +
              "</span>" +
              "Passed</option>" +
              '<option value="FAILED"' +
              (data === "FAILED" ? " selected" : "") +
              "><span" +
              ">" +
              (data === "FAILED" ? "&#xf058;" : "") +
              "</span>" +
              "Failed</option>" +
              '<option value="PENDING"' +
              (data === "PENDING" ? " selected" : "") +
              "><span" +
              ">" +
              (data === "PENDING" ? "&#xf058;" : "") +
              "</span>" +
              "Pending</option>" +
              '<option value="CANCEL"' +
              (data === "CANCEL" ? " selected" : "") +
              "><span" +
              ">" +
              (data === "CANCEL" ? "&#xf058;" : "") +
              "</span>" +
              "Cancel</option>" +
              "</select>" +
              "</td>"
            );
          }
        },
        createdCell: function (td, cellData, rowData, row, col) {
          $(td).attr("id", "resultSelect");
          $(td).attr("style", "width:12%");
        },
        className: "align-middle",
      },
      {
        data: "status",
        render: function (data, type, row) {
          return data;
        },
        createdCell: function (td) {
          $(td).attr("id", "statusChange");
        },
        className: "align-middle",
      },
      {
        data: "id",
        render: function (data, type, row) {
          var mailStatus = row.mailStatus;
          var date1 = row.interviewSchedule.interviewDate.substring(0, 10);
          var time1 = row.interviewSchedule.startTime;
          var time2 = row.interviewSchedule.endTime;
          var typeName = row.interviewSchedule.interviewType.name;
          var typeValue = row.interviewSchedule.interviewLocation;
          var name = row.candidate.name;
          var candidateEmail = row.candidate.email;
          var candidateId = row.candidate.id;
          const isAuthorized =
            isUserAuthorized(
              row.jobDetail.team.department.departmentHead.username
            ) || isUserAuthorized(row.jobDetail.team.teamLeader);
          if (isAuthorized) {
            return (
              '<a type="button" class="btn btn-outline-primary send-btn "  data-candidatename="' +
              name +
              '" ' +
              ' style="margin-left:20px;" data-email="' +
              candidateEmail +
              '" data-date1="' +
              date1 +
              '" ' +
              ' data-time1="' +
              time1 +
              '" data-time2="' +
              time2 +
              '"  data-interviewid="' +
              data +
              '"' +
              ' data-type="' +
              typeName +
              '" data-mailstatus="' +
              mailStatus +
              '" ' +
              ' data-value="' +
              typeValue +
              '" data-candidateid="' +
              candidateId +
              '"' +
              ' id="sendEamilBtn"  role="button">' +
              '<i class="fa-solid fa-paper-plane"></i></a>' +
              '<span class="position-absolute translate-middle p-1 ' +
              (mailStatus === "NOT_SEND"
                ? "text-danger fa-solid fa-circle-info"
                : "text-success fa-solid fa-circle-check") +
              '">' +
              '<span class="visually-hidden">New alerts</span>' +
              "</span>"
            );
          } else {
            return (
              '<a type="button" class="btn btn-outline-secondary"  data-candidatename="' +
              name +
              '" ' +
              ' style="margin-left:20px;pointer-events: none;" data-email="' +
              candidateEmail +
              '" data-date1="' +
              date1 +
              '" ' +
              ' data-time1="' +
              time1 +
              '" data-time2="' +
              time2 +
              '"  data-interviewid="' +
              data +
              '"' +
              ' data-type="' +
              typeName +
              '" data-mailstatus="' +
              mailStatus +
              '" ' +
              ' data-value="' +
              typeValue +
              '" data-candidateid="' +
              candidateId +
              '"' +
              ' id="sendEamilBtn"  role="button">' +
              '<i class="fa-solid fa-paper-plane"></i></a>' +
              '<span class="position-absolute translate-middle p-1 ' +
              (mailStatus === "NOT_SEND"
                ? "text-danger fa-solid fa-circle-info"
                : "text-success fa-solid fa-circle-check") +
              '">' +
              '<span class="visually-hidden">New alerts</span>' +
              "</span>"
            );
          }
        },
        className: "align-middle",
        orderable: false,
      },
      {
        data: "id",
        render: function (data, type, row) {
          var mailStatus = row.mailStatus;
          var candidateId2 = row.candidate.id;
          var interviewId = row.id;
          const isAuthorized =
            isUserAuthorized(
              row.jobDetail.team.department.departmentHead.username
            ) || isUserAuthorized(row.jobDetail.team.teamLeader);
          if (isAuthorized) {
            return (
              '<div class="row w-100"><div class="col-md-3"><i class="fa-solid fa-pen-to-square edit-btn" ' +
              'data-candidateid="' +
              candidateId2 +
              '" data-mailstatus="' +
              mailStatus +
              '"></i>' +
              '<input type="hidden" value="' +
              data +
              '" class="eidtInterviewId"></div>' +
              '<div class="col-md-3"><i class="fa-solid fa-eye see-detail-interview" data-id="' +
              interviewId +
              '" ' +
              '"></i></div>' +
              '<div class="col-md-3"><a href="/mng/interview/candidateInterview?candidateId=' +
              candidateId2 +
              '"><i class="fa-solid fa-angles-right" ' +
              '"></i></a></div></div>'
            );
          } else {
            return (
              '<div class="row w-100"><div class="col-md-3"><i class="fa-solid fa-pen-to-square text-secondary"></i>' +
              '<input type="hidden" value="' +
              data +
              '" class="eidtInterviewId"></div>' +
              '<div class="col-md-3"><i class="fa-solid fa-eye text-secondary"></i></div>' +
              '<div class="col-md-3"><i class="fa-solid fa-angles-right text-secondary"></i></div></div>'
            );
          }
        },
        createdCell: function (td, cellData, rowData, row, col) {
          $(td).attr("style", "width:8%");
        },
        className: "align-middle ",
        orderable: false,
      },
      {
        data: "jobDetail.team.department.name",
        className: "align-middle d-none",
        orderable: false,
      },
      {
        data: "jobDetail.team.name",
        className: "align-middle d-none",
        orderable: false,
      },
    ],
  });

  function applyFilters() {
    var typeFilter = $("#type").val();
    var stageFilter = $("#stageInterview").val();
    var resultFilter = $("#result").val();
    var statusFilter = $("#status").val();
    var departmentFilter = $("#department").val();
    var teamFilter = $("#team").val();

    table.column(4).search(typeFilter).draw();
    table.column(5).search(stageFilter).draw();
    table.column(6).search(resultFilter).draw();
    table.column(7).search(statusFilter).draw();
    table.column(10).search(departmentFilter).draw();
    table.column(11).search(teamFilter).draw();
  }
  $("#result").on("change", function () {
    applyFilters();
  });
  $("#stageInterview").on("change", function () {
    applyFilters();
  });

  $("#type").on("change", function () {
    applyFilters();
  });
  $("#status").on("change", function () {
    applyFilters();
  });
  $("#department").on("change", function () {
    applyFilters();
  });
  $("#team").on("change", function () {
    applyFilters();
  });

  table.column(dateColumnIndex).search(null).draw();

  // Initialize date range picker
  var start = null;
  var end = null;

  $("#daterange span").html("Select your date range");

  function cb(start, end) {
    $("#min-date").val(start.format("YYYY-MM-DD"));
    $("#max-date").val(end.format("YYYY-MM-DD"));
    $("#daterange span").html(
      start.format("MM/DD/YYYY") + " - " + end.format("MM/DD/YYYY")
    );
    table
      .column(dateColumnIndex)
      .search(start.format("YYYY-MM-DD") + ";" + end.format("YYYY-MM-DD"))
      .draw();
  }

  $("#daterange").daterangepicker(
    {
      showDropdowns: true,
      locale: {
        cancelLabel: "Clear",
      },
      ranges: {
        Today: [moment(), moment()],
        Yesterday: [moment().subtract(1, "days"), moment().subtract(1, "days")],
        "Last 7 Days": [moment().subtract(6, "days"), moment()],
        "Last 30 Days": [moment().subtract(29, "days"), moment()],
        "This Month": [moment().startOf("month"), moment().endOf("month")],
        "Last Month": [
          moment().subtract(1, "month").startOf("month"),
          moment().subtract(1, "month").endOf("month"),
        ],
      },
      alwaysShowCalendars: true,
    },
    cb
  );

  // return all rows when date range gets cleared
  $("#daterange").on("cancel.daterangepicker", function (ev, picker) {
    $("#daterange span").html("Select your date range");
    table.column(dateColumnIndex).search(null).draw();
  });

  table.on("draw", function () {
    //for check interview result in database by fetch when select
    document.querySelectorAll('[id^="resultSelect"]').forEach((element) => {
      element.lastElementChild.addEventListener("change", function (e) {
        var interviewId = element.firstElementChild.value;
        var interviewResult = element.lastElementChild.value;
        var selectBox = element.lastElementChild;
        var selectedOption = selectBox.options[selectBox.selectedIndex];
        var selectedOptionText = selectedOption.textContent;
        var icon = "&#xf058;";

        // Add the icon to the selected option
        selectedOption.innerHTML = icon + " " + selectedOptionText;

        // Remove the icon from the other options or not selected option
        for (var i = 0; i < selectBox.options.length; i++) {
          if (i !== selectBox.selectedIndex) {
            var option = selectBox.options[i];
            var icon = "&#xf058;"; // The icon you want to remove (replace this with the actual icon)
            switch (i) {
              case 0:
                option.innerHTML = "Next Interview";
                break;
              case 1:
                option.innerHTML = "Passed";
                break;
              case 2:
                option.innerHTML = "Failed";
                break;
              case 3:
                option.innerHTML = "Pending";
                break;
              case 4:
                option.innerHTML = "Cancel";
                break;
            }
          }
        }

        //to get index of selected element
        var indexOfSelectElement = Array.from(
          document.querySelectorAll('[id^="resultSelect"]')
        ).indexOf(element);

        //for get the list of interview result
        var statusChange = document.querySelectorAll('[id^="statusChange"]');

        //for insert status value in database
        var addStatus = null;

        //loop interview status and find status that change result colume
        for (var j = 0; j < statusChange.length; j++) {
          var statusValue = statusChange[j];
          //for interview status when select passed and cancel
          if (
            selectedOption.value === "PASSED" ||
            selectedOption.value === "CANCEL" ||
            selectedOption.value === "FAILED"
          ) {
            if (j === indexOfSelectElement) {
              statusValue.innerHTML = "FINISHED";
              addStatus = "FINISHED";
            }
          }
          if (
            selectedOption.value === "NEXT_INTERVIEW" ||
            selectedOption.value === "PENDING"
          ) {
            if (j === indexOfSelectElement) {
              statusValue.innerHTML = "ONGOING";
              addStatus = "ONGOING";
            }
          }
        }

        //check mail status
        var mailStatus = e.target.dataset.mailstatus;
        if (mailStatus == "SEND") {
          //for build interview object
          const interview = {
            id: interviewId,
            result: interviewResult,
            status: addStatus,
          };
          console.log("token : " + token);
          fetch("/mng/interview/addInterviewResult", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              [header]: token,
              "X-XSRF-Token": token,
            },
            body: JSON.stringify(interview),
          })
            .then((response) => {
              if (!response.ok) {
                console.log("Fail");
                throw new Error("Response is not ok");
              }

              return response.text();
            })
            .then((data) => console.log(data))
            .catch((error) => console.error("Error :" + error));
        }
      }); //end tag
    }); //end tag of interviewv result select

    //delclare variable to get from each interview table
    var candidateNameMail,
      startDateMail,
      startTimeMail,
      endTimeMail,
      typeNameMail,
      typeValueMail;

    //to send interview mail
    document.querySelectorAll(".send-btn").forEach((el) => {
      el.onclick = (e) => {
        const targetBtn = e.target.closest(".send-btn");
        if (targetBtn) {
          var mailStatus = targetBtn.dataset.mailstatus;
          if (mailStatus === "NOT_SEND") {
            candidateIdForSendMail.value = targetBtn.dataset.candidateid;
            sendMailInterviewId.value = targetBtn.dataset.interviewid;
            candidateNameMail = targetBtn.dataset.candidatename;
            toEmail.value = targetBtn.dataset.email;
            fromEmail.value = "acecompany771@gmail.com";
            startDateMail = targetBtn.dataset.date1;
            startTimeMail = targetBtn.dataset.time1;
            endTimeMail = targetBtn.dataset.time2;
            typeNameMail = targetBtn.dataset.type;
            typeValueMail = targetBtn.dataset.value;
            redirectPage.value = 1;
            //get candidate count to check confirmBox
            $.ajax({
              url:
                "/mng/interview/candidateCount/" +
                targetBtn.dataset.candidateid,
              type: "GET",
              success: function (data) {
                if (data > 1) {
                  //to check interview result
                  $.ajax({
                    url:
                      "/mng/interview/checkIneterviewResultByMailStatus/" +
                      targetBtn.dataset.candidateid,
                    type: "GET",
                    success: function (data) {
                      //check when previous interview result is not next interview
                      if (data == true) {
                        checkInterviewResultModal.show();
                      } else {
                        confirmInterviewResultModal.show();
                      }
                    },
                    error: function () {
                      console.error(
                        "Error in check interviewresult when create interview"
                      );
                    },
                  });
                } else {
                  togglePopupEmail();
                }
              },
              error: function () {
                console.error(
                  "Error in getting candidate count for confirmation box"
                );
              },
            });
          } else {
            togglePopupWarning("Mail Warning !!", "This mail has been sent ");
          }
        }
      };
    }); //end tag of send interview mail

    //to select email type
    document
      .getElementById("typeSelect")
      .addEventListener("change", function () {
        var typeId = typeSelect.value;
        var editor = tinymce.get("interviewMail");
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
              var onlineMetting, location, locationValue;
              if (
                typeNameMail.includes("Online") ||
                typeNameMail.includes("online") ||
                typeNameMail.includes("ONLINE")
              ) {
                onlineMetting =
                  "<p>Join Zoom Meeting</p>" +
                  '<p contenteditable="false">' +
                  typeValueMail +
                  "</p>";
                location = "URL";
                locationValue = "Join below";
              } else {
                onlineMetting = "";
                location = "Location";
                locationValue = typeValueMail;
              }
              var insertContent =
                "<p>&nbsp;</p>" +
                "<p>Interview Schedule</p>" +
                '<p contenteditable="false">Date&nbsp; &nbsp; :&nbsp; ' +
                startDateMail +
                "</p>" +
                '<p contenteditable="false">Time&nbsp; &nbsp; :&nbsp; &nbsp;' +
                startTimeMail +
                "- " +
                endTimeMail +
                "</p>" +
                '<p contenteditable="false">Type&nbsp; &nbsp; :&nbsp; &nbsp; ' +
                typeNameMail +
                "</p>" +
                '<p contenteditable="false">' +
                location +
                ": " +
                locationValue +
                "</p>" +
                onlineMetting;
              if (data.subject == null) {
                editor.setContent(insertContent);
                subject.value = null;
              } else {
                var replaceName = data.bodyText.replaceAll(
                  "[Candidate's Name]",
                  candidateNameMail
                );
                editor.setContent(replaceName + insertContent);
                subject.value = data.subject;
              }
            })
            .catch((error) => {
              console.error("Error :" + error);
            });
        }
      }); //end tag of email type select

    //to eidt interview
    document.querySelectorAll(".edit-btn").forEach((element, index) => {
      element.addEventListener("click", function (e) {
        var mailStatus = e.target.dataset.mailstatus;
        var candidateId3 = e.target.dataset.candidateid;
        if (mailStatus === "NOT_SEND") {
          var editInterviewId =
            document.querySelectorAll(".eidtInterviewId")[index];
          var interviewId = editInterviewId.value;
          fetch("/mng/interview/editInterview", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              [header]: token,
              "X-XSRF-Token": token,
            },
            body: JSON.stringify(interviewId),
          })
            .then((response) => {
              if (!response.ok) {
                console.log("Fail");
                throw new Error("Response is not ok");
              }
              return response.json();
            })
            .then((data) => {
              interviewDate1.value = data.startDate;
              timeInput1.value = data.startTime;
              timeInput2.value = data.endTime;
              typeValue.value = data.typeValue;
              updateInterviewId.value = data.id;
              candidateId.value = candidateId3;
              jobDetailId.value = "All";
              $("#dropdown-menu input")
                .eq(0)
                .prop("value", data.interviewerIdList[0]);
              $("#dropdown-menu input")
                .eq(1)
                .prop("value", data.interviewerIdList[1]);
              //to set candidate detail in interview schedule
              $("#candidateDetail span").eq(0).text(data.candidateName);
              $("#candidateDetail span")
                .eq(1)
                .text(
                  data.jobDetailName +
                    (data.jobLevelName != null
                      ? "(" + data.jobLevelName + ")"
                      : "")
                );
              // to be selected interivew type
              for (var i = 0; i < selectBox.options.length; i++) {
                if (selectBox.options[i].innerHTML === data.interviewType) {
                  selectBox.options[i].setAttribute("selected", "selected");
                } else
                  selectBox.options[i].removeAttribute("selected", "selected");
              }
              //to be selected interviewer
              if (
                data.interviewerList[0] != null &&
                data.interviewerList[1] != null
              ) {
                $("#dropdown-menu input").eq(0).prop("checked", true);
                $("#dropdown-menu input").eq(1).prop("checked", true);
                mySelectedItems.push("Department Head"),
                  mySelectedItems.push("Team Leader");
                dropdownButton.innerText = "Department Head, Team Leader";
              } else {
                if (
                  data.interviewerList[0] != null &&
                  data.interviewerList[0].includes("DEPART")
                ) {
                  $("#dropdown-menu input").eq(0).prop("checked", true);
                  mySelectedItems.push("Department Head"),
                    (dropdownButton.innerText = "Department Head");
                }
                if (
                  data.interviewerList[0] != null &&
                  data.interviewerList[0].includes("TEAM")
                ) {
                  $("#dropdown-menu input").eq(1).prop("checked", true);
                  mySelectedItems.push("Team Leader");
                  dropdownButton.innerText = "Team Leader";
                }
              }
              // to be selected interview stage
              for (var i = 0; i < stage.options.length; i++) {
                if (stage.options[i].innerHTML === data.interviewStage) {
                  stage.options[i].setAttribute("selected", "selected");
                } else {
                  stage.options[i].removeAttribute("selected", "selected");
                }
              }
              contactForm.removeAttribute("action");
              contactForm.setAttribute(
                "action",
                "/mng/interview/updateInterview"
              );
              createBtn.value = "Update";
              togglePopup();
            })
            .catch((error) => console.error("Error :" + error));
        } else {
          togglePopupWarning(
            "Warning !!",
            "This interview schedule has been sent "
          );
        }
      });
    }); //end tag of edit interview

    //to see more detail interview by modal box
    document.querySelectorAll(".see-detail-interview").forEach((element) => {
      element.addEventListener("click", function (e) {
        const targetBtn = e.target.closest(".see-detail-interview");
        if (targetBtn) {
          var interviewId = targetBtn.dataset.id;
          fetch("/mng/interview/editInterview", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              [header]: token,
              "X-XSRF-Token": token,
            },
            body: JSON.stringify(interviewId),
          })
            .then((response) => {
              if (!response.ok) {
                console.log("Fail");
                throw new Error("Response is not ok");
              }
              return response.json();
            })
            .then((data) => {
              var nameList = "";
              data.interviewerNameList.forEach((name) => {
                nameList += name + ",";
              });
              $("#interviewModal span").eq(0).text(data.candidateName);
              $("#interviewModal span")
                .eq(1)
                .text(
                  data.jobDetailName +
                    (data.jobLevelName != null
                      ? "(" + data.jobLevelName + ")"
                      : "")
                );
              $("#interviewModal span").eq(2).text(data.departHeadName);
              $("#interviewModal span").eq(3).text(data.teamLeaderName);
              $("#interviewModal span").eq(4).text(data.startDate);
              $("#interviewModal span")
                .eq(5)
                .text(data.startTimeFormat + "-" + data.endTimeFormat);
              $("#interviewModal span").eq(6).text(data.interviewType);
              $("#interviewModal p strong")
                .eq(7)
                .text(
                  data.interviewType.includes("online") ||
                    data.interviewType.includes("Online")
                    ? "URL:"
                    : "Location:"
                );
              $("#interviewModal span")
                .eq(7)
                .text(
                  data.interviewType.includes("online") ||
                    data.interviewType.includes("Online")
                    ? data.typeValue.substring(0, 20) + "..."
                    : data.typeValue
                );
              $("#interviewModal span").eq(8).text(nameList.slice(0, -1));
              $("#interviewModal span").eq(9).text(data.interviewStage);
              $("#interviewModal").modal("show");
            });
        }
      });
    });

    // Get all elements with the class "scrollable-cell"
    const column6Cells = document.querySelectorAll("td:nth-child(6)");

    // Add click event listener to each cell in Column 2
    column6Cells.forEach((cell) => {
      cell.addEventListener("click", () => {
        // Create a temporary textarea to copy the text to the clipboard
        const textarea = document.createElement("textarea");
        textarea.value = cell.textContent.trim(); // Get the text content and remove leading/trailing spaces

        // Append the textarea to the document body
        document.body.appendChild(textarea);

        // Select the text inside the textarea
        textarea.select();

        // Copy the text to the clipboard
        const successful = document.execCommand("copy");

        // Remove the temporary textarea from the document
        document.body.removeChild(textarea);

        // Show the notification message if copying is successful
        if (successful) {
          const copyNotification = document.getElementById("copyNotification");
          copyNotification.style.display = "block";
          setTimeout(() => {
            copyNotification.style.display = "none";
          }, 1000); // Hide the message after 2 seconds
        }

        // Optionally, provide some visual feedback to the user
        cell.style.backgroundColor = "#f0f0f0";
        setTimeout(() => {
          cell.style.backgroundColor = "";
        }, 250);
      });
    });
  }); //end tag of table draw
});
