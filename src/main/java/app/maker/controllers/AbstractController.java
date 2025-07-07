package app.maker.controllers;

import app.engine.Observable;
import javafx.fxml.FXML;

public abstract class AbstractController extends Observable {

    @FXML
    public abstract void initialize();

    public abstract void updateLanguage();
}
