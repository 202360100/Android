import React, {
  createContext,
  useContext,
  useState,
} from 'react';

const GameContext = createContext();

export function GameProvider({ children }) {
  const [characters, setCharacters] = useState([]);
  const [enemies, setEnemies] = useState([]);

  const addCharacter = (character) => {
    setCharacters((current) => [
      ...current,
      character,
    ]);
  };

  const addEnemy = (enemy) => {
    setEnemies((current) => [
      ...current,
      enemy,
    ]);
  };

  const removeCharacter = (id) => {
    setCharacters((current) =>
      current.filter((character) => character.id !== id)
    );
  };

  const removeEnemy = (id) => {
    setEnemies((current) =>
      current.filter((enemy) => enemy.id !== id)
    );
  };

  const clearCharacters = () => {
    setCharacters([]);
  };

  const clearEnemies = () => {
    setEnemies([]);
  };

  return (
    <GameContext.Provider
      value={{
        characters,
        enemies,

        addCharacter,
        addEnemy,

        removeCharacter,
        removeEnemy,

        clearCharacters,
        clearEnemies,
      }}
    >
      {children}
    </GameContext.Provider>
  );
}

export function useGame() {
  const context = useContext(GameContext);

  if (!context) {
    throw new Error(
      'useGame debe utilizarse dentro de GameProvider'
    );
  }

  return context;
}