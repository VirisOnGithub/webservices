<script setup>
const token = useCookie('token')

const logout = () => {
  token.value = null
  navigateTo('/login')
}

const { channels, error, status, fetchChannels, createChannel } = useChannels(token.value)
</script>

<template>
  <div id="app" class="flex h-screen w-screen">
    <div id="sidebar" class="w-62.5 bg-[#2c3e50] text-white flex flex-col">

      <div class="flex items-center gap-4 p-5">
        <NuxtImg id="logo_image" class="w-12.5" src="/liscord.png"/>
        <h1 id="logo_title" class="text-2xl font-bold">Liscord</h1>
      </div>

      <template v-if="status === 'connecting' || (status === 'open' && channels.length === 0)">
        <div class="channel_skeleton" v-for="i in 5" :key="i">
          <div class="flex items-center gap-4">
            <USkeleton class="h-5 w-62.5 m-5"/>
          </div>
        </div>
      </template>

      <ul v-else-if="status === 'open' && channels.length > 0">
        <li v-for="channel in channels" :key="channel.idc">
          <Channel :channel="channel"/>
        </li>
      </ul>

      <div v-else-if="status === 'closed' || error">
        <p class="text-red-500 text-center mt-4">
          {{ error ?? 'Connexion perdue, reconnexion...' }}
        </p>
      </div>

      <div class="w-full mt-auto flex items-center justify-center p-5">
        <button
          class="border p-4 rounded-xl cursor-pointer hover:bg-white hover:text-[#2c3e50]"
          @click="logout"
        >
          Se déconnecter
        </button>
      </div>
    </div>

    <div class="main w-full h-full">
      <slot />
    </div>
  </div>
</template>

<style scoped>
</style>
