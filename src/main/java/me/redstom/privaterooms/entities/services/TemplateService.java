/*
 * PrivateRooms is a discord bot to manage vocal chats.
 * Copyright (C) 2022 GravenDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.redstom.privaterooms.entities.services;

import lombok.RequiredArgsConstructor;
import me.redstom.privaterooms.entities.entity.Model;
import me.redstom.privaterooms.entities.entity.Template;
import me.redstom.privaterooms.entities.entity.TemplateId;
import me.redstom.privaterooms.entities.entity.User;
import me.redstom.privaterooms.entities.repository.TemplateRepository;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public Command.Choice[] completeTemplatesOf(long userId) {
        return templateRepository.findAllByIdAuthorDiscordId(userId).stream()
          .map(t -> new Command.Choice(
            t.id().name(),
            t.id().toString()
          ))
          .toArray(Command.Choice[]::new);
    }

    public List<Template> getTemplatesOf(long userId) {
        return templateRepository.findAllByIdAuthorDiscordId(userId);
    }

    public Template save(String name, User owner, Model model) {
        Template template = Template.builder()
          .id(TemplateId.builder()
            .author(owner)
            .name(name)
            .build())
          .model(model)
          .build();

        Optional<Template> load = load(owner, name);
        load.ifPresent(value -> template.id(value.id()));

        return templateRepository.save(template);
    }

    public Optional<Template> load(User owner, String name) {
        return templateRepository.findByIdAuthorDiscordIdAndIdName(owner.discordId(), name);
    }
}
