export type SliderMode = "equal" | "minimum" | "maximum";

export interface SliderFilter {
  value: number;
  mode: SliderMode;
  enabled: boolean;
}
