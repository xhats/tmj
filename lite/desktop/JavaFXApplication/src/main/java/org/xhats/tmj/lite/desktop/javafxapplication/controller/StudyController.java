package org.xhats.tmj.lite.desktop.javafxapplication.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.xhats.production.core.LogicChain;
import org.xhats.production.core.dao.RuleDAO;
import org.xhats.production.core.dao.VarDAO;
import org.xhats.production.core.entity.RuleType;
import org.xhats.production.core.entity.RulesetType;
import org.xhats.production.core.entity.VarType;
import org.xhats.tmj.lite.desktop.javafxapplication.model.Study;
import static org.xhats.tmj.lite.desktop.javafxapplication.model.Study.DEFAULT_GOAL_IMAGE;

/**
 * Profile Controller.
 */
@Controller
public class StudyController extends AnchorPane implements Initializable {

    @Resource
    private LogicChain logicChain;
    @Resource
    private RuleDAO ruleDAOImpl;
    @Resource
    private RulesetType ruleset;
    @Resource
    private ResourceBundle resourceBundle;
    @Resource
    private Study study;
    @Resource
    private VarDAO varDAOImpl;

    @FXML
    private TextArea reasonTextArea;
    @FXML
    private TextField aslTextField, pslTextField, sslTextField;
    @FXML
    private TextField asrTextField, psrTextField, ssrTextField;
    @FXML
    private ImageView goalImageView;

    private static final NumberStringConverter numberStringConverter = new NumberStringConverter();
    private final Service service = new Service<Void>() {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                public Void call() {
                    updateMessage("");
                    logicChain.addIntoStack(ruleset.getGoal());
                    while (logicChain.isModifed() && !isCancelled()) {
                        logicChain.reverseChain(ruleset, Study.PERSON_ID, Study.NUMBER_STUDY);
                    }
                    if (!logicChain.isModifed() && !logicChain.isEmptyStack()) {
                        failed();
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    VarType goal = varDAOImpl.findByName(ruleset.getGoal(), ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
                    study.setGoal(goal == null ? Study.DEFAULT_GOAL_IMAGE : (byte) goal.getValue());
                    if (goal != null) {
                        List<RuleType> rules = ruleDAOImpl.findReason(ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
                        String reason = "";
                        for (RuleType rule : rules) {
                            reason += resourceBundle.getString("rule") + " №" + Integer.toString(rule.getRuleId()) + ": " + rule.getDescription() + "\n";
                        }
                        updateMessage(reason);
                    }
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    updateMessage(resourceBundle.getString("cancelled") + "!");
                }

                @Override
                protected void failed() {
                    super.failed();
                    updateMessage(resourceBundle.getString("failed") + "!");
                }
            };
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aslTextField.textProperty().bindBidirectional(study.aslProperty(), numberStringConverter);
        asrTextField.textProperty().bindBidirectional(study.asrProperty(), numberStringConverter);
        goalImageView.imageProperty().bindBidirectional(study.goalProperty());
        pslTextField.textProperty().bindBidirectional(study.pslProperty(), numberStringConverter);
        psrTextField.textProperty().bindBidirectional(study.psrProperty(), numberStringConverter);
        sslTextField.textProperty().bindBidirectional(study.sslProperty(), numberStringConverter);
        ssrTextField.textProperty().bindBidirectional(study.ssrProperty(), numberStringConverter);
        reasonTextArea.textProperty().bind(service.messageProperty());
        initFromDatabase();
    }

    public void consult(ActionEvent event) {
        varDAOImpl.clear(ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "ASL", study.aslProperty().get());
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "ASR", study.asrProperty().get());
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "PSL", study.pslProperty().get());
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "PSR", study.psrProperty().get());
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "SSL", study.sslProperty().get());
        varDAOImpl.create(ruleset.getRulesetId(), 0, Study.PERSON_ID, Study.NUMBER_STUDY, "SSR", study.ssrProperty().get());
        if (service.isRunning()) {
            service.cancel();
        }
        service.restart();
    }

    public void reset(ActionEvent event) {
        varDAOImpl.clear(ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        initFromDatabase();
    }

    private void initFromDatabase() {
        VarType var;
        var = varDAOImpl.findByName("ASL", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.aslProperty().set(var == null ? 1.2 : var.getValue());
        var = varDAOImpl.findByName("ASR", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.asrProperty().set(var == null ? 1.2 : var.getValue());
        study.setGoal(DEFAULT_GOAL_IMAGE);
        var = varDAOImpl.findByName("PSL", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.pslProperty().set(var == null ? 2.0 : var.getValue());
        var = varDAOImpl.findByName("PSR", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.psrProperty().set(var == null ? 2.0 : var.getValue());
        var = varDAOImpl.findByName("SSL", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.sslProperty().set(var == null ? 2.5 : var.getValue());
        var = varDAOImpl.findByName("SSR", ruleset.getRulesetId(), Study.PERSON_ID, Study.NUMBER_STUDY);
        study.ssrProperty().set(var == null ? 2.5 : var.getValue());
    }
}
