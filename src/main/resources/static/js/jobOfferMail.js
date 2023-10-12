 //for mail popUp
 function togglePopupEmail() {
    popupDivEmail.classList.toggle("show");

}
var subject = document.getElementById('subject');
var sendMailCandidateId = document.getElementById('sendMailCandidateId');
var pageRedirect = document.getElementById('pageRedirect');
var messageError = document.getElementById('emailMessageError');
var checkInterviewResultModal = new bootstrap.Modal(document.getElementById("check-interview-result-modal"));
var confirmInterviewResultModal = new bootstrap.Modal(document.getElementById("confirm-interview-result-modal"));
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



function closePopUpEmail() {
    var editor = tinymce.get('interviewMail');
    subject.value = null;
    toEmail.value = null;
    editor.setContent('');
    fileInput.value = '';
    fileCountLabel.innerHTML = '';
    popupDivEmail.classList.toggle("show");
}

//for reshow error when send mail
var setErrorMail = (element, message) => {
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
    var htmlDoc = parser.parseFromString(editor.getContent(), "text/html");
    var textContent1 = htmlDoc.body.textContent;
    if (textContent1 === '' || textContent1.trim() === '') {
        console.log('hihihhih')
        setErrorMail(messageError, 'Please insert email message !!')
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