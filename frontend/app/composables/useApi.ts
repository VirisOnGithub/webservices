export const useApi = () => {
  type HttpMethod =
    "GET"
    | "get"
    | "HEAD"
    | "PATCH"
    | "POST"
    | "PUT"
    | "DELETE"
    | "CONNECT"
    | "OPTIONS"
    | "TRACE"
    | "head"
    | "patch"
    | "post"
    | "put"
    | "delete"
    | "connect"
    | "options"
    | "trace"
    | undefined

  const config = useRuntimeConfig()

  const baseURL = config.public.apiBase
  const baseEndpoint = `${baseURL}/api`

  interface ApiRequestOptions {
    endpoint: string
    method?: HttpMethod
    body?: string | any
    useBaseEndpoint?: boolean
    token?: string
  }

  const fetchData = (options: ApiRequestOptions) => {
    let { endpoint, method = 'GET', body, useBaseEndpoint = true, token = null } = options
    const loading = ref(false)
    const data = ref(null)
    const error = ref('')

    const fetch = async () => {
      loading.value = true
      error.value = ''
      data.value = null

      try {
        const uri = useBaseEndpoint ? `${baseEndpoint}${endpoint}` : `${baseURL}${endpoint}`
        console.log(`Fetching: ${method} ${uri}`)
        if (method == 'GET') {
          data.value = await $fetch(uri, {
            method,
            headers: {
              'Authorization': token ? `Bearer ${token}` : ''
            }
          })
        } else {
          if (body && typeof body !== 'string') {
            body = JSON.stringify(body)
          }


          data.value = await $fetch(uri, {
            method,
            body,
            headers: {
              'Content-Type': 'application/json',
              'Authorization': token ? `Bearer ${token}` : ''
            }
          })
        }
      } catch (err) {
        error.value = err instanceof Error ? err.message : String(err)
        console.error('API Error:', err)
        navigateTo('/login')
        useState('toast').value = {
          type: 'error',
          message: 'Une erreur est survenue lors de la communication avec le serveur. Veuillez vous reconnecter.' + error.value
        }
      } finally {
        loading.value = false
      }
    }

    return { fetch, data, loading, error }
  }

  return { fetchData }
}
