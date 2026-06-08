<template>
  <p>Oh c'est beau !</p>
</template>

<script setup>
const { showToast } = useToastDisplay()

onMounted(() => {
  const toastState = useState('toast')
  if (toastState.value) {
    showToast(toastState.value.message, toastState.value.type)
    toastState.value = null
  }
})
</script>

<style scoped>

</style>
