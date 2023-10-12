var contactForm = document.getElementById('contactForm');
var createBtn = document.getElementById('createBtn');
var stage = document.getElementById('stage');
var popupDiv = document.getElementById("popupDiv");
var popupDivEmail = document.getElementById('popupDivEmail')
var updateInterviewId = document.getElementById('interviewId');
var subject = document.getElementById('subject');
var toEmail = document.getElementById('toEmail');
var fromEmail = document.getElementById('fromEmail');
var interviewScheduleBtn = document.getElementById('interviewScheduleBtn');
var timeInput1 = document.getElementById('interviewTime1');
var timeInput2 = document.getElementById('interviewTime2');
var fromMailError = document.getElementById('fromMailError');
var toMailError = document.getElementById('toMailError');
var subjectError = document.getElementById('subjectError');
var attachmentFileError = document.getElementById('attachmentFileError');
var sendMailInterviewId = document.getElementById('sendMailInterviewId');
var fileInput = document.getElementById('fileInput');
var attachments = document.getElementById('attachments');
var fileCountLabel = document.getElementById('fileCountLabel');
var interviewDate1 = document.getElementById('interviewDate1');
var selectBox = document.getElementById('interviewType');
var typeValue = document.getElementById('typeValue');
var interviewCreateError = document.getElementById('interviewCreateError');
var interviewTypeSelect = document.getElementById('typeSelect');
var emailMessageError = document.getElementById('messageError');
const dropdownButton = document.getElementById('multiSelectDropdown');
var confirmInterviewResultModal = new bootstrap.Modal(document.getElementById("confirm-interview-result-modal"));
var checkInterviewResultModal = new bootstrap.Modal(document.getElementById("check-interview-result-modal"));
const candidateIdForSendMail = document.getElementById('candidateIdForSendMail');
const redirectPage = document.getElementById('redirectPage');

//function for prevent space input
function preventSpace(event) {
  const inputElement = event.target;
  const value = inputElement.value;
  const firstChar = value.charAt(0);

  if (event.keyCode === 32 && firstChar === '') {
    event.preventDefault();
  }
}

//function for remove space
function removeLeadingSpace(event) {
  const inputElement = event.target;
  const value = inputElement.value;
  const newValue = value.replace(/^\s+/, ""); // Remove leading spaces
  inputElement.value = newValue;
}

//for disable past date 
const date = new Date();
var tDate = date.getDate();
var month = date.getMonth() + 1;
var year = date.getUTCFullYear();

if (tDate < 10) {
  tDate = '0' + tDate;
}

if (month < 10) {
  month = '0' + month;
}

var minDate = year + '-' + month + '-' + tDate;
interviewDate1.setAttribute('min', minDate);

//to call function for time to convert to AM and PM
function formatTime(timeInput) {

  const selectedTime = timeInput.value;
  const parts = selectedTime.split(':');
  const hours = parseInt(parts[0]);
  const minutes = parts[1];

  let formattedTime = '';
  if (hours === 0) {
    formattedTime = '12:' + minutes + ' AM';
  } else if (hours < 12) {
    formattedTime = (hours < 10 ? '0' : '') + hours + ':' + minutes + ' AM';
  } else if (hours === 12) {
    formattedTime = '12:' + minutes + ' PM';
  } else {
    const pmHours = hours - 12;
    formattedTime = (pmHours < 10 ? '0' : '') + pmHours + ':' + minutes + ' PM';
  }

  return formattedTime;

}

//for select box for interview type
selectBox.addEventListener('change', function () {
  var typeId = selectBox.value;
  console.log(typeId);
  fetch('/mng/interview/getInterviewType', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [header]: token,
      'X-XSRF-Token': token
    },
    body: JSON.stringify(typeId)
  })
    .then(response => {
      if (!response.ok) {
        console.log("Fail");
        throw new Error("Response is not ok");
      }
      return response.json();
    }
    )
    .then(data => {
      if (data.meetingUrl == null) {
        typeValue.value = data.location.building + "," + data.location.township + "," +
          data.location.division;
      }
      else { typeValue.value = data.meetingUrl; }

    })
    .catch(error => {
      console.error(error)
    });

});//end tag of interview select type

//for reshow error when send mail
const setErrorInterviewCreate = (element, message) => {
  element.innerHTML = message;
  setTimeout(() => {
    element.innerHTML = '';
  }, 4000);
}

