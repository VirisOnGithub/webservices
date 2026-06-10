<script lang="ts" setup>
const props = defineProps({
  channel: {
    type: Object,
    required: true
  }
})

const route = useRoute()

const isCurrentChannel = computed(() => {
  const channelId = route.params.id
  // obligé de comparer sans les types parce que route.params.id est un string alors que props.channel.idc est un number
  return channelId == props.channel.idc
})
</script>

<template>
  <NuxtLink class="flex items-center p-2.5 transition m-1.25 rounded h-10 hover:bg-[#34495e] border-l-4" :class="{ 'border-l-red-500 ': isCurrentChannel }" :to="`/channel/${channel.idc}`">
    <Icon :name="channel.isPublic ? 'mdi:hashtag' : 'material-symbols:lock'" size="16" class="mr-2.5" />
    {{ channel.name }}
  </NuxtLink>
</template>

<style scoped>
</style>
