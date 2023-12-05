package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.BotApplication;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.Direction;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.Player;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.AbstractEatable;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.Eatable;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.game.model.eatables.PoisonedEatable;

import java.util.Random;

public class GameApplication extends Application {
    private int speed = 500;

    private static GameApplication gameInstance = null;

    private static Stage primaryStage = null;

    private int time = 0;

    @Override
    public void start(Stage stage) throws Exception {
        gameInstance = this;
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(BotApplication.class.getResource("/game_page.fxml"));
        AnchorPane pane = loader.load();
        Player player = Player.getInstance();
        pane.getChildren().add(player);
        Text text = new Text(10, 10, "00:00:00");
        pane.getChildren().add(text);

        Random random = new Random();
        Eatable eatable = new Eatable(random.nextInt(30, 570), random.nextInt(30, 570), 10);

        int poisonedX = random.nextInt(30, 570);
        int poisonedY = random.nextInt(30, 570);
        boolean correct = doEatableCoordinatesSatisfy(poisonedX, poisonedY, eatable);
        while (!correct) {
            poisonedX = random.nextInt(30, 570);
            poisonedY = random.nextInt(30, 570);
            correct = doEatableCoordinatesSatisfy(poisonedX, poisonedY, eatable);
        }
        PoisonedEatable poisonedEatable = new PoisonedEatable(poisonedX, poisonedY, 10);
        poisonedEatable.setVisible(false);
        pane.getChildren().add(eatable);
        pane.getChildren().add(poisonedEatable);

        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP, W -> player.switchDirection(Direction.UP);
                case DOWN, S -> player.switchDirection(Direction.DOWN);
                case LEFT, A -> player.switchDirection(Direction.LEFT);
                case RIGHT, D -> player.switchDirection(Direction.RIGHT);
            }
        });

        Runnable gameLogic = () -> {
            while (true) {
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    if (!Thread.interrupted()) throw new RuntimeException(e);
                }

                player.move();
                if (!poisonedEatable.isVisible()) {
                    int randomNumb = random.nextInt();
                    if (randomNumb <= 0.1) {
                        poisonedEatable.setVisible(true);
                    }
                }
                if (poisonedEatable.isVisible()) {
                    int randomNumb = random.nextInt();
                    if (randomNumb >= 0.4) {
                        poisonedEatable.setVisible(false);
                        int x = random.nextInt(30, 570);
                        int y = random.nextInt(30, 570);
                        poisonedEatable.setCenterX(x);
                        poisonedEatable.setCenterY(y);
                    }
                }
                if (checkPlayerCollisions(eatable, player)) player.eat(eatable);
                if (checkPlayerCollisions(poisonedEatable, player)) {
                    if (poisonedEatable.isVisible()) {
                        System.exit(0);
                    }
                }
            }
        };

        Runnable timerSpeed = () -> {
            while (true) {
                try {
                    Thread.sleep(30 * 1_000);
                } catch (InterruptedException e) {
                }
                speed = (int) (speed / 1.2);
            }
        };

        Runnable timer = () -> {
            while (true) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                }
                time += 1;
                text.setText(getFormatTime());
            }
        };

        Thread timerSpeedThread = new Thread(timerSpeed);
        Thread timerThread = new Thread(timer);
        Thread gameThread = new Thread(gameLogic);

        gameThread.start();
        timerSpeedThread.start();
        timerThread.start();

        stage.setTitle("Snake");
        stage.setOnCloseRequest(e -> {
            gameThread.interrupt();
            timerSpeedThread.interrupt();
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }

    private String format(int time, boolean end) {
        String res = "";
        if (time <= 9) {
            res += "0";
        }
        res += time;
        if (!end) res += ":";
        return res;
    }

    public String getFormatTime() {
        int hours = time / 1440;
        int minutes = (time % 1440) / 60;
        int seconds = (time % 1440) % 60;
        return format(hours, false) + format(minutes, false) + format(seconds, true);
    }

    public void notifyEatablesIsEaten(AbstractEatable eatable) {
        Random random = new Random();
        int randX = random.nextInt(30, 570);
        int randY = random.nextInt(30, 570);
        eatable.setCenterX(randX);
        eatable.setCenterY(randY);
    }

    public boolean checkPlayerCollisions(AbstractEatable eatable, Player player) {
        double centerEatableX = eatable.getCenterX();
        double centerEatableY = eatable.getCenterY();
        double radius = eatable.getRadius();
        double startX = centerEatableX - radius;
        double endX = centerEatableX + radius;
        double startY = centerEatableY - radius;
        double endY = centerEatableY + radius;

        double playerStartX = player.getX();
        double playerEndX = playerStartX + player.getWidth();
        double playerStartY = player.getY();
        double playerEndY = playerStartY + player.getHeight();

        if (playerStartX >= startX && playerStartX <= endX && playerStartY >= startY && playerStartY <= endY) {
            return true;
        }
        if (centerEatableX >= playerStartX && centerEatableX <= playerEndX && centerEatableY >= playerStartY && centerEatableY <= playerEndY) {
            return true;
        }
        return false;
    }

    public boolean doEatableCoordinatesSatisfy(int x, int y, Eatable eatable) {
        double centerEatableX = eatable.getCenterX();
        double centerEatableY = eatable.getCenterY();
        double radius = eatable.getRadius();
        double startX = centerEatableX - radius;
        double endX = centerEatableX + radius;
        double startY = centerEatableY - radius;
        double endY = centerEatableY + radius;

        if (x >= startX && x <= endX && y >= startY && y <= endY) return false;
        else return true;
    }

    public static GameApplication getGameInstance() {
        return gameInstance;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
