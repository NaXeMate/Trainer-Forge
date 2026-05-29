package dev.trainerforge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.trainerforge.exception.notfound.VideogameNotFoundException;
import dev.trainerforge.model.entities.Videogame;
import dev.trainerforge.model.entities.VideogamePokedex;
import dev.trainerforge.repository.VideogamePokedexRepository;
import dev.trainerforge.repository.VideogameRepository;

@Transactional(readOnly = true)
@Service
public class VideogameService {

    private final VideogameRepository videogameRepo;
    private final VideogamePokedexRepository videogamePokedexRepo;

    private static final Long GENERATION_MAX_ID = 10L;

    private static final Long REGION_MAX_ID = 10L;

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

    public List<Videogame> findByGenerationId(Long generationId) {
        if (generationId < 1 || generationId > GENERATION_MAX_ID) {
            throw new IllegalArgumentException("Generation ID must be between 1 and " + GENERATION_MAX_ID + ".");
        }

        List<Videogame> result = videogameRepo.findByGenerationId(generationId);
        
        if (result.isEmpty()) {
            throw new VideogameNotFoundException("No videogames found for Generation ID: " + generationId + ".");
        }
        
        return result;
    }

    public List<Videogame> findByRegionId(Long regionId) {
        if (regionId < 1 || regionId > REGION_MAX_ID) {
            throw new IllegalArgumentException("Region ID must be between 1 and " + REGION_MAX_ID + ".");
        }

        List<Videogame> result = videogameRepo.findByRegionId(regionId);
        
        if (result.isEmpty()) {
            throw new VideogameNotFoundException("No videogames found for Region ID: " + regionId + ".");
        }

        return result;
    }

    public boolean existsById(Long id) {
        return videogameRepo.existsById(id);
    }

    public List<VideogamePokedex> findPokedexByVideogameId(Long videogameId) {
        List<VideogamePokedex> result = videogamePokedexRepo.findByVideogameId(videogameId);
        
        if (result.isEmpty()) {
            throw new VideogameNotFoundException("There are no Pokemon in this videogame ID: " + videogameId + ".");
        }
        
        return result;
    }
}
