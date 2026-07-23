package com.centimo.api.it;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.Replace.ANY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(type = POSTGRES, provider = ZONKY, replace = ANY)
@ExtendWith(OutputCaptureExtension.class)
public abstract class AbstractIntegrationIT {

  @Autowired
  protected DataSource dataSource;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected EntityManagerFactory entityManagerFactory;

  protected Statistics statistics() {
    return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
  }

  @BeforeEach
  protected void resetState(CapturedOutput output) {
    statistics().clear();
    resetCapturedOutput(output);
  }

  private static void resetCapturedOutput(CapturedOutput output) {
    try {
      Class<?> outputCaptureClass = output.getClass();
      Method reset = outputCaptureClass.getDeclaredMethod("reset");
      reset.setAccessible(true);
      reset.invoke(output);

      Field systemCapturesField = outputCaptureClass.getDeclaredField("systemCaptures");
      systemCapturesField.setAccessible(true);
      Deque<?> systemCaptures = (Deque<?>) systemCapturesField.get(output);
      for (Object frame : systemCaptures) {
        Method frameReset = frame.getClass().getDeclaredMethod("reset");
        frameReset.setAccessible(true);
        frameReset.invoke(frame);
      }
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException(
          "Failed to reset CapturedOutput; Spring Boot OutputCapture API may have changed", ex);
    }
  }
}
