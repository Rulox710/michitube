package app.maker.controllers;

import app.engine.Observable;
import app.engine.Observer;
import javafx.fxml.FXML;

public abstract class AbstractController extends Observable implements Observer {

    @FXML
    public abstract void initialize();

    public abstract void updateLanguage();

    @Override
    public void update(char event, Object data) {}
}
