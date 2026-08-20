package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.VideogamePokedexNotFoundException;
import dev.trainerforge.model.entities.VideogamePokedex;
import dev.trainerforge.repository.VideogamePokedexRepository;
import dev.trainerforge.validator.VideogamePokedexValidator;

@Transactional
@Service
public class VideogamePokedexService {

    private final VideogamePokedexRepository videogamePokedexRepo;
    private final PokedexService pokedexService;
    private final VideogameService videogameService;

    public VideogamePokedexService(VideogamePokedexRepository videogamePokedexRepo, PokedexService pokedexService, VideogameService videogameService) {
        this.videogamePokedexRepo = videogamePokedexRepo;
        this.pokedexService = pokedexService;
        this.videogameService = videogameService;
    }

    public List<VideogamePokedex> findAll() {
        return videogamePokedexRepo.findAll();
    }

    public VideogamePokedex findById(Long id) {
        return videogamePokedexRepo.findById(id)
        .orElseThrow(() -> new VideogamePokedexNotFoundException(id));
    }

    /**
     * Retrieves videogame-pokedex associations for a species after validating species existence.
     *
     * @param pokedexId identifier of the species used to filter associations.
     * @return every videogame-pokedex association linked to the species.
     * @throws VideogamePokedexNotFoundException when the species does not exist or has no associations.
     */
    public List<VideogamePokedex> findByPokedexId(Long pokedexId) {
        VideogamePokedexValidator.validatePokedexExists(pokedexService.existsById(pokedexId), pokedexId);

        List<VideogamePokedex> result = videogamePokedexRepo.findByPokedexId(pokedexId);
        VideogamePokedexValidator.validateByPokedexResult(result, pokedexId);

        return result;
    }

    /**
     * Retrieves videogame-pokedex associations for a videogame after validating videogame existence.
     *
     * @param videogameId identifier of the videogame used to filter associations.
     * @return every videogame-pokedex association linked to the videogame.
     * @throws VideogamePokedexNotFoundException when the videogame does not exist or has no associations.
     */
    public List<VideogamePokedex> findByVideogameId(Long videogameId) {
        VideogamePokedexValidator.validateVideogameExists(videogameService.existsById(videogameId), videogameId);

        List<VideogamePokedex> result = videogamePokedexRepo.findByVideogameId(videogameId);
        VideogamePokedexValidator.validateByVideogameResult(result, videogameId);

        return result;
    }
}
