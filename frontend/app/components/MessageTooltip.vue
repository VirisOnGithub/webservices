<script setup lang="ts">
const emit = defineEmits<{
  delete: []
}>()

let testAlert = () => {
  alert('test')
}

let deleteMsg = () => {
  emit('delete')
}
</script>

<template>
  <div class="flex bg-gray-700 rounded">
    <button id="edit" @click="testAlert" class="p-2 rounded grid place-items-center hover:bg-gray-600">
      <UIcon name="material-symbols:edit-outline" class="w-5 h-5" />
    </button>
    <button id="answer" @click="testAlert" class="p-2 rounded grid place-items-center hover:bg-gray-600">
      <UIcon name="i-ri-reply-line" class="w-5 h-5" />
    </button>
    <button id="delete" @click="deleteMsg" class="p-2 rounded grid place-items-center hover:bg-gray-600 text-red-500">
      <UIcon name="mdi:delete-outline" class="w-5 h-5" />
    </button>
  </div>
</template>

<style scoped>

</style>
