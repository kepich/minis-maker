package org.vaadin.example.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.vaadin.example.Miniature;

import java.util.*;

@Service
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
}
