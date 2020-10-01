package chadchat.api;

import chadchat.domain.Message;
import chadchat.domain.User;
import chadchat.domain.UserExists;
import chadchat.domain.UserRepository;
import chadchat.infrastructure.Database;

import javax.imageio.plugins.tiff.TIFFImageReadParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
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
                instance = new ChadChat(u);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private final UserRepository users;
    private final List<MessageNotifier> notifiers = new ArrayList<>();

    private ChadChat(UserRepository users) {
        this.users = users;
    }

    public User createUser(String name, String password) throws UserExists {
        byte[] salt = User.generateSalt();
        byte[] secret = User.calculateSecret(salt, password);
        return users.createUser(name, salt, secret);
    }

    public User login(String name, String password) throws InvalidPassword {
        User user = users.findUser(name);
        if (user.isPasswordCorrect(password)) {
            return user;
        } else  {
            throw new InvalidPassword();
        }
    }

    public void sendMessage(User user, String message){
        Message m = users.createMessage(user.getId(), message, LocalDateTime.now());
        for (MessageNotifier n: notifiers){
            n.notifyNewMessage(m);
        }
    }

    public User findUser(int id){
        return users.findUser(id);
    }

    public void register(MessageNotifier n){
        notifiers.add(n);
    }

    public Iterable<Message> findSomeMessages(int i) {

        return users.findSomeMessages(i);
    }

    public interface MessageNotifier {
        void notifyNewMessage(Message m);
    }

    public Iterable<User> getUsers() {
        return users.findAllUsers();
    }

    /*public Board getBoard() {
        return new BoardFactory(questions).makeBoard();
    }

    public Game getCurrentGame() {
        return game;
    }*/
}
