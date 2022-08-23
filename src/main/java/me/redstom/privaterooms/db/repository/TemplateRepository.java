package me.redstom.privaterooms.db.repository;

import me.redstom.privaterooms.db.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findAllByAuthorDiscordId(long authorId);

}
