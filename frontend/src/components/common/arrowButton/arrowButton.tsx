import type { ComponentPropsWithoutRef } from "react";

import "./arrowButton.css";

type ArrowButtonProps = Omit<ComponentPropsWithoutRef<"button">, "children" | "className" | "type"> & {
  direction?: "left" | "right";
};

export default function ArrowButton({ direction = "left", ...props }: ArrowButtonProps) {
  return (
    <button {...props} className="arrow-button" type="button" data-direction={direction}>
      <svg viewBox="0 0 24 24" aria-hidden="true">
        <path d="M19 12H5m0 0 7-7m-7 7 7 7" />
      </svg>
    </button>
  );
}
