package repository;

import entity.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User, Long> {
    private static final String FILE_NAME = "users.dat";
    private List<User> users;

    public UserRepository() {
        this.users = loadFromFile();
    }

    @Override
    public void save(User user) {
        users = loadFromFile();
        Optional<User> existing = findById(user.getId());
        if (existing.isPresent()) {
            users.remove(existing.get());
        }
        users.add(user);
        saveToFile();
    }

    @Override
    public Optional<User> findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public void deleteById(Long id) {
        users = loadFromFile();
        users.removeIf(u -> u.getId().equals(id));
        saveToFile();
    }

    public Optional<User> findByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    public boolean existsByLogin(String login) {
        return users.stream().anyMatch(u -> u.getLogin().equals(login));
    }

    public long getNextId() {
        return users.stream().mapToLong(User::getId).max().orElse(0) + 1;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения пользователей: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки пользователей: " + e.getMessage(), e);
        }
    }
}
