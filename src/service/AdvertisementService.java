package service;

import entity.Advertisement;
import entity.AdStatus;
import entity.User;
import facade.ApplicationFacade.*;
import repository.AdvertisementRepository;
import repository.UserRepository;
import request.CreateAdRequest;
import request.EditAdRequest;
import request.ChangeAdStatusRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdvertisementService {
    private final AdvertisementRepository adRepository;
    private final UserRepository userRepository;

    public AdvertisementService(AdvertisementRepository adRepository, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    public boolean isActive(Advertisement ad) {
        return ad.getStatus() == AdStatus.ACTIVE;
    }

    public boolean isBlockedByAdmin(Advertisement ad) {
        return ad.getStatus() == AdStatus.BLOCKED_BY_ADMIN;
    }

    public String getStatusText(Advertisement ad) {
        switch (ad.getStatus()) {
            case ACTIVE: return "Активно";
            case INACTIVE: return "Неактивно";
            case BLOCKED_BY_ADMIN: return "Заблокировано администратором";
            default: return "Неизвестно";
        }
    }

    public String getFormattedDate(Advertisement ad) {
        return ad.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getPriceDisplay(Advertisement ad) {
        return (ad.getPrice() == null || ad.getPrice().isEmpty()) ? "Договорная" : ad.getPrice() + " руб.";
    }

    public String preparePrice(String price) {
        return (price == null || price.isEmpty()) ? null : price;
    }

    public Advertisement createAd(CreateAdRequest request) throws UserNotFoundException, BlockedException {
        Optional<User> userOpt = userRepository.findById(request.getAuthorId());
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }

        User user = userOpt.get();
        if (user.isBlocked()) {
            throw new BlockedException("Заблокированные пользователи не могут создавать объявления.");
        }

        long id = adRepository.getNextId();
        String price = preparePrice(request.getPrice());
        Advertisement ad = new Advertisement(id, request.getAuthorId(), request.getCategory(),
                request.getTitle(), request.getDescription(), price);
        adRepository.save(ad);
        return ad;
    }

    public void editAd(EditAdRequest request) throws AdNotFoundException, AccessDeniedException, BlockedException {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            throw new AdNotFoundException("Объявление не найдено.");
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            throw new AccessDeniedException("Вы можете редактировать только свои объявления.");
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            throw new BlockedException("Заблокированные пользователи не могут редактировать объявления.");
        }

        ad.setCategory(request.getCategory());
        ad.setTitle(request.getTitle());
        ad.setDescription(request.getDescription());
        ad.setPrice(preparePrice(request.getPrice()));
        adRepository.save(ad);
    }

    public void deactivateAd(ChangeAdStatusRequest request) throws AdNotFoundException, AccessDeniedException, BlockedException, ValidationException {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            throw new AdNotFoundException("Объявление не найдено.");
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            throw new AccessDeniedException("Вы можете деактивировать только свои объявления.");
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            throw new BlockedException("Заблокированные пользователи не могут деактивировать объявления.");
        }

        if (isBlockedByAdmin(ad)) {
            throw new ValidationException("Это объявление заблокировано администратором и не может быть активировано.");
        }

        ad.setStatus(AdStatus.INACTIVE);
        adRepository.save(ad);
    }

    public void activateAd(ChangeAdStatusRequest request) throws AdNotFoundException, AccessDeniedException, BlockedException, ValidationException {
        Optional<Advertisement> adOpt = adRepository.findById(request.getAdId());
        if (adOpt.isEmpty()) {
            throw new AdNotFoundException("Объявление не найдено.");
        }

        Advertisement ad = adOpt.get();
        if (ad.getAuthorId() != request.getUserId()) {
            throw new AccessDeniedException("Вы можете активировать только свои объявления.");
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isPresent() && userOpt.get().isBlocked()) {
            throw new BlockedException("Заблокированные пользователи не могут активировать объявления.");
        }

        if (isBlockedByAdmin(ad)) {
            throw new ValidationException("Это объявление заблокировано администратором и не может быть активировано.");
        }

        ad.setStatus(AdStatus.ACTIVE);
        adRepository.save(ad);
    }

    public void deactivateAdByAdmin(long adId, long adminId) throws AccessDeniedException, AdNotFoundException {
        Optional<User> adminOpt = userRepository.findById(adminId);
        if (adminOpt.isEmpty() || adminOpt.get().getRole() != entity.UserRole.ADMIN) {
            throw new AccessDeniedException("Только администратор может деактивировать объявления.");
        }

        Optional<Advertisement> adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            throw new AdNotFoundException("Объявление не найдено.");
        }

        Advertisement ad = adOpt.get();
        ad.setStatus(AdStatus.BLOCKED_BY_ADMIN);
        adRepository.save(ad);
    }

    public List<Advertisement> getActiveAds() {
        return adRepository.findActiveAds();
    }

    public List<Advertisement> getMyAds(long userId) {
        return adRepository.findByAuthorId(userId);
    }

    public List<Advertisement> getAllAds() {
        return adRepository.findAll();
    }

    public List<Advertisement> searchByCategory(String category) {
        return adRepository.findByCategory(category);
    }

    public List<Advertisement> searchByTitle(String keyword) {
        return adRepository.findByTitleContaining(keyword);
    }
}
