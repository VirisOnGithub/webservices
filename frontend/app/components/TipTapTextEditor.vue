<script setup lang="ts">
import {useEditor, EditorContent, type Editor} from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import {Placeholder} from "@tiptap/extensions";
import {Extension} from "@tiptap/core";
import type {ShallowRef} from "vue";

// Comportement de la touche entrée
// - si juste Entrée => envoyer le message
// - si Shift + Entrée => saut de ligne
const EnterKeyExtension = Extension.create({
  name: 'enterKey',
  addKeyboardShortcuts() {
    return {
      Enter: () => {
        sendMessage()
        return true
      },
      'Shift-Enter': () => {
        // editor?.commands.setHardBreak()
        return true
      }
    }
  }
})

const editor: ShallowRef<Editor | undefined, Editor | undefined> = useEditor({
  extensions: [
    StarterKit,
    Placeholder.configure({
      placeholder: 'Taper le message'
    }),
    EnterKeyExtension
  ],
  // Don't render on the server, only on the client after hydration
  // immediatelyRender: false,
  editorProps: {
    attributes: {
      class: 'min-h-4 outline-none'
    }
  }
})

function sendMessage() {
  const content = editor?.value?.getHTML()
  alert(content)
}
</script>

<template>
  <div class="flex items-center gap-2 p-3">
    <div class="flex-1 max-h-50 overflow-y-auto bg-gray-700 p-5 rounded-lg">
      <EditorContent :editor="editor" />
    </div>
    <button class="cursor-pointer grid place-items-center">
      <Icon name="mdi:send" size="24" />
    </button>
  </div>
</template>

<style scoped>

</style>
