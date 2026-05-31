package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.PokedexItemNotFoundException;
import dev.trainerforge.model.entities.PokedexItem;
import dev.trainerforge.repository.PokedexItemRepository;

@Transactional(readOnly = true)
@Service
public class PokedexItemService {

    private final PokedexItemRepository pokedexItemRepo;

    public PokedexItemService(PokedexItemRepository pokedexItemRepo) {
        this.pokedexItemRepo = pokedexItemRepo;
    }

    public List<PokedexItem> findAll() {
        return pokedexItemRepo.findAll();
    }

    public PokedexItem findById(Long id) {
        return pokedexItemRepo.findById(id)
        .orElseThrow(() -> new PokedexItemNotFoundException(id));
    }
}
