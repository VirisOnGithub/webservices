export function useChannels(token: string) {
  const channels = ref<Channel[]>([])
  const error = ref<string | null>(null)
  const status = ref<'connecting' | 'open' | 'closed'>('connecting')

  let ws: WebSocket | null = null

  // --- Connexion ---
  const connect = () => {
    ws = new WebSocket('ws://localhost:8080/ws/channels?token=' + token)

    ws.onopen = () => {
      status.value = 'open'
      fetchChannels()
    }

    ws.onmessage = (event) => {
      const msg = JSON.parse(event.data)
      handleMessage(msg)
    }

    ws.onclose = () => {
      status.value = 'closed'
      setTimeout(connect, 3000) // reconnexion
    }
  }

  // --- Dispatch des messages reçus ---
  const handleMessage = (msg: WsMessage) => {
    error.value = null
    switch (msg.type) {
      case 'CHANNELS_LIST':
        channels.value = msg.data
        break
      case 'CHANNEL_CREATED':
        // Reçu par TOUS les clients connectés — mise à jour temps réel
        channels.value.push(msg.data)
        break
      case 'ERROR':
        error.value = msg.error ?? 'Erreur inconnue'
        break
    }
  }

  // --- Actions (remplacent les appels fetch) ---
  const fetchChannels = () => {
    send({ action: 'GET_CHANNELS' })
  }

  const createChannel = (payload: Partial<Channel>) => {
    send({ action: 'CREATE_CHANNEL', payload })
  }

  const send = (message: object) => {
    if (ws?.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(message))
    }
  }

  onMounted(connect)
  onUnmounted(() => ws?.close())

  return { channels, error, status, fetchChannels, createChannel }
}

// --- Types ---
interface Channel {
  idc: string
  name: string
  description?: string
  isPublic: boolean
  url?: string
  creationDate: string
}

interface WsMessage {
  type: 'CHANNELS_LIST' | 'CHANNEL_CREATED' | 'ERROR'
  data?: any
  error?: string
}
