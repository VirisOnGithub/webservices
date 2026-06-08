export const useToastDisplay = () => {
  const toast = useToast()

  const showToast = (message: string, type: 'success' | 'error' = 'success') => {
    toast.add({
      title: message,
      color: type,
    })
  }

  return { showToast }
}
