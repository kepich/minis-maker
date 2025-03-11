package org.vaadin.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.vaadin.example.Miniature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
}
