package epfl.sweng.events;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

/**
 * Un ��v��nement. Cette classe repr��sente un ��v��nement pouvant ��tre ��mit par un
 * EventEmitter et re��u par un EventListener. Il ne poss��de aucune propri��t�� et
 * doit ��tre ��tendu pour lui associer une signification.
 */
@SuppressWarnings("serial")
public class Event implements Cloneable, Serializable {
    
    private static final String TAG = "Event";
    
    /**
     * L'��metteur qui a ��mit cet ��v��nement. Cet attribut n'est pas s��rialis��
     * avec l'��v��nement, dans un tel cas, l'��metteur original est perdu.
     */
    private transient EventEmitterInterface mEmitter;

    /**
     * Retourne l'��metteur de cet ��v��nement.
     */
    public EventEmitterInterface getEmitter() {
        return mEmitter;
    }

    /**
     * Notifie un EventListener pr��cis que cet ��v��nement est survenu.
     * 
     * @param listener
     *            L'EventListener �� notifier.
     * 
     * @throws UnhandledEventException
     *             Si l'��v��nement n'est pas g��r�� par le listener sp��cifi��.
     * @throws InvocationTargetException
     *             Si l'execution du listener a provoqu�� une exception.
     */
    public final void trigger(EventListener listener) throws InvocationTargetException {
        trigger(listener, null);
    }

    /**
     * Notifie un EventListener pr��cis que cet ��v��nement est survenu comme s'il
     * provenait d'un ��metteur donn��.
     * 
     * @param listener
     *            L'EventListener �� notifier.
     * @param emitter
     *            L'��metteur de cet ��v��nement.
     * 
     * @throws UnhandledEventException
     *             Si l'��v��nement n'est pas g��r�� par le listener sp��cifi��.
     * @throws InvocationTargetException
     *             Si l'execution du listener a provoqu�� une exception.
     */
    public final void trigger(EventListener listener,
            EventEmitterInterface emitter) throws InvocationTargetException {
        // Event itself is not modified
        Event event = (Event) this.clone();

        // Set the emitter
        event.mEmitter = emitter;

        // --------------------------------------
        // In memoriam of Generics-powered events
        // "Because *this*, doesnt work"
        // --------------------------------------

        // The class of the event
        Class<?> eventClass = event.getClass();

        while (eventClass != null) {
            try {
                // Try to get a handler for the exact class of this event
                Method on = listener.getClass().getMethod("on", eventClass);
                
                // Ensure accessibility
                on.setAccessible(true);
// Inner-class are otherwise unavailable

                // Invoke!
                on.invoke(listener, event);
                
                // Done
                return;
            } catch (NoSuchMethodException e) {
                // A listener should not need to implement a 'on' method.
                // Maybe a parent handles it.
                Log.v(TAG, "Event callback", e);
                eventClass = getParent(eventClass);
            } catch (IllegalArgumentException e) {
                // A listener should not need to handle every 'on' method.
                // Maybe a parent handles it.
                Log.v(TAG, "Event callback", e);
                eventClass = getParent(eventClass);
            } catch (IllegalAccessException e) {
                eventClass = getParent(eventClass);
            }
            
        }
    }

    private Class<?> getParent(Class<?> eventClass) {
        // Exception when getting the handler, handler is probably
        // undefined

        if (eventClass == Event.class) {
            // Event is the super-class of all events
            return null;
        }

        // Try
        return eventClass.getSuperclass();
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen
            Log.d(TAG, "Clone not supported Exception", e);
            return this;
        }
    }
}
