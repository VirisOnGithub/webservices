<script lang="ts" setup>
import type {Message} from "~/types/message";

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const message = props.message as Message
const sendDate = new Date(message.sendDate)

const formatTime = (date: Date): string => {
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}
</script>

<template>
<div class="m-2 flex">
  <div id="avatar" class="mr-4">
    <img :src="`data:image/jpeg;base64,${message.author.avatar}`" alt="avatar" class="w-10 h-10 rounded-full"/>
  </div>
  <div id="content">
    <div id="header">
      <span id="username">{{ message.author.pseudo }}</span>
      <span id="timestamp" class="ml-2 text-gray-500">{{ formatTime(sendDate) }}</span>
    </div>
    <div id="text">{{ message.content }}</div>
  </div>
</div>
</template>

<style scoped>

</style>
