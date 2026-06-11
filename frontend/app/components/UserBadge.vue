<script setup lang="ts">
interface User {
  idu: number
  pseudo: string
  email: string
  avatar: string
  inscriptionDate: string
}

const props = defineProps<{
  user: Ref<User>
}>()
</script>

<template>
  <div id="badge" class="mb-4 flex justify-center w-full items-center" v-if="props.user.value">
    <img :src="`data:image/jpeg;base64,${props.user.value.avatar}`" alt="avatar" class="w-10 h-10 rounded-full mr-2"/>
    <p>{{ user.value.pseudo }}</p>
  </div>
</template>

<style scoped>

</style>
