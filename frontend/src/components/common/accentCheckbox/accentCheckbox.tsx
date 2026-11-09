import "./accentCheckbox.css";

type AccentCheckboxProps = {
  checked: boolean;
  onCheckedChange: (checked: boolean) => void;
  className?: string;
  "aria-label"?: string;
};

export default function AccentCheckbox({
  checked,
  onCheckedChange,
  className,
  "aria-label": ariaLabel,
}: AccentCheckboxProps) {
  return (
    <input
      className={className ? `accent-checkbox ${className}` : "accent-checkbox"}
      type="checkbox"
      checked={checked}
      aria-label={ariaLabel}
      onChange={event => onCheckedChange(event.currentTarget.checked)}
    />
  );
}
