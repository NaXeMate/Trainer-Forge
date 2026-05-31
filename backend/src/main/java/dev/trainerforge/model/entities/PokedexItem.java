package dev.trainerforge.model.entities;

import dev.trainerforge.model.enumerated.ItemRelationship;
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
@Table(name = "pokedex_items")
public class PokedexItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pokedex_id", nullable = false)
    private Pokedex pokedex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private PokemonItem item;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_relationship", nullable = false)
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
