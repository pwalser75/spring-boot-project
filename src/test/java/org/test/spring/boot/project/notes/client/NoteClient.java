package org.test.spring.boot.project.notes.client;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.test.spring.boot.project.RestClientConfig;
import org.test.spring.boot.project.notes.api.Note;

import java.util.List;

import static org.test.spring.boot.project.ResponseExceptionMapper.check;

/**
 * Note client API
 */
public class NoteClient implements AutoCloseable {

    private final String baseURL;
    private final Client client;

    public NoteClient(String baseURL) {
        this.baseURL = baseURL;
        client = RestClientConfig.clientBuilder().build();
    }

    @Override
    public void close() {
        client.close();
    }

    /**
     * List all notes
     *
     * @return list of notes (never null)
     */
    public List<Note> list() {
        Invocation invocation = client
                .target(baseURL)
                .request()
                .buildGet();

        Response response = check(invocation.invoke(), 200);
        return response.readEntity(new GenericType<>() {
        });
    }

    /**
     * Get a note by id. Throws a {@link NotFoundException} if the note wasn't found.
     *
     * @param id id
     * @return note.
     */
    public Note get(long id) {
        Invocation invocation = client
                .target(baseURL + "/" + id)
                .request()
                .buildGet();

        Response response = check(invocation.invoke(), 200);
        return response.readEntity(Note.class);
    }

    /**
     * Create a new note with the provided data
     *
     * @param note data
     * @return created note
     */
    public Note create(Note note) {
        Invocation invocation = client
                .target(baseURL)
                .request()
                .buildPost(Entity.json(note));

        Response response = check(invocation.invoke(), 201);
        return response.readEntity(Note.class);
    }

    /**
     * Update a note
     *
     * @param note note (whose id is required)
     */
    public void save(Note note) {

        if (note.getId() == null) {
            throw new IllegalArgumentException("Not yet persisted, use the create() method instead");
        }

        Invocation invocation = client
                .target(baseURL + "/" + note.getId())
                .request()
                .buildPut(Entity.json(note));

        check(invocation.invoke(), 204);
    }

    /**
     * Delete the note with the given id, if it exists (no error thrown otherwise).
     *
     * @param id id of the record
     */
    public void delete(long id) {

        Invocation invocation = client
                .target(baseURL + "/" + id)
                .request()
                .buildDelete();

        check(invocation.invoke(), 204);
    }

}