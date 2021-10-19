package org.test.spring.boot.project.notes.web;


import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.test.spring.boot.project.notes.api.Note;
import org.test.spring.boot.project.notes.client.NoteClient;
import org.test.spring.boot.project.platform.api.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Note endpoint test
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("performance-logging")
public class NoteEndpointTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void init() {
        baseUrl = String.format("https://localhost:%d/api/notes", port);
        log.info("BASE URL: " + baseUrl);
    }

    @Test
    public void testCRUD() {

        try (final NoteClient noteClient = new NoteClient(baseUrl)) {

            // create
            Note note = new Note();
            note.setText("Aloha");

            Note created = noteClient.create(note);
            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
            assertThat(created.getText()).isEqualTo(note.getText());
            long id = created.getId();
            note = created;

            // read
            Note loaded = noteClient.get(id);
            assertThat(loaded).isNotNull();
            assertThat(loaded.getId()).isNotNull();
            assertThat(loaded.getText()).isEqualTo(note.getText());

            // list
            assertThat((Boolean) noteClient.list().stream().anyMatch(p1 -> p1.getId() == id)).isTrue();

            // update
            note.setText("Lorem ipsum dolor sit amet");
            noteClient.save(note);

            loaded = noteClient.get(id);
            assertThat(loaded).isNotNull();
            assertThat(loaded.getId()).isEqualTo(note.getId());
            assertThat(loaded.getText()).isEqualTo(note.getText());

            // delete
            noteClient.delete(id);

            // delete again - must not result in an exception
            noteClient.delete(id);

            // must not be found afterwards
            assertThat((Boolean) noteClient.list().stream().anyMatch(p -> p.getId() == id)).isFalse();
            assertThatThrownBy(() -> noteClient.get(id)).isInstanceOf(NotFoundException.class);
        }
    }

    @Test
    public void testValidationMissingText() {
        try (final NoteClient noteClient = new NoteClient(baseUrl)) {
            Note note = new Note();
            assertThatThrownBy(() -> noteClient.create(note)).isInstanceOfSatisfying(ValidationException.class, ex ->
                    assertThat(ex.getErrors()).hasSize(1).anySatisfy(validationError -> {
                        assertThat(validationError.getPath()).isEqualTo("note:text");
                        assertThat(validationError.getErrorCode()).isEqualTo("NotBlank");
                        assertThat(validationError.getMessage()).isEqualTo("must not be blank");
                    }));
        }
    }

    @Test
    public void testValidationTextTooLong() {
        try (final NoteClient noteClient = new NoteClient(baseUrl)) {
            Note note = new Note();
            note.setText("Lorem ipsum".repeat(1000));
            assertThatThrownBy(() -> noteClient.create(note)).isInstanceOfSatisfying(ValidationException.class, ex ->
                    assertThat(ex.getErrors()).hasSize(1).anySatisfy(validationError -> {
                        assertThat(validationError.getPath()).isEqualTo("note:text");
                        assertThat(validationError.getErrorCode()).isEqualTo("Size");
                        assertThat(validationError.getMessage()).isEqualTo("size must be between 1 and 2048");
                    }));
        }
    }
}