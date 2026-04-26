package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.LearningMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "moves_pokedex")
public class MovePokedex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Pokedex pokedex;

    private Move move;

    private Videogame videogame;

    private LearningMethod learningMethod;

    private int level;

    public MovePokedex() {}

    public MovePokedex(Pokedex pokedex, Move move, Videogame videogame, LearningMethod learningMethod, int level) {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    
}
