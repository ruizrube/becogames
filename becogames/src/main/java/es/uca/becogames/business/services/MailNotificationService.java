package es.uca.becogames.business.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.User;

@Service
public class MailNotificationService {

	@Autowired
	public JavaMailSender emailSender;

	@Value("${SERVER_URL}")
	public String server;

	@Value("${MAIL_MAIN}")
	public String mainMailAccount;

	@Value("${MAIL_HEADER}")
	public String mailEnvironment;

	static final int BUFFER_SIZE = 6; // for example, adjust it to you needs

	static final int CCO_SIZE = 8;

	MimeMessage[] messages = new MimeMessage[BUFFER_SIZE];

	static int messageIndex = 0;

	public void sendMailCreateUser(User target) {
		String clickBackURL = "Click <a href='" + server + "/usermanagement" + "'>" + "here</a> to ";
		String to = mainMailAccount;
		String subject = "[" + mailEnvironment + "] New user registered!";
		String text = "The user " + target.getFirstName() + " " + target.getLastName()
				+ " has been registered and confirmed in the system. " + clickBackURL + "check it.";

		this.dispatchMails(to, "BECOGAMES", null, subject, text);
	}

	public void sendMailConfirmUser(User target, String token) {
		String clickBackURL = "Click <a href='" + server + "/confirmuser?userId=" + target.getId() + "&token=" + token
				+ "'>" + "here</a> to ";
		String to = target.getMail();
		String subject = "[" + mailEnvironment + "] User creation confirmation";
		String text = "Your account has been sucessfully created. " + clickBackURL + "activate your account.";

		this.dispatchMails(to, target.getFirstName(), null, subject, text);
	}

	public void sendMailRecoverPassword(User target, String token) {
		String clickBackURL = "Click <a href='" + server + "/passwordrecovery?userId=" + target.getId() + "&token="
				+ token + "'>" + "here</a> to ";
		String to = target.getMail();
		String subject = "[" + mailEnvironment + "] Password recovery";
		String text = "Remember that your username is: " + target.getUsername() + ". " + clickBackURL
				+ "reset your password.";

		this.dispatchMails(to, target.getFirstName(), null, subject, text);

	}

	public void sendMailDeactivateUser(User target) {
		String to = target.getMail();
		String subject = "[" + mailEnvironment + "] User deactivation confirmation";
		String text = "Your account has been sucessfully deactivated. ";

		this.dispatchMails(to, target.getFirstName(), null, subject, text);
	}

	public void sendMailInvitateUsers(Game game) {

		String clickBackURL = "Click <a href='" + server + "/game/" + game.getId() + "'>" + "here</a> to ";
		String to = mainMailAccount;
		String subject = "[" + mailEnvironment + "] Invitation to " + game.toString() + " #" + game.getId();
		String text = "User " + game.getOwner().getUsername() + " has invited you to join " + game.toString() + ". "
				+ clickBackURL + "join the game.";

		String[] targets = game.getInvitedPlayers().stream().map(p -> p.getUser().getMail()).toArray(String[]::new);

		if (targets.length > 0) {
			this.dispatchMails(to, null, targets, subject, text);
		}

	}

	public void sendMailGameRunning(Game game) {

		String clickBackURL = "Click <a href='" + server + "/game/" + game.getId() + "'>" + "here</a> to ";
		String to = game.getOwner().getMail();
		String subject = "[" + mailEnvironment + "] " + game.toString() + " #" + game.getId() + " is already running";
		String text = "The game is now runnning. " + clickBackURL + "play the game.";

		this.dispatchMails(to, null,
				game.getJoinedPlayers().stream().map(p -> p.getUser().getMail()).toArray(String[]::new), subject, text);

	}

	public void sendMailGameEveryBodyRespondedInvitations(Game game) {
		User target = game.getOwner();
		String clickBackURL = "Click <a href='" + server + "/game/" + game.getId() + "'>" + "here</a> to ";
		String to = target.getMail();
		String subject = "[" + mailEnvironment + "] " + game.toString() + " #" + game.getId()
				+ " does not have invitations to be responded";
		String text = "There are no pending invitations to manage. All the invited users have responded to the invitations to join the game. "
				+ clickBackURL + "open the game.";

		this.dispatchMails(to, target.getFirstName(), null, subject, text);

	}

