package dev.trainerforge.model.entities;

import java.util.ArrayList;
import java.util.List;

import dev.trainerforge.model.enumerated.TeamModality;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "videogame_id", nullable = false)
    private Videogame videogame;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamModality modality;

    @Column(name = "hidden", nullable = false)
    private boolean isHidden;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokemonTeam> pokemonTeams = new ArrayList<>();

    public Team() {}

    public Team(Trainer trainer, Videogame videogame, String name, TeamModality modality) {
        this.trainer = trainer;
        this.videogame = videogame;
        this.name = name;
        this.modality = modality;
        this.isHidden = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Videogame getVideogame() {
        return videogame;
    }

    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamModality getModality() {
        return modality;
    }

    public void setModality(TeamModality modality) {
        this.modality = modality;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public List<PokemonTeam> getPokemonTeams() {
        return pokemonTeams;
    }

    public void setPokemonTeams(List<PokemonTeam> pokemonTeams) {
        this.pokemonTeams = pokemonTeams;
    }

    

    
}
