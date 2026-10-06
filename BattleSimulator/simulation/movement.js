const GRID_SIZE = 50;

const DIRECTIONS = [
  { dx: -1, dy: -1 },
  { dx: 0, dy: -1 },
  { dx: 1, dy: -1 },
  { dx: -1, dy: 0 },
  { dx: 1, dy: 0 },
  { dx: -1, dy: 1 },
  { dx: 0, dy: 1 },
  { dx: 1, dy: 1 },
];

const STUCK_MANEUVER_TURNS = 2;
const STUCK_BREAKOUT_TURNS = 5;

function getDistance(unitA, unitB) {
  const dx = unitA.x - unitB.x;
  const dy = unitA.y - unitB.y;

  return Math.sqrt(
    dx * dx + dy * dy
  );
}

function findNearestEnemy(unit, units) {
  let nearestEnemy = null;
  let nearestDistance = Infinity;

  for (const otherUnit of units) {
    if (otherUnit.type === unit.type) {
      continue;
    }

    const distance = getDistance(
      unit,
      otherUnit
    );

    if (distance < nearestDistance) {
      nearestDistance = distance;
      nearestEnemy = otherUnit;
    }
  }

  return nearestEnemy;
}

function isInsideGrid(x, y) {
  return (
    x >= 0 &&
    x < GRID_SIZE &&
    y >= 0 &&
    y < GRID_SIZE
  );
}

function getAdjacentPositions(unit) {
  const positions = [];

  for (const direction of DIRECTIONS) {
    const x = unit.x + direction.dx;
    const y = unit.y + direction.dy;

    if (!isInsideGrid(x, y)) {
      continue;
    }

    positions.push({
      x,
      y,
    });
  }

  return positions;
}

function getUnitsAround(
  position,
  units
) {
  return units.filter((otherUnit) => {
    const distanceX =
      Math.abs(
        otherUnit.x - position.x
      );

    const distanceY =
      Math.abs(
        otherUnit.y - position.y
      );

    return (
      distanceX <= 1 &&
      distanceY <= 1
    );
  });
}

function isPositionFree(
  position,
  occupied
) {
  return !occupied.has(
    `${position.x}-${position.y}`
  );
}

function countFreeNeighbors(
  position,
  occupied
) {
  return getAdjacentPositions({
    x: position.x,
    y: position.y,
  }).filter((neighbor) => {
    return isPositionFree(
      neighbor,
      occupied
    );
  }).length;
}

function countNearbyEnemies(
  unit,
  position,
  units
) {
  let enemies = 0;

  for (const otherUnit of units) {
    if (
      otherUnit.type === unit.type
    ) {
      continue;
    }

    const distanceX =
      Math.abs(
        otherUnit.x - position.x
      );

    const distanceY =
      Math.abs(
        otherUnit.y - position.y
      );

    if (
      distanceX <= 1 &&
      distanceY <= 1
    ) {
      enemies++;
    }
  }

  return enemies;
}

function countNearbyAllies(
  unit,
  position,
  units
) {
  let allies = 0;

  for (const otherUnit of units) {
    if (
      otherUnit.type !== unit.type
    ) {
      continue;
    }

    if (
      otherUnit.id === unit.id
    ) {
      continue;
    }

    const distanceX =
      Math.abs(
        otherUnit.x - position.x
      );

    const distanceY =
      Math.abs(
        otherUnit.y - position.y
      );

    if (
      distanceX <= 1 &&
      distanceY <= 1
    ) {
      allies++;
    }
  }

  return allies;
}

function isEngaged(unit, units) {
  return units.some((otherUnit) => {
    if (
      otherUnit.type === unit.type
    ) {
      return false;
    }

    const distanceX =
      Math.abs(
        otherUnit.x - unit.x
      );

    const distanceY =
      Math.abs(
        otherUnit.y - unit.y
      );

    return (
      distanceX <= 1 &&
      distanceY <= 1
    );
  });
}

