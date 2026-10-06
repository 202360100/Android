import { moveUnits } from './movement';
import { resolveCombat } from './combat';

export function simulateTurn(units) {
  const movedUnits =
    moveUnits(units);

  const combatResult =
    resolveCombat(movedUnits);

  const survivingUnits =
    combatResult.units;

  const knights =
    survivingUnits.filter(
      (unit) =>
        unit.type === 'knight'
    );

  const orcs =
    survivingUnits.filter(
      (unit) =>
        unit.type === 'orc'
    );

  let status = 'fighting';

  if (
    knights.length === 0 &&
    orcs.length === 0
  ) {
    status = 'draw';
  } else if (
    knights.length === 0
  ) {
    status = 'orcs_won';
  } else if (
    orcs.length === 0
  ) {
    status = 'knights_won';
  }

  return {
    units: survivingUnits,
    status,
    combatPositions:
      combatResult.combatPositions,
  };
}
