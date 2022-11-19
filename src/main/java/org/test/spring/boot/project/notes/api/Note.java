package org.test.spring.boot.project.notes.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for Note.
 */
public class Note implements Serializable {

    private final String EXAMPLE_ID = "12345";
    private final String EXAMPLE_OFFSET_DATE_TIME = "2019-08-07T16:54:32+01:00";

    @Schema(name = "id", description = "identifier (generated)", accessMode = READ_ONLY, example = EXAMPLE_ID)
    @JsonProperty("id")
    private Long id;

    @Schema(name = "created", description = "creation date (generated)", accessMode = READ_ONLY, example = EXAMPLE_OFFSET_DATE_TIME)
    @JsonProperty("created")
    @PastOrPresent
    private OffsetDateTime created;

    @Schema(name = "updated", description = "last modification date (generated)", accessMode = READ_ONLY, example = EXAMPLE_OFFSET_DATE_TIME)
    @JsonProperty("lastModified")
    @PastOrPresent
    private OffsetDateTime lastModified;

    @NotBlank
    @Size(min = 1, max = 2048)
    @Schema(name = "text", description = "text of the note, up to 2048 characters", example = "Si vis pacem para bellum", required = true)
    @JsonProperty("text")
    private String text;

    public Note() {
        //
    }

    public Note(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id + ": " + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}