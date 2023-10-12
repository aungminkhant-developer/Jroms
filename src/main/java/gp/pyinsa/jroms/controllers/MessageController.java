package gp.pyinsa.jroms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import gp.pyinsa.jroms.dtos.NotificationDto;

@Controller
public class MessageController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/applicants")
    @SendTo("/all/messages/applicants")
    public NotificationDto sendApplicationNotifications(final NotificationDto notificationDto) throws Exception {
        return notificationDto;
    }

    @MessageMapping("/interviews")
    @SendTo("/all/messages/interviews")
    public NotificationDto sendInterviewNotifications(final NotificationDto notificationDto) throws Exception {
        return notificationDto;
    }

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload NotificationDto message) {
        System.out.println("Private: " + message);
        simpMessagingTemplate.convertAndSendToUser(message.getUsernames().get(0), "/specific", message);
        // for (String username : message.getUsernames()) {
        //     simpMessagingTemplate.convertAndSendToUser(username, "/specific", message);
        // }
    }

}
