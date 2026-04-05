import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { vueKeycloak } from "@josempgon/vue-keycloak";

import App from './App.vue'
import initRouter from './router'

const initApp = async () => {

  const app = createApp(App)

  // Initialize vue-keycloak
  await vueKeycloak.install(app,
    {
      config: {
        url: 'http://localhost:7080',
        realm: 'microservices-example',
        clientId: 'vue-example',
      }
    }
  )
  const router = initRouter()

  app.use(createPinia())
  app.use(router)
  app.mount('#app')
}

initApp()

