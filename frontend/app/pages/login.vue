<script setup lang="ts">
// La page est publique
definePageMeta({
  public: true,
  layout: 'empty'
})

const {fetchData} = useApi()

const username = useTemplateRef('username')
const password = useTemplateRef('password')
const error = ref("")

async function onClickLogin() {
  error.value = ""
  // alert(username.value?.value + ", " + password.value?.value)
  const tokenData = fetchData({
    endpoint: '/login?username=' + username.value?.value + '&password=' + password.value?.value,
    useBaseEndpoint: false
  })
  await tokenData.fetch()
  if (tokenData.error.value) {
    // alert('Login failed: ' + tokenData.error.value)
    error.value = 'Le mot de passe ou le nom d\'utilisateur est incorrect. Veuillez réessayer.'
    return
  }
  const token = tokenData.data.value?.token
  if (!token) {
    error.value = 'Erreur lors de la connexion. Veuillez réessayer.'
    return
  }
  console.log('Received token: ' + token)
  useCookie('token').value = token
  useState('toast').value = {message: 'Successfully logged in!', type: 'success'}
  navigateTo('/')
}

const { showToast } = useToastDisplay()

onMounted(() => {
  const toastState = useState('toast')
  if (toastState.value) {
    showToast(toastState.value.message, toastState.value.type)
    toastState.value = null
  }
})
</script>

<template>
  <div class="grid place-items-center w-screen h-screen">
    <div class="relative py-3 sm:max-w-xs sm:mx-auto">
      <form
        @submit.prevent="onClickLogin"
        class="min-h-96 px-8 py-6 mt-4 text-left bg-white dark:bg-gray-900 rounded-xl shadow-lg"
      >
        <div class="flex flex-col justify-center items-center h-full select-none">
          <div class="flex flex-col items-center justify-center gap-2 mb-8">
            <img class="w-8 h-8" src="/liscord.png" alt="Liscord logo"/>
            <p class="m-0 text-[16px] font-semibold dark:text-white">
              Login to your Account
            </p>
            <span class="m-0 text-xs max-w-[90%] text-center text-[#8B8E98]">
              Get started with our app, just start section and enjoy experience.
            </span>
          </div>
          <div class="w-full flex flex-col gap-2">
            <label class="font-semibold text-xs text-gray-400">Username</label>
            <input
              ref="username"
              placeholder="Username"
              class="border rounded-lg px-3 py-2 mb-5 text-sm w-full outline-none dark:border-gray-500 dark:bg-gray-900"
            />
          </div>
        </div>
        <div class="w-full flex flex-col gap-2">
          <label class="font-semibold text-xs text-gray-400">Password</label>
          <input
            ref="password"
            placeholder="••••••••"
            class="border rounded-lg px-3 py-2 mb-5 text-sm w-full outline-none dark:border-gray-500 dark:bg-gray-900"
            type="password"
          />
        </div>
        <div>
          <button
            type="submit"
            class="py-1 px-8 bg-blue-500 hover:bg-blue-800 focus:ring-offset-blue-200 text-white w-full transition ease-in duration-200 text-center text-base font-semibold shadow-md focus:outline-none focus:ring-2 focus:ring-offset-2 rounded-lg cursor-pointer select-none"
          >
            Login
          </button>
        </div>
        <p class="text-red-600 mt-2 p-4 border border-red-600 bg-red-300 rounded-xl" v-if="error">{{ error }}</p>
      </form>
    </div>
  </div>
</template>

<style scoped>

</style>
