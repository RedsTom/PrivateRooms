package me.redstom.privaterooms.db.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.db.entity.Template;
import me.redstom.privaterooms.db.repository.TemplateRepository;
import me.redstom.privaterooms.db.repository.UserRepository;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;

    public Command.Choice[] completeTemplatesOf(long userId) {
        return templateRepository.findAllByAuthorDiscordId(userId).stream()
          .map(t -> new Command.Choice(
            t.name(),
            t.id().toString()
          ))
          .toArray(Command.Choice[]::new);
    }

    public List<Template> getTemplatesOf(long userId) {
        return templateRepository.findAllByAuthorDiscordId(userId);
    }
}
