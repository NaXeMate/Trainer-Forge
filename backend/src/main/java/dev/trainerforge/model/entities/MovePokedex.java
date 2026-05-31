package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.LearningMethod;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "moves_pokedex")
public class MovePokedex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pokedex_id", nullable = false)
    private Pokedex pokedex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "move_id", nullable = false)
    private Move move;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "videogame_id", nullable = false)
    private Videogame videogame;

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_method", nullable = false)
    private LearningMethod learningMethod;

    @Column
    private Integer level;

    public MovePokedex() {}

    public MovePokedex(Pokedex pokedex, Move move, Videogame videogame, LearningMethod learningMethod, Integer level) {
        this.pokedex = pokedex;
        this.move = move;
        this.videogame = videogame;
        this.learningMethod = learningMethod;
        this.level = level;
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

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Videogame getVideogame() {
        return videogame;
    }

    public void setVideogame(Videogame videogame) {
        this.videogame = videogame;
    }

    public LearningMethod getLearningMethod() {
        return learningMethod;
    }

    public void setLearningMethod(LearningMethod learningMethod) {
        this.learningMethod = learningMethod;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    
}
