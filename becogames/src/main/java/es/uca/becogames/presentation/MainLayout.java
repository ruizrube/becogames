package es.uca.becogames.presentation;

import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.uca.becogames.business.entities.Game;
import es.uca.becogames.business.entities.Role;
import es.uca.becogames.presentation.pages.SignInPage;
import es.uca.becogames.presentation.pages.WelcomePage;
import es.uca.becogames.presentation.views.CommonGoodsGameView;
import es.uca.becogames.presentation.views.GameListView;
import es.uca.becogames.presentation.views.HomeView;
import es.uca.becogames.presentation.views.OrganizationManagementView;
import es.uca.becogames.presentation.views.ProfileView;
import es.uca.becogames.presentation.views.QuestionnaireManagementView;
import es.uca.becogames.presentation.views.UserManagementView;
import es.uca.becogames.security.SecurityUtils;

@JsModule("./styles/shared-styles.js")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@CssImport("./styles/shared-styles.css")
@PWA(name = "BEhaviour eCOnomics Games", shortName = "BECOGames", enableInstallPrompt = false, backgroundColor = "#227aef", themeColor = "#227aef", // ,
		offlinePath = "offline-page.html", offlineResources = { "images/offline-login-banner.jpg" })
@Push(PushMode.MANUAL)
@Theme(value = Lumo.class, variant = Lumo.DARK)

public class MainLayout extends AppLayout {

	Game runningGame = null;
	Tabs tabs;
	Tab runningGameTab;
	private H2 viewTitle;

	public MainLayout() {

		setPrimarySection(Section.NAVBAR);
		addToNavbar(true, createHeaderContent());
		tabs = createMenu();
		addToDrawer(createDrawerContent(tabs));
	}

	private Component createHeaderContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setId("header");
		layout.getThemeList().set("dark", true);
		layout.setWidthFull();
		layout.setSpacing(false);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.add(new DrawerToggle());
		viewTitle = new H2();
		layout.add(viewTitle);
		//layout.add(new Image("images/user.svg", "Avatar"));
		return layout;


	}
	
	private Component createDrawerContent(Tabs menu) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.getThemeList().set("spacing-s", true);
		layout.setAlignItems(FlexComponent.Alignment.STRETCH);
		layout.add(menu);
		
		return layout;
	}

	

	private Tabs createMenu() {
		final Tabs tabs = new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
		tabs.setId("tabs");

		Tab aux;
		
		if (SecurityUtils.isUserLoggedIn()) {

		
			aux = new Tab();
			aux.add(VaadinIcon.HOME.create());
			aux.add(new RouterLink("Home", HomeView.class));
			tabs.add(aux);

			aux = new Tab();
			aux.add(VaadinIcon.USER.create());
			aux.add(new RouterLink("Profile", ProfileView.class));
			tabs.add(aux);

			runningGameTab = new Tab();
			runningGameTab.add(VaadinIcon.GAMEPAD.create());
			runningGameTab.add(new RouterLink("Running Game", CommonGoodsGameView.class));
			tabs.add(runningGameTab);
			
			if (UI.getCurrent().getSession().getAttribute("idRunningGame") != null) {
				runningGameTab.setVisible(true);
			} else {
				runningGameTab.setVisible(false);

			}

			if (SecurityUtils.hasRole(Role.Admin.name())) {
				aux = new Tab();
				aux.add(VaadinIcon.FILE.create());
				aux.add(new RouterLink("Games", GameListView.class));
				tabs.add(aux);

			} else {
				aux = new Tab();
				aux.add(VaadinIcon.FILE.create());
				aux.add(new RouterLink("My games", GameListView.class));
				tabs.add(aux);

			}

			if (SecurityUtils.isAccessGranted(UserManagementView.class)) {
				aux = new Tab();
				aux.add(VaadinIcon.DATABASE.create());
				aux.add(new RouterLink("Users", UserManagementView.class));
				tabs.add(aux);

			}

			if (SecurityUtils.isAccessGranted(OrganizationManagementView.class)) {
				aux = new Tab();
				aux.add(VaadinIcon.DATABASE.create());
				aux.add(new RouterLink("Organizations", OrganizationManagementView.class));
				tabs.add(aux);

			}

			if (SecurityUtils.isAccessGranted(QuestionnaireManagementView.class)) {
				aux = new Tab();
				aux.add(VaadinIcon.DATABASE.create());
				aux.add(new RouterLink("Questionanaires", QuestionnaireManagementView.class));
				tabs.add(aux);

			}

			Button logout = new Button("Sign Out", e -> signOut());
			logout.setIcon(VaadinIcon.SIGN_OUT.create());
			tabs.add(logout);

		}

		return tabs;

	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		updateChrome();
		
		
		if (UI.getCurrent().getSession().getAttribute("idRunningGame") != null) {
			runningGameTab.setVisible(true);
		} else {
			runningGameTab.setVisible(false);

		}
	}

	private void updateChrome() {
		getTabWithCurrentRoute().ifPresent(tabs::setSelectedTab);
		viewTitle.setText(getCurrentPageTitle());
	}

	private Optional<Tab> getTabWithCurrentRoute() {
		String currentRoute = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
		return tabs.getChildren().filter(tab -> hasLink(tab, currentRoute)).findFirst().map(Tab.class::cast);
	}

	private boolean hasLink(Component tab, String currentRoute) {
		return tab.getChildren().filter(RouterLink.class::isInstance).map(RouterLink.class::cast)
				.map(RouterLink::getHref).anyMatch(currentRoute::equals);
	}

	private String getCurrentPageTitle() {
		return getContent().getClass().getAnnotation(PageTitle.class).value();
	}

	private void signOut() {

		// Remove from broadcaster
		VaadinSession.getCurrent().getAttribute(Registration.class).remove();

		// Redirect this page immediately
		UI.getCurrent().getPage().executeJavaScript("location.assign('logout')");

		// Close the session
		UI.getCurrent().getSession().close();

		// Notice quickly if other UIs are closed
		UI.getCurrent().setPollInterval(3000);
		
		UI.getCurrent().navigate(WelcomePage.class);

	}

}
