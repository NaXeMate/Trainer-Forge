package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.VideogameNotFoundException;
import dev.trainerforge.model.entities.Videogame;
import dev.trainerforge.model.entities.VideogamePokedex;
import dev.trainerforge.repository.VideogamePokedexRepository;
import dev.trainerforge.repository.VideogameRepository;
import dev.trainerforge.validator.VideogameValidator;

@Transactional(readOnly = true)
@Service
public class VideogameService {

    private final VideogameRepository videogameRepo;
    private final VideogamePokedexRepository videogamePokedexRepo;

    public VideogameService(VideogameRepository videogameRepo, VideogamePokedexRepository videogamePokedexRepo) {
        this.videogameRepo = videogameRepo;
        this.videogamePokedexRepo = videogamePokedexRepo;
    }

    public List<Videogame> findAll() {
        return videogameRepo.findAll();
    }

    public Videogame findById(Long id) {
        return videogameRepo.findById(id)
        .orElseThrow(() -> new VideogameNotFoundException(id));
    }

    public Videogame findByName(String name) {
        return videogameRepo.findByName(name)
         .orElseThrow(() -> new VideogameNotFoundException("Videogame not found with name: " + name + "."));
    }

    /**
     * Filters videogames by generation after validating the supported generation range.
     *
     * @param generationId identifier of the generation used to filter videogames.
     * @return all videogames assigned to the requested generation.
     * @throws IllegalArgumentException when the generation identifier is outside the supported range.
     * @throws VideogameNotFoundException when no videogame is registered for the provided generation.
     */
    public List<Videogame> findByGenerationId(Long generationId) {
        VideogameValidator.validateGenerationId(generationId);

        List<Videogame> result = videogameRepo.findByGenerationId(generationId);
        VideogameValidator.validateByGenerationResult(result, generationId);

        return result;
    }

    /**
     * Filters videogames by region after validating the configured region boundaries.
     *
     * @param regionId identifier of the region used to filter videogames.
     * @return all videogames linked to the requested region.
     * @throws IllegalArgumentException when the region identifier is outside the supported range.
     * @throws VideogameNotFoundException when no videogame is associated with the provided region.
     */
    public List<Videogame> findByRegionId(Long regionId) {
        VideogameValidator.validateRegionId(regionId);

        List<Videogame> result = videogameRepo.findByRegionId(regionId);
        VideogameValidator.validateByRegionResult(result, regionId);

        return result;
    }

    public boolean existsById(Long id) {
        return videogameRepo.existsById(id);
    }

    /**
     * Retrieves all Pokedex entries available in a specific videogame.
     *
     * @param videogameId identifier of the videogame whose roster is requested.
     * @return the videogame-pokedex associations available for the given videogame.
     * @throws VideogameNotFoundException when the videogame has no associated pokemon entries.
     */
    public List<VideogamePokedex> findPokedexByVideogameId(Long videogameId) {
        List<VideogamePokedex> result = videogamePokedexRepo.findByVideogameId(videogameId);
        VideogameValidator.validateByPokedexResult(result, videogameId);

        return result;
    }
}
