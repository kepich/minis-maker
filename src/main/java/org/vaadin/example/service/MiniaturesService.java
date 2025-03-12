package org.vaadin.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.vaadin.example.Miniature;
import org.vaadin.example.pdf.Floor;

import java.awt.image.BufferedImage;
import java.util.*;

@Service
@SessionScope
public class MiniaturesService {
    private final Map<String, Miniature> files = new HashMap<>();
    private Miniature selected = null;

    public Collection<Miniature> miniatures() {
        return files.values();
    }

    public Optional<Miniature> selected() {
        return Optional.ofNullable(selected);
    }

    public Optional<Miniature> select(Miniature miniature) {
        selected = miniature;
        return Optional.ofNullable(selected);
    }

    public Collection<Miniature> addAndSelect(Miniature miniature) {
        selected = miniature;
        files.put(miniature.getFileName(), miniature);
        return files.values();
    }

    public Collection<Miniature> remove(String fileName) {
        files.remove(fileName);
        return files.values();
    }

    public ArrayList<Floor> getFloors() {
        Map<Integer, ArrayList<Miniature>> miniatureMap = getSortedBySizeMap();
        return getFloors(miniatureMap);
    }

    private Map<Integer, ArrayList<Miniature>> getSortedBySizeMap() {
        Map<Integer, ArrayList<Miniature>> miniatureMap = new HashMap<>();

        files.values().forEach(miniature -> {
            BufferedImage image = miniature.getDoubledImage();
            if (miniatureMap.containsKey(image.getHeight())) {
                miniatureMap.get(image.getHeight()).add(miniature);
            } else {
                ArrayList<Miniature> list = new ArrayList<>();
                list.add(miniature);
                miniatureMap.put(image.getHeight(), list);
            }
        });
        return miniatureMap;
    }

    private ArrayList<Floor> getFloors(Map<Integer, ArrayList<Miniature>> miniatureMap) {
        ArrayList<Floor> floors = new ArrayList<>();
        miniatureMap.keySet().stream().sorted((i1, i2) -> i2 - i1).forEach(key -> miniatureMap.get(key).forEach(miniature -> {
            BufferedImage image = miniature.getDoubledImage();
            int minisNeedToPush = miniature.getNumber();

            for (int currentFloor = 0; currentFloor < floors.size() && minisNeedToPush > 0; currentFloor++) {
                minisNeedToPush = floors.get(currentFloor).putMinis(image, minisNeedToPush);
            }

            while (minisNeedToPush > 0) {
                Floor newFloor = new Floor(image.getHeight());
                floors.add(newFloor);
                minisNeedToPush = newFloor.putMinis(image, minisNeedToPush);
            }
        }));
        return floors;
    }
}
