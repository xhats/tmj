package org.xhats.tmj.lite.desktop.javafxapplication.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class Study implements InitializingBean {

    public static final byte PERSON_ID = 0;
    public static final byte NUMBER_STUDY = 0;
    public static final byte DEFAULT_GOAL_IMAGE = 55;
    public static final String RULESET_NAME = "TMJ";
    private static final String RESOURCE_PATH = "images/";
    private static final String FILENAME_GOAL_IMAGE = "goal";
    private static final String IMAGE_EXTENSION = ".png";
    private static final Image[] images = new Image[100];
    private final DoubleProperty _asl = new SimpleDoubleProperty(), _ssl = new SimpleDoubleProperty(), _psl = new SimpleDoubleProperty();
    private final DoubleProperty _asr = new SimpleDoubleProperty(), _ssr = new SimpleDoubleProperty(), _psr = new SimpleDoubleProperty();
    private final ObjectProperty<Image> _goal = new SimpleObjectProperty<>();
    
    public DoubleProperty aslProperty() {
        return _asl;
    }

    public DoubleProperty sslProperty() {
        return _ssl;
    }

    public DoubleProperty pslProperty() {
        return _psl;
    }

    public DoubleProperty asrProperty() {
        return _asr;
    }

    public DoubleProperty ssrProperty() {
        return _ssr;
    }

    public DoubleProperty psrProperty() {
        return _psr;
    }

    public ObjectProperty<Image> goalProperty() {
        return _goal;
    }

    public void setGoal(byte goal) {
        _goal.set(images[goal]);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (byte i = 0; i <= 9; i++) {
            for (byte j = 0; j <= 9; j++) {
                images[i * 10 + j] = new Image(RESOURCE_PATH + FILENAME_GOAL_IMAGE + String.valueOf(i) + String.valueOf(j) + IMAGE_EXTENSION);
            }
        }
    }
}
