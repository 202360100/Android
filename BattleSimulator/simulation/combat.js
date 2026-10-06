export function resolveCombat(units) {
  const deadUnits = new Set();
  const combatPositions = [];

  for (const unit of units) {
    const enemies = [];
    const allies = [];

    for (const otherUnit of units) {
      const distanceX =
        Math.abs(otherUnit.x - unit.x);

      const distanceY =
        Math.abs(otherUnit.y - unit.y);

      if (
        distanceX <= 1 &&
        distanceY <= 1
      ) {
        if (
          otherUnit.type === unit.type
        ) {
          if (
            otherUnit.id !== unit.id
          ) {
            allies.push(otherUnit);
          }
        } else {
          enemies.push(otherUnit);
        }
      }
    }

    // Si hay enemigos cerca, registramos esta posición
    // como una zona de combate.
    if (enemies.length > 0) {
      const positionKey =
        `${unit.x}-${unit.y}`;

      if (
        !combatPositions.some(
          (position) =>
            position.key === positionKey
        )
      ) {
        combatPositions.push({
          key: positionKey,
          x: unit.x,
          y: unit.y,
        });
      }
    }

    if (
      enemies.length > allies.length
    ) {
      deadUnits.add(unit.id);
    }
  }

  return {
    units: units.filter(
      (unit) =>
        !deadUnits.has(unit.id)
    ),
    combatPositions,
  };
}
