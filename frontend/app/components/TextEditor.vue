<script setup lang="ts">
import { useTemplateRef } from 'vue'

const emit = defineEmits<{
  send: [content: string]
}>()

const editorRef = useTemplateRef('editor')

function onInput() {
  const el = editorRef.value
  if (!el) return
  if (el.innerHTML === '<br>' || el.innerHTML === '<br/>') {
    el.innerHTML = ''
  }
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

function sendMessage() {
  const el = editorRef.value
  if (!el) return
  const content = el.innerHTML.trim()
  if (!content) return
  emit('send', content)
  el.innerHTML = ''
  el.focus()
}
</script>

<template>
  <div class="flex items-center gap-2 p-3">
      <div
        ref="editor"
        contenteditable="true"
        role="textbox"
        aria-multiline="true"
        aria-label="Message"
        class="min-h-6 outline-none text-white empty:before:content-[attr(data-placeholder)] empty:before:text-gray-400 flex-1 max-h-50 overflow-y-auto bg-gray-700 p-3 rounded-lg"
        data-placeholder="Taper le message"
        @keydown="onKeydown"
        @input="onInput"
      />
    <button
      class="cursor-pointer grid place-items-center shrink-0"
      @click="sendMessage"
    >
      <UIcon name="mdi:send" size="24" />
    </button>
  </div>
</template>