function scoreNormalPosition(
  unit,
  position,
  enemy,
  units,
  occupied
) {
  if (
    !isPositionFree(
      position,
      occupied
    )
  ) {
    return -Infinity;
  }

  const currentDistance =
    getDistance(
      unit,
      enemy
    );

  const newDistance =
    getDistance(
      position,
      enemy
    );

  let score = 0;

  /*
   * Preferimos acercarnos al enemigo.
   */
  const distanceImprovement =
    currentDistance - newDistance;

  score +=
    distanceImprovement * 20;

  /*
   * Premiar llegar cerca del enemigo.
   */
  if (newDistance <= 1.5) {
    score += 8;
  } else if (newDistance <= 2.5) {
    score += 4;
  }

  const nearbyAllies =
    countNearbyAllies(
      unit,
      position,
      units
    );

  const nearbyEnemies =
    countNearbyEnemies(
      unit,
      position,
      units
    );

  /*
   * Tener aliados cerca ayuda
   * a mantener una línea.
   */
  score +=
    Math.min(
      nearbyAllies,
      3
    ) * 2;

  /*
   * Evitar aglomeraciones.
   */
  if (nearbyAllies >= 5) {
    score -= 8;
  }

  /*
   * Evitar meterse demasiado
   * solo contra muchos enemigos.
   */
  if (
    nearbyEnemies >
    nearbyAllies + 2
  ) {
    score -= 10;
  }

  /*
   * Si hay aliados suficientes
   * para apoyar el combate,
   * es una buena posición.
   */
  if (
    nearbyEnemies > 0 &&
    nearbyAllies >= nearbyEnemies
  ) {
    score += 3;
  }

  /*
   * Preferir posiciones con espacio.
   */
  const freeNeighbors =
    countFreeNeighbors(
      position,
      occupied
    );

  if (freeNeighbors === 0) {
    score -= 15;
  } else if (freeNeighbors >= 4) {
    score += 2;
  }

  /*
   * Pequeña variación para evitar
   * comportamientos perfectamente
   * simétricos.
   */
  score += Math.random() * 2;

  return score;
}

function scoreManeuverPosition(
  unit,
  position,
  enemy,
  units,
  occupied
) {
  if (
    !isPositionFree(
      position,
      occupied
    )
  ) {
    return -Infinity;
  }

  const currentDistance =
    getDistance(
      unit,
      enemy
    );

  const newDistance =
    getDistance(
      position,
      enemy
    );

  let score = 0;

  /*
   * En modo maniobra ya no exigimos
   * acercarnos directamente.
   *
   * Una posición ligeramente más
   * alejada puede ser mejor si
   * permite rodear al enemigo.
   */
  const distanceChange =
    currentDistance - newDistance;

  score +=
    distanceChange * 5;

  /*
   * Mucho espacio alrededor =
   * buena posición para continuar.
   */
  const freeNeighbors =
    countFreeNeighbors(
      position,
      occupied
    );

  score +=
    freeNeighbors * 5;

  /*
   * Preferimos posiciones que
   * no estén completamente rodeadas
   * de aliados.
   */
  const nearbyAllies =
    countNearbyAllies(
      unit,
      position,
      units
    );

  score -=
    nearbyAllies * 3;

  /*
   * No queremos rodearnos de enemigos.
   */
  const nearbyEnemies =
    countNearbyEnemies(
      unit,
      position,
      units
    );

  score -=
    nearbyEnemies * 2;

  /*
   * Si conseguimos estar cerca
   * de un enemigo, sigue siendo útil.
   */
  if (newDistance <= 2.5) {
    score += 8;
  }

  score += Math.random() * 4;

  return score;
}

function scoreBreakoutPosition(
  unit,
  position,
  enemy,
  units,
  occupied
) {
  if (
    !isPositionFree(
      position,
      occupied
    )
  ) {
    return -Infinity;
  }

  const currentDistance =
    getDistance(
      unit,
      enemy
    );

  const newDistance =
    getDistance(
      position,
      enemy
    );

  let score = 0;

  /*
   * En breakout aceptamos alejarnos.
   *
   * Lo importante es escapar de la
   * zona congestionada.
   */
  const distanceChange =
    newDistance - currentDistance;

  score -=
    distanceChange * 2;

  /*
   * Mucho espacio es extremadamente
   * importante durante el breakout.
   */
  const freeNeighbors =
    countFreeNeighbors(
      position,
      occupied
    );

  score +=
    freeNeighbors * 10;

  /*
   * Evitar aliados alrededor.
   */
  const nearbyAllies =
    countNearbyAllies(
      unit,
      position,
      units
    );

  score -=
    nearbyAllies * 5;

  /*
   * Evitar entrar en una zona
   * demasiado rodeada de enemigos.
   */
  const nearbyEnemies =
    countNearbyEnemies(
      unit,
      position,
      units
    );

  score -=
    nearbyEnemies * 4;

  /*
   * Alejarse demasiado no es ideal,
   * pero durante un breakout está
   * permitido.
   */
  if (newDistance > currentDistance) {
    score += 3;
  }

  score += Math.random() * 5;

  return score;
}

