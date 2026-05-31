// SectionCard — reusable card used on the Home page to navigate to each main section

import { useNavigate } from "react-router-dom";
import Button from "../button";
import "./sectionCard.css";

// Arrow icon used as trailing icon in the ENTER button
function ArrowIcon() {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <path
        d="M5 12h14M13 6l6 6-6 6"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

type SectionCardProps = {
  title: string; // Section name displayed as card heading
  description: string; // Short description of the section
  imageSrc: string; // Path to the section illustration image
  imageAlt: string; // Alt text for the image
  linkTo: string; // Route to navigate to when clicking ENTER
};

function SectionCard({
  title,
  description,
  imageSrc,
  imageAlt,
  linkTo,
}: SectionCardProps) {
  const navigate = useNavigate();

  return (
    <article className="section-card">
      <h3 className="section-card__title">{title}</h3>
      <p className="section-card__description">{description}</p>
      <div className="section-card__image-wrapper">
        <img
          src={imageSrc}
          alt={imageAlt}
          className="section-card__image"
          loading="lazy"
        />
      </div>
      <div className="section-card__actions">
        <Button
          label="ENTER"
          variant="secondary"
          trailingIcon={<ArrowIcon />}
          uppercase
          onClick={() => navigate(linkTo)}
        />
      </div>
    </article>
  );
}

export default SectionCard;
