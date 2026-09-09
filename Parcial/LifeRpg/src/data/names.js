export const firstNames = [
  'Aldric',
  'Tharion',
  'Elara',
  'Brunna',
  'Kael',
  'Mira',
  'Dorian',
  'Lyra',
  'Gareth',
  'Seraphine',
  'Ragnar',
  'Neria',
  'Cedric',
  'Arwen',
  'Borin',
  'Valen',
];

export const lastNames = [
  'Piedraluna',
  'Barbazul',
  'Filooscuro',
  'Rompehuesos',
  'Hojaplata',
  'Cuervonegro',
  'Martillorojo',
  'Sombragrís',
  'Vientoférreo',
  'Guardafuego',
];

export function randomName() {
  const first =
    firstNames[Math.floor(Math.random() * firstNames.length)];

  const last =
    lastNames[Math.floor(Math.random() * lastNames.length)];

  return `${first} ${last}`;
}