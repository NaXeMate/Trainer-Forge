package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.ItemRelationship;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokedex_items")
public class PokedexItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Pokedex pokedex;

    private PokemonItem item;

    private ItemRelationship relationship;

    public PokedexItem() {}

    public PokedexItem(Pokedex pokedex, PokemonItem item, ItemRelationship relationship) {
        this.pokedex = pokedex;
        this.item = item;
        this.relationship = relationship;
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

    public PokemonItem getItem() {
        return item;
    }

    public void setItem(PokemonItem item) {
        this.item = item;
    }

    public ItemRelationship getRelationship() {
        return relationship;
    }

    public void setRelationship(ItemRelationship relationship) {
        this.relationship = relationship;
    }
}
