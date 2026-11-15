export const TRAINER_CLASSES = [
  "NOVICE", "CHAMPION", "ACE_TRAINER", "GYM_LEADER", "ELITE_FOUR", "RIVAL",
  "PROFESSOR", "HIKER", "BIKE_RIDER", "SWIMMER", "BLACK_BELT", "BEAUTY",
  "COOLTRAINER", "LASS", "CAMPER", "POKEMON_BREEDER", "POKEMON_RANGER",
  "POKEMON_SCHOOL_TEACHER", "POKEMON_FAN", "POKEMON_COLLECTOR", "POKEMON_RIDER",
  "DRAGON_TAMER", "FISHERMAN", "GENTLEMAN", "LADY", "NINJA", "PARASOL_LADY",
  "PICNICKER", "POLICEMAN", "PSYCHIC", "SAILOR", "SCHOOL_KID", "SENIOR",
  "SWIMSUIT", "TUBER", "YOUNGSTER", "WORKER", "JUGGLER", "KIMONO_GIRL", "KIMONO_BOY",
  "KAHUNA", "CAPTAIN", "COORDINATOR", "ADVENTURER",
] as const;

export type TrainerClass = typeof TRAINER_CLASSES[number];

export type TrainerProfile = {
  id: number;
  username: string;
  email: string;
  realName: string | null;
  trainerClass: TrainerClass;
  profilePictureUrl: string | null;
  favoriteGameName: string | null;
  favoritePokemonId: number | null;
  favoritePokemonName: string | null;
  regionName: string | null;
  bestFriendUsername: string | null;
  friendCode: string;
};

export type EditableTrainerProfile = Pick<TrainerProfile,
  "realName" | "trainerClass" | "profilePictureUrl" | "favoriteGameName" | "favoritePokemonId">;

export type TrainerProfileChanges = Partial<EditableTrainerProfile>;
