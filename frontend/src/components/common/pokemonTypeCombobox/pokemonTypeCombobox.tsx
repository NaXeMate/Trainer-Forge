import Combobox from "../combobox";
import { POKEMON_TYPES } from "../../../constants/pokedex";
import { getTypeColor } from "../../../utils/pokemonPresentation";
import "./pokemonTypeCombobox.css";

interface PokemonTypeComboboxProps {
  name: string;
  label: string;
  value: string;
  excludedValue: string;
  onChange: (value: string) => void;
}

const TYPE_OPTIONS = POKEMON_TYPES.map(({ name }) => ({
  value: name,
  label: name,
  color: getTypeColor(name),
}));

export default function PokemonTypeCombobox({ name, label, value, excludedValue, onChange }: PokemonTypeComboboxProps) {
  return (
    <Combobox
      className="pokedex-type-filter"
      name={name}
      label={label}
      placeholder="TYPE"
      value={value}
      options={TYPE_OPTIONS}
      excludedValue={excludedValue}
      emptyMessage="No matching types."
      invalidMessage="Choose one of the 18 types."
      excludedMessage="Already selected in the other field."
      onChange={onChange}
      onInputChange={onChange}
    />
  );
}
