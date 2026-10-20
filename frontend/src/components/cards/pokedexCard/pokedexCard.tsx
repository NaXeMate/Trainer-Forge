import { useLayoutEffect, useRef } from "react";
import cardRing from "../../../assets/icons/pokedex/card-ring.svg";
import type { PokedexEntry } from "../../../types/pokedexTypes";
import { getPokedexCardPresentation } from "../../../utils/pokemonPresentation";

import "./pokedexCard.css";

export default function PokedexCard({ pokemon }: { pokemon: PokedexEntry }) {
  const nameRef = useRef<HTMLHeadingElement>(null);
  const presentation = getPokedexCardPresentation(pokemon);

  useLayoutEffect(() => {
    const heading = nameRef.current;
    if (!heading) return;

    function fitName() {
      if (!heading) return;
      heading.style.fontSize = "";

      const availableWidth = heading.getBoundingClientRect().width;
      const range = document.createRange();
      range.selectNodeContents(heading);
      const textWidth = range.getBoundingClientRect().width;

      if (availableWidth > 1 && textWidth > availableWidth - 1) {
        let minSize = 0;
        let maxSize = parseFloat(getComputedStyle(heading).fontSize);

        // Measure each candidate: rendered glyph widths do not scale exactly linearly.
        for (let step = 0; step < 8; step += 1) {
          const size = (minSize + maxSize) / 2;
          heading.style.fontSize = `${size}px`;

          if (range.getBoundingClientRect().width <= availableWidth - 1) {
            minSize = size;
          } else {
            maxSize = size;
          }
        }

        heading.style.fontSize = `${Math.floor(minSize * 100) / 100}px`;
      }
    }

    fitName();
    const observer = new ResizeObserver(fitName);
    observer.observe(heading.parentElement!);
    document.fonts.addEventListener("loadingdone", fitName);

    return () => {
      observer.disconnect();
      document.fonts.removeEventListener("loadingdone", fitName);
    };
  }, [pokemon.name]);

  return (
    <article className={presentation.className} style={presentation.style}>
      <img className="pokedex-card__decoration" src={cardRing} alt="" />
      <span className="pokedex-card__regional-number">
        #{String(pokemon.id).padStart(3, "0")}
      </span>
      <img
        className="pokedex-card__image"
        src={pokemon.imageUrl}
        alt={pokemon.name}
        loading="lazy"
      />
      <div className="pokedex-card__caption">
        <span className="pokedex-card__national-number">
          #{String(pokemon.nationalPokedex).padStart(4, "0")}
        </span>
        <h2 ref={nameRef}>{pokemon.name}</h2>
      </div>
    </article>
  );
}
