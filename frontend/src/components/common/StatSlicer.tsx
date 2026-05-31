// StatSlicer — a stat range filter with three click-cycled modes:
//   mode 0 = EXACT    (filled circle handle, no fill bar)
//   mode 1 = MINIMUM  (right triangle handle, fill from handle → right end)
//   mode 2 = MAXIMUM  (left triangle handle, fill from left end → handle)

import { useRef } from "react";
import type { CSSProperties } from "react";
import "./StatSlicer.css";

export interface StatSlicerProps {
  label: string; // "HP", "ATTACK", etc.
  value: number;
  mode: 0 | 1 | 2;
  onChange: (value: number) => void;
  onModeChange: (mode: 0 | 1 | 2) => void;
}

const MAX = 255;

function StatSlicer({
  label,
  value,
  mode,
  onChange,
  onModeChange,
}: StatSlicerProps) {
  // Remembers the value at pointer-down so we can tell a click from a drag
  const downValueRef = useRef<number | null>(null);

  const percent = (value / MAX) * 100;

  const handlePointerDown = () => {
    downValueRef.current = value;
  };

  const handlePointerUp = () => {
    // If the value didn't change between down and up, it was a click → cycle mode
    if (downValueRef.current !== null && downValueRef.current === value) {
      const next = ((mode + 1) % 3) as 0 | 1 | 2;
      onModeChange(next);
    }
    downValueRef.current = null;
  };

  // The filled section of the bar depends on the current mode
  let fillStyle: CSSProperties | undefined;
  if (mode === 1) {
    // MINIMUM: highlight from handle to the right end
    fillStyle = { left: `${percent}%`, right: 0 };
  } else if (mode === 2) {
    // MAXIMUM: highlight from the left end to the handle
    fillStyle = { left: 0, width: `${percent}%` };
  }

  return (
    <div className="slicer">
      <div className="slicer__header">
        <span className="slicer__label">{label}</span>
      </div>

      <div className="slicer__track-row">
        <span className="slicer__bound">0</span>

        <div className="slicer__track-wrap">
          <div className="slicer__track" aria-hidden="true" />
          {fillStyle && (
            <div className="slicer__fill" style={fillStyle} aria-hidden="true" />
          )}

          <input
            type="range"
            min={0}
            max={MAX}
            value={value}
            className={`slicer__range slicer__range--mode${mode}`}
            aria-label={`${label} filter value`}
            onChange={(e) => onChange(Number(e.target.value))}
            onPointerDown={handlePointerDown}
            onPointerUp={handlePointerUp}
          />

          <span
            className="slicer__value"
            style={{ left: `${percent}%` }}
            aria-hidden="true"
          >
            {value}
          </span>
        </div>

        <span className="slicer__bound">{MAX}</span>
      </div>
    </div>
  );
}

export default StatSlicer;
