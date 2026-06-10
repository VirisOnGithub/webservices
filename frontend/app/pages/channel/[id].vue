<script setup lang="ts">
import type { CookieRef } from "#app"

const route = useRoute()
const token: CookieRef<string> = useCookie('token')

const id = route.params.id as string

const { messages, error, status, sendMessage, editMessage, deleteMessage } = useMessages(id, token.value)


// quand un user envoie un message, scroll auto vers le bas pour voir le message
const scrollToBottom = async () => {
  await nextTick()

  const container = document.getElementById('messages-container')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

watch(messages, () => {
  // console.log('Messages updated, scrolling to bottom...')
  scrollToBottom()
}, { deep: true })
</script>

<template>
  <div class="flex flex-col w-full h-[calc(100vh-8px)]">

    <template v-if="status === 'connecting'">
      <div class="flex flex-col justify-end p-4 gap-2 h-full">
        <USkeleton v-for="i in 8" :key="i" class="h-10 w-full rounded-lg" />
      </div>
    </template>

    <div v-else-if="status === 'open'" class="flex flex-col flex-1 p-4 overflow-y-auto" id="messages-container">
      <div class="mt-auto"></div> <!-- prends l'espace tant qu'il n'y a pas beaucoup de messages -->
      <Message v-for="message in messages" :key="message.idm" :message="message" @delete="deleteMessage" @update="editMessage" />
    </div>

    <div v-else class="flex items-center justify-center flex-1">
      <p class="text-red-400">{{ error ?? 'Connexion perdue, reconnexion en cours...' }}</p>
    </div>

    <TextEditor @send="sendMessage" />
  </div>
</template>
