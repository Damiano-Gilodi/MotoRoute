const difficultyLabels = {
  EASY: "Facile",
  MEDIUM: "Media",
  HARD: "Difficile",
};

export function formatRouteDifficulty(difficulty) {
  return difficultyLabels[difficulty] ?? difficulty;
}
