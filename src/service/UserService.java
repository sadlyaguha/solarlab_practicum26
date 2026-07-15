package service;

import entity.User;
import repository.UserRepository;
import request.LoginRequest;
import request.RegisterRequest;
import request.BlockUserRequest;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private User currentUser;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        if (!userRepository.existsByLogin("admin")) {
            long id = userRepository.getNextId();
            User admin = new User(id, "admin", "Администратор", "admin@system.ru", "admin123", User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Создан администратор по умолчанию (логин: admin, пароль: admin123)");
        }
    }

    public Optional<User> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByLogin(request.getLogin());

        if (userOpt.isEmpty()) {
            System.out.println("Неверный логин или пароль.");
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            System.out.println("Неверный логин или пароль.");
            return Optional.empty();
        }

        if (user.isBlocked()) {
            System.out.println("Ваш аккаунт заблокирован.");
            return Optional.empty();
        }

        currentUser = user;
        System.out.println("Добро пожаловать, " + user.getName());
        return Optional.of(user);
    }

    public boolean register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            System.out.println("Пользователь с таким логином уже существует.");
            return false;
        }

        long id = userRepository.getNextId();
        User newUser = new User(id, request.getLogin(), request.getName(),
                               request.getEmail(), request.getPassword(), User.Role.USER);
        userRepository.save(newUser);
        System.out.println("Регистрация успешна. Теперь вы можете войти.");
        return true;
    }

    public void logout() {
        currentUser = null;
        System.out.println("Вы вышли из системы.");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean blockUser(BlockUserRequest request) {
        Optional<User> adminOpt = userRepository.findById(request.getAdminId());
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            System.out.println("Только администратор может блокировать пользователей.");
            return false;
        }

        Optional<User> userOpt = userRepository.findByLogin(request.getLoginToBlock());
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден.");
            return false;
        }

        User userToBlock = userOpt.get();
        if (userToBlock.isAdmin()) {
            System.out.println("Нельзя заблокировать другого администратора.");
            return false;
        }

        if (userToBlock.isBlocked()) {
            System.out.println("Пользователь уже заблокирован.");
            return false;
        }

        userToBlock.setBlocked(true);
        userRepository.save(userToBlock);
        System.out.println("Пользователь " + request.getLoginToBlock() + " заблокирован.");
        return true;
    }

    public boolean unblockUser(String login, long adminId) {
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            System.out.println("Только администратор может разблокировать пользователей.");
            return false;
        }

        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден.");
            return false;
        }

        User userToUnblock = userOpt.get();
        if (!userToUnblock.isBlocked()) {
            System.out.println("Пользователь не заблокирован.");
            return false;
        }

        userToUnblock.setBlocked(false);
        userRepository.save(userToUnblock);
        System.out.println("Пользователь " + login + " разблокирован.");
        return true;
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