// for validate interviewer schedule
function submitForm() {
  var interivewer = document.getElementById('interviewer');
  var formattedTime1 = formatTime(timeInput1);
  var formattedTime2 = formatTime(timeInput2);

  //to put formatted time to input value
  var time1 = document.getElementById('time1');
  var time2 = document.getElementById('time2');
  time1.value = formattedTime1;
  time2.value = formattedTime2;
  const hourValid1 = parseInt(timeInput1.value.substring(0, 2));
  const minuteValid1 = parseInt(timeInput1.value.substring(3, 5));
  const hourValid2 = parseInt(timeInput2.value.substring(0, 2));
  const minuteValid2 = parseInt(timeInput2.value.substring(3, 5));
  const currentTime = new Date();
  if (interviewDate1.value === '') {
    setErrorInterviewCreate(interviewCreateError, 'Please insert interview date !!')
    return false;
  }
  if (timeInput1.value === '' || timeInput2.value === '') {
    setErrorInterviewCreate(interviewCreateError, 'Please insert interview time !!')
    return false;
  }
  if (timeInput1.value === timeInput2.value) {
    setErrorInterviewCreate(interviewCreateError, 'Interview time must be different !!')
    return false;
  }
  if (hourValid1 > 20 && minuteValid1 > 0 || hourValid1 < 9) {
    setErrorInterviewCreate(interviewCreateError, 'Interview Time must be between 9AM and 9PM !!')
    return false;
  }
  if (hourValid2 > 20 && minuteValid2 > 0 || hourValid2 < 9) {
    setErrorInterviewCreate(interviewCreateError, 'Interview Time must be between 9AM and 9PM !!')
    return false;
  }
  if (hourValid2 <= hourValid1 && minuteValid2 < minuteValid1) {
    setErrorInterviewCreate(interviewCreateError, 'Interview Time is invalid !!')
    return false;
  }
  if(interviewDate1.value ==minDate && currentTime.getHours()>20){
    setErrorInterviewCreate(interviewCreateError, 'Interview time have been passed for today!!');
    return false;
  }
  if (interviewDate1.value == minDate && hourValid1 <= currentTime.getHours() && minuteValid1 < currentTime.getMinutes()) {
    setErrorInterviewCreate(interviewCreateError, 'This time have been passed for today!!');
    return false;
  }
  if (interviewDate1.value == minDate && hourValid2 <= currentTime.getHours() && minuteValid2 < currentTime.getMinutes()) {
    setErrorInterviewCreate(interviewCreateError, 'This time have been passed for today!!');
    return false;
  }
  if (typeValue.value.trim() === '' || typeValue.value === '') {
    setErrorInterviewCreate(interviewCreateError, 'Please insert interview location !!')
    return false;
  }
  if (dropdownButton.innerText.includes('Choose')) {
    setErrorInterviewCreate(interviewCreateError, 'Choose interviewer !!')
    return false;
  }
  return true;

};

//for mail popUp
function togglePopupEmail() {
  popupDivEmail.classList.toggle("show");

}

function closePopUpEmail() {
  //to reset interview select
  interviewTypeSelect.options[0].setAttribute('selected', 'selected');
  var editor = tinymce.get('interviewMail');
  subject.value = null;
  toEmail.value = null;
  editor.setContent('');
  fileInput.value = '';
  fileCountLabel.innerHTML = '';
  popupDivEmail.classList.toggle("show");
}

//for reshow error when send mail
const setErrorMail = (element, message) => {
  element.innerHTML = message;
  setTimeout(() => {
    element.innerHTML = '';
  }, 4000);
}

function validateFromMail() {
  if (fromEmail.value.length == 0) {
    setErrorMail(fromMailError, 'Mail is required !!');
    return false;
  }
  return true;
}

function validateToMail() {
  if (toEmail.value.length == 0) {
    setErrorMail(toMailError, 'Candidate mail is requierd !!')
    return false;
  }
  return true;
}

function validateSubject() {
  if (subject.value === null || subject.value === '') {
    setErrorMail(subjectError, 'Email subject name is required !!')
    return false;
  }
  if (subject.value.length > 200) {
    setErrorMail(subjectError, 'Email subject name must be within 200 characters !!');
    return false;
  }
  return true;
}

//validation function for interview send mail
function validateSendInterviewMail() {
  console.log('hihi')
  if (!validateFromMail() || !validateToMail()) {
    return false;
  }
  if (!fromEmail.value.match(/^[A-Za-z]*[0-9]*[@][A-Za-z]*[\.][a-z]{2,4}$/)) {
    setErrorMail(fromMailError, 'Invalid Email !!')
    return false;
  }
  if (!toEmail.value.match(/^[A-Za-z]*[0-9]*[@][A-Za-z]*[\.][a-z]{2,4}$/)) {
    setErrorMail(toMailError, 'Invalid Email !!')
    return false;
  }
  if (!validateSubject()) {
    return false;
  }
  var editor = tinymce.get('interviewMail');
  var parser = new DOMParser();
  var htmlDoc = parser.parseFromString(editor.getContent(), 'text/html');
  var textContent = htmlDoc.body.textContent;
  if (editor.getContent() === null || textContent.trim() === '') {
    setErrorMail(emailMessageError, 'Please insert email message !!')
    return false;
  }
  if (fileInput.value.length < 1) {
    attachments.value = 'Null';
  }
  else {
    attachments.value = 'NotNull';
  }
  var files = fileInput.files;
  for (var i = 0; i < files.length; i++) {
    var fileSize = Math.round(files[i].size / 1024);
    if (fileSize > 1024 * 10) {
      // alert('File ' + files[i].name + ' is greater than 2MB');
      setErrorMail(attachmentFileError, 'File ' + files[i].name + ' is greater than 10MB')
      return false;
    }
  }
  $("#loadingAnimation").css("display", "flex");
  return true;
}

//to show attachment file count
document.addEventListener("DOMContentLoaded", function () {
  var fileInput = document.getElementById("fileInput");
  var fileCountLabel = document.getElementById("fileCountLabel");

  fileInput.addEventListener("change", function () {
    var selectedFiles = fileInput.files;
    fileCountLabel.textContent = selectedFiles.length + " files selected";
  });
});

//to reshow pop up when have error 
var notSendEmail = document.getElementById('notSendEmail');
if (notSendEmail.innerHTML !== null && notSendEmail.innerHTML.trim() !== '') {
  // Show the popup when the value is null or empty
  var popupDiv = document.getElementById("popupDiv");
  popupDiv.classList.add("show");
  setTimeout(function () {
    notSendEmail.innerHTML = null
  }, 4000);
}

//show alert box when mail exception have
if (mailException != null) {
  togglePopupDanger("Mail", mailException);
}
if (mailSuccess != null) {
  togglePopupSuccess("Mail", mailSuccess);
}


