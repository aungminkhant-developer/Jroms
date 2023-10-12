let stompClient = null;

// initialize stompClient
let socket = new SockJS("/ws");
stompClient = Stomp.over(socket);

function sendMessage(message, client, type) {
  if (type == "interview") {
    client.send("/app/private", {}, message);
  } else {
    client.send("/app/applicants", {}, message);
  }
}

document.addEventListener("DOMContentLoaded", (e) => {
  stompClient.connect({}, function (frame) {
    // when dom is ready, test if there is a notification that needs to be sent
    const notiMsg = document.getElementById("notification-message");
    const message = notiMsg.dataset.noti;
    const type = notiMsg.dataset.type;
    if (message) {
      sendMessage(message, stompClient, type);
    }
  });
});
