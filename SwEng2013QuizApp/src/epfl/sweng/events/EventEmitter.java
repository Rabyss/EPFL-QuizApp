package epfl.sweng.events;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

/**
 * Un émetteur d'événement. Cette classe fourni les méthodes de bases pour
 * associer des EventListeners é un émetteur et gérer le processus
 * d'émission.
 * 
 * Cette version basique d'EventEmitter utilise une émission synchrone. Le
 * thread émetteur est bloqué tant que la distribution dun événement n'est
 * pas terminée.
 */
public abstract class EventEmitter implements EventEmitterInterface {
    /**
     * La liste des EventListeners associés é cet émetteur.
     */
    private Set<EventListener> mListeners = new HashSet<EventListener>();
    
    private static final String TAG = "EventEmitter";

    protected Set<EventListener> getListeners() {
        return this.mListeners;
    }

    /**
     * L'objet qui sera associé comme émetteur pour les événements émis par
     * cet émetteur. Ceci permet, avec la classe EventEmitterInterface,
     * d'émuler le comportement d'un EventListener complet dans une classe qui
     * ne pourrait pas en hériter.
     */

    private EventEmitterInterface mEmitter;

    protected EventEmitterInterface getEmitter() {
        return this.mEmitter;
    }

    /**
     * Crée un nouveau émetteur d'événement.
     */
    public EventEmitter() {
        this(null);
    }

    /**
     * Crée un nouveau émetteur d'événement émettant des événements pour
     * le compte d'un autre éméteur ou pseudo-émetteur.
     * 
     * @param emitter
     *            L'émetteur é utiliser comme origine pour les événements
     *            émis. Par défaut soi-méme si null.
     */
    public EventEmitter(EventEmitterInterface emitter) {
        this.mEmitter = emitter != null ? emitter : this;
    }

    /**
     * Ajoute un gestionnaire aux événements de cet émétteur.
     */
    public synchronized void addListener(EventListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    /**
     * Retire un gestionnaire de cet émetteur.
     */
    public synchronized void removeListener(EventListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Emet un événement.
     * 
     * @throws UnhandledEventException
     */
    public synchronized void emit(Event event) {
        for (EventListener listener : mListeners) {
            try {
                event.trigger(listener, mEmitter);
            } catch (InvocationTargetException e) {
                Log.d(TAG, "'on' methods shouldn't throw exception", e);
            }
        }
    }
}