function chooseBestPosition(
  unit,
  enemy,
  units,
  occupied
) {
  const positions =
    getAdjacentPositions(unit);

  const engaged =
    isEngaged(
      unit,
      units
    );

  /*
   * --------------------------------
   * MODO 1: NORMAL
   * --------------------------------
   */
  if (
    !engaged &&
    (unit.stuckTurns || 0) <
      STUCK_MANEUVER_TURNS
  ) {
    let bestPosition = {
      x: unit.x,
      y: unit.y,
    };

    let bestScore = -Infinity;

    for (const position of positions) {
      const score =
        scoreNormalPosition(
          unit,
          position,
          enemy,
          units,
          occupied
        );

      if (score > bestScore) {
        bestScore = score;
        bestPosition = position;
      }
    }

    return bestPosition;
  }

  /*
   * --------------------------------
   * MODO 2: MANIOBRA
   * --------------------------------
   *
   * Si estamos cerca del enemigo
   * y llevamos varios turnos sin
   * avanzar, buscamos rodear.
   */
  if (
    (unit.stuckTurns || 0) <
      STUCK_BREAKOUT_TURNS
  ) {
    let bestPosition = {
      x: unit.x,
      y: unit.y,
    };

    let bestScore = -Infinity;

    for (const position of positions) {
      const score =
        scoreManeuverPosition(
          unit,
          position,
          enemy,
          units,
          occupied
        );

      if (score > bestScore) {
        bestScore = score;
        bestPosition = position;
      }
    }

    return bestPosition;
  }

  /*
   * --------------------------------
   * MODO 3: BREAKOUT
   * --------------------------------
   *
   * Después de demasiados turnos
   * bloqueado, permitimos una retirada
   * táctica para salir de la congestión.
   */
  let bestPosition = {
    x: unit.x,
    y: unit.y,
  };

  let bestScore = -Infinity;

  for (const position of positions) {
    const score =
      scoreBreakoutPosition(
        unit,
        position,
        enemy,
        units,
        occupied
      );

    if (score > bestScore) {
      bestScore = score;
      bestPosition = position;
    }
  }

  return bestPosition;
}

export function moveUnits(units) {
  /*
   * Estado del tablero al comienzo
   * del turno.
   */
  const occupied = new Set();

  for (const unit of units) {
    occupied.add(
      `${unit.x}-${unit.y}`
    );
  }

  const proposedMoves = [];

  /*
   * Cada unidad decide
   * individualmente.
   */
  for (const unit of units) {
    const enemy =
      findNearestEnemy(
        unit,
        units
      );

    /*
     * Si no quedan enemigos,
     * permanece en su posición.
     */
    if (!enemy) {
      proposedMoves.push({
        unit,
        x: unit.x,
        y: unit.y,
      });

      continue;
    }

    const position =
      chooseBestPosition(
        unit,
        enemy,
        units,
        occupied
      );

    proposedMoves.push({
      unit,
      x: position.x,
      y: position.y,
    });
  }

  /*
   * Contamos cuántas unidades
   * quieren cada destino.
   */
  const destinationCounts =
    new Map();

  for (const move of proposedMoves) {
    const key =
      `${move.x}-${move.y}`;

    const count =
      destinationCounts.get(key) || 0;

    destinationCounts.set(
      key,
      count + 1
    );
  }

  /*
   * Aplicamos los movimientos.
   */
  return proposedMoves.map((move) => {
    const currentPosition =
      `${move.unit.x}-${move.unit.y}`;

    const destination =
      `${move.x}-${move.y}`;

    const stayedInPlace =
      currentPosition === destination;

    /*
     * Si no se movió, aumentamos
     * su contador individual.
     */
    const previousStuckTurns =
      move.unit.stuckTurns || 0;

    const stuckTurns =
      stayedInPlace
        ? previousStuckTurns + 1
        : 0;

    /*
     * No se mueve.
     */
    if (stayedInPlace) {
      return {
        ...move.unit,
        stuckTurns,
      };
    }

    /*
     * La casilla estaba ocupada
     * al comienzo del turno.
     */
    if (
      occupied.has(destination)
    ) {
      return {
        ...move.unit,
        stuckTurns:
          previousStuckTurns + 1,
      };
    }

    /*
     * Varias unidades intentan
     * entrar en la misma casilla.
     */
    if (
      destinationCounts.get(destination) > 1
    ) {
      return {
        ...move.unit,
        stuckTurns:
          previousStuckTurns + 1,
      };
    }

    /*
     * Movimiento aprobado.
     */
    return {
      ...move.unit,
      x: move.x,
      y: move.y,
      stuckTurns: 0,
    };
  });
}
