package repository;

import entity.Advertisement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdvertisementRepository implements Repository<Advertisement, Long> {
    private static final String FILE_NAME = "advertisements.dat";
    private List<Advertisement> advertisements;

    public AdvertisementRepository() {
        this.advertisements = loadFromFile();
    }

    @Override
    public void save(Advertisement ad) {
        advertisements = loadFromFile();
        Optional<Advertisement> existing = findById(ad.getId());
        if (existing.isPresent()) {
            advertisements.remove(existing.get());
        }
        advertisements.add(ad);
        saveToFile();
    }

    @Override
    public Optional<Advertisement> findById(Long id) {
        return advertisements.stream().filter(ad -> ad.getId().equals(id)).findFirst();
    }

    @Override
    public List<Advertisement> findAll() {
        return new ArrayList<>(advertisements);
    }

    @Override
    public void deleteById(Long id) {
        advertisements = loadFromFile();
        advertisements.removeIf(ad -> ad.getId().equals(id));
        saveToFile();
    }

    public List<Advertisement> findByAuthorId(long authorId) {
        return advertisements.stream()
                .filter(ad -> ad.getAuthorId() == authorId)
                .collect(Collectors.toList());
    }

    public List<Advertisement> findActiveAds() {
        return advertisements.stream()
                .filter(ad -> ad.getStatus() == entity.AdStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Advertisement> findByCategory(String category) {
        return advertisements.stream()
                .filter(ad -> ad.getStatus() == entity.AdStatus.ACTIVE)
                .filter(ad -> ad.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Advertisement> findByTitleContaining(String keyword) {
        return advertisements.stream()
                .filter(ad -> ad.getStatus() == entity.AdStatus.ACTIVE)
                .filter(ad -> ad.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public long getNextId() {
        return advertisements.stream().mapToLong(Advertisement::getId).max().orElse(0) + 1;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(advertisements);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения объявлений: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Advertisement> loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Advertisement>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки объявлений: " + e.getMessage(), e);
        }
    }
}
