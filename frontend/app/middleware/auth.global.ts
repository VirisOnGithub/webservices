export default defineNuxtRouteMiddleware((to) => {
  const { loggedIn } = useAuth()

  if (to.meta.public) return  // Si la page est publique dans les meta c'est ok

  if (!loggedIn.value) {
    return navigateTo('/login')
  }
})
