export function uppercaseInput(input: HTMLInputElement): string {
  const { value, selectionStart, selectionEnd, selectionDirection } = input;
  const uppercase = value.toUpperCase();

  if (uppercase !== value) {
    input.value = uppercase;
    if (selectionStart !== null && selectionEnd !== null) {
      input.setSelectionRange(
        value.slice(0, selectionStart).toUpperCase().length,
        value.slice(0, selectionEnd).toUpperCase().length,
        selectionDirection ?? "none",
      );
    }
  }

  return uppercase;
}
