export function toTitleCase(term: string): string {
  return term.charAt(0).toUpperCase() + term.slice(1).toLowerCase();
}
