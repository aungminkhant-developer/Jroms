const USER_MANAGEMENT_EXPAND = "user-management-expand";
const JOB_OFFER_EXPAND = "job-offer-expand";
const OTHERS_EXPAND = "others-expand";
const mainContentElement = document.querySelector(".main-content");
const mainHeaderElement = document.querySelector(".main-header");
var r = document.querySelector(':root');

function setToExpandedMaxWidth() {
    mainContentElement.style.setProperty("max-width", "calc(100vw - var(--expanded-sidebar-width))");
    mainHeaderElement.style.setProperty("max-width", "calc(100vw - var(--expanded-sidebar-width))");
}

function setToCollapsedMaxWidth() {
    mainContentElement.style.setProperty("max-width", "calc(100vw - var(--sidebar-width))");
    mainHeaderElement.style.setProperty("max-width", "calc(100vw - var(--sidebar-width))");
}

function styleActiveNavigation() {
    const url = window.location.href;

    document.querySelectorAll(".nav-item").forEach(el => {
        el.classList.remove("active");
    })

    let isActiveDone = false;
    const elementIds = ["dashboard", "allInterviews", "candidates", "locations", "departments", "miscellaneous", "reports"]
    elementIds.forEach(elementId => {
        if(url.includes(elementId)) {
            document.getElementById(elementId).classList.add("active");
            isActiveDone = true;
        }
    })

    if(isActiveDone) {
        // do nothing
    } else if(url.includes("users/add")) {
        document.getElementById("add-users").classList.add("active");
    } else if (url.includes("users/viewUser")) {
        document.getElementById("view-users").classList.add("active");
    } else if (url.includes("job-details/add")) {
        document.getElementById("add-offers").classList.add("active");
    } else if (url.includes("mng/jobs")) {
        document.getElementById("view-offers").classList.add("active");
    } else if (url.includes("mng/mail/create")) {
        document.getElementById("email-templates").classList.add("active");
    } else if (url.includes("mng/work-schedules")) {
        document.getElementById("work-schedules").classList.add("active");
    }
    
}

document.addEventListener('DOMContentLoaded', (e) => {
    // Reload group by statement
    if(localStorage.getItem("group-notification")) {
        document.getElementById("noti-group-by-toggle").checked = true;
    } else {
        document.getElementById("noti-group-by-toggle").checked = false;
    }

    // Reload the state from storage for sidebar
    if (!localStorage.getItem("expanded")) {
        document.querySelector("#navbar-title").classList.add("hide-element");
        localStorage.setItem("expanded", "false");
    } else {
        if (localStorage.getItem("expanded") === "true") {
            document.querySelector("#navbar-title").classList.remove("hide-element");
            document.querySelector("#main-sidebar").classList.add("sidebar-expanded");
            setToExpandedMaxWidth();
        } else {
            document.querySelector("#navbar-title").classList.add("hide-element");
            document.querySelector("#main-sidebar").classList.remove("sidebar-expanded");
            setToCollapsedMaxWidth();
        }
    }

    // Reload the state of collapsed menu
    if(localStorage.getItem(USER_MANAGEMENT_EXPAND) && document.getElementById("user-management-sublist") != null) {
        document.getElementById("user-management-sublist").classList.add("show");
        document.getElementById("user-management-sublist-icon").classList.remove("fa-caret-right");
        document.getElementById("user-management-sublist-icon").classList.add("fa-caret-down");
    }

    if(localStorage.getItem(JOB_OFFER_EXPAND)) {
        document.getElementById("job-offer-sublist").classList.add("show");
        document.getElementById("job-offer-sublist-icon").classList.remove("fa-caret-right");
        document.getElementById("job-offer-sublist-icon").classList.add("fa-caret-down");
    }

    if(localStorage.getItem(OTHERS_EXPAND) && document.getElementById("others-sublist") != null) {
        document.getElementById("others-sublist").classList.add("show");
        document.getElementById("others-sublist-icon").classList.remove("fa-caret-right");
        document.getElementById("others-sublist-icon").classList.add("fa-caret-down");
    }

    styleActiveNavigation();
})

document.getElementById("toggle-sidebar").onclick = (e) => {
    toggleSidebar();
}

// Change icon on collapse sub menu on sidebar

// User Management
const userManagement = document.getElementById("user-management-sublist");
const userManagementIcon = document.getElementById("user-management-sublist-icon");
if(userManagement != null && userManagementIcon != null) {
    userManagement.addEventListener("hide.bs.collapse", (e) => {
        localStorage.removeItem(USER_MANAGEMENT_EXPAND);
        if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
            e.preventDefault();
            toggleSidebar();
        } else {
            userManagementIcon.classList.remove("fa-caret-down");
            userManagementIcon.classList.add("fa-caret-right");
        }
    })
    
    userManagement.addEventListener("show.bs.collapse", (e) => {
        localStorage.setItem(USER_MANAGEMENT_EXPAND, true);
        if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
            toggleSidebar();
        }
        userManagementIcon.classList.remove("fa-caret-right");
        userManagementIcon.classList.add("fa-caret-down");
    })
    
}

// Job Offer
const jobOffer = document.getElementById("job-offer-sublist");
const jobOfferIcon = document.getElementById("job-offer-sublist-icon");
jobOffer.addEventListener("hide.bs.collapse", (e) => {
    localStorage.removeItem(JOB_OFFER_EXPAND);
    if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
        e.preventDefault();
        toggleSidebar();
    } else {
        jobOfferIcon.classList.remove("fa-caret-down");
        jobOfferIcon.classList.add("fa-caret-right");
    }
})

jobOffer.addEventListener("show.bs.collapse", (e) => {
    localStorage.setItem(JOB_OFFER_EXPAND, true);
    if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
        toggleSidebar();
    }
    jobOfferIcon.classList.remove("fa-caret-right");
    jobOfferIcon.classList.add("fa-caret-down");
})

// Job Offer
const others = document.getElementById("others-sublist");
const othersIcon = document.getElementById("others-sublist-icon");

if(others != null && othersIcon != null) {
    others.addEventListener("hide.bs.collapse", (e) => {
        localStorage.removeItem(OTHERS_EXPAND);
        if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
            e.preventDefault();
            toggleSidebar();
        } else {
            othersIcon.classList.remove("fa-caret-down");
            othersIcon.classList.add("fa-caret-right");
        }
    })
    
    others.addEventListener("show.bs.collapse", (e) => {
        localStorage.setItem(OTHERS_EXPAND, true);
        if (!document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
            toggleSidebar();
        }
        othersIcon.classList.remove("fa-caret-right");
        othersIcon.classList.add("fa-caret-down");
    })
}

function toggleSidebar() {
    document.querySelector("#main-sidebar").style.transition = "0.5s";
    mainContentElement.style.transition = "0.5s";
    mainHeaderElement.style.transition = "0.5s";
    document.querySelector("#main-sidebar").classList.toggle("sidebar-expanded");
    if (document.querySelector("#main-sidebar").classList.contains("sidebar-expanded")) {
        setToExpandedMaxWidth();
        document.querySelector("#navbar-title").classList.remove("hide-element");
        localStorage.setItem("expanded", "true");
    } else {
        setToCollapsedMaxWidth();
        document.querySelector("#navbar-title").classList.add("hide-element");
        localStorage.setItem("expanded", "false");
    }
}