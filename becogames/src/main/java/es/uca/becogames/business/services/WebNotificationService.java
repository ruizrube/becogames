package es.uca.becogames.business.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vaadin.flow.shared.Registration;

@Service
public class WebNotificationService {
	static Executor executor = Executors.newSingleThreadExecutor();

	static Map<String, Consumer<String>> listeners = new HashMap<>();

	public synchronized Registration register(String userName, Consumer<String> listener) {

		if (!listeners.containsKey(userName)) {
//			System.out.println("Registering (1st time) " + userName + " to broadcast service.. ["+ listener.toString()+"]");
		} else {
//			System.out.println("Registering " + userName + " to broadcast service.. ["+ listener.toString()+"]");
		}
		listeners.put(userName, listener);

		return () -> {
			synchronized (WebNotificationService.class) {

				if (listeners.containsKey(userName)) {
					listeners.remove(userName);
//					System.out.println("Removing from " + userName +" from broadcast service");
				}
			}
		};
	}

	public synchronized void broadcast(String userName, String message) {

		if (listeners.containsKey(userName)) {
//			System.out.println("Broadcasting... '"+ message + " ' to " + userName + " [" + listeners.get(userName) + "]");	

			try {
				executor.execute(() -> listeners.get(userName).accept(message));

			} catch (Exception ex) {
				listeners.remove(userName);
			}
		}

	}

	public String getUsers() {
		return listeners.keySet().stream().collect(Collectors.joining(","));
	}
}