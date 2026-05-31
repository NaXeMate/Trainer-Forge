package dev.trainerforge.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "videogames_pokedex")
public class VideogamePokedex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pokedex_id", nullable = false)
    private Pokedex pokedex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "videogame_id", nullable = false)
    private Videogame videogame;

    public VideogamePokedex() {}

    public VideogamePokedex(Pokedex pokedex, Videogame videogame) {
        this.pokedex = pokedex;
        this.videogame = videogame;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pokedex getPokedex() {
        return pokedex;
    }

    public void setPokedex(Pokedex pokedex) {
        this.pokedex = pokedex;
    }

    public Videogame getVideogame() {
        return videogame;
    }

    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }
}
