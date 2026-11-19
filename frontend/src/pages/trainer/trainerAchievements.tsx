import { useEffect, useState, type CSSProperties } from "react";

import ArrowButton from "../../components/common/arrowButton";
import { getAchievementProgress, type AchievementProgress } from "../../services/achievementService";

const ACHIEVEMENT_COLUMNS = 5;
const ACHIEVEMENT_ROWS = 3;

export default function TrainerAchievements({ token }: { token: string }) {
  const [state, setState] = useState<{ status: "loading" | "error" } |
    { status: "success"; achievements: AchievementProgress[] }>({ status: "loading" });
  const [attempt, setAttempt] = useState(0);
  const [columnOffset, setColumnOffset] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    getAchievementProgress(token, controller.signal).then(achievements => {
      if (!controller.signal.aborted) setState({ status: "success", achievements });
    }).catch(() => {
      if (!controller.signal.aborted) setState({ status: "error" });
    });
    return () => controller.abort();
  }, [token, attempt]);

  const achievements = state.status === "success" ? state.achievements : [];
  const lastOffset = Math.max(0, Math.ceil(achievements.length / ACHIEVEMENT_ROWS) - ACHIEVEMENT_COLUMNS);
  const offset = Math.min(columnOffset, lastOffset);
  const initialSlots = ACHIEVEMENT_COLUMNS * ACHIEVEMENT_ROWS;

  return (
    <section className="trainer-achievements" aria-labelledby="trainer-achievements-title">
      <h2 id="trainer-achievements-title">Achievements</h2>
      {state.status === "loading" && <p role="status">Loading achievements…</p>}
      {state.status === "error" && <p role="alert">Could not load achievements.{" "}
        <button
          type="button"
          onClick={() => {
            setState({ status: "loading" });
            setAttempt(previous => previous + 1);
          }}
        >
          Retry
        </button>
      </p>}
      {state.status === "success" && achievements.length === 0 && <p>No achievements available.</p>}
      {achievements.length > 0 &&
        <div className="trainer-achievements__body">
          <ArrowButton
            aria-label="Previous achievement column"
            aria-controls="trainer-achievements-grid"
            disabled={offset === 0}
            onClick={() => setColumnOffset(previous => Math.max(0, Math.min(previous, lastOffset) - 1))}
          />
          <div className="trainer-achievements__viewport">
            <ul
              className="trainer-achievements__grid"
              id="trainer-achievements-grid"
              style={{ "--achievement-columns": ACHIEVEMENT_COLUMNS, "--achievement-offset": offset } as CSSProperties}
            >
              {achievements.map((achievement, index) => {
                // Keep the first fifteen badges in their original positions, then fill new columns.
                const column = index < initialSlots ? index % ACHIEVEMENT_COLUMNS
                  : ACHIEVEMENT_COLUMNS + Math.floor((index - initialSlots) / ACHIEVEMENT_ROWS);
                const row = index < initialSlots ? Math.floor(index / ACHIEVEMENT_COLUMNS)
                  : (index - initialSlots) % ACHIEVEMENT_ROWS;
                const visibleColumn = column - offset;
                const midpoint = (ACHIEVEMENT_COLUMNS - 1) / 2;
                const tooltipAlign = visibleColumn < midpoint ? "start" : visibleColumn > midpoint ? "end" : "center";

                return <li
                  key={achievement.id}
                  className="trainer-achievements__slot"
                  style={{ gridColumn: column + 1, gridRow: row + 1 }}
                  inert={visibleColumn < 0 || visibleColumn >= ACHIEVEMENT_COLUMNS}
                  data-tooltip-align={tooltipAlign}
                >
                  <button
                    type="button"
                    className="trainer-achievements__badge"
                    data-unlocked={achievement.unlocked}
                    aria-label={achievement.name + (achievement.unlocked ? ", unlocked" : ", locked")}
                    aria-describedby={"achievement-requirement-" + achievement.id}
                    onClick={event => event.currentTarget.focus()}
                  >
                    <span aria-hidden="true">{achievement.unlocked ? "✓" : "○"}</span>
                  </button>
                  <div className="trainer-achievements__tooltip" role="tooltip">
                    <strong>{achievement.name}</strong>
                    <span id={"achievement-requirement-" + achievement.id}>{achievement.description}</span>
                  </div>
                </li>;
              })}
            </ul>
          </div>
          <ArrowButton
            direction="right"
            aria-label="Next achievement column"
            aria-controls="trainer-achievements-grid"
            disabled={offset === lastOffset}
            onClick={() => setColumnOffset(previous => Math.min(previous + 1, lastOffset))}
          />
        </div>}
    </section>
  );
}
