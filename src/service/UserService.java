package service;

import entity.User;
import entity.UserRole;
import facade.ApplicationFacade.*;
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
            User admin = new User(id, "admin", "Администратор", "admin@system.ru", "admin123", UserRole.ADMIN);
            userRepository.save(admin);
        }
    }

    public User login(LoginRequest request) throws AuthenticationException, BlockedException {
        Optional<User> userOpt = userRepository.findByLogin(request.getLogin());

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Неверный логин или пароль.");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("Неверный логин или пароль.");
        }

        if (user.isBlocked()) {
            throw new BlockedException("Ваш аккаунт заблокирован.");
        }

        currentUser = user;
        return user;
    }

    public void register(RegisterRequest request) throws ValidationException {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ValidationException("Пользователь с таким логином уже существует.");
        }

        long id = userRepository.getNextId();
        User newUser = new User(id, request.getLogin(), request.getName(),
                request.getEmail(), request.getPassword(), UserRole.USER);
        userRepository.save(newUser);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }

    public void blockUser(BlockUserRequest request) throws AccessDeniedException, UserNotFoundException, ValidationException {
        Optional<User> adminOpt = userRepository.findById(request.getAdminId());
        if (adminOpt.isEmpty() || !isAdmin(adminOpt.get())) {
            throw new AccessDeniedException("Только администратор может блокировать пользователей.");
        }

        Optional<User> userOpt = userRepository.findByLogin(request.getLoginToBlock());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }

        User userToBlock = userOpt.get();
        if (isAdmin(userToBlock)) {
            throw new AccessDeniedException("Нельзя заблокировать другого администратора.");
        }

        if (userToBlock.isBlocked()) {
            throw new ValidationException("Пользователь уже заблокирован.");
        }

        userToBlock.setBlocked(true);
        userRepository.save(userToBlock);
    }

    public void unblockUser(String login, long adminId) throws AccessDeniedException, UserNotFoundException, ValidationException {
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !isAdmin(adminOpt.get())) {
            throw new AccessDeniedException("Только администратор может разблокировать пользователей.");
        }

        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }

        User userToUnblock = userOpt.get();
        if (!userToUnblock.isBlocked()) {
            throw new ValidationException("Пользователь не заблокирован.");
        }

        userToUnblock.setBlocked(false);
        userRepository.save(userToUnblock);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