	public void sendMailGameEverybodyInvested(Game game) {
		User target = game.getOwner();
		String clickBackURL = "Click <a href='" + server + "/game/" + game.getId() + "'>" + "here</a> to ";
		String to = target.getMail();
		String subject = "[" + mailEnvironment + "] " + game.toString() + " #" + game.getId()
				+ " has collected all the investments";
		String text = "There are no pending invitations to manage and all the joined users have invested in the game. Thus, the game could be prepared to be resolved. "
				+ clickBackURL + "open the game.";

		this.dispatchMails(to, target.getFirstName(), null, subject, text);

	}

	public void sendMailGameResolved(Game game) {
		String clickBackURL = "Click <a href='" + server + "/game/" + game.getId() + "'>" + "here</a> to ";
		String to = game.getOwner().getMail();
		String subject = "[" + mailEnvironment + "] Results of " + game.toString() + " #" + game.getId();
		String text = "The results of your game are already available. " + clickBackURL
				+ "find out the benefits obtained.";

		this.dispatchMails(to, null,
				game.getJoinedPlayers().stream().map(p -> p.getUser().getMail()).toArray(String[]::new), subject, text);

	}

	synchronized private void dispatchMails(String to, String firstName, String[] targets, String subject,
			String text) {

		messageIndex = 0;

		if (to != null) {

			if (targets != null) { // ENVIO MASSIVO CON CCO

				enqueueMailWithCC(to, targets, subject, text);

			} else { // ENVIO SIMPLE SIN CCO

				enqueueMail(to, " " + firstName, null, subject, text);

			}

			sendMailsFromBuffer();
		}

	}

	protected void enqueueMail(String to, String firstName, String[] bcc, String subject, String text) {

		MimeMessage mailMsg = emailSender.createMimeMessage();
		MimeMessageHelper helper;

		// preparacion del correo
		try {
			helper = new MimeMessageHelper(mailMsg, true, "UTF-8");

			helper.setFrom(new InternetAddress(mainMailAccount, "BECO Games"));

			helper.setTo(to);

			if (bcc != null && bcc.length > 0) {
				helper.setBcc(bcc);
			}

			helper.setSubject(subject);

			String preMessage = "Hi" + firstName + "!<br><br>";
			String postMessage = "<br><br><br> Kind regards." + "<br><br>The team of BECOGAMES"
					+ "<br><br><br><br><br>";

			String appendix = "<i><small>You receive these emails because you gave us your consent. If you are not interested anymore, you can revoke it ";
			appendix += "<a href='" + server + "/profile/" + "'>" + "here</a>.</small></i>";

			helper.setText(preMessage + text + postMessage + appendix, true);

		} catch (MessagingException e) {
			e.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		// metemos mail en el buffer
		messages[messageIndex++] = helper.getMimeMessage();

		// si llenamos el buffer, lo mandamos
		if (messageIndex == BUFFER_SIZE) {

			sendMailsFromBuffer();

		}


	}

	

	private void enqueueMailWithCC(String to, String[] bcc, String subject, String text) {
		int idCCO = 0;

		List<String> bccQueue = new ArrayList<String>();
		for (int idMail = 0; idMail < bcc.length; idMail++) {
			if (idCCO < CCO_SIZE) {
				bccQueue.add(bcc[idMail]);
				idCCO++;
			} else {
				enqueueMail(to, "", bccQueue.toArray(new String[bccQueue.size()]), subject, text);
				bccQueue = new ArrayList<String>();
				bccQueue.add(bcc[idMail]);
				idCCO = 1;
			}
		}

		if (bccQueue.size() > 0) {
			enqueueMail(to, "", bccQueue.toArray(new String[bccQueue.size()]), subject, text);
		}
	}

	
	protected void sendMailsFromBuffer() {

		if (messageIndex > 0) {

			MimeMessage[] lastMessages = new MimeMessage[messageIndex];
			for (int i = 0; i < messageIndex; i++) {
				lastMessages[i] = messages[i];
			}

			imprimeLote(lastMessages);
			
			try {
				emailSender.send(lastMessages);
				System.out.println(">>>Enviados");
				
			}catch(Exception e) {
				System.out.println(">>>NO ENVIADOS");
				e.printStackTrace();
			} finally {
				messageIndex = 0;
				messages = new MimeMessage[BUFFER_SIZE];				
			}


		}
	}
	
	protected void imprimeLote(MimeMessage[] messages) {
		try {
			int i = 1;

			for (MimeMessage message : messages) {
				if (i == 1) {
					System.out.print(">>>Sending batch of mails: " + message.getSubject());
				}
				i++;
				System.out.print("\n>>>>> Recipients: ");
				for (Address address : message.getAllRecipients()) {
					System.out.print(address.toString() + ", ");
				}

			}

			System.out.print("\n");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
