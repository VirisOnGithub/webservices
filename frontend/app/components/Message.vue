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
</script>

<template>
<div class="m-2 flex hover:bg-[#2c2f33] rounded-lg p-2 relative group:"
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
    </div>
    <div id="text" class="whitespace-pre-line">{{ message.content }}</div>
  </div>

  <!--  tooltip -->
  <div class="absolute right-0 top-0 -translate-y-1/2 group-hover:text-red-400 hidden" :class="{ 'block!': messageHover }">
    <MessageTooltip @delete="deleteMsg" />
  </div>
</div>
</template>

<style scoped>

</style>
