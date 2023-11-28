package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.view;

import javafx.scene.Parent;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.ChatApplication;

public abstract class BaseView {
    private static ChatApplication chatApplication;

    public static ChatApplication getChatApplication() {
        if (chatApplication != null) {
            return chatApplication;
        }
        throw new RuntimeException("App did not initialized.");
    }

    public static void setChatApplication(ChatApplication chatApplication) {
        BaseView.chatApplication = chatApplication;
    }

    public abstract Parent getView();
}