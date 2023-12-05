package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables;

import javafx.scene.paint.Color;

public class Eatable extends AbstractEatable{
    public Eatable(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        setFill(Color.GREEN);
    }
}
