package facade;

import entity.User;
import entity.Advertisement;
import repository.AdvertisementRepository;
import repository.UserRepository;
import request.*;
import service.AdvertisementService;
import service.UserService;

import java.util.List;
import java.util.Scanner;

public class ApplicationFacade {

    public static class BulletinException extends Exception {
        public BulletinException(String message) { super(message); }
    }

    public static class AuthenticationException extends BulletinException {
        public AuthenticationException(String message) { super(message); }
    }

    public static class BlockedException extends BulletinException {
        public BlockedException(String message) { super(message); }
    }

    public static class UserNotFoundException extends BulletinException {
        public UserNotFoundException(String message) { super(message); }
    }

    public static class AdNotFoundException extends BulletinException {
        public AdNotFoundException(String message) { super(message); }
    }

    public static class AccessDeniedException extends BulletinException {
        public AccessDeniedException(String message) { super(message); }
    }

    public static class ValidationException extends BulletinException {
        public ValidationException(String message) { super(message); }
    }

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
            try {
                if (!userService.isLoggedIn()) {
                    showMainMenu();
                } else {
                    User currentUser = userService.getCurrentUser();
                    if (userService.isAdmin(currentUser)) {
                        showAdminMenu();
                    } else {
                        showUserMenu();
                    }
                }
            } catch (BulletinException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private void showMainMenu() throws BulletinException {
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
            case "3": viewActiveAds(); break;
            case "0": exit(); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void showUserMenu() throws BulletinException {
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
            case "1": viewActiveAds(); break;
            case "2": viewMyAds(); break;
            case "3": createAd(); break;
            case "4": editAd(); break;
            case "5": deactivateAd(); break;
            case "6": activateAd(); break;
            case "7": searchByCategory(); break;
            case "8": searchByTitle(); break;
            case "9": showProfile(); break;
            case "0": userService.logout(); System.out.println("Вы вышли из системы."); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void showAdminMenu() throws BulletinException {
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
            case "1": viewActiveAds(); break;
            case "2": viewAllAds(); break;
            case "3": adminDeactivateAd(); break;
            case "4": blockUser(); break;
            case "5": unblockUser(); break;
            case "6": viewUsers(); break;
            case "7": searchByCategory(); break;
            case "8": searchByTitle(); break;
            case "0": userService.logout(); System.out.println("Вы вышли из системы."); break;
            default: System.out.println("Неверный выбор.");
        }
    }

    private void login() throws BulletinException {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        User user = userService.login(new LoginRequest(login, password));
        System.out.println("Добро пожаловать, " + user.getName());
    }

    private void register() throws BulletinException {
        System.out.print("Логин: ");
        String login = scanner.nextLine().trim();
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Пароль: ");
        String password = scanner.nextLine().trim();
        userService.register(new RegisterRequest(login, name, email, password));
        System.out.println("Регистрация успешна. Теперь вы можете войти.");
    }

    private void createAd() throws BulletinException {
        User user = userService.getCurrentUser();
        System.out.print("Категория: ");
        String category = scanner.nextLine().trim();
        System.out.print("Название: ");
        String title = scanner.nextLine().trim();
        System.out.print("Описание: ");
        String description = scanner.nextLine().trim();
        System.out.print("Цена (оставьте пустым для 'Договорная'): ");
        String price = scanner.nextLine().trim();

        Advertisement ad = adService.createAd(new CreateAdRequest(user.getId(), category, title, description, price));
        System.out.println("Объявление создано. ID: " + ad.getId());
    }

    private void editAd() throws BulletinException {
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

        adService.editAd(new EditAdRequest(adId, user.getId(), category, title, description, price));
        System.out.println("Объявление обновлено.");
    }

    private void deactivateAd() throws BulletinException {
        User user = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.deactivateAd(new ChangeAdStatusRequest(adId, user.getId()));
        System.out.println("Объявление деактивировано.");
    }

    private void activateAd() throws BulletinException {
        User user = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.activateAd(new ChangeAdStatusRequest(adId, user.getId()));
        System.out.println("Объявление активировано.");
    }

    private void adminDeactivateAd() throws BulletinException {
        User admin = userService.getCurrentUser();
        System.out.print("ID объявления: ");
        long adId = parseLong(scanner.nextLine().trim());
        adService.deactivateAdByAdmin(adId, admin.getId());
        System.out.println("Объявление заблокировано администратором.");
    }

    private void blockUser() throws BulletinException {
        User admin = userService.getCurrentUser();
        System.out.print("Логин пользователя: ");
        String login = scanner.nextLine().trim();
        userService.blockUser(new BlockUserRequest(login, admin.getId()));
        System.out.println("Пользователь заблокирован.");
    }

    private void unblockUser() throws BulletinException {
        User admin = userService.getCurrentUser();
        System.out.print("Логин пользователя: ");
        String login = scanner.nextLine().trim();
        userService.unblockUser(login, admin.getId());
        System.out.println("Пользователь разблокирован.");
    }

    private void viewActiveAds() {
        List<Advertisement> ads = adService.getActiveAds();
        if (ads.isEmpty()) {
            System.out.println("Нет активных объявлений.");
            return;
        }
        System.out.println("\n=== Активные объявления ===");
        ads.forEach(ad -> {
            System.out.println(formatAd(ad));
            System.out.println("---");
        });
    }

    private void viewMyAds() {
        User user = userService.getCurrentUser();
        List<Advertisement> ads = adService.getMyAds(user.getId());
        if (ads.isEmpty()) {
            System.out.println("У вас нет объявлений.");
            return;
        }
        System.out.println("\n=== Мои объявления ===");
        ads.forEach(ad -> {
            System.out.println(formatAd(ad));
            System.out.println("---");
        });
    }

    private void viewAllAds() {
        List<Advertisement> ads = adService.getAllAds();
        if (ads.isEmpty()) {
            System.out.println("Нет объявлений в системе.");
            return;
        }
        System.out.println("\n=== Все объявления ===");
        ads.forEach(ad -> {
            System.out.println(formatAd(ad));
            System.out.println("---");
        });
    }

    private void searchByCategory() {
        System.out.print("Категория: ");
        String category = scanner.nextLine().trim();
        List<Advertisement> found = adService.searchByCategory(category);
        if (found.isEmpty()) {
            System.out.println("Объявления в категории '" + category + "' не найдены.");
            return;
        }
        System.out.println("\n=== Результаты поиска по категории: " + category + " ===");
        found.forEach(ad -> {
            System.out.println(formatAd(ad));
            System.out.println("---");
        });
    }

    private void searchByTitle() {
        System.out.print("Ключевое слово: ");
        String keyword = scanner.nextLine().trim();
        List<Advertisement> found = adService.searchByTitle(keyword);
        if (found.isEmpty()) {
            System.out.println("Объявления по запросу '" + keyword + "' не найдены.");
            return;
        }
        System.out.println("\n=== Результаты поиска по названию: " + keyword + " ===");
        found.forEach(ad -> {
            System.out.println(formatAd(ad));
            System.out.println("---");
        });
    }

    private String formatAd(Advertisement ad) {
        return "Объявление #" + ad.getId() + "\n" +
                "  Заголовок: " + ad.getTitle() + "\n" +
                "  Категория: " + ad.getCategory() + "\n" +
                "  Описание: " + ad.getDescription() + "\n" +
                "  Цена: " + adService.getPriceDisplay(ad) + "\n" +
                "  Дата: " + adService.getFormattedDate(ad) + "\n" +
                "  Статус: " + adService.getStatusText(ad);
    }

    private void viewUsers() {
        System.out.println("\n=== Пользователи системы ===");
        userService.findAll().forEach(System.out::println);
    }

    private void showProfile() {
        System.out.println("\n=== Ваш профиль ===");
        System.out.println(userService.getCurrentUser());
    }

    private long parseLong(String str) throws ValidationException {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректное число.");
        }
    }

    private void exit() {
        System.out.println("До свидания ;(");
        scanner.close();
        System.exit(0);
    }
}
