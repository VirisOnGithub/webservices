<script lang="ts" setup>
defineProps({
  channel: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <NuxtLink class="channel" :to="`/channel/${channel.idc}`">
    <Icon :name="channel.isPublic ? 'mdi:hashtag' : 'material-symbols:lock'" size="16" class="channel__logo" />
    {{ channel.name }}
  </NuxtLink>
</template>

<style scoped>
.channel {
  display: flex;
  align-items: center;
  padding: 10px;
  transition: background-color 0.2s ease;
  margin: 5px;
  border-radius: 5px;
  height: 40px;
}

.channel:hover {
  background-color: #34495e;
}

.channel__logo {
  margin-right: 10px;
}
</style>
