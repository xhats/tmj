/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xhats.tmj.lite.desktop.javafxapplication;

import java.util.ResourceBundle;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.xhats.production.core.dao.RulesetDAO;
import org.xhats.production.core.entity.RulesetType;
import org.xhats.tmj.lite.desktop.javafxapplication.model.Study;

/**
 *
 * @author Гаврилов АН
 */
@Configurable
@ComponentScan(basePackages = "org.xhats.tmj.lite.desktop.javafxapplication")
public class JavaFXContext {

    @Resource
    private RulesetDAO rulesetDAOImpl;
    
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("i18n");
    }

    @Bean
    public RulesetType ruleset() {
        return rulesetDAOImpl.findByName(Study.RULESET_NAME);
    }
}
