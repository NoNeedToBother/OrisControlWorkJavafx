package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.GameApplication;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.AbstractEatable;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.Eatable;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.PoisonedEatable;

public class Player extends Rectangle{
    private static Player player = null;

    private int size = 10;

    private Direction direction = Direction.RIGHT;

    private Player() {
        super(10, 10);
        setX(300);
        setY(300);
        setFill(Color.BLACK);
    }

    public static Player getInstance() {
        if (player == null) {
            player = new Player();
        }
        return player;
    }

    public void move() {
        int delta = 10;
        switch (direction) {
            case LEFT -> {
                setX(getX() - delta);
                if (getX() <= 0) setX(600);
            }
            case RIGHT -> {
                setX(getX() + delta);
                if (getX() >= 600) setX(0);
            }
            case UP -> {
                setY(getY() - delta);
                if (getY() <= 0) setY(600);
            }
            case DOWN -> {
                setY(getY() + delta);
                if (getY() >= 600) setY(0);
            }
            default -> throw new RuntimeException("Impossible");
        }
    }

    public void eat(AbstractEatable eatable) {
        GameApplication gameApplication = GameApplication.getGameInstance();
        if (eatable instanceof Eatable) {
            size += 5;
            setWidth(size);
            setHeight(size);
            gameApplication.notifyEatablesIsEaten(eatable);
        }
    }

    public void switchDirection(Direction direction) {
        this.direction = direction;
    }
}
