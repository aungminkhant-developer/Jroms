let stompClient = null;

// Subscribe public notification
let socket = new SockJS("/ws");
stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
  console.log(frame);
  stompClient.subscribe("/all/messages", function (result) {
    showNotification(JSON.parse(result.body));
  });
});

function sendMessage() {
  stompClient.send(
    "/app/application",
    {},
    JSON.stringify({ text: "This is test message" })
  );
}

function showNotification(message) {
  let msgContainer = document.getElementById("notification-dropdown");
  const p = document.createElement("p");
  p.innerHTML = "message: " + message.text;
  msgContainer.appendChild(p);
}
