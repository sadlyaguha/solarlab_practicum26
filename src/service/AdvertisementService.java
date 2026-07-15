package service;

import entity.Advertisement;
import entity.User;
import repository.AdvertisementRepository;
import repository.UserRepository;
import request.CreateAdRequest;
import request.EditAdRequest;
import request.ChangeAdStatusRequest;

import java.util.List;
import java.util.Optional;

public class AdvertisementService {
    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;

    public AdvertisementService(AdvertisementRepository adRepository, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    public Advertisement createAd(CreateAdRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getAuthorId());
        if (userOpt.isEmpty()) {
            System.out.println("Пользователь не найден.");
            return null;
        }

        User user = userOpt.get();
        if (user.isBlocked()) {
            System.out.println("Заблокированные пользователи не могут создавать объявления.");
            return null;
        }

        long id = adRepository.getNextId();
        Advertisement ad = new Advertisement(id, request.getAuthorId(), request.getCategory(),
                                              request.getTitle(), request.getDescription(),
                                              request.getPrice());
        adRepository.save(ad);
        System.out.println("Объявление создано. ID: " + ad.getId());
        return ad;
    }

    public boolean editAd(EditAdRequest request) {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            System.out.println("Объявление не найдено.");
            return false;
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            System.out.println("Вы можете редактировать только свои объявления.");
            return false;
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            System.out.println("Заблокированные пользователи не могут редактировать объявления.");
            return false;
        }

        ad.setCategory(request.getCategory());
        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setPrice(request.getPrice());
        adRepository.save(ad);
        System.out.println("Объявление обновлено.");
        return true;
    }

    public boolean deactivateAd(ChangeAdStatusRequest request) {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            System.out.println("Объявление не найдено.");
            return false;
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            System.out.println("Вы можете деактивировать только свои объявления.");
            return false;
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            System.out.println("Заблокированные пользователи не могут деактивировать объявления.");
            return false;
        }

        if (ad.isBlockedByAdmin()) {
            System.out.println("Это объявление заблокировано администратором и не может быть активировано.");
            return false;
        }

        ad.setStatus(Advertisement.Status.INACTIVE);
        adRepository.save(ad);
        System.out.println("Объявление деактивировано.");
        return true;
    }

    public boolean activateAd(ChangeAdStatusRequest request) {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            System.out.println("Объявление не найдено.");
            return false;
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            System.out.println("Вы можете активировать только свои объявления.");
            return false;
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            System.out.println("Заблокированные пользователи не могут активировать объявления.");
            return false;
        }

        if (ad.isBlockedByAdmin()) {
            System.out.println("Это объявление заблокировано администратором и не может быть активировано.");
            return false;
        }

        ad.setStatus(Advertisement.Status.ACTIVE);
        adRepository.save(ad);
        System.out.println("Объявление активировано.");
        return true;
    }

    public boolean deactivateAdByAdmin(long adId, long adminId) {
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || !adminOpt.get().isAdmin()) {
            System.out.println("Только администратор может деактивировать объявления.");
            return false;
        }

        Optional<Advertisement> adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            System.out.println("Объявление не найдено.");
            return false;
        }

        Advertisement ad = adOpt.get();
        ad.setStatus(Advertisement.Status.BLOCKED_BY_ADMIN);
        adRepository.save(ad);
        System.out.println("Объявление #" + adId + " заблокировано администратором.");
        return true;
    }

    public void viewActiveAds() {
        List<Advertisement> activeAds = adRepository.findActiveAds();
        if (activeAds.isEmpty()) {
            System.out.println("Нет активных объявлений.");
            return;
        }
        System.out.println("\n=== Активные объявления ===");
        activeAds.forEach(ad -> {
            System.out.println(ad);
            System.out.println("---");
        });
    }

    public void viewMyAds(long userId) {
        List<Advertisement> myAds = adRepository.findByAuthorId(userId);
        if (myAds.isEmpty()) {
            System.out.println("У вас нет объявлений.");
            return;
        }
        System.out.println("\n=== Мои объявления ===");
        myAds.forEach(ad -> {
            System.out.println(ad);
            System.out.println("---");
        });
    }

    public void viewAllAds() {
        List<Advertisement> allAds = adRepository.findAll();
        if (allAds.isEmpty()) {
            System.out.println("Нет объявлений в системе.");
            return;
        }
        System.out.println("\n=== Все объявления ===");
        allAds.forEach(ad -> {
            System.out.println(ad);
            System.out.println("---");
        });
    }

    public void searchByCategory(String category) {
        List<Advertisement> found = adRepository.findByCategory(category);
        if (found.isEmpty()) {
            System.out.println("Объявления в категории '" + category + "' не найдены.");
            return;
        }
        System.out.println("\n=== Результаты поиска по категории: " + category + " ===");
        found.forEach(ad -> {
            System.out.println(ad);
            System.out.println("---");
        });
    }

    public void searchByTitle(String keyword) {
        List<Advertisement> found = adRepository.findByTitleContaining(keyword);
        if (found.isEmpty()) {
            System.out.println("Объявления по запросу '" + keyword + "' не найдены.");
            return;
        }
        System.out.println("\n=== Результаты поиска по названию: " + keyword + " ===");
        found.forEach(ad -> {
            System.out.println(ad);
            System.out.println("---");
        });
    }
}
