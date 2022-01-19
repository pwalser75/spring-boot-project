package org.test.spring.boot.project.notes.service;

import org.mapstruct.Mapper;
import org.test.spring.boot.project.notes.api.Note;
import org.test.spring.boot.project.notes.persistence.NoteEntity;

@Mapper(componentModel = "spring")
public interface NoteEntityMapper {

    Note toDTO(NoteEntity noteEntity);
}
