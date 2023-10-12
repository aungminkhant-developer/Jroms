//for email pop up

		// document.addEventListener("DOMContentLoaded", function () {
		// 	var popupDiv = document.getElementById("popupDiv");
		// 	// Remove the "show" class to hide the div by default
		// 	popupDiv.classList.add("show");
		// });

		function togglePopup() {
			var popupDiv = document.getElementById("popupDiv");
			popupDiv.classList.toggle("show");
		}

		//to reshow pop up when have error 
		var notSendEmail = document.getElementById('notSendEmail');
		if (notSendEmail.innerHTML !== null && notSendEmail.innerHTML.trim() !== '') {
			// Show the popup when the value is null or empty
			var popupDiv = document.getElementById("popupDiv");
			popupDiv.classList.add("show");
			setTimeout(function(){
				notSendEmail.innerHTML=null
			},4000);
		  }

		document.addEventListener("DOMContentLoaded", function () {
			var fileInput = document.getElementById("fileInput");
			var fileCountLabel = document.getElementById("fileCountLabel");

			fileInput.addEventListener("change", function () {
				var selectedFiles = fileInput.files;
				fileCountLabel.textContent = selectedFiles.length + " files selected";
			});
		});