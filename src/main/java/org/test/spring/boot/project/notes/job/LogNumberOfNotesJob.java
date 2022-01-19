package org.test.spring.boot.project.notes.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.test.spring.boot.project.notes.persistence.NoteRepository;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * A scheduled job that logs the number of notes once a minute.<br>
 * Note: alternatively, the <code>invoke</code> method could just have been annotated with
 * <code>@Scheduled(fixedRate = 60000)</code>. Manual scheduling has some more flexibility
 * though, so the interval could become configurable in the future.
 */
@Component
public class LogNumberOfNotesJob {

    private static final Logger logger = LoggerFactory.getLogger(LogNumberOfNotesJob.class);

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private NoteRepository noteRepository;

    @PostConstruct
    public void init() {
        taskScheduler.scheduleAtFixedRate(this::invoke, Duration.ofMinutes(1));
    }

    public void invoke() {
        long count = noteRepository.count();
        logger.info("there are currently {} notes in the DB", count);
    }
}
