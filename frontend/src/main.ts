import { createApp } from "vue";
import App from "./App.vue";
import 'regenerator-runtime/runtime'

// import './assets/main.css'
import i18n from "./i18n";
import "./icons";
import "./ce";

const app = createApp(App);
// app = createApp(App).mount('#app')

app.use(i18n);
app.mount("#app");

// customElements.define('my-custom-element', defineCustomElement(App))
