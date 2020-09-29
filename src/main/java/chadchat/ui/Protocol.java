package chadchat.ui;

import chadchat.api.ChadChat;
import chadchat.api.InvalidPassword;
import chadchat.domain.Message;
import chadchat.domain.User;
import chadchat.domain.UserExists;
import chadchat.infrastructure.Database;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Protocol implements ChadChat.MessageNotifier {

    ChadChat chadchat = ChadChat.getInstance();

    private Scanner in;
    private PrintWriter out;
    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    public Protocol(Scanner in, PrintWriter out) throws IOException {
        this.in = in;
        this.out = out;
    }

    public void run() throws ClassNotFoundException, InterruptedException, UserExists {
        Database d = new Database();
        //String userName = user.getName();

        ChadChat.getInstance().register(this);
        Thread t = new Thread(() -> {
            while(true) {
                //ChadChat.getInstance().sendMessage(user, in.nextLine());
                String msg = null;
                try {
                    msg = messages.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                out.println(msg);
                out.flush();
            }
        });
        t.start();

        User user = findUser();
        handleUserInput(user);

        /*out.println("Welcome to the General chatroom: " + userName + "\nIn this room there are: \n" + d.findAllUsers());
        out.flush();

        String line;
        while (!(line = in.next()).equals("exit")) {
            out.println(userName + ">" + line);
            out.flush();
        }*/
    }

    public void handleUserInput(User user){
        while(true){
            String input = in.nextLine();
            switch(input){
                case "!exit":
                    return;
                case "!channel":
                    //send to channel
                    break;
                default:
                    ChadChat.getInstance().sendMessage(user, input);
                    break;
            }
        }
    }

    public User findUser() throws ClassNotFoundException, UserExists {
        Database d = new Database();
        String line;
        String loginMsg = getLoginScreen();
        out.println(loginMsg);
        out.flush();
        while (!(line = in.next()).equals("!exit")) {
            switch (line) {
                case "l":
                case "login":
                    out.println("Username: ");
                    out.flush();
                    String usernameLogin = in.next();
                    out.println("Password: ");
                    out.flush();
                    String passwordLogin = in.next();
                    try {
                        User user = chadchat.login(usernameLogin, passwordLogin);
                        out.println("Successfully logged in.");
                        out.flush();
                        return user;
                    } catch (InvalidPassword invalidPassword) {
                        out.println("Rejected.");
                        out.flush();
                    }
                    break;
                case "s":
                case "signup":
                    out.println("Username: ");
                    out.flush();
                    String userNameCreate = in.next();
                    out.println("Password: ");
                    out.flush();
                    String passwordCreate = in.next();
                    User userbefore = chadchat.createUser(userNameCreate, passwordCreate);
                    return userbefore;
                default:
                    out.println("Ugyldigt input");
                    out.flush();
                    break;
            }
        }
        return null;
    }

    private String getLoginScreen() {
        String login = "Welcome to the chatroom! " +
                    "\nIf you are a new user write: [s]ignup" +
                    "\nIf you are a returning user: [l]ogin";
        return login;
    }

    public void addMessage(String message){
        messages.add(message);
    }

    @Override
    public void notifyNewMessage(Message m) {
        m.getUserID();
        User user = chadchat.findUser(m.getUserID());
        messages.add("" + m.getTime().format(DateTimeFormatter.ISO_TIME) + " " + user.getName() + ": " + m.getMsg());
    }
}
