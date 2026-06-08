import type {User} from "~/types/user";
import type {Channel} from "~/types/channel";

export interface Message {
  idm: number,
  content: string,
  sendDate: string,
  edited: boolean,
  editDate: string | null,
  author: User,
  channel: Channel,
  attachment: string,
  parentMessage: Message | null,
}
