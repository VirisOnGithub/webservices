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
        case 'MESSAGE_UPDATED':
          const index = messages.value.findIndex(m => m.idm === msg.data.idm)
          if (index !== -1) messages.value[index] = msg.data
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
    content = content.replaceAll('<br>', '\n').trim()
    if (ws?.readyState !== WebSocket.OPEN || content.length === 0) return
    ws.send(JSON.stringify({
      action: 'SEND_MESSAGE',
      payload: {
        content,
        ...(parentMessage ? { parentMessage } : {})
      }
    }))
  }

  const editMessage = (idm: number, content: string) => {
    content = content.replaceAll('<br>', '\n').trim()
    if (ws?.readyState !== WebSocket.OPEN || content.length === 0) return
    ws.send(JSON.stringify({
      action: 'EDIT_MESSAGE',
      payload: {
        idm,
        content
      }
    }))
  }

  onMounted(connect)
  onUnmounted(() => ws?.close())

  return { messages, error, status, sendMessage, editMessage }
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
  type: 'MESSAGES_LIST' | 'MESSAGE_CREATED' | 'MESSAGE_UPDATED' | 'ERROR'
  data?: any
  error?: string
}
