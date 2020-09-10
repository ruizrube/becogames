/**
 * 
 */
package es.uca.becogames.presentation.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import es.uca.becogames.business.entities.User;
import es.uca.becogames.business.services.WebNotificationService;
import es.uca.becogames.business.services.UserService;
import es.uca.becogames.presentation.pages.SignInPage;
import es.uca.becogames.security.SecurityUtils;

/**
 * @author ivanruizrube
 *
 */
public abstract class AbstractView extends VerticalLayout implements BeforeEnterObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9068306256698865110L;

	User currentUser;

	Registration broadcasterRegistration;

	UserService userService;

	WebNotificationService broadcastingService;

	// protected BrowserNotifications browserNotifications;

	public AbstractView(UserService userService, WebNotificationService broadcastingService) {
		this.userService = userService;
		this.broadcastingService = broadcastingService;
		this.setJustifyContentMode(JustifyContentMode.AROUND);
		this.setAlignItems(Alignment.CENTER);
		this.setWidth("100%");

		currentUser = UI.getCurrent().getSession().getAttribute(User.class);

	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		currentUser = UI.getCurrent().getSession().getAttribute(User.class);

		UI ui = attachEvent.getUI();

		if (currentUser != null) {
			broadcasterRegistration = broadcastingService.register(currentUser.getUsername(), msg -> {
				ui.access(() -> {
					this.enter();

					Notification.show((msg));
					try {
						ui.push();
					} catch (java.lang.IllegalStateException ex) {
						;
					}

				});
			});
			VaadinSession.getCurrent().setAttribute(Registration.class, broadcasterRegistration);
		}

	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		// broadcasterRegistration.remove();
		// broadcasterRegistration = null;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {

		VaadinSession.getCurrent().setAttribute("intendedPath", event.getLocation().getPath());

		final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget());
		if (!accessGranted) {
			if (SecurityUtils.isUserLoggedIn()) {
				event.rerouteToError(RuntimeException.class, "Not authorised");
			} else {
				event.rerouteTo(SignInPage.class);
			}
		} else {
			enter();
		}

		// browserNotifications = BrowserNotifications.extend(UI.getCurrent());

//				if (this.currentUser.isSubscription()) {
//				browserNotifications.showNotification(msg);
//			} else {
//			notifications.queryIfSupported().thenAccept(this::acceptSupportState)
//			.thenCompose(aVoid -> notifications.queryPermissionLevel()).thenCompose(this::acceptPermissionLevel)
//			.thenCompose(ev -> notifications.askForPermission())
//			.thenAccept(this::conditionallyShowNotifierButton);
//			}

	}

	abstract protected void enter();

}
