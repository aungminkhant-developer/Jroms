let warningInterval;
let dangerInterval;
let successInterval;
var warningHeader = document.getElementById('warningHeader');
var warningParagraph = document.getElementById('warningParagraph');
var dangerHeader=document.getElementById('dangerHeader');
var dangerParagraph=document.getElementById('dangerParagraph');
var successHeader=document.getElementById('successHeader');
var successParagraph=document.getElementById('successParagraph');

//warning alert
function togglePopupWarning(header, paragraph) {
    var popupDiv = document.getElementById("warningPopupDiv");
    warningHeader.innerHTML = header;
    warningParagraph.innerHTML = paragraph;
    popupDiv.classList.toggle("show");
    if (popupDiv.classList.contains("show")) {
        startWarningTimer(3000); // Start the timer with 3000 milliseconds (3 seconds)
    } else {
        resetWarningProgressBar();
        clearInterval(warningInterval);
    }
}

function closeWarningPopup() {
    var popupDiv = document.getElementById("warningPopupDiv");
    popupDiv.classList.remove("show");
    resetWarningProgressBar();
    clearInterval(warningInterval);
}

function startWarningTimer(duration) {
    var progressBar = document.getElementById("warningTimerProgress");
    var width = 0;
    var increment = 100 / (duration / 10); // Update the progress bar every 10 milliseconds

    warningInterval = setInterval(function () {
        if (width >= 100) {
            clearInterval(warningInterval);
            closeWarningPopup();
        } else {
            width += increment;
            progressBar.style.width = width + "%";
        }
    }, 10);
}

function resetWarningProgressBar() {
    var progressBar = document.getElementById("warningTimerProgress");
    progressBar.style.width = "0";
}

// Danger alert
function togglePopupDanger(header,paragraph) {
    var popupDiv = document.getElementById("dangerPopupDiv");
    dangerHeader.innerHTML = header;
    dangerParagraph.innerHTML = paragraph;
    popupDiv.classList.toggle("show");
    if (popupDiv.classList.contains("show")) {
        startDangerTimer(3000); // Start the timer with 3000 milliseconds (3 seconds)
    } else {
        resetDangerProgressBar();
        clearInterval(dangerInterval);
    }
}

function closeDangerPopup() {
    var popupDiv = document.getElementById("dangerPopupDiv");
    popupDiv.classList.remove("show");
    resetDangerProgressBar();
    clearInterval(dangerInterval);
}

function startDangerTimer(duration) {
    var progressBar = document.getElementById("dangerTimerProgress");
    var width = 0;
    var increment = 100 / (duration / 10); // Update the progress bar every 10 milliseconds

    dangerInterval = setInterval(function () {
        if (width >= 100) {
            clearInterval(dangerInterval);
            closeDangerPopup();
        } else {
            width += increment;
            progressBar.style.width = width + "%";
        }
    }, 10);
}

function resetDangerProgressBar() {
    var progressBar = document.getElementById("dangerTimerProgress");
    progressBar.style.width = "0";
}

// success alert
function togglePopupSuccess(header,paragraph) {
    var popupDiv = document.getElementById("successPopupDiv");
    successHeader.innerHTML = header;
    successParagraph.innerHTML = paragraph;
    popupDiv.classList.toggle("show");
    if (popupDiv.classList.contains("show")) {
        startSuccessTimer(3000); // Start the timer with 3000 milliseconds (3 seconds)
    } else {
        resetSuccessProgressBar();
        clearInterval(successInterval);
    }
}

function closeSuccessPopup() {
    var popupDiv = document.getElementById("successPopupDiv");
    popupDiv.classList.remove("show");
    resetSuccessProgressBar();
    clearInterval(successInterval);
}

function startSuccessTimer(duration) {
    var progressBar = document.getElementById("successTimerProgress");
    var width = 0;
    var increment = 100 / (duration / 10); // Update the progress bar every 10 milliseconds

    successInterval = setInterval(function () {
        if (width >= 100) {
            clearInterval(successInterval);
            closeSuccessPopup();
        } else {
            width += increment;
            progressBar.style.width = width + "%";
        }
    }, 10);
}

function resetSuccessProgressBar() {
    var progressBar = document.getElementById("successTimerProgress");
    progressBar.style.width = "0";
}


