package epfl.sweng.events;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

/**
 * Un ������metteur d'������v������nement. Cette classe fourni les m������thodes de bases pour
 * associer des EventListeners ������ un ������metteur et g������rer le processus
 * d'������mission.
 * 
 * Cette version basique d'EventEmitter utilise une ������mission synchrone. Le
 * thread ������metteur est bloqu������ tant que la distribution dun ������v������nement n'est
 * pas termin������e.
 */
public abstract class EventEmitter implements EventEmitterInterface {
	/**
	 * La liste des EventListeners associ������s ������ cet ������metteur.
	 */
	private Set<EventListener> mListeners = new HashSet<EventListener>();
	
	private static final String TAG = "EventEmitter";

	protected Set<EventListener> getListeners() {
		return this.mListeners;
	}

	/**
	 * L'objet qui sera associ������ comme ������metteur pour les ������v������nements ������mis par
	 * cet ������metteur. Ceci permet, avec la classe EventEmitterInterface,
	 * d'������muler le comportement d'un EventListener complet dans une classe qui
	 * ne pourrait pas en h������riter.
	 */

	private EventEmitterInterface mEmitter;

	protected EventEmitterInterface getEmitter() {
		return this.mEmitter;
	}

	/**
	 * Cr������e un nouveau ������metteur d'������v������nement.
	 */
	public EventEmitter() {
		this(null);
	}

	/**
	 * Cr������e un nouveau ������metteur d'������v������nement ������mettant des ������v������nements pour
	 * le compte d'un autre ������m������teur ou pseudo-������metteur.
	 * 
	 * @param emitter
	 *            L'������metteur ������ utiliser comme origine pour les ������v������nements
	 *            ������mis. Par d������faut soi-m������me si null.
	 */
	public EventEmitter(EventEmitterInterface emitter) {
		this.mEmitter = emitter != null ? emitter : this;
	}

	/**
	 * Ajoute un gestionnaire aux ������v������nements de cet ������m������tteur.
	 */
	public synchronized void addListener(EventListener listener) {
		if (listener != null) {
			mListeners.add(listener);
		}
	}

	/**
	 * Retire un gestionnaire de cet ������metteur.
	 */
	public synchronized void removeListener(EventListener listener) {
		mListeners.remove(listener);
	}

	/**
	 * Emet un ������v������nement.
	 * 
	 * @throws UnhandledEventException
	 */
	public synchronized void emit(Event event) {
		for (EventListener listener : mListeners) {
			try {
				event.trigger(listener, mEmitter);
			} catch (InvocationTargetException e) {
				System.err.println("'on' methods shouldn't throw exception");
				Log.d(TAG, "'on' methods shouldn't throw exception");
			}
		}
	}
}
