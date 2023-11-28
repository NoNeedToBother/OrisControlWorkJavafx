package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.client.ChatClient;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.view.BaseView;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.view.ChatView;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.view.UserConfigView;

public class ChatApplication extends Application {
    private UserConfig userConfig;
    private UserConfigView userConfigView;
    private ChatView chatView;

    private ChatClient chatClient;
    private BorderPane root;

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Chat");
        stage.setOnCloseRequest(e -> System.exit(0));

        BaseView.setChatApplication(this);
        userConfigView = new UserConfigView();
        chatView = new ChatView();

        chatClient = new ChatClient(this);

        root = new BorderPane();
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();

        setView(userConfigView);
    }

    public void appendMessage(String message) {
        chatView.appendMessage(message);
    }

    public void startChat() {
        chatClient.start();
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

    public ChatView getChatView() {
        return chatView;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }
}
