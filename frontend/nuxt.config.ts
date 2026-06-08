// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    '@nuxt/eslint',
    '@nuxt/ui',
    '@nuxt/image',
    'nuxt-security',
    '@nuxt/icon'
  ],

  devtools: {
    enabled: true
  },

  security: {
    headers: {
      crossOriginResourcePolicy: 'cross-origin'
    }
  },

  css: ['~/assets/css/main.css'],

  routeRules: {
    '/': { prerender: true }
  },

  compatibilityDate: '2025-01-15',

  eslint: {
    config: {
      stylistic: {
        commaDangle: 'never',
        braceStyle: '1tbs'
      }
    }
  },

  imports: {
    dirs: ['types']
  },

  vite: {
    optimizeDeps: {
      include: [
        '@tiptap/core',
        '@tiptap/extensions',
        '@tiptap/starter-kit',
        '@tiptap/vue-3',
        '@vue/devtools-core',
        '@vue/devtools-kit',
      ]
    }
  }
})
