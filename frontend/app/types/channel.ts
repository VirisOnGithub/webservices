import type {User} from "~/types/user";

export interface Channel {
  idc: number,
  name: string,
  description: string,
  isPublic: boolean,
  creator: User,
}
