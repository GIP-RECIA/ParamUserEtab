import { createApp } from "vue";
import App from "./App.vue";
import 'regenerator-runtime/runtime'

import i18n from "./i18n";
import "./icons";
import { register as registerCustomElements } from "./ce";

const app = createApp(App);

registerCustomElements();

app.use(i18n);
app.mount("#app");

