<script setup>
const token = useCookie('token')

const logout = () => {
  token.value = null
  navigateTo('/login')
}

const { fetchData } = useApi()

const userId = useState('userId', () => null)

const userData = fetchData({
  endpoint: '/user/@me',
  useBaseEndpoint: true,
  token: useCookie('token').value
})

const channelData = fetchData({
  endpoint: '/channels',
  useBaseEndpoint: true,
  token: useCookie('token').value
})

onMounted(async () => {
  await (async () => {
    await userData.fetch()

    if (userData.data.value.idu) {
      userId.value = userData.data.value.idu
    }
    if (userData.error.value) {
      console.error('Failed to fetch user data:', userData.error.value)
      logout()
    }
  })();

  await (async () => {
    await channelData.fetch()

    if (channelData.error.value) {
      console.error('Failed to fetch channels:', channelData.error.value)
    }

    if (channelData.data.value) {
      console.log('Channels fetched successfully:', channelData.data.value)
    }
  })();
})
</script>

<template>
  <div id="app" class="flex h-screen w-screen">
    <div id="sidebar" class="w-62.5 bg-[#2c3e50] text-white flex flex-col">

      <div class="flex items-center gap-4 p-5">
        <NuxtImg id="logo_image" class="w-12.5" src="/liscord.png"/>
        <h1 id="logo_title" class="text-2xl font-bold">Liscord</h1>
      </div>

      <ul v-if="channelData.data">
        <li v-for="channel in channelData.data.value" :key="channel.idc">
          <Channel :channel="channel"/>
        </li>
      </ul>

      <template v-else-if="channelData.loading">
        <div class="channel_skeleton" v-for="i in 5" :key="i">
          <div class="flex items-center gap-4">
            <USkeleton class="h-5 w-62.5 m-5"/>
          </div>
        </div>
      </template>

      <div v-else-if="channelData.error" class="w-full mt-auto flex flex-col items-center justify-center p-5">
        <p class="text-red-500 text-center mt-4">
          {{ error ?? 'Connexion perdue, reconnexion...' }}
        </p>
      </div>

      <div class="w-full mt-auto flex flex-col items-center justify-center p-5">
        <UserBadge :user="userData.data" />

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
