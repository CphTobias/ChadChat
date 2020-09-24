package chadchat.api;

import chadchat.domain.User;
import chadchat.domain.UserRepository;
import chadchat.infrastructure.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ChadChat {
    private static ChadChat instance;

    public static ChadChat getInstance() {
        if (instance == null) {
            //InputStream s = ChadChat.class;
            try {
                UserRepository u = new Database();

                //Chatlog chatlog = new Chatlog(new BoardFactory(repo).makeBoard(), new ArrayList<>());
                instance = new ChadChat(u.findAllUsers());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private final Iterable<User> users;
    //private final Chatlog chatlog;
    private final List<MessageNotifier> notifiers = new ArrayList<>();

    private ChadChat(Iterable<User> users) {
        this.users = users;
        //this.chatlog = chatlog;
    }

    public void sendMessage(User user, String message){
        for (MessageNotifier n: notifiers){
            n.notifyNewMessage(user, message);
        }
    }

    public void register(MessageNotifier n){
        notifiers.add(n);
    }

    public interface MessageNotifier {
        void notifyNewMessage(User user, String message);
    }

    public Iterable<User> getUsers() {
        //return users.findAllUsers();
        return null;
    }

    /*public Board getBoard() {
        return new BoardFactory(questions).makeBoard();
    }

    public Game getCurrentGame() {
        return game;
    }*/
}
