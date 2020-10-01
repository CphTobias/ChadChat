package chadchat.domain;

public interface UserRepository extends UserFactory {
    Iterable<User> findAllUsers();
    User findUser(String name);
    User findUser(int id);
    Iterable<Message> findSomeMessages(int i);
    Iterable<Message> findAllMessages();
    Iterable<Message> findMessageFrom(int i);
}
