package facade;

import entity.User;
import repository.AdvertisementRepository;
import repository.UserRepository;
import request.*;
import service.AdvertisementService;
import service.UserService;

import java.util.Scanner;

public class ApplicationFacade {
    private final UserService userService;
    private final AdvertisementService adService;
    private final Scanner scanner;

    public ApplicationFacade() {
        UserRepository userRepo = new UserRepository();
        AdvertisementRepository adRepo = new AdvertisementRepository();
        this.userService = new UserService(userRepo);
        this.adService = new AdvertisementService(adRepo, userRepo);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("========================================");
        System.out.println("        --- Доска объявлений ---");
        System.out.println("========================================");

        while (true) {
            if (!userService.isLoggedIn()) {
                showMainMenu();
            } else {
                User currentUser = userService.getCurrentUser();
                if (currentUser.isAdmin()) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Главное меню ---");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Просмотреть активные объявления");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": login(); break;
            case "2": register(); break;
            case "3": adService.viewActiveAds(); break;
            case "0": exit(); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void showUserMenu() {
        System.out.println("\n--- Меню пользователя ---");
        System.out.println("1. Просмотреть активные объявления");
        System.out.println("2. Мои объявления");
        System.out.println("3. Создать объявление");
        System.out.println("4. Редактировать объявление");
        System.out.println("5. Деактивировать объявление");
        System.out.println("6. Активировать объявление");
        System.out.println("7. Поиск по категории");
        System.out.println("8. Поиск по названию");
        System.out.println("9. Профиль");
        System.out.println("0. Выйти из аккаунта");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": adService.viewActiveAds(); break;
            case "2": adService.viewMyAds(userService.getCurrentUser().getId()); break;
            case "3": createAd(); break;
            case "4": editAd(); break;
            case "5": deactivateAd(); break;
            case "6": activateAd(); break;
            case "7": searchByCategory(); break;
            case "8": searchByTitle(); break;
            case "9": showProfile(); break;
            case "0": userService.logout(); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void showAdminMenu() {
        System.out.println("\n--- Меню администратора ---");
        System.out.println("1. Просмотреть активные объявления");
        System.out.println("2. Просмотреть все объявления");
        System.out.println("3. Деактивировать объявление (админ)");
        System.out.println("4. Заблокировать пользователя");
        System.out.println("5. Разблокировать пользователя");
        System.out.println("6. Просмотреть пользователей");
        System.out.println("7. Поиск по категории");
        System.out.println("8. Поиск по названию");
        System.out.println("0. Выйти из аккаунта");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": adService.viewActiveAds(); break;
            case "2": adService.viewAllAds(); break;
            case "3": adminDeactivateAd(); break;
            case "4": blockUser(); break;
            case "5": unblockUser(); break;
            case "6": viewUsers(); break;
            case "7": searchByCategory(); break;
            case "8": searchByTitle(); break;
            case "0": userService.logout(); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void login() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        userService.login(new LoginRequest(login, password));
    }

    private void register() {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        userService.register(new RegisterRequest(login, name, email, password));
    }

    private void createAd() {
        User user = userService.getCurrentUser();
        System.out.print("Категория: ");
        String category = scanner.nextLine().trim();
        System.out.print("Название: ");
        String title = scanner.nextLine().trim();
        System.out.print("Описание: ");
        String description = scanner.nextLine().trim();
        System.out.print("Цена (оставьте пустым для 'Договорная'): ");
        String price = scanner.nextLine().trim();
        if (price.isEmpty()) price = null;

        adService.createAd(new CreateAdRequest(user.getId(), category, title, description, price));
    }

    private void editAd() {
        User user = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        System.out.print("Новая категория: ");
        String category = scanner.nextLine().trim();
        System.out.print("Новое название: ");
        String title = scanner.nextLine().trim();
        System.out.print("Новое описание: ");
        String description = scanner.nextLine().trim();
        System.out.print("Новая цена (оставьте пустым для 'Договорная'): ");
        String price = scanner.nextLine().trim();
        if (price.isEmpty()) price = null;

        adService.editAd(new EditAdRequest(adId, user.getId(), category, title, description, price));
    }

    private void deactivateAd() {
        User user = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.deactivateAd(new ChangeAdStatusRequest(adId, user.getId()));
    }

    private void activateAd() {
        User user = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.activateAd(new ChangeAdStatusRequest(adId, user.getId()));
    }

    private void adminDeactivateAd() {
        User admin = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.deactivateAdByAdmin(adId, admin.getId());
    }

    private void blockUser() {
        User admin = userService.getCurrentUser();
        System.out.print("Логин пользователя: ");
        String login = scanner.nextLine().trim();
        userService.blockUser(new BlockUserRequest(login, admin.getId()));
    }

    private void unblockUser() {
        User admin = userService.getCurrentUser();
        System.out.print("Логин пользователя: ");
        String login = scanner.nextLine().trim();
        userService.unblockUser(login, admin.getId());
    }

    private void searchByCategory() {
        System.out.print("Категория: ");
        String category = scanner.nextLine().trim();
        adService.searchByCategory(category);
    }

    private void searchByTitle() {
        System.out.print("Ключевое слово: ");
        String keyword = scanner.nextLine().trim();
        adService.searchByTitle(keyword);
    }

    private void viewUsers() {
        System.out.println("\n=== Пользователи системы ===");
        userService.findAll().forEach(System.out::println);
    }

    private void showProfile() {
        System.out.println("\n=== Ваш профиль ===");
        System.out.println(userService.getCurrentUser());
    }

    private long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            System.out.println("Некорректное число.");
            return -1;
        }
    }

    private void exit() {
        System.out.println("До свидания ;(");
        scanner.close();
        System.exit(0);
    }
}
