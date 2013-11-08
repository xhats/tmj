package org.xhats.tmj.lite.desktop.javafxapplication;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.xhats.tmj.lite.desktop.javafxapplication.controller.StudyController;

/**
 * Main Application. This class handles navigation and user session.
 */
@Configuration
public class Main extends Application {

    @Resource
    private SingleConnectionDataSource dataSource;
    private static Stage stage;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(JavaFXContext.class, DatabaseContext.class);
    private static final JavaFXBuilderFactory javaFXBuilderFactory = new JavaFXBuilderFactory();
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n");
    private static final FXMLLoader loader = new FXMLLoader() {
        {
            setBuilderFactory(javaFXBuilderFactory);
            setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> clazz) {
                    return applicationContext.getBean(clazz);
                }
            });
            setResources(resourceBundle);
        }
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            gotoStudy();
            primaryStage.show();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private void gotoStudy() {
        try {
            StudyController study = (StudyController) replaceSceneContent("/fxml/study.fxml");
            stage.setTitle(resourceBundle.getString("study.title"));
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private static Initializable replaceSceneContent(String fxml) throws Exception {
        URL url = Main.class.getResource(fxml);
        try (InputStream in = Main.class.getResourceAsStream(fxml)) {
            loader.setLocation(url);
            stage.setScene(new Scene((Parent) loader.load(in)));
        }
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
}
