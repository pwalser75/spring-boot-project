package org.test.spring.boot.project.notes.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;

import static java.time.OffsetDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.Locale.ENGLISH;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.test.spring.boot.project.platform.util.ObjectMappers.json;

/**
 * Tests for Note model
 */
public class NoteTest {

    @BeforeEach
    public void init() {
        Locale.setDefault(ENGLISH);
    }

    @Test
    public void testValidate() {

        Note note = new Note("Hello World");

        // all ok
        validate(note);

        // text not empty
        note.setText(null);
        validate(note, "text");

        // text not blank
        note.setText("");
        validate(note, "text", "text");

        // text length <= 2048 chars
        StringBuilder builder = new StringBuilder();
        builder.append("#".repeat(2048));
        note.setText(builder.toString());
        validate(note);
        builder.append('#');
        note.setText(builder.toString());
        validate(note, "text");

        // dates not in the future
        note.setText("Hello World");
        note.setCreated(now().plusMinutes(1));
        note.setLastModified(now().plusDays(1));
        validate(note, "created", "lastModified");
    }

    @Test
    public void testSerialize() throws Exception {

        Note note = new Note();
        note.setText("Hello World");
        note.setId(12345L);
        note.setCreated(now().minusDays(1));
        note.setLastModified(now().minusHours(1));

        String json = json().writeValueAsString(note);
        System.out.println(json);

        Note restored = json().readValue(json, Note.class);

        assertThat(restored).isEqualTo(note);
        assertThat(restored.hashCode()).isEqualTo(note.hashCode());
        assertThat(restored.toString()).isEqualTo(note.toString());

        assertThat(restored).usingRecursiveComparison()
                .withComparatorForFields(comparing(OffsetDateTime::toInstant), "created", "lastModified")
                .isEqualTo(note);
    }

    private void validate(Object obj, String... expectedErrorPropertyPaths) {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(obj);
        errors.forEach(e -> System.out.printf("- %s: %s\n", e.getPropertyPath(), e.getMessage()));
        assertThat(errors).extracting(ConstraintViolation::getPropertyPath).map(Path::toString).containsExactlyInAnyOrder(expectedErrorPropertyPaths);
    }
}