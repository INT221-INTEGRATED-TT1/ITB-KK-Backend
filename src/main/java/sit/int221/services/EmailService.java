package sit.int221.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.mail.from-email}")
    private String fromEmailId;

    @Value("${app.mail.sender-display-name}")
    private String senderName;

    public void sendEmail(String collaboratorEmail, String inviterName, String accessRight, String boardName, String boardId){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(collaboratorEmail);
        simpleMailMessage.setSubject(inviterName + " has invited you to collaborate with "
                + accessRight + " access right on " + boardName + " board");
        simpleMailMessage.setReplyTo("DO NOT REPLY <noreply@intproj23.sit.kmutt.ac.th>");
        simpleMailMessage.setFrom(senderName + " <" + fromEmailId + ">");

        String invitationLink = "http://intproj23.sit.kmutt.ac.th/tt1/board/" + boardId + "/collab/invitations";

        simpleMailMessage.setText(inviterName + " has invited you to collaborate with " + accessRight + " access right on " +
                boardName + " board with the link to " + invitationLink + " page");


        javaMailSender.send(simpleMailMessage);
    }
}
