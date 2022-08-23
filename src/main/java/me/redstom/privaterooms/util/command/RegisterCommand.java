package me.redstom.privaterooms.util.command;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Describes a command component.
 * @author RedsTom
 * @since 1.0.0
 * @version 1.0.0
 * @see ICommand
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RegisterCommand {
}
