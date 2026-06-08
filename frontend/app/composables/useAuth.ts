export const useAuth = () => {
  const cookie = useCookie('token')

  const loggedIn = computed(() => !!cookie.value)

  return { loggedIn}
}
