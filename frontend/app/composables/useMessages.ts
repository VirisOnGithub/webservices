export function useMessages(channelId: string | string[], token: string) {
  const messages = ref<Message[]>([])
  const error = ref<string | null>(null)
  const status = ref<'connecting' | 'open' | 'closed'>('connecting')

  let ws: WebSocket | null = null

  const connect = () => {
    const id = Array.isArray(channelId) ? channelId[0] : channelId
    // obligé de passer le token en paramètre (pas de body dans les websockets)
    ws = new WebSocket(`ws://localhost:8080/ws/channels/${id}/messages?token=${token}`)

    ws.onopen = () => {
      status.value = 'open'
    }

    ws.onmessage = (event) => {
      const msg: WsMessage = JSON.parse(event.data)
      error.value = null

      switch (msg.type) {
        case 'MESSAGES_LIST':
          messages.value = msg.data
          break
        case 'MESSAGE_CREATED':
          messages.value.push(msg.data)
          break
        case 'ERROR':
          error.value = msg.error ?? 'Erreur inconnue'
          break
      }
    }

    ws.onclose = () => {
      status.value = 'closed'
      setTimeout(connect, 3000) // reconnexion auto
    }
  }

  const sendMessage = (content: string, parentMessage?: { idm: number }) => {
    if (ws?.readyState !== WebSocket.OPEN) return
    ws.send(JSON.stringify({
      action: 'SEND_MESSAGE',
      payload: {
        content,
        ...(parentMessage ? { parentMessage } : {})
      }
    }))
  }

  onMounted(connect)
  onUnmounted(() => ws?.close())

  return { messages, error, status, sendMessage }
}

interface Message {
  idm: number
  content: string
  sendDate: string
  edited: boolean
  author: { id: number; username: string }
  parentMessage?: { idm: number }
}

interface WsMessage {
  type: 'MESSAGES_LIST' | 'MESSAGE_CREATED' | 'ERROR'
  data?: any
  error?: string
}
