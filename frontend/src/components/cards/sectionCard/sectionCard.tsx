import type { HTMLAttributes, MouseEventHandler } from "react";

import AccessButton from "../../common/accessButton";

import "./sectionCard.css";

export type SectionCardProps = Omit<HTMLAttributes<HTMLElement>, "title"> & {
  title: string;
  description: string;
  image?: string;
  imageAlt?: string;
  onAccess?: MouseEventHandler<HTMLButtonElement>;
};

export default function SectionCard({
  title,
  description,
  image,
  imageAlt = "",
  onAccess,
  className,
  ...props
}: SectionCardProps) {
  const cardClassName = ["section-card", className].filter(Boolean).join(" ");

  return (
    <article {...props} className={cardClassName}>
      <h2 className="section-card__title">{title}</h2>

      <p className="section-card__description">{description}</p>

      <div
        aria-hidden={image ? undefined : true}
        className="section-card__image-frame">
        {image && <img className="section-card__image" src={image} alt={imageAlt} />}
      </div>

      <AccessButton
        aria-label={`Enter ${title}`}
        className="section-card__access-button"
        onClick={onAccess}/>
    </article>
  );
}
