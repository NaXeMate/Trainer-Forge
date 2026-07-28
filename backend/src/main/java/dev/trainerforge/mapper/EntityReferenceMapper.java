package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import dev.trainerforge.model.entities.*;
import dev.trainerforge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityReferenceMapper {

    private final RegionRepository regionRepository;
    private final GenerationRepository generationRepository;
    private final PokedexRepository pokedexRepository;
    private final VideogameRepository videogameRepository;
    private final AbilityRepository abilityRepository;
    private final AchievementRepository achievementRepository;
    private final GamePossessionRepository gamePossessionRepository;
    private final MoveRepository moveRepository;
    private final MovePokedexRepository movePokedexRepository;
    private final MoveSecondaryEffectRepository moveSecondaryEffectRepository;
    private final MoveTargetRepository moveTargetRepository;
    private final PokedexItemRepository pokedexItemRepository;
    private final PokemonRepository pokemonRepository;
    private final PokemonItemRepository pokemonItemRepository;
    private final PokemonTeamRepository pokemonTeamRepository;
    private final NatureRepository natureRepository;
    private final PokemonTypeRepository pokemonTypeRepository;
    private final TeamRepository teamRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerAchievementRepository trainerAchievementRepository;
    private final TypeEffectivenessRepository typeEffectivenessRepository;
    private final VideogamePokedexRepository videogamePokedexRepository;

    // String / Primitive → Entity

    public Region mapRegion(String name) {
        if (name == null) return null;
        return regionRepository.findByName(name).orElse(null);
    }

    public Generation mapGeneration(String name) {
        if (name == null) return null;
        return generationRepository.findByName(name).orElse(null);
    }

    public Generation mapGenerationFromInt(int number) {
        return generationRepository.findById((long) number).orElse(null);
    }

    public Pokedex mapPokedex(String name) {
        if (name == null) return null;
        return pokedexRepository.findByName(name).orElse(null);
    }

    public Pokedex mapPokedexFromLong(Long id) {
        if (id == null) return null;
        return pokedexRepository.findById(id).orElse(null);
    }

    public Videogame mapVideogame(String name) {
        if (name == null) return null;
        return videogameRepository.findByName(name).orElse(null);
    }

    public Ability mapAbility(String name) {
        if (name == null) return null;
        return abilityRepository.findByName(name).orElse(null);
    }

    public Achievement mapAchievement(String name) {
        if (name == null) return null;
        return achievementRepository.findByName(name).orElse(null);
    }

    public Move mapMove(String name) {
        if (name == null) return null;
        return moveRepository.findByName(name).orElse(null);
    }

    public MoveTarget mapMoveTarget(String name) {
        if (name == null) return null;
        return new MoveTarget(name);
    }

    public MoveSecondaryEffect mapMoveSecondaryEffect(String name) {
        if (name == null) return null;
        return new MoveSecondaryEffect(name);
    }

    public PokemonItem mapPokemonItem(String name) {
        if (name == null) return null;
        return pokemonItemRepository.findByName(name).orElse(null);
    }

    public Nature mapNature(String name) {
        if (name == null) return null;
        return natureRepository.findByName(name).orElse(null);
    }

    public PokemonType mapPokemonType(String name) {
        if (name == null) return null;
        return pokemonTypeRepository.findByName(name).orElse(null);
    }

    public Trainer mapTrainer(String username) {
        if (username == null) return null;
        return trainerRepository.findByUsername(username).orElse(null);
    }
    
    // Entity → String / Primitive
    public String map(Region r) { return r == null ? null : r.getName(); }
    public String map(Generation g) { return g == null ? null : g.getName(); }
    public String map(Pokedex p) { return p == null ? null : p.getName(); }
    public Long mapToLong(Pokedex p) { return p == null ? null : p.getId(); }
    public String map(Videogame v) { return v == null ? null : v.getName(); }
    public String map(Ability a) { return a == null ? null : a.getName(); }
    public String map(Achievement ac) { return ac == null ? null : ac.getName(); }
    public String map(Move m) { return m == null ? null : m.getName(); }
    public String map(MoveTarget t) { return t == null ? null : t.getTarget(); }
    public String map(MoveSecondaryEffect se) { return se == null ? null : se.getSecondaryEffect(); }
    public String map(PokemonItem i) { return i == null ? null : i.getName(); }
    public String map(Nature n) { return n == null ? null : n.getName(); }
    public String map(PokemonType t) { return t == null ? null : t.getName(); }
    public int mapToInt(Generation g) { return g == null ? 0 : (int) (long) g.getId(); }
    public String map(Trainer t) { return t == null ? null : t.getUsername(); }

    public Set<Achievement> mapAchievements(Long[] achievementsIds) {
        Set<Achievement> achievements = new HashSet<>();
        if (achievementsIds != null) {
            for (Long id : achievementsIds) {
                if (id != null) {
                    achievementRepository.findById(id).ifPresent(achievements::add);
                }
            }
        }
        return achievements;
    }

    public Long[] mapAchievementsIds(Set<Achievement> achievements) {
        if (achievements == null || achievements.isEmpty()) {
            return new Long[0];
        }
        return achievements.stream().map(achievement -> achievement.getId()).toArray(Long[]::new);
    }

    public Set<Ability> mapAbilities(Long[] abilitiesIds) {
        Set<Ability> abilities = new HashSet<>();
        if (abilitiesIds != null) {
            for (Long id : abilitiesIds) {
                if (id != null) {
                    abilityRepository.findById(id).ifPresent(abilities::add);
                }
            }
        }
        return abilities;
    }

    public Long[] mapAbilitiesIds(Set<Ability> abilities) {
        if (abilities == null || abilities.isEmpty()) {
            return new Long[0];
        }
        return abilities.stream().map(ability -> ability.getId()).toArray(Long[]::new);
    }

    public Set<GamePossession> mapGamePossessions(Long[] gamePossessionsIds) {
        Set<GamePossession> gamePossessions = new HashSet<>();
        if (gamePossessionsIds != null) {
            for (Long id : gamePossessionsIds) {
                if (id != null) {
                    gamePossessionRepository.findById(id).ifPresent(gamePossessions::add);
                }
            }
        }
        return gamePossessions;
    }

    public Long[] mapGamePossessionsIds(Set<GamePossession> gamePossessions) {
        if (gamePossessions == null || gamePossessions.isEmpty()) {
            return new Long[0];
        }
        return gamePossessions.stream().map(gamePossession -> gamePossession.getId()).toArray(Long[]::new);
    }

    public Set<Generation> mapGeneration(Long[] generationsIds) {
        Set<Generation> generations = new HashSet<>();
        if (generationsIds != null) {
            for (Long id : generationsIds) {
                if (id != null) {
                    generationRepository.findById(id).ifPresent(generations::add);
                }
            }
        }
        return generations;
    }

    public Long[] mapGenerationsIds(Set<Generation> generations) {
        if (generations == null || generations.isEmpty()) {
            return new Long[0];
        }
        return generations.stream().map(generation -> generation.getId()).toArray(Long[]::new);
    }

    public Set<Move> mapMove(Long[] movesIds) {
        Set<Move> moves = new HashSet<>();
        if (movesIds != null) {
            for (Long id : movesIds) {
                if (id != null) {
                    moveRepository.findById(id).ifPresent(moves::add);
                }
            }
        }
        return moves;
    }

    public Long[] mapMovesIds(Set<Move> moves) {
        if (moves == null || moves.isEmpty()) {
            return new Long[0];
        }
        return moves.stream().map(move -> move.getId()).toArray(Long[]::new);
    }

    public Set<MovePokedex> mapMovePokedex(Long[] movePokedexesIds) {
        Set<MovePokedex> movePokedexes = new HashSet<>();
        if (movePokedexesIds != null) {
            for (Long id : movePokedexesIds) {
                if (id != null) {
                    movePokedexRepository.findById(id).ifPresent(movePokedexes::add);
                }
            }
        }
        return movePokedexes;
    }

    public Long[] mapMovePokedexesIds(Set<MovePokedex> movePokedexes) {
        if (movePokedexes == null || movePokedexes.isEmpty()) {
            return new Long[0];
        }
        return movePokedexes.stream().map(movePokedex -> movePokedex.getId()).toArray(Long[]::new);
    }

    public Set<MoveSecondaryEffect> mapSecondaryEffects(Long[] secondaryEffectsIds) {
        Set<MoveSecondaryEffect> secondaryEffects = new HashSet<>();
        if (secondaryEffectsIds != null) {
            for (Long id : secondaryEffectsIds) {
                if (id != null) {
                    moveSecondaryEffectRepository.findById(id).ifPresent(secondaryEffects::add);
                }
            }
        }
        return secondaryEffects;
    }

    public Long[] mapSecondaryEffectsIds(Set<MoveSecondaryEffect> secondaryEffects) {
        if (secondaryEffects == null || secondaryEffects.isEmpty()) {
            return new Long[0];
        }
        return secondaryEffects.stream().map(secondaryEffect -> secondaryEffect.getId()).toArray(Long[]::new);
    }

    public Set<MoveTarget> mapMoveTargets(Long[] moveTargetIds) {
        Set<MoveTarget> moveTargets = new HashSet<>();
        if (moveTargetIds != null) {
            for (Long id : moveTargetIds) {
                if (id != null) {
                    moveTargetRepository.findById(id).ifPresent(moveTargets::add);
                }
            }
        }
        return moveTargets;
    }

    public Long[] mapMoveTargetIds(Set<MoveTarget> moveTargets) {
        if (moveTargets == null || moveTargets.isEmpty()) {
            return new Long[0];
        }
        return moveTargets.stream().map(moveTarget -> moveTarget.getId()).toArray(Long[]::new);
    }

    public Set<Nature> mapNatures(Long[] natureIds) {
        Set<Nature> natures = new HashSet<>();
        if (natureIds != null) {
            for (Long id : natureIds) {
                if (id != null) {
                    natureRepository.findById(id).ifPresent(natures::add);
                }
            }
        }
        return natures;
    }

    public Long[] mapNatureIds(Set<Nature> natures) {
        if (natures == null || natures.isEmpty()) {
            return new Long[0];
        }
        return natures.stream().map(nature -> nature.getId()).toArray(Long[]::new);
    }

    public Set<Pokedex> mapPokedex(Long[] pokedexesIds) {
        Set<Pokedex> pokedexes = new HashSet<>();
        if (pokedexesIds != null) {
            for (Long id : pokedexesIds) {
                if (id != null) {
                    pokedexRepository.findById(id).ifPresent(pokedexes::add);
                }
            }
        }
        return pokedexes;
    }

    public Long[] mapPokedexesIds(Set<Pokedex> pokedexes) {
        if (pokedexes == null || pokedexes.isEmpty()) {
            return new Long[0];
        }
        return pokedexes.stream().map(pokedex -> pokedex.getId()).toArray(Long[]::new);
    }

    public Set<PokedexItem> mapPokedexItems(Long[] pokedexItemIds) {
        Set<PokedexItem> pokedexItems = new HashSet<>();
        if (pokedexItemIds != null) {
            for (Long id : pokedexItemIds) {
                if (id != null) {
                    pokedexItemRepository.findById(id).ifPresent(pokedexItems::add);
                }
            }
        }
        return pokedexItems;
    }

    public Long[] mapPokedexItemIds(Set<PokedexItem> pokedexItems) {
        if (pokedexItems == null || pokedexItems.isEmpty()) {
            return new Long[0];
        }
        return pokedexItems.stream().map(pokedexItem -> pokedexItem.getId()).toArray(Long[]::new);
    }

    public Set<Pokemon> mapPokemons(Long[] pokemonsIds) {
        Set<Pokemon> pokemons = new HashSet<>();
        if (pokemonsIds != null) {
            for (Long id : pokemonsIds) {
                if (id != null) {
                    pokemonRepository.findById(id).ifPresent(pokemons::add);
                }
            }
        }
        return pokemons;
    }

    public Long[] mapPokemonsIds(Set<Pokemon> pokemons) {
        if (pokemons == null || pokemons.isEmpty()) {
            return new Long[0];
        }
        return pokemons.stream().map(pokemon -> pokemon.getId()).toArray(Long[]::new);
    }

    public Set<PokemonItem> mapPokemonItems(Long[] pokemonItemsIds) {
        Set<PokemonItem> pokemonItems = new HashSet<>();
        if (pokemonItemsIds != null) {
            for (Long id : pokemonItemsIds) {
                if (id != null) {
                    pokemonItemRepository.findById(id).ifPresent(pokemonItems::add);
                }
            }
        }
        return pokemonItems;
    }

    public Long[] mapPokemonItemsIds(Set<PokemonItem> pokemonItems) {
        if (pokemonItems == null || pokemonItems.isEmpty()) {
            return new Long[0];
        }
        return pokemonItems.stream().map(pokemonItem -> pokemonItem.getId()).toArray(Long[]::new);
    }

    public Set<PokemonTeam> mapPokemonTeams(Long[] pokemonTeamIds) {
        Set<PokemonTeam> pokemonTeams = new HashSet<>();
        if (pokemonTeamIds != null) {
            for (Long id : pokemonTeamIds) {
                if (id != null) {
                    pokemonTeamRepository.findById(id).ifPresent(pokemonTeams::add);
                }
            }
        }
        return pokemonTeams;
    }

    public Long[] mapPokemonTeamIds(Set<PokemonTeam> pokemonTeams) {
        if (pokemonTeams == null || pokemonTeams.isEmpty()) {
            return new Long[0];
        }
        return pokemonTeams.stream().map(pokemonTeam -> pokemonTeam.getId()).toArray(Long[]::new);
    }

    public Set<PokemonType> mapPokemonTypes(Long[] pokemonTypeIds) {
        Set<PokemonType> pokemonTypes = new HashSet<>();
        if (pokemonTypeIds != null) {
            for (Long id : pokemonTypeIds) {
                if (id != null) {
                    pokemonTypeRepository.findById(id).ifPresent(pokemonTypes::add);
                }
            }
        }
        return pokemonTypes;
    }

    public Long[] mapPokemonTypeIds(Set<PokemonType> pokemonTypes) {
        if (pokemonTypes == null || pokemonTypes.isEmpty()) {
            return new Long[0];
        }
        return pokemonTypes.stream().map(pokemonType -> pokemonType.getId()).toArray(Long[]::new);
    }

    public Set<Region> mapRegions(Long[] regionsIds) {
        Set<Region> regions = new HashSet<>();
        if (regionsIds != null) {
            for (Long id : regionsIds) {
                if (id != null) {
                    regionRepository.findById(id).ifPresent(regions::add);
                }
            }
        }
        return regions;
    }

    public Long[] mapRegionsIds(Set<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return new Long[0];
        }
        return regions.stream().map(region -> region.getId()).toArray(Long[]::new);
    }

    public Set<Team> mapTeams(Long[] teamsIds) {
        Set<Team> teams = new HashSet<>();
        if (teamsIds != null) {
            for (Long id : teamsIds) {
                if (id != null) {
                    teamRepository.findById(id).ifPresent(teams::add);
                }
            }
        }
        return teams;
    }

    public Long[] mapTeamsIds(Set<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return new Long[0];
        }
        return teams.stream().map(team -> team.getId()).toArray(Long[]::new);
    }

    public Set<Trainer> mapTrainers(Long[] trainersIds) {
        Set<Trainer> trainers = new HashSet<>();
        if (trainersIds != null) {
            for (Long id : trainersIds) {
                if (id != null) {
                    trainerRepository.findById(id).ifPresent(trainers::add);
                }
            }
        }
        return trainers;
    }

    public Long[] mapTrainersIds(Set<Trainer> trainers) {
        if (trainers == null || trainers.isEmpty()) {
            return new Long[0];
        }
        return trainers.stream().map(trainer -> trainer.getId()).toArray(Long[]::new);
    }

    public Set<TrainerAchievement> mapTrainerAchievements(Long[] trainerAchievementIds) {
        Set<TrainerAchievement> trainerAchievements = new HashSet<>();
        if (trainerAchievementIds != null) {
            for (Long id : trainerAchievementIds) {
                if (id != null) {
                    trainerAchievementRepository.findById(id).ifPresent(trainerAchievements::add);
                }
            }
        }
        return trainerAchievements;
    }

    public Long[] mapTrainerAchievementIds(Set<TrainerAchievement> trainerAchievements) {
        if (trainerAchievements == null || trainerAchievements.isEmpty()) {
            return new Long[0];
        }
        return trainerAchievements.stream().map(trainerAchievement -> trainerAchievement.getId()).toArray(Long[]::new);
    }

    public Set<TypeEffectiveness> mapTypeEffectiveness(Long[] typeEffectivenessIds) {
        Set<TypeEffectiveness> typeEffectivenessSet = new HashSet<>();
        if (typeEffectivenessIds != null) {
            for (Long id : typeEffectivenessIds) {
                if (id != null) {
                    typeEffectivenessRepository.findById(id).ifPresent(typeEffectivenessSet::add);
                }
            }
        }
        return typeEffectivenessSet;
    }

    public Long[] mapTypeEffectivenessIds(Set<TypeEffectiveness> typeEffectivenessSet) {
        if (typeEffectivenessSet == null || typeEffectivenessSet.isEmpty()) {
            return new Long[0];
        }
        return typeEffectivenessSet.stream().map(typeEffectiveness -> typeEffectiveness.getId()).toArray(Long[]::new);
    }

    public Set<Videogame> mapVideogames(Long[] videogamesIds) {
        Set<Videogame> videogames = new HashSet<>();
        if (videogamesIds != null) {
            for (Long id : videogamesIds) {
                if (id != null) {
                    videogameRepository.findById(id).ifPresent(videogames::add);
                }
            }
        }
        return videogames;
    }

    public Long[] mapVideogamesIds(Set<Videogame> videogames) {
        if (videogames == null || videogames.isEmpty()) {
            return new Long[0];
        }
        return videogames.stream().map(videogame -> videogame.getId()).toArray(Long[]::new);
    }

    public Set<VideogamePokedex> mapVideogamePokedexes(Long[] videogamePokedexesIds) {
        Set<VideogamePokedex> videogamePokedexes = new HashSet<>();
        if (videogamePokedexesIds != null) {
            for (Long id : videogamePokedexesIds) {
                if (id != null) {
                    videogamePokedexRepository.findById(id).ifPresent(videogamePokedexes::add);
                }
            }
        }
        return videogamePokedexes;
    }

    public Long[] mapVideogamePokedexesIds(Set<VideogamePokedex> videogamePokedexes) {
        if (videogamePokedexes == null || videogamePokedexes.isEmpty()) {
            return new Long[0];
        }
        return videogamePokedexes.stream().map(videogamePokedex -> videogamePokedex.getId()).toArray(Long[]::new);
    }
}
