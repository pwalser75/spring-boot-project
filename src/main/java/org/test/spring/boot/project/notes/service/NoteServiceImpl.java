package org.test.spring.boot.project.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.test.spring.boot.project.notes.api.Note;
import org.test.spring.boot.project.notes.api.NoteService;
import org.test.spring.boot.project.notes.api.ResourceNotFoundException;
import org.test.spring.boot.project.notes.persistence.NoteEntity;
import org.test.spring.boot.project.notes.persistence.NoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of the NoteService.
 */
@Service
@Transactional
@Validated
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Note get(long id) {
        return convert(load(id));
    }

    private NoteEntity load(long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Note save(Note note) {
        NoteEntity entity = Optional.ofNullable(note.getId()).map(this::load).orElseGet(NoteEntity::new);
        entity = update(entity, note);
        return convert(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> list() {
        Spliterator<NoteEntity> spliterator = repository.findAll().spliterator();
        Stream<NoteEntity> stream = StreamSupport.stream(spliterator, false);
        return stream.map(this::convert).collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    private Note convert(NoteEntity entity) {
        if (entity == null) {
            return null;
        }
        Note dto = new Note();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setCreated(entity.getCreated());
        dto.setLastModified(entity.getLastModified());
        return dto;
    }

    private NoteEntity update(NoteEntity entity, Note dto) {
        if (dto == null) {
            return null;
        }
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        return entity;
    }
}