$(document).ready(function () {
    // Function to open the confirmation modal
    function openConfirmationModal(name, role) {
        $(".userNamePlaceholder").text(name);
        $("#userRolePlaceholder").text(role);
        $("#confirmationModal").fadeIn();
    }

    // Function to close the confirmation modal
    function closeConfirmationModal() {
        $("#confirmationModal").fadeOut();
        $("#loadingAnimation").css("display", "none");
    }

    // Close the modal if user clicks the "No" button or the close button
    $("#cancelButtonModal").on("click", function () {
        closeConfirmationModal();
    });

    $(document).on("click", function (event) {
        var confirmationModal = $("#confirmationModal");
        if ($(event.target).is(confirmationModal)) {
            closeConfirmationModal();
        }
    });

    // Confirm User Creation button click event
    $("#confirmUserCreation").on("click", function () {
        // Show the loading animation
        $("#loadingAnimation").css("display", "flex");
        // Submit the form once the user confirms
        $("form").submit();
    });


    const oldName = $("#name").val();
    const oldUsername = $("#username").val();
    const oldRole = $("#role").val();
    const oldEmail = $("#email").val();
   const oldPassword = $("#inputPassword").val().trim();


    console.log(oldName + " " + oldUsername + " " + oldRole + " " + oldEmail + " " + oldPassword);
    

    // Function to validate the form on submission
    $("#submitFormBtn").on("click", function () {
        // Clear previous error messages
        $(".error-display").text("");

        // Check each input field for validation
        var isValid = true;
        var areDetailsSame = true;

        // Name validation
        var name = $("#name").val();
        if(oldName !== name){
            areDetailsSame = false;
        }
        if (name.trim() === "") {
            $("#name-error").text("Name is required.");
            isValid = false;
        } else if (!/^[a-zA-Z\s]+$/.test(name)) {
            $("#name-error").text(
                "Name should only contain letters and spaces."
            );
            isValid = false;
        } else if (/\d/.test(name)) {
            $("#name-error").text("Name should not contain numbers.");
            isValid = false;
        } else {
            $("#name-error").text(""); // Clear the error message if the name is valid
        }

        // Username validation
        var username = $("#username").val();
        if(oldUsername !== username){
            areDetailsSame = false;
        }
        if (username.trim() === "") {
            $("#username-error").text("Username is required.");
            isValid = false;
        }

        // Role validation
        var role = $("#role").val();
        if (role === null) {
            $("#role-error").text("Role is required.");
            isValid = false;
        }

        //email validation
        var email = $("#email").val();
        if(oldEmail !== email){
            areDetailsSame = false;
        }
        if (email.trim() === "") {
            $("#email-error").text("Email is required.");
            isValid = false;
        } else {
            // Email regex pattern
            var emailRegex =
                /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
            if (!emailRegex.test(email)) {
                $("#email-error").text("Invalid email address.");
                isValid = false;
            }
        }

        var role = $("#role").val();
        if(oldRole !== role){
            areDetailsSame = false;
        }

        // Password validation
var password = $("#inputPassword").val();
if(oldPassword !== password){
    areDetailsSame = false;
}
if (password.trim() !== "") {
    // Check password length
    if (password.length < 8 || password.length > 20) {
        $("#password-error").text(
            "Password must be 8-20 characters long."
        );
        isValid = false;
    }

    // Check for at least one uppercase letter
    var uppercaseRegex = /[A-Z]/;
    if (!uppercaseRegex.test(password)) {
        $("#password-error").text(
            "Password must contain at least one uppercase letter."
        );
        isValid = false;
    }

    // Check for at least one lowercase letter
    var lowercaseRegex = /[a-z]/;
    if (!lowercaseRegex.test(password)) {
        $("#password-error").text(
            "Password must contain at least one lowercase letter."
        );
        isValid = false;
    }
}

// Confirm Password validation
var confirmPassword = $("#confirmPassword").val();
if (confirmPassword.trim() !== "" && password !== confirmPassword) {
    $("#confirmPassword-error").text("Passwords do not match.");
    isValid = false;
}

if(areDetailsSame){
    togglePopupWarning("Warning", "The user details remain the same and no update is needed.")
}

        // If all fields are valid, submit the form
        else if (isValid) {
            var name = $("#name").val();
            var role = $("#role option:selected").text();
            openConfirmationModal(name, role);
        }
    });

    // Function to clear validation messages when Name input field is focused
    $("#name").on("focus", function () {
        $("#name-error").text("");
    });

    // Function to clear validation messages when Username input field is focused
    $("#username").on("focus", function () {
        $("#username-error").text("");
    });

    // Function to clear validation messages when Role select field is focused
    $("#role").on("focus", function () {
        $("#role-error").text("");
    });

    // Function to clear validation messages when Email input field is focused
    $("#email").on("focus", function () {
        $("#email-error").text("");
    });

    // Function to clear validation messages when Password input field is focused
    $("#inputPassword").on("focus", function () {
        $("#password-error").text("");
    });

    // Function to clear validation messages when Confirm Password input field is focused
    $("#confirmPassword").on("focus", function () {
        $("#confirmPassword-error").text("");
    });
});