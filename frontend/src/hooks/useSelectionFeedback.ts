import { useCallback, useEffect, useRef, useState } from "react";
import type { CSSProperties } from "react";
import "../styles/selectionFeedback.css";

const BLINK_STEP_DURATION_MS = 90;

export type PendingSelection = Readonly<{
  itemKey: string;
  phase: "dimmed" | "highlighted";
}>;

export type SelectionFeedbackProps = Readonly<{
  className: "selection-feedback";
  "data-selection-phase": PendingSelection["phase"] | undefined;
  style: CSSProperties | undefined;
}>;

function waitForBlink(signal: AbortSignal): Promise<boolean> {
  if (signal.aborted) return Promise.resolve(false);

  return new Promise(resolve => {
    const abort = () => {
      window.clearTimeout(timer);
      resolve(false);
    };
    const timer = window.setTimeout(() => {
      signal.removeEventListener("abort", abort);
      resolve(true);
    }, BLINK_STEP_DURATION_MS);
    signal.addEventListener("abort", abort, { once: true });
  });
}

export function useSelectionFeedback() {
  const [pendingSelection, setPendingSelection] = useState<PendingSelection | null>(null);
  const selectionRef = useRef<AbortController | null>(null);

  useEffect(() => () => {
    const selection = selectionRef.current;
    selectionRef.current = null;
    selection?.abort();
  }, []);

  const cancelSelection = useCallback(() => {
    selectionRef.current?.abort();
    selectionRef.current = null;
    setPendingSelection(null);
  }, []);

  const selectWithFeedback = useCallback(async (itemKey: string, onSelect: () => void | Promise<void>) => {
    if (selectionRef.current) return;

    const selection = new AbortController();
    selectionRef.current = selection;

    try {
      if (!window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        setPendingSelection({ itemKey, phase: "dimmed" });
        if (!await waitForBlink(selection.signal)) return;

        setPendingSelection({ itemKey, phase: "highlighted" });
        if (!await waitForBlink(selection.signal)) return;
      }

      setPendingSelection(null);
      await onSelect();
    } finally {
      if (selectionRef.current === selection) {
        selectionRef.current = null;
        if (!selection.signal.aborted) setPendingSelection(null);
      }
    }
  }, []);

  const getFeedbackProps = useCallback((itemKey: string): SelectionFeedbackProps => {
    const phase = pendingSelection?.itemKey === itemKey ? pendingSelection.phase : undefined;
    let style: CSSProperties | undefined;

    if (phase === "dimmed") {
      style = {
        background: "var(--selection-feedback-dimmed-background, transparent)",
        opacity: "var(--selection-feedback-dimmed-opacity, 0.55)",
      };
    } else if (phase === "highlighted") {
      style = {
        background: "var(--selection-feedback-highlighted-background, var(--color-accent))",
        color: "var(--selection-feedback-highlighted-color, var(--color-accent-contrast))",
        opacity: 1,
      };
    }

    return { className: "selection-feedback", "data-selection-phase": phase, style };
  }, [pendingSelection]);

  return { pendingSelection, selectWithFeedback, cancelSelection, getFeedbackProps };
}
