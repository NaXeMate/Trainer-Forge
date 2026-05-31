// SectionCard component — reusable card used on the Home page to navigate to each main section

import { useNavigate } from "react-router-dom";
import "./sectionCard.css";

type SectionCardProps = {
  title: string;
  description: string;
  imageSrc: string;
  imageAlt: string;
  linkTo: string;
};

function SectionCard({
  title,
  description,
  imageSrc,
  imageAlt,
  linkTo,
}: SectionCardProps) {
  const navigate = useNavigate();

  const handleEnter = () => {
    navigate(linkTo);
  };

  return (
    <article className="section-card">
      <h2 className="section-card__title">{title}</h2>
      <p className="section-card__description">{description}</p>

      <div className="section-card__image-wrapper">
        <img
          src={imageSrc}
          alt={imageAlt}
          className="section-card__image"
        />
      </div>

      <button type="button" className="section-card__btn" onClick={handleEnter}>
        ENTER
        <span className="section-card__arrow" aria-hidden="true">
          →
        </span>
      </button>
    </article>
  );
}

export default SectionCard;
