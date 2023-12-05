package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables;

import javafx.scene.paint.Color;

public class PoisonedEatable extends AbstractEatable{
    public PoisonedEatable(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        setFill(Color.RED);
    }
}
