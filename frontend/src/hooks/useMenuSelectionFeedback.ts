import { useCallback, useEffect, useRef, useState } from "react";

const BLINK_STEP_DURATION_MS = 90;

export type PendingMenuSelection = Readonly<{
  itemKey: string;
  phase: "dimmed" | "highlighted";
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

export function useMenuSelectionFeedback() {
  const [pendingSelection, setPendingSelection] = useState<PendingMenuSelection | null>(null);
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

  return { pendingSelection, selectWithFeedback, cancelSelection };
}
