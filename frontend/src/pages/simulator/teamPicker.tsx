import { sideLabels, type Side } from "./sides";

type TeamChoice = { key: string; name: string };

type TeamPickerProps<Choice extends TeamChoice> = {
  side: Side;
  groups: { title: string; choices: Choice[] }[];
  currentKey?: string;
  oppositeKey?: string;
  canSwap: (choice: Choice) => boolean;
  onChoose: (choice: Choice) => void;
};

export default function TeamPicker<Choice extends TeamChoice>({
  side,
  groups,
  currentKey,
  oppositeKey,
  canSwap,
  onChoose,
}: TeamPickerProps<Choice>) {
  return (
    <>
      <h2 id="simulator-picker-title">Choose team for {sideLabels[side]}</h2>

      {groups.map(({ title, choices }) => {
        if (choices.length === 0) return null;

        return (
          <section key={title} className="simulator-picker__group">
            <h3>{title}</h3>
            <ul>
              {choices.map(choice => {
                const current = choice.key === currentKey;
                const opposite = choice.key === oppositeKey;
                const swappable = canSwap(choice);
                const swapLabel = currentKey === undefined ? "Move to this side" : "Swap sides";

                return (
                  <li key={choice.key}>
                    <button type="button" disabled={current || !swappable} onClick={() => onChoose(choice)}>
                      <strong>{choice.name}</strong>
                      {current && <span>Currently selected</span>}
                      {opposite && <span>{swappable ? swapLabel : "Cannot swap the current teams"}</span>}
                    </button>
                  </li>
                );
              })}
            </ul>
          </section>
        );
      })}

      <form method="dialog">
        <button className="simulator-picker__cancel" type="submit">Cancel</button>
      </form>
    </>
  );
}
