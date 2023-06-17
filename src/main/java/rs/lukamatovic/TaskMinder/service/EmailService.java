package rs.lukamatovic.TaskMinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String receiver, String password) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(receiver);
        message.setSubject("TaskMinder: Password reset request");
        message.setText("Hello,\n\n"
        		+ "Someone requested password request.\n"
        		+ "To login to system use your username and this password: " + password
        		+ "\n\nThanks for using TaskMinder App");

        mailSender.send(message);
	}
}
