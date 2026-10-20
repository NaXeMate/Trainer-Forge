import { useId, useRef } from "react";
import type { CSSProperties, PointerEvent } from "react";
import selectorCircle from "../../../assets/icons/pokedex/selector-circle.svg";
import selectorRight from "../../../assets/icons/pokedex/selector-right.svg";
import selectorLeft from "../../../assets/icons/pokedex/selector-left.svg";
import statHelp from "../../../assets/icons/pokedex/stat-help.svg";
import { BASE_STAT_MIN, BASE_STAT_MAX } from "../../../constants/pokedex";
import type { PokedexStatFilter, StatFilterMode } from "../../../types/pokedexTypes";
import "./statisticSlider.css";

export interface StatisticSliderProps {
  label: string;
  filter: PokedexStatFilter;
  onChange: (filter: PokedexStatFilter) => void;
}

const modes = {
  equal: { next: "minimum", icon: selectorCircle, rotation: "0deg", text: "exactly" },
  minimum: { next: "maximum", icon: selectorRight, rotation: "90deg", text: "at least" },
  maximum: { next: "equal", icon: selectorLeft, rotation: "-90deg", text: "at most" },
} as const satisfies Record<StatFilterMode, { next: StatFilterMode; icon: string; rotation: string; text: string }>;

type SliderStyle = CSSProperties & { "--stat-thumb": string; "--stat-rotation": string; "--stat-track": string };

export default function StatisticSlider({ label, filter, onChange }: StatisticSliderProps) {
  const instructionsId = useId();
  const pointerStart = useRef<{ x: number; y: number; onThumb: boolean } | null>(null);
  const mode = modes[filter.mode];
  const position = (filter.value - BASE_STAT_MIN) / (BASE_STAT_MAX - BASE_STAT_MIN) * 100;
  const accent = "var(--color-accent)";
  const muted = "var(--color-glass-tint)";
  const track = filter.mode === "equal" ? `linear-gradient(${accent}, ${accent})` : filter.mode === "minimum"
    ? `linear-gradient(to right, ${muted} ${position}%, ${accent} ${position}%)`
    : `linear-gradient(to right, ${accent} ${position}%, ${muted} ${position}%)`;
  const style: SliderStyle = {
    "--stat-thumb": `url("${mode.icon}")`,
    "--stat-rotation": mode.rotation,
    "--stat-track": track,
  };

  function cycleMode() {
    onChange({ ...filter, mode: mode.next });
  }

  function rememberPointer(event: PointerEvent<HTMLInputElement>) {
    const rect = event.currentTarget.getBoundingClientRect();
    const centre = rect.left + rect.height / 2 + position / 100 * (rect.width - rect.height);
    pointerStart.current = {
      x: event.clientX,
      y: event.clientY,
      onThumb: Math.abs(event.clientX - centre) <= rect.height / 2,
    };
  }

  return (
    <div className={`statistic-slider${filter.enabled ? "" : " statistic-slider--inactive"}`} style={style}>
      <label className="statistic-slider__label">
        <input
          className="statistic-slider__enabled"
          type="checkbox"
          aria-label={`Apply ${label} filter`}
          checked={filter.enabled}
          onChange={event => onChange({ ...filter, enabled: event.target.checked })}
        />
        <span>{label}</span>
      </label>
      {filter.stat === "hp" && (
        <img className="statistic-slider__help" src={statHelp} alt="" title="Check a statistic to include it in SEARCH. Click its slider icon to switch between exactly, at least and at most." />
      )}
      <span id={instructionsId} className="visually-hidden">
        Check to include this statistic in SEARCH. Drag or use arrow keys to change the value.
        Click the slider icon, or press Enter or Space, to cycle exactly, at least and at most.
      </span>
      <div className="statistic-slider__scale">
        <span aria-hidden="true">{BASE_STAT_MIN}</span>
        <div className="statistic-slider__control">
          <input
            className="statistic-slider__range"
            type="range"
            min={BASE_STAT_MIN}
            max={BASE_STAT_MAX}
            step={1}
            value={filter.value}
            aria-label={`${label} value`}
            aria-valuetext={`${mode.text} ${filter.value}`}
            aria-describedby={instructionsId}
            onChange={event => onChange({ ...filter, value: Number(event.target.value) })}
            onPointerDown={rememberPointer}
            onPointerCancel={() => { pointerStart.current = null; }}
            onBlur={() => { pointerStart.current = null; }}
            onClick={event => {
              const start = pointerStart.current;
              pointerStart.current = null;
              if (start?.onThumb && Math.abs(event.clientX - start.x) < 4 && Math.abs(event.clientY - start.y) < 4) cycleMode();
            }}
            onKeyDown={event => {
              if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                cycleMode();
              }
            }}
          />
          <output className="statistic-slider__value" style={{ left: `${position}%` }} aria-hidden="true">
            {filter.value}
          </output>
        </div>
        <span aria-hidden="true">{BASE_STAT_MAX}</span>
      </div>
    </div>
  );
}
