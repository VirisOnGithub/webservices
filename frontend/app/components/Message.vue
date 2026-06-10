<script lang="ts" setup>
import type {Message} from "~/types/message";
import MessageTooltip from "~/components/MessageTooltip.vue";

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const emit = defineEmits<{
  delete: [idm: number]
  update: [idm: number, content: string]
}>()

const message = props.message as Message
const sendDate = new Date(message.sendDate)

const formatTime = (date: Date): string => {
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

const messageHover = ref(false)

const deleteMsg = () => {
  // répercute sur le parent
  emit('delete', message.idm)
}

const updateMessage = (content: string) => {
  emit('update', message.idm, content)
  is_editing.value = false
}

const is_editing = ref(false)
</script>

<template>
  <div class="m-2 flex hover:bg-[#2c2f33] rounded-lg p-2 relative duration-200 w-[calc(100%-1rem)]"
       @mouseenter="messageHover = true"
       @mouseleave="messageHover = false"
  >
    <div id="avatar" class="mr-4">
      <img :src="`data:image/jpeg;base64,${message.author.avatar}`" alt="avatar" class="w-10 h-10 rounded-full"/>
    </div>
    <div id="content">
      <div id="header">
        <span id="username">{{ message.author.pseudo }}</span>
        <span id="timestamp" class="ml-2 text-gray-500">{{ formatTime(sendDate) }}</span>
        <span id="edited" v-if="message.edited" class="ml-2 text-gray-500 italic text-sm">(modifié)</span>
      </div>
      <div v-if="!is_editing" id="text" class="whitespace-pre-line">{{ message.content }}</div>
      <div v-else id="edit" class="flex gap-2 h-full">
        <TextEditor :key="message.idm" @send="updateMessage" :initial_content="message.content" />
        <button @click="is_editing = false" class="text-gray-500 hover:text-gray-400">
          <UIcon name="mdi:close" class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!--  tooltip -->
    <div class="absolute right-0 top-0 -translate-y-1/2 hidden"
         :class="{ 'block!': messageHover }">
      <MessageTooltip @delete="deleteMsg" @request_edit="() => is_editing = true"/>
    </div>
  </div>
</template>

<style scoped>

</style>
