package me.redstom.privaterooms.util.events;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Describes an event listener.
 *
 * @author redstom
 * @since 1.0.0
 * @version 1.0.0
 * @see net.dv8tion.jda.api.hooks.ListenerAdapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RegisterListener {
}
